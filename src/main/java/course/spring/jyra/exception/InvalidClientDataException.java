package course.spring.jyra.exception;

public class InvalidClientDataException extends RuntimeException {
    public InvalidClientDataException() {
    }

    public InvalidClientDataException(String message) {
        super(message);
    }

    public InvalidClientDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidClientDataException(Throwable cause) {
        super(cause);
    }
}
