package gr.cite.rda.common.exception;

public class DmpStaleException extends RuntimeException {

    public DmpStaleException(String message) {
        super(message);
    }

    public DmpStaleException(String message, Throwable cause) {
        super(message, cause);
    }
}
