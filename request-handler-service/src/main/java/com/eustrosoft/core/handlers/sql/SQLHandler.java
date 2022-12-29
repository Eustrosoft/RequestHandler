package com.eustrosoft.core.handlers.sql;

import com.eustrosoft.core.context.UsersContext;
import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;

import javax.servlet.http.HttpServletRequest;
import java.sql.ResultSet;

public final class SQLHandler implements Handler {
    private DBWrapper dbWrapper;
    private String dbUrl;

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock, HttpServletRequest request) throws Exception {
        UsersContext usersContext = UsersContext.getInstance();
        UsersContext.SQLUser user = usersContext.getSQLUser(request.getSession(false).getId());

        this.dbUrl = request.getServletContext().getInitParameter("dbUrl");
        this.dbWrapper = DBWrapper.getInstance(user.getUser(), user.getPassword(), this.dbUrl);
        SQLRequestBlock sqlRequest = (SQLRequestBlock) requestBlock;
        String query = sqlRequest.getQuery();

        SQLResponseBlock responseBlock = new SQLResponseBlock();
        ResultSet resultSet = null;
        try {
            resultSet = this.dbWrapper.executeQuery(query);
            responseBlock.setStatus(200L);
            responseBlock.setErrCode(0);
            responseBlock.setErrMsg("No errors");
        } catch (Exception ex) {
            responseBlock.setErrMsg(ex.getMessage());
            responseBlock.setStatus(500L);
            responseBlock.setErrCode(1);
        }
        responseBlock.setResultSet(resultSet);
        return responseBlock;
    }
}
