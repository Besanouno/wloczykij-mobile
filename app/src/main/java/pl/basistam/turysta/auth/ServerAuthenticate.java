package pl.basistam.turysta.auth;

import pl.basistam.turysta.exceptions.AuthorizationException;

public interface ServerAuthenticate {
    String signIn(final String login, final String password, final String authType) throws AuthorizationException;
    String signUp(final String name, final String email, final String password, final String authType)  throws AuthorizationException;
}
