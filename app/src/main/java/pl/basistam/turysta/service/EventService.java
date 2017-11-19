package pl.basistam.turysta.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.Date;

import pl.basistam.turysta.service.retrofit.RetrofitEventService;
import pl.basistam.turysta.service.retrofit.RetrofitUserService;

public class EventService extends Service {
    private static EventService instance;

    public static EventService getInstance() {
        if (instance == null) {
            synchronized (EventService.class) {
                if (instance == null) {
                    instance = new EventService();
                }
            }
        }
        return instance;
    }


    private final RetrofitEventService retrofitUserService;

    private EventService() {
        retrofitUserService = retrofit().create(RetrofitEventService.class);
    }

    public RetrofitEventService eventService() {
        return retrofitUserService;
    }

    @Override
    protected final Gson gsonConfiguration() {
        return new GsonBuilder()
                .registerTypeAdapter(Date.class, dateJsonDeserializer())
                .registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
                            @Override
                            public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext
                                    context) {
                                return src == null ? null : new JsonPrimitive(src.getTime());
                            }
                        }
                )
                .create();
    }
}
