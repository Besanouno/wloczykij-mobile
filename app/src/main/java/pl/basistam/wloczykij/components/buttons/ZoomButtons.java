package pl.basistam.wloczykij.components.buttons;

import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

public class ZoomButtons {

    private final GoogleMap map;
    private final ImageButton zoomIn;
    private final ImageButton zoomOut;

    public ZoomButtons(
            GoogleMap map,
            ImageButton zoomIn,
            ImageButton zoomOut) {
        this.map = map;
        this.zoomIn = zoomIn;
        this.zoomOut = zoomOut;
    }

    public void initializeListeners() {
        zoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });
        zoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });
    }
}
