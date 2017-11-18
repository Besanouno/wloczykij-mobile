package pl.basistam.turysta.map;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import pl.basistam.turysta.database.AppDatabase;
import pl.basistam.turysta.database.model.Place;
import pl.basistam.turysta.database.type.PlaceType;


public class PlacesInitializer extends AsyncTask<Void, Void, List<Place>> {

    private AppDatabase appDatabase;
    private GoogleMap map;

    public PlacesInitializer(AppDatabase appDatabase, GoogleMap map) {
        this.appDatabase = appDatabase;
        this.map = map;
    }

    @Override
    protected List<Place> doInBackground(Void... voids) {
        return appDatabase.placeDao().getAll();
    }

    @Override
    protected void onPostExecute(List<Place> places) {
        for (Place place : places) {
            LatLng coordinates = new LatLng(place.getLatitude(), place.getLongitude());
            map.addMarker(new MarkerOptions()
                    .position(coordinates)
                    .icon(getIcon(place.getType()))
                    .title(place.getName()));
        }
    }

    private BitmapDescriptor getIcon(PlaceType placeType) {
        return BitmapDescriptorFactory.fromAsset(placeType.name().toLowerCase() + ".png");
    }

}
