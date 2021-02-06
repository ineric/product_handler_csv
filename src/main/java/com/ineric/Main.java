package com.ineric;

import com.ineric.product.ProductHandler;

import java.util.logging.Logger;

public class Main {

    private static final String PARAM_SOURCE = "-source";
    private static final String PARAM_OUT = "-out";

    private static Logger LOGGER = Logger.getLogger(Main.class.getName());

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

        Main.initProductHandler(sourceDirectory, outFileName);
    }

    private static void initProductHandler(String sourceDirectory, String outFileName) throws InterruptedException {
        if (sourceDirectory.isEmpty() || outFileName.isEmpty()) {
            LOGGER.warning(String.format("Expected: %s PATH_TO_CSV %s FILE_OUT_RESULT", PARAM_SOURCE, PARAM_OUT));
        } else {
            ProductHandler productHandler = new ProductHandler(sourceDirectory, outFileName);
            productHandler.runHandler();
        }
    }
}
