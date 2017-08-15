package EventSpecific.Exceptions;

public class InvalidOperationException extends Exception {

    private static final long serialVersionUID = -6191545255213410803L;

    public InvalidOperationException(String message) {
        super(message);
    }

}