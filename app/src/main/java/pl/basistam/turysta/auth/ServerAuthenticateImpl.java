package pl.basistam.turysta.auth;

public class ServerAuthenticateImpl implements ServerAuthenticate {

    private static ServerAuthenticate serverAuthenticate;

    private ServerAuthenticateImpl() {}

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
    public String signIn(String login, String password, String authType) {
        return null;
    }

    @Override
    public String signUp(String name, String email, String password, String authType) {
        return null;
    }
}
