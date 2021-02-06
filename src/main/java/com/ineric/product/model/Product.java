package com.ineric.product.model;

import com.ineric.product.utils.common.Constants;

public class Product implements Comparable<Product> {

    private Integer id;
    private String name;
    private String condition;
    private String state;
    private Double price;

    public Product(Integer id, String name, String condition, String state, Double price) {
        this.id = id;
        this.name = name;
        this.condition = condition;
        this.state = state;
        this.price = price;
    }

    public Product() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return id + Constants.CSV_SEPARATOR
                + name + Constants.CSV_SEPARATOR
                + condition + Constants.CSV_SEPARATOR
                + state + Constants.CSV_SEPARATOR
                + price;
    }


    @Override
    public int compareTo(Product o) {
        return this.price.compareTo(o.price);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;

        Product product = (Product) o;

        if (getId() != null ? !getId().equals(product.getId()) : product.getId() != null) return false;
        if (getName() != null ? !getName().equals(product.getName()) : product.getName() != null) return false;
        if (getCondition() != null ? !getCondition().equals(product.getCondition()) : product.getCondition() != null)
            return false;
        if (getState() != null ? !getState().equals(product.getState()) : product.getState() != null) return false;
        return getPrice() != null ? getPrice().equals(product.getPrice()) : product.getPrice() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getCondition() != null ? getCondition().hashCode() : 0);
        result = 31 * result + (getState() != null ? getState().hashCode() : 0);
        result = 31 * result + (getPrice() != null ? getPrice().hashCode() : 0);
        return result;
    }
}
