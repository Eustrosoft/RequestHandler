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

@Getter
@Setter
@NoArgsConstructor
public class User extends DBObject {
    private Long id;
    private String username;
    private String fullName;
    private String sessionPath;

    public User(String fullName, String username) {
        this.fullName = fullName;
        this.username = username;
    }

    @Override
    public <T extends DBObject> void fillFromResultSet(ResultSet resultSet) throws SQLException {
        resultSet.next();
        super.fillFromResultSet(resultSet);
        setFullName(resultSet.getString("full_name"));
        setUsername(resultSet.getString("login"));
        setId(resultSet.getLong("id"));
        resultSet.close();
    }
}
