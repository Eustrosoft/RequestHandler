package com.eustrosoft.core.context;

import com.eustrosoft.core.tools.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.eustrosoft.core.filter.Constants.PROPERTY_UPLOAD_DIRECTORY;
import static com.eustrosoft.core.filter.Constants.SYSTEM_FILE_NAME;

public class UserStorage implements StorageContext {
    private static final Properties systemProperties = new Properties();
    private String baseUploadPath = "";
    private String currentUserStoragePath = "";

    private User user;


    public UserStorage(User user) throws IOException {
        this.user = user;
        setUploadFilePath();
    }

    @Override
    public synchronized void clearCurrentStorage() {
        String path = getCurrentUserStoragePath();
        if (path != null && !path.isEmpty()) {
            File directoryToClear = new File(path);
            FileUtils.deleteDir(directoryToClear);
        }
    }

    @Override
    public synchronized String getStoragePath() {
        return this.currentUserStoragePath;
    }

    @Override
    public synchronized String createAndGetNewStoragePath() {
        File newStoragePath = new File(baseUploadPath, getUserDirectory());
        newStoragePath.mkdirs();
        setCurrentUserStoragePath(newStoragePath.getAbsolutePath());
        return this.currentUserStoragePath;
    }

    public synchronized String getExistedPathOrCreate() {
        File newStoragePath = new File(baseUploadPath, getUserDirectory());
        if (newStoragePath.exists()) {
            return newStoragePath.getAbsolutePath();
        }
        return createAndGetNewStoragePath();
    }

    private synchronized String getCurrentUserStoragePath() {
        return this.currentUserStoragePath;
    }

    private synchronized void setCurrentUserStoragePath(String path) {
        this.currentUserStoragePath = path;
    }

    private String getUserDirectory() {
        return String.format("%s_%d", user.getUserName(), System.currentTimeMillis());
    }

    private synchronized void setUploadFilePath() throws IOException {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(SYSTEM_FILE_NAME)) {
            systemProperties.load(input);
            this.baseUploadPath = systemProperties.getProperty(PROPERTY_UPLOAD_DIRECTORY);
        }
    }
}
