package pl.basistam.turysta.map;

import android.content.Context;
import android.os.AsyncTask;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.basistam.turysta.adapters.RouteAdapter;
import pl.basistam.turysta.components.utils.CameraUtils;
import pl.basistam.turysta.database.AppDatabase;
import pl.basistam.turysta.database.model.Place;
import pl.basistam.turysta.database.model.Trail;
import pl.basistam.turysta.enums.MarkerPriority;

public class MarkersController {

    private final Context context;
    private final RouteAdapter adapter;
    private final List<HashMap<String, String>> items;
    private GoogleMap googleMap;
    private Marker currentMarker;
    private Place currentMarkerPlace;
    private final AppDatabase appDatabase;
    private final List<Marker> routeMarkers = new ArrayList<>();
    private final ArrayList<Integer> trailIds = new ArrayList<>();
    private RouteFinder routeFinder;
    private final SparseArray<Polyline> polylines;
    private List<Integer> polylinesIds = new ArrayList<>();
    private boolean routeMode = false;

    public MarkersController(GoogleMap map, Context context, RouteAdapter adapter, List<HashMap<String, String>> items, SparseArray<Polyline> polylines) {
        this.googleMap = map;
        this.adapter = adapter;
        this.items = items;
        this.context = context;
        this.appDatabase = AppDatabase.getInstance(context);
        routeFinder = new RouteFinder(appDatabase);
        this.polylines = polylines;
    }

    public void clearRoute() {
        for (Marker m : routeMarkers) {
            m.remove();
        }
        routeMarkers.clear();
        trailIds.clear();
        items.clear();
        adapter.notifyDataSetChanged();
        routeMode = false;
        for (Integer i: polylinesIds) {
            polylines.get(i).setWidth(4f);
        }
        polylinesIds.clear();
    }


    public ArrayList<Integer> getRouteTrailIds() {
        return trailIds;
    }

    public void initRoute(final List<Integer> trailIds) {
        new AsyncTask<Void, Void, List<Trail>>() {

            @Override
            protected List<Trail> doInBackground(Void... params) {
                List<Trail> trails = appDatabase.trailDao().findAllByIds(trailIds);
                for (Trail t : trails) {
                    t.setFirst(appDatabase.placeDao().getById(t.getFirstPoint()));
                    t.setLast(appDatabase.placeDao().getById(t.getLastPoint()));
                }
                return trails;
            }

            @Override
            protected void onPostExecute(List<Trail> trails) {
                if (trails == null || trails.isEmpty())
                    return;

                clearRoute();

                routeMode = true;
                Place start = trails.get(0).getFirst();
                markRoute(start);
                for (int i = 0; i < trails.size() - 1; i++) {
                    createRouteNode(trails.get(i));
                }
                clearAndSetCurrentMarker(trails.get(trails.size()-1).getLast());
            }
        }.execute();
    }

    public void clearAndSetCurrentMarker(Place place) {
        if (currentMarker != null) {
            currentMarker.remove();
        }
        CameraUtils.moveAndZoom(googleMap, place.getLatitude(), place.getLongitude(), 12f);
        currentMarker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(place.getLatitude(), place.getLongitude()))
                .zIndex(MarkerPriority.CURRENT.getValue())
                .icon(BitmapDescriptorFactory.fromAsset("marker.png"))
                .title(place.getName()));
        currentMarkerPlace = place;
        if (routeMode) {
            addCurrentToRoute();
        }
    }

    public void addCurrentToRoute() {
        routeMode = true;
        if (routeMarkers.size() == 0) {
            HashMap<String, String> item = new HashMap<>();
            item.put("name", currentMarker.getTitle());
            item.put("time", "");
            items.add(item);
            adapter.notifyDataSetChanged();
            Marker m = googleMap.addMarker(new MarkerOptions()
                    .position(currentMarker.getPosition())
                    .zIndex(MarkerPriority.FLAG.getValue())
                    .title(currentMarker.getTitle())
                    .icon(BitmapDescriptorFactory.fromAsset("flag.png")));
            routeMarkers.add(m);
        } else {
            addToRoute(currentMarkerPlace);
        }
    }

    public void addToRoute(final Place place) {
        if (!routeMode || routeMarkers.isEmpty()) {
            return;
        }
        final String lastNodeName = routeMarkers.get(routeMarkers.size() - 1).getTitle();
        new AsyncTask<Void, Void, List<Trail>>() {

            @Override
            protected List<Trail> doInBackground(Void... params) {
                return routeFinder.findRoute(lastNodeName, place.getName());
            }

            @Override
            protected void onPostExecute(List<Trail> trails) {
                if (trails.isEmpty()) {
                    Toast.makeText(context, "Nie znaleziono odcinka prowadzącego do tego punktu", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (Trail t: trails) {
                    createRouteNode(t);
                }
            }
        }.execute();
    }

    private void createRouteNode(Trail trail) {
        final Place destination = trail.getLast();
        HashMap<String, String> item = new HashMap<>();
        item.put("name", destination.getName());
        item.put("time", Integer.toString(trail.getTime()) + "m");
        markRoute(destination);
        items.add(item);
        Polyline polyline = polylines.get(trail.getId());
        if (polyline == null) {
            polyline = polylines.get(trail.getTwinTrail());
            polylinesIds.add(trail.getTwinTrail());
        } else {
            polylinesIds.add(trail.getId());
        }
        polyline.setWidth(12f);
        adapter.notifyDataSetChanged();
        trailIds.add(trail.getId());
    }

    private void markRoute(Place place) {
        Marker flag = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(place.getLatitude(), place.getLongitude()))
                .zIndex(MarkerPriority.FLAG.getValue())
                .title(place.getName())
                .icon(BitmapDescriptorFactory.fromAsset("flag.png")));
        routeMarkers.add(flag);
    }
}
