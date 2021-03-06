package pl.basistam.wloczykij.service.retrofit;

import java.util.List;

import pl.basistam.wloczykij.dto.Page;
import pl.basistam.wloczykij.dto.Relation;
import pl.basistam.wloczykij.dto.UserInput;
import pl.basistam.wloczykij.dto.UserDetails;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitUserService {
    @GET("users/self")
    Call<UserDetails> getUserDetails(@Header("Authorization") final String authorization);

    @GET("users/{id}")
    Call<UserDetails> getPersonDetails(@Header("Authorization") final String authorization, @Path("id") final String login);

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

    @Headers("Content-Type: application/json")
    @PUT("users/self")
    Call<Void> update(@Header("Authorization") final String authorization, @Body UserInput userInput);
}
