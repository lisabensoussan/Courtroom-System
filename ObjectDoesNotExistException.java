package exceptions;

/**
 * Exception thrown when trying to access an object that doesn't exist
 */
public class ObjectDoesNotExistException extends Exception {
    private static final long serialVersionUID = 1L;

    public ObjectDoesNotExistException(String message) {
        super(message);
    }
}