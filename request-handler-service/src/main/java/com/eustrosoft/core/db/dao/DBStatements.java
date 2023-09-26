/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.db.dao;

import com.eustrosoft.core.db.Query;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;

public final class DBStatements {

    @SneakyThrows
    public static PreparedStatement getFunctionStatement(Connection connection, String function, String... params) {
        return connection.prepareStatement(
                Query.builder()
                        .select()
                        .all()
                        .from()
                        .add(function)
                        .add(String.format("(%s)", String.join(",", params)))
                        .buildWithSemicolon()
                        .toString()
        );
    }
}
