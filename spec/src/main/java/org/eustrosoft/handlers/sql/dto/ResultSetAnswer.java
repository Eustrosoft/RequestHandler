/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.handlers.sql.dto;

import org.eustrosoft.json.JsonUtil;
import org.eustrosoft.json.exception.JsonException;
import org.eustrosoft.spec.experimental.JsonParser;
import org.eustrosoft.spec.interfaces.JsonConvertible;

import java.util.List;

public class ResultSetAnswer implements JsonConvertible {
    private List<String> columns;
    private List<String> dataTypes;
    private List<List<Object>> rows;
    private Integer rowsCount;

    public ResultSetAnswer() {
    }

    public ResultSetAnswer(List<String> columns, List<String> data_types, List<List<Object>> rows, Integer rows_count) {
        this.columns = columns;
        this.dataTypes = data_types;
        this.rows = rows;
        this.rowsCount = rows_count;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<String> getDataTypes() {
        return dataTypes;
    }

    public void setDataTypes(List<String> dataTypes) {
        this.dataTypes = dataTypes;
    }

    public List<List<Object>> getRows() {
        return rows;
    }

    public void setRows(List<List<Object>> rows) {
        this.rows = rows;
    }

    public Integer getRowsCount() {
        return rowsCount;
    }

    public void setRowsCount(Integer rowsCount) {
        this.rowsCount = rowsCount;
    }

    @Override
    public String convertToString() throws JsonException {
        String s = null;
        try {
            s = new JsonParser().parseCollection(rows);
        } catch (Exception exception) {

        }
        return JsonUtil.toJsonFormatted(
                JsonUtil.AsEntry.getStringCollection("columns", columns),
                JsonUtil.AsEntry.getStringCollection("dataTypes", dataTypes),
                JsonUtil.AsEntry.getRawParams("rows", s),
                JsonUtil.AsEntry.getNumberParams("rowsCount", rowsCount)
        );
    }
}
