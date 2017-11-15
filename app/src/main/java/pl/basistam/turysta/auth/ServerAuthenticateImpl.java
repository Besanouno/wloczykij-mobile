package pl.basistam.turysta.auth;


import java.io.IOException;

import pl.basistam.turysta.exceptions.ServerConnectionException;
import pl.basistam.turysta.json.TokenDetails;
import pl.basistam.turysta.json.UserInputJson;
import pl.basistam.turysta.service.AuthService;
import pl.basistam.turysta.service.UserService;
import retrofit2.Response;

public class ServerAuthenticateImpl implements ServerAuthenticate {

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
        String authToken;
        try {
            Response<TokenDetails> response = AuthService.getInstance()
                    .authService()
                    .getTokenDetails("password", login, password)
                    .execute();
            if (response.isSuccessful() && response.body() != null) {
                authToken = response.body().getAccessToken();
            } else {
                throw new ServerConnectionException(Integer.toString(response.code()));
            }
        } catch (IOException e) {
            throw new ServerConnectionException(e.getMessage());
        }
        if (authToken == null) {
            throw new ServerConnectionException("Auth token is empty");
        }
        return authToken;
    }

    @Override
    public void signUp(UserInputJson userInputJson) throws ServerConnectionException {
        try {
            Response<Void> response = UserService.getInstance()
                    .userService()
                    .signUp(userInputJson)
                    .execute();
            if (!response.isSuccessful()) {
                throw new ServerConnectionException(Integer.toString(response.code()));
            }
        } catch (IOException e) {
            throw new ServerConnectionException(e.getMessage());
        }
    }


}
