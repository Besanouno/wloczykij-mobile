package pl.basistam.wloczykij.actions;

import android.os.AsyncTask;

import pl.basistam.wloczykij.components.fields.PlaceDetailsField;
import pl.basistam.wloczykij.database.dao.PlaceDao;
import pl.basistam.wloczykij.database.model.Place;

public class UpdatePlaceDetailsAction extends AsyncTask<String, Void, Place> {

    private PlaceDetailsField placeDetailsField;
    private PlaceDao placeDao;

    public UpdatePlaceDetailsAction(PlaceDetailsField placeDetailsField, PlaceDao placeDao) {
        this.placeDetailsField = placeDetailsField;
        this.placeDao = placeDao;
    }

    @Override
    protected Place doInBackground(String... name) {
        return placeDao.getByName(name[0]);
    }

    @Override
    protected void onPostExecute(Place place) {
        placeDetailsField.updateName(place.getName());
        placeDetailsField.updateHeightAboveSeaLevel(place.getHeight());
        placeDetailsField.updateMapPosition(place.getLatitude(), place.getLongitude());
    }
}
