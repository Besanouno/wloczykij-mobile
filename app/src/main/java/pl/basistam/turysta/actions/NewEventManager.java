package pl.basistam.turysta.actions;

import android.content.Context;

import java.util.List;

import pl.basistam.turysta.dto.UserItem;

public class NewEventManager extends EventManager {

    private List<UserItem> changes;

    public NewEventManager(Context context, String eventGuid, List<String> participantsLogins) {
        super(context, eventGuid, participantsLogins);
    }

    @Override
    public void postExecute(List<UserItem> changes) {
        this.changes = changes;
    }
}
