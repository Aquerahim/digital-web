package com.phoenixacces.apps.utiles.encryption;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.hibernate5.encryptor.HibernatePBEEncryptorRegistry;
import org.jasypt.iv.RandomIvGenerator;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EncryptionConfig {

    public EncryptionConfig() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setPoolSize(4);
        encryptor.setPassword("MY-PASSPASSPASS123");
        encryptor.setAlgorithm("PBEWithMD5AndTripleDES");
        encryptor.setIvGenerator(new RandomIvGenerator());
        encryptor.setKeyObtentionIterations(1500);

        HibernatePBEEncryptorRegistry registry = HibernatePBEEncryptorRegistry.getInstance();
        registry.registerPBEStringEncryptor("myStringEncryptor", encryptor);
    }
}
