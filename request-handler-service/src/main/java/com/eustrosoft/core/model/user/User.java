/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.model.user;

import com.eustrosoft.core.model.DBObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.eustrosoft.core.constants.Constants.UNKNOWN;

@Getter
@Setter
@NoArgsConstructor
public class User extends DBObject {
    private Long id;
    private String username;
    private String fullName;
    private String dbUser;
    private String sessionPath;

    public User(String fullName, String username) {
        this.fullName = fullName;
        this.username = username;
    }

    public User(String fullName, String username, String dbUser) {
        this.fullName = fullName;
        this.username = username;
        this.dbUser = dbUser;
    }

    @Override
    public <T extends DBObject> void fillFromResultSet(ResultSet resultSet) throws SQLException {
        try {
            resultSet.next();
            super.fillFromResultSet(resultSet);
            setId(resultSet.getLong("id"));
            setFullName(resultSet.getString("full_name"));
            setUsername(resultSet.getString("login"));
            setDbUser(resultSet.getString("db_user"));
        } catch (Exception ex) {
            setId(-1L);
            setFullName(UNKNOWN);
            setUsername(UNKNOWN);
            setDbUser(UNKNOWN);
        } finally {
            resultSet.close();
        }
    }
}
