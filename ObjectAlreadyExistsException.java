package exceptions;

/**
 * Exception thrown when an object already exists in a collection
 */
public class ObjectAlreadyExistsException extends Exception {
    private static final long serialVersionUID = 1L;

    public ObjectAlreadyExistsException(String message) {
        super(message);
    }
}