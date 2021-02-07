package com.ineric.product.utils;

import com.ineric.product.model.Product;

import java.io.IOException;
import java.util.List;

public interface ProductsWriter {
    void saveProducts(List<Product> products, final String outFileName) throws  IOException;
}
