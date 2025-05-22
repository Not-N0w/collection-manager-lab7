package com.labs.common.user;

import java.io.Serializable;

public class User implements Serializable {
    private Long id;
    private String login;
    private String passwordHash;
    private Status status;
    private boolean isVerified = false;

    @Override
    public String toString() {
        return "User{" +
                "isVerified=" + isVerified +
                ", status=" + status +
                ", login='" + login + '\'' +
                ", id=" + id +
                '}';
    }

    public User(String login, String passwordHash)  {
        this.login = login;
        this.passwordHash = passwordHash;
        this.status = Status.SIMPLE;
    }
    public User() {}

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Long getId() {
        return id;
    }
}
