package com.devXmachina.dxmgame.desktopActors;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.devXmachina.dxmgame.GameLoader;
import com.devXmachina.dxmgame.gamelogic.GameEvent;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

public class ManagerWindow extends AppWindow{
    private Table runningEvents,runningEventsFix,doneEvents,doneEventsFIX,viewResults,viewResultsFix,debugPane, debugPaneFIX,firstPage,autoPlayInfo,index;
    private TextButton runningButton, doneButton, resultsButton, autoPlay;
    public Skin managerSkin;
    private ScrollPane leftSP,rightSP;
    private Table bottomBar;
    private TextButton debugButton;

    public ManagerWindow(DesktopGameApp app, GameLoader gameLoader) {
        super(app);
        this.gameLoader= gameLoader;
        this.managerSkin = app.game.gameLoader.getSkin("menu");
        create();
    }

    //--------------------------------------------------------------------------CREATE
    @Override
    public void start(){
    }
    public void create(){
        index = createIndex();
        createAppPages();
        leftSP= new ScrollPane(runningEvents);
        leftSP.setScrollingDisabled(true,false);
        rightSP= new ScrollPane(runningEventsFix);
        rightSP.setScrollingDisabled(true,false);
        //---------------------------------CONSTRUCT PAGE
        this.align(Align.left);
        this.row();
        this.add(index).fillX().colspan(2).row();
        this.add(leftSP).grow().fill();
        this.add(rightSP).grow().fill().row();
        this.add(construct_bottomBar()).fillX().colspan(2);

    }
    private Table createIndex() {
        Table table = new Table(managerSkin);
        table.background("black").align(Align.left);
        create_IndexButtons();

        table.add(runningButton).growX().pad(20,0,0,0);
        table.add(doneButton).growX().pad(20,0,0,0);
        table.add(resultsButton).growX().pad(20,0,0,0);
        table.add(debugButton).growX().pad(20,0,0,0);
        table.add(autoPlay).growX().pad(20,0,0,0);
        table.pack();
        return table;
    }
    private void create_IndexButtons() {
        runningButton = new TextButton("Current Tasks",managerSkin);
        runningButton.getStyle().checked=gameLoader.getPicture("wine_background");//runningButton.getStyle().down;
        runningButton.setChecked(true);
        runningButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                focus_runningPage();
            }
        });
        doneButton= new TextButton(" Done Tasks ",managerSkin);
        doneButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                focus_donePage();
            }
        });
        resultsButton= new TextButton(" IA_Results ",managerSkin);
        resultsButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                focus_resultsPage();
            }
        });
        autoPlay= new TextButton("    Auto_play  ",managerSkin);
        autoPlay.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                focus_autoPlayPage();
            }
        });
        debugButton = new TextButton("    Debug Data  ",managerSkin);
        debugButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                focus_debugPage();
            }
        });
    }
    private void createAppPages() {

        firstPage = construct_firstPage();
        if(gameLoader!=null) {
            runningEvents = construct_runningPage(false);
            doneEvents = construct_donePage(false);
            viewResults = construct_resultsPage(false);
            debugPane = construct_debugPane(false);
        }
        else {
            runningEvents = firstPage;
            doneEvents = firstPage;
            viewResults = firstPage;
            debugPane = firstPage;
        }
        runningEventsFix = construct_runningPage(true);
        doneEventsFIX = construct_donePage(true);
        viewResultsFix = construct_resultsPage(true);
        debugPaneFIX=construct_debugPane(true);
        autoPlayInfo = construct_autoPlayInfo();
        bottomBar= construct_bottomBar();
    }


    //--------------------------------------CONSTRUCT PAGES
    private Table construct_autoPlayInfo() {
        Table table= gameLoader.superPool.obtain_table(managerSkin);
        table.setBackground( gameLoader.getPicture("wine_background"));


        return table;
    }
    private Table construct_debugPane(boolean fixed) {
        Table table= gameLoader.superPool.obtain_table(managerSkin);
        table.setBackground( gameLoader.getPicture("wine_background"));
        if(fixed){

        }else{
            table.defaults().align(Align.right).pad(5,2,5,2).fillX();
            table.add(gameLoader.superPool.obtain_label("   ARRAYS:  ", managerSkin)).row();
            table.add("PendingEvents: size "+ gameLoader.getPendingEvents().size).row();
            table.add("RunningEvents: size "+ gameLoader.getRunningEvents().size).row();
            table.add("FireEventsQueue: size "+ gameLoader.getFireEventsQueue().size).row();
            table.add("DoneEvents: size "+ gameLoader.getDoneEvents().size).row();
        }
        return table;
    }
    private Table construct_bottomBar() {
        Table bar = new Table(managerSkin);
        bar.background("black").align(Align.bottomLeft);
        Label currentMoney = gameLoader.superPool.obtain_label(gameLoader.getCurrentMoney()+" $  ",managerSkin);;
        Label currentReputation = gameLoader.superPool.obtain_label(gameLoader.getCurrentReputation()+" *  ",managerSkin);
        bar.add(currentMoney).pad(2,10,2,10);
        bar.add(currentReputation).pad(2,10,2,10);
        bar.add("some value").pad(2,10,2,10);
        return bar;
    }
    private Table construct_firstPage() {
        Table table=new Table(managerSkin);
        table.setBackground( gameLoader.getPicture("wine_background"));
        return table;
    }
    private Table construct_resultsPage(boolean fixed) {
        Table table=new Table(managerSkin);
        table.setBackground( gameLoader.getPicture("wine_background"));
        return table;
    }
    private Table construct_donePage(boolean fixed) {
        Table table = new Table(managerSkin);
        table.defaults().align(Align.left).pad(2,2,2,2);
        table.setBackground( gameLoader.getPicture("wine_background"));
        table.add().pad(20,10,20,10).row();
        if(fixed){
        }
        else {
            if (gameLoader.getRunningEvents().notEmpty()) {
                for (GameEvent gameEvent : gameLoader.getDoneEvents()) {
//                    table.add(gameLoader.superPool.obtain_image(gameEvent.getImages()[0]));
                    table.add(gameEvent.getTitle()).row();
                    final TextButton goTo = gameLoader.superPool.obtain_textButton("view",gameLoader.getSkin("BIOS"));

                    goTo.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            gameEvent.game.getDesktop().showGameEvent_PageApp(gameEvent);
                        }
                    });
//                    table.add(skip);
                    table.add(goTo).row();
                }
            }
        }
        return table;
    }
    private Table construct_runningPage(boolean fixed) {
        Table table = new Table(managerSkin);
        table.defaults().align(Align.left).pad(2,2,2,2);
        table.setBackground( gameLoader.getPicture("wine_background"));
        table.add("").pad(20,10,20,10).row();
        if(fixed){
        }
        else {
            if (gameLoader.getRunningEvents().notEmpty()) {
                for (GameEvent gameEvent : gameLoader.getRunningEvents()) {
                    final ProgressBar progressBar = new ProgressBar(0.0f, 1.0f, 0.1f, false, gameLoader.getSkin("BIOS"));
                    progressBar.setName(gameEvent.getId() + "_progressBar");
                    table.add(gameEvent.getTitle()).row();
                    table.add(progressBar);
                    final TextButton skip = gameLoader.superPool.obtain_textButton(">>skip",gameLoader.getSkin("BIOS"));
                    final TextButton goTo = gameLoader.superPool.obtain_textButton("view",gameLoader.getSkin("BIOS"));
                    skip.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            gameEvent.forceCompleteEvent();
                        }
                    });
                    skip.setSkin(gameLoader.getSkin("BIOS"));
                    goTo.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            gameEvent.game.getDesktop().showGameEvent_PageApp(gameEvent);
                        }
                    });
                    table.add(skip);
                    table.add(goTo).row();
                }
            }else{
                table.add("0 running tasks");
            }
        }
        return table;
    }

    //---------------------------------------------------------------------------------
    //--------------------------------------FOCUS METHODS
    private void focus_update(){
        if(runningButton.isChecked()){
            focus_runningPage();
        }
        if(doneButton.isChecked()){
            focus_donePage();
        }
        if(resultsButton.isChecked()){
            focus_resultsPage();
        }
        if(autoPlay.isChecked()){
            focus_autoPlayPage();
        }
        if(debugButton.isChecked()){
            focus_debugPage();
        }
    }
    private void focus_debugPage() {
        runningButton.setChecked(false);
        doneButton.setChecked(false);
        resultsButton.setChecked(false);
        autoPlay.setChecked(false);
        debugButton.setChecked(true);
        leftSP.setActor(debugPaneFIX);
        rightSP.setActor(debugPane);
        update(false);
    }
    private void focus_autoPlayPage() {
        runningButton.setChecked(false);
        doneButton.setChecked(false);
        resultsButton.setChecked(false);
        autoPlay.setChecked(true);
        debugButton.setChecked(false);
        leftSP.setActor(firstPage);
        rightSP.setActor(autoPlayInfo);
        update(false);
    }
    private void focus_resultsPage() {
        runningButton.setChecked(false);
        doneButton.setChecked(false);
        resultsButton.setChecked(true);
        autoPlay.setChecked(false);
        debugButton.setChecked(false);
        leftSP.setActor(viewResults);
        rightSP.setActor(viewResultsFix);
        update(false);
    }
    private void focus_donePage() {
        runningButton.setChecked(false);
        doneButton.setChecked(true);
        resultsButton.setChecked(false);
        autoPlay.setChecked(false);
        debugButton.setChecked(false);
        leftSP.setActor(doneEvents);
        rightSP.setActor(doneEventsFIX);
        update(false);
    }
    private void focus_runningPage() {
        runningButton.setChecked(true);
        doneButton.setChecked(false);
        resultsButton.setChecked(false);
        autoPlay.setChecked(false);
        debugButton.setChecked(false);
        leftSP.setActor(runningEvents);
        rightSP.setActor(runningEventsFix);
        update(false);
    }

    //--------------------------------------UTIL METHODS
    public void updateManager(Boolean reconstruct) {
        if(reconstruct){
            dispose();
            createAppPages();
            ((AppWindow) this).update(true);
        }
        for(GameEvent gameEvent: gameLoader.getRunningEvents()){
            try {
                ((ProgressBar) this.runningEvents.findActor(gameEvent.getId() + "_progressBar")).setValue(gameEvent.getProgress());
            }catch (Exception e){
                System.out.println(gameEvent.getId() + "_progressBar = ERR_NOT_FOUND");
                //enmascarandooo
            }
        }
        try{
            Cell cell = this.getCell(bottomBar);
            bottomBar=construct_bottomBar();
            cell.setActor(bottomBar);
        }
        catch (Exception e){
            //enmascarandooo
        }
        doneEvents=construct_donePage(false);
        doneEventsFIX=construct_donePage(true);
        update(false);
        focus_update();
    }
    @Override
    public AppPage currentAppPage() {
        return currentAppPage;
    }



    private void disposePages(){

        gameLoader.superPool.free(runningEvents);
        gameLoader.superPool.free(runningEventsFix);
        gameLoader.superPool.free(doneEvents);
        gameLoader.superPool.free(doneEventsFIX);
        gameLoader.superPool.free(viewResults);
        gameLoader.superPool.free(viewResultsFix);
        gameLoader.superPool.free(debugPane);
        gameLoader.superPool.free(debugPaneFIX);
        gameLoader.superPool.free(autoPlayInfo);
        gameLoader.superPool.free(firstPage);
        runningEvents = null;
        runningEventsFix = null;
        doneEvents = null;
        doneEventsFIX = null;
        viewResults = null;
        viewResultsFix = null;
        debugPane=null;
        debugPaneFIX=null;
        autoPlayInfo = null;
        firstPage =null;
    }

}
