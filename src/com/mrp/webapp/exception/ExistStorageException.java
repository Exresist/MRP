package com.mrp.webapp.exception;

public class ExistStorageException  extends StorageException {
    public ExistStorageException(String uuid) {
        super("ERROR! Storage already contain" + uuid + "!", uuid);
    }
}
