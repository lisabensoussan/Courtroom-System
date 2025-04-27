package exceptions;

/**
 * Exception thrown when a future date is provided where a past or present date is expected
 */
public class FutureDateException extends Exception {
    private static final long serialVersionUID = 1L;

    public FutureDateException(String message) {
        super(message);
    }
}