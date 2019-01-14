package pl.basistam.wloczykij.actions;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import pl.basistam.wloczykij.enums.MarkerPriority;
import pl.basistam.wloczykij.listeners.LocationListenerImpl;

import static android.content.Context.LOCATION_SERVICE;

public class LocationHandler {
    private Activity activity;
    private LocationManager locationManager;
    private GoogleMap map;
    private boolean isUpdated = false;
    private Marker marker;

    public LocationHandler(Activity activity, LocationManager locationManager, GoogleMap map) {
        this.activity = activity;
        this.locationManager = locationManager;
        this.map = map;
    }

    public void turnOnUpdates() {
        if (!verifyPermissions() || isUpdated) {
            return;
        }
        isUpdated = true;
        locationManager.requestLocationUpdates(getMostAccuracyProvider(), 0, 10, new LocationListenerImpl(map, activity));
        Location location = getLocation();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        putMarker(latLng);
    }

    public void localize() {
        if (!verifyPermissions()) {
            return;
        }
        Location location = getLocation();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        putMarker(latLng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
        map.animateCamera(cameraUpdate);
        turnOnUpdates();
    }

    private void putMarker(LatLng location) {
        if (marker != null) {
            marker.remove();
        }
        marker = map.addMarker(new MarkerOptions()
                .position(location)
                .zIndex(MarkerPriority.CURRENT.getValue())
                .title("Ty"));
    }

    private boolean isLocationPermitted() {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requireLocationPermissions() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    private String getMostAccuracyProvider() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        return locationManager.getBestProvider(criteria, true);
    }

    private Location getLocation() {
        return this.getLastKnownLocation();
    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager)activity.getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    private boolean verifyPermissions() {
        if (!isLocationPermitted()) {
            requireLocationPermissions();
            return false;
        }
        return true;
    }

}
