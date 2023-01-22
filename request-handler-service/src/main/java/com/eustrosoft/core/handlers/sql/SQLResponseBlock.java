package com.eustrosoft.core.handlers.sql;

import com.eustrosoft.core.handlers.responses.ResponseBlock;
import com.eustrosoft.core.handlers.sql.model.ResultSetAnswer;
import com.eustrosoft.core.tools.QJson;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SQLResponseBlock implements ResponseBlock {
    private String errMsg = "";
    private Short errCode = 0;

    private List<ResultSet> resultSets;
    private Long status = 200L;

    public SQLResponseBlock() {
    }

    @Override
    public String getSubsystem() {
        return "sql";
    }

    @Override
    public String getRequest() {
        return "sql";
    }

    @Override
    public Long getStatus() {
        return status;
    }

    @Override
    public Long getQId() {
        return 723153492L;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    @Override
    public String getErrMsg() {
        return this.errMsg;
    }

    @Override
    public Short getErrCode() {
        return errCode;
    }

    public void setErrCode(int code) {
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
        object.addProperty("subsystem", getSubsystem());
        object.addProperty("status", getStatus());
        object.addProperty("qid", getQId());
        object.addProperty("err_code", getErrCode());
        object.addProperty("err_msg", getErrMsg());
        object.add("result", new Gson().toJsonTree(processResultSets()));
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

    private String postProcessJsonSets(List<QJson> sets) {
        QJson qJson = new QJson();
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(
                sets.stream().map(QJson::toJSONString).collect(Collectors.joining(","))
        );
        builder.append("]");
        qJson.addItem("sets", builder.toString());
        return qJson.toJSONString();
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
