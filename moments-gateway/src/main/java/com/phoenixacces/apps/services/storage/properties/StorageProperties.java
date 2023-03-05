package com.phoenixacces.apps.services.storage.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.File;

@Configuration("storage")
public class StorageProperties {
    /**
     * Folder location for storing files
     */
    @Value(value = "${upload.dir}")
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
