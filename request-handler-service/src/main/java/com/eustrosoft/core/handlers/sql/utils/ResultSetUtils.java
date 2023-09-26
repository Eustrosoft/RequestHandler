/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.sql.utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public class ResultSetUtils {

    public String resSetToString(ResultSet rs) {
        try {
            List<String[]> allRows = new ArrayList();
            while (rs.next()) {
                ResultSetMetaData metaData = rs.getMetaData();
                String[] currentRow = new String[metaData.getColumnCount()];
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    currentRow[i - 1] = rs.getString(i);
                }
                allRows.add(currentRow);
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < allRows.size(); i++) {
                builder.append(convertStringMassiveToString(allRows.get(i)) + "\n");
            }
            return builder.toString();
        } catch (Exception ex) {

        }
        return "";
    }

    private String convertStringMassiveToString(String[] mass) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < mass.length; i++) {
            builder.append(mass[i] + " ");
        }
        return builder.toString();
    }
}
