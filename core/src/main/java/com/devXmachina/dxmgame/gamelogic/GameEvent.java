package com.devXmachina.dxmgame.gamelogic;

import com.badlogic.gdx.math.Vector2;
import com.devXmachina.dxmgame.DxM_Game;
import com.devXmachina.dxmgame.GameLoader;

import static com.devXmachina.dxmgame.DxM_Game.*;
import static java.lang.Integer.parseInt;

public class GameEvent {
    //-----------------------------------Main attributes
    public DxM_Game game;
    public boolean autoPlay;
    public boolean answer_isA;
    GameLoader gameLoader;
    //ID FORMAT =  DECADE + App + EVENT   ex 1980MA  App= M mail/W web
    String id,eventState,textBlocs,images,nextEvent,linkedEvents,gameApp,appWebPage; //String values text blocs contains Title;subtitle;longtext1;longtext2;...;buttonText1;buttonText2 or Title;Asunto;from;longtext; button text
    Boolean mainEvent,active, testEvent,sendNotification;
    int decade;
    public int duration;
    float decadeValue,eventCost_Rep,eventCost_Mon,eventValue_Rep,eventValue_Mon,progress; // TODO event cost and value recount system
    public boolean viewed;
    private EventManager eventManager;
    public int ml;
    public String answer;

    public GameEvent(String data, DxM_Game game) {
        this.game=game;
        this.gameLoader = game.gameLoader;
        this.eventManager = gameLoader.eventManager;
        parseEventData(data);
    }
    //-------------------------------------------------------------------- CONFIG
    private void parseEventData(String data) {

        String[] text = data.split(";");
        String field;
        for(int i=0;i<text.length;i++){
            field=text[i];
            switch(i){
                case 0:
                    this.id=field;
//                    System.out.println(id);
                    break;
                case 1:
                    this.eventState=field;
//                    if (this.eventState.equalsIgnoreCase("accepted")) {
//                        this.eventState= EventState.SUCCESS.name();
//                    }
                    break;
                case 2:
                    this.textBlocs=field;
                    break;
                case 3:
                    this.images=field;
                    break;
                case 4:
                    this.nextEvent=field;
                    break;
                case 5:
                    this.linkedEvents=field;
                    break;
                case 6:
                    this.gameApp=field;
                    break;
                case 7:
                    this.appWebPage= field;
                    break;
                case 8:
                    this.mainEvent= (Boolean.parseBoolean(field));
                    break;
                case 9:
                    this.active= (Boolean.parseBoolean(field));
                    break;
                case 10:
                    this.testEvent = (Boolean.parseBoolean(field));
                    break;
                case 11:
                    this.decade=(parseInt(field));
                    break;
                case 12:
                    this.decadeValue=(Float.parseFloat(field));
//                    System.out.println(decade);
                    break;
                case 13:
                    this.eventCost_Mon=(Float.parseFloat(field));
                    break;
                case 14:
                    this.eventCost_Rep=(Float.parseFloat(field));
                    break;
                case 15:
                    this.eventValue_Mon=(Float.parseFloat(field));
                    break;
                case 16:
                    this.eventValue_Rep=(Float.parseFloat(field));
                    break;
                case 17:
                    this.progress=(Float.parseFloat(field));
                    break;
                case 18:
                    this.sendNotification=(Boolean.parseBoolean(field));
                    break;
                case 19:
                    this.duration= parseInt(field);
                    break;
                case 20:
                    this.autoPlay=Boolean.parseBoolean(field);
//                    System.out.println(autoPlay);
                    break;
                case 21:
                    this.ml=Integer.parseInt(field);
                    break;
                case 22:
                    this.answer=field;
                    break;
                default:
                    break;
            }
        }
    }
    @Override
    public String toString() {
        return (""+id+";"
                +eventState+";" +
                textBlocs+";" +
                images+";" +
                nextEvent+";" +
                linkedEvents+";" +
                gameApp+";" +
                appWebPage+";" +
                mainEvent+";" +
                active+";" +
                testEvent +";" +
                decade+";" +
                decadeValue+";" +
                eventCost_Mon+";" +
                eventCost_Rep+";" +
                eventValue_Mon+";" +
                eventValue_Rep+";" +
                progress+";" +
                sendNotification+";" +
                duration+";"+
                autoPlay+";" +
                ml+";"+
                answer+"\n");
    }
    //-------------------------------------------------------------------- GETTERS
    public String getId() {
        return id;
    }
    public EventState getEventState() {
        if(eventState.equalsIgnoreCase(EventState.ACCEPTED.name()))
            return EventState.ACCEPTED;
        else if(eventState.equalsIgnoreCase(EventState.DISCARDED.name()))
            return EventState.DISCARDED;
        else if(eventState.equalsIgnoreCase(EventState.PENDING.name()))
            return EventState.PENDING;
        else if(eventState.equalsIgnoreCase(EventState.SUCCESS.name()))
            return EventState.SUCCESS;
        else
            return EventState.NULL;
    }
    public String[] getTextBlocs() {
        return textBlocs.replace("&","\n").replace("#",gameLoader.getUserName()).split("//");
    }
    public String[] getImages() {
        return images.split("//");
    }
    public GameEvent getNextEvent() {
        if (!this.nextEvent.equalsIgnoreCase("NULL")){
            return eventManager.getGameEvent_fromMainList(this.nextEvent);
        }
        return null;
    }
    public GameEvent getLinkedEvent() {
        if (!(this.linkedEvents.equalsIgnoreCase("NULL"))){
            return eventManager.getGameEvent_fromMainList(this.linkedEvents);
        }
        return null;
    }

    public DesktopAppType getDesktopGameApp() {
        if(this.gameApp.equalsIgnoreCase("MAIL")){return DesktopAppType.MAIL;}
        if(this.gameApp.equalsIgnoreCase("MANAGER")){return DesktopAppType.MANAGER;}
        if(this.gameApp.equalsIgnoreCase("BROWSER")){return DesktopAppType.BROWSER;}
        if(this.gameApp.equalsIgnoreCase("CONSOLE")){return DesktopAppType.CONSOLE;}
        if(this.gameApp.equalsIgnoreCase("NOTEPAD")){return DesktopAppType.NOTEPAD;}
        else {return null;}
    }
    public BrowserURL getAppWebPage() {
        return BrowserURL.valueOf(appWebPage);
    }
    public Boolean isMainEvent() {
        return mainEvent;
    }
    public Boolean isActive() {
        return active;
    }
    public void setActive(Boolean active) {
        this.active = active;
    }
    public Boolean isTestEvent() {
        return testEvent;
    }
    public Boolean mustSendNotification() {
        return sendNotification;
    }
    public float getDecadeValue() {
        return decadeValue;
    }
    public EventDecade getDecade() {
        if (decade < 1980)
            return EventDecade.SEVENTIES;
        else if (decade < 1990)
            return EventDecade.EIGHTIES;
        else if (decade < 2000)
            return EventDecade.NINETIES;
        else if (decade < 2010)
            return EventDecade.NOUGHTIES;
        else if (decade < 2020)
            return EventDecade.TWENTY_TENS;
        else
            return EventDecade.TWENTY_TWENTIES;
    }
    public int getEventCost(boolean money) {
        return getCost(money);
    }
    public Vector2 getEventValue(){
        return new Vector2(eventValue_Mon,eventValue_Rep);
    }
    public float getProgress() {
        return progress;
    }
    public String getTitle(){
        return this.getTextBlocs()[0];
    }
    private boolean haveChildEvent() {
        return this.getNextEvent() != null;
    }
    //-------------------------------------------------------------------- SETTERS
    public void  setEventState(EventState state){
        this.eventState = state.name();
    }
    //-------------------------------------------------------------------- ACTIONS
    public void fireEvent_data() {
        if(this.eventState.equalsIgnoreCase("NULL")) {
            this.viewed = false;
        }
        if(this.getDesktopGameApp().equals(DesktopAppType.CONSOLE)){
            answer_isA=true;
        }
    }
    public void acceptEvent(){
        this.eventManager.update_gameEvent(this,EventState.ACCEPTED);
            if(duration>0){
                if(gameLoader.isDebugMode()||gameLoader.isAutoPlay()){forceCompleteEvent();}
            }else{
                forceCompleteEvent();
            }
            if(this.isDoubleAnswer()) {
                if (this.nextEvent.equalsIgnoreCase(this.linkedEvents)&&this.answer_isA) {
                    gameLoader.updateCurrentMoney(eventCost_Mon, false);
                    gameLoader.updateCurrentReputation(eventCost_Rep, false);
                }
            }else{

                gameLoader.updateCurrentMoney(eventCost_Mon, false);
                gameLoader.updateCurrentReputation(eventCost_Rep, false);
            }
        gameLoader.saveAllData();
        game.updateManager(true);
    }
    public void discardEvent(){
        this.eventManager.update_gameEvent(this,EventState.DISCARDED);
        game.updateManager(true);
    }
    public void progressEvent(){
        this.progress += 0.1f;
        if(this.progress >= duration){
            if(this.getDesktopGameApp().equals(DesktopAppType.CONSOLE)) {
                eventManager.activate_nextEvent(this, true);
            }
            completeEvent();
        }
        else {

            if(gameLoader.isAutoPlay()){this.progress=this.duration-0.1f;}
        }
        game.updateManager(false);
    }
    public void completeEvent(){
        eventManager.update_gameEvent(this,EventState.SUCCESS);
        if(this.sendNotification){game.getDesktop().sendNotification(this,"Tasca Completada", this.getTitle()+"\n  Beneficis:       \n"+ printValue(true)+" "+printValue(false) + " \n ");}
        if(!this.gameApp.equalsIgnoreCase("CONSOLE")){game.getDesktop().getApp(this.getDesktopGameApp()).updateWindow();}
        gameLoader.increaseDecadeProgress(this.decadeValue);
        if(this.isDoubleAnswer()) {
            if (this.nextEvent.equalsIgnoreCase(this.linkedEvents)&&this.answer_isA) {
                gameLoader.updateCurrentMoney(eventValue_Mon, true);
                gameLoader.updateCurrentReputation(eventValue_Rep, true);
            }
        }else{

            gameLoader.updateCurrentMoney(eventValue_Mon, true);
            gameLoader.updateCurrentReputation(eventValue_Rep, true);
        }
        gameLoader.setSaveGame(true);
        game.updateManager(true);
        gameLoader.saveAllData();

    }
    public void forceCompleteEvent() {
        this.progress=duration-0.1f;
    }
    //-------------------------------------------------------------------- XXXXXX
    public void dispose() {
        //TODO
    }

    public String printCost(boolean money) {
        int variation;
        if(money) {
            if (this.eventCost_Mon > 10) {
                variation = (int) this.eventCost_Mon;
            } else {
                variation = (int) (gameLoader.getCurrentMoney() * this.eventCost_Mon);
            }
            return "-"+variation+" $";
        }
        else{
            if (this.eventCost_Rep > 10) {
                variation = (int) this.eventCost_Rep;
            } else {
                variation = (int) (gameLoader.getCurrentReputation() * this.eventCost_Rep);
            }
            return "-"+variation+" *";
        }
    }
    public int getCost(boolean money) {
        int variation;
        if(money) {
            if (this.eventCost_Mon > 10) {
                variation = (int) this.eventCost_Mon;
            } else {
                variation = (int) (gameLoader.getCurrentMoney() * this.eventCost_Mon);
            }
            return variation;
        }
        else{
            if (this.eventCost_Rep > 10) {
                variation = (int) this.eventCost_Rep;
            } else {
                variation = (int) (gameLoader.getCurrentReputation() * this.eventCost_Rep);
            }
            return variation;
        }
    }
    public String printValue(boolean money) {
        int variation;
        if(money) {
            if (this.eventValue_Mon > 10) {
                variation = (int) this.eventValue_Mon;
            } else {
                variation = (int) (gameLoader.getCurrentMoney() * this.eventValue_Mon);
            }
            return "+"+variation+" $";
        }
        else{
            if (this.eventValue_Rep > 10) {
                variation = (int) this.eventValue_Rep;
            } else {
                variation = (int) (gameLoader.getCurrentReputation() * this.eventValue_Rep);
            }
            return "+"+variation+" *";
        }
    }

    public boolean isDoubleAnswer(){
        return this.getLinkedEvent() != null;
    }
    public boolean isVirus(){
        return this.nextEvent.equalsIgnoreCase("VIRUS");
    }
    public boolean handle_eventAnswer(boolean isA){
        if(isA){this.answer="a";}else{this.answer="b";}
        if(isTestEvent()){
            ResultStruct res =  gameLoader.find_resultsAnswer(this.id);
            if(res!=null){
                if(isA){res.playerResult="a";}else{res.playerResult="b";}
            }else{
                System.out.println("ERR L396 GAMEEVENT");
            }
        }
        if(isVirus()){   //IS A VIRUS
            if(isA){
                game.executeVirus();
                acceptEvent();
                eventManager.activate_nextEvent(this,false);
                return true;
            }else{
                discardEvent();
                return false;
            }
        }else{          //NORMAL EVENT
            if(isDoubleAnswer()){      //----------------------------DOUBLE ANSWER
                this.answer_isA=isA;
                acceptEvent();
                eventManager.activate_nextEvent(this,isA);
                return true;
            }else{                          //-----------------------SIMPLE ANSWER
                if(isA){
                    acceptEvent();
                    eventManager.activate_nextEvent(this, true);
                }
                else {
                    discardEvent();
                }

                return isA;
            }

        }
    }
}
