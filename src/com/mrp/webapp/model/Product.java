package com.mrp.webapp.model;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class Product {
    private static final long serialVersionUID = 1L;

    private String uuid;
    private String fullName;

    private final HashMap<String, Integer> materials = new HashMap<>();

    public String getUuid() {return uuid;}

    public String getFullName() {
        return fullName;
    }
    
    public HashMap getMaterials () { return  materials;}

    public Product() {

    }

    public Product(String fullName) {
        this(UUID.randomUUID().toString(), fullName);
    }

    public Product(String uuid, String fullName) {
        Objects.requireNonNull(uuid, "uuid must not be null!");
        Objects.requireNonNull(fullName, "full name must not be null!");
        this.uuid = uuid;
        this.fullName = fullName;
    }


    public void addMaterial(String name, int quantity) {
        materials.put(name, quantity);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product resume = (Product) o;
        return uuid.equals(resume.uuid) &&
                fullName.equals(resume.fullName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, fullName);
    }

    @Override
    public String toString() {
        return String.join(" ", uuid, fullName);
    }

}
