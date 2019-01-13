package pl.basistam.wloczykij.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import es.usc.citius.hipster.algorithm.Algorithm;
import es.usc.citius.hipster.algorithm.Hipster;
import es.usc.citius.hipster.graph.GraphBuilder;
import es.usc.citius.hipster.graph.GraphSearchProblem;
import es.usc.citius.hipster.graph.HipsterDirectedGraph;
import es.usc.citius.hipster.model.problem.SearchProblem;
import pl.basistam.wloczykij.database.AppDatabase;
import pl.basistam.wloczykij.database.model.Place;
import pl.basistam.wloczykij.database.model.Trail;

public class RouteFinder {

    private final AppDatabase appDatabase;

    public RouteFinder(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
    }

    public List<Trail> findRoute(String startName, String endName) {
        Place start = appDatabase.placeDao().getByName(startName);
        Place end = appDatabase.placeDao().getByName(endName);
        Trail trail = appDatabase.trailDao().findByStartAndEndpoint(start.getId(), end.getId());
        return directTrailExists(trail) ?
                Collections.singletonList(getDirectTrail(start, end, trail)) :
                designatePath(start, end);
    }

    private boolean directTrailExists(Trail trail) {
        return trail != null;
    }

    private List<Trail> designatePath(Place start, Place end) {
        List<Integer> placesIds = useDijkstraToDesignateRoute(start, end);
        if (!Objects.equals(placesIds.get(placesIds.size() - 1), end.getId())) {
            return new ArrayList<>();
        }
        List<Place> places = findPlacesByIds(placesIds);
        return createRouteConnectingPlaces(places);
    }

    private Trail getDirectTrail(Place start, Place end, Trail trail) {
        trail.setFirst(start);
        trail.setLast(end);
        return trail;
    }

    private List<Place> findPlacesByIds(List<Integer> placesIds) {
        List<Place> result = new ArrayList<>(placesIds.size());
        for (Integer id : placesIds) {
            result.add(appDatabase.placeDao().getById(id));
        }
        return result;
    }

    public List<Integer> useDijkstraToDesignateRoute(Place from, Place to) {
        GraphBuilder<Integer, Integer> graphBuilder = GraphBuilder.create();
        graphBuilder = completeGraph(graphBuilder, from.getId(), new HashSet<Integer>());
        HipsterDirectedGraph<Integer, Integer> graph = graphBuilder.createDirectedGraph();
        SearchProblem p = GraphSearchProblem
                .startingFrom(from.getId())
                .in(graph)
                .takeCostsFromEdges()
                .build();

        Algorithm.SearchResult f = Hipster.createDijkstra(p).search(to.getId());
        return Algorithm.recoverStatePath(f.getGoalNode());
    }

    public List<Trail> createRouteConnectingPlaces(List<Place> places) {
        List<Trail> result = new ArrayList<>(places.size() - 1);
        for (int i = 0; i < places.size() - 1; i++) {
            Place start = places.get(i);
            Place end = places.get(i + 1);
            Trail trail = appDatabase.trailDao().findByStartAndEndpoint(start.getId(), end.getId());
            result.add(getDirectTrail(start, end, trail));
        }
        return result;
    }

    public GraphBuilder<Integer, Integer> completeGraph(GraphBuilder<Integer, Integer> graphBuilder, Integer node, Set<Integer> visited) {
        if (!visited.contains(node)) {
            visited.add(node);
            List<Trail> availableTrails = appDatabase.trailDao().findAvailableTrails(node);
            for (Trail t : availableTrails) {
                Integer neighbour = t.getLastPoint();
                graphBuilder = completeGraph(graphBuilder.connect(node).to(neighbour).withEdge(t.getTime()), neighbour, visited);
            }
        }
        return graphBuilder;
    }
}
