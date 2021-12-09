package com.devXmachina.dxmgame.gamelogic;

public class PlayerData {
    public int phase;
    public String playerName = null;

    public void dispose() {
        phase=0;
        playerName=null;
    }
}
