package pl.basistam.wloczykij.adapters;

import android.app.Activity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import pl.basistam.wloczykij.R;
import pl.basistam.wloczykij.dto.Group;
import pl.basistam.wloczykij.dto.Relation;
import pl.basistam.wloczykij.fragments.PersonFragment;
import pl.basistam.wloczykij.service.interfaces.RelationsChangesHandler;

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
            convertView = inflater.inflate(R.layout.item_relations_group, null);
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
            convertView = inflater.inflate(R.layout.item_relation, null);
        }
        final TextView textView = convertView.findViewById(R.id.tv_login);
        textView.setText(children.getFullName());
        final CheckBox checkBox = convertView.findViewById(R.id.chb_friend);
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
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonFragment fragment = new PersonFragment();
                Bundle bundle = new Bundle();
                bundle.putString("login", children.getLogin());
                fragment.setArguments(bundle);
                activity.getFragmentManager().beginTransaction()
                        .add(R.id.content, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}