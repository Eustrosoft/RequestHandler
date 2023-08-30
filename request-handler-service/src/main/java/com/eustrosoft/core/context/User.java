/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.context;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User implements EustrosoftUser {
    private Long id;
    private String username;
    private String password;
    private String ip;
    private String sessionPath;

    public User(String username, String password, String ip) {
        this.username = username;
        this.password = password;
        this.ip = ip;
    }

    public User() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getIp() {
        return this.ip;
    }

    @Override
    public String getUserName() {
        return this.username;
    }

    public String getSessionPath() {
        return this.sessionPath;
    }

    public void setSessionPath(String sessionPath) {
        this.sessionPath = sessionPath;
    }

    public User fromResultSet(ResultSet resultSet) throws SQLException {
        User user = new User(
                resultSet.getString("login"), "", ""
        );
        user.setId(resultSet.getLong("id"));
        return user;
    }
}
