/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.sql.dto;

import java.util.List;

public class ResultSetAnswer {
    private List<String> columns;
    private List<String> data_types;
    private List<List<Object>> rows;
    private Integer rows_count;

    public ResultSetAnswer() {
    }

    public ResultSetAnswer(List<String> columns, List<String> data_types, List<List<Object>> rows, Integer rows_count) {
        this.columns = columns;
        this.data_types = data_types;
        this.rows = rows;
        this.rows_count = rows_count;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<String> getData_types() {
        return data_types;
    }

    public void setData_types(List<String> data_types) {
        this.data_types = data_types;
    }

    public List<List<Object>> getRows() {
        return rows;
    }

    public void setRows(List<List<Object>> rows) {
        this.rows = rows;
    }

    public Integer getRows_count() {
        return rows_count;
    }

    public void setRows_count(Integer rows_count) {
        this.rows_count = rows_count;
    }
}
