package pl.basistam.turysta.fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import pl.basistam.turysta.R;
import pl.basistam.turysta.actions.LocationHandler;
import pl.basistam.turysta.actions.UpdatePlaceDetailsAction;
import pl.basistam.turysta.components.buttons.ZoomButtons;
import pl.basistam.turysta.components.fields.PlaceDetailsField;
import pl.basistam.turysta.components.fields.search.SearchField;
import pl.basistam.turysta.database.AppDatabase;
import pl.basistam.turysta.listeners.LocationListenerImpl;
import pl.basistam.turysta.map.MapInitializer;
import pl.basistam.turysta.map.MarkersController;

public class MapViewFragment extends Fragment {

    private MapView mapView;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private MarkersController markersController;
    private LocationHandler locationHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        View bottomSheet = rootView.findViewById(R.id.map_place);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setPeekHeight(160);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                MapInitializer.init(AppDatabase.getInstance(getActivity()), map);
                initZoomButtons(map);
                initMarkers(rootView, map);
                initLocalizationButton(rootView, map);
                markersController = new MarkersController(map);
            }
        });

        return rootView;
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
        ibStartRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markersController.addCurrentToRoute();
            }
        });
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED
                        || bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }

                if (marker.getTitle() == null || marker.getTitle().isEmpty()) {
                    return true;
                }
                new UpdatePlaceDetailsAction(
                        new PlaceDetailsField(
                                (TextView) view.findViewById(R.id.tv_place_name),
                                (TextView) view.findViewById(R.id.tv_height_name),
                                map
                        ),
                        AppDatabase.getInstance(getActivity().getBaseContext()).placeDao(),
                        markersController)
                        .execute(marker.getTitle());


                return true;
            }
        });
    }

    private void initZoomButtons(GoogleMap map) {
        View view = getView();
        if (view != null) {
            new ZoomButtons(map,
                    (ImageButton) getView().findViewById(R.id.ib_zoom_in),
                    (ImageButton) getView().findViewById(R.id.ib_zoom_out)).initializeListeners();
        }
    }

    public void initSearchField(final Context context, final SearchView actionView) {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                new SearchField(
                        context,
                        actionView,
                        map).initialize();
            }
        });
    }
}