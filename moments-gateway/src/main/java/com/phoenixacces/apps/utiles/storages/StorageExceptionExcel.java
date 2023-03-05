package com.phoenixacces.apps.utiles.storages;

public class StorageExceptionExcel extends RuntimeException {
    public StorageExceptionExcel(String message) {
        super(message);
    }

    public StorageExceptionExcel(String message, Throwable cause) {
        super(message, cause);
    }
}
