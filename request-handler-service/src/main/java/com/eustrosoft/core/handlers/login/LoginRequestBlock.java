package com.eustrosoft.core.handlers.login;

import com.eustrosoft.core.handlers.requests.BasicRequest;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.tools.QJson;
import com.eustrosoft.datasource.sources.model.CMSType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.eustrosoft.core.Constants.SUBSYSTEM_CMS;
import static com.eustrosoft.core.Constants.SUBSYSTEM_LOGIN;

public class LoginRequestBlock extends BasicRequest {
    private String requestType;
    private String login;
    private String password;

    public LoginRequestBlock(HttpServletRequest request,
                             HttpServletResponse response,
                             QJson qJson) {
        super(request, response);
        parseQJson(qJson);
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRequestType() {
        return requestType;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getS() {
        return SUBSYSTEM_LOGIN;
    }

    @Override
    public String getR() {
        return this.requestType;
    }

    @Override
    public HttpServletRequest getHttpRequest() {
        return this.request;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    private void parseQJson(QJson qJson) {
        if (qJson == null) {
            throw new NullPointerException("QJson was null");
        }
        setLogin(qJson.getItemString("login"));
        setPassword(qJson.getItemString("password"));
    }
}