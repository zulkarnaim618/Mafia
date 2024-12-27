package Server;

import com.example.game.ClientLobby;
import com.example.game.GameState;
import com.example.game.PartyLogin;
import com.example.game.Select;

import java.util.HashMap;
import java.util.Scanner;

public class Client {
    private NetworkUtil networkUtil = null;
    private String clientID;
    public ClientLobby clientLobby = new ClientLobby();
    public GameState gameState = new GameState();
    private Scanner scanner = new Scanner(System.in);
    public Client(String serverAddress, int serverPort) {
        try {
            while (true) {
                System.out.println("Menu:");
                System.out.println("1. Create lobby");
                System.out.println("2. Join lobby");
                System.out.println("3. Show lobby info");
                System.out.println("4. Start Game");
                System.out.println("5. Game Stat");
                System.out.println("6. Select");
                System.out.println("7. Exit lobby");
                int option = Integer.parseInt(scanner.nextLine());
                if (option == 1) {
                    System.out.print("Enter name: ");
                    String userName = scanner.nextLine();
                    System.out.print("Enter lobby code: ");
                    String lobbyCode = scanner.nextLine();
                    PartyLogin partyLogin = new PartyLogin(userName, lobbyCode);
                    networkUtil = new NetworkUtil(serverAddress, serverPort);
                    networkUtil.write(partyLogin);
                    partyLogin = (PartyLogin) networkUtil.read();
                    if (partyLogin.isLoginSuccessful()) {
                        clientID = partyLogin.getUserID();
                        System.out.println("Lobby creation successful");
                        new ReadThreadClient(userName,networkUtil,this);
                    }
                    else {
                        System.out.println("Lobby creation failed");
                        networkUtil.closeConnection();
                    }
                } else if (option == 2) {
                    System.out.print("Enter name: ");
                    String userName = scanner.nextLine();
                    System.out.print("Enter lobbyID: ");
                    String lobbyID = scanner.nextLine();
                    System.out.print("Enter lobby code: ");
                    String lobbyCode = scanner.nextLine();
                    PartyLogin partyLogin = new PartyLogin(userName, lobbyID, lobbyCode);
                    networkUtil = new NetworkUtil(serverAddress, serverPort);
                    networkUtil.write(partyLogin);
                    partyLogin = (PartyLogin) networkUtil.read();
                    if (partyLogin.isLoginSuccessful()) {
                        clientID = partyLogin.getUserID();
                        System.out.println("Lobby join successful");
                        new ReadThreadClient(userName,networkUtil,this);
                    }
                    else {
                        System.out.println("Lobby join failed");
                        networkUtil.closeConnection();
                    }

                } else if (option == 3) {
                    System.out.println(clientLobby);
                    System.out.println("Lobby ID: " + clientLobby.getLobbyID());
                    System.out.println("Lobby Code: " + clientLobby.getLobbyCode());
                    System.out.println("Host name: " + clientLobby.getMembers().get(clientLobby.getHostID()));
                    System.out.println("Members:");
                    int d = 1;
                    for (HashMap.Entry<String, String> i : clientLobby.getMembers().entrySet()) {
                        System.out.println(d + ". " + i.getValue());
                        d++;
                    }
                }
                else if (option == 4) {
                    if (clientID.equalsIgnoreCase(clientLobby.getHostID())) {
                        networkUtil.write("start");
                    }
                    else System.out.println("Not Host");
                }
                else if (option == 5) {
                    System.out.println("Members:");
                    int d = 1;
                    for (HashMap.Entry<String, String> i : gameState.getMembers().entrySet()) {
                        System.out.println(d + ". " + i.getValue() + "\t" + gameState.getRole().get(i.getKey()) + "\t" + gameState.getIsAlive().get(i.getKey()));
                        d++;
                    }
                }
                else if (option == 6) {
                    System.out.print("Enter index");
                    int index;
                    try {
                        index = Integer.parseInt(scanner.nextLine());
                    }
                    catch(Exception e) {
                        index = 0;
                    }
                    int d = 1;
                    for (HashMap.Entry<String, String> i : gameState.getMembers().entrySet()) {
                        if (d == index) {
                            String request = "";
                            if (gameState.getCurrentTime().equalsIgnoreCase("night")) {
                                if (gameState.getRole().get(clientID).equalsIgnoreCase("doctor")) request = "save";
                                else if (gameState.getRole().get(clientID).equalsIgnoreCase("mafia")) request = "kill";
                                else if (gameState.getRole().get(clientID).equalsIgnoreCase("detective")) request = "investigate";
                                else if (gameState.getRole().get(clientID).equalsIgnoreCase("villager")) request = "kick";
                            }
                            else if (gameState.getCurrentTime().equalsIgnoreCase("day")) {
                                request = "kick";
                            }
                            networkUtil.write(new Select(clientID,i.getKey(),request));
                            break;
                        }
                        d++;
                    }
                }
                else if (option == 7) {
                    networkUtil.write("disconnect");
                    System.out.println("disconnecting");
                }
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
    public static void main(String[] args) {
        Client client = new Client("52.77.42.98",44449);
    }
}


