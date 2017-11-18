package pl.basistam.turysta.auth;


import java.io.IOException;

import pl.basistam.turysta.exceptions.ServerConnectionException;
import pl.basistam.turysta.dto.TokenDetails;
import pl.basistam.turysta.dto.UserInput;
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
        try {
            Response<TokenDetails> response = AuthService.getInstance()
                    .authService()
                    .getTokenDetails("password", login, password)
                    .execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body().getAccessToken();
            } else {
                throw new ServerConnectionException("Access token is empty: " + Integer.toString(response.code()));
            }
        } catch (IOException e) {
            throw new ServerConnectionException(e.getMessage());
        }
    }

    @Override
    public void signUp(UserInput userInput) throws ServerConnectionException {
        try {
            Response<Void> response = UserService.getInstance()
                    .userService()
                    .signUp(userInput)
                    .execute();
            if (!response.isSuccessful()) {
                throw new ServerConnectionException(Integer.toString(response.code()));
            }
        } catch (IOException e) {
            throw new ServerConnectionException(e.getMessage());
        }
    }


}
