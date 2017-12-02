package pl.basistam.turysta.errors;


public class ServerConnectionException extends RuntimeException {
    private final String message;

    public ServerConnectionException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
