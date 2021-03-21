package com.mrp.webapp.storage;

import com.mrp.webapp.model.Product;

import java.util.List;
public interface Storage {
    void clear();

    void update(Product Product);

    void save(Product Product);

    Product get(String uuid);

    void delete(String uuid);

    /**
     * @return array, contains only Products in storage (without null)
     */
    List<Product> getAllSorted();

    int size();
}
