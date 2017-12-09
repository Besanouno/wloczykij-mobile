package pl.basistam.turysta.auth;

import pl.basistam.turysta.dto.UserInputDto;
import pl.basistam.turysta.errors.ServerConnectionException;
import retrofit2.Response;

public interface ServerAuthenticate {
    String signIn(final String login, final String password, final String authType) throws ServerConnectionException;
    Response<Void> signUp(UserInputDto userInput)  throws ServerConnectionException;
}
