package pl.basistam.turysta.service.retrofit;

import java.util.List;

import pl.basistam.turysta.dto.Page;
import pl.basistam.turysta.dto.Relation;
import pl.basistam.turysta.dto.UserInput;
import pl.basistam.turysta.dto.UserSimpleDetails;
import pl.basistam.turysta.dto.UserDetails;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface RetrofitUserService {
    @GET("users/self")
    Call<UserDetails> getUserDetails(@Header("Authorization") final String authorization);

    @GET("users")
    Call<Page<Relation>> getUserSimpleDetailsByPattern(
            @Header("Authorization") final String authorization,
            @Query("pattern") final String pattern,
            @Query("page") final int page,
            @Query("size") final int size);

    @Headers("Content-Type: application/json")
    @POST("users")
    Call<Void> signUp(@Body UserInput userInput);

    @GET("users/self/relations")
    Call<List<Relation>> getRelations(@Header("Authorization") final String authorization);

    @PUT("users/self/relations")
    Call<Void> updateRelations(@Header("Authorization") final String authorization, @Body List<Relation> relations);
}
