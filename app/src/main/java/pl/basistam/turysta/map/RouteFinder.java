package pl.basistam.turysta.map;

import java.util.Collections;
import java.util.List;

import pl.basistam.turysta.database.AppDatabase;
import pl.basistam.turysta.database.model.Place;
import pl.basistam.turysta.database.model.Trail;
import pl.basistam.turysta.database.model.TrailPoint;

public class RouteFinder {

    
    private final AppDatabase appDatabase;

    public RouteFinder(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
    }

    public List<Trail> findRoute(String from, String to) {
        Place start = appDatabase.placeDao().getByName(from);
        Place end = appDatabase.placeDao().getByName(to);
        Trail trail = appDatabase.trailDao().findByStartAndEndpoint(start.getId(), end.getId());
        if (trail == null) {
            return Collections.emptyList();
        }
        trail.setFirst(start);
        trail.setLast(end);
        List<TrailPoint> points = appDatabase.trailPointDao().getByTrailId(trail.getId());
        trail.setPoints(points);
        return Collections.singletonList(trail);
    }
}
