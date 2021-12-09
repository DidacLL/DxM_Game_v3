package com.devXmachina.dxmgame.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.devXmachina.dxmgame.DxM_Game;
import com.devXmachina.dxmgame.GameLoader;

import java.util.Random;

public class GameVirus extends Stage {
    private Skin skin;
    private DxM_Game game;
    private GameLoader gameLoader;
    private Table rootTable;
    private Label titlelabel,bodyLine_1,bodyLine_2,lastlabel;
    private int virusStep,tickCounter;
    private Label textLabel;

    public GameVirus(FitViewport fitviewport, DxM_Game game){
        super(fitviewport);
        this.game = game;
        this.gameLoader=game.gameLoader;
        skin = gameLoader.getSkin("BIOS2");
        virusStep=0;
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
        rootTable.add(textLabel).fillX().align(Align.topLeft);
        rootTable.row().fillY();
        this.addActor(rootTable);
    }


    private void endVirus() {

        Dialog dialog = new Dialog("", skin, "dialog-modal") {
            protected void result(Object object) {
                if (object.equals(true)){
                    game.setGameState(DxM_Game.GameState.MENU_MODE);
                    dispose();
                }
            }
        };
        dialog.getTitleTable().reset();
        Label label = new Label("ERR_:  CORE_DUMPED", skin, "title");
        label.setAlignment(Align.bottom);
        dialog.getTitleTable().add(label).expand();
        label = new Label("    \n        \n        \n        \n        Welcome to the Dungeon (c)    \n    1986 Amjads (pvt) Ltd VIRUS_SHOE RECORD V9.0  \n   " +
                "Dedicated to the dynamic memories of millions of    \n     viruses who are no longer with us today -  \n   " +
                "Thanks GOODNESS!!! BEWARE OF THE er..VIRUS :     \n    this program is catching program follows after \n   " +
                " these messages....$#@%$@!! \n  " +
                "    BRAIN COMPUTER SERVICES 730 NIZAM\n" +
                "    BLOCK ALLAMA IQBAL TOWN LAHORE-PAKISTAN  \n   PHONE: 430791,443248,280530. Beware of this VIRUS.... \n    Contact us for vaccination...    \n        \n        \n        \n    ",
                skin);
        label.setFontScale(0.5f,0.6f);
        dialog.text(label).align(Align.center).setPosition((DxM_Game.MAXWIN_WIDTH/2)-(dialog.getWidth()/2),(DxM_Game.MAXWIN_HEIGHT/2)-(dialog.getHeight()/2));
        dialog.button("   PAY   ", true)
                .key(Input.Keys.ENTER, true).key(Input.Keys.ESCAPE, true).show(this);
        this.addActor(dialog);


    }

    @Override
    public void draw() {
        if (tickCounter>20){
            tickCounter=0;
            updateVirus();
            if(virusStep>50){
                endVirus();
            }else{virusStep++;}
        }else {
            tickCounter++;
        }
        super.draw();
    }

    private void updateVirus() {
        textLabel.setText(textLabel.getText()+randText());
        if(textLabel.getText().length>2500){textLabel.setText("");}
        config_rootTable();
    }

    private String randText() {
            if (virusStep % 2 == 0) {
                return gameLoader.eventManager.getEventList().random().toString();
            } else {
                return game.getDesktop().getActors().random().toString();
            }
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
