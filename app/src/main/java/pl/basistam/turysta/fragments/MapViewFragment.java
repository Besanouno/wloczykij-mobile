package pl.basistam.turysta.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import pl.basistam.turysta.MainActivity;
import pl.basistam.turysta.R;
import pl.basistam.turysta.components.buttons.ZoomButtons;
import pl.basistam.turysta.components.fields.search.SearchField;
import pl.basistam.turysta.database.AppDatabase;
import pl.basistam.turysta.map.MapInitializer;

public class MapViewFragment extends Fragment {

    private MapView mapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.map_fragment, container, false);

        mapView = rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                MapInitializer.init(AppDatabase.getInstance(getActivity()), map);
                initZoomButtons(map);
            }
        });

        return rootView;
    }

    private void initZoomButtons(GoogleMap map) {
        View view = getView();
        if (view != null) {
            new ZoomButtons(map,
                    (ImageButton) getView().findViewById(R.id.zoom_in),
                    (ImageButton) getView().findViewById(R.id.zoom_out)).initializeListeners();
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