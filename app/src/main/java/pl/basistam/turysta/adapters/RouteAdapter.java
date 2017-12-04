package pl.basistam.turysta.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.basistam.turysta.R;

public class RouteAdapter extends SimpleAdapter {

    private final List<? extends Map<String, String>> data;
    private final LayoutInflater inflater;

    public RouteAdapter(Activity activity, List<? extends Map<String, String>> data, int resource, String[] from, int[] to) {
        super(activity.getBaseContext(), data, resource, from, to);
        this.inflater = activity.getLayoutInflater();
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_route_node, null);
        }
        final ImageButton ibRemove = convertView.findViewById(R.id.ib_remove);
        if (position != data.size() - 1) {
            ibRemove.setVisibility(View.INVISIBLE);
        } else {
            final HashMap<String, String> child = (HashMap<String, String>) getItem(position);
            ibRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count = 0;
                    for (Map<String, String> m : data) {
                        String name = m.get("name");
                        if (name != null && name.equals(child.get("name"))) {
                            data.remove(count);
                            notifyDataSetChanged();
                            return;
                        }
                        count++;
                    }
                }
            });
        }
        return super.getView(position, convertView, parent);
    }
}
