package pl.basistam.wloczykij.service.retrofit;


import java.util.List;

import pl.basistam.wloczykij.dto.WeatherDto;
import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitWeatherService {
    @GET("weather")
    Call<List<WeatherDto>> getWeather();
}
