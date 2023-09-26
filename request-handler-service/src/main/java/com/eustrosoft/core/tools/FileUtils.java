/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.tools;

import com.eustrosoft.cms.exception.CMSException;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.util.zip.CRC32;

public final class FileUtils {
    public final static char LEFT_BRACKET = '(';
    public final static char RIGHT_BRACKET = ')';

    public static void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (!Files.isSymbolicLink(f.toPath())) {
                    deleteDir(f);
                }
            }
        }
        file.delete();
    }

    public static byte[] readFileToByteArray(File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException("File not found.");
        }
        if (!file.canRead()) {
            throw new AccessDeniedException("Can not read the file.");
        }
        FileInputStream fis = new FileInputStream(file);
        byte[] arr = new byte[(int) file.length()];
        fis.read(arr);
        fis.close();
        return arr;
    }

    public static void checkPathInjection(String... params) throws CMSException {
        for (String param : params) {
            if (param == null || param.isEmpty()) {
                throw new CMSException("Param was null or empty.");
            }
            if (param.contains("..")) {
                throw new CMSException("Path Injection Detected.");
            }
        }
    }

    public static String hashCrc32(File file) throws IOException {
        InputStream stream = new FileInputStream(file);
        return hashCrc32(stream);
    }

    public static String hashCrc32(InputStream stream) throws IOException {
        byte[] buffer = new byte[1024];
        int readCount = 0;
        CRC32 crc32 = new CRC32();
        while ((readCount = stream.read(buffer)) != -1) {
            crc32.update(buffer, 0, readCount);
        }
        if (stream != null) {
            stream.close();
        }
        return String.format("%x", crc32.getValue());
    }

    public static String getNextIterationFilePath(String dirPath, String fileName) {
        File targetDir = new File(dirPath);
        File file = new File(targetDir, fileName);
        if (!file.exists()) {
            return file.getAbsolutePath();
        }
        String name = FilenameUtils.removeExtension(fileName);
        int left = name.lastIndexOf(LEFT_BRACKET);
        int right = name.lastIndexOf(RIGHT_BRACKET);

        if (left != -1 && right == (name.length() - 1)) {
            int number = Integer.parseInt(
                    name.substring(left + 1, right)
            );
            String finalName = getNumberFileName(fileName.substring(0, left - 1), left, ++number) + FilenameUtils.getExtension(fileName);
            if (new File(targetDir, finalName).exists()) {
                return getNextIterationFilePath(dirPath, finalName);
            }
            return new File(targetDir, finalName).getAbsolutePath();
        } else {
            File numFile = new File(
                    targetDir,
                    getNumberFileName(fileName, fileName.length() + 1, 1)
            );
            if (numFile.exists()) {
                return getNextIterationFilePath(targetDir.getAbsolutePath(), numFile.getName());
            }
            return numFile.getAbsolutePath();
        }
    }

    private static String getNumberFileName(String fileName, int leftBound, int number) {
        StringBuilder builder = new StringBuilder();
        builder.append(FilenameUtils.removeExtension(fileName));
        builder.append(" ");
        builder.append(LEFT_BRACKET);
        builder.append(number);
        builder.append(RIGHT_BRACKET);
        builder.append(".");
        builder.append(FilenameUtils.getExtension(fileName));
        return builder.toString();
    }
}
