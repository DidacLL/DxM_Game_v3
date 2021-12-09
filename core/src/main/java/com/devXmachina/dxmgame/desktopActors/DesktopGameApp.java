package com.devXmachina.dxmgame.desktopActors;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Pool;
import com.devXmachina.dxmgame.DxM_Game;
import com.devXmachina.dxmgame.gamelogic.GameEvent;
import com.devXmachina.dxmgame.screens.GameDesktop;

public class DesktopGameApp implements Pool.Poolable {


    public boolean focused;

    public void updateWindow() {
    }

    DxM_Game game;
    GameDesktop desktop;
    public Skin skin;
    public DockIcon dockIcon;
    public AppWindow appWindow;
    public DxM_Game.DesktopAppType appType;
    String appName;

    //------------------browser

    public DesktopGameApp(DxM_Game game, GameDesktop desktop, DxM_Game.DesktopAppType appType) {
        if(appType!=null){construct_gameApp(game, desktop, appType);}
    }

    public void construct_gameApp(DxM_Game game, GameDesktop desktop, DxM_Game.DesktopAppType appType) {
        this.game = game;
        this.desktop = desktop;
        this.appType= appType;
        this.appName = ""+ appType;
        this.skin = game.gameLoader.getSkin("desktop");
        createGameApp_members(appType);
    }
    private void createGameApp_members(DxM_Game.DesktopAppType appType) {
        switch (appType){
            case BROWSER:
                appWindow = new BrowserWindow(this);
                break;
            case MAIL:
                appWindow = new MailWindow(this);
                break;
            case MANAGER:
                appWindow = new ManagerWindow(this, game.gameLoader);
                break;
            case NOTEPAD:
                appWindow = new NotePadWindow(this);
        }
//        popNotification = new PopNotification(this.desktop,new GameEvent("TEST",this.game),"POP!Notification test",skin);
        dockIcon = new DockIcon(this, appType);
    }
    public void add_eventToAppList(GameEvent event) {
        this.appWindow.addEvent(event);
    }
    public void update(){
        appWindow.update(false);
        dockIcon.update();

    }
    public void start() {
        this.appWindow.start();
        dockIcon.update();
    }
    public BrowserWindow getBrowserWindow(){
            return (BrowserWindow) this.appWindow;
    }
    public void toogle_IconNotification(boolean activate){
        this.dockIcon.toogleNotification(activate);
    }
    @Override
    public void reset() {
        this.game=null;
        this.desktop=null;
        this.skin.dispose();
        this.dockIcon.dispose();
        this.dockIcon=null;
        appWindow.dispose();
        this.appWindow.dispose();
        appType = null;
        appName=null;
    }
    public void updateManager(Boolean full){
        AppWindow window = this.appWindow;
        if(window instanceof ManagerWindow){
            ((ManagerWindow) window).updateManager(full);
        }
    }
}
