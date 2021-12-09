package com.devXmachina.dxmgame.desktopActors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.devXmachina.dxmgame.DxM_Game;
import com.devXmachina.dxmgame.GameLoader;

public class DockIcon extends ImageButton {
    private DesktopGameApp app;
    private GameLoader gameLoader;
    private boolean updateIcon,notification;;
    private int notificationCounter;

    public DockIcon(DesktopGameApp app, DxM_Game.DesktopAppType appType){
        super(app.game.gameLoader.getPicture(appType.name()));
        this.gameLoader= app.game.gameLoader;
        this.app = app;
        this.scaleBy(0.1f);
        loadImages(appType);
        createListener(app, appType);
        notificationCounter=0;
    }
    private void loadImages(DxM_Game.DesktopAppType appType) {
        String iconName="";
        if(notification){iconName+="n";}
        iconName+= appType.name();
        this.getStyle().imageUp =gameLoader.getPicture(iconName);
        this.getStyle().imageDown =gameLoader.getPicture(iconName+"b");
        this.getStyle().imageOver = gameLoader.getPicture(iconName+"c");
    }
    private void createListener(DesktopGameApp app, DxM_Game.DesktopAppType appType) {
        this.addListener(new InputListener(){
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if (app.appWindow.minimized){
                    try{
                        ((NotePadWindow)app.appWindow).recoverText();
                    }
                    catch(Exception e){
                    }
                    app.desktop.focus= appType;
                    app.appWindow.minimized=false;
                    app.appWindow.setZIndex(app.appWindow.getZIndex() + 10);
                }else {
                    if(app.desktop.focus== appType){
                        app.appWindow.minimizeWindow();
                        app.focused=false;
                    }else{
                        app.appWindow.setZIndex(app.appWindow.getZIndex() + 10);
                        app.desktop.focus= appType;
                    }
                }
                return true;
            }
        });
    }
    public void update() {
        if(updateIcon){
            loadImages(this.app.appType);
            this.updateIcon =false;
        }
    }
    public void toogleNotification(Boolean activate){
        if(activate){
            if(!notification){this.notification=true;}
            updateIcon=true;
            this.notificationCounter+=1;
        }else{
            this.notificationCounter-=1;
            if (notificationCounter<=0){
                notification=false;
                updateIcon=true;
                notificationCounter=0;
            }
        }
    }

    public void dispose() {
        app=null;
        updateIcon= false;
        notification=false;
        this.clear();
        gameLoader.superPool.free(this);
        gameLoader=null;
    }
}
