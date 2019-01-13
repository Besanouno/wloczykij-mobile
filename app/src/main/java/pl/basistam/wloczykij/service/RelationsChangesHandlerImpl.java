package pl.basistam.wloczykij.service;

import java.util.ArrayList;
import java.util.List;

import pl.basistam.wloczykij.items.RelationItem;
import pl.basistam.wloczykij.service.interfaces.RelationsChangesHandler;

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
