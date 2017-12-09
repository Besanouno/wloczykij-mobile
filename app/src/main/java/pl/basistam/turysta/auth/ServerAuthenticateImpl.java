package pl.basistam.turysta.auth;


import java.io.IOException;

import pl.basistam.turysta.dto.UserInputDto;
import pl.basistam.turysta.errors.ServerConnectionException;
import pl.basistam.turysta.dto.TokenDto;
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
            Response<TokenDto> response = AuthService.getInstance()
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
    public Response<Void> signUp(UserInputDto userInput) throws ServerConnectionException {
        try {
            return UserService.getInstance()
                    .userService()
                    .signUp(userInput)
                    .execute();
        } catch (IOException e) {
            throw new ServerConnectionException(e.getMessage());
        }
    }


}
