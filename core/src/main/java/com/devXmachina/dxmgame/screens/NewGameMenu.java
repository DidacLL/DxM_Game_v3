package com.devXmachina.dxmgame.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.devXmachina.dxmgame.DxM_Game;
import com.devXmachina.dxmgame.GameLoader;

public class NewGameMenu extends Stage {
    private static final String[] textArray = {"   ",
            "\n\n\n   Politicament parlant,        \n com et consideres?         \n\n\n\n\n//Conservador//Progresista",
            "\n\n\n   Que en penses      \n  de l'economia basada en \n  grans multinacionals?     \n\n\n\n\n//A favor//En contra",
            "\n\n\n   Estaries a favor de      \n  perdre drets o llibertats     \n  a canvi d'una major      \n  seguretat?     \n\n\n\n\n//Si//No",
            "\n\n\n   La IA (inteligencia artificial) \n  hauria de substituir una gran \n  quantitat de l'activitat \n  i decisio humana     \n\n\n\n\n// Si // No ",
            "\n\n\n   Creus que s'haurien   \n  de gastar la quantitat de recursos \n  que sigui per fer les \n  situacions mes accessibles \n  per totes les persones?      \n\n\n\n\n// Si // No"
    };

    private Skin skin;
    private DxM_Game game;
    private GameLoader gameLoader;
    private Table rootTable,members;
    private Label titlelabel,bodyLine_1,bodyLine_2,lastlabel;
    private String[] results;
    private int testStep;
    public String userName;

    public NewGameMenu(FitViewport fitviewport, DxM_Game game){
        super(fitviewport);
        this.game = game;
        this.gameLoader=game.gameLoader;
        skin = gameLoader.getSkin("BIOS");
        testStep=0;
        results= new String[5];

    }
    public void start() {
        //todo añadir texto con Actions para simular carga
        textArray[0]="\n \n  Welcome " + userName +", \n \n This game implements a \n Machine Learning algorithm. \n \n Answer the following questions    \n to let game know you\n \n    (data is only saved in\n      your browser cookies)\n  \n \n        Lets start our test!    \n \n \n//START";
        config_rootTable();
        config_Title();
        config_body();
        config_buttons();

    }

    private void config_buttons() {
        rootTable.row().align(Align.bottom);
        TextButton nextButton = new TextButton(" START TEST ",skin);
        TextButton backButton = new TextButton(" BACK ",skin);
        nextButton.setVisible(true);
        backButton.setVisible(true);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                game.setGameState(DxM_Game.GameState.MENU_MODE);
            }
        });
        nextButton.addListener(new ChangeListener() {
            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                config_Dialog(testStep);
            }
        });
        rootTable.add().fillY();
        rootTable.row();
        rootTable.add(backButton).pad(60,10,2,2).align(Align.bottomLeft);
        rootTable.add(nextButton).pad(60,10,2,2).align(Align.bottomRight);
        addListener(new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if(keycode==Input.Keys.ENTER){
                    nextButton.fire(new ChangeListener.ChangeEvent());
                }
                return super.keyDown(event, keycode);
            }
        });
    }
    private void config_body() {
        rootTable.row();
        rootTable.add( gameLoader.superPool.obtain_label("\nSTARTING A NEW GAME.", skin)).align(Align.center).pad(2,10,20,2).colspan(2);
        Label label = gameLoader.superPool.obtain_label(" This game uses a Machine Learning \n algorithm to predict your choices during  \n the game, so we need you to answer a few \n questions to allow the IA know you better :) \n\n \n",skin);
        label.setFontScale(0.7f,0.8f);
        label.setAlignment(Align.topLeft,Align.left);
        rootTable.row();
        rootTable.add(label).pad(2,10,20,2).colspan(2);
        rootTable.row();
    }
    private void config_Title() {
        titlelabel =  gameLoader.superPool.obtain_label("**** Dev X Machina ****\nUOC_TEX:: IA & OpenSource simulator", skin);
        titlelabel.setAlignment(Align.top);
        titlelabel.setWrap(true);
        rootTable.add(titlelabel).align(Align.top).fillX().colspan(4);
        rootTable.row();
        rootTable.add(gameLoader.superPool.obtain_image("DxM_logo")).align(Align.center).pad(20,10,2,10).colspan(2);
        rootTable.add().growX();
    }
    private void config_rootTable() {
        rootTable = gameLoader.superPool.obtain_table(skin);
        rootTable.background("window");
        rootTable.setFillParent(true);
        rootTable.align(Align.topLeft);
        this.addActor(rootTable);
    }

    private void config_Dialog(int step) {

        Dialog dialog = new Dialog("", skin, "dialog-modal") {
            protected void result(Object object) {
                parse_result(object);
                testStep++;
                if(testStep<textArray.length){ config_Dialog(testStep);}
                else { endTest();}
            }
        };
        dialog.getTitleTable().reset();
        Label label = new Label(" Question_"+testStep+" ", skin, "title");
        label.setAlignment(Align.bottom);
        String[] text = textArray[testStep].split("//");
        dialog.getTitleTable().add(label).expand();
        Label question = new Label(text[0],skin);
        dialog.getContentTable().add(question).fillX().colspan(2);
        dialog.button(text[1], 1);
        if (text.length>2){
            for (int i=2; i<text.length;i++) {
                dialog.button(text[i],i);
            }
        }
        dialog.key(Input.Keys.NUM_1, 3).key(Input.Keys.ESCAPE, 0).show(game.getCurrentStage());
    }

    private void endTest() {
        gameLoader.addTestResults(results);
        game.reloadMenu(false);
        game.start_desktop();
        this.dispose();


        //TODO
    }

    private void parse_result(Object object) {
        if(object.equals(0)){
            testStep=0;
        }
        else{
            if(testStep>0){
                if (object.equals(2)) {
                    this.results[testStep - 1] = "b";
                } else {
                    this.results[testStep - 1] = "a";
                }
            }
//
        }

    }

    @Override
    public void dispose() {
//        super.dispose();
//        skin.dispose();
    }

    public void addUserName(String text) {
        this.userName=text;
        start();

    }
}
