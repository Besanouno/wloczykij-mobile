package pl.basistam.turysta.service.retrofit;


import java.util.List;

import pl.basistam.turysta.dto.EventDto;
import pl.basistam.turysta.dto.EventSimpleDetails;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitEventService {
    @GET("events")
    Call<List<EventSimpleDetails>> getActiveEventsByType(@Header("Authorization") final String authorization, @Query("statusCode") final String statusCode);

    @GET("events/archive")
    Call<List<EventSimpleDetails>> getArchivalEvents(@Header("Authorization") final String authorization);

    @POST("events")
    Call<Void> saveEvent(@Header("Authorization") final String authorization, @Body final EventDto eventDto);
}
