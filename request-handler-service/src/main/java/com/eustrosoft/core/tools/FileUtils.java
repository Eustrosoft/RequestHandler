package com.eustrosoft.core.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;

public final class FileUtils {
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
}
