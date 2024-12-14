package com.example.game;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReadThreadClient implements Runnable {
    private Thread thr;
    private NetworkUtil networkUtil;
    public ClientLobby clientLobby;
    private MainActivity main;
    private String userName;


    public ReadThreadClient(String userName, NetworkUtil networkUtil,MainActivity main) {
        this.main = main;
        this.userName = userName;
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
                        Log.i("dis","disconnected");
                        main.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                main.setContentView(R.layout.activity_main);
                            }
                        });
                        break;
                    }
                    else if (msg.equalsIgnoreCase("selected")) {
                        main.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                main.select.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                    else {
                        main.gameOver = true;
                        main.gameOverMsg = msg;
                        main.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                main.setContentView(R.layout.gameover);
                                main.ending();
                            }
                        });
                        System.out.println(msg);
                    }
                }
                else if (o instanceof ClientLobby) {
                    this.main.clientLobby = (ClientLobby) o;
                    if (!main.gameOver) {
                        main.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                main.populateLobby();
                                Log.i("new", "populate");
                            }
                        });
                    }

                }
                else if (o instanceof GameState) {
                    System.out.println("new State");
                    this.main.gameState = (GameState) o;
                    if (!main.gameOver) {
                        main.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                main.setContentView(R.layout.game);
                                main.gameScene();
                            }
                        });
                    }
                }
                else if (o instanceof Integer) {
                    Integer time = (Integer) o;
                    main.time = time;
                    main.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            main.timeText.setText("Time: " + main.time);
                        }
                    });
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



