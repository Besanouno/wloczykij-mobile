package pl.basistam.turysta.map;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import pl.basistam.turysta.enums.MarkerPriority;

public class MarkersController {
    private GoogleMap googleMap;
    private Marker currentMarker;
    private List<Marker> route = new ArrayList<>();

    private boolean routeMode = false;

    public MarkersController(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public void clearCurrentMarkerIfNotNullAndSet(Marker marker) {
        if (currentMarker != null) {
            currentMarker.remove();
        }
        currentMarker = marker;
        if (routeMode) {
            addCurrentToRoute();
        }
    }


    public List<Marker> getRoute() {
        return route;
    }

    public void clearRoute() {
        route.clear();
        routeMode = false;
    }

    public void addToRoute(Marker marker) {
        routeMode = true;
        route.add(marker);
        googleMap.addMarker(new MarkerOptions()
                .position(marker.getPosition())
                .zIndex(MarkerPriority.FLAG.getValue())
                .title(marker.getTitle())
                .icon(BitmapDescriptorFactory.fromAsset("flag.png")));
    }

    public void addCurrentToRoute() {
        addToRoute(currentMarker);
    }
}
