package pl.basistam.turysta.actions;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.Marker;

import pl.basistam.turysta.components.fields.PlaceDetailsField;
import pl.basistam.turysta.database.dao.PlaceDao;
import pl.basistam.turysta.database.model.Place;
import pl.basistam.turysta.map.MarkersController;

public class UpdatePlaceDetailsAction extends AsyncTask<String, Void, Place> {

    private PlaceDetailsField placeDetailsField;
    private PlaceDao placeDao;
    private MarkersController markersController;

    public UpdatePlaceDetailsAction(
            PlaceDetailsField placeDetailsField,
            PlaceDao placeDao,
            MarkersController markersController) {
        this.placeDetailsField = placeDetailsField;
        this.placeDao = placeDao;
        this.markersController = markersController;
    }

    @Override
    protected Place doInBackground(String... name) {
        return placeDao.getByName(name[0]);
    }

    @Override
    protected void onPostExecute(Place place) {
        placeDetailsField.updateName(place.getName());
        placeDetailsField.updateHeightAboveSeaLevel(place.getHeight());
        Marker marker = placeDetailsField.updateMapPosition(place.getLatitude(), place.getLongitude());
        marker.setTitle(place.getName());
        markersController.clearCurrentMarkerIfNotNullAndSet(marker);
    }
}
