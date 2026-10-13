package gr.cite.rda.common.exception;

public class InvalidQueryStringException extends RuntimeException {

    public InvalidQueryStringException(String message) {
        super(message);
    }

    public InvalidQueryStringException(String message, Throwable cause) {
        super(message, cause);
    }
}
