package pl.basistam.turysta.map;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import pl.basistam.turysta.database.AppDatabase;

public class MapInitializer {
    public static final float INITIAL_ZOOM = 10f;
    public static final LatLng INITIAL_LOCATION = new LatLng(49.27587,19.9036641);

    public static void init(GoogleMap map) {
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(MapInitializer.INITIAL_LOCATION, MapInitializer.INITIAL_ZOOM));
        map.getUiSettings().setMapToolbarEnabled(false);
    }
}
