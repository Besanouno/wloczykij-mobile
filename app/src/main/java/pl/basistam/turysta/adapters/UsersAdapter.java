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
import pl.basistam.turysta.items.EventUserItem;
import pl.basistam.turysta.enums.EventUserStatus;
import pl.basistam.turysta.fragments.UserPreviewFragment;
import pl.basistam.turysta.groups.RelationsGroup;
import pl.basistam.turysta.service.interfaces.RelationsChangesHandler;

public class UsersAdapter extends BaseExpandableListAdapter {
    private final SparseArray<RelationsGroup> groups;
    private LayoutInflater inflater;
    private Activity activity;
    private final RelationsChangesHandler relationsChangesHandler;
    private boolean isAdmin;

    public UsersAdapter(
            SparseArray<RelationsGroup> groups,
            Activity activity,
            RelationsChangesHandler relationsChangesHandler,
            boolean isAdmin) {
        this.groups = groups;
        this.activity = activity;
        this.inflater = activity.getLayoutInflater();
        this.relationsChangesHandler = relationsChangesHandler;
        this.isAdmin = isAdmin;
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
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final EventUserItem child = (EventUserItem) getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_relation, null);
        }
        setCheckBoxIfAdmin(convertView, child);
        final TextView textView = convertView.findViewById(R.id.tv_login);
        textView.setText(child.getName());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {showUserDetails(child); }
        });
        return convertView;
    }

    private void showUserDetails(EventUserItem child) {
        UserPreviewFragment fragment = new UserPreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putString("login", child.getLogin());
        fragment.setArguments(bundle);
        activity.getFragmentManager().beginTransaction()
                .add(R.id.content, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void setCheckBoxIfAdmin(View view, final EventUserItem item) {
        final CheckBox checkBox = view.findViewById(R.id.chb_friend);
        /*if (isAdmin) {
            checkBox.setChecked(involved(item));
            checkBox.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            item.setStatus(EventUserStatus.INVITED.getValue());
                            relationsChangesHandler.registerChange(item);
                        }
                    }
            );
        } else {
            checkBox.setVisibility(View.GONE);
        }*/
    }

    private boolean involved(EventUserItem user) {
        return EventUserStatus.PARTICIPANT.getValue().equals(user.getStatus())
                || EventUserStatus.INVITED.getValue().equals(user.getStatus());
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}