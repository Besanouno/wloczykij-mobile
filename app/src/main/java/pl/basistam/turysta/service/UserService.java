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

import pl.basistam.turysta.exceptions.ServerConnectionException;
import pl.basistam.turysta.json.UserInputJson;
import pl.basistam.turysta.model.UserDetails;

public class UserService {

    public UserDetails obtainUserDetails(String authToken) throws ServerConnectionException {
        HttpURLConnection connection = prepareGetConnection("/users/self", authToken);
        String response = getResponseAsString(connection);
        return getGson().fromJson(response, UserDetails.class);
    }

    public boolean editUser(UserInputJson json, String authToken) throws ServerConnectionException, IOException {
        return true;
    }

    private HttpURLConnection prepareGetConnection(String path, String authToken) throws ServerConnectionException {
        try {
            URL url = new URL("http://192.168.1.3:8070/api" + path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + authToken);
            return connection;
        } catch (IOException e) {
            throw new ServerConnectionException(e.getMessage());
        }
    }

    private String getResponseAsString(HttpURLConnection connection) throws ServerConnectionException {
        try {
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
                return response.toString();
            } else {
                throw new ServerConnectionException(Integer.toString(responseCode));
            }
        } catch (IOException e) {
            throw new ServerConnectionException(e.getMessage());
        }
    }

    private Gson getGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });
        return builder.create();
    }
}
