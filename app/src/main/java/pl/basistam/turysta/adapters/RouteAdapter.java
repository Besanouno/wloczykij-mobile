package pl.basistam.turysta.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.basistam.turysta.R;
import pl.basistam.turysta.items.RouteNodeItem;
import pl.basistam.turysta.map.MarkersController;

import static android.view.View.GONE;

public class RouteAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private final List<RouteNodeItem> items;
    private MarkersController markersController;

    public RouteAdapter(@NonNull Activity activity, @LayoutRes int resource, @NonNull List<RouteNodeItem> objects) {
        this.items = objects;
        this.inflater = activity.getLayoutInflater();

    }

    public void setMarkersController(MarkersController markersController) {
        this.markersController = markersController;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_route_node, null);
        }
        final ImageButton ibRemove = convertView.findViewById(R.id.ib_remove);
        final TextView tvName = convertView.findViewById(R.id.tv_name);
        final TextView tvTime = convertView.findViewById(R.id.tv_time);
        final TextView tvHeightDifference = convertView.findViewById(R.id.tv_height_difference);
        final ImageView ivArrow = convertView.findViewById(R.id.iv_arrow);

        RouteNodeItem item = items.get(position);
        if (item.getTime() == null && item.getHeightDifference() == null) {
            tvTime.setVisibility(GONE);
            tvHeightDifference.setVisibility(GONE);
            ivArrow.setVisibility(GONE);
        } else {
            tvTime.setVisibility(View.VISIBLE);
            tvHeightDifference.setVisibility(View.VISIBLE);
            ivArrow.setVisibility(View.VISIBLE);
            tvTime.setText(item.getPrintableTime());
            tvHeightDifference.setText(item.getPrintableHeightDifference());
            ivArrow.setImageResource(item.getHeightDifference() >= 0 ? R.drawable.ic_up : R.drawable.ic_down);
        }
        tvName.setText(item.getName());
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
        System.out.println(tvName.getText() + " " + tvTime.getText() + " " + tvHeightDifference.getText());
       return convertView;
    }
}
