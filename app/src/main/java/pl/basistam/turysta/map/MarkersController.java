package pl.basistam.turysta.map;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pl.basistam.turysta.adapters.RouteAdapter;
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
    private final AppDatabase appDatabase;
    private final List<Marker> routeMarkers = new ArrayList<>();
    private final ArrayList<Integer> trailIds = new ArrayList<>();
    private RouteFinder routeFinder;

    private boolean routeMode = false;

    public MarkersController(GoogleMap map, Context context, RouteAdapter adapter, List<HashMap<String, String>> items) {
        this.googleMap = map;
        this.adapter = adapter;
        this.items = items;
        this.context = context;
        this.appDatabase = AppDatabase.getInstance(context);
        routeFinder = new RouteFinder(appDatabase);
    }

    public void clearAndSetCurrentMarker(Marker marker) {
        if (currentMarker != null) {
            currentMarker.remove();
        }
        currentMarker = marker;
        if (routeMode) {
            addCurrentToRoute();
        }
    }


    public List<Marker> getRouteMarkers() {
        return routeMarkers;
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
    }

    public void addToRoute(final Marker marker) {
        routeMode = true;
        if (routeMarkers.size() > 0) {
            final String previousPlaceName = routeMarkers.get(routeMarkers.size() - 1).getTitle();
            new AsyncTask<String, Void, List<Trail>>() {

                @Override
                protected List<Trail> doInBackground(String... params) {
                    String placeName = params[0];
                    return routeFinder.findRoute(previousPlaceName, placeName);
                }

                @Override
                protected void onPostExecute(List<Trail> trails) {
                    if (trails.isEmpty()) {
                        Toast.makeText(context, "Nie znaleziono odcinka prowadzącego do tego punktu", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String name = trails.get(0).getLast().getName();
                    String time = Integer.toString(trails.get(0).getTime()) + "m";
                    putMarkerToRoute(
                            marker.getPosition(),
                            trails.get(0).getId(),
                            name,
                            time);
                }
            }.execute(marker.getTitle());
        } else {
            putMarkerToRoute(marker.getPosition(), null, marker.getTitle(), "");
        }
    }

    private void putMarkerToRoute(LatLng position, Integer trailId, String name, String time) {
        HashMap<String, String> item = new HashMap<>();
        item.put("name", name);
        item.put("time", time);
        items.add(item);
        adapter.notifyDataSetChanged();
        Marker m = googleMap.addMarker(new MarkerOptions()
                .position(position)
                .zIndex(MarkerPriority.FLAG.getValue())
                .title(name)
                .icon(BitmapDescriptorFactory.fromAsset("flag.png")));
        routeMarkers.add(m);
        if (trailId != null)
            trailIds.add(trailId);
    }

    public void addCurrentToRoute() {
        addToRoute(currentMarker);
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
                clearRoute();
                routeMode = true;
                Place firstPoint = trails.get(0).getFirst();
                putMarkerToRoute(
                        new LatLng(firstPoint.getLatitude(), firstPoint.getLongitude()),
                        null,
                        firstPoint.getName(),
                        ""
                );
                for (int i = 0; i < trails.size(); i++) {
                    clearAndSetCurrentMarker(googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(trails.get(i).getLast().getLatitude(), trails.get(i).getLast().getLongitude()))
                            .zIndex(MarkerPriority.FLAG.getValue())
                            .title(trails.get(i).getLast().getName())
                            .icon(BitmapDescriptorFactory.fromAsset("flag.png"))));
                }
            }
        }.execute();
    }
}
