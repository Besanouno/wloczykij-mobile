package pl.basistam.turysta.adapters;

import android.app.Activity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import pl.basistam.turysta.R;
import pl.basistam.turysta.dto.Group;
import pl.basistam.turysta.dto.Relation;
import pl.basistam.turysta.service.interfaces.RelationsChangesHandler;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private final SparseArray<Group> groups;
    private LayoutInflater inflater;
    private Activity activity;
    private final RelationsChangesHandler relationsChangesHandler;

    public ExpandableListAdapter(SparseArray<Group> groups, Activity activity, RelationsChangesHandler relationsChangesHandler) {
        this.groups = groups;
        this.activity = activity;
        this.inflater = activity.getLayoutInflater();
        this.relationsChangesHandler = relationsChangesHandler;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).getChildren().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).getChildren().get(childPosition);
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
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.group_item, null);
        }
        Group group = (Group) getGroup(groupPosition);
        TextView textView = (TextView) convertView;
        textView.setText(group.getName());
        textView.setTextSize(20f);
        textView.setPaddingRelative(textView.getPaddingStart(), textView.getPaddingTop(), textView.getPaddingEnd(), 20);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final Relation children = (Relation) getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.friend_item, null);
        }
        final TextView textView = convertView.findViewById(R.id.label);
        textView.setText(children.getFullName());
        final CheckBox checkBox = convertView.findViewById(R.id.check);
        checkBox.setChecked(children.isFriend());
        checkBox.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        children.setFriend(checkBox.isChecked());
                        relationsChangesHandler.registerChange(children);
                    }
                }
        );
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}