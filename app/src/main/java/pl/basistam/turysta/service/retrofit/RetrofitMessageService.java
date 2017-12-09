package pl.basistam.turysta.service.retrofit;


import java.util.List;

import pl.basistam.turysta.dto.EventSimpleDetails;
import pl.basistam.turysta.dto.MessageDto;
import pl.basistam.turysta.dto.Page;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitMessageService {

    @GET("messages/{guid}")
    Call<Page<MessageDto>> getPage(@Header("Authorization") final String authorization, @Path("guid") final String guid, @Query("page") int page, @Query("size") int size);
}
