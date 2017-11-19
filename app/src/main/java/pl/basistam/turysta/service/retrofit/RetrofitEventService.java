package pl.basistam.turysta.service.retrofit;


import pl.basistam.turysta.dto.EventSimpleDetails;
import pl.basistam.turysta.dto.Page;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface RetrofitEventService {
    @GET("events")
    Call<Page<EventSimpleDetails>> getEventsByType(@Header("Authorization") final String authorization);
}
