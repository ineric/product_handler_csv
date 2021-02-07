package com.ineric.product.exception;

public class InvalidPathToDirectoryException extends RuntimeException {
    private static final String EXCEPTION_MESSAGE = "The specified path is not a directory. Path: %s";

    public InvalidPathToDirectoryException(String pathToDerectory) {
        super(String.format(EXCEPTION_MESSAGE, pathToDerectory));
    }
}
