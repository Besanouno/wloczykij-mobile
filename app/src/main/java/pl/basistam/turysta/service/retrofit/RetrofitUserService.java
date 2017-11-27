package pl.basistam.turysta.service.retrofit;

import java.util.List;

import pl.basistam.turysta.dto.Page;
import pl.basistam.turysta.dto.RelationItem;
import pl.basistam.turysta.dto.UserDto;
import pl.basistam.turysta.dto.UserInputDto;
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
    Call<UserDto> getUserDetails(@Header("Authorization") final String authorization);

    @GET("users/{id}")
    Call<UserDto> getPersonDetails(@Header("Authorization") final String authorization, @Path("id") final String login);

    @GET("users")
    Call<Page<UserDto>> getUsersByPattern(
            @Header("Authorization") final String authorization,
            @Query("pattern") final String pattern,
            @Query("page") final int page,
            @Query("size") final int size);

    @Headers("Content-Type: application/json")
    @POST("users")
    Call<Void> signUp(@Body UserInputDto userInput);

    @GET("users/self/relations")
    Call<List<UserDto>> getRelations(@Header("Authorization") final String authorization);

    @PUT("users/self/relations")
    Call<Void> updateRelations(@Header("Authorization") final String authorization, @Body List<RelationItem> relationItems);

    @Headers("Content-Type: application/json")
    @PUT("users/self")
    Call<Void> update(@Header("Authorization") final String authorization, @Body UserInputDto userInput);
}
