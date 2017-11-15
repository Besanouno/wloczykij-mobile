package pl.basistam.turysta.service;


import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import pl.basistam.turysta.service.retrofit.RetrofitAuthService;

public class AuthService extends Service {
    private static AuthService instance;

    public static AuthService getInstance() {
        if (instance == null) {
            synchronized (UserService.class) {
                if (instance == null) {
                    instance = new AuthService();
                }
            }
        }
        return instance;
    }


    private final RetrofitAuthService retrofitAuthService;

    private AuthService() {
        retrofitAuthService = retrofit().create(RetrofitAuthService.class);
    }

    public RetrofitAuthService authService() {
        return retrofitAuthService;
    }

    @Override
    protected final Gson gsonConfiguration() {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }
}
