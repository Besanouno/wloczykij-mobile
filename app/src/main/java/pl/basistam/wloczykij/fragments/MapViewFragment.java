package pl.basistam.wloczykij.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;

import pl.basistam.wloczykij.R;
import pl.basistam.wloczykij.actions.UpdatePlaceDetailsAction;
import pl.basistam.wloczykij.components.buttons.ZoomButtons;
import pl.basistam.wloczykij.components.fields.PlaceDetailsField;
import pl.basistam.wloczykij.components.fields.search.SearchField;
import pl.basistam.wloczykij.database.AppDatabase;
import pl.basistam.wloczykij.map.MapInitializer;

public class MapViewFragment extends Fragment {

    private MapView mapView;
    private BottomSheetBehavior<View> bottomSheetBehavior;

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
            }
        });

        return rootView;
    }

    private void initMarkers(final View view, final GoogleMap map) {
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                new UpdatePlaceDetailsAction(
                        new PlaceDetailsField(
                                (TextView) view.findViewById(R.id.tv_place_name),
                                (TextView) view.findViewById(R.id.tv_height_name),
                                map
                                ),
                        AppDatabase.getInstance(getActivity().getBaseContext()).placeDao())
                        .execute(marker.getTitle());

                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED
                        || bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }

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