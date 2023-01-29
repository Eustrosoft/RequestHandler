package com.eustrosoft.core.context;

public class User implements EustrosoftUser {
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
}
