package Server;

import com.example.game.ClientLobby;
import com.example.game.Lobby;
import com.example.game.PartyLogin;


import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
    private HashMap<String, Lobby> lobbies = new HashMap<>();
    private HashMap<String, String> lobbyInfos = new HashMap<>();
    private int userCount;
    private int lobbyCount;
    private ServerSocket serverSocket;
    private HashMap<String, NetworkUtil> clientMap = new HashMap<>();

    public Server() {

        try {
            serverSocket = new ServerSocket(44449);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("client accepted");
                NetworkUtil networkUtil = new NetworkUtil(clientSocket);
                PartyLogin partyLogin = (PartyLogin) networkUtil.read();
                String userID = "user" + userCount;
                if (partyLogin.isHost()) {
                    System.out.println("yes");
                    String lobbyID = "lobby" + lobbyCount;
                    Lobby lobby = new Lobby(lobbyID,partyLogin.getLobbyCode(),userID);
                    lobby.addClient(userID,partyLogin.getUserName(),networkUtil);
                    lobbies.put(lobbyID,lobby);
                    lobbyInfos.put(lobbyID,partyLogin.getLobbyCode());
                    lobbyCount++;
                    userCount++;
                    partyLogin.setLoginSuccessful(true);
                    partyLogin.setLobbyID(lobbyID);
                    partyLogin.setUserID(userID);
                    networkUtil.write(partyLogin);
                    ClientLobby clientLobby = new ClientLobby(lobby);
                    lobby.sendAll(clientLobby);
                    new ReadThreadServer(userID,lobby,networkUtil);
                }
                else {
                    boolean lobbyExists = false;
                    String lobbyID = partyLogin.getLobbyID();
                    String lobbyCode = lobbyInfos.get(lobbyID);
                    if (lobbyCode != null && lobbyCode.equals(partyLogin.getLobbyCode())) lobbyExists = true;
                    if (lobbyExists) {
                        Lobby lobby = lobbies.get(lobbyID);
                        lobby.addClient(userID,partyLogin.getUserName(), networkUtil);
                        userCount++;
                        partyLogin.setLoginSuccessful(true);
                        partyLogin.setUserID(userID);
                        networkUtil.write(partyLogin);
                        ClientLobby clientLobby = new ClientLobby(lobby);
                        lobby.sendAll(clientLobby);
                        new ReadThreadServer(userID,lobby,networkUtil);
                    }
                    else {
                        partyLogin.setLoginSuccessful(false);
                        networkUtil.write(partyLogin);
                    }

                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    public static void main(String args[]) {
        Server server = new Server();
    }
}
