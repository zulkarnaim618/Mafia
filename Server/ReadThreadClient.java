package Server;

import com.example.game.ClientLobby;
import com.example.game.GameState;

import java.io.IOException;

public class ReadThreadClient implements Runnable {
    private Thread thr;
    private NetworkUtil networkUtil;
    private Client client;
    private String userName;


    public ReadThreadClient(String userName, NetworkUtil networkUtil,Client client) {
        this.userName = userName;
        this.client = client;
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
                        System.out.println("disconnected");
                        break;
                    }
                    else if (msg.equalsIgnoreCase("selected")) {
                        System.out.println("Selected");
                    }
                    else {
                        System.out.println(msg);
                    }
                }
                else if (o instanceof ClientLobby) {
                    System.out.println("new list uwu");
                    System.out.println(o);
                    client.clientLobby = (ClientLobby) o;
                }
                else if (o instanceof GameState) {
                    System.out.println("new State");
                    client.gameState = (GameState) o;
                }
                else if (o instanceof Integer) {
                    Integer time = (Integer) o;
                    if(time%20 == 0) System.out.println("Time: " + time);
                }
            }
        }
        catch (Exception e) {
            System.out.println("thread exception");
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



