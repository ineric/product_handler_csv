package com.ineric.product;

import com.ineric.product.exception.InvalidColumnsCountException;
import com.ineric.product.exception.InvalidModelMapException;
import com.ineric.product.exception.InvalidPathToDirectoryException;
import com.ineric.product.exception.NoFilesToProcessException;
import com.ineric.product.model.Product;
import com.ineric.product.utils.ProductsReader;
import com.ineric.product.utils.common.Constants;
import com.ineric.product.utils.impl.ProductsReaderFromCSV;
import com.ineric.product.utils.impl.ProductsWriterToCSV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.InvalidPathException;
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
import java.util.stream.Collectors;

public class ProductHandler {

    private static final int MAX_NUMBER_SAME_PRODUCT = 20;
    private static final int MAX_SIZE_PRODUCTS = 1000;
    private static Logger LOGGER = LoggerFactory.getLogger(ProductHandler.class.getName());

    private ExecutorService executorService;
    private CountDownLatch countDownLatch;

    private final String outFileName;
    private List<String> fileNames;
    private Map<Integer, List<Product>> allProducts = new HashMap<>();

    public ProductHandler(String sourceDirectory, String outFileName) {
        this.outFileName = outFileName;
        initFileNamesToProcess(sourceDirectory);
    }

    public void runHandler() throws InterruptedException {
        LOGGER.info("Start product handler!");

        initExecutorService();
        launchingProductReadThreads();
        waitingEndProcessing();
        prepareAndSaveProducts();
    }

    private void waitingEndProcessing() throws InterruptedException {
        countDownLatch.await();
        executorService.shutdown();
    }

    private void prepareAndSaveProducts() {
        List<Product> resultProducts = prepareProducts();
        saveResultProducts(resultProducts);
    }

    private synchronized void addProductsToResult(List<Product> products) {
        if (products == null) {
            LOGGER.warn("An attempt was made to add null products to the results list.");
        } else {
            this.allProducts.putAll(products
                    .stream()
                    .collect(Collectors.groupingBy(Product::getId)));
        }
    }


    private void initFileNamesToProcess(String sourceDirectory) {
        File productFilesDirectory = new File(sourceDirectory);
        if (validateDirectory(productFilesDirectory)) {
            fileNames = Arrays.stream(Objects.requireNonNull(productFilesDirectory.
                    list((dir, name) -> name.matches(Constants.FILE_FILTER_REGEX))))
                    .map(fileName -> sourceDirectory + File.separator + fileName)
                    .collect(Collectors.toList());

            if (fileNames.isEmpty()) {
                throw new NoFilesToProcessException(sourceDirectory);
            }
        }
    }

    private List<Product> prepareProducts() {
        LOGGER.info("Start prepare products !");

        return allProducts.values()
                .stream()
                .map(products -> products.stream().sorted().limit(MAX_NUMBER_SAME_PRODUCT).collect(Collectors.toList()))
                .flatMap(List::stream)
                .sorted()
                .limit(MAX_SIZE_PRODUCTS)
                .collect(Collectors.toList());
    }

    private boolean validateDirectory(File productDirectory) {
        if (!productDirectory.exists()) {
            throw new InvalidPathException(productDirectory.getAbsolutePath(), "Directory not found");
        } else if (!productDirectory.isDirectory()) {
            throw new InvalidPathToDirectoryException(productDirectory.getAbsolutePath());
        }

        return true;
    }

    private void initExecutorService() {
        int cpuCoreCount = Runtime.getRuntime().availableProcessors();
        LOGGER.info("Available processor cores: {}", cpuCoreCount);

        executorService = Executors.newFixedThreadPool(cpuCoreCount);
        countDownLatch = new CountDownLatch(fileNames.size());
    }

    private void launchingProductReadThreads() {
        fileNames.forEach(fileName ->
                CompletableFuture.supplyAsync(() -> readProducts(fileName), executorService)
                        .thenAcceptAsync(this::addProductsToResult)
                        .thenAccept(aVoid -> countDownLatch.countDown())
        );
    }

    private List<Product> readProducts(String fileName) {
        LOGGER.info("Read products from {}", fileName);

        ProductsReader productsReader = new ProductsReaderFromCSV();
        List<Product> products = getProductsFromReader(fileName, productsReader);

        LOGGER.info("Finish read products from {} | Total count: {}}", fileName, products.size());

        return products;
    }

    private List<Product> getProductsFromReader(String fileName, ProductsReader productsReader) {
        List<Product> products = new ArrayList<>();

        try {
            products = productsReader.getProducts(fileName);
            products.forEach(System.out::println);
        } catch (FileNotFoundException exception) {
            LOGGER.error("File {} read error. May not be found", fileName, exception);
        } catch (IOException exception) {
            LOGGER.error("File {} read error.", fileName, exception);
        } catch (InvalidModelMapException | InvalidColumnsCountException exception) {
            LOGGER.error(exception.getMessage());
        }

        return products;
    }

    private void saveResultProducts(List<Product> products) {
        try {
            LOGGER.info("Save result products to file {} ", outFileName);
            new ProductsWriterToCSV().saveProducts(products, outFileName);
        } catch (IOException exception) {
            LOGGER.error("An error occurred while saving product results to a file.", exception);
        }
    }
}
