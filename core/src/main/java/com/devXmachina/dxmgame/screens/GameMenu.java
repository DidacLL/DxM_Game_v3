package com.devXmachina.dxmgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.devXmachina.dxmgame.DxM_Game;
import com.devXmachina.dxmgame.GameLoader;


public class GameMenu extends Stage {

    private final static String ABOUT_TEXT=("\n  We are four colleagues studying  \n  software engineering in UOC.  \n  This is a simple game to show the  \n  results about our investigation  \n  from IA evolution and the  \n  OpenSource contribution on it  \n \n  it also shows the basic  \n  use of an ML to predict your  \n  game decisions from your  \n  initial test result  \n \n  You can see full documentation at  \n  our webSite or in our wiki  \n  inside the game  \n\n");
    private final static String NEWGAME_LINE1= "Welcome to DxM_Game."  ;
    private final static String NEWGAME_LINE2= " This is just a fun presentation\n from a class group project." ;
    private final static String NEWGAME_LINE3= " You can see more info \n in the 'about' button below" ;
    private final static String NEWGAME_LINE4= " P.S: This was our very first game" ;
    private final static String NEWGAME_LINE5= "       So, please, be gentle \n\n                 AND ENJOY!\n";




    private Skin skin;
    private DxM_Game game;
    private GameLoader gameLoader;
    private Table rootTable, bottomButtons,creditsTable,lastLineTable;
    private String line_1,line_2,line_3,line_4,line_5;
    private Label titlelabel,bodyLine_1,bodyLine_2,bodyLine_3,bodyLine_4,bodyLine_5,lastlabel;
    TextButton optionA_Button,optionB_Button,optionC_Button, optionD_Button, debug_Button,reset_Button,sound_Button,fullScreen_Button;
    public Preferences preferences;
    boolean newGame;
    private TextField textField;
    private Image dxmLogo;

    public GameMenu(FitViewport fitViewport, DxM_Game game) {
        super(fitViewport);
        this.game = game;
        this.gameLoader=game.gameLoader;
        this.newGame=!gameLoader.savedData;

    }

    public void start() {
        //todo añadir texto con Actions para simular carga
        skin = gameLoader.getSkin("BIOS");
        this.addActor(create_Root());
        instance_allActors();
        construct_menuStructure();
        config_stdButtons();
        if(newGame){
            config_newGame_menu();
        }else{
            config_mainMenu();
        }
    }

    private void config_stdButtons() {
        sound_Button.addListener(new ChangeListener() {
            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                if(!gameLoader.isSoundActive()) {
                    sound_Button.setChecked(true);
                    gameLoader.setSound(true);
                } else{
                    sound_Button.setChecked(false);
                    gameLoader.setSound(false);
                }
            }
        });
        sound_Button.setTransform(true);
        sound_Button.setScale(0.6f,0.8f);
        debug_Button.addListener(new ChangeListener() {
            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                if(!gameLoader.isDebugMode()) {
                    debug_Button.setChecked(true);
                    gameLoader.setDebugMode(true);

                } else{
                    debug_Button.setChecked(false);
                    gameLoader.setDebugMode(false);
                }
            }
        });
        debug_Button.setTransform(true);
        debug_Button.setScale(0.6f,0.8f);
        fullScreen_Button.addListener(new ChangeListener() {
            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                if(!Gdx.graphics.isFullscreen()) {
                    debug_Button.setChecked(true);
                    Gdx.app.getGraphics().setFullscreenMode(Gdx.graphics.getDisplayMode());
                } else{
                    debug_Button.setChecked(false);
                    Gdx.app.getGraphics().setWindowedMode(DxM_Game.MAXWIN_WIDTH,DxM_Game.MAXWIN_HEIGHT);

                }
            }
        });
        fullScreen_Button.setTransform(true);
        fullScreen_Button.setScale(0.6f,0.8f);
    }
    private void config_newGame_menu() {
        optionD_Button.setVisible(true);
        optionC_Button.setVisible(false);
        optionB_Button.setVisible(false);
        optionA_Button.setVisible(true);
        line_1=NEWGAME_LINE1;
        line_2=NEWGAME_LINE2;
        line_3=NEWGAME_LINE3;
        line_4=NEWGAME_LINE4;
        line_5=NEWGAME_LINE5;
        bodyLine_1.setText(line_1);
        bodyLine_2.setText(line_2);
        bodyLine_3.setText(line_3);
        bodyLine_4.setText(line_4);
        bodyLine_5.setText(line_5);
        bodyLine_2.setFontScale(0.70f,0.85f);
        bodyLine_3.setFontScale(0.70f,0.85f);
        bodyLine_4.setFontScale(0.70f,0.85f);
        bodyLine_5.setFontScale(0.70f,0.85f);
        rootTable.getCell(lastLineTable).align(Align.topLeft).pad(0,10,20,10);


        optionD_Button.setText(" NEW GAME ");
        optionD_Button.addListener(new ChangeListener() {
            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                if ((textField.getText()==null)||(textField.getText().equalsIgnoreCase("user") || textField.getText().equalsIgnoreCase(" ") )) {
                    Dialog dialog = new Dialog("", skin, "dialog-modal");
                    dialog.getTitleTable().reset();
                    Label label = new Label("ERR_MISSING_USERNAME", skin, "title");
                    label.setAlignment(Align.bottom);
                    dialog.getTitleTable().add(label).expand();
                    dialog.text("\n Hey user,\n \n  let us know your name, please!   \n \n \n \n").button("...ok", true)
                            .key(Input.Keys.ENTER, true).key(Input.Keys.ESCAPE, false).show(game.getMenu());
                }
                else{
                    gameLoader.setUserName(textField.getText());
                    game.getNewGame_stage().addUserName(textField.getText());
                    game.setGameState(DxM_Game.GameState.NEW_GAME);

                }

            }
        });
        optionA_Button.setText(" :ABOUT ");
        optionA_Button.addListener(new ChangeListener() {
            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                    Dialog dialog = new Dialog("", skin, "dialog-modal");
                    dialog.getTitleTable().reset();
                        Label label = new Label("ABOUT : DXM_GAME", skin, "title");
                    label.setAlignment(Align.bottom);
                    dialog.getTitleTable().add(label).expand();
                    label = new Label(ABOUT_TEXT,skin);
                    label.setFontScale(0.75f,0.85f);
                    dialog.text(label).align(Align.center).setPosition((DxM_Game.MAXWIN_WIDTH/2)-(dialog.getWidth()/2),(DxM_Game.MAXWIN_HEIGHT/2)-(dialog.getHeight()/2));
                    dialog.button("   lets play!   ", true)
                            .key(Input.Keys.ENTER, true).key(Input.Keys.ESCAPE, false).show(game.getMenu());

            }
        });
    }
    private void config_mainMenu() {

        optionD_Button.setText("  PLAY >> ");
        optionD_Button.setVisible(true);
        optionD_Button.addListener(new ChangeListener() {
            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                game.setGameState(DxM_Game.GameState.DESKTOP_MODE);
            }
        });
        optionC_Button.setVisible(false);
        optionB_Button.setVisible(false);
        optionA_Button.setText(" DELETE GAME ");
        optionA_Button.setVisible(true);
        optionA_Button.addListener(new ChangeListener() {
            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                Dialog dialog = new Dialog("Are you sure?",skin,"dialog-modal") {
                    protected void result(Object object) {
                        if (object.equals(true)){
                            gameLoader.clearPreferences();
                            game.reset();
                        }
                    }
                };
                dialog.getTitleTable().reset();
                Label label = new Label(" DELETE GAME DATA ", skin, "title");
                label.setAlignment(Align.bottom);
                dialog.getTitleTable().add(label).expand();
                dialog.text("\n Are you sure?\n \n  this option cannot be undone  \n \n \n \n").button("Yes", true).button("No", false)
                        .key(Input.Keys.ENTER, true).key(Input.Keys.ESCAPE, false).show(game.getMenu());
            }
        });

        bodyLine_1.setText("Welcome again "+gameLoader.getUserName()+"  ");
        lastLineTable.setVisible(false);

    }

    private void construct_menuStructure() {
        //--------TITLE LINE
        titlelabel.setAlignment(Align.top);
        titlelabel.setWrap(true);

        rootTable.add(titlelabel).align(Align.top).fillX().colspan(4).pad(2,0,2,20);
        rootTable.row();
        //--------LOGO LINE
        rootTable.add(dxmLogo).align(Align.topLeft).pad(0,30,2,2);
        rootTable.add(creditsTable).growX().pad(8,0,0,20);
        //-------- LINE 1
        rootTable.row();
        rootTable.add(bodyLine_1).pad(10,10,2,2).align(Align.left);
        rootTable.add().fillX();
        //-------- LINE 2
        rootTable.row();
        rootTable.add(bodyLine_2).pad(10,10,2,2).align(Align.left);
        rootTable.add(fullScreen_Button).align(Align.right).pad(10,60,2,2);;
        //-------- LINE 3
        rootTable.row();
        rootTable.add(bodyLine_3).pad(10,10,2,2).align(Align.left);
        rootTable.add(sound_Button).align(Align.right).pad(10,60,2,2);;
        //-------- LINE 4
        rootTable.row();
        rootTable.add(bodyLine_4).pad(10,10,2,2).align(Align.left);
        rootTable.add(debug_Button).align(Align.right).pad(10,60,2,2);
        //-------- LINE 5
        rootTable.row();
        rootTable.add(bodyLine_5).pad(10,10,2,2).align(Align.left);
        rootTable.add().align(Align.right).pad(10,20,2,2);
        //-------- LAST LINE 
        rootTable.row();
        lastLineTable = gameLoader.superPool.obtain_table(skin);
        lastlabel=gameLoader.superPool.obtain_label("ENTER YOUR NAME:   ",skin);
        lastLineTable.add(lastlabel).pad(15,10,2,2).align(Align.right);
        lastLineTable.add(textField).pad(15,10,2,2);
        rootTable.add(lastLineTable).align(Align.bottom).colspan(2);
        //-------- BOTTOM BUTTONS
        rootTable.row().fillY().align(Align.bottom);
        bottomButtons.defaults().fillX().expandX();
        bottomButtons.add(optionA_Button).pad(10,10,10,2).align(Align.right).growX();
        bottomButtons.add(optionB_Button).pad(10,10,10,2).align(Align.center).growX();
        bottomButtons.add(optionC_Button).pad(10,10,10,2).align(Align.center).growX();
        bottomButtons.add(optionD_Button).pad(10,10,10,2).align(Align.left).growX();
        rootTable.add(bottomButtons).align(Align.bottomLeft).colspan(2).expandX();
    }
    private Table create_Root() {
        rootTable = gameLoader.superPool.obtain_table(skin);
        rootTable.background("window");
        rootTable.setFillParent(true);
        if(gameLoader.isDebugMode()){rootTable.debugAll();}
        rootTable.align(Align.topLeft);
        return rootTable;
    }
    private void instance_allActors() {
        titlelabel = new Label("**** Dev X Machina ****  \nIA & OpenSource development simulator   ", skin);
        bodyLine_1 =  new Label(line_1, skin);
        bodyLine_2 =  new Label(line_2, skin);
        bodyLine_3 =  new Label(line_3, skin);
        bodyLine_4 =  new Label(line_4, skin);
        bodyLine_5 =  new Label(line_5, skin);
        optionD_Button = new TextButton("",skin);
        optionB_Button = new TextButton("",skin);
        optionC_Button = new TextButton("",skin);
        optionA_Button = new TextButton("",skin);
        textField = new TextField("", skin, "nobg");
        if(gameLoader.savedData){textField.setText(gameLoader.getUserName());}
        textField.setMessageText("USER NAME");
        dxmLogo = gameLoader.superPool.obtain_image("DxM_logo");
        bottomButtons = gameLoader.superPool.obtain_table(skin);
        create_credits();
        debug_Button = new TextButton(" DEBUG_MODE ",skin,"toggle");
        reset_Button = new TextButton(" RESET_GAME ",skin);
        sound_Button = new TextButton(" SOUND : ON ",skin,"toggle");
        fullScreen_Button = new TextButton(" FULLSCREEN ",skin,"toggle");

    }
    private void create_credits() {
        creditsTable = gameLoader.superPool.obtain_table(skin);
        creditsTable.defaults().align(Align.left).fillX();
        Label label = gameLoader.superPool.obtain_label("",skin);
        label.setFontScale(0.7f);
        label.setText("Jordi Pons");
        creditsTable.add(label).row();
        label = gameLoader.superPool.obtain_label("",skin);
        label.setFontScale(0.7f);
        label.setText("Joan March");
        creditsTable.add(label).row();
        label = gameLoader.superPool.obtain_label("",skin);
        label.setFontScale(0.7f);
        label.setText("Daniel Mejias");
        creditsTable.add(label).row();
        label = gameLoader.superPool.obtain_label("",skin);
        label.setFontScale(0.7f);
        label.setText("Didac Llorens");
        creditsTable.add(label);
    }

    public boolean isNewGame(){
        return newGame;
    }

    @Override
    public void dispose() {
        super.dispose();
        skin.dispose();
    }

    public void setNewGame(boolean b) {
        this.newGame=b;
    }
}
