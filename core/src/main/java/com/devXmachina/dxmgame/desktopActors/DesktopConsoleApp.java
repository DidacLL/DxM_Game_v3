package com.devXmachina.dxmgame.desktopActors;

import com.devXmachina.dxmgame.DxM_Game;
import com.devXmachina.dxmgame.gamelogic.GameEvent;
import com.devXmachina.dxmgame.screens.GameDesktop;

public class DesktopConsoleApp extends DesktopGameApp{
    public DesktopConsoleApp(DxM_Game game, GameDesktop desktop, DxM_Game.DesktopAppType appType) {
        super(game, desktop, appType);
    }

    public void fire_consoleEvent(GameEvent gameEvent){
        if(!game.gameLoader.isAutoPlay()){
            desktop.addActor( new ConsoleWindow(this,gameEvent));
            game.gameLoader.consoleBusy=true;
        }

//        desktop.showGameEvent_PageApp(gameEvent);
    }




    @Override
    public void construct_gameApp(DxM_Game game, GameDesktop desktop, DxM_Game.DesktopAppType appType) {
        this.game = game;
        this.desktop = desktop;
        this.appType= appType;
        this.appName = ""+ appType;
        this.skin = game.gameLoader.getSkin("desktop");
    }
    @Override
    public void add_eventToAppList(GameEvent event) {
        fire_consoleEvent(event);
    }
    @Override
    public void update() {
        //TODO
    }
    @Override
    public void start() {

    }
    @Override
    public BrowserWindow getBrowserWindow() {
        return null;
    }
    @Override
    public void toogle_IconNotification(boolean activate) {
    }
    @Override
    public void reset() {
    }
    @Override
    public void updateManager(Boolean full) {
    }
}
