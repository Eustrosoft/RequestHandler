/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.sql;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public final class DBConnector {
    public static final String KEY_USER = "user";
    public static final String KEY_PASSWORD = "password";
    private Connection connection;
    private String url = "jdbc:postgresql://%s";
    private Properties dbProps;

    public DBConnector(String db, String user, String password) {
        this.url = String.format(url, db);
        dbProps = new Properties();
        dbProps.setProperty(KEY_USER, user);
        dbProps.setProperty(KEY_PASSWORD, password);
    }

    public synchronized Connection connectToDatabase() throws Exception {
        if (this.connection == null) {
            this.connection = connectToDB();
        }
        return this.connection;
    }

    public synchronized ResultSet getData(String query) throws Exception {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        connection = connectToDB();
        statement = connection.createStatement();
        resultSet = statement.executeQuery(query);
        return resultSet;
    }

    public synchronized void closeConnection() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized Connection connectToDB() throws Exception {
        Driver driver = (Driver) Class.forName("org.postgresql.Driver").getDeclaredConstructor().newInstance();
        DriverManager.registerDriver(driver);
        return DriverManager.getConnection(url, dbProps);
    }

    public Properties getDbProps() {
        return dbProps;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

