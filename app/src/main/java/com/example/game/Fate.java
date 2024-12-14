package com.example.game;

public class Fate {
    private boolean[] saveAttempt;
    private boolean[] murderAttempt;
    private String[] investigateAttempt;
    private boolean[] kickAttempt;
    private int saveCount;
    private int murderCount;
    private int investigateCount;
    private int kickCount;
    public Fate (int doctor, int mafia, int detective, int player) {
        saveAttempt = new boolean[doctor];
        murderAttempt = new boolean[mafia];
        investigateAttempt = new String[detective];
        kickAttempt = new boolean[player];
    }
    public void save() {
        saveAttempt[saveCount] = true;
        saveCount++;
    }
    public void murder() {
        murderAttempt[murderCount] = true;
        murderCount++;
    }
    public void investigate(String clientID) {
        investigateAttempt[investigateCount] = clientID;
        investigateCount++;
    }
    public void kick() {
        kickAttempt[kickCount] = true;
        kickCount++;
    }

    public int getInvestigateCount() {
        return investigateCount;
    }

    public int getKickCount() {
        return kickCount;
    }

    public int getMurderCount() {
        return murderCount;
    }

    public int getSaveCount() {
        return saveCount;
    }
    public String getDetective(int index) {
        return investigateAttempt[index];
    }
}
