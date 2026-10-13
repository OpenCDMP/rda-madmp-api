package gr.cite.rda.common.exception;

public class ConflictException extends RuntimeException{

    private String message;

    public ConflictException(String message) {
        this.message = message;
    }

    public ConflictException(String message, String message1) {
        super(message);
        this.message = message1;
    }

    public ConflictException(String message, Throwable cause, String message1) {
        super(message, cause);
        this.message = message1;
    }

    public ConflictException(Throwable cause, String message) {
        super(cause);
        this.message = message;
    }

    public ConflictException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String message1) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.message = message1;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
