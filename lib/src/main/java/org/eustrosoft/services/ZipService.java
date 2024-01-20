/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.services;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipService {
    public static Path createZipOfDirectory(String nameZipArchive, String directory, String zipDirectory) {
        Path zipPath = Paths.get(zipDirectory).resolve(nameZipArchive);
        try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(zipPath)))) {
            Files.walk(Paths.get(directory)).forEach(path -> {
                try {
                    Path reliativePath = Paths.get(directory).relativize(path);
                    if (!path.equals(zipPath)) {
                        File file = path.toFile();
                        if (file.isDirectory()) {
                            File[] files = file.listFiles();
                            if (files == null || files.length == 0) {
                                zos.putNextEntry(new ZipEntry(reliativePath + "" + File.separator));
                                zos.closeEntry();
                            }
                        } else {
                            zos.putNextEntry(new ZipEntry(reliativePath.toString()));
                            zos.write(Files.readAllBytes(path));
                            zos.closeEntry();
                        }
                    }
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            });
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return zipPath;
    }
}
