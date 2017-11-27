package pl.basistam.turysta.adapters;

import android.app.Activity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import pl.basistam.turysta.R;
import pl.basistam.turysta.dto.EventUserDto;
import pl.basistam.turysta.dto.RelationItem;
import pl.basistam.turysta.dto.RelationsGroup;
import pl.basistam.turysta.enums.EventUserStatus;
import pl.basistam.turysta.fragments.PersonFragment;
import pl.basistam.turysta.service.interfaces.RelationsChangesHandler;

public class RelationsAdapter extends BaseExpandableListAdapter {
    private final SparseArray<RelationsGroup> groups;
    private final LayoutInflater inflater;
    private final Activity activity;
    private final RelationsChangesHandler relationsChangesHandler;

    public RelationsAdapter(
            SparseArray<RelationsGroup> groups,
            Activity activity,
            RelationsChangesHandler relationsChangesHandler) {
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
        return groupPosition;
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
        RelationsGroup relationsGroup = (RelationsGroup) getGroup(groupPosition);
        TextView textView = (TextView) convertView;
        textView.setText(relationsGroup.getName());
        textView.setTextSize(20f);
        textView.setPaddingRelative(textView.getPaddingStart(), textView.getPaddingTop(), textView.getPaddingEnd(), 20);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
        final RelationItem child = (RelationItem) getChild(groupPosition, childPosition);
        if (view == null) {
            view = inflater.inflate(R.layout.item_relation, null);
        }
        final CheckBox checkBox = view.findViewById(R.id.chb_friend);
        checkBox.setChecked(child.isRelated());
        checkBox.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        child.setRelated(checkBox.isChecked());
                        relationsChangesHandler.registerChange(child);
                    }
                }
        );
        final TextView textView = view.findViewById(R.id.tv_login);
        textView.setText(child.getName());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {showUserDetails(child); }
        });
        return view;
    }

    private void showUserDetails(RelationItem child) {
        PersonFragment fragment = new PersonFragment();
        Bundle bundle = new Bundle();
        bundle.putString("login", child.getLogin());
        fragment.setArguments(bundle);
        activity.getFragmentManager().beginTransaction()
                .add(R.id.content, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}