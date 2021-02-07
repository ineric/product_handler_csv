package com.ineric;

import com.ineric.product.ProductHandler;
import com.ineric.product.exception.InvalidPathToDirectoryException;
import com.ineric.product.exception.MissingIncomingParametersException;
import com.ineric.product.exception.NoFilesToProcessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.InvalidPathException;

public class Main {

    private static final String PARAM_SOURCE = "-source";
    private static final String PARAM_OUT = "-out";

    private static Logger LOGGER = LoggerFactory.getLogger(Main.class.getName());

    public static void main(String[] args) throws InterruptedException {

        readParamsAndRun(args);
    }

    private static void readParamsAndRun(String[] args) throws InterruptedException {
        String sourceDirectory = "";
        String outFileName = "";

        for (int i = 0; i < args.length; i += 2) {
            if (args[i].equals(PARAM_SOURCE))
                sourceDirectory = args[i + 1];
            if (args[i].equals(PARAM_OUT))
                outFileName = args[i + 1];
        }

        try {
            initProductHandler(sourceDirectory, outFileName);
        } catch (MissingIncomingParametersException exception) {
            LOGGER.error("Expected: {} ", exception.getMessage());
        }
    }

    private static void initProductHandler(String sourceDirectory, String outFileName) throws InterruptedException {
        if (sourceDirectory.isEmpty() || outFileName.isEmpty()) {
            throw new MissingIncomingParametersException(PARAM_SOURCE, PARAM_OUT);
        } else {
            try {
                ProductHandler productHandler = new ProductHandler(sourceDirectory, outFileName);
                productHandler.runHandler();
            } catch (NoFilesToProcessException | InvalidPathException | InvalidPathToDirectoryException exception) {
                LOGGER.error(exception.getMessage());
            }
        }
    }
}
