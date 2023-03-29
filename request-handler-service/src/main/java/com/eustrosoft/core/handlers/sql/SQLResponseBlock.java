package com.eustrosoft.core.handlers.sql;

import com.eustrosoft.core.handlers.responses.ResponseBlock;
import com.eustrosoft.core.handlers.sql.model.ResultSetAnswer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.eustrosoft.core.handlers.responses.ResponseLang.en_EN;

public class SQLResponseBlock implements ResponseBlock {
    private String errMsg = "";
    private Short errCode = 0;

    private List<ResultSet> resultSets;
    private Long status = 200L;

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

    public void setStatus(Long status) {
        this.status = status;
    }

    @Override
    public String getM() {
        return this.errMsg;
    }

    @Override
    public String getL() {
        return en_EN;
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
    public String toJson() throws SQLException {
        JsonObject object = new JsonObject();
        object.addProperty("s", getS());
        object.addProperty("e", getE());
        object.addProperty("m", this.getM());
        object.add("r", new Gson().toJsonTree(processResultSets()));
        return new Gson().toJson(object);
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
            List<List<Object>> allRows = new ArrayList<>();

            while (set.next()) {
                List<Object> values = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    values.add(set.getObject(i));
                    set.getMetaData().getColumnType(i);
                }
                allRows.add(values);
            }
            ResultSetAnswer answer = new ResultSetAnswer();
            answer.setColumns(columnNames);
            answer.setData_types(columnTypes);
            answer.setRows(allRows);
            answer.setRows_count(allRows.size());
            sets.add(answer);
        }
        return sets;
    }

    private List<String> getColumnNames(ResultSetMetaData metadata) throws SQLException {
        final int columnCount = metadata.getColumnCount();
        List<String> colNames = new ArrayList<>();
        for (int i = 1; i <= columnCount; i++) {
            colNames.add(metadata.getColumnName(i));
        }
        return colNames;
    }

    private List<String> getColumnTypes(ResultSetMetaData metadata) throws SQLException {
        final int columnCount = metadata.getColumnCount();
        List<String> colNames = new ArrayList<>();
        for (int i = 1; i <= columnCount; i++) {
            colNames.add(metadata.getColumnTypeName(i));
        }
        return colNames;
    }
}
