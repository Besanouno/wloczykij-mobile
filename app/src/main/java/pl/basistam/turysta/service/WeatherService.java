package pl.basistam.turysta.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import pl.basistam.turysta.service.retrofit.RetrofitWeatherService;

public class WeatherService extends Service {
    private static WeatherService instance;

    public static WeatherService getInstance() {
        if (instance == null) {
            synchronized (WeatherService.class) {
                if (instance == null) {
                    instance = new WeatherService();
                }
            }
        }
        return instance;
    }


    private final RetrofitWeatherService retrofitWeatherService;

    private WeatherService() {
        retrofitWeatherService = retrofit().create(RetrofitWeatherService.class);
    }

    public RetrofitWeatherService weatherService() {
        return retrofitWeatherService;
    }

    @Override
    protected Gson gsonConfiguration() {
        return new GsonBuilder()
                .registerTypeAdapter(Date.class, dateJsonDeserializer())
                .create();
    }
}
