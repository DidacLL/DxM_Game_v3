package com.devXmachina.dxmgame.desktopActors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.devXmachina.dxmgame.DxM_Game;
import com.devXmachina.dxmgame.gamelogic.GameEvent;
import com.devXmachina.dxmgame.screens.GameDesktop;

public class PopNotification extends Window {
    GameDesktop desktop;
    Image icon;
    GameEvent gameEvent;
    DxM_Game game;
    String title;
    String text;

    public PopNotification(DxM_Game game, GameDesktop desktop, GameEvent gameEvent, String title, String text, Skin skin) {
        super("",skin);

        construct_notification(game, desktop, gameEvent, title, text);
    }

    public void construct_notification(DxM_Game game, GameDesktop desktop, GameEvent gameEvent, String title, String text) {
        this.desktop= desktop;
        this.gameEvent = gameEvent;
        this.game= game;
        this.title = title;
        this.text= text;
        createNotification();
    }

    private void createNotification() {
        this.setSize(160, 120);
        this.setPosition(640, 20);
        this.setMovable(false);
        this.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                desktop.showGameEvent_PageApp(gameEvent);
            }
        });
        Label titleLabel = new Label(title,getSkin());
        Label textLabel = new Label(text,getSkin());
        titleLabel.setAlignment(Align.left);;
        titleLabel.setWrap(false);
        textLabel.setAlignment(Align.left);
        textLabel.setWrap(false);

        this.row();
        this.add(title).align(Align.left);
        this.row();
        this.add(text).pad(10,10,10,10).align(Align.left);
    }
    @Override
    public void act(float delta) {
        super.act(delta);
    }

    public void fireNotification() {
            desktop.addActor(this);
            this.setVisible(true);
            this.addAction(Actions.sequence(Actions.parallel(Actions.moveBy(-60, 0, 0.3f), Actions.alpha(1, 0.3f)),
                    Actions.sequence(Actions.moveTo(640, getY(), 1), Actions.delay(3), Actions.alpha(0, 0.4f),
                            Actions.sequence(Actions.removeActor(this),
                                        new Action() {
                                        @Override
                                        public boolean act(float delta) {
                                            dispose();
                                            return false;
                                        }
                                    }
                                    ))));
    }

    public void dispose(){
        desktop.disposeNotification(this);
    }
    public void reSendNotification(){
        desktop.notificationQueue.removeValue(this,true);
        this.fireNotification();

    }
}
