package com.example.game;

import java.io.Serializable;

public class PartyLogin implements Serializable {
    private String userName;
    private String userID;
    private boolean host;
    private String lobbyID;
    private String lobbyCode;
    private boolean loginSuccessful;
    public PartyLogin(String userName, String lobbyID, String lobbyCode) {
        this.userName = userName;
        this.lobbyID = lobbyID;
        this.lobbyCode = lobbyCode;
        this.host = false;
    }
    public PartyLogin(String userName, String lobbyCode) {
        this.userName = userName;
        this.lobbyCode = lobbyCode;
        this.host = true;
    }
    public void setHost(boolean host) {
        this.host = host;
    }

    public boolean isHost() {
        return host;
    }

    public String getLobbyID() {
        return lobbyID;
    }

    public void setLobbyID(String lobbyID) {
        this.lobbyID = lobbyID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setLobbyCode(String lobbyCode) {
        this.lobbyCode = lobbyCode;
    }

    public String getLobbyCode() {
        return lobbyCode;
    }

    public void setLoginSuccessful(boolean loginSuccessful) {
        this.loginSuccessful = loginSuccessful;
    }

    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}