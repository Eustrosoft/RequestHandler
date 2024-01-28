/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.sam.model;

import org.eustrosoft.core.model.DBObject;
import org.eustrosoft.spec.Constants;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User extends DBObject {
    private Long id;
    private String username;
    private String fullName;
    private String dbUser;
    private String sessionPath;

    public User() {
        super(-1L, -1L, -1L);
    }

    public User(String fullName, String username) {
        this();
        this.fullName = fullName;
        this.username = username;
    }

    public User(String fullName, String username, String dbUser) {
        this(fullName, username);
        this.dbUser = dbUser;
    }

    @Override
    public <T extends DBObject> void fillFromResultSet(ResultSet resultSet) throws SQLException {
        try {
            super.fillFromResultSet(resultSet);
            setId(resultSet.getLong("id"));
            setFullName(resultSet.getString("full_name"));
            setUsername(resultSet.getString("login"));
            setDbUser(resultSet.getString("db_user"));
        } catch (Exception ex) {
            setId(-1L);
            setFullName(Constants.UNKNOWN);
            setUsername(Constants.UNKNOWN);
            setDbUser(Constants.UNKNOWN);
        } finally {
            resultSet.close();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getSessionPath() {
        return sessionPath;
    }

    public void setSessionPath(String sessionPath) {
        this.sessionPath = sessionPath;
    }
}
