package com.ineric.product.exception;

public class InvalidColumnsCountException extends RuntimeException {

    private static final String EXCEPTION_MESSAGE = "Error columns count. Actual: %s | Excepted: %s ";

    public InvalidColumnsCountException(Integer actualColumnsCount, Integer exceptedColunmsCount) {
        super(String.format(EXCEPTION_MESSAGE, actualColumnsCount, exceptedColunmsCount));
    }
}
