package com.devXmachina.dxmgame.desktopActors;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.utils.Align;
import com.devXmachina.dxmgame.gamelogic.GameEvent;

public class NotePadWindow extends AppWindow{
    private String readMe;
    private ScrollPane scrollPane;
    private Label textField;


    public NotePadWindow(DesktopGameApp app) {
        super(app);
        create();
    }
    public void create(){
        readMe = gameLoader.readMe_txt;
        textField = new Label(readMe,getSkin());
        textField.setWrap(true);
        textField.setAlignment(Align.topLeft);
        this.row();
        this.row();
        scrollPane=new ScrollPane(textField);
        scrollPane.setScrollingDisabled(true,false);
        this.add(scrollPane).grow().pad(2,3,2,2);
    }
    @Override
    public void start() {

    }

    @Override
    public void restoreWindow() {
        super.restoreWindow();
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
