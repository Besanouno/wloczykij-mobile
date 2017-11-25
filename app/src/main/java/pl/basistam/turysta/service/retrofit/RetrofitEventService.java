package pl.basistam.turysta.service.retrofit;


import java.util.List;

import pl.basistam.turysta.dto.EventDto;
import pl.basistam.turysta.dto.EventFullDto;
import pl.basistam.turysta.dto.EventSimpleDetails;
import pl.basistam.turysta.dto.UserItem;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitEventService {
    @GET("events")
    Call<List<EventSimpleDetails>> getActiveEventsByType(@Header("Authorization") final String authorization, @Query("statusCode") final String statusCode);

    @GET("events/archive")
    Call<List<EventSimpleDetails>> getArchivalEvents(@Header("Authorization") final String authorization);

    @POST("events")
    Call<Void> saveEvent(@Header("Authorization") final String authorization, @Body final EventDto eventDto);

    @GET("events/{guid}/full")
    Call<EventFullDto> getFullEvent(@Header("Authorization") final String authToken, @Path("guid") final String eventGuid);

    @PUT("events/{guid}/users")
    Call<Void> updateParticipants(@Header("Authorization") final String authtoken, @Path("guid") final String eventGuid, @Body final List<UserItem> changes);
}
