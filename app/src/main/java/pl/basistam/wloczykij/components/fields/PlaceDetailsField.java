package pl.basistam.wloczykij.components.fields;

import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import pl.basistam.wloczykij.components.utils.CameraUtils;

public class PlaceDetailsField {

    private TextView nameField;
    private TextView heightAboveSeaLevelField;
    private GoogleMap map;

    public PlaceDetailsField(TextView nameField, TextView heightAboveSeaLevelField, GoogleMap map) {
        this.nameField = nameField;
        this.heightAboveSeaLevelField = heightAboveSeaLevelField;
        this.map = map;
    }

    public void updateName(String name) {
        nameField.setText(name);
    }

    public void updateHeightAboveSeaLevel(double height) {
        heightAboveSeaLevelField.setText(Double.toString(height) + " m n.p.m.");
    }

    public void updateMapPosition(double latitude, double longitude) {
        CameraUtils.moveAndZoom(map, latitude, longitude, 12f);
    }
}
