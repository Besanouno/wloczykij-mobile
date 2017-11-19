package pl.basistam.turysta.service;

import java.util.ArrayList;
import java.util.List;

import pl.basistam.turysta.dto.Relation;
import pl.basistam.turysta.service.interfaces.RelationsChangesHandler;

public class RelationsChangesHandlerImpl implements RelationsChangesHandler {

    private final List<Relation> relationChanges = new ArrayList<>();

    @Override
    public void registerChange(Relation relation) {
        int index = relationChanges.indexOf(relation);
        if (index == -1) {
            relationChanges.add(relation);
        } else {
            relationChanges.set(index, relation);
        }
    }

    @Override
    public void adjustRelationToUnsavedChanges(Relation relation) {
        int index = relationChanges.indexOf(relation);
        if (index != -1) {
            relation.setFriend(relationChanges.get(index).isFriend());
        }
    }

    @Override
    public List<Relation> getAndClearAllChanges() {
        List<Relation> result = new ArrayList<>(this.relationChanges);
        this.relationChanges.clear();
        return result;
    }
}
