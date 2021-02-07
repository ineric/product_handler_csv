package com.ineric.product.utils;

import com.ineric.product.model.Product;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface ProductsReader {
    List<Product> getProducts(final String fileName) throws IOException;
}
