/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.sql;

import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;
import com.eustrosoft.core.providers.SessionProvider;
import org.eustrosoft.qdbp.QDBPSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static com.eustrosoft.core.constants.Constants.ERR_OK;
import static com.eustrosoft.core.constants.Constants.ERR_UNEXPECTED;
import static com.eustrosoft.core.constants.Constants.MSG_OK;

public final class SQLHandler implements Handler {
    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock) throws Exception {
        HttpServletRequest request = requestBlock.getHttpRequest();
        HttpServletResponse httpResponse = requestBlock.getHttpResponse();
        SessionProvider sessionProvider = new SessionProvider(request, httpResponse);
        QDBPSession session = sessionProvider.getSession();
        Connection sqlConnection = session.getSQLConnection();

        SQLRequestBlock sqlRequest = (SQLRequestBlock) requestBlock;
        String query = sqlRequest.getQuery();

        SQLResponseBlock responseBlock = new SQLResponseBlock();
        List<ResultSet> resultSet = new ArrayList<>();
        List<String> queries = getQueries(query);
        try {
            for (String targetQuery : queries) {
                PreparedStatement preparedStatement = sqlConnection.prepareStatement(targetQuery);
                resultSet.add(preparedStatement.executeQuery());
                responseBlock.setE(ERR_OK);
                responseBlock.setErrMsg(MSG_OK);
            }
        } catch (Exception ex) {
            responseBlock.setErrMsg(ex.getMessage());
            responseBlock.setE(ERR_UNEXPECTED);
        }
        responseBlock.setResultSet(resultSet);
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
