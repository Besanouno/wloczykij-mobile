package pl.basistam.turysta.service.retrofit;

import pl.basistam.turysta.dto.Page;
import pl.basistam.turysta.dto.UserInput;
import pl.basistam.turysta.dto.UserSimpleDetails;
import pl.basistam.turysta.dto.UserDetails;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitUserService {
    @GET("users/self")
    Call<UserDetails> getUserDetails(@Header("Authorization") final String authorization);

    @GET("users")
    Call<Page<UserSimpleDetails>> getUserSimpleDetailsByPattern(
            @Header("Authorization") final String authorization,
            @Query("pattern") final String pattern,
            @Query("page") final int page,
            @Query("size") final int size);

    @Headers("Content-Type: application/json")
    @POST("user")
    Call<Void> signUp(@Body UserInput userInput);
}
