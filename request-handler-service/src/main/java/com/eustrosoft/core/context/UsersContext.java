package com.eustrosoft.core.context;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public final class UsersContext {
    private final static int EXPIRE_TIMEOUT = 6_000_000;

    private static Map<String, Connection> connections;
    private static Map<String, SQLUser> usersDetails;
    private static UsersContext usersContext;

    private UsersContext() {
        connections = new HashMap<>();
        usersDetails = new HashMap<>();
    }

    public static UsersContext getInstance() {
        if (usersContext == null) {
            usersContext = new UsersContext();
        }
        return usersContext;
    }

    public Map<String, SQLUser> getUserDetails() {
        return usersDetails;
    }

    public void setUserDetails(String sessionId, SQLUser userDetails) {
        usersDetails.put(sessionId, userDetails);
    }

    public SQLUser getSQLUser(String sessionId) {
        return usersDetails.get(sessionId);
    }

    public Connection getConnection(String userDetails) {
        return connections.get(userDetails);
    }

    public void setConnection(String userDetails, Connection connection) {
        connections.put(userDetails, connection);
    }

    public void removeConnection(String userDetails) {
        Connection connection = connections.get(userDetails);
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            connections.remove(userDetails);
        }
    }

    public Map<String, Connection> getConnections() {
        return connections;
    }

    public static class SQLUser {
        private String user;
        private String password;

        public SQLUser(String user, String password) {
            this.user = user;
            this.password = password;
        }

        public SQLUser() {

        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
