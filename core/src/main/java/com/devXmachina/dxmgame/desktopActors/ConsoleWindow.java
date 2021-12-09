package com.devXmachina.dxmgame.desktopActors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.utils.Align;
import com.devXmachina.dxmgame.gamelogic.GameEvent;

import java.util.Random;

import static com.devXmachina.dxmgame.DxM_Game.RESTWIN_HEIGHT;
import static com.devXmachina.dxmgame.DxM_Game.RESTWIN_WIDTH;


public class ConsoleWindow extends AppWindow{
    private static final String LAST_LINE = "\n>> COMPLETE...";
    public String[] textLines;
    private String currentText;
    private ScrollPane scrollPane;
    private TextArea textField;
    public int currentIndex;
    private GameEvent gameEvent;


    public ConsoleWindow(DesktopGameApp app, GameEvent gameEvent) {
        super(app);
        create();
        this.gameEvent=gameEvent;

        this.getTitleLabel().setText(gameEvent.getTitle());
        currentText="./DxM_data_x86 :";
        textLines=gameEvent.getTextBlocs();
        addAction(Actions.delay(2, new Action() {
            @Override
            public boolean act(float delta) {
               print_newLine();
                configAction();
                return true;
            }
        }));


    }

    @Override
    public void restoreWindow() {
        Random random = new Random();
        this.setPosition(random.nextInt(640-RESTWIN_WIDTH-10) + 10, random.nextInt(540 - RESTWIN_HEIGHT -20)+20);
        super.restoreWindow();
    }
    private void configAction() {
        addAction(Actions.delay(0.8f,new Action() {
            @Override
            public boolean act(float delta) {
                if(currentIndex< textLines.length-2){
                    print_newLine();
                    configAction();
                    return true;
                }else{
                    wait_andExit();
                    return false;
                }
            }
        }));
    }
    private void print_newLine() {
        currentIndex++;
        this.currentText+="\n"+textLines[currentIndex];
        this.textField.setText(this.currentText);

    }
    private void wait_andExit() {
        print_lastLine();
        addAction(Actions.delay(1, new Action(){
            @Override
            public boolean act(float delta) {
                disposeConsole();
                return false;
            }
        }));
    }
    private void print_lastLine() {
        this.textField.setText(this.currentText+"\n"+LAST_LINE);
    }
    public void create(){
        this.currentIndex=0;
        this.setVisible(true);
        textField = new TextArea("Loading...",gameLoader.getSkin("BIOS2"));
        textField.getStyle().background=gameLoader.getSkin("BIOS2").newDrawable("dialog");
        textField.getStyle().fontColor=Color.WHITE;
        textField.setAlignment(Align.topLeft);
        textField.getStyle().font.getData().setScale(0.4f,0.56f);
        scrollPane=new ScrollPane(textField);
        scrollPane.setScrollingDisabled(true,false);
        this.add(scrollPane).align(Align.top).grow();
        this.pack();
        this.minimized=false;
        this.restoreWindow();
    }
    @Override
    public void start() {

    }
    @Override
    public AppPage currentAppPage() {
        return null;
    }

    public void disposeConsole() {
        this.clearActions();
        gameLoader.consoleBusy=false;
        this.gameEvent.acceptEvent();
        app.desktop.getActors().removeValue(this,true);

        super.dispose();
    }

}
