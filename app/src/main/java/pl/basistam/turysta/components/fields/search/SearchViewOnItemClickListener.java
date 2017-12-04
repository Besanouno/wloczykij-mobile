package pl.basistam.turysta.components.fields.search;


import android.os.AsyncTask;
import android.support.v7.widget.SearchView;

import com.google.android.gms.maps.GoogleMap;

import pl.basistam.turysta.components.utils.CameraUtils;
import pl.basistam.turysta.database.dao.PlaceDao;
import pl.basistam.turysta.database.model.Place;

class SearchViewOnItemClickListener extends AsyncTask<String, Void, Place> {

    private final GoogleMap map;
    private final PlaceDao placeDao;
    private final SearchView.SearchAutoComplete searchView;

    SearchViewOnItemClickListener(
            GoogleMap map,
            PlaceDao placeDao,
            SearchView.SearchAutoComplete searchView) {
        this.map = map;
        this.placeDao = placeDao;
        this.searchView = searchView;
    }

    @Override
    protected Place doInBackground(String... strings) {
        return strings.length > 0 ? placeDao.getByName(strings[0]) : null;
    }

    @Override
    protected void onPostExecute(Place place) {
        if (place != null) {
            CameraUtils.moveAndZoom(map, place.getLatitude(), place.getLongitude(), 9f);
            searchView.setCursorVisible(false);
            searchView.setText(place.getName());
        } else {
            searchView.showDropDown();
        }
    }
}