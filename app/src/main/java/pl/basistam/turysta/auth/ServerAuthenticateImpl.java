package pl.basistam.turysta.auth;


import android.accounts.AccountManager;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;

import pl.basistam.turysta.exceptions.AuthorizationException;
import pl.basistam.turysta.json.UserInputJson;
import pl.basistam.turysta.model.UserDetails;

import static android.util.Base64.DEFAULT;

public class ServerAuthenticateImpl implements ServerAuthenticate {

    private final String clientId = "id";
    private final String clientSecret = "dupa";
    private static ServerAuthenticate serverAuthenticate;

    private ServerAuthenticateImpl() {}

    public static ServerAuthenticate getInstance() {
        if (serverAuthenticate == null) {
            synchronized (ServerAuthenticateImpl.class) {
                if (serverAuthenticate == null) {
                    serverAuthenticate = new ServerAuthenticateImpl();
                }
            }
        }
        return serverAuthenticate;
    }

    @Override
    public String signIn(String login, String password, String authType) throws AuthorizationException {
        String authToken = null;
        try {
            URL url = new URL("http://192.168.1.3:8070/api/oauth/token");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            String basicAuth = Base64.encodeToString((clientId + ":" + clientSecret).getBytes(), DEFAULT);
            connection.setRequestProperty("Authorization", "Basic " + basicAuth);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String body = "grant_type=password&username=" + login + "&password=" + password;
            connection.setDoOutput(true);

            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes(body);
            dataOutputStream.flush();
            dataOutputStream.close();

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

                JSONObject jsonResponse = new JSONObject(response.toString());
                authToken = jsonResponse.getString("access_token");
            }
        } catch (IOException | JSONException e) {
            throw new AuthorizationException(e.getMessage());
        }
        if (authToken == null) {
            throw new AuthorizationException("Auth token is empty");
        }
        return authToken;
    }

    @Override
    public void signUp(UserInputJson userInputJson, String authType)  throws AuthorizationException {
        try {
            URL url = new URL("http://192.168.1.3:8070/api/user");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            String json = new Gson().toJson(userInputJson);
            connection.setDoOutput(true);

            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes(json);
            dataOutputStream.flush();
            dataOutputStream.close();

            int responseCode = connection.getResponseCode();
            if (responseCode != 201) {
                throw new AuthorizationException(Integer.toString(responseCode));
            }
        } catch (IOException e) {
            throw new AuthorizationException(e.getMessage());
        }
    }


}
