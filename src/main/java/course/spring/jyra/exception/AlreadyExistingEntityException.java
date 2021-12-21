package course.spring.jyra.exception;

public class AlreadyExistingEntityException extends RuntimeException {
    public AlreadyExistingEntityException() {
    }

    public AlreadyExistingEntityException(String message) {
        super(message);
    }

    public AlreadyExistingEntityException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyExistingEntityException(Throwable cause) {
        super(cause);
    }
}
