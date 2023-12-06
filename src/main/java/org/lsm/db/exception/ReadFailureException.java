package org.lsm.db.exception;

public class ReadFailureException extends RuntimeException {
    public ReadFailureException(String message) {
        super(message);
    }

    public ReadFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
