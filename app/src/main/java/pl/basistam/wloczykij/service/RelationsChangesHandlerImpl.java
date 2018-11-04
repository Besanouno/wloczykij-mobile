package pl.basistam.wloczykij.service;

import java.util.ArrayList;
import java.util.List;

import pl.basistam.wloczykij.dto.Relation;
import pl.basistam.wloczykij.service.interfaces.RelationsChangesHandler;

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
    public void adjustRelationToChanges(Relation relation) {
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
