package pl.basistam.turysta.map;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.SparseArray;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.basistam.turysta.database.AppDatabase;
import pl.basistam.turysta.database.dao.TrailDao;
import pl.basistam.turysta.database.dao.TrailPointDao;
import pl.basistam.turysta.database.model.Trail;
import pl.basistam.turysta.database.model.TrailPoint;


public class TrailsInitializer extends AsyncTask<Void, Void, List<Trail>> {

    private AppDatabase appDatabase;
    private GoogleMap map;
    private SparseArray<Polyline> polylines = new SparseArray<>();

    public TrailsInitializer(AppDatabase appDatabase, GoogleMap map) {
        this.appDatabase = appDatabase;
        this.map = map;
    }

    @Override
    protected List<Trail> doInBackground(Void... voids) {
        TrailDao trailDao = appDatabase.trailDao();
        TrailPointDao trailPointDao = appDatabase.trailPointDao();
        List<Trail> trails = trailDao.getDrawable();
        for (Trail trail : trails) {
            trail.setPoints(trailPointDao.getByTrailId(trail.getId()));
        }
        return trails;
    }

    @Override
    protected void onPostExecute(List<Trail> trails) {
        for (Trail trail : trails) {
            Polyline polyline = map.addPolyline(
                    new PolylineOptions()
                            .addAll(getTrailsCoordinates(trail.getPoints()))
                            .color(trail.getColour())
                            .clickable(true)
                            .width(4f)
            );
            polylines.put(trail.getId(), polyline);
        }
    }

    private List<LatLng> getTrailsCoordinates(List<TrailPoint> trails) {
        List<LatLng> coordinates = new ArrayList<>(trails.size());
        for (TrailPoint tp : trails) {
            coordinates.add(new LatLng(tp.getLatitude(), tp.getLongitude()));
        }
        return coordinates;
    }

    public SparseArray<Polyline> getPolylines() {
        return polylines;
    }
}