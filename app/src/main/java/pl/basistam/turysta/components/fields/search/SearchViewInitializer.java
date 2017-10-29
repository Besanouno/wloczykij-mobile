package pl.basistam.turysta.components.fields.search;


import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.SearchView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pl.basistam.turysta.R;
import pl.basistam.turysta.database.dao.PlaceDao;
import pl.basistam.turysta.database.model.Place;

class SearchViewInitializer extends AsyncTask<Void, Void, List<Place>> {

    private final Context context;
    private final PlaceDao placeDao;
    private final SearchView.SearchAutoComplete searchView;

    SearchViewInitializer(
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
            entry.put("icon", Integer.toString(R.drawable.ic_peak));
            items.add(entry);
        }
        String[] from = {"icon", "name"};
        int[] to = {R.id.flag, R.id.txt};

        SimpleAdapter adapter = new SimpleAdapter(context, items, R.layout.search_field_item, from, to);
        searchView.setAdapter(adapter);
    }
}