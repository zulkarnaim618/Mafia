package Server;

import java.io.Serializable;

public class Player implements Serializable {
    private String name;
    private int userID;
    public Player (String name, int userID) {
        this.name = name;
        this.userID = userID;
    }
}
