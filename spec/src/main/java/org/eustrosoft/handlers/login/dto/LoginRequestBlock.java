/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.handlers.login.dto;

import org.eustrosoft.json.QJson;
import org.eustrosoft.spec.request.TISRequestBlock;

import static org.eustrosoft.spec.Constants.SUBSYSTEM_LOGIN;

public class LoginRequestBlock extends TISRequestBlock<T> {
    private String login;
    private String password;

    public LoginRequestBlock(String request, QJson qJson) {
        super(SUBSYSTEM_LOGIN, request, qJson);
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

    private void parseQJson(QJson qJson) {
        if (qJson == null) {
            throw new NullPointerException("QJson was null");
        }
        setLogin(qJson.getItemString("login"));
        setPassword(qJson.getItemString("password"));
    }
}
