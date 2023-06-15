/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.sql;

import com.eustrosoft.core.handlers.sql.utils.ResultSetUtils;

import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicReference;

public final class DBWrapper {
    private static DBWrapper dbWrapper;
    private DBConnector dbConnector;
    private ResultSetUtils resultSetUtils;

    private DBWrapper(String user, String password, String dbName) {
        this.dbConnector = new DBConnector(dbName, user, password);
        this.resultSetUtils = new ResultSetUtils();
    }

    public static DBWrapper getInstance(String user, String password, String dbName) {
        if (dbWrapper == null) {
            return new DBWrapper(user, password, dbName);
        }
        return dbWrapper;
    }

    public String executeStringQuery(String command) {
        AtomicReference<String> answer = new AtomicReference<>("");
        try {
            Thread newThread = new Thread(() -> {
                try {
                    dbConnector.connectToDatabase();
                    String result = executeQueryAndGetString(dbConnector, command);
                    answer.set(result);
                } catch (Exception e) {
                    answer.set(e.getLocalizedMessage());
                }
            });
            newThread.start();
            newThread.join();
        } catch (Exception ex) {
            answer.set(ex.getLocalizedMessage());
        } finally {
            dbConnector.closeConnection();
        }
        return answer.get();
    }

    public ResultSet executeQuery(String command) throws Exception {
        try {
            return dbConnector.getData(command);
        } finally {
            dbConnector.closeConnection();
        }
    }

    private String executeQueryAndGetString(DBConnector connector, String query) throws Exception {
        if (connector == null) {
            throwDBException("Connector isn't initialized.");
        }
        if (query == null || query.equals("")) {
            throwDBException("Query string is empty.");
        }
        return resultSetUtils.resSetToString(connector.getData(query));
    }

    private void throwDBException(String message) throws Exception {
        if (message == null || message.equals("")) {
            throw new Exception();
        }
        throw new Exception(message);
    }
}

