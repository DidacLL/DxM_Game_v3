package com.devXmachina.dxmgame.desktopActors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.devXmachina.dxmgame.DxM_Game;
import com.devXmachina.dxmgame.gamelogic.GameEvent;
import com.devXmachina.dxmgame.gamelogic.ResultStruct;
import com.devXmachina.dxmgame.screens.GameDesktop;

public class PopNotification extends Window {
    GameDesktop desktop;
    Image icon;
    GameEvent gameEvent;
    DxM_Game game;
    String title;
    String text;
    Image img;

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
        if(gameEvent!=null){this.img= game.gameLoader.superPool.obtain_image(gameEvent.getAppWebPage().name()+"_mini");}
        createNotification();
    }

    private void createNotification() {
        this.setSize(180, 120);
        this.setPosition(640, 20);
        this.defaults().align(Align.topLeft).pad(10,10,10,10);
        this.setMovable(false);
        this.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                desktop.showGameEvent_PageApp(gameEvent);
            }
        });
        Label textLabel = new Label(text,getSkin());
        textLabel.setAlignment(Align.left);
        textLabel.setWrap(true);
        this.getTitleLabel().setText(title);
        this.row();
        if(img!=null){this.add(img).align(Align.topLeft);}
        this.add(textLabel).grow().grow().align(Align.topLeft);
        this.row();
//        if(gameEvent!=null&&gameEvent.isTestEvent()){this.add(create_MLbar()).fillX().align(Align.left).colspan(2);}
    }

    private Label create_MLbar() {
        ResultStruct res = game.gameLoader.find_resultsAnswer(gameEvent.getId());
        int k=((int)res.ratio*100);
//        if(res==null){System.out.println("sdfagdshngjnhnsetzsjzjzeszjernesztjneztjnetjnrj");}
        Label label = new Label("ML : "+res.result+ "confidence "+k+"% conf.\nYour Answer is : "+res.playerResult,game.gameLoader.getSkin("BIOS"));
        label.setFontScale(0.3f,0.4f);
        label.setWrap(true);
        if(res.result.equalsIgnoreCase(res.playerResult)){
            label.setColor(Color.GREEN);
        }else {
            label.setColor(Color.RED);
        }
        return label;
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
