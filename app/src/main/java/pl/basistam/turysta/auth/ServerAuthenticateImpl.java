package pl.basistam.turysta.auth;


import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import pl.basistam.turysta.exceptions.AuthorizationException;

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
    public String signUp(String name, String email, String password, String authType)  throws AuthorizationException {
        return null;
    }
}
