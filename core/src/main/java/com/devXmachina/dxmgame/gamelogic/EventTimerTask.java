//package com.devXmachina.dxmgame.gamelogic;
//
//import com.badlogic.gdx.utils.Timer;
//import com.devXmachina.dxmgame.DxM_Game;
//import com.devXmachina.dxmgame.GameLoader;
//
//public class EventTimerTask extends Timer.Task {
//    private final DxM_Game game;
//    private final GameLoader gameLoader;
//    private final boolean eventMode;
//
//    public EventTimerTask(DxM_Game game, GameLoader gameLoader,boolean eventMode) {
//        super();
//        this.game = game;
//        this.gameLoader = gameLoader;
//        this.eventMode = eventMode;
//    }
//
//    @Override
//    public void run() {
//        if (eventMode) {
//            if(gameLoader.getRunningEvents().size<4){
//                System.out.println("eventTask executed");
//                gameLoader.fire_gameEvent();
//            }
//        }else{
//            if(gameLoader.isSaveGame()){gameLoader.saveAllData();}
//
//            System.out.println("saveTask executed");
//            gameLoader.schedule_eventsCheck();
//        }
//    }
//}
