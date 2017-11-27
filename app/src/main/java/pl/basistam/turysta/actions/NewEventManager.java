package pl.basistam.turysta.actions;

import android.content.Context;

import java.util.List;

import pl.basistam.turysta.dto.RelationItem;

public class NewEventManager extends EventUsersDataSet {

    private List<RelationItem> changes;

    public NewEventManager(Context context, String eventGuid, List<String> participantsLogins) {
        super(context, eventGuid, participantsLogins);
    }
/*
    @Override
    public void postExecute(List<RelationItem> changes) {
        this.changes = changes;
    }*/
}
