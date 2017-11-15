package pl.basistam.turysta.exceptions;


public class ServerConnectionException extends Exception{
    private final String message;

    public ServerConnectionException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
