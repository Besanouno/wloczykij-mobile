package pl.basistam.turysta.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.basistam.turysta.R;
import pl.basistam.turysta.map.MarkersController;

public class RouteAdapter extends SimpleAdapter {

    private final List<? extends Map<String, String>> data;
    private final LayoutInflater inflater;
    private MarkersController markersController;

    public RouteAdapter(Activity activity, List<? extends Map<String, String>> data, int resource, String[] from, int[] to) {
        super(activity.getBaseContext(), data, resource, from, to);
        this.inflater = activity.getLayoutInflater();
        this.data = data;
    }

    public void setMarkersController(MarkersController markersController) {
        this.markersController = markersController;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_route_node, null);
        }
        final ImageButton ibRemove = convertView.findViewById(R.id.ib_remove);
        final HashMap<String, String> child = (HashMap<String, String>) getItem(position);
       /* if (markersController == null || position != data.size() - 1) {
            ibRemove.setVisibility(View.INVISIBLE);
        } else {
            ibRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    markersController.removeLast();
                }
            });
        }*/
        return super.getView(position, convertView, parent);
    }
}
