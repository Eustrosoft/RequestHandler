package com.eustrosoft.core.context;

public interface StorageContext {
    void clearCurrentStorage();

    String getStoragePath();

    String createAndGetNewStoragePath();
}
