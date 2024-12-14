package com.example.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Client {
    private NetworkUtil networkUtil = null;
    private ReadThreadClient readThreadClient;
    public Client(String serverAddress, int serverPort,PartyLogin partyLogin) {
        try {
            networkUtil = new NetworkUtil(serverAddress,serverPort);
            networkUtil.write(partyLogin);
            PartyLogin obj = (PartyLogin) networkUtil.read();
            if (obj.isLoginSuccessful()) {
                //new ReadThreadClient(partyLogin.getUserName(),networkUtil,this);
            }
            else networkUtil.closeConnection();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}


