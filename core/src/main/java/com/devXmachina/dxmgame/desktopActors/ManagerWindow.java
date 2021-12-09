package com.devXmachina.dxmgame.desktopActors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.devXmachina.dxmgame.DxM_Game;
import com.devXmachina.dxmgame.GameLoader;
import com.devXmachina.dxmgame.gamelogic.EventManager;
import com.devXmachina.dxmgame.gamelogic.GameEvent;

public class ManagerWindow extends AppWindow{
    private final EventManager eventManager;
    private Table runningEvents,runningEventsFix,doneEvents,doneEventsFIX,viewResults,viewResultsFix,debugPane, debugPaneFIX,firstPage,autoPlayInfo,index;
    private TextButton runningButton, doneButton, resultsButton, autoPlay;
    public Skin managerSkin;
    private ScrollPane leftSP,rightSP;
    private Table bottomBar;
    private TextButton debugButton;
    private Array<ProgressBar> progressBarsArr;
    private String log;

    public ManagerWindow(DesktopGameApp app, GameLoader gameLoader) {
        super(app);
        this.eventManager = gameLoader.eventManager;
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
        progressBarsArr = new Array<>();
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
        table.add(debugButton).growX().pad(20,0,0,0);
        table.add(autoPlay).growX().pad(20,0,0,0);
        table.add(resultsButton).growX().pad(20,0,0,0);
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


    public void get_autoDecadeLog(String toString) {
        this.log=toString;
    }
    //--------------------------------------CONSTRUCT PAGES
    private Table construct_autoPlayInfo() {
        Table table= gameLoader.superPool.obtain_table(managerSkin);
        table.setBackground( gameLoader.getPicture("wine_background"));
        table.defaults().align(Align.center);
        table.add("     Activa aquesta opció\n    per avancar de decada,\n  la IA de l'aplicacio respondra \n  automaticament a les tasques \n d'aquesta decada segons la teva \n informacio i la prediccio de la ML").row();
        table.add().row();

        TextButton autoPlay_button = new TextButton(" AutoPlay ", managerSkin);
        autoPlay_button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameLoader.setAutoPlay(true);
                gameLoader.setDebugMode(true);
                gameLoader.game.executeAutoplay();
            }
        });
        table.add(autoPlay_button).padTop(20).row();

        return table;
    }
    private Table construct_debugPane(boolean fixed) {
        Table table= gameLoader.superPool.obtain_table(managerSkin);
        table.setBackground( gameLoader.getPicture("wine_background"));
        if(fixed){
            table.defaults().align(Align.right).pad(5, 2, 5, 2).fillX();
            if(gameLoader.isDebugMode()) {
                for (GameEvent gameEvent : eventManager.getEventList()) {
                    table.add(gameEvent.getId());
                    table.add("    " + gameEvent.getTitle());
                    table.add(": " + gameEvent.getEventState());
                    table.row();
                }
            }else{
                table.add("Activa el mode DEBUG.\n \n podras veure les dades internes del joc,\n a mes a mes, les tasques es completaran\n de manera inmediata per tal de \n poder recorrer tot el\n joc en menys temps");
            }
        }else{
            table.defaults().align(Align.right).pad(5,2,5,2).fillX();
            if(gameLoader.isDebugMode()) {
                table.add(gameLoader.superPool.obtain_label("   ARRAYS:  ", managerSkin)).row();
                table.add("PendingEvents: size "+ eventManager.getPendingEvents().size).row();
                table.add("RunningEvents: size "+ eventManager.getRunningEvents().size).row();
                table.add("FireEventsQueue: size "+ eventManager.getFireEventsQueue().size).row();
                table.add("DoneEvents: size "+ eventManager.getDoneEvents().size).row();
                TextButton virus_button = new TextButton("DON'T TOUCH ME!", managerSkin);
                virus_button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        gameLoader.game.executeVirus();
                    }
                });
                TextButton endGame_button = new TextButton("END GAME", managerSkin);
                endGame_button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        gameLoader.endGame();
                    }
                });
                TextButton increase_fireSpeed_bttn = new TextButton("++", managerSkin);
                TextButton decrease_fireSpeed_bttn = new TextButton("--", managerSkin);
                increase_fireSpeed_bttn.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        eventManager.increase_fireSpeed(true);
                    }
                });
                decrease_fireSpeed_bttn.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        eventManager.increase_fireSpeed(false);
                    }
                });
                table.add(virus_button);
                table.row();
                table.add(endGame_button);
                table.row();
                table.add().fillY();
                table.row();
                table.add("Fire Speed "+ eventManager.fireSpeed+": ");
                table.add(decrease_fireSpeed_bttn);
                table.add(increase_fireSpeed_bttn);
                table.row();
            }else{
                TextButton debug_button = new TextButton("ACTIVATE DEBUG_MODE", managerSkin);
                debug_button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        gameLoader.setDebugMode(true);
                        gameLoader.game.getMenu().update_debugButton();
                        update(true);
                    }
                });
                table.add(debug_button);
                table.row();
            }

        }
        return table;
    }
    private Table construct_bottomBar() {
        Table bar = new Table(managerSkin);
        bar.background("black").align(Align.bottomLeft);
        Label currentMoney = gameLoader.superPool.obtain_label(gameLoader.getCurrentMoney()+" $  ",managerSkin);
        Label currentReputation = gameLoader.superPool.obtain_label(gameLoader.getCurrentReputation()+" *  ",managerSkin);
        bar.add(currentMoney).pad(2,10,2,10);
        bar.add(currentReputation).pad(2,10,2,10);
        bar.add(gameLoader.getCurrentDecade().toString()).pad(2,10,2,10);
        return bar;
    }
    private Table construct_firstPage() {
        Table table=new Table(managerSkin);
        table.setBackground( gameLoader.getPicture("wine_background"));
        table.add(this.log);
        return table;
    }
    private Table construct_resultsPage(boolean fixed) {
        Table table=new Table(managerSkin);
        if(!fixed) {

            table.setBackground( "black");
            if(gameLoader.resultStructArray!=null){
                String structStr = "";
                for (int i = 0; i < gameLoader.resultStructArray.size; i++) {
                    GameEvent gameEvent= eventManager.find_gameEvent(gameLoader.resultStructArray.get(i).gameEvent_id);
                    if(gameEvent.isActive()) {
                        Label label = new Label(structStr, gameLoader.getSkin("BIOS"));
                        label.setFontScale(0.4f, 0.54f);
                        int conf = (int) (gameLoader.resultStructArray.get(i).ratio * 100);
                        structStr += "Task:  " + gameEvent.getTextBlocs()[gameEvent.getTextBlocs().length - 1] + "\n  Predicted: " + gameLoader.resultStructArray.get(i).result + " confidence:" + conf + "% ";
                        if (gameLoader.resultStructArray.get(i).playerResult != null && (!(gameLoader.resultStructArray.get(i).playerResult.equalsIgnoreCase("null")))) {
                            if (gameEvent.answer.equalsIgnoreCase(gameLoader.resultStructArray.get(i).result)) {
                                label.setColor(Color.CHARTREUSE);
                            } else {
                                label.setColor(Color.BLUE);
                            }
                            structStr += "   Your Answer: " + gameLoader.resultStructArray.get(i).playerResult;
                        } else {
                            structStr += "                ";
                        }
                        structStr += "\n";
                        label.setText(structStr);
                        label.pack();
                        structStr = "";
                        table.add(label).padTop(25).row();
                        table.add().row();
                    }
                }


            }

        }else{

            table.setBackground( gameLoader.getPicture("wine_background"));
            if (gameLoader.testAnswer != null) {
                table.add(" Respostes Questionari");
                table.row();
                String testAnswers = "";
                for (int i = 0; i < gameLoader.testAnswer.length; i++) {
                    testAnswers += "Pregunta_" + i + ": " + gameLoader.testAnswer[i] + "\n";
                }
                table.add(testAnswers).row();
            }
        }
        return table;
    }
    private Table construct_donePage(boolean fixed) {
        Table table = new Table(managerSkin);
        table.defaults().align(Align.left).pad(2,2,2,2);
        table.setBackground( gameLoader.getPicture("wine_background"));
        table.add().pad(20,10,20,10).row();
        if(fixed){
            table.add(eventManager.getDoneEvents().size+" TASQUES COMPLETADES\n \n" +
                    " Aqui pots veure les tasques \n" +
                    "principals ja completades.\n\n\n" +
                    " Prem al boto VIEW per veure'n\n total la informacio \n" +
                    " ... \n\n\n\n\n\n\n\n\n").row();

        }
        else {
            if (eventManager.getDoneEvents().notEmpty()) {
                for (GameEvent gameEvent : eventManager.getDoneEvents()) {
//                       table.add(gameLoader.superPool.obtain_image(gameEvent.getImages()[0]));
                    if(!(gameEvent.getDesktopGameApp().equals(DxM_Game.DesktopAppType.CONSOLE))) {
                        table.add(gameEvent.getTitle()).row();
                        final TextButton goTo = gameLoader.superPool.obtain_textButton("view", gameLoader.getSkin("BIOS"));

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
        }
        return table;
    }
    private Table construct_runningPage(boolean fixed) {
        Table table = new Table(managerSkin);
        table.defaults().align(Align.left).pad(2,2,2,2);
        table.setBackground( gameLoader.getPicture("wine_background"));
        table.add("").pad(20,10,20,10).row();
        table.defaults().pad(10,5,10,5);
        if (fixed) {
            table.add(eventManager.getRunningEvents().size+" TASQUES EN PROCES").pad(10,4,2,4).row();
            if (eventManager.getRunningEvents().notEmpty()) {
                for (GameEvent gameEvent : eventManager.getRunningEvents()) {
                    final ProgressBar progressBar = new ProgressBar(0.0f, gameEvent.duration, 0.1f, false, gameLoader.getSkin("BIOS"));
                    progressBar.setName(gameEvent.getId() + "_progressBar");
                    progressBarsArr.add(progressBar);
                    table.add(gameEvent.getTitle()).pad(10,4,2,4).row();
                    table.add(progressBar).pad(2,4,2,4);
                    final TextButton skip = gameLoader.superPool.obtain_textButton(">>skip", gameLoader.getSkin("BIOS"));
                    final TextButton goTo = gameLoader.superPool.obtain_textButton("view", gameLoader.getSkin("BIOS"));
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
                table.add(" Aqui es mostren les tasques que,\n"+
                        "un cop acceptades, es troben\n"+
                        "en proces de completarse.\n\n"+
                        "Prem el boto de SKIP\n"+
                        "per completar la tasca \n inmediatament\n\n\n\n").row();
            }
        }
        else {
            table.add(eventManager.getPendingEvents().size + " TASQUES PENDENTS").row();

            if (eventManager.getPendingEvents().notEmpty()) {
                table.add(" Les tasques principals\n que fan avançar la historia \n" +
                    " estan marcades amb un \" * \" \n").row();
                for (GameEvent gameEvent : eventManager.getPendingEvents()) {
                    String str="";
                    if(gameEvent.isTestEvent()){str="* ";}
                    str+= gameEvent.getTitle();
                    table.add(str);
                    table.add(gameEvent.getDesktopGameApp()+ " // " + gameEvent.getAppWebPage().name()).row();

                    table.add("COST: "+ gameEvent.printCost(true) + " / " +gameEvent.printCost(false));

                    final TextButton goTo = gameLoader.superPool.obtain_textButton("view", gameLoader.getSkin("BIOS"));
                    goTo.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            gameEvent.game.getDesktop().showGameEvent_PageApp(gameEvent);
                        }
                    });
                    table.add(goTo).row();
                }
            }else{
                table.add(" Aqui pots veure les tasques enviades\n" +
                        " i pendents de resposta.\n\n" +
                        " Les tasques principals que \n fan avançar la historia \n" +
                        " estan marcades amb un \" * \" \n" +
                        " ... \n\n\n\n\n\n\n\n\n").row();
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
            progressBarsArr.clear();
            dispose();
            createAppPages();
            ((AppWindow) this).update(true);
        }
        for(GameEvent gameEvent: eventManager.getRunningEvents()){
            try {
                for(ProgressBar bar: progressBarsArr){
                    if (bar.getName().contains(gameEvent.getId())){
                        bar.setValue(gameEvent.getProgress());
                    }
                }
            }catch (Exception e){
                System.out.println(gameEvent.getId() + "_progressBar = ERR_NOT_FOUND");
                System.out.println(e);
                //enmascarandooo
            }
        }
        try{
            bottomBar=construct_bottomBar();
            this.getCells().get(this.getCells().size-1).setActor(bottomBar);
        }
        catch (Exception e){
            System.out.println("ERR_ Updating manager bottomBar");
            System.out.println(e);
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
