package dragonjetz.blocklimiter.exceptions;

public class FailedFileCreationException extends Exception {
    public FailedFileCreationException() {
        super("Failed to create file");
    }

    public FailedFileCreationException(String message) {
        super(message);
    }

    public FailedFileCreationException(Throwable cause) {
        super(cause);
    }

    public FailedFileCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
