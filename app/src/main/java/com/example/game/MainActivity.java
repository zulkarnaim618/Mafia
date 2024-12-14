package com.example.game;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.ViewCompat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    public ClientLobby clientLobby = new ClientLobby();
    public GameState gameState = new GameState();
    private String clientID;
    private String victimID = "none";
    NetworkUtil networkUtil;
    private int height;
    private int width;
    public int time;
    public TextView timeText;
    private TextView previousSelected;
    private TextView empty;
    public Button select;
    public boolean gameOver;
    public String gameOverMsg ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //populateLobby();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        empty = new TextView(this);
        previousSelected = empty;
    }

    public void buttonClick(View v) {
        setContentView(R.layout.createlobby);
    }
    public void button2Click(View v) {
        setContentView(R.layout.joinlobby);
    }
    public void goBack(View v) {
        setContentView(R.layout.activity_main);
    }
    public void leaveLobby(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    networkUtil.write("disconnect");
                }
                catch (Exception e) {

                }
            }
        }).start();
    }
    public void viewLobby(View v) {
        EditText name = findViewById(R.id.editTextTextPersonName);
        EditText lobbyCode = findViewById(R.id.editTextTextPersonName2);
        final PartyLogin[] partyLogin = {new PartyLogin(name.getText().toString(), lobbyCode.getText().toString())};
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendRequest("52.77.42.98",44449, partyLogin[0]);
            }
        }).start();

    }
    public void viewLobby1(View v) {
        EditText name = findViewById(R.id.editTextTextPersonName3);
        EditText lobbyID = findViewById(R.id.editTextTextPersonName4);
        EditText lobbyCode = findViewById(R.id.editTextTextPersonName5);
        final PartyLogin[] partyLogin = {new PartyLogin(name.getText().toString(), lobbyID.getText().toString(), lobbyCode.getText().toString())};
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendRequest("52.77.42.98",44449, partyLogin[0]);
            }
        }).start();

    }
    public void populateLobby() {
        HashMap<String, String> members = clientLobby.getMembers();
        String hostID = clientLobby.getHostID();
        ConstraintLayout constraintLayout = findViewById(R.id.scrollLayout);
        TextView lobbyDetails = findViewById(R.id.lobby_details);
        lobbyDetails.setGravity(Gravity.CENTER);
        String lobbyDet = "Lobby ID: " + clientLobby.getLobbyID() + "\nLobby Code: " + clientLobby.getLobbyCode() + "\nHost name: " + clientLobby.getMembers().get(hostID);
        lobbyDetails.setText(lobbyDet);
        constraintLayout.removeAllViews();
        int t=0;
        for (HashMap.Entry<String, String> i : members.entrySet()) {
            TextView tv = new TextView(this);
            String name = i.getValue();
            if (hostID.equals(i.getKey())) name+=" (Host)";
            tv.setText(name);
            tv.setId(ViewCompat.generateViewId());
            tv.setBackgroundColor(Color.parseColor("green"));
            int tabWidth = (width-300)/2;
            tv.setLayoutParams(new ConstraintLayout.LayoutParams(tabWidth,200));
            tv.setGravity(Gravity.CENTER);
            constraintLayout.addView(tv);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            if (t%2==0) {
                constraintSet.connect(tv.getId(), ConstraintSet.LEFT, R.id.scrollLayout, ConstraintSet.LEFT, 100);
                constraintSet.connect(tv.getId(), ConstraintSet.TOP, R.id.scrollLayout, ConstraintSet.TOP, 100 + t * 150);
            }
            else {
                constraintSet.connect(tv.getId(), ConstraintSet.LEFT, R.id.scrollLayout, ConstraintSet.LEFT, 200+tabWidth);
                constraintSet.connect(tv.getId(), ConstraintSet.TOP, R.id.scrollLayout, ConstraintSet.TOP, 100 + (t-1) * 150);
            }
            constraintSet.applyTo(constraintLayout);
            t++;
        }
        Button button8 = findViewById(R.id.button8);
        if(clientID.equals(clientLobby.getHostID())) {
            button8.setVisibility(View.VISIBLE);
        }
        else {
            button8.setVisibility(View.INVISIBLE);
        }
    }
    public void sendRequest(String serverAddress, int serverPort, PartyLogin partyLogin) {
        try {
            networkUtil = new NetworkUtil(serverAddress, serverPort);
            networkUtil.write(partyLogin);
            Log.i("inside","bad");
            partyLogin = (PartyLogin) networkUtil.read();
            if (partyLogin.isLoginSuccessful()) {
                clientID = partyLogin.getUserID();
                new ReadThreadClient(partyLogin.getUserName(),networkUtil,this);
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setContentView(R.layout.lobby);
                        populateLobby();
                    }
                });
            }
            else {
                Log.i("failure","work");
                try {
                    networkUtil.closeConnection();
                }
                catch (Exception e) {

                }
            }
        }
        catch (Exception e) {
            Log.i("Exception",e.toString());
        }
    }
    public void getRole(View v) {
        Log.i("role","clicked");
        if (clientID.equals(clientLobby.getHostID())) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        networkUtil.write("start");
                    }
                    catch (Exception e) {

                    }
                }
            }).start();
        }
    }
    public void gameScene() {
        HashMap<String, String> members = gameState.getMembers();
        HashMap<String, String> role = gameState.getRole();
        HashMap<String, Boolean> isAlive = gameState.getIsAlive();
        String roleText = role.get(clientID);
        String alive = "";
        if (isAlive.get(clientID)) alive = "alive";
        else alive = "dead";
        ConstraintLayout constraintLayout = findViewById(R.id.scrollGame);
        TextView roleDetails = findViewById(R.id.role_details);
        timeText = findViewById(R.id.time);
        select = findViewById(R.id.button10);
        TextView activity = findViewById(R.id.activity);
        roleDetails.setGravity(Gravity.CENTER);
        timeText.setGravity(Gravity.CENTER);
        activity.setGravity(Gravity.CENTER);
        String lobbyDet = "Role: " + roleText + "(" + alive + ")";
        timeText.setText("Time: " + time);
        String details = "";
        if (gameState.getCurrentTime().equalsIgnoreCase("day")) details = "Talk with one another and vote to hang";
        else if (gameState.getCurrentTime().equalsIgnoreCase("night")) details = "Perform your secret role";
        if (gameState.getCurrentTime().equalsIgnoreCase("init")) details = "Get introduced with one another";
        activity.setText(details);
        roleDetails.setText(lobbyDet);
        constraintLayout.removeAllViews();
        int t=0;
        for (HashMap.Entry<String, String> i : members.entrySet()) {
            TextView tv = new TextView(this);
            String name = i.getValue();
            if (!role.get(i.getKey()).equalsIgnoreCase("none")) {
                name += "(" + role.get(i.getKey()) + ")";
            }
            if (!isAlive.get(i.getKey())) name += "(dead)";
            tv.setText(name);
            tv.setId(ViewCompat.generateViewId());
            tv.setBackgroundColor(Color.parseColor("green"));
            int tabWidth = (width-300)/2;
            tv.setLayoutParams(new ConstraintLayout.LayoutParams(tabWidth,200));
            tv.setGravity(Gravity.CENTER);
            constraintLayout.addView(tv);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv.setBackgroundColor(Color.parseColor("red"));
                    previousSelected.setBackgroundColor(Color.parseColor("green"));
                    if (previousSelected == tv) {
                        victimID = "none";
                        previousSelected = empty;
                    }
                    else {
                        victimID = i.getKey();
                        previousSelected = tv;
                    }
                    Log.i("Victim: ", victimID);
                }
            });
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            if (t%2==0) {
                constraintSet.connect(tv.getId(), ConstraintSet.LEFT, R.id.scrollGame, ConstraintSet.LEFT, 100);
                constraintSet.connect(tv.getId(), ConstraintSet.TOP, R.id.scrollGame, ConstraintSet.TOP, 100 + t * 150);
            }
            else {
                constraintSet.connect(tv.getId(), ConstraintSet.LEFT, R.id.scrollGame, ConstraintSet.LEFT, 200+tabWidth);
                constraintSet.connect(tv.getId(), ConstraintSet.TOP, R.id.scrollGame, ConstraintSet.TOP, 100 + (t-1) * 150);
            }
            constraintSet.applyTo(constraintLayout);
            t++;
        }
        if (gameState.isAlreadySelected()) select.setVisibility(View.INVISIBLE);
        else {
            select.setVisibility(View.VISIBLE);
            previousSelected = empty;
            victimID = "none";
        }
    }
    public void onSelect(View v) {
        if (!victimID.equalsIgnoreCase("none")) {
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
            String finalRequest = request;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        networkUtil.write(new Select(clientID,victimID, finalRequest));
                    }
                    catch (Exception e) {

                    }
                }
            }).start();
        }
    }
    public void ending() {
        TextView gameOverText = findViewById(R.id.gameOverText);
        TextView returningLobby = findViewById(R.id.returningLobby);
        gameOverText.setGravity(Gravity.CENTER);
        returningLobby.setGravity(Gravity.CENTER);
        gameOverText.setText(gameOverMsg);
        final int[] endTime = {5};
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        returningLobby.setText("Returning to lobby in " + endTime[0]);
                    }
                });
                if (endTime[0] == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setContentView(R.layout.lobby);
                            populateLobby();
                        }
                    });
                    gameOver = false;
                    timer.cancel();
                }
                endTime[0]--;
            }
        },0,1000);
    }
}