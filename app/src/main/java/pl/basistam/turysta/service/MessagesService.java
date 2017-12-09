package pl.basistam.turysta.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Date;

import pl.basistam.turysta.service.retrofit.RetrofitMessageService;

public class MessagesService extends Service {
    private static MessagesService instance;

    public static MessagesService getInstance() {
        if (instance == null) {
            synchronized (MessagesService.class) {
                if (instance == null) {
                    instance = new MessagesService();
                }
            }
        }
        return instance;
    }


    private final RetrofitMessageService retrofitMessageService;

    private MessagesService() {
        retrofitMessageService = retrofit().create(RetrofitMessageService.class);
    }

    public RetrofitMessageService messageService() {
        return retrofitMessageService;
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
