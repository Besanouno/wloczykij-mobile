package pl.basistam.turysta.fragments.events.tabs;


import android.app.Activity;

import java.util.List;

import pl.basistam.turysta.R;
import pl.basistam.turysta.dto.EventSimpleDetails;
import pl.basistam.turysta.enums.EventUserStatus;
import pl.basistam.turysta.service.EventService;
import retrofit2.Call;

public class UpcomingEventsDataSet extends TabDataSet {

    public UpcomingEventsDataSet(Activity activity) {
        super(activity);
    }

    @Override
    protected Call<List<EventSimpleDetails>> prepareRequest(String authToken) {
        return EventService.getInstance()
                .eventService()
                .getActiveEventsByType(authToken,
                        new String[]{
                                EventUserStatus.PARTICIPANT.getValue(),
                                EventUserStatus.ADMIN.getValue()
                        });
    }

    @Override
    protected int getListViewId() {
        return R.id.lv_upcoming_events;
    }
}