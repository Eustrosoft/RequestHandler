package com.eustrosoft.core.context;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public final class UsersContext {
    private final static int EXPIRE_TIMEOUT = 6_000_000;

    private static Map<String, Connection> connections;
    private static Map<String, User> usersDetails;
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

    public Map<String, User> getUserDetails() {
        return usersDetails;
    }

    public void setUserDetails(String sessionId, User userDetails) {
        usersDetails.put(sessionId, userDetails);
    }

    public User getSQLUser(String sessionId) {
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
}
