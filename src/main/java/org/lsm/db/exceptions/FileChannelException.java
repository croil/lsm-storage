package org.lsm.db.exceptions;

public class FileChannelException extends RuntimeException {
    public FileChannelException(String message) {
        super(message);
    }

    public FileChannelException(String message, Throwable cause) {
        super(message, cause);
    }
}
