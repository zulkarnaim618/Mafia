package com.example.game;

import java.io.Serializable;
import java.util.HashMap;

public class GameState implements Serializable {
    private HashMap<String, String> members = new HashMap<>();
    private HashMap<String, String> role = new HashMap<>();
    private HashMap<String, Boolean> isAlive = new HashMap<>();
    private String currentTime;
    private boolean alreadySelected;

    public GameState() {

    }
    public GameState(HashMap<String, String> members, HashMap<String, String> role, HashMap<String, Boolean> isAlive, String currentTime, boolean alreadySelected) {
        this.members = members;
        this.role = role;
        this.isAlive = isAlive;
        this.currentTime = currentTime;
        this.alreadySelected = alreadySelected;
    }

    public HashMap<String, Boolean> getIsAlive() {
        return isAlive;
    }

    public HashMap<String, String> getMembers() {
        return members;
    }

    public HashMap<String, String> getRole() {
        return role;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public boolean isAlreadySelected() {
        return alreadySelected;
    }
}
