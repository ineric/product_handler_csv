package com.ineric.product.exception;

public class NoFilesToProcessException extends RuntimeException {

    private static final String EXCEPTION_MESSAGE = "Not found files to process in %s";

    public NoFilesToProcessException(String pathToDirectory) {
        super(String.format(EXCEPTION_MESSAGE, pathToDirectory));
    }
}
