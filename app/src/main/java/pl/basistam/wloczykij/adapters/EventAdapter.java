package pl.basistam.wloczykij.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import pl.basistam.wloczykij.R;
import pl.basistam.wloczykij.dto.EventSimpleDetails;
import pl.basistam.wloczykij.utils.Converter;

public class EventAdapter extends BaseAdapter {

    private List<EventSimpleDetails> events;
    private LayoutInflater inflater;

    public EventAdapter(List<EventSimpleDetails> events, Activity activity) {
        this.events = events;
        this.inflater = activity.getLayoutInflater();
    }

    public void addAll(List<EventSimpleDetails> c) {
        events.addAll(c);
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EventSimpleDetails event = (EventSimpleDetails) getItem(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_event, null);
        }

        final TextView tvName = convertView.findViewById(R.id.tv_name);
        tvName.setText(event.getName());

        final TextView tvInitiator = convertView.findViewById(R.id.tv_initiator);
        tvInitiator.setText(event.getInitiator());

        final TextView tvStartDate = convertView.findViewById(R.id.tv_start_date);
        tvStartDate.setText(Converter.dateTimeToString(event.getStartDate()));

        return convertView;
    }
}
