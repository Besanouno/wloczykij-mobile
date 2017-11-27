package pl.basistam.turysta.service.retrofit;


import java.util.List;

import pl.basistam.turysta.dto.EventDto;
import pl.basistam.turysta.dto.EventSimpleDetails;
import pl.basistam.turysta.dto.Page;
import pl.basistam.turysta.dto.RelationItem;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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

    @PUT("events/{guid}")
    Call<Void> updateEvent(@Header("Authorization") final String authorization, @Path("guid") final String eventGuid, @Body final EventDto eventDto);

    @GET("events/{guid}")
    Call<EventDto> getEvent(@Header("Authorization") final String authToken, @Path("guid") final String eventGuid);

    @PUT("events/{guid}/users")
    Call<Void> updateParticipants(@Header("Authorization") final String authtoken, @Path("guid") final String eventGuid, @Body final List<RelationItem> changes);

    @POST("events/{guid}/invitations/acceptance")
    Call<Void> acceptInvitation(@Header("Authorization") final String authtoken, @Path("guid") final String eventGuid);

    @POST("events/{guid}/invitations/rejection")
    Call<Void> rejectInvitation(@Header("Authorization") final String authtoken, @Path("guid") final String eventGuid);

    @POST("events/{guid}/applications")
    Call<Void> apply(@Header("Authorization") final String authToken, @Path("guid") final String eventGuid);

    @DELETE("events/{guid}")
    Call<Void> remove(@Header("Authorization") final String authtoken, @Path("guid") final String eventGuid);

    @GET("events/public")
    Call<Page<EventSimpleDetails>> getPublicEvents(@Header("Authorization") final String authtoken, @Query("page") int page, @Query("size") int size);

}
