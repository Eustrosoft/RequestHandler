/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.sql;

import com.eustrosoft.core.handlers.responses.BasicResponse;
import com.eustrosoft.core.handlers.responses.ResponseLang;
import com.eustrosoft.core.handlers.sql.dto.ResultSetAnswer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLResponseBlock extends BasicResponse {
    private String errMsg = "";
    private Short errCode = 0;

    private List<ResultSet> resultSets;

    public SQLResponseBlock() {
    }

    @Override
    public String getS() {
        return "sql";
    }

    @Override
    public String getR() {
        return "sql";
    }

    @Override
    public String getM() {
        return this.errMsg;
    }

    @Override
    public String getL() {
        return ResponseLang.en_US;
    }

    @Override
    public Short getE() {
        return errCode;
    }

    public void setE(int code) {
        errCode = (short) code;
    }

    public List<ResultSet> getResultSets() {
        return this.resultSets;
    }

    public void setResultSet(List<ResultSet> resultSets) {
        this.resultSets = resultSets;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public JsonObject toJsonObject() throws Exception {
        JsonObject object = super.toJsonObject();
        object.add("r", new Gson().toJsonTree(processResultSets()));
        return object;
    }

    private List<ResultSetAnswer> processResultSets() throws SQLException {
        if (this.resultSets == null || this.resultSets.isEmpty()) {
            return new ArrayList<>();
        }
        List<ResultSet> resSets = this.resultSets;
        List<ResultSetAnswer> sets = new ArrayList<>();

        for (ResultSet set : resSets) {
            ResultSetMetaData resultSetMetaData = set.getMetaData();
            final int columnCount = resultSetMetaData.getColumnCount();

            List<String> columnNames = getColumnNames(resultSetMetaData);
            List<String> columnTypes = getColumnTypes(resultSetMetaData);
            List<List<Object>> allRows = new ArrayList<>(100);

            while (set.next()) {
                List<Object> values = new ArrayList<>(columnCount);
                for (int i = 1; i <= columnCount; i++) {
                    values.add(set.getObject(i));
                }
                allRows.add(values);
            }
            ResultSetAnswer answer = new ResultSetAnswer();
            answer.setColumns(columnNames);
            answer.setData_types(columnTypes);
            answer.setRows(allRows);
            answer.setRows_count(allRows.size());
            sets.add(answer);
            set.close();
        }
        return sets;
    }

    private List<String> getColumnNames(ResultSetMetaData metadata) throws SQLException {
        final int columnCount = metadata.getColumnCount();
        List<String> colNames = new ArrayList<>(columnCount);
        for (int i = 1; i <= columnCount; i++) {
            colNames.add(metadata.getColumnName(i));
        }
        return colNames;
    }

    private List<String> getColumnTypes(ResultSetMetaData metadata) throws SQLException {
        final int columnCount = metadata.getColumnCount();
        List<String> colNames = new ArrayList<>(columnCount);
        for (int i = 1; i <= columnCount; i++) {
            colNames.add(metadata.getColumnTypeName(i));
        }
        return colNames;
    }
}
