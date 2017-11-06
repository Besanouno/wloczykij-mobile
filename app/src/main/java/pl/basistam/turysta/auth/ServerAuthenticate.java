package pl.basistam.turysta.auth;

import pl.basistam.turysta.exceptions.AuthorizationException;
import pl.basistam.turysta.json.UserInputJson;

public interface ServerAuthenticate {
    String signIn(final String login, final String password, final String authType) throws AuthorizationException;
    void signUp(UserInputJson userInputJson, final String authType)  throws AuthorizationException;
}
