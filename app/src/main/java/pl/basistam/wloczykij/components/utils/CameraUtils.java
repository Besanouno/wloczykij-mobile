package pl.basistam.wloczykij.components.utils;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class CameraUtils {

    public static void move(GoogleMap map, double lat, double lon) {
        map.animateCamera(
                CameraUpdateFactory.newLatLng(new LatLng(lat, lon)),
                400,
                null);
    }

    public static void moveAndZoom(GoogleMap map, double lat, double lon, float zoom) {
        map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), Math.max(zoom, map.getCameraPosition().zoom)),
                400,
                null);
    }
}
