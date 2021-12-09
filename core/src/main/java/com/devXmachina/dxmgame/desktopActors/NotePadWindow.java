package com.devXmachina.dxmgame.desktopActors;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.devXmachina.dxmgame.GameLoader;
import com.devXmachina.dxmgame.gamelogic.GameEvent;

import java.util.Arrays;

import static com.devXmachina.dxmgame.DxM_Game.RESTWIN_HEIGHT;
import static com.devXmachina.dxmgame.DxM_Game.RESTWIN_WIDTH;

public class NotePadWindow extends AppWindow{
    private String readMe= "This is the readme file \n \n \n just trying how it works, Hey there all! I really can’t seem to wrap my head around how to set up ruling for terrains sets. I’m trying to draw a cliff, but it draws very weird and I really don’t know what I’m doing wrong.\n" +
            "Could someone ELI5? know there’s a Resize map, but that only increases the bounds. I want to double the size of my map so that there for every 1 tile, there are now 4 (2 horizontal, 2 vertical). Is there any way to do that?";
    private ScrollPane scrollPane;
    private TextArea textField;

    @Override
    public void restoreWindow() {
        super.restoreWindow();
    }

    public NotePadWindow(DesktopGameApp app) {
        super(app);
        create();
    }
    public void create(){

        textField = new TextArea(readMe,getSkin());
        textField.setAlignment(Align.topLeft);
        this.row();
        this.row();
        scrollPane=new ScrollPane(textField);
        scrollPane.setScrollingDisabled(true,false);
        this.add(scrollPane).grow();
    }
    @Override
    public void start() {

    }
    @Override
    public AppPage currentAppPage() {
        return null;
    }
    public void recoverText() {
        this.textField.setText(readMe);
    }
    public void setNewText(String text){
        this.textField.setText(text);
    }

    @Override
    protected boolean addEvent(GameEvent gameEvent) {
        setNewText(String.join("\n",gameEvent.getTextBlocs()));
        return true;
    }
}
