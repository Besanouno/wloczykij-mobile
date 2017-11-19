package pl.basistam.turysta.fragments.tabs;


import android.app.Activity;

import java.util.List;

import pl.basistam.turysta.R;
import pl.basistam.turysta.dto.EventSimpleDetails;
import pl.basistam.turysta.service.EventService;
import retrofit2.Call;

public class ArchivalEventsDataSet extends TabDataSet {

    public ArchivalEventsDataSet(Activity activity) {
        super(activity);
    }

    @Override
    protected Call<List<EventSimpleDetails>> prepareRequest(String authToken) {
        return EventService.getInstance()
                .eventService()
                .getArchivalEvents(authToken);
    }

    @Override
    protected int getListViewId() {
        return R.id.lv_archival_events;
    }
}