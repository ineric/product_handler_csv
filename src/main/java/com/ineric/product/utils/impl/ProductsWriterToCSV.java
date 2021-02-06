package com.ineric.product.utils.impl;

import com.ineric.product.model.Product;
import com.ineric.product.utils.common.Constants;
import com.ineric.product.utils.ProductsWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class ProductsWriterToCSV implements ProductsWriter {

    @Override
    public void saveProducts(List<Product> products, String outFileName) throws IOException {
        OutputStream outputStream = new FileOutputStream(new File(outFileName));
        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            bufferedWriter.write(Constants.CSV_HEADER);
            bufferedWriter.newLine();
            products.forEach(product -> {
                try {
                    bufferedWriter.write(product.toString());
                    bufferedWriter.newLine();
                } catch (IOException ignored) {

                }
            });
        }
    }
}