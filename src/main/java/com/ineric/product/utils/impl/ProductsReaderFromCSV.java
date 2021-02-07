package com.ineric.product.utils.impl;

import com.ineric.product.model.Product;
import com.ineric.product.utils.common.Constants;
import com.ineric.product.utils.ProductsReader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class ProductsReaderFromCSV implements ProductsReader {

    public static final int HEADER_LINES_COUNT = 1;

    @Override
    public List<Product> getProducts(String fileName) throws IOException {
        try (InputStream inputStream = new FileInputStream(fileName);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            return bufferedReader.lines()
                    .skip(HEADER_LINES_COUNT)
                    .map(this::mapStringToProduct)
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    private Product mapStringToProduct(String productLine) {
        String[] productValues = productLine.split(Constants.CSV_SEPARATOR);
        return new Product(productValues);
    }
}
