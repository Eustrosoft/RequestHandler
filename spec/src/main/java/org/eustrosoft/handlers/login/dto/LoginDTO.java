package org.eustrosoft.handlers.login.dto;

import org.eustrosoft.json.QJson;
import org.eustrosoft.json.exception.JsonException;
import org.eustrosoft.spec.interfaces.JsonParsable;

public class LoginDTO implements JsonParsable<LoginDTO> {
    public static final String PARAM_LOGIN = "login";
    public static final String PARAM_PASSWORD = "password";

    private String login;
    private String password;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public LoginDTO convertToObject(QJson qJson) throws JsonException {
        setLogin(qJson.getItemString(PARAM_LOGIN));
        setPassword(qJson.getItemString(PARAM_PASSWORD));
        return this;
    }
}
