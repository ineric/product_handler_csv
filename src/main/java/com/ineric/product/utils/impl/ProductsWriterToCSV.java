package com.ineric.product.utils.impl;

import com.ineric.product.model.Product;
import com.ineric.product.utils.common.Constants;
import com.ineric.product.utils.ProductsWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class ProductsWriterToCSV implements ProductsWriter {

    private static Logger LOGGER = LoggerFactory.getLogger(ProductsWriterToCSV.class.getName());

    @Override
    public void saveProducts(List<Product> products, String outFileName) throws IOException {
        try (OutputStream outputStream = new FileOutputStream(new File(outFileName));
             BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            writeProductsToFile(products, bufferedWriter);
        }
    }

    private void writeProductsToFile(List<Product> products, BufferedWriter bufferedWriter) throws IOException {
        bufferedWriter.write(Constants.CSV_HEADER);
        bufferedWriter.newLine();
        products.forEach(product -> {
            try {
                bufferedWriter.write(product.toString());
                bufferedWriter.newLine();
            } catch (IOException exception) {
                LOGGER.error("Error save products: ", exception);
            }
        });
    }
}
