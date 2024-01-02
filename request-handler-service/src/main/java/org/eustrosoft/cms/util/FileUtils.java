/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.cms.util;

import org.apache.commons.io.IOUtils;
import org.eustrosoft.core.constants.DBConstants;

import java.io.*;

public final class FileUtils {
    private final static int BUFFER_SIZE = 8 * 1024;

    public static void createFile(String path, InputStream stream) throws IOException {
        if (path == null || path.isEmpty() || stream == null) {
            throw new IOException("Path does not exist.");
        }
        File outputFile = new File(path);
        if (outputFile.exists()) {
            throw new IOException("File is already exist");
        }
        try (OutputStream outStream = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = stream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
            IOUtils.closeQuietly(stream);
            IOUtils.closeQuietly(outStream);
        }
    }

    public static int getPathLvl(String path) throws Exception {
        if (path == null || path.isEmpty() || path.trim().isEmpty()) {
            throw new Exception("Path was null.");
        }
        String processedPath = path.trim().replace("..", "");
        if (processedPath.equals(DBConstants.SEPARATOR)) {
            return DBConstants.LVL_SCOPE;
        }
        processedPath = processedPath.substring(1);
        int nextSlash = processedPath.indexOf(DBConstants.SEPARATOR);
        if (nextSlash == -1) {
            return DBConstants.LVL_ROOT;
        }
        if (nextSlash > 0) {
            String afterSlash = processedPath.substring(nextSlash + 1);
            if (afterSlash == null || afterSlash.isEmpty()) {
                return DBConstants.LVL_ROOT;
            }
        }
        return DBConstants.LVL_OTHER;
    }

    public static String getFirstLevelFromPath(String path) {
        String firSlashRem = path.substring(1);
        int nextSlash = firSlashRem.indexOf(DBConstants.SEPARATOR);
        if (nextSlash == -1) {
            return firSlashRem;
        } else {
            return firSlashRem.substring(0, nextSlash);
        }
    }

    public static String getLastLevelFromPath(String path) throws Exception {
        int lastSlash = path.lastIndexOf(DBConstants.SEPARATOR);
        if (lastSlash == -1) {
            throw new Exception("Illegal path.");
        } else {
            return path.substring(lastSlash + 1);
        }
    }

    public static String getParentPath(String path) throws Exception {
        int lastSlash = path.lastIndexOf(DBConstants.SEPARATOR);
        if (lastSlash == -1) {
            throw new Exception("Illegal path.");
        } else {
            if (path.endsWith(DBConstants.SEPARATOR)) {
                path = path.substring(0, path.length() - 1);
            }
            return path.substring(0, path.lastIndexOf(DBConstants.SEPARATOR));
        }
    }

    public static String getWhereForLvlAndName(String[] partNames, int lvl) {
        StringBuilder builder = new StringBuilder();
        builder.append(" ");
        for (int i = 0; i < lvl; i++) {
            if (i == 0) {
                builder.append(String.format("XS.name = '%s'", partNames[i]));
            } else if (i == 1) {
                builder.append(String.format("FF.ZSID = XS.id and FF.name = '%s'", partNames[i]));
            } else if (i == 2) {
                builder.append(String.format("FD0.ZOID = FF.ZOID and FD0.fname = '%s'", partNames[i]));
            } else {
                builder.append(String.format("FD%d.ZOID = FD%d.f_id and FD%d.fname ='%s'", i - 2, i - 3, i - 2, partNames[i]));
            }
            if (i != lvl - 1) {
                builder.append(" and ");
            }
        }
        return builder.toString();
    }

    public static String[] getPathParts(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path was null.");
        }
        if (path.indexOf(DBConstants.SEPARATOR) == 0) {
            path = path.substring(1);
        }
        if (path.lastIndexOf(DBConstants.SEPARATOR) == path.length() - 1) {
            path = path.substring(0, path.length() - 1);
        }
        return path.trim().split(DBConstants.SEPARATOR);
    }

    public String getNameFromPath(String path, int level) {
        String firstLevelFromPath = getFirstLevelFromPath(path);
        if (level == 0) {
            return firstLevelFromPath;
        }
        return getNameFromPath(path.substring(firstLevelFromPath.length()), level - 1);
    }

    private FileUtils() {

    }
}
