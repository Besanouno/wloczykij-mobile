package pl.basistam.wloczykij.components.fields;

import android.widget.TextView;

public class PlaceDetailsField {

    private TextView nameField;
    private TextView heightAboveSeaLevelField;

    public PlaceDetailsField(TextView nameField, TextView heightAboveSeaLevelField) {
        this.nameField = nameField;
        this.heightAboveSeaLevelField = heightAboveSeaLevelField;
    }

    public void updateName(String name) {
        nameField.setText(name);
    }

    public void updateHeightAboveSeaLevel(double height) {
        heightAboveSeaLevelField.setText(Double.toString(height) + " m n.p.m.");
    }
}
