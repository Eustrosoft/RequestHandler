/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.context;

import com.eustrosoft.cms.parameters.HexFileResult;
import com.eustrosoft.core.tools.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.eustrosoft.core.tools.PropertiesConstants.CMS_FILE_NAME;
import static com.eustrosoft.core.tools.PropertiesConstants.PROPERTY_UPLOAD_DIRECTORY;

public class UserStorage implements StorageContext {
    private static final Properties systemProperties = new Properties();
    private static final Map<User, UserStorage> storageMap = new HashMap<>();
    private String baseUploadPath = "";
    private String currentUserStoragePath = "";

    private final User user;
    private final List<String> usedPaths = new ArrayList<>();
    private final Map<String, String> userPaths = new HashMap<>();
    private final Map<String, HexFileResult> hexUserUploads = new HashMap<>();

    private UserStorage(User user) throws IOException {
        this.user = user;
        setUploadFilePath();
    }

    public static UserStorage getInstanceForUser(User user) throws IOException {
        UserStorage userStorage = storageMap.get(user);
        if (userStorage == null) {
            UserStorage newUserStorage = new UserStorage(user);
            storageMap.put(user, newUserStorage);
            return newUserStorage;
        }
        return userStorage;
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
        String newPath = newStoragePath.getAbsolutePath();
        setCurrentUserStoragePath(newPath);
        setUsedPath(newPath);
        return this.currentUserStoragePath;
    }

    public synchronized String getExistedPathOrCreate() {
        String storagePath = getCurrentUserStoragePath();
        if (storagePath == null || storagePath.isEmpty()) {
            File newStoragePath = new File(baseUploadPath, getUserDirectory());
            setCurrentUserStoragePath(newStoragePath.getAbsolutePath());
        }
        File newStoragePath = new File(storagePath);
        if (newStoragePath.exists()) {
            // TODO think about getting path from local variable
            return newStoragePath.getAbsolutePath();
        }
        return createAndGetNewStoragePath();
    }

    public void clearPathOfCurrentStoragePath() {
        this.currentUserStoragePath = "";
    }

    public void clearChunksOfCurrentPath() {
        File dirWithChunks = new File(this.currentUserStoragePath);
        if (dirWithChunks.exists()) {
            int smallestName = Integer.MAX_VALUE;
            File[] files = dirWithChunks.listFiles();
            for (File file : files) {
                if (smallestName >= file.getName().length()) {
                    smallestName = file.getName().length();
                }
            }
            for (File file : files) {
                if (file.getName().length() != smallestName) {
                    file.delete();
                }
            }
        }
    }

    public String getBaseUploadPath() {
        return this.baseUploadPath;
    }

    private synchronized String getCurrentUserStoragePath() {
        return this.currentUserStoragePath;
    }

    private synchronized void setCurrentUserStoragePath(String path) {
        this.currentUserStoragePath = path;
    }

    public synchronized List<String> getUsedPaths() {
        return this.usedPaths;
    }

    private synchronized void setUsedPath(String path) {
        this.usedPaths.add(path);
    }

    public synchronized Map<String, String> getUserPaths() {
        return this.userPaths;
    }

    public synchronized Map<String, HexFileResult> getUserHexUploads() {
        return this.hexUserUploads;
    }

    private String getUserDirectory() {
        return String.format("%s_%d", user.getUserName(), System.currentTimeMillis());
    }

    private synchronized void setUploadFilePath() throws IOException {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CMS_FILE_NAME)) {
            systemProperties.load(input);
            this.baseUploadPath = systemProperties.getProperty(PROPERTY_UPLOAD_DIRECTORY);
        }
    }
}
