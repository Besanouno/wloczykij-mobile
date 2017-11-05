package pl.basistam.turysta.auth;

public interface ServerAuthenticate {
    String signIn(final String login, final String password, final String authType);
    String signUp(final String name, final String email, final String password, final String authType);
}
