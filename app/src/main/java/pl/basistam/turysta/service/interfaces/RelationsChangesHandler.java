package pl.basistam.turysta.service.interfaces;

import java.util.List;

import pl.basistam.turysta.items.RelationItem;

public interface RelationsChangesHandler {
    void registerChange(RelationItem relationItem);
    void adjustRelationToUnsavedChanges(RelationItem relationItem);
    List<RelationItem> getAndClearAllChanges();
}
