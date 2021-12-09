package com.devXmachina.dxmgame.desktopActors;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.devXmachina.dxmgame.DxM_Game;


public class MailWindow extends AppWindow{
    private AppPage mail;

    public MailWindow(DesktopGameApp app) {
        super(app);
        create();
    }
    public void create(){
        createAppPages();
        //---------------------------------CONSTRUCT PAGE
        this.row();
        ScrollPane scrollPane= new ScrollPane(currentAppPage);
        this.add(scrollPane).grow().fill();

    }
    @Override
    public void start(){
        mail.start();
    }

    @Override
    public AppPage currentAppPage() {
        return currentAppPage;
    }


    private void createAppPages() {
        mail=new AppPage(getSkin(), DxM_Game.BrowserURL.MAIL,app, gameLoader);
        this.appContentPages.add(mail);
        currentAppPage=mail;
    }
    public void dispose(){
        mail=null;
    }

}
