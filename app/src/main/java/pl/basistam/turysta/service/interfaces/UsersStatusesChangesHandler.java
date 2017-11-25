package pl.basistam.turysta.service.interfaces;

import java.util.List;

import pl.basistam.turysta.dto.UserItem;

public interface UsersStatusesChangesHandler {
    void registerChange(UserItem userItem);
    void adjustRelationToUnsavedChanges(UserItem userItem);
    List<UserItem> getAndClearAllChanges();
}
