package exceptions;

/**
 * Exception thrown when a negative loss amount is provided for a financial case
 */
public class NegativeNumberOfLossesAmountException extends Exception {
    private static final long serialVersionUID = 1L;

    public NegativeNumberOfLossesAmountException(String message) {
        super(message);
    }
}