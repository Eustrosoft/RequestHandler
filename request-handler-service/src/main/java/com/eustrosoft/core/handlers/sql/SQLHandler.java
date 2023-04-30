package com.eustrosoft.core.handlers.sql;

import com.eustrosoft.core.context.User;
import com.eustrosoft.core.context.UsersContext;
import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;
import com.eustrosoft.core.tools.WebParams;

import javax.servlet.http.HttpServletRequest;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public final class SQLHandler implements Handler {
    private DBWrapper dbWrapper;
    private String dbUrl;

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock) throws Exception {
        HttpServletRequest request = requestBlock.getHttpRequest();
        UsersContext usersContext = UsersContext.getInstance();
        User user = usersContext.getSQLUser(request.getSession(false).getId());

        this.dbUrl = WebParams.getString(request, WebParams.DB_URL);
        this.dbWrapper = DBWrapper.getInstance(user.getUserName(), user.getPassword(), this.dbUrl);
        SQLRequestBlock sqlRequest = (SQLRequestBlock) requestBlock;
        String query = sqlRequest.getQuery();

        SQLResponseBlock responseBlock = new SQLResponseBlock();
        List<ResultSet> resultSet = new ArrayList<>();
        List<String> queries = getQueries(query);
        try {
            for (String targetQuery : queries) {
                resultSet.add(this.dbWrapper.executeQuery(targetQuery));
                responseBlock.setE(0);
                responseBlock.setErrMsg("No errors");
            }
        } catch (Exception ex) {
            responseBlock.setErrMsg(ex.getMessage());
            responseBlock.setE(1);
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
