package pl.basistam.wloczykij.auth;

import pl.basistam.wloczykij.exceptions.ServerConnectionException;
import pl.basistam.wloczykij.dto.UserInput;

public interface ServerAuthenticate {
    String signIn(final String login, final String password, final String authType) throws ServerConnectionException;
    void signUp(UserInput userInput)  throws ServerConnectionException;
}
