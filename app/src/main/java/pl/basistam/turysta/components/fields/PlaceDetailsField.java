package pl.basistam.turysta.components.fields;


import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import pl.basistam.turysta.components.utils.CameraUtils;

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
