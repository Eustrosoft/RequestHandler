/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class DBStatements {

    public static PreparedStatement getFunctionStatement(Connection connection, String function, String... where) throws SQLException {
        return connection.prepareStatement(
                String.format(
                        "SELECT * FROM %s %s;",
                        function.endsWith("()") ? function : String.format("%s()", function),
                        where == null || where.length == 0
                                ? ""
                                : String.format("where (%s)", String.join(" AND ", where))
                )
        );
    }
}
