package pl.basistam.turysta.service.retrofit;

import pl.basistam.turysta.dto.TokenDto;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface RetrofitAuthService {
    @Headers({
            "Authorization: Basic aWQ6ZHVwYQ=="
    })
    @POST("oauth/token")
    @FormUrlEncoded
    Call<TokenDto> getTokenDetails(
            @Field("grant_type") final String grantType,
            @Field("username") final String username,
            @Field("password") final String password
    );
}
