package EventSpecific.Exceptions;

public class MaxNumOfEventsAllowedException extends Exception {

    private static final long serialVersionUID = -8430876973516292695L;

    public MaxNumOfEventsAllowedException(String message) {
        super(message);
    }
}