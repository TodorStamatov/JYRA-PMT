package course.spring.jyra.exception;

public class ExistingEntityException extends RuntimeException {
    public ExistingEntityException() {
    }

    public ExistingEntityException(String message) {
        super(message);
    }

    public ExistingEntityException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExistingEntityException(Throwable cause) {
        super(cause);
    }
}
