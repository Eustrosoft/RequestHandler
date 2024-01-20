/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.login;

import org.eustrosoft.core.handlers.requests.BasicRequest;
import org.eustrosoft.core.tools.QJson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.eustrosoft.core.constants.Constants.SUBSYSTEM_LOGIN;

public class LoginRequestBlock extends BasicRequest {
    private String login;
    private String password;

    public LoginRequestBlock(HttpServletRequest request,
                             HttpServletResponse response,
                             QJson qJson) {
        super(request, response, qJson);
        parseQJson(qJson);
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
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

    private void parseQJson(QJson qJson) {
        if (qJson == null) {
            throw new NullPointerException("QJson was null");
        }
        setLogin(qJson.getItemString("login"));
        setPassword(qJson.getItemString("password"));
    }
}
