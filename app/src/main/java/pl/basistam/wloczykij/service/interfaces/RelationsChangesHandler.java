package pl.basistam.wloczykij.service.interfaces;

import java.util.List;

import pl.basistam.wloczykij.dto.Relation;

public interface RelationsChangesHandler {
    void registerChange(Relation relation);
    void adjustRelationToChanges(Relation relation);
    List<Relation> getAndClearAllChanges();
}
