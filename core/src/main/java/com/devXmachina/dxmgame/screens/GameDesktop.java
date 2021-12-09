package com.devXmachina.dxmgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.devXmachina.dxmgame.DxM_Game;
import com.devXmachina.dxmgame.GameLoader;
import com.devXmachina.dxmgame.desktopActors.*;
import com.devXmachina.dxmgame.gamelogic.GameEvent;
import com.devXmachina.dxmgame.gamelogic.SuperPool;


public class GameDesktop extends Stage {
    private static final int ASYNC_PULSE = 60;
    private Skin skin;
    private DxM_Game game;
    private GameLoader gameLoader;
    Table dockZone;
    ProgressBar progressBar;
    private ImageButton sound,turnOff,reputation,money,calendar,nextEventButton;
    boolean mute;
    private Label currentMoney,currentReputation,currentDate;
    public Table root,topBar,dockBar;
    DesktopGameApp taskManager,mail,browser,console,notepad;
   public DxM_Game.DesktopAppType focus;
   public boolean currentNotification;
   public Array<PopNotification> notificationQueue;
   private  int timeTickCounter;



    public GameDesktop(FitViewport fitViewport, DxM_Game game) {
        super(fitViewport);
        this.game = game;
        this.gameLoader= game.gameLoader;
        timeTickCounter=0;
        skin = game.gameLoader.getSkin("desktop");
        this.notificationQueue = new Array<>();
        Gdx.input.setInputProcessor(this);
    }
    public void start(){
        createTopBar();
        createPrograms();
        createDock();
        createRoot();
        addActor(root);
        this.addActor(mail.appWindow);
        this.addActor(browser.appWindow);
        this.addActor(taskManager.appWindow);
        this.addActor(notepad.appWindow);

    }
    //-------------------------------------------------------------------------------CREATE METHODS
    private void createRoot() {

        skin.get("title-color", Label.LabelStyle.class).font.getData().markupEnabled = true;
        root = gameLoader.superPool.obtain_table(skin);
        root.setBackground(skin.getTiledDrawable("bg"));
        root.setFillParent(true);
        root.add(topBar).align(Align.top).growX();
        root.align(Align.top);
        root.row().growY();
        root.row().fill(true, false);
        root.add(dockZone).align(Align.bottom).growX();
    }
    private void createDock() {
        dockZone = new Table(skin);
        dockZone.add("").growX();
        dockBar = new Table(skin);
        dockBar.add(notepad.dockIcon).padLeft(0.0f).padRight(5.0f).align(Align.center);
        dockBar.add(browser.dockIcon).padLeft(5.0f).padRight(5.0f).align(Align.center);
        dockBar.add(mail.dockIcon).padLeft(0.0f).padRight(5.0f).align(Align.center);
        dockBar.add(taskManager.dockIcon).padLeft(0.0f).padRight(5.0f).align(Align.center);
        dockZone.add(dockBar);
        dockZone.add("").growX();
        dockZone.setZIndex(mail.appWindow.getZIndex()+1);
    }
    private void createTopBar() {
        createTopBarButtons();
        topBar =  gameLoader.superPool.obtain_table(skin);
        topBar.setBackground("progress-bar-startup");
        topBar.add(gameLoader.superPool.obtain_label("DxM Game", skin)).padLeft(20.0f).padRight(20.0f).align(Align.left);
        topBar.add(nextEventButton).padLeft(20.0f).padRight(20.0f).align(Align.left);
        topBar.add("").growX();
        progressBar = new ProgressBar(0.0f, 2.5f, 0.1f, false, gameLoader.getSkin("BIOS2"));
        progressBar.setColor(Color.CYAN);
        progressBar.setValue(gameLoader.getDecadeProgress());
        topBar.add( progressBar);
        topBar.add(money).padLeft(6.0f);
        topBar.add(gameLoader.superPool.obtain_label(intToString(gameLoader.getCurrentMoney(),6), skin)).padLeft(6.0f);
        topBar.add(reputation).padLeft(6.0f);
        topBar.add(gameLoader.superPool.obtain_label(intToString(gameLoader.getCurrentReputation(),6), skin)).padLeft(2.0f);
        currentDate = gameLoader.superPool.obtain_label("20/4/"+gameLoader.getCurrentDecade().toString(),skin);
        topBar.add(calendar).padLeft(10.0f);
        topBar.add(currentDate);
        topBar.add(sound).padRight(10.f).padLeft(10.f);
        topBar.add(turnOff);
    }
    static String intToString(int num, int digits) {
        // format number as String
        String str=""+num;
        str.trim();
        if(str.length()<digits){
            int length=  digits-str.length();
            String newStr= "";
            for(int i=0; i<length;i++){
                newStr+="0";
            }
            newStr+=str;
            str=newStr;
        }

        return str;
    }
    private void createTopBarButtons() {
        turnOff = new ImageButton(game.gameLoader.getPicture("switch"),game.gameLoader.getPicture("switchb"));
        reputation = new ImageButton(game.gameLoader.getPicture("big_star"));
        money= new ImageButton(game.gameLoader.getPicture("dollar"));
        calendar=new ImageButton(game.gameLoader.getPicture("timer"));
        sound = new ImageButton(game.gameLoader.getPicture("sound"),game.gameLoader.getPicture("soundb"),game.gameLoader.getPicture("soundc"));
        sound.setChecked(gameLoader.isSoundActive());
        nextEventButton= new ImageButton(gameLoader.getPicture("nextEvent"),gameLoader.getPicture("nextEventb"),gameLoader.getPicture("nextEventc"));
        nextEventButton.getStyle().imageOver=nextEventButton.getStyle().imageChecked;
        nextEventButton.getStyle().imageChecked=nextEventButton.getStyle().imageUp;
        nextEventButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                gameLoader.eventManager.fire_nextEvent();
            }
        });
        turnOff.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameLoader.saveAllData();
                game.setGameState(DxM_Game.GameState.MENU_MODE);
            }
        });
        sound.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameLoader.setSound(mute);
                if(mute){
                    mute = false;
                    sound.setChecked(false);
                }
                else{
                    mute = true;
                    sound.setChecked(true);
                }

            }
        });
    }
    private void createPrograms() {
        console = new DesktopConsoleApp(this.game,this, DxM_Game.DesktopAppType.CONSOLE);
        browser = gameLoader.superPool.obtain_DesktopApp(this.game,this, DxM_Game.DesktopAppType.BROWSER);
        browser.start();
        mail =  gameLoader.superPool.obtain_DesktopApp(this.game,this, DxM_Game.DesktopAppType.MAIL);
        mail.start();
        taskManager =  gameLoader.superPool.obtain_DesktopApp(this.game,this, DxM_Game.DesktopAppType.MANAGER);
        taskManager.start();
        notepad =  gameLoader.superPool.obtain_DesktopApp(this.game,this, DxM_Game.DesktopAppType.NOTEPAD);
        notepad.start();

    }

    //-------------------------------------------------------------------------------GETTER AND SETTERS

    public DesktopGameApp getTaskManager() {
        return taskManager;
    }
    public DesktopGameApp getMail() {
        return mail;
    }
    public DesktopGameApp getBrowser() {
        return browser;
    }
    public Skin getSkin(){
        return this.skin;
    }
    public DesktopGameApp getApp(DxM_Game.DesktopAppType type){
        switch (type){
            case BROWSER:
                return this.browser;
            case MAIL:
                return this.mail;
            case MANAGER:
                return this.taskManager;
            case NOTEPAD:
                return this.notepad;
            case CONSOLE:
                return this.console;
        }
        return null;
    }
    public BrowserWindow getBrowserWindow(){
        return this.browser.getBrowserWindow();
    }

    //-----------------------------------------------------------------------GAME EVENT
    public void addEvent(GameEvent event, DxM_Game.DesktopAppType appType) {
        switch (appType){
            case MAIL:
                mail.add_eventToAppList(event);
                break;
            case BROWSER:
                browser.add_eventToAppList(event);
                break;
            case MANAGER:
            case NOTEPAD:
                notepad.add_eventToAppList(event);
                break;
            case CONSOLE:
                ((DesktopConsoleApp)console).fire_consoleEvent(event);
                break;
            default:
                break;
        }

    }
    public void showGameEvent_PageApp(GameEvent gameEvent){
        focus= gameEvent.getDesktopGameApp();
        try {
            getApp(focus).appWindow.maximized = true;
            getApp(focus).appWindow.minimized = false;
            getApp(focus).appWindow.setZIndex(getBrowser().appWindow.getZIndex() + 10);
        } catch (Exception e) {
//            e.printStackTrace();
        }
        if(gameEvent.getDesktopGameApp().equals(DxM_Game.DesktopAppType.BROWSER)){
            getApp(focus).getBrowserWindow().selectURL(gameEvent.getAppWebPage()).update();
        }
    }

    //-----------------------------------------------------------------------NOTIFICATION

    public void sendNotification(GameEvent newGameEvent,String title,String text) {
        if(!gameLoader.isAutoPlay()){notificationQueue.add(gameLoader.superPool.obtain_popNotification(this.game,this,newGameEvent,title,text));}
    }
    public void disposeNotification(PopNotification notification) {
        currentNotification=false;
        notification.clearActions();
        notification.clear();
    }

    //-----------------------------------------------------------------------GAME METHODS
    public void update(){

        if(gameLoader.isAutoPlay()){
            gameLoader.updateGameData();
        }else {
            int timeTick = timeTicker();
            switch (timeTick) {
                case 1:
                    try {
                        taskManager.updateManager(true);
                    } catch (Exception e) {

                    }
                    break;
                case 2:
                    updateTopBar();
                    break;
                case 3:
                    taskManager.updateManager(true);
                    break;
                case 4:
                    gameLoader.updateGameData();
                    break;
                default:
                    mail.update();
                    browser.update();
                    //do nothing
                    break;
            }
        }
    }
    private int timeTicker() {
        timeTickCounter++;
        if(timeTickCounter>ASYNC_PULSE){
            timeTickCounter=0;
            return 4;
        }else if (timeTickCounter==(int)((ASYNC_PULSE/4)*3)){
            updateTopBar();
                return 3;

        }else if (timeTickCounter==(int)(ASYNC_PULSE/4)) {
            return 2;
        }else if (timeTickCounter==1){
            return 1;
        }
        return 0;
    }

    @Override
    public void act() {
        super.act();
    }
    @Override
    public void draw() {
        this.update();
        if(notificationQueue.notEmpty()){
                notificationQueue.get(0).reSendNotification();
        }
        super.draw();
    }
    @Override
    public void dispose() {
        super.dispose();
        SuperPool pool = gameLoader.superPool;
        skin.dispose();
        game=null;
        try{
            pool.free(dockBar);
            pool.free(dockZone);
            pool.free(sound);
            pool.free(turnOff);
            pool.free(reputation);
            pool.free(money);
            pool.free(calendar);
            pool.free(currentMoney);
            pool.free(currentReputation);
            pool.free(currentDate);
            pool.free(root);
            pool.free(topBar);
            pool.free(dockBar);
            pool.free(taskManager);
            pool.free(mail);
            pool.free(browser);
            focus=null;
            for(PopNotification pop : notificationQueue){
                notificationQueue.removeValue(pop,true);
                pool.free(pop);
            }
            skin.dispose();
        }catch (IllegalArgumentException ex){
            //THIS IS A MASK
        }

    }

    public void updateTopBar() {
        Cell cell = root.getCell(topBar);
        cell.clearActor();
        createTopBar();
        cell.setActor(topBar);
    }

    public void forceTimeTick() {
        timeTickCounter = ASYNC_PULSE;
    }
}










