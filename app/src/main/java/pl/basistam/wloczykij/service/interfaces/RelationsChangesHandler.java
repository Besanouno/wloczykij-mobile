package pl.basistam.wloczykij.service.interfaces;

import java.util.List;

import pl.basistam.wloczykij.items.RelationItem;

public interface RelationsChangesHandler {
    void registerChange(RelationItem relationItem);
    void adjustRelationToUnsavedChanges(RelationItem relationItem);
    List<RelationItem> getAndClearAllChanges();
}
