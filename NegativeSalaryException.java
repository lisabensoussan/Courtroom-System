package exceptions;

/**
 * Exception thrown when a negative salary is provided
 */
public class NegativeSalaryException extends Exception {
    private static final long serialVersionUID = 1L;

    public NegativeSalaryException(String message) {
        super(message);
    }
}