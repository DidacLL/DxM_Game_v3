package com.devXmachina.dxmgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.devXmachina.dxmgame.gamelogic.GameEvent;
import com.devXmachina.dxmgame.gamelogic.SuperPool;

public class GameLoader {
    private static final String INIT_DATA = "1970CONSOLE002;NULL;Welcome to your brand new personal computer!!//  //   // Check the text file below// to view the game information// and how to play//  // starting game? // // ;NULL;1970CONSOLE003;NULL;CONSOLE;CONSOLE;true;false;false;1970;1;0;0;100;100;0;false;1\n" +
            "1970CONSOLE003;NULL;Setting regional config?// languaje: CATALA  current_date: 4/20/1978_//  Estem configurant els ultims detalls... // per jugar nomes has d'estar //atent a les notificacions  que aniran apareixent-->>;NULL;1970CONSOLE004;NULL;CONSOLE;CONSOLE;false;false;false;1970;1;0;0;100;100;0;false;0\n" +
            "1970CONSOLE004;NULL;Exacte! //A traves d'aquestes notificacions podras //coneixer les noves oportunitas// i decisions per avancar en la historia;NULL;1970CONSOLE005;NULL;CONSOLE;CONSOLE;false;false;false;1970;1;0;0;100;100;0;true;0\n" +
            "1970CONSOLE005;NULL;\"Consulta el fitxer de text per mes detalls// o directament prova de navegar per les diferents aplicacions.// A l'administrador de tasques hi tens tota la informaci¢//de la partida actual i de la IA // tamb\u0082 hi pots activar el mode \"\"DEBUG\"\" o incl£s//activar el mode de joc autom\u0085tic guiat per IA \";NULL;1970MAIL006;NULL;CONSOLE;CONSOLE;false;false;false;1970;0;0;0;100;100;0;false;0\n" +
            "1970MAIL006;NULL;GAME developer//Aquest es el teu primer mail//Ey! Benvingut, aquest es el teu primer correu, amb correus com aquests podr\u0085s acceptar o rebutjar projectes, o b\u0082 escollir entre dues opcions i segons el que decideixis, la historia evolucionar\u0085 de manera diferent.//Entesos//Spam//NOU CORREU!;MAIL//MAILB;NULL;NULL;MAIL;MAIL;false;false;false;1970;1;0;0;0;0;0;true;3";

    private DxM_Game game;
    private Preferences preferences;
    private Array<GameEvent> unusedGameEvents;
    private AssetManager assetManager;
    public SuperPool superPool;
    protected DxM_Game.EventDecade currentDecade;
    private float decadeProgress;
    protected int currentMoney, currentReputation;

    private String userName;
    private String gameResults, testResults;
    private Array<GameEvent> eventList, fireEventsQueue, runningEvents, doneEvents, pendingEvents;
    public boolean savedData;
    private Skin desktopSkin, menuSkin, biosSkin;
    private boolean autoPlay, saveGame, debugMode;
    private boolean sound;

    public GameLoader(DxM_Game game) {
        this.game = game;
        preferences = Gdx.app.getPreferences("IAGame_Preferences");
        assetManager = new AssetManager();
        menuSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        desktopSkin = new Skin(Gdx.files.internal("os8ui/OS Eight.json"));
        biosSkin = new Skin(Gdx.files.internal("commodore64ui/uiskin.json"));
        getSavedData();
        createArrays();
        loadImages();
        loadSounds();
        superPool = new SuperPool(this, game);
    }

    //-------------------------------------------------------------------------CONSTRUCT
    private void createArrays() {
        unusedGameEvents = new Array<GameEvent>();
        eventList = new Array<GameEvent>();
        pendingEvents = new Array<GameEvent>();
        doneEvents = new Array<GameEvent>();
        fireEventsQueue = new Array<GameEvent>();
        runningEvents = new Array<GameEvent>();
    }
    private void loadImages() {
    }
    private void loadSounds() {
    }
    //-------------------------------------------------------------------------START
    public void start() {
        load_EventsData();
    }
    private void load_EventsData() {
        String data = "";
        if (!savedData) {
            data = INIT_DATA;
            autoPlay = false;
            markTo_SaveData();
        } else {
            data = preferences.getString("GameEvent_Data");
        }
        String[] EventArray = data.split("\\r?\\n");
        for (String dataLine : EventArray) {
            if (dataLine.contains("//")) {
                GameEvent event = new GameEvent(dataLine, this.game);
                event.viewed=true;
                eventList.add(event);
                parse_eventData(event);
            }
        }

        if(!savedData){loadDecadeEvents();}
        game.updateManager(true);

    }
    public void parse_eventData(GameEvent event) {
        if (event.isActive()) {
            if (!event.getEventState().equals(DxM_Game.EventState.DISCARDED)) {
                switch (event.getEventState()) {
                    case NULL:
                        this.fireEventsQueue.add(event);
                        break;
                    case PENDING:
                        this.pendingEvents.add(event);
                        game.getDesktop().addEvent(event, event.getDesktopGameApp());
                        break;
                    case ACCEPTED:
                        event.setEventState(DxM_Game.EventState.SUCCESS);
                        this.doneEvents.add(event);
                        game.getDesktop().addEvent(event, event.getDesktopGameApp());
                        if(event.mustSendNotification()){game.getDesktop().sendNotification(event,"Task complete!", "Task "+event.getTitle()+"\n was successfully done");}
                        increaseDecadeProgress(event.getDecadeValue());
                        setSaveGame(true);
                        break;
                    case SUCCESS:
                        game.getDesktop().addEvent(event, event.getDesktopGameApp());
                        this.doneEvents.add(event);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + event.getEventState());
                }

            }
        } else {
            if (event.getDecade().equals(currentDecade)) {
                unusedGameEvents.add(event);
            }
        }


    }
    //-------------------------------------------------------------------------DATA & PREFERENCES
    private void getSavedData() {
        if (preferences.contains("GameEvent_Data")) {
            savedData = true;
            currentMoney = preferences.getInteger("currentMoney");
            currentDecade = DxM_Game.EventDecade.valueOf(preferences.getString("currentDecade"));
            decadeProgress = preferences.getFloat("decadeProgress");
            currentReputation = preferences.getInteger("currentReputation");
            gameResults = preferences.getString("gameResults");
            userName = preferences.getString("userName");
            testResults = preferences.getString("testResults");
            sound = preferences.getBoolean("sound");
            debugMode = preferences.getBoolean("debug");
        } else {
            savedData = false;
            currentMoney = 0;
            currentDecade = DxM_Game.EventDecade.SEVENTIES;
            decadeProgress = 0.f;
            currentReputation = 0;
            gameResults = "";
            userName = "";
            testResults = "";
        }
    }
    public void saveAllData() {
        StringBuilder stringBuilder = new StringBuilder();
        for (GameEvent event : eventList) {
            stringBuilder.append(event.toString());
        }
        preferences.putString("GameEvent_Data", stringBuilder.toString());
        preferences.putInteger("currentMoney", currentMoney);
        preferences.putString("currentDecade", currentDecade.name());
        preferences.putFloat("decadeProgress", decadeProgress);
        preferences.putInteger("currentReputation", currentReputation);
        preferences.putString("results", gameResults);
        preferences.putString("userName", userName);
        preferences.putString("testResults", testResults);
        preferences.putBoolean("sound", sound);
        preferences.putBoolean("debug", debugMode);
        preferences.flush();
        saveGame = false;
    }
    public void clearPreferences() {
        preferences.clear();
        preferences.flush();
    }
    public void markTo_SaveData() {
        saveGame = true;
    }
    //-------------------------------------------------------------------------GAME EVENTS
    public void fire_gameEvent() {
        if(fireEventsQueue.notEmpty()) {
            GameEvent newGameEvent = fireEventsQueue.get(0);
            newGameEvent.fireEvent();
            System.out.println("fired event : "+ newGameEvent.getId());
            game.getDesktop().addEvent(newGameEvent, newGameEvent.getDesktopGameApp());
            saveGame = true;
            if(newGameEvent.getDesktopGameApp()!= DxM_Game.DesktopAppType.CONSOLE){game.getDesktop().getApp(newGameEvent.getDesktopGameApp()).toogle_IconNotification(true);}
            if (newGameEvent.mustSendNotification()) {
                game.getDesktop().sendNotification(newGameEvent, "New Task!!!", "Hey, " + userName + ". \n Take an eye on\n this history event:");
            }
        }
    }
    public void loadDecadeEvents() {
        DxM_Game.EventDecade checkDecade;
        if (currentDecade == null) {
            currentDecade = DxM_Game.EventDecade.SEVENTIES;
        }
        checkDecade=currentDecade;
        for (GameEvent gameEvent : eventList) {
            if ((gameEvent.getDecade().equals(checkDecade) &&
                    (gameEvent.isMainEvent() && (!(gameEvent.isActive())) || (!gameEvent.isMainEvent()) && gameEvent.isActive()))) {
                gameEvent.setActive(true);
                parse_eventData(gameEvent);
            }

        }

    }
    public GameEvent getGameEvent(String id) {
        for (GameEvent event : eventList) {
            if (event.getId().equalsIgnoreCase(id)) {
                return event;
            }
        }
        return null;
    }
    //-------------------------------------------------------------------------GAME PROGRESS
    public void updateGameData() {
        if (getDecadeProgress() >= 4) {
            nextDecade();
        }
        else{
            if(autoPlay){autoPlay_currentDecade();}
            else{
                GameEvent gameEvent;
                for(int i=0; i<runningEvents.size;i++){
                    gameEvent=runningEvents.get(i);
                    gameEvent.progressEvent();
                }
                fire_gameEvent();
            }
        }
        //TODO
    }
    public void nextDecade() {
        switch (currentDecade) {
            case SEVENTIES:
                currentDecade = DxM_Game.EventDecade.EIGHTIES;
                autoPlay = false;
                decadeProgress=0;
                loadDecadeEvents();
                markTo_SaveData();
                break;
            case EIGHTIES:
                currentDecade = DxM_Game.EventDecade.NINETIES;
                autoPlay = false;
                decadeProgress=0;
                loadDecadeEvents();
                markTo_SaveData();
                break;
            case NINETIES:
                currentDecade = DxM_Game.EventDecade.NOUGHTIES;
                autoPlay = false;
                decadeProgress=0;
                loadDecadeEvents();
                markTo_SaveData();
                break;
            case NOUGHTIES:
                currentDecade = DxM_Game.EventDecade.TWENTY_TENS;
                autoPlay = false;
                decadeProgress=0;
                loadDecadeEvents();
                markTo_SaveData();
                break;
            case TWENTY_TENS:
                currentDecade = DxM_Game.EventDecade.TWENTY_TWENTIES;
                autoPlay = false;
                decadeProgress=0;
                loadDecadeEvents();
                markTo_SaveData();
                break;
            case TWENTY_TWENTIES:
                autoPlay = false;
                decadeProgress=0;
                markTo_SaveData();
                endGame();
                break;
            default:
                break;
        }
        game.desktop.getBrowserWindow().setSelectBox_options();
    }
    //Called when you end the last event. it should show your results
    private void endGame() {
        //TODO
    }
    public void autoPlay_currentDecade() {
    }
    //-------------------------------------------------------------------------Getters_&_Setters
    public Array<GameEvent> getUnusedGameEvents() {
        return unusedGameEvents;
    }
    public DxM_Game.EventDecade getCurrentDecade() {
        return currentDecade;
    }
    public float getDecadeProgress() {
        return decadeProgress;
    }
    public int getCurrentMoney() {
        return currentMoney;
    }
    public void updateCurrentMoney(float value, boolean increase) {
        if (increase) {
            this.currentMoney += (int) currentMoney * value;
        } else {
            this.currentMoney -= (int) currentMoney * value;
        }
    }
    public int getCurrentReputation() {
        return currentReputation;
    }
    public void updateCurrentReputation(float value, boolean increase) {
        if (increase) {
            this.currentReputation += (int) currentReputation * value;
        } else {
            this.currentReputation -= (int) currentReputation * value;
        }
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getGameResults() {
        return gameResults;
    }
    public void addGameResults(String gameResults) {
        this.gameResults = gameResults;
    }
    public String getTestResults() {
        return testResults;
    }
    public void addTestResults(String testResults) {
        this.testResults += testResults;
    }
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
    public TextureRegionDrawable getPicture(String name) {
        return new TextureRegionDrawable(new Texture(Gdx.files.internal("img/" + name + ".png")));
        //TODO change that loadAtRunTime method to assetloader by atlas
    }
    public Skin getSkin(String name) {
        if (name.equalsIgnoreCase("DESKTOP")) {
            return this.desktopSkin;
        } else if (name.equalsIgnoreCase("BIOS")) {
            return this.biosSkin;
        } else if(name.equalsIgnoreCase("BIOS2")){
            return new Skin(Gdx.files.internal("commodore64ui/uiskin.json"));
        }else {
            return this.menuSkin;
        }
    }
    public boolean isDebugMode() {
        return debugMode;
    }
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
    public boolean isSaveGame() {
        return saveGame;
    }
    public void setSaveGame(boolean saveGame) {
        this.saveGame = saveGame;
    }
    public boolean isAutoPlay() {
        return autoPlay;
    }
    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }
    public void increaseDecadeProgress(float decadeValue) {
        this.decadeProgress += decadeValue;
    }
    public boolean isSoundActive() {
        return this.sound;
    }
    public void setSound(boolean active) {
        this.sound = active;
    }
    public void addResult(String id, boolean result) {
        addGameResults(id + ":" + Boolean.toString(result) + "\n");
        markTo_SaveData();
        //TODO handle A/B RESULTS
    }

    public void dispose() {
        //TODO
    }
}

