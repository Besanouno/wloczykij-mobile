package pl.basistam.turysta.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pl.basistam.turysta.R;
import pl.basistam.turysta.actions.LocationHandler;
import pl.basistam.turysta.actions.UpdatePlaceDetailsAction;
import pl.basistam.turysta.adapters.RouteAdapter;
import pl.basistam.turysta.components.buttons.ZoomButtons;
import pl.basistam.turysta.components.fields.PlaceDetailsField;
import pl.basistam.turysta.components.fields.search.SearchField;
import pl.basistam.turysta.database.AppDatabase;
import pl.basistam.turysta.fragments.events.UpcomingEventFragment;
import pl.basistam.turysta.items.RouteNodeItem;
import pl.basistam.turysta.map.MapInitializer;
import pl.basistam.turysta.map.MarkersController;
import pl.basistam.turysta.map.PlacesInitializer;
import pl.basistam.turysta.map.Route;
import pl.basistam.turysta.map.TrailsInitializer;

public class MapViewFragment extends Fragment {

    private MapView mapView;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private MarkersController markersController;
    private LocationHandler locationHandler;
    private Route route;
    private boolean editRoute = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        if (getArguments() != null) {
            route = (Route) getArguments().getSerializable("route");
            editRoute = !getArguments().containsKey("editable") || getArguments().getBoolean("editable");
        }
        mapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        View bottomSheet = rootView.findViewById(R.id.map_place);
        initBottomSheetBehaviour(bottomSheet);
        final List<RouteNodeItem> items = new ArrayList<>();
        final RouteAdapter adapter = new RouteAdapter(getActivity(), R.layout.item_route_node, items);
        ListView lvRoute = bottomSheet.findViewById(R.id.lv_route);
        lvRoute.setAdapter(adapter);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                new PlacesInitializer(AppDatabase.getInstance(getActivity()), map).execute();
                TrailsInitializer trailsInitializer = new TrailsInitializer(AppDatabase.getInstance(getActivity()), map);
                trailsInitializer.execute();
                MapInitializer.init(map);
                initZoomButtons(map);
                initMarkers(rootView, map);
                initLocalizationButton(rootView, map);
                markersController = new MarkersController(map, getActivity().getBaseContext(), adapter, items, trailsInitializer.getPolylines());
                if (route != null) {
                    markersController.initRoute(route.getTrailIds());
                } else {
                    route = new Route(new ArrayList<Integer>());
                }
            }
        });
        ImageButton ibClearRoute = rootView.findViewById(R.id.ib_clear_route);
        if (!editRoute) {
            ibClearRoute.setVisibility(View.GONE);
        } else {
            ibClearRoute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    markersController.clearRoute();
                }
            });
        }
        initAddEventButton(rootView);
        return rootView;
    }

    private void initBottomSheetBehaviour(View bottomSheet) {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setPeekHeight(160);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void initAddEventButton(View rootView) {
        ImageButton ibAddEvent = rootView.findViewById(R.id.ib_add_event);
        if (!editRoute) {
            ibAddEvent.setVisibility(View.GONE);
        } else {
            if (route == null) {
                ibAddEvent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UpcomingEventFragment fragment = new UpcomingEventFragment();
                        Bundle args = new Bundle();
                        args.putBoolean("isAdmin", true);
                        args.putIntegerArrayList("trailIds", markersController.getRouteTrailIds());
                        fragment.setArguments(args);
                        FragmentManager fragmentManager = getActivity().getFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.content, fragment)
                                .addToBackStack(null)
                                .commit();
                    }
                });
            } else {
                ibAddEvent.setImageResource(R.drawable.ic_save_small);
                ibAddEvent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        route.setTrailIds(markersController.getRouteTrailIds());
                        getFragmentManager().popBackStack();
                    }
                });
            }
        }
    }

    private void initLocalizationButton(final View view, final GoogleMap map) {
        locationHandler = new LocationHandler(
                getActivity(),
                (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE),
                map);
        ImageButton ibLocalization = view.findViewById(R.id.ib_localization);
        ibLocalization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationHandler.localize();
            }
        });
        locationHandler.turnOnUpdates();
    }

    private void initMarkers(final View view, final GoogleMap map) {
        ImageButton ibStartRoute = view.findViewById(R.id.ib_start_route);
        if (!editRoute) {
            ibStartRoute.setVisibility(View.GONE);
        } else {
            ibStartRoute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    markersController.addCurrentToRoute();
                }
            });
        }
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED || bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                if (marker.getTitle() != null && !marker.getTitle().isEmpty()) {
                    refreshPlaceDetailsWindow(view, marker.getTitle());
                }
                return true;
            }
        });
    }

    private void refreshPlaceDetailsWindow(View view, String placeName) {
        new UpdatePlaceDetailsAction(
                new PlaceDetailsField(
                        (TextView) view.findViewById(R.id.tv_place_name),
                        (TextView) view.findViewById(R.id.tv_height_name)
                ),
                AppDatabase.getInstance(getActivity().getBaseContext()).placeDao(),
                markersController)
                .execute(placeName);
    }

    private void initZoomButtons(GoogleMap map) {
        View view = getView();
        if (view != null) {
            new ZoomButtons(map,
                    (ImageButton) getView().findViewById(R.id.ib_zoom_in),
                    (ImageButton) getView().findViewById(R.id.ib_zoom_out)).initializeListeners();
        }
    }

    public void initSearchField(final SearchView actionView) {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                new SearchField(
                        getActivity(),
                        actionView,
                        map).initialize();
            }
        });
    }
}