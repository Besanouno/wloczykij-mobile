package pl.basistam.turysta.exceptions;


public class AuthorizationException extends Exception{
    private final String message;

    public AuthorizationException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
