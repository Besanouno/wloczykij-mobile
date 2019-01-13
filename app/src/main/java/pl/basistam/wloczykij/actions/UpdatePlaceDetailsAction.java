package pl.basistam.wloczykij.actions;

import android.os.AsyncTask;

import pl.basistam.wloczykij.components.fields.PlaceDetailsField;
import pl.basistam.wloczykij.database.dao.PlaceDao;
import pl.basistam.wloczykij.database.model.Place;
import pl.basistam.wloczykij.map.MarkersController;

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
        if (markersController.isRouteMode() && markersController.isLast(place)) {
            markersController.removeLast();
        } else {
            markersController.clearAndSetCurrentMarker(place);
        }
    }
}
