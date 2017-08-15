package EventSpecific.Exceptions;

public class EventDoesNotExistException extends Exception {

    private static final long serialVersionUID = -3398463738273811509L;

    public EventDoesNotExistException(String message) {
        super(message);
    }
}