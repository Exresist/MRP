package com.mrp.webapp.exception;

public class NotExistStorageException extends StorageException {
    public NotExistStorageException(String uuid) {
        super("ERROR! Storage does not contain " + uuid + "!", uuid);
    }
}
