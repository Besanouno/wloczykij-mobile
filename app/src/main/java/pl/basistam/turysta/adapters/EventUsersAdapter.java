package pl.basistam.turysta.adapters;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.Serializable;

import pl.basistam.turysta.R;
import pl.basistam.turysta.dto.EventUserDto;
import pl.basistam.turysta.enums.EventUserStatus;
import pl.basistam.turysta.fragments.UserPreviewFragment;
import pl.basistam.turysta.groups.RelationsGroup;
import pl.basistam.turysta.service.EventUsers;

import static android.view.View.GONE;

public class EventUsersAdapter extends BaseExpandableListAdapter implements Serializable {
    private final SparseArray<RelationsGroup<EventUserDto>> groups;
    private final LayoutInflater inflater;
    private final Activity activity;
    private boolean isAdmin;
    private final EventUsers eventUsers;

    public EventUsersAdapter(
            SparseArray<RelationsGroup<EventUserDto>> groups,
            Activity activity,
            boolean isAdmin,
            EventUsers eventUsers) {
        this.groups = groups;
        this.activity = activity;
        this.inflater = activity.getLayoutInflater();
        this.isAdmin = isAdmin;
        this.eventUsers = eventUsers;
    }

    public void setAdmin(boolean admin) {
        this.isAdmin = admin;
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
        final EventUserDto child = (EventUserDto) getChild(groupPosition, childPosition);
        if (view == null) {
            view = inflater.inflate(R.layout.item_relation, null);
        }
        final CheckBox checkBox = view.findViewById(R.id.chb_friend);
        if (isAdmin) {
            String status = child.getStatus();
            if (status.equals(EventUserStatus.ADMIN.name())) {
                checkBox.setVisibility(GONE);
            } else if (status.equals(EventUserStatus.PARTICIPANT.name())) {
                checkBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#006507")));
                checkBox.setChecked(true);
            } else if (status.equals(EventUserStatus.INVITED.name())) {
                checkBox.setChecked(true);
                checkBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#00a70b")));
            } else {
                checkBox.setChecked(false);
                checkBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#00a70b")));
            }
            checkBox.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            eventUsers.registerChange(child);
                        }
                    }
            );
        } else {
            checkBox.setVisibility(GONE);
        }

        final TextView textView = view.findViewById(R.id.tv_login);
        textView.setText(child.getName() + " (" + child.getLogin() + ")");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {showUserDetails(child); }
        });
        return view;
    }

    private void showUserDetails(EventUserDto child) {
        UserPreviewFragment fragment = new UserPreviewFragment();
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