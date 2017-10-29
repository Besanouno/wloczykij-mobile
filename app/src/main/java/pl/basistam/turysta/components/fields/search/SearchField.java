package pl.basistam.turysta.components.fields.search;

import android.content.Context;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.gms.maps.GoogleMap;

import java.util.HashMap;

import pl.basistam.turysta.R;
import pl.basistam.turysta.components.utils.KeyboardUtils;
import pl.basistam.turysta.database.AppDatabase;
import pl.basistam.turysta.database.dao.PlaceDao;

public class SearchField {
    private final Context context;
    private final SearchView searchView;
    private final GoogleMap map;

    public SearchField(
            final Context context,
            final SearchView actionView,
            final GoogleMap map) {
        this.context = context;
        this.searchView = actionView;
        this.map = map;
    }

    public void initialize() {
        final SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        final PlaceDao placeDao = AppDatabase.getInstance(context).placeDao();

        searchAutoComplete.setDropDownBackgroundDrawable(context.getDrawable(R.drawable.search_drop_down_background));
        searchAutoComplete.setDropDownVerticalOffset(10);
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                HashMap<String, String> place = (HashMap<String, String>) adapterView.getItemAtPosition(position);
                final String name = place.get("name");
                new SearchViewOnItemClickListener(map, placeDao, searchAutoComplete).execute(name);
                KeyboardUtils.hide(context, view);
            }
        });
        new SearchViewInitializer(context, placeDao, searchAutoComplete).execute();
    }
}
