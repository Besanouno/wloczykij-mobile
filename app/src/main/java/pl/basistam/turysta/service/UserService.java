package pl.basistam.turysta.service;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import pl.basistam.turysta.auth.LoggedUser;
import pl.basistam.turysta.exceptions.AuthorizationException;
import pl.basistam.turysta.model.UserDetails;

public class UserService {

    public void obtainUserDetails(String authToken) throws AuthorizationException {
        try {
            URL url = new URL("http://192.168.1.3:8070/api/users/self");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + authToken);
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                GsonBuilder builder = new GsonBuilder();
                builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return new Date(json.getAsJsonPrimitive().getAsLong());
                    }
                });
                Gson gson = builder.create();
                UserDetails userDetails = gson.fromJson(response.toString(), UserDetails.class);
                LoggedUser.getInstance().setUserDetails(userDetails);
            }
        } catch (IOException e) {
            throw new AuthorizationException(e.getMessage());
        }
    }
}
