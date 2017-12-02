package pl.basistam.turysta.service;

import java.util.ArrayList;
import java.util.List;

import pl.basistam.turysta.items.RelationItem;
import pl.basistam.turysta.service.interfaces.RelationsChangesHandler;

public class RelationsChangesHandlerImpl implements RelationsChangesHandler {

    private final List<RelationItem> userItemChanges = new ArrayList<>();

    @Override
    public void registerChange(RelationItem relationItem) {
        int index = userItemChanges.indexOf(relationItem);
        if (index == -1) {
            userItemChanges.add(relationItem);
        } else {
            userItemChanges.set(index, relationItem);
        }
    }

    @Override // uzywane gdy pobieramy użytkownika którego status już się zmienił
    public void adjustRelationToUnsavedChanges(RelationItem relationItem) {
        int index = userItemChanges.indexOf(relationItem);
        if (index != -1) {
            relationItem.setRelated(userItemChanges.get(index).isRelated());
        }
    }

    @Override
    public List<RelationItem> getAndClearAllChanges() {
        List<RelationItem> result = new ArrayList<>(this.userItemChanges);
        this.userItemChanges.clear();
        return result;
    }
}
