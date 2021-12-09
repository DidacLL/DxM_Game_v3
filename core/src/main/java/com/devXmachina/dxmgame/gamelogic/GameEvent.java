package com.devXmachina.dxmgame.gamelogic;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.devXmachina.dxmgame.DxM_Game;
import com.devXmachina.dxmgame.GameLoader;

import static com.devXmachina.dxmgame.DxM_Game.*;

public class GameEvent {
    //-----------------------------------Main attributes
    public DxM_Game game;
    GameLoader gameLoader;
    //ID FORMAT =  DECADE + App + EVENT   ex 1980MA  App= M mail/W web
    String id,eventState,textBlocs,images,nextEvent,linkedEvents,gameApp,appWebPage; //String values text blocs contains Title;subtitle;longtext1;longtext2;...;buttonText1;buttonText2 or Title;Asunto;from;longtext; button text
    Boolean mainEvent,active, testEvent,sendNotification;
    int decade,duration;
    float decadeValue,eventCost_Rep,eventCost_Mon,eventValue_Rep,eventValue_Mon,progress; // TODO event cost and value recount system
    public boolean viewed;

    public GameEvent(String data, DxM_Game game) {
        this.game=game;
        this.gameLoader = game.gameLoader;

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
                    break;
                case 1:
                    this.eventState=field;
                    if (this.eventState.equalsIgnoreCase("accepted")) {
                        this.eventState= EventState.SUCCESS.name();
                    }
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
                case 7:
                    this.appWebPage= field;
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
                    this.decade=(Integer.parseInt(field));
                    break;
                case 12:
                    this.decadeValue=(Float.parseFloat(field));
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
                    this.duration=Integer.parseInt(field);
                    break;
                default:
                    break;
            }
        }
    }
    @Override
    public String toString() {
        return (""+id+";" +eventState+";" +textBlocs+";" +images+";" +nextEvent+";" +linkedEvents+";" +gameApp+";" +appWebPage+";" +mainEvent+";" +active+";" + testEvent +";" +decade+";" +decadeValue+";" +eventCost_Mon+";" +eventCost_Rep+";" +eventValue_Mon+";" +eventValue_Rep+";" +progress+";" +sendNotification+";" +duration+"\n");
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
        return textBlocs.split("//");
    }
    public String[] getImages() {
        return images.split("//");
    }
    public GameEvent getNextEvent() {
        if (!this.nextEvent.equalsIgnoreCase("NULL")){
            GameEvent event;
            for (int i=0;i<gameLoader.getEventList().size;i++){
                event=gameLoader.getEventList().get(i);
                if (event.id.equalsIgnoreCase(nextEvent)) {
                    return event;
                }
            }
        }
        return null;
    }
    public GameEvent getLinkedEvents() {
        GameEvent event;
        for (int i=0;i<gameLoader.getEventList().size;i++){
            event=gameLoader.getEventList().get(i);
            if(event.id.equalsIgnoreCase(linkedEvents)){return event;}
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
    public Vector2 getEventCost() {
        return new Vector2(eventCost_Mon,eventCost_Rep);
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
    public Array<String> getTextBody(){
        String[] text = this.getTextBlocs();
        Array<String> retVal =new Array<String>();
        retVal.ordered=true;
        int i;
        switch (this.getDesktopGameApp()){
            case MAIL:
                i=3;
                break;
            case BROWSER:
            default:
                i=2;
                break;
        }

        for(int j =i; j<text.length;j++) {
                retVal.add(text[j]);
        }
        return retVal;
    }
    public TextureRegionDrawable getIcon(){
        return game.gameLoader.getPicture(this.getImages()[0]);
    }
    public TextureRegionDrawable getHeaderImage(){
        return game.gameLoader.getPicture(this.getImages()[1]);
    }
    public TextureRegionDrawable getSignatureImage(){
        return game.gameLoader.getPicture(this.getImages()[2]);
    }
    private boolean haveChildEvent() {
        if(this.getNextEvent()!=null){return true;}else{return false;}
    }
    //-------------------------------------------------------------------- SETTERS
    public void  setEventState(EventState state){
        this.eventState = state.name();
    }
    //-------------------------------------------------------------------- ACTIONS
    public void fireEvent() {
        //remove from queue
        if(gameLoader.getFireEventsQueue().contains(this,true)){gameLoader.getFireEventsQueue().removeValue(this,true);}
        //add to running events
        gameLoader.getPendingEvents().add(this);
        //change state
        this.eventState= EventState.PENDING.name();
        this.viewed=false;
        game.updateManager(true);
        gameLoader.saveAllData();
    }
    public void acceptEvent(){
        this.eventState = EventState.ACCEPTED.name();
        gameLoader.getPendingEvents().removeValue(this,true);
        gameLoader.getRunningEvents().add(this);
            if(duration>0){
               //FIX
            }else{
                forceCompleteEvent();
            }

        if(gameLoader.isDebugMode()){forceCompleteEvent();}
        gameLoader.saveAllData();
        game.updateManager(true);
    }
    public void discardEvent(){
        this.eventState = EventState.DISCARDED.name();
        gameLoader.getPendingEvents().removeValue(this,true);
        game.updateManager(true);
       // if(this.isTestEvent()){gameLoader.addResult(this.getId(),false);}
    }
    public void progressEvent(){
        this.progress += 0.1f;
        if(this.progress >= duration){
            completeEvent();
        }
        game.updateManager(false);
    }
    public void completeEvent(){
        this.eventState = EventState.SUCCESS.name();
        gameLoader.getRunningEvents().removeValue(this,true);
        gameLoader.getDoneEvents().add(this);
        if(this.sendNotification){game.getDesktop().sendNotification(this,"Tasca Completada", this.getTitle()+"completada\n \n ");}

        GameEvent nextEvent = gameLoader.getGameEvent(this.nextEvent);
        if(nextEvent!=null){
            if(nextEvent.duration==0) {
                gameLoader.getFireEventsQueue().insert(0,getNextEvent());
                game.getDesktop().forceTimeTick();
            }else{
                gameLoader.getFireEventsQueue().add(getNextEvent());

            }
        }
//        game.getDesktop().getApp(this.getDesktopGameApp()).updateWindow(this.getAppWebPage());
        gameLoader.increaseDecadeProgress(this.decadeValue);
        gameLoader.setSaveGame(true);
        game.updateManager(true);
        gameLoader.saveAllData();

    }
    public void forceCompleteEvent() {
        this.progress=1;
        completeEvent();
    }
    //-------------------------------------------------------------------- XXXXXX
    public void dispose() {
        //TODO
    }
}
