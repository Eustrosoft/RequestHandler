/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.sql;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eustrosoft.core.BasicHandler;
import org.eustrosoft.core.BasicService;
import org.eustrosoft.core.annotation.Handler;
import org.eustrosoft.handlers.sql.dto.ResultSetAnswer;
import org.eustrosoft.handlers.sql.dto.SqlResponseBock;
import org.eustrosoft.providers.SessionProvider;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.spec.request.BasicRequestBlock;
import org.eustrosoft.spec.request.StringDTO;
import org.eustrosoft.spec.response.BasicResponseBlock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static org.eustrosoft.spec.Constants.ERR_OK;
import static org.eustrosoft.spec.Constants.ERR_UNEXPECTED;
import static org.eustrosoft.spec.Constants.MSG_OK;
import static org.eustrosoft.spec.Constants.SUBSYSTEM_SQL;

@Handler(SUBSYSTEM_SQL)
public final class SQLHandler extends BasicService implements BasicHandler {

    @Override
    public BasicResponseBlock<ResultSetAnswer> processRequest(BasicRequestBlock<?> requestBlock) throws Exception {
        HttpServletRequest request = getRequest();
        HttpServletResponse httpResponse = getResponse();
        SessionProvider sessionProvider = new SessionProvider(request, httpResponse);
        QDBPSession session = sessionProvider.getSession();
        Connection sqlConnection = session.getSQLConnection();

        ((BasicRequestBlock) requestBlock).setData(new StringDTO());
        BasicRequestBlock<StringDTO> dto = (BasicRequestBlock<StringDTO>) requestBlock;

        BasicResponseBlock<ResultSetAnswer> responseBlock = new SqlResponseBock<>(requestBlock.getR());
        List<ResultSet> resultSet = new ArrayList<>();
        List<String> queries = getQueries(dto.getData().getQuery());
        try {
            for (String targetQuery : queries) {
                PreparedStatement preparedStatement = sqlConnection.prepareStatement(targetQuery);
                resultSet.add(preparedStatement.executeQuery());
                responseBlock.setE(ERR_OK);
                responseBlock.setM(MSG_OK);
            }
        } catch (Exception ex) {
            responseBlock.setM(ex.getMessage());
            responseBlock.setE(ERR_UNEXPECTED);
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
