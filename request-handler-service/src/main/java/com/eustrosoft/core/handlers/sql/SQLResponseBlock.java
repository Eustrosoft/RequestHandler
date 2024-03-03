/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.sql;

import com.eustrosoft.core.handlers.responses.BasicResponse;
import com.eustrosoft.core.handlers.sql.dto.ResultSetAnswer;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.eustrosoft.core.constants.Constants.SUBSYSTEM_SQL;

public class SQLResponseBlock extends BasicResponse {
    private List<ResultSet> resultSets;

    public SQLResponseBlock() {
        super(SUBSYSTEM_SQL);
    }

    public List<ResultSet> getResultSets() {
        return resultSets;
    }

    public void setResultSets(List<ResultSet> resultSets) {
        this.resultSets = resultSets;
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
