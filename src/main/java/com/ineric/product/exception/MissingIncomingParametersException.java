package com.ineric.product.exception;

import java.util.Arrays;

public class MissingIncomingParametersException extends RuntimeException {

    public MissingIncomingParametersException(String... params) {
        super(Arrays.toString(params));
    }
}
