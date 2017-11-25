package pl.basistam.turysta.service;

import java.util.ArrayList;
import java.util.List;

import pl.basistam.turysta.dto.UserItem;
import pl.basistam.turysta.service.interfaces.UsersStatusesChangesHandler;

public class UsersStatusesChangesHandlerImpl implements UsersStatusesChangesHandler {

    private final List<UserItem> userItemChanges = new ArrayList<>();

    @Override
    public void registerChange(UserItem userItem) {
        int index = userItemChanges.indexOf(userItem);
        if (index == -1) {
            userItemChanges.add(userItem);
        } else {
            userItemChanges.set(index, userItem);
        }
    }

    @Override
    public void adjustRelationToUnsavedChanges(UserItem userItem) {
        int index = userItemChanges.indexOf(userItem);
        if (index != -1) {
            userItem.setStatus(userItemChanges.get(index).getStatus());
        }
    }

    @Override
    public List<UserItem> getAndClearAllChanges() {
        List<UserItem> result = new ArrayList<>(this.userItemChanges);
        this.userItemChanges.clear();
        return result;
    }
}
