package pl.basistam.turysta.service.retrofit;


import java.util.List;

import pl.basistam.turysta.dto.WeatherDto;
import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitWeatherService {
    @GET("weather")
    Call<List<WeatherDto>> getWeather();
}
