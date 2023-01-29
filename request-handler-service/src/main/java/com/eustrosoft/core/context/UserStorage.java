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
    public void clearCurrentStorage() {
        String path = getCurrentUserStoragePath();
        if (path != null && !path.isEmpty()) {
            File directoryToClear = new File(path);
            FileUtils.deleteDir(directoryToClear);
        }
    }

    @Override
    public String getStoragePath() {
        return this.currentUserStoragePath;
    }

    @Override
    public String createAndGetNewStoragePath() {
        File newStoragePath = new File(baseUploadPath, getUserDirectory());
        setCurrentUserStoragePath(newStoragePath.getAbsolutePath());
        return this.currentUserStoragePath;
    }

    private String getCurrentUserStoragePath() {
        return this.currentUserStoragePath;
    }

    private void setCurrentUserStoragePath(String path) {
        this.currentUserStoragePath = path;
    }

    private String getUserDirectory() {
        return String.format("%s_%d", user.getUserName(), System.currentTimeMillis());
    }

    private void setUploadFilePath() throws IOException {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(SYSTEM_FILE_NAME)) {
            systemProperties.load(input);
            this.baseUploadPath = systemProperties.getProperty(PROPERTY_UPLOAD_DIRECTORY);
        }
    }
}
