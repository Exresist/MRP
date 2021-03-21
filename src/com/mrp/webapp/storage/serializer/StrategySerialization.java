package com.mrp.webapp.storage.serializer;

import com.mrp.webapp.model.Product;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface StrategySerialization {
    void doWrite(Product r, OutputStream os) throws IOException;
    Product doRead(InputStream is) throws IOException;
}
