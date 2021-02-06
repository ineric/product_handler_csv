package com.ineric.product;

import com.ineric.product.model.Product;
import com.ineric.product.utils.ProductsReader;
import com.ineric.product.utils.common.Constants;
import com.ineric.product.utils.impl.ProductsReaderFromCSV;
import com.ineric.product.utils.impl.ProductsWriterToCSV;
import com.sun.istack.internal.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ProductHandler {

    private static final int MAX_SIZE_SAME_PRODUCT = 20;
    private static final int MAX_SIZE_PRODUCTS = 1000;

    private ExecutorService executor;
    private CountDownLatch latch;

    private final String outFileName;
    private List<String> fileNames;
    private Map<Integer, List<Product>> allProducts = new HashMap<>();


    private static Logger LOGGER = Logger.getLogger(ProductHandler.class.getName());

    public ProductHandler(@NotNull String sourceDirectory, @NotNull String outFileName) {
        this.outFileName = outFileName;

        try {
            File productFilesDirectory = new File(sourceDirectory);
            if (validateDirectory(productFilesDirectory)) {
                fileNames = Arrays.stream(Objects.requireNonNull(productFilesDirectory.
                        list((dir, name) -> name.matches(Constants.FILE_FILTER_REGEX))))
                        .map(fileName -> sourceDirectory + File.separator + fileName)
                        .collect(Collectors.toList());

                if (fileNames.isEmpty()) {
                    LOGGER.log(Level.WARNING, String.format("Not found product files to process in %s", sourceDirectory));
                } else {
                    LOGGER.log(Level.INFO, String.format("Total files to work: %s", fileNames.size()));
                }
            }
        } catch (RuntimeException exception) {
            LOGGER.log(Level.SEVERE, "Unexpected error, contact the developer.", exception);
        }
    }

    public void runHandler() throws InterruptedException {
        if (canStartHandler()) {
            LOGGER.log(Level.INFO, "Start product handler!");

            initExecutorService();
            launchingProductReadThreads();

            latch.await();
            executor.shutdown();

            List<Product> resultProducts = prepareProducts();
            saveResultProducts(resultProducts);
        }
    }

    private boolean canStartHandler() {
        return !(fileNames == null) && !fileNames.isEmpty();
    }

    private boolean validateDirectory(File productDirectory) {
        boolean resultValid = true;

        if (!productDirectory.exists()) {
            LOGGER.log(Level.WARNING, String.format("Directory not found. %s", productDirectory.getAbsolutePath()));
            resultValid = false;
        } else if (!productDirectory.isDirectory()) {
            LOGGER.log(Level.WARNING, String.format("%s - This is not a directory.", productDirectory.getAbsolutePath()));
            resultValid = false;
        } else {
            LOGGER.log(Level.INFO, String.format("%s - Directory is valid.", productDirectory.getAbsolutePath()));
        }

        return resultValid;
    }

    private void initExecutorService() {
        int cpuCoreCount = Runtime.getRuntime().availableProcessors();
        LOGGER.log(Level.INFO, String.format("Available processor cores: %s", cpuCoreCount));

        executor = Executors.newFixedThreadPool(cpuCoreCount);
        latch = new CountDownLatch(fileNames.size());
    }

    private void launchingProductReadThreads() {
        fileNames.forEach(fileName ->
                CompletableFuture.supplyAsync(() -> readProducts(fileName), executor)
                        .thenAcceptAsync(this::addProductsToResult)
                        .thenAccept(aVoid -> latch.countDown())
        );
    }

    private List<Product> readProducts(String fileName) {
        LOGGER.log(Level.INFO, String.format("Read products from %s", fileName));

        ProductsReader productsReader = new ProductsReaderFromCSV();
        List<Product> products = new ArrayList<>();

        try {
            products = productsReader.getProducts(fileName);
        } catch (FileNotFoundException exception) {
            LOGGER.log(Level.SEVERE, String.format("File %s read error. May not be found", fileName), exception);
        } catch (NumberFormatException exception) {
            LOGGER.log(Level.SEVERE, String.format("File %s read error. Inhomogeneous data structure.", fileName), exception);
        }

        LOGGER.log(Level.INFO, String.format("Finish read products from %s | Total count: %s", fileName, products.size()));

        return products;
    }

    private void saveResultProducts(List<Product> products) {
        try {
            LOGGER.log(Level.INFO, String.format("Save result products to file %s ", outFileName));
            new ProductsWriterToCSV().saveProducts(products, outFileName);
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "An error occurred while saving product results to a file.", exception);
        }
    }

    private synchronized void addProductsToResult(List<Product> products) {
        if (products == null) {
            LOGGER.log(Level.WARNING, "An attempt was made to add null products to the results list.");
        } else {
            this.allProducts.putAll(products
                    .stream()
                    .collect(Collectors.groupingBy(Product::getId)));
        }
    }

    private List<Product> prepareProducts() {
        LOGGER.log(Level.INFO, "Start prepare products !");

        return allProducts.values()
                .stream()
                .map(products -> products.stream().sorted().limit(MAX_SIZE_SAME_PRODUCT).collect(Collectors.toList()))
                .flatMap(List::stream)
                .sorted()
                .limit(MAX_SIZE_PRODUCTS)
                .collect(Collectors.toList());
    }

}
