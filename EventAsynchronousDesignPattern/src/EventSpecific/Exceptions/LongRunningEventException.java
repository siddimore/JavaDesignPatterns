package EventSpecific.Exceptions;

public class LongRunningEventException extends Exception {

    private static final long serialVersionUID = -483423544320148809L;

    public LongRunningEventException(String message) {
        super(message);
    }
}