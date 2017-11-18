package pl.basistam.turysta.adapters;

import android.app.Activity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pl.basistam.turysta.R;
import pl.basistam.turysta.dto.Relation;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private final SparseArray<Group> groups;
    public LayoutInflater inflater;
    public Activity activity;

    public ExpandableListAdapter(SparseArray<Group> groups, Activity activity) {
        this.groups = groups;
        this.activity = activity;
        this.inflater = activity.getLayoutInflater();
    }

    @Override
    public int getGroupCount() {
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).children.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String children = (String) getChild(groupPosition, childPosition);
        TextView textView = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.friend_item, null);
        }
        textView = (TextView) convertView.findViewById(R.id.label);
        textView.setText(children);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}

class Group {
    public Relation relation;
    public final List<Relation> children = new ArrayList<>();

    public Group(Relation relation) {
        this.relation = relation;
    }
}