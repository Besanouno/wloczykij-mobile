package pl.basistam.wloczykij.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import pl.basistam.wloczykij.service.retrofit.RetrofitUserService;
 
public class UserService extends Service {

    private static UserService instance;

    public static UserService getInstance() {
        if (instance == null) {
            synchronized (UserService.class) {
                if (instance == null) {
                    instance = new UserService();
                }
            }
        }
        return instance;
    }


    private final RetrofitUserService retrofitUserService;

    private UserService() {
        retrofitUserService = retrofit().create(RetrofitUserService.class);
    }

    public RetrofitUserService userService() {
        return retrofitUserService;
    }

    @Override
    protected final Gson gsonConfiguration() {
        return new GsonBuilder()
                .registerTypeAdapter(Date.class, dateJsonDeserializer())
                .create();
    }

}
