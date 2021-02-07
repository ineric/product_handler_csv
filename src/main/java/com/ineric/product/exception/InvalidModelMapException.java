package com.ineric.product.exception;

public class InvalidModelMapException extends RuntimeException {
    private static final String EXCEPTION_MESSAGE = "Inhomogeneous data structure. %s";

    private final String sourceData;

    public InvalidModelMapException(String sourceData, String message) {
        super(String.format(EXCEPTION_MESSAGE, message));
        this.sourceData = sourceData;
    }

    public String getSourceData() {
        return this.sourceData;
    }
}
