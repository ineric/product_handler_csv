package com.ineric.product.utils.impl;

import com.ineric.product.model.Product;
import com.ineric.product.utils.common.Constants;
import com.ineric.product.utils.ProductsReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class ProductsReaderFromCSV implements ProductsReader {

    @Override
    public List<Product> getProducts(String fileName) throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(new File(fileName));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        return bufferedReader.lines()
                .skip(1)
                .map(this::mapStringToItem)
                .collect(Collectors.toList());
    }

    private Product mapStringToItem(String productLine) {
        String[] productValues = productLine.split(Constants.CSV_SEPARATOR);
        return new Product(Integer.valueOf(productValues[0]), productValues[1], productValues[2], productValues[3], Double.valueOf(productValues[4]));
    }
}
