package com.phoenixacces.apps.utiles.storages;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration("storageFileExcel")
public class StoragePropertiesExcel {
    /**
     * Folder location for storing files
     */
    @Value(value = "${upload-dir}")
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
