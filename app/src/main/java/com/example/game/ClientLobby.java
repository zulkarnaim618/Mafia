package com.example.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientLobby implements Serializable {
    private HashMap<String, String> members = new HashMap<>();
    private String lobbyID;
    private String lobbyCode;
    private String hostID;
    public ClientLobby() {

    }
    public ClientLobby(String lobbyID, String lobbyCode, String hostID, HashMap<String, String> members) {
        this.lobbyID = lobbyID;
        this.lobbyCode = lobbyCode;
        this.hostID = hostID;
        this.members = members;
    }
    public ClientLobby(Lobby lobby) {
        this.lobbyID = lobby.getLobbyID();
        this.lobbyCode = lobby.getLobbyCode();
        this.hostID = lobby.getHostID();
        this.members = lobby.getClientName();
    }

    public String getHostID() {
        return hostID;
    }

    public String getLobbyCode() {
        return lobbyCode;
    }

    public HashMap<String, String> getMembers() {
        return members;
    }

    public String getLobbyID() {
        return lobbyID;
    }
}
