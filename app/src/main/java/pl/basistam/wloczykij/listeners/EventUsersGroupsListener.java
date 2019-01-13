package pl.basistam.wloczykij.listeners;

import android.util.SparseArray;
import android.widget.ExpandableListView;

import pl.basistam.wloczykij.items.EventUserItem;
import pl.basistam.wloczykij.groups.RelationsGroup;

public class EventUsersGroupsListener implements ExpandableListView.OnGroupExpandListener, ExpandableListView.OnGroupCollapseListener {

    private ExpandableListView elvParticipants;
    private int[] expandedHeights;
    private SparseArray<RelationsGroup<EventUserItem>> groups;

    public EventUsersGroupsListener(ExpandableListView elvParticipants, SparseArray<RelationsGroup<EventUserItem>> groups) {
        this.elvParticipants = elvParticipants;
        this.groups = groups;
        this.expandedHeights = new int[groups.size()];
    }

    @Override
    public void onGroupExpand(int groupPosition) {
        final int PERSON_ITEM_HEIGHT = 80;
        int children = groups.get(groupPosition).getChildren().size();
        int expandedHeight = PERSON_ITEM_HEIGHT * children;
        expandedHeights[groupPosition] = expandedHeight;
        elvParticipants.getLayoutParams().height += expandedHeight;
    }

    @Override
    public void onGroupCollapse(int groupPosition) {
        elvParticipants.getLayoutParams().height -= expandedHeights[groupPosition];
    }
}
