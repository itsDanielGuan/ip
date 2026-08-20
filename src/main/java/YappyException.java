/**
 * Represents an error caused by invalid user input in the Yappy chatbot.
 */
public class YappyException extends Exception {
    /**
     * Creates a Yappy-specific exception with a user-friendly message.
     */
    public YappyException(String message) {
        super(message);
    }
}
