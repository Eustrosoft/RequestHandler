package com.eustrosoft.filedatasource.util;

import org.apache.commons.io.IOUtils;

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

    private FileUtils() {

    }
}
