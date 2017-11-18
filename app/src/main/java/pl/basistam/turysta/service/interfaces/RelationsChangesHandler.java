package pl.basistam.turysta.service.interfaces;

import java.util.List;

import pl.basistam.turysta.dto.Relation;

public interface RelationsChangesHandler {
    void registerChange(Relation relation);
    void adjustRelationToChanges(Relation relation);
    List<Relation> getAndClearAllChanges();
}
