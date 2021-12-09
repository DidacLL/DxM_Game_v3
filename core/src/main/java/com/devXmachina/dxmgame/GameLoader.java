package com.devXmachina.dxmgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.devXmachina.dxmgame.desktopActors.ManagerWindow;
import com.devXmachina.dxmgame.gamelogic.*;

public class GameLoader {
    public DxM_Game game;
    private final Preferences preferences;
    public SuperPool superPool;
    protected DxM_Game.EventDecade currentDecade;
    private float decadeProgress;
    protected int currentMoney, currentReputation;

    private String userName;
    public boolean savedData;
    private final Skin desktopSkin, menuSkin, biosSkin, blueDeskSkin;
    private boolean autoPlay, saveGame, debugMode, sound;
    public boolean consoleBusy, endGame;
    public EventManager eventManager;
    public String[] wiki_txt, test_txt;
    public String readMe_txt;
    //--------------------------------------------------------------------Machine_Learning
    public String[] testAnswer;
    public Array<ResultStruct> resultStructArray;
    private String testFinalResults;

    public GameLoader(DxM_Game game) {
        this.game = game;
        preferences = Gdx.app.getPreferences("IAGame_Preferences");
        menuSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        desktopSkin = new Skin(Gdx.files.internal("os8ui/OS Eight.json"));
        blueDeskSkin = new Skin(Gdx.files.internal("os8ui/OS Eight.json"));
        biosSkin = new Skin(Gdx.files.internal("commodore64ui/uiskin.json"));
        getSavedData();
        createArrays();
        loadImages();
        loadSounds();
        superPool = new SuperPool(this, game);
//        testWeka();
    }

    //-------------------------------------------------------------------------CONSTRUCT
    private void createArrays() {
        eventManager = new EventManager(this, game);
        wiki_txt = (Gdx.files.internal("txt/wiki.txt").readString()).split("\\r?\\n");
        test_txt = (Gdx.files.internal("txt/test.txt").readString()).split("\\r?\\n");
        readMe_txt = (Gdx.files.internal("txt/readMe.txt").readString());
        resultStructArray = new Array<>();
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
//            data=INIT_DATA;
            data = (Gdx.files.internal("txt/EventGameData2.txt").readString());
            autoPlay = false;
        } else {
            data = preferences.getString("GameEvent_Data");
        }
        String[] EventArray = data.split("\\r?\\n");
        TreeScript treeTest= new TreeScript();
        for (String dataLine : EventArray) {
            if (dataLine.contains("//")) {
                GameEvent event = new GameEvent(dataLine, this.game);
                event.viewed = true;
                eventManager.addEvent_toMainList(event);
                if (event.isTestEvent()) {
                        if(event.ml>0){
                            ResultStruct aux = treeTest.get_mlAnswer(this,event);
                            if(event.answer!=null){aux.playerResult=event.answer;}
                            resultStructArray.add(aux);
                        }

                        eventManager.gameResults.add(event.getId() + ":N");

                }
            }
        }
        if (savedData) {
            String[] strArr = preferences.getString("GameResults").split("\n");
            for (String eResult : strArr) {
                eventManager.gameResults.add(eResult);
            }
        }
        game.updateManager(true);

    }


    //-------------------------------------------------------------------------DATA & PREFERENCES
    private void getSavedData() {
        if (preferences.contains("GameEvent_Data")) {
            savedData = true;
            currentMoney = preferences.getInteger("currentMoney");
            currentDecade = DxM_Game.EventDecade.valueOf(preferences.getString("currentDecade"));
            decadeProgress = preferences.getFloat("decadeProgress");
            currentReputation = preferences.getInteger("currentReputation");
            userName = preferences.getString("userName");

            testFinalResults = preferences.getString("testResults");
            testAnswer = testFinalResults.split("\n");


            sound = preferences.getBoolean("sound");
            debugMode = preferences.getBoolean("debug");
        } else {
            savedData = false;
            currentMoney = 0;
            currentDecade = DxM_Game.EventDecade.SEVENTIES;
            decadeProgress = 0.f;
            currentReputation = 100;
            testFinalResults="";
            userName = "";
        }
    }

    public void saveAllData() {
        preferences.putString("GameEvent_Data", eventManager.toString());
        preferences.putInteger("currentMoney", currentMoney);
        preferences.putString("currentDecade", currentDecade.name());
        preferences.putFloat("decadeProgress", decadeProgress);
        preferences.putInteger("currentReputation", currentReputation);
        preferences.putString("userName", userName);
        preferences.putString("testResults", testFinalResults);
        preferences.putBoolean("sound", sound);
        preferences.putBoolean("debug", debugMode);
        preferences.flush();
        saveGame = false;
        eventManager.save_resultsArray(preferences);
    }

    public void clearPreferences() {
        preferences.clear();
        preferences.flush();
    }

    public void markTo_SaveData() {
        saveGame = true;
    }

    //-------------------------------------------------------------------------GAME EVENTS
    public void loadDecadeEvents() {
        eventManager.activate_DecadeEvents(currentDecade);
        saveAllData();
    }

    //-------------------------------------------------------------------------GAME PROGRESS
    public void updateGameData() {
        if (getDecadeProgress() >= 4) {
            decadeProgress = 0.f;
            nextDecade();
        } else {
            eventManager.update_runningEvents();
            if (eventManager.must_fireEvent()) {
                eventManager.eventTicker = 11;
            }
            if (eventManager.must_fireEvent()) {
                eventManager.fire_nextEvent();
            }

        }
        //TODO
    }

    //    private void clean_FireEventsQueue() {
//        for(int i=0;i<fireEventsQueue.size;i++){
//            if(!(fireEventsQueue.get(i).getDecade().equals(currentDecade))){
//                fireEventsQueue.removeIndex(i);
//            }
//        }
//    }
    public void nextDecade() {
        switch (currentDecade) {
            case SEVENTIES:
                this.currentDecade = DxM_Game.EventDecade.EIGHTIES;
                autoPlay = false;
                loadDecadeEvents();
                markTo_SaveData();
                break;
            case EIGHTIES:
                this.currentDecade = DxM_Game.EventDecade.NINETIES;
                autoPlay = false;
                loadDecadeEvents();
                markTo_SaveData();
                break;
            case NINETIES:
                this.currentDecade = DxM_Game.EventDecade.NOUGHTIES;
                autoPlay = false;
                loadDecadeEvents();
                markTo_SaveData();
                break;
            case NOUGHTIES:
                this.currentDecade = DxM_Game.EventDecade.TWENTY_TENS;
                autoPlay = false;
                loadDecadeEvents();
                markTo_SaveData();
                break;
            case TWENTY_TENS:
                this.currentDecade = DxM_Game.EventDecade.TWENTY_TWENTIES;
                autoPlay = false;
                loadDecadeEvents();
                markTo_SaveData();
                break;
            case TWENTY_TWENTIES:
                autoPlay = false;
                markTo_SaveData();
                endGame();
                break;
            default:
                break;
        }
//        clean_FireEventsQueue();
        game.getDesktop().getBrowserWindow().setSelectBox_options();
    }

    //Called when you end the last event. it should show your results
    public void endGame() {
        endGame = true;
        game.setGameState(DxM_Game.GameState.MENU_MODE);
        Dialog dialog = new Dialog(" GAME COMPLETE! ", biosSkin, "dialog-modal") {
            protected void result(Object object) {
                if (object.equals(true)) {
                    clearPreferences();
                    game.reset();
                }
            }
        };
        dialog.getTitleTable().reset();
        Label label = new Label("  CONGRATULATIONS!  ", biosSkin, "title");
        label.setAlignment(Align.bottom);
        dialog.getTitleTable().add(label).expand();
        dialog.text("\n \n \n           GAME COMPLETE!!!     \n \n \n   Do you want to start a new game?  \n \n    this option cannot be undone  \n \n \n       You can also keep your  \n      game data and review all \n          the game info\n \n \n")
                .button(" NEW GAME ", true).button(" DESKTOP ", false)
                .key(Input.Keys.ENTER, true).key(Input.Keys.ESCAPE, false).show(game.getMenu()).align(Align.center);
        game.getCurrentStage().addActor(dialog);

    }

    //-------------------------------------------------------------------------Getters_&_Setters
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
        int variation;
        if (value > 10) {
            variation = (int) value;
        } else {
            variation = (int) (currentMoney * value);
        }
        if (increase) {
            this.currentMoney += variation;
        } else {
            this.currentMoney -= variation;
        }
    }

    public int getCurrentReputation() {
        return currentReputation;
    }

    public void updateCurrentReputation(float value, boolean increase) {
        int variation;
        if (value > 10) {
            variation = (int) value;
        } else {
            variation = (int) (currentReputation * value);
        }
        if (increase) {
            this.currentReputation += variation;
        } else {
            this.currentReputation -= variation;
        }
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
        } else if (name.equalsIgnoreCase("BIOS2")) {
            return new Skin(Gdx.files.internal("commodore64ui/uiskin.json"));
        } else if (name.equalsIgnoreCase("BLUEDESK")) {
            return this.blueDeskSkin;
        } else {
            return this.menuSkin;
        }
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
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

    public void dispose() {

    }

    public void get_autoDecadeLog(String toString) {
        ((ManagerWindow) game.getDesktop().getTaskManager().appWindow).get_autoDecadeLog(toString);
    }


    public void addTestResults(String[] results) {
        testFinalResults = "";
        testAnswer = results;
        for (String str : results) {
            testFinalResults += str + "\n";
        }
    }
    public ResultStruct find_resultsAnswer(String id){
        for(ResultStruct res: resultStructArray){
            if(res.gameEvent_id.equalsIgnoreCase(id)){
                return res;
            }
        }
        return null;
    }
}


