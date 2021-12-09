package com.devXmachina.dxmgame.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.devXmachina.dxmgame.DxM_Game;
import com.devXmachina.dxmgame.GameLoader;

public class AutoPlayScreen extends Stage {
    private Skin skin;
    private DxM_Game game;
    private GameLoader gameLoader;
    private Table rootTable;
    private int tickCounter;
    private Label textLabel;
    private DxM_Game.EventDecade decade;

    public AutoPlayScreen(FitViewport fitviewport, DxM_Game game, DxM_Game.EventDecade decade){
        super(fitviewport);
        this.game = game;
        this.gameLoader=game.gameLoader;
        this.decade = decade;
        skin = gameLoader.getSkin("BIOS2");
        tickCounter=0;
        textLabel = new Label("",skin);
        textLabel.setFontScale(0.5f);
        config_rootTable();

    }

    private void config_rootTable() {
        rootTable = gameLoader.superPool.obtain_table(skin);
        rootTable.background("window");
        rootTable.setFillParent(true);
        rootTable.align(Align.topLeft);
        rootTable.row();
        Label titleLabel = new Label("Auto Play "+ gameLoader.getCurrentDecade().toString(),skin);
        titleLabel.setFontScale(2);
        rootTable.add(titleLabel).pad(10,10,10,10).row();
        rootTable.add(textLabel).fillX().align(Align.topLeft);
        rootTable.row().fillY();
        TextButton abortButton =new TextButton(" ABORT >>",skin);
        abortButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                endDecade();
            }
        });
        rootTable.add().grow();
        rootTable.row();
        rootTable.add(abortButton);
        this.addActor(rootTable);
    }


    private void endDecade() {
        gameLoader.setAutoPlay(false);
        gameLoader.setDebugMode(false);
        game.setGameState(DxM_Game.GameState.DESKTOP_MODE);
        String sb = this.textLabel.getText().toString();
        game.gameLoader.get_autoDecadeLog(sb);
    }

    @Override
    public void draw() {
        game.getDesktop().update();
        if (tickCounter>5){
            tickCounter=0;
            next_autoEvent();
            if(gameLoader.getCurrentDecade()!=this.decade){
                endDecade();
            }
        }else {
            tickCounter++;
        }
        super.draw();
    }

    private void next_autoEvent() {
        String str = gameLoader.eventManager.autoPlay_nextEvent();
        if(str.length()>2) {
            textLabel.setText(textLabel.getText()+"\n" + str);
            if (textLabel.getText().length > 2500) {
                textLabel.setText("");
            }
            config_rootTable();
        }
    }


    @Override
    public void dispose() {
        super.dispose();
    }
}
