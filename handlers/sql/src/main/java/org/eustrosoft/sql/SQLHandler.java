/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.sql;

import org.eustrosoft.constants.Constants;
import org.eustrosoft.core.annotations.Handler;
import org.eustrosoft.core.interfaces.BasicHandler;
import org.eustrosoft.core.request.BasicRequestBlock;
import org.eustrosoft.core.request.RequestBlock;
import org.eustrosoft.core.response.ResponseBlock;
import org.eustrosoft.core.response.basic.ListStringResponseData;
import org.eustrosoft.handlers.sql.dto.SqlDto;
import org.eustrosoft.handlers.sql.dto.SqlResponseBlock;
import org.eustrosoft.providers.RequestContextHolder;
import org.eustrosoft.providers.SessionProvider;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.sql.utils.ResultSetUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.eustrosoft.constants.Constants.REQUEST_SQL;
import static org.eustrosoft.constants.Constants.SUBSYSTEM_SQL;

@Handler(SUBSYSTEM_SQL)
public final class SQLHandler implements BasicHandler {
    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock) throws Exception {
        RequestContextHolder.ServletAttributes attributes = RequestContextHolder.getAttributes();
        SessionProvider sessionProvider = new SessionProvider(attributes.getRequest(), attributes.getResponse());
        QDBPSession session = sessionProvider.getSession();
        Connection sqlConnection = session.getSQLConnection();

        BasicRequestBlock<SqlDto> sqlRequest = (BasicRequestBlock<SqlDto>) requestBlock;
        sqlRequest.setData(new SqlDto());
        String query = sqlRequest.getData().getSql();

        SqlResponseBlock responseBlock = new SqlResponseBlock(REQUEST_SQL);
        List<ResultSet> resultSet = new ArrayList<>();
        List<String> queries = getQueries(query);
        try {
            for (String targetQuery : queries) {
                PreparedStatement preparedStatement = sqlConnection.prepareStatement(targetQuery);
                resultSet.add(preparedStatement.executeQuery());
                responseBlock.setE(Constants.ERR_OK);
                responseBlock.setM(Constants.MSG_OK);
            }
            List<String> rows = resultSet.stream().map(rs -> new ResultSetUtils().resSetToString(rs))
                    .collect(Collectors.toList());
            responseBlock.setData(new ListStringResponseData("sql", rows));
        } catch (Exception ex) {
            responseBlock.setM(ex.getMessage());
            responseBlock.setE(Constants.ERR_UNEXPECTED);
        }
        return responseBlock;
    }

    private List<String> getQueries(String query) {
        String[] queries = query.trim().split(";");
        List<String> queris = new ArrayList<>();
        for (int i = 0; i < queries.length; i++) {
            if (!queries[i].isEmpty())
                queris.add(queries[i]);
        }
        return queris;
    }
}
