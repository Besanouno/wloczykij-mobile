package pl.basistam.wloczykij.auth;

import pl.basistam.wloczykij.dto.UserInputDto;
import pl.basistam.wloczykij.errors.ServerConnectionException;
import retrofit2.Response;

public interface ServerAuthenticate {
    String signIn(final String login, final String password, final String authType) throws ServerConnectionException;
    Response<Void> signUp(UserInputDto userInput)  throws ServerConnectionException;
}
