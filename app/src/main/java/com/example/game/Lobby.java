package com.example.game;

import java.util.*;

public class Lobby {
    private String lobbyID;
    private HashMap<String, NetworkUtil> clientMap = new HashMap<>();
    private HashMap<String, String> clientName = new HashMap<>();
    private HashMap<String, String> clientRole = new HashMap<>();
    private HashMap<String, HashMap<String, String>> specificRole = new HashMap<>();
    private HashMap<String, Boolean> isAlive = new HashMap<>();
    private HashMap<String, Fate> fates = new HashMap<>();
    private HashMap<String, Boolean> isSelected = new HashMap<>();
    private String hostID;
    private String lobbyCode;
    private Timer timer = new Timer();
    private int time;
    private String currentTime = "";
    private boolean gameOn;
    private int doctor = 1;
    private int mafia = 1;
    private int detective = 0;
    private int villager;
    private int initTime = 10;
    private int dayTime = 100;
    private int nightTime = 100;

    public Lobby(String lobbyID,String lobbyCode, String hostID) {
        this.lobbyID = lobbyID;
        this.lobbyCode = lobbyCode;
        this.hostID = hostID;
    }
    public void addClient(String userID, String userName, NetworkUtil nu) {
        clientMap.put(userID,nu);
        clientName.put(userID,userName);
    }

    public String getLobbyCode() {
        return lobbyCode;
    }
    public void sendAll(Object o) {
        try {
            for (HashMap.Entry<String, NetworkUtil> i : clientMap.entrySet()) {
                NetworkUtil nu = i.getValue();
                nu.write(o);
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
    public void initializeFate() {
        for (HashMap.Entry<String, String> i : clientName.entrySet()) {
            fates.put(i.getKey(),new Fate(doctor,mafia,detective,clientName.size()));
            isSelected.put(i.getKey(),false);
        }
    }
    public void decreaseCount(String clientID) {
        String role = clientRole.get(clientID);
        if (role.equalsIgnoreCase("doctor")) doctor--;
        else if (role.equalsIgnoreCase("mafia")) mafia--;
        else if (role.equalsIgnoreCase("detective")) detective--;
        else if (role.equalsIgnoreCase("villager")) villager--;
    }
    public void decideFate() {
        if (currentTime.equalsIgnoreCase("day")) {
            String result = dayKicked();
            System.out.println("kick " + result);
            if (!result.equalsIgnoreCase("none")) {
                isAlive.put(result,false);
                decreaseCount(result);
            }
            currentTime = "night";
            time = nightTime;
        }
        else if (currentTime.equalsIgnoreCase("night")) {
            nightRevealed();
            String result = nightMurdered();
            if (!result.equalsIgnoreCase("none")) {
                isAlive.put(result,false);
                decreaseCount(result);
            }

            currentTime = "day";
            time = dayTime;
        }
        else if (currentTime.equalsIgnoreCase("init")) {
            currentTime = "night";
            time = nightTime;
        }
        String result = gameOver();
        if(!result.equalsIgnoreCase("none")) {
            timer.cancel();
            sendAll(result);
        }
    }
    public String gameOver() {
        String result = "none";
        if (mafia>=villager+doctor+detective) result = "Mafia won";
        else if (mafia == 0) result = "Villagers won";
        return result;
    }
    public void nightRevealed() {
        for (HashMap.Entry<String, Fate> i : fates.entrySet()) {
            for (int j=0;j<i.getValue().getInvestigateCount();j++) {
                specificRole.get(i.getValue().getDetective(j)).put(i.getKey(),clientRole.get(i.getKey()));
                //System.out.println("Detective: " + i.getValue().getDetective(j));
            }
        }
    }
    public String nightMurdered() {
        List<String> list = new ArrayList<>();
        String result = "none";
        int maximumMurder = 1;
        for (HashMap.Entry<String, Fate> i : fates.entrySet()) {
            if (i.getValue().getSaveCount() > 0) continue;
            if (i.getValue().getMurderCount() > maximumMurder) {
                maximumMurder = i.getValue().getMurderCount();
                list = new ArrayList<>();
                list.add(i.getKey());
            }
            else if (i.getValue().getMurderCount() == maximumMurder) {
                list.add(i.getKey());
            }
        }
        if (list.size() > 0) {
            Random random = new Random();
            result = list.get(random.nextInt(list.size()));
        }
        return result;
    }
    public String dayKicked() {
        String result = "none";
        int maximumKick = 0;
        for (HashMap.Entry<String, Fate> i : fates.entrySet()) {
            if (i.getValue().getKickCount() > maximumKick) {
                maximumKick = i.getValue().getKickCount();
                result = i.getKey();
            }
            else if (i.getValue().getKickCount() == maximumKick) {
                result = "none";
            }
        }
        return result;
    }
    public void updateFate(Select select) {
        if (!isSelected.get(select.getClientID()) && isAlive.get(select.getClientID())) {
            if (currentTime.equalsIgnoreCase("night")) {
                if (select.getRequest().equalsIgnoreCase("save")) {
                    if (clientRole.get(select.getClientID()).equalsIgnoreCase("doctor")) {
                        fates.get(select.getVictimID()).save();
                        isSelected.put(select.getClientID(),true);
                    }
                } else if (select.getRequest().equalsIgnoreCase("kill")) {
                    if (clientRole.get(select.getClientID()).equalsIgnoreCase("mafia")) {
                        fates.get(select.getVictimID()).murder();
                        isSelected.put(select.getClientID(),true);
                    }
                } else if (select.getRequest().equalsIgnoreCase("investigate")) {
                    if (clientRole.get(select.getClientID()).equalsIgnoreCase("detective")) {
                        fates.get(select.getVictimID()).investigate(select.getClientID());
                        isSelected.put(select.getClientID(),true);
                    }
                }
            } else if (currentTime.equalsIgnoreCase("day")) {
                if (select.getRequest().equalsIgnoreCase("kick")) {
                    fates.get(select.getVictimID()).kick();
                    isSelected.put(select.getClientID(),true);
                }
            }
        }
    }
    public void sendSpecific(String userID, Object o) {
        try {
            clientMap.get(userID).write(o);
            System.out.println(clientMap.get(userID));
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public String getHostID() {
        return hostID;
    }

    public String getLobbyID() {
        return lobbyID;
    }

    public HashMap<String, String> getClientName() {
        return clientName;
    }

    public HashMap<String, Boolean> getIsAlive() {
        return isAlive;
    }

    public HashMap<String, String> getClientRole(String userID) {
        HashMap<String, String> role = new HashMap<>();
        String userRole = clientRole.get(userID);
        for (HashMap.Entry<String, String > i: clientRole.entrySet()) {
            role.put(i.getKey(),"none");
            if (userID.equals(i.getKey())) role.put(i.getKey(),i.getValue());
            if (userRole.equalsIgnoreCase("mafia")) {
                if (i.getValue().equalsIgnoreCase("mafia")) {
                    role.put(i.getKey(),i.getValue());
                }
            }
        }
        System.out.println("role made");
        return role;
    }

    public void removeClient(String userID) {
        clientName.remove(userID);
        clientMap.remove(userID);
        if (userID.equals(hostID)) {
            for (HashMap.Entry<String, String > i: clientName.entrySet()) {
                hostID = i.getKey();
                break;
            }
        }
        if (gameOn) {
            clientRole.remove(userID);
            isAlive.remove(userID);
            fates.remove(userID);
        }
        System.out.println("removed");
    }
    //Game
    public void setRole() {
        gameOn = true;
        clientRole = new HashMap<>();
        isAlive = new HashMap<>();
        villager = clientName.size()-mafia-detective-doctor;
        String[] role = generateRole(mafia,detective,doctor,clientName.size());
        int d = 0;
        for (HashMap.Entry<String, String > i: clientName.entrySet()) {
            clientRole.put(i.getKey(),role[d]);
            isAlive.put(i.getKey(),true);
            d++;
        }
        for (HashMap.Entry<String, String > i: clientName.entrySet()) {
            specificRole.put(i.getKey(),getClientRole(i.getKey()));
        }
        System.out.println("sending state");
        currentTime = "init";
        sendState();
        initializeFate();
        runTime(initTime);
        System.out.println("State sent");
    }
    public void runTime(int sec) {
        time = sec;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendAll(time);
                if (time == 0) {
                    decideFate();
                    sendState();
                    initializeFate();
                    System.out.println("done");
                    time++;
                }
                time--;
            }
        }, 0, 1000);
        System.out.println("after timer");
    }
    public void sendState() {
        for (HashMap.Entry<String, String > i: clientName.entrySet()) {
            System.out.println("sending one");
            sendSpecific(i.getKey(),new GameState(clientName,specificRole.get(i.getKey()),isAlive,currentTime,isSelected.get(i.getKey())));
            System.out.println("one sent");
        }
    }
    public String[] generateRole(int mafia, int detective, int doctor, int playerNumber) {
        Random random = new Random();
        String[] role = new String[playerNumber];
        for (int i=0;i<playerNumber;i++) {
            role[i] = "villager";
        }
        for (int i=0;i<mafia;i++) {
            int rand = random.nextInt(playerNumber);
            while (!role[rand].equalsIgnoreCase("villager")) {
                rand++;
            }
            role[rand] = "mafia";
            playerNumber--;
        }
        for (int i=0;i<doctor;i++) {
            int rand = random.nextInt(playerNumber);
            while (!role[rand].equalsIgnoreCase("villager")) {
                rand++;
            }
            role[rand] = "doctor";
            playerNumber--;
        }
        for (int i=0;i<detective;i++) {
            int rand = random.nextInt(playerNumber);
            while (!role[rand].equalsIgnoreCase("villager")) {
                rand++;
            }
            role[rand] = "detective";
            playerNumber--;
        }
        return role;
    }

    public boolean isGameOn() {
        return gameOn;
    }
}
