package com.devXmachina.dxmgame.desktopActors;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.devXmachina.dxmgame.DxM_Game;


public class BrowserWindow extends AppWindow{

    private AppPage facebook,wiki,blank,github,news,yahoo;
    private ImageButton updateBTN,backBTN,forwardBTN;
    private SelectBox urlBar;
    private ScrollPane scrollPane;


    public BrowserWindow(DesktopGameApp app) {
        super(app);
        create();
    }
    public void create(){
        urlBar = createBrowserBar();
        createBrowserBar_Buttons();
        createAppPages();
        //---------------------------------CONSTRUCT PAGE
        this.row();
        this.add(urlBar).growX().fillX();
        configBrowserBar_Buttons();
        this.row();
        scrollPane=new ScrollPane(currentAppPage);
        this.add(scrollPane).grow().fill().colspan(5);
    }
    @Override
    public void start(){
        for (AppPage page : appContentPages) {
            page.start();
        }
    }
    @Override
    public AppPage currentAppPage() {
        return currentAppPage;
    }
    @Override
    public void dispose() {
        facebook=null;
        wiki=null;
        blank=null;
        github=null;
        news=null;
        yahoo=null;
        gameLoader.superPool.free(updateBTN);
        gameLoader.superPool.free(backBTN);
        gameLoader.superPool.free(forwardBTN);
        urlBar.clear();
        scrollPane.clear();
        super.dispose();

    }
    private void configBrowserBar_Buttons() {
        this.add(updateBTN).fill(false,false).pad(0,2,0,2).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                update(true);
            }
        });
        this.add(backBTN).fill(false,false).pad(0,2,0,2).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int index = urlBar.getSelectedIndex()-1;
                if (index<0){index=0;}
                urlBar.setSelectedIndex(index);
            }
        });

        this.add(forwardBTN).fill(false,false).pad(0,2,0,0).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int index = urlBar.getSelectedIndex()+1;
                if(index>=urlBar.getItems().size){index= urlBar.getItems().size-1;}
                    urlBar.setSelectedIndex(index);
            }
        });
    }
    private void createAppPages() {
        blank=gameLoader.superPool.obtain_appPage(DxM_Game.BrowserURL.BLANK,app);
        this.appContentPages.add(blank);
        facebook = gameLoader.superPool.obtain_appPage( DxM_Game.BrowserURL.FACEBOOK,app);
        this.appContentPages.add(facebook);
        github=gameLoader.superPool.obtain_appPage( DxM_Game.BrowserURL.GITHUB,app);
        this.appContentPages.add(github);
        wiki=gameLoader.superPool.obtain_appPage(DxM_Game.BrowserURL.WIKI,app);
        this.appContentPages.add(wiki);
        news=gameLoader.superPool.obtain_appPage( DxM_Game.BrowserURL.NEWS,app);
        this.appContentPages.add(news);
        currentAppPage=blank;
    }
    private void createBrowserBar_Buttons() {
        forwardBTN=new ImageButton(gameLoader.getPicture("arrow_right"),gameLoader.getPicture("arrow_rightb"));
        updateBTN=new ImageButton(gameLoader.getPicture("replay"),gameLoader.getPicture("replayb"));
        backBTN=new ImageButton(gameLoader.getPicture("arrow_left"),gameLoader.getPicture("arrow_leftb"));
    }
    private SelectBox createBrowserBar() {
        urlBar = new SelectBox(this.getSkin());
        setSelectBox_options();
        urlBar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                switch((DxM_Game.BrowserURL)urlBar.getSelected()){
                    case FACEBOOK:
                        facebook.updateIndex=true;
                        facebook.updatePage=true;
                        facebook.update();
                        scrollPane.setActor(facebook);
                        currentAppPage= facebook;

                        break;
                    case WIKI:
                        wiki.updatePage=true;
                        wiki.updateIndex=true;
                        wiki.update();
                        scrollPane.setActor(wiki);
                        currentAppPage= wiki;
                        break;
                    case GITHUB:
                        github.updatePage=true;
                        github.updateIndex=true;
                        github.update();
                        scrollPane.setActor(github);
                        currentAppPage= github;
                        break;
                    case BLANK:
                        scrollPane.setActor(blank);
                        currentAppPage = blank;
                        break;
                    case NEWS:
                        news.updatePage=true;
                        news.update();
                        scrollPane.setActor(news);
                        currentAppPage= news;
                        break;
                }
            }
        });
        return urlBar;
    }
    public void setSelectBox_options() {
        SelectBox selectBox = this.urlBar;
        switch (gameLoader.getCurrentDecade()){

            case SEVENTIES:
            case EIGHTIES:
            case NINETIES:
                selectBox.setItems(DxM_Game.BrowserURL.BLANK, DxM_Game.BrowserURL.NEWS, DxM_Game.BrowserURL.WIKI);
                break;
            case NOUGHTIES:
                selectBox.setItems(DxM_Game.BrowserURL.BLANK, DxM_Game.BrowserURL.NEWS, DxM_Game.BrowserURL.WIKI, DxM_Game.BrowserURL.FACEBOOK);
                break;
            case TWENTY_TENS:
            case TWENTY_TWENTIES:
                selectBox.setItems(DxM_Game.BrowserURL.BLANK, DxM_Game.BrowserURL.NEWS, DxM_Game.BrowserURL.WIKI,DxM_Game.BrowserURL.FACEBOOK,DxM_Game.BrowserURL.GITHUB);
                break;
        }
    }
    public AppPage selectURL(DxM_Game.BrowserURL appWebPage) {
        for(int i=0;i<appContentPages.size;i++){
            if(appContentPages.get(i).webPageURL.equals(appWebPage)){
                urlBar.setSelected(appWebPage);
                return appContentPages.get(i);
            }
        }
        return null;
    }
}
