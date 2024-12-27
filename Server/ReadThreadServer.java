package Server;


import com.example.game.ClientLobby;
import com.example.game.Lobby;
import com.example.game.Select;

import java.io.IOException;

public class ReadThreadServer implements Runnable {
    private Thread thr;
    private NetworkUtil networkUtil;
    private String userID;
    private Lobby lobby;


    public ReadThreadServer(String userID, Lobby lobby, NetworkUtil networkUtil) {
        this.userID = userID;
        this.lobby = lobby;
        this.networkUtil = networkUtil;
        this.thr = new Thread(this);
        thr.start();
    }

    public void run() {
        try {
            while (true) {
                Object o = networkUtil.read();
                if (o instanceof String) {
                    String msg = (String) o;
                    if (msg.equalsIgnoreCase("disconnect")) {
                        lobby.removeClient(userID);
                        networkUtil.write("disconnect");
                        if (!lobby.isGameOn()) lobby.sendAll(new ClientLobby(lobby));
                        else lobby.sendState();
                        break;
                    }
                    else if (msg.equalsIgnoreCase("start")) {
                        if (userID.equals(lobby.getHostID())) {
                            lobby.setRole();
                        }
                    }
                }
                else if (o instanceof Select) {
                    lobby.updateFate((Select) o);
                    networkUtil.write("selected");
                }

            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
        finally {
            try {
                networkUtil.closeConnection();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
/*
for (HashMap.Entry<String, Server.Server.NetworkUtil> i: clientMap.entrySet()) {
                        Server.Server.NetworkUtil nu = i.getValue();
                        nu.write(sellPlayers);
                    }
 */



