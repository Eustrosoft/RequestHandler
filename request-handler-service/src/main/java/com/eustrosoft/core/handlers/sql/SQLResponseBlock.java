package com.eustrosoft.core.handlers.sql;

import com.eustrosoft.core.handlers.responses.ResponseBlock;
import com.eustrosoft.core.tools.QJson;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLResponseBlock implements ResponseBlock {
    private String errMsg = "";
    private Short errCode = 0;

    private ResultSet resultSet;
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

    public ResultSet getResultSet() {
        return this.resultSet;
    }

    public void setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public QJson toJson() throws SQLException {
        QJson json = new QJson();
        json.addItem("subsystem", getSubsystem());
        json.addItem("status", getStatus());
        json.addItem("qid", String.valueOf(getQId()));
        json.addItem("err_code", String.valueOf(getErrCode()));
        json.addItem("err_msg", String.valueOf(getErrMsg()));
        json.addItem("result", processResultSet());
        return json;
    }

    private String processResultSet() throws SQLException {
        if (this.resultSet == null || this.resultSet.equals("")) {
            return "";
        }
        ResultSet resultSet = this.resultSet;
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        final int columnCount = resultSetMetaData.getColumnCount();

        List<String> columnNames = getColumnNames(resultSetMetaData);
        List<String> columnTypes = getColumnTypes(resultSetMetaData);
        List<List<Object>> allRows = new ArrayList<>();

        while (resultSet.next()) {
            List<Object> values = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                values.add(resultSet.getObject(i));
                resultSet.getMetaData().getColumnType(i);
            }
            allRows.add(values);
        }
        QJson qJson = new QJson();
        qJson.addItem("columns", columnNames.toString());
        qJson.addItem("data_types", columnTypes.toString());
        qJson.addItem("rows", allRows.toString());
        qJson.addItem("rows_count", String.valueOf(allRows.size()));
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
