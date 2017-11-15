package pl.basistam.turysta.auth;


import android.util.Base64;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import pl.basistam.turysta.exceptions.ServerConnectionException;
import pl.basistam.turysta.json.UserInputJson;

import static android.util.Base64.DEFAULT;

public class ServerAuthenticateImpl implements ServerAuthenticate {

    private final String clientId = "id";
    private final String clientSecret = "dupa";
    private static ServerAuthenticate serverAuthenticate;

    private ServerAuthenticateImpl() {
    }

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
    public String signIn(String login, String password, String authType) throws ServerConnectionException {
        String authToken = null;
        try {
            URL url = new URL("http://10.128.6.57:8070/api/oauth/token");
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
                String response = readResponse(connection.getInputStream());
                JSONObject jsonResponse = new JSONObject(response);
                authToken = jsonResponse.getString("access_token");
            } else {
                String response = readResponse(connection.getErrorStream());
                throw new ServerConnectionException(response);
            }
        } catch (IOException | JSONException e) {
            throw new ServerConnectionException(e.getMessage());
        }
        if (authToken == null) {
            throw new ServerConnectionException("Auth token is empty");
        }
        return authToken;
    }

    private String readResponse(InputStream inputStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    @Override
    public void signUp(UserInputJson userInputJson, String authType) throws ServerConnectionException {
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
                throw new ServerConnectionException(Integer.toString(responseCode));
            }
        } catch (IOException e) {
            throw new ServerConnectionException(e.getMessage());
        }
    }


}
