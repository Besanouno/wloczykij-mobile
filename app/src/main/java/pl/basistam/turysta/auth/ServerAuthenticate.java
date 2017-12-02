package pl.basistam.turysta.auth;

import pl.basistam.turysta.dto.UserInputDto;
import pl.basistam.turysta.errors.ServerConnectionException;

public interface ServerAuthenticate {
    String signIn(final String login, final String password, final String authType) throws ServerConnectionException;
    void signUp(UserInputDto userInput)  throws ServerConnectionException;
}
