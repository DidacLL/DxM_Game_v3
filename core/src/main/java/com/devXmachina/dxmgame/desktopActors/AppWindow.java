package com.devXmachina.dxmgame.desktopActors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.devXmachina.dxmgame.DxM_Game;
import com.devXmachina.dxmgame.GameLoader;
import com.devXmachina.dxmgame.gamelogic.GameEvent;

import java.util.Random;

import static com.devXmachina.dxmgame.DxM_Game.*;

public abstract class AppWindow extends Window {
    public boolean maximized,minimized;
    DesktopGameApp app;
    String name;
    GameLoader gameLoader;
    Array<AppPage> appContentPages;
    public AppPage currentAppPage;

    public AppWindow(DesktopGameApp app) {
        super(app.appName,app.skin);
        this.app = app;
        this.name = app.appName;
        this.gameLoader= app.game.gameLoader;
        this.appContentPages= new Array<AppPage>();
        setSize(RESTWIN_WIDTH,RESTWIN_HEIGHT);
        this.setPosition(210,120);
        align(Align.center);
        createTopBar(name);
        maximized=false;
        minimized=true;
        add_focusListener(app);
        pack();
    }
    void add_focusListener(DesktopGameApp app) {
        this.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                app.desktop.focus= app.appType;
            }
        });
    }
    private void createTopBar(String name) {
        //CREATE CLOSE BUTTON
        getTitleTable().clearChildren();
        getTitleTable().defaults().space(5.0f);
        final Button closeButton = gameLoader.superPool.obtain_button(this.getSkin(), "close");
        closeButton.setColor(0.6f,0,0.1f,0.9f);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                minimized = true;
            }
        });
        //CREATE CENTER TITLE
        getTitleTable().add(closeButton);
        Image image = gameLoader.superPool.obtain_image( "title-bar-bg",this.getSkin());
        image.setScaling(Scaling.stretchX);
        getTitleTable().add(image).growX();
        Label label = gameLoader.superPool.obtain_label(name, this.getSkin());
        getTitleTable().add(label).padLeft(20.0f).padRight(20.0f);
        image = gameLoader.superPool.obtain_image( "title-bar-bg",this.getSkin());
        image.setScaling(Scaling.stretchX);
        getTitleTable().add(image).growX();
        //CREATE RESTORE N MINIMIZE BUTTONS
        final Button restButton = gameLoader.superPool.obtain_button(this.getSkin(), "restore");
        restButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (maximized){
                    app.desktop.focus=app.appType;
                    maximized = false;
                    restoreWindow();
                }
                else{
                    app.desktop.focus=app.appType;
                    maximized=true;
                }
            }
        });
        getTitleTable().add(restButton);
        final Button minButton = gameLoader.superPool.obtain_button(this.getSkin(), "minimize");
        minButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                minimized = true;
            }
        });
        getTitleTable().add(minButton);
    }
    public void restoreWindow() {
        if(this.getX()==0){this.setPosition(210,120);}
    }
    public void minimizeWindow(){
        this.minimized=true;
    }
    @Override
    public void act(float delta) {
        if(minimized){
            this.setVisible(false);
        }
        else {
            if (maximized) {
                this.setVisible(true);
                this.setBounds(0, 0, MAXWIN_WIDTH, MAXWIN_HEIGHT);
                super.act(delta);
            } else {
                if (this.getY() <= 50) {
                    this.setY(50.f);
                }
                if (this.getY() >= 460 - RESTWIN_HEIGHT) {
                    this.setY(460.f - RESTWIN_HEIGHT);
                }
                if (!this.isVisible()) {
                    Random random = new Random();
                    this.setPosition(random.nextInt(640-RESTWIN_WIDTH-10) + 10, random.nextInt(480 - RESTWIN_HEIGHT -20)+20);
                    this.setVisible(true);
                }
                this.setSize(RESTWIN_WIDTH, RESTWIN_HEIGHT);
                super.act(delta);
            }
        }
    }
    @Override
    public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
    }
    protected boolean addEvent(GameEvent gameEvent){
        AppPage currentPage;
        for(int i=0; i< appContentPages.size;i++){
            currentPage = appContentPages.get(i);
            if(currentPage.webPageURL.equals(gameEvent.getAppWebPage())){
                currentPage.add_gameEvent(gameEvent);
                return true;
            }
        }
        return false;
    }
    public void update(Boolean force){
        for (AppPage page:appContentPages) {
            if(force){
                if(page.webPageURL.equals(DxM_Game.BrowserURL.FACEBOOK)||page.webPageURL.equals(DxM_Game.BrowserURL.MAIL)||
                        page.webPageURL.equals(DxM_Game.BrowserURL.GITHUB)||page.webPageURL.equals(DxM_Game.BrowserURL.WIKI)) {
                    page.updateIndex = true;
                }
                page.updatePage=true;
            }
            page.update();
        }
    }
    public abstract void start();
    public abstract AppPage currentAppPage();

    public void dispose(){
        for(AppPage page: appContentPages){
               gameLoader.superPool.free(page);
        }

    }
}
