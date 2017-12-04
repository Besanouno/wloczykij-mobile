package pl.basistam.turysta.components.fields;

import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import pl.basistam.turysta.components.utils.CameraUtils;
import pl.basistam.turysta.enums.MarkerPriority;

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
}
