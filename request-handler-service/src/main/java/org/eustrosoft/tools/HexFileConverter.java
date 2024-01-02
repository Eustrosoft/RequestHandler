/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.tools;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// source: https://mkyong.com/java/how-to-convert-file-to-hex-in-java/
public class HexFileConverter {

    private static final String NEW_LINE = System.lineSeparator();
    private static final String UNKNOWN_CHARACTER = ".";

    public static void main(String[] args) throws IOException {
        String file = "request-handler-service/src/main/resources/cms.properties";
        String s = convertFileToHex(Paths.get(file));
        System.out.println(s);
    }

    public static String convertFileToHex(Path path) throws IOException {

        if (Files.notExists(path)) {
            throw new IllegalArgumentException("File not found! " + path);
        }

        StringBuilder result = new StringBuilder();
        StringBuilder hex = new StringBuilder();
        StringBuilder input = new StringBuilder();

        int count = 0;
        int value;
        try (InputStream inputStream = Files.newInputStream(path)) {
            while ((value = inputStream.read()) != -1) {

                hex.append(String.format("%02X ", value));

                if (!Character.isISOControl(value)) {
                    input.append((char) value);
                } else {
                    input.append(UNKNOWN_CHARACTER);
                }

                if (count == 14) {
                    result.append(String.format("%-60s | %s%n", hex, input));
                    hex.setLength(0);
                    input.setLength(0);
                    count = 0;
                } else {
                    count++;
                }

            }

            if (count > 0) {
                result.append(String.format("%-60s | %s%n", hex, input));
            }
        }
        return result.toString();
    }
}
