package pl.basistam.wloczykij.service.retrofit;


import pl.basistam.wloczykij.dto.MessageDto;
import pl.basistam.wloczykij.dto.Page;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitMessageService {

    @GET("messages/{guid}")
    Call<Page<MessageDto>> getPage(@Header("Authorization") final String authorization, @Path("guid") final String guid, @Query("page") int page, @Query("size") int size);
}
