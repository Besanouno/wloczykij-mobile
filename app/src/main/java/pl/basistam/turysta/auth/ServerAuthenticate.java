package pl.basistam.turysta.auth;

import pl.basistam.turysta.exceptions.ServerConnectionException;
import pl.basistam.turysta.json.UserInputJson;

public interface ServerAuthenticate {
    String signIn(final String login, final String password, final String authType) throws ServerConnectionException;
    void signUp(UserInputJson userInputJson)  throws ServerConnectionException;
}
