package com.devXmachina.dxmgame.gamelogic;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;
import com.devXmachina.dxmgame.DxM_Game;
import com.devXmachina.dxmgame.GameLoader;

import java.util.Random;

public class EventManager {
    DxM_Game game;
    GameLoader gameLoader;
    private Array<GameEvent> eventList, fireEventsQueue, runningEvents, doneEvents, pendingEvents,unusedGameEvents;
    public int eventTicker;
    public Array<String> gameResults, mlResults;
    public int fireSpeed;

    public EventManager(GameLoader gameLoader, DxM_Game game) {
        this.game=game;
        this.gameLoader=gameLoader;
        runningEvents = new Array<>();
        doneEvents = new Array<>();
        fireEventsQueue = new Array<>();
        pendingEvents = new Array<>();
        unusedGameEvents = new Array<>();
        eventList = new Array<>();
        gameResults = new Array<>();
        mlResults = new Array<>();
        fireSpeed=2;
    }
    //------------------------------------------------------------------------------- PUBLIC METHODS
    public void addEvent_toMainList(GameEvent gameEvent){
        this.eventList.add(gameEvent);
        classify_gameEvent(gameEvent,false);
    }
    public void increase_fireSpeed(boolean b){
        if (b) {
            fireSpeed++;
        }else {
            fireSpeed--;
            if (fireSpeed<=0){
                fireSpeed=1;
            }
        }
    }
    public boolean must_fireEvent(){
        if((fireEventsQueue.notEmpty()&&fireEventsQueue.first().getDesktopGameApp().equals(DxM_Game.DesktopAppType.CONSOLE))){return true;}
        if(gameLoader.consoleBusy){return false;}
        else {
            Random rand = new Random(1);
            if (((fireSpeed*(eventTicker* rand.nextInt(10)) )/ (1+((pendingEvents.size +runningEvents.size )))  > 30)) {
                eventTicker = 0;
                return true;
            } else {
                eventTicker++;
                return false;
            }
        }
    }
    private void classify_gameEvent(GameEvent gameEvent, boolean isInMainList) {
        if (gameEvent.eventState.equalsIgnoreCase("NULL")) {   //----------------------------------NOT FIRED EVENT
            if (gameEvent.getDecade() == gameLoader.getCurrentDecade()) {   //---------------------FROM CURRENT DECADE
                if (gameEvent.isMainEvent()) {                            //-----------IS MAIN_EVENT
                    if(!fireEventsQueue.contains(gameEvent,true)){this.fireEventsQueue.add(gameEvent);}        //<<--------ADD TO FIRE QUEUE
                } else {
                    if(gameEvent.isActive()){
                        if(!fireEventsQueue.contains(gameEvent,true)){
                            if(gameEvent.getDesktopGameApp().equals(DxM_Game.DesktopAppType.CONSOLE)) {

                                this.fireEventsQueue.insert(0,gameEvent);
                            }else{
                                this.fireEventsQueue.add(gameEvent);
                            }

                        }       //<<--------ADD TO FIRE QUEUE
                    }
                    this.unusedGameEvents.add(gameEvent);
                }
            }
            // NULLS FROM ANOTHER DECADES GET DISCARDED
        } else {                                                          //----------------------------------ALREADY FIRED EVENT
            switch (gameEvent.getEventState()) {
                case NULL:
                    // TRIVIAL CASE
                    break;
                case PENDING:
                    if(!this.pendingEvents.contains(gameEvent,false)){this.pendingEvents.add(gameEvent);}
                    if(!isInMainList){game.getDesktop().addEvent(gameEvent,gameEvent.getDesktopGameApp());}
                    break;
                case ACCEPTED:
                    this.runningEvents.add(gameEvent);
                    if(!isInMainList){game.getDesktop().addEvent(gameEvent,gameEvent.getDesktopGameApp());}
                    break;
                case DISCARDED:
                    this.unusedGameEvents.add(gameEvent);
                    break;
                case SUCCESS:
                    this.doneEvents.add(gameEvent);
                    if(!isInMainList &&(!(gameEvent.getDesktopGameApp().equals(DxM_Game.DesktopAppType.CONSOLE)))){game.getDesktop().addEvent(gameEvent,gameEvent.getDesktopGameApp());}
                    break;
            }

        }
    }
    public void fire_nextEvent(){
        if(fireEventsQueue.notEmpty()) {
            GameEvent newGameEvent = fireEventsQueue.first(); //--------------------GET FIRST ELEM
            if(!pendingEvents.contains(newGameEvent,false)){

                newGameEvent.fireEvent_data();

                if (newGameEvent.getDesktopGameApp() != DxM_Game.DesktopAppType.CONSOLE) {
                    game.getDesktop().getApp(newGameEvent.getDesktopGameApp()).toogle_IconNotification(true); //ICON NOTIF
                }
                if (newGameEvent.mustSendNotification()) {
                    game.getDesktop().sendNotification(newGameEvent,
                            "Avis ",
                            "Hey, " + gameLoader.getUserName() + ". \n \n " + newGameEvent.getTextBlocs()[newGameEvent.getTextBlocs().length-1]);
                }
                if(newGameEvent.getEventState().equals(DxM_Game.EventState.NULL)){update_gameEvent(newGameEvent, DxM_Game.EventState.PENDING);}
                else{System.out.println("Aqui esta el &%$ err que duplica los GameEvent");}
            }else{System.out.println("DUPLICADOOOOOOOOOO");}


            game.updateManager(true);
            gameLoader.saveAllData();
        }
    }
    public String autoPlay_nextEvent(){
        String str="";
        gameLoader.consoleBusy=false;
        if(!pendingEvents.isEmpty()){
            GameEvent gameEvent = pendingEvents.first();
            if(gameEvent.isTestEvent()){
                boolean answer = getML_answer(gameEvent.getId());
                gameEvent.handle_eventAnswer(answer);
                str="\n Tasca Principal: "+gameEvent.getTitle() + "\n    Selecciona:  ";
                if(answer){str+="A";
                }else{str+="B";}
                str+=  "  "+ gameEvent.printCost(true)+"  "+ gameEvent.printCost(false)+" ";
            }else {
                if(gameEvent.getDesktopGameApp().equals(DxM_Game.DesktopAppType.CONSOLE)){
                    gameEvent.acceptEvent();
                    str = "\n loadingTask: " + gameEvent.getTitle() + "\n    COMPLETE  \n  " + gameEvent.printCost(true) + "$  " + gameEvent.printCost(false) + "* \n";

                }
                else{
                    if (gameEvent.autoPlay) {
                        gameEvent.handle_eventAnswer(true);
                        str = "\n Tasca Opcional: " + gameEvent.getTitle() + "\n    ACCEPTADA  \n  " + gameEvent.printCost(true) + "$  " + gameEvent.printCost(false) + "* \n";
                    } else {
                        gameEvent.discardEvent();
                        str = "\n Tasca Opcional: " + gameEvent.getTitle() + "\n    REBUTJADA  \n";
                    }
                }
            }
        }
        return str;
    }

    public void update_gameEvent(GameEvent gameEvent, DxM_Game.EventState futureState){
        switch(futureState){
            case NULL:
                // TRIVIAL CASE
                break;
            case PENDING:
                if(!pendingEvents.contains(gameEvent,true)) {
                    fireEventsQueue.removeValue(gameEvent, true);
                    gameEvent.setEventState(DxM_Game.EventState.PENDING);
                    pendingEvents.add(gameEvent);
                    game.getDesktop().addEvent(gameEvent, gameEvent.getDesktopGameApp());
                }
                break;
            case ACCEPTED:
                if(!runningEvents.contains(gameEvent,true)) {
                    pendingEvents.removeValue(gameEvent, true);
                    gameEvent.setEventState(DxM_Game.EventState.ACCEPTED);
                    runningEvents.add(gameEvent);
                }
                break;
            case DISCARDED:
                pendingEvents.removeValue(gameEvent,true);
                gameEvent.setEventState(DxM_Game.EventState.DISCARDED);
                break;
            case SUCCESS:
                runningEvents.removeValue(gameEvent,true);
                gameEvent.setEventState(DxM_Game.EventState.SUCCESS);

                doneEvents.add(gameEvent);
                break;
        }
    }

    public GameEvent getGameEvent_fromMainList(String id) {
        for (GameEvent event : eventList) {
            if (event.getId().equalsIgnoreCase(id)) {
                return event;
            }
        }
        return null;
    }
    //------------------------------------------------------------------------------- GETTERSnSETTERS

    public Array<GameEvent> getEventList() {
        return eventList;
    }
    public Array<GameEvent> getFireEventsQueue() {
        return fireEventsQueue;
    }
    public Array<GameEvent> getRunningEvents() {
        return runningEvents;
    }
    public Array<GameEvent> getDoneEvents() {
        return doneEvents;
    }
    public Array<GameEvent> getPendingEvents() {
        return pendingEvents;
    }

    public GameEvent find_gameEvent(String id){
        for (GameEvent gameEvent: eventList){
            if(gameEvent.getId().equalsIgnoreCase(id)){
                return gameEvent;
            }
        }
        return null;
    }

    //------------------------------------------------------------------------------- PRIVATE METHODS


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (GameEvent event : eventList) {
            stringBuilder.append(event.toString());
        }
        return stringBuilder.toString();
    }

    public void dispose(){
        runningEvents.clear();
        doneEvents.clear();
        fireEventsQueue.clear();
        pendingEvents.clear();
        unusedGameEvents.clear();
        eventList.clear();
    }

    public void activate_DecadeEvents(DxM_Game.EventDecade currentDecade) {
        for (GameEvent gameEvent : eventList) {
            if ((gameEvent.getDecade().equals(currentDecade))) {
                if(gameEvent.isMainEvent()&&gameEvent.getEventState().equals(DxM_Game.EventState.NULL)&&!gameEvent.isActive()) {
                    gameEvent.setActive(true);
                    classify_gameEvent(gameEvent, true);
                }
                if((!gameEvent.isMainEvent())&&gameEvent.isActive()&&gameEvent.getEventState().equals(DxM_Game.EventState.NULL)){
                    gameEvent.setActive(true);
                    classify_gameEvent(gameEvent, true);
                }
            }
        }
    }

    public void update_runningEvents() {
        GameEvent gameEvent;
        for(int i=0; i<runningEvents.size;i++){
            gameEvent=runningEvents.get(i);
            gameEvent.progressEvent();
        }
    }

    public void activate_nextEvent(GameEvent gameEvent,boolean isA) {
        GameEvent nextGameEvent;
        if (isA) {
            nextGameEvent = gameEvent.getNextEvent();
        } else {
            nextGameEvent = gameEvent.getLinkedEvent();
        }
        if (nextGameEvent != null) {
            nextGameEvent.setActive(true);
            classify_gameEvent(nextGameEvent, true);
        }

    }
    //------------------------------------------------------------------------------- RESULTS METHODS
    public boolean getML_answer(String id){
        ResultStruct res =  gameLoader.find_resultsAnswer(id);
        if(res!=null){
            return res.result.equalsIgnoreCase("a");
        }
        System.out.println("RESULT ERR event:"+id);
        return false;
    }


    public void save_resultsArray(Preferences preferences) {
        String str="";
        for(String res: gameResults){
            if(!res.contains("null")){
                str+=res+  "\n";
            }else{gameResults.removeValue(res,true);}
        }
        preferences.putString("GameResults",str);
        preferences.flush();
    }
}
