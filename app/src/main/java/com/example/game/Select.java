package com.example.game;

import java.io.Serializable;

public class Select implements Serializable {
    private String clientID;
    private String victimID;
    private String request;
    public Select() {

    }
    public Select (String clientID, String victimID, String request) {
        this.clientID = clientID;
        this.victimID = victimID;
        this.request = request;
    }

    public String getClientID() {
        return clientID;
    }

    public String getRequest() {
        return request;
    }

    public String getVictimID() {
        return victimID;
    }
}
