package pl.basistam.turysta.actions;

import android.os.AsyncTask;

import pl.basistam.turysta.components.fields.PlaceDetailsField;
import pl.basistam.turysta.database.dao.PlaceDao;
import pl.basistam.turysta.database.model.Place;

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
