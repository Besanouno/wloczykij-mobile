package pl.basistam.wloczykij.components.fields.search;


import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.SearchView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pl.basistam.wloczykij.R;
import pl.basistam.wloczykij.database.dao.PlaceDao;
import pl.basistam.wloczykij.database.model.Place;

public class SearchViewInitializer extends AsyncTask<Void, Void, List<Place>> {

    private final Context context;
    private final PlaceDao placeDao;
    private final SearchView.SearchAutoComplete searchView;

    public SearchViewInitializer(
            Context context,
            PlaceDao placeDao,
            SearchView.SearchAutoComplete searchView) {
        this.placeDao = placeDao;
        this.context = context;
        this.searchView = searchView;
    }

    @Override
    protected List<Place> doInBackground(Void... voids) {
        return placeDao.getAll();
    }

    @Override
    protected void onPostExecute(List<Place> places) {
        final List<HashMap<String, String>> items = new ArrayList<>(places.size());
        for (Place p : places) {
            HashMap<String, String> entry = new HashMap<>();
            entry.put("name", p.getName());
            entry.put("height", Double.toString(p.getHeight()));
            items.add(entry);
        }
        String[] from = {"height", "name"};
        int[] to = {R.id.tv_height, R.id.tv_name};

        SimpleAdapter adapter = new SimpleAdapter(context, items, R.layout.item_search_place, from, to);
        searchView.setAdapter(adapter);
    }
}