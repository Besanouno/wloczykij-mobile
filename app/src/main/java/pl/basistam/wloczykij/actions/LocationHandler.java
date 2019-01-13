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
import com.google.android.gms.maps.model.LatLng;

import pl.basistam.wloczykij.listeners.LocationListenerImpl;

public class LocationHandler {
    private Activity activity;
    private LocationManager locationManager;
    private GoogleMap map;
    private boolean isUpdated = false;

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
        locationManager.requestLocationUpdates(getMostAccuracyProvider(), 0, 0, new LocationListenerImpl(map, activity));
    }

    public void localize() {
        if (!verifyPermissions()) {
            return;
        }
        Location location = getLocation();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
        map.animateCamera(cameraUpdate);
        map.setMyLocationEnabled(true);
        turnOnUpdates();
    }

    private boolean isLocationPermitted() {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;    }

    private void requireLocationPermissions() {
        ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    private String getMostAccuracyProvider() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        return locationManager.getBestProvider(criteria, true);
    }

    private Location getLocation() {
        return locationManager.getLastKnownLocation(getMostAccuracyProvider());
    }

    private boolean verifyPermissions() {
        if (!isLocationPermitted()) {
            requireLocationPermissions();
            return false;
        }
        return true;
    }

}
