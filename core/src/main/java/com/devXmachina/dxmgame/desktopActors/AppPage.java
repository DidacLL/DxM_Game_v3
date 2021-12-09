package com.devXmachina.dxmgame.desktopActors;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.devXmachina.dxmgame.DxM_Game;
import com.devXmachina.dxmgame.GameLoader;
import com.devXmachina.dxmgame.gamelogic.GameEvent;
import com.devXmachina.dxmgame.gamelogic.ResultStruct;


public class AppPage extends Table implements Pool.Poolable {

//-----------------------------INDEX_EVENT_CONSTANTS
    private final static int FACEBOOK_PROFILE_IMG = 0;
    private final static int FACEBOOK_COVER_IMG = 1;
    private final static int FACEBOOK_NAME_TXT = 0;
    private final static int FACEBOOK_TEXT_TXT=2;
    private final static int FACEBOOK_BUTTONA_TXT=3;
    private final static int FACEBOOK_BUTTONB_TXT=4;
    private final static int FACEBOOK_NOTIFICATION_TXT=4;
    private final static int MAIL_SIGNATURE_IMG = 0;
    private final static int MAIL_IMG = 1;
    private final static int MAIL_FROM_TXT=0;
    private final static int MAIL_SUBJ_TXT=1;
    private final static int MAIL_TEXT_TXT=2;
    private final static int MAIL_BUTTON_A_TXT = 3;
    private final static int MAIL_BUTTON_B_TXT =4;
    private final static int MAIL_NOTIFICATION_TXT=5;
    private static final int GITHUB_COVER_IMG = 1;
    private static final int GITHUB_IMG = 0;
    private static final int NEWS_TITLE_TXT=0;
    private static final int NEWS_TEXT1_TXT=1;
    private static final int NEWS_TEXT2_TXT=2;
    private static final int NEWS_COVER_IMG=0;
    private static final int NEWS_BODY_IMG=1;
//-----------------------------    END
    public DxM_Game.BrowserURL webPageURL;
    DesktopGameApp app;
    GameLoader gameLoader;
    Array<Table> webBodies;
    Array<Button> eventButtons;
    Table currentBody;
    ScrollPane indexBox,bodySP;
    GameEvent currentEvent;
    public Array<GameEvent> assignedEvents;
    boolean updateIndex, updatePage;
    private Drawable blue_button_bckgrnd;

    public AppPage(Skin skin, DxM_Game.BrowserURL browserURL, DesktopGameApp app, GameLoader gameLoader) {
        super(skin);
        construct_AppPage(browserURL, app,gameLoader);

    }

    public void construct_AppPage(DxM_Game.BrowserURL browserURL, DesktopGameApp app,GameLoader gameLoader) {
        this.app= app;
        this.gameLoader=gameLoader;
        this.align(Align.topLeft);
        this.webPageURL = browserURL;
        this.assignedEvents = new Array<>();
        this.eventButtons =new Array<>();
        this.blue_button_bckgrnd= gameLoader.getPicture("MAIL_index_button_chk");
        webBodies = new Array<>();
        bodySP= new ScrollPane(createBody(webPageURL,null));
        bodySP.layout();
        mainfullpage_construct();
        if(browserURL.equals(DxM_Game.BrowserURL.MAIL)||browserURL.equals(DxM_Game.BrowserURL.FACEBOOK)){
            this.setSkin(gameLoader.getSkin("BLUEDESK"));
            getSkin().get(Button.ButtonStyle.class).checked=blue_button_bckgrnd;
        }else{
            this.setSkin(gameLoader.getSkin("DESKTOP"));
            getSkin().get(Button.ButtonStyle.class).checked=getSkin().get(Button.ButtonStyle.class).down;
        }
    }

    private void mainfullpage_construct() {
        this.add(createHead(webPageURL)).align(Align.top).growX().colspan(2).expandX();
        this.row();
        if(webPageURL.equals(DxM_Game.BrowserURL.FACEBOOK)||webPageURL.equals(DxM_Game.BrowserURL.MAIL)||
                webPageURL.equals(DxM_Game.BrowserURL.GITHUB)||webPageURL.equals(DxM_Game.BrowserURL.WIKI)){
            indexBox= new ScrollPane(createIndex(webPageURL));
            this.add(indexBox).align(Align.left).growY();
        }
        this.add(bodySP).align(Align.right).grow();
    }

    public void start(){

    }
    public void update(){
        clean_assignedEvents();
        if(this.webPageURL.equals(DxM_Game.BrowserURL.WIKI)&&updatePage) {
            bodySP.setActor(currentBody);
            updatePage=false;
        }else if ( this.webPageURL.equals(DxM_Game.BrowserURL.NEWS)&&updatePage){
            bodySP.setActor(newsBodyConstruct(gameLoader.superPool.obtain_table(getSkin())));
            updatePage=false;
        }else {
            if (updateIndex) {
                reLoadIndex();
                updateIndex = false;
            }
            if (updatePage) {
                bodySP.clear();
                if (currentEvent != null) {
                    currentBody = searchBody(currentEvent);
                    if (!currentEvent.viewed) {
                        app.toogle_IconNotification(false);
                        currentEvent.viewed = true;
                        app.dockIcon.update();
                    }
                } else {
                    if (webBodies.notEmpty()) {
                        if (currentBody != webBodies.first()) {
                            currentBody = webBodies.first();
                        }
                    }
                }
                bodySP.setActor(currentBody);
                updatePage = false;
            }
        }
    }
    //----------------------------------------------------------------------------Index_Constructors
    private Table createIndex(DxM_Game.BrowserURL browserURL) {
        Table table = gameLoader.superPool.obtain_table(getSkin());
        String name;
        switch (browserURL){
            case FACEBOOK:
                name=this.webPageURL.name();
                table= constructIndexFields(facebookIndexConstruct(table), name);
                table.pack();
                break;
            case GITHUB:
                name=this.webPageURL.name();
                table= constructIndexFields(githubIndexConstruct(table), name);
                table.pack();
                break;
            case BLANK:
                break;
            case WIKI:
                name=this.webPageURL.name();
                table= wikiIndexConstruct(table);
                table.pack();
                break;
            case MAIL:
                name=this.webPageURL.name();
                table= constructIndexFields(mailIndexConstruct(table), name);
                table.pack();
                break;
            default:
                table = new Table(getSkin());
                table.setBackground("white-rect");
                break;
        }
        return table;
    }

    private Table mailIndexConstruct(Table table) {
        table.setBackground(app.game.gameLoader.getPicture("MailBCKGRND"));
        table.add(new Label(" ::INBOX:: ", getSkin(),"medium", Color.WHITE)).pad(20,20,20,20).align(Align.topLeft);
        table.row();
        return table;
    }
    private Table wikiIndexConstruct(Table table) {
        table.setBackground("progress-bar-horizontal");
        table.defaults().align(Align.top).pad(10,10,10,10).fillX();
        table.add(gameLoader.superPool.obtain_label("Index", getSkin())).padLeft(20.0f).padRight(20.0f).align(Align.topLeft);
        table.row();
        Table memberBody = createWikiBody("MEMBERS", "Dxm Members");
        Table conceptsBody = createWikiBody("CONCEPTS","Conceptes Basics");
        Table iaBody = createWikiBody("IA", "     I.A.   ");
        Table openSourceBody = createWikiBody("OPENSOURCE", "OpenSource");
        create_wikiButton("MEMBERS","DxM_Members",memberBody);
        create_wikiButton("CONCEPTS","Conceptes",conceptsBody);
        create_wikiButton("IA"," I.A. ",iaBody);
        create_wikiButton("OPENSOURCE","OpenSource",openSourceBody);

        for(Button button:eventButtons){
            table.add(button);
            table.row();
        }
        table.row();
        table.add().growY().expandY().fill();
        return table;
    }

    private Table createWikiBody(String sectionREF,String sectionName) {
        Table table = gameLoader.superPool.obtain_table(getSkin());
        table.setBackground("white-rect");
        Label titleLabel = new Label(sectionName,getSkin(), "title");
        titleLabel.setFontScale(0.6f);
        table.add(titleLabel).expandX().row();
        for (String element : gameLoader.wiki_txt) {
            String[] article = element.split("//");
            if(article[0].equalsIgnoreCase(sectionREF)) {
                table.row();
                table.add(article[1]).align(Align.topLeft).row();
                for (int i = 2; i < article.length; i++) {
                    Label label = gameLoader.superPool.obtain_label("",getSkin());
                    label.setText(article[i]);
                    label.setWrap(true);
                    table.add(label).align(Align.topLeft).fillX().growY().pad(12,10,10,10);
                    table.row();
                }
                table.row();
                table.add().padTop(20);
            }
        }
        table.setName(sectionREF);
        return table;
    }

    private void create_wikiButton(String sectionREF, String sectionName, Table wikiBody) {
        Button button;
        button = gameLoader.superPool.obtain_button(getSkin());
        button.setName(sectionREF);
        button.add(gameLoader.superPool.obtain_label(sectionName, getSkin())).expandX();
        button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                updatePage = true;
                uncheckButtons();
                searchButton(sectionREF).setChecked(true);
                currentBody = wikiBody;
                update();
            }
        });
        eventButtons.add(button);

    }


    private Table githubIndexConstruct(Table table) {
        table.setBackground("black");
        table.add(gameLoader.superPool.obtain_label("Collaborations:", getSkin())).padLeft(20.0f).padRight(20.0f).align(Align.left);
        table.row();
        return table;
    }
    private Table facebookIndexConstruct(Table table) {
        table.setBackground("progress-bar-startup");
        table.add(gameLoader.superPool.obtain_label("Best Friends", getSkin())).pad(20,20,20,20).align(Align.topLeft);
        table.row();
        return table;
    }

    private Table constructIndexFields(Table index, String appName) {
        appName += "_index_button";
        clean_assignedEvents();
        if(assignedEvents!=null){
            Array<Button> pending;
            Array<Button> accepted;
            pending=new Array<>();
            accepted=new Array<>();
            for (GameEvent gameEvent : assignedEvents) {
                Table body = createBody(gameEvent.getAppWebPage(),gameEvent);
                body.setName(gameEvent.getId());
                webBodies.add(body);
                constructIndexButton(appName, pending, accepted, gameEvent,this.getSkin());
            }
            order_indexButtons(pending,accepted,index);
            pending.clear();
            accepted.clear();
        }

        index.row();
        index.add().growY().expandY().fill();
        return index;
    }

    private void clean_assignedEvents() {
        GameEvent gameEvent;
        if(assignedEvents.notEmpty()) {
            for (int i = 0; i < assignedEvents.size; i++) {
                gameEvent = assignedEvents.get(i);
                if(gameEvent.getDecade().ordinal()+1<gameLoader.getCurrentDecade().ordinal()&&(!(gameEvent.getEventState().equals(DxM_Game.EventState.PENDING)))){
                    assignedEvents.removeIndex(i);
                }
                else if(gameEvent.getEventState().equals(DxM_Game.EventState.DISCARDED)){
                    assignedEvents.removeIndex(i);
                }
            }
        }
    }

    private void constructIndexButton(String appName, Array<Button> pending, Array<Button> accepted, GameEvent gameEvent,Skin skin) {
        Button button;
        button = gameLoader.superPool.obtain_button(skin);
        button.setName(gameEvent.getId()+"_button");
        if (gameEvent.getEventState().equals(DxM_Game.EventState.PENDING)) {
            button.add(gameLoader.superPool.obtain_image("unread"+ appName)).expandY();
            pending.add(button);
        }else{
            button.add(gameLoader.superPool.obtain_image(appName)).expandY();
            accepted.add(button);
        }
        button.add(gameLoader.superPool.obtain_label(gameEvent.getTitle(), getSkin())).expandX();
        button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                currentEvent = gameEvent;
                updatePage = true;
                uncheckButtons();
                searchButton(gameEvent.getId()).setChecked(true);
//                gameEvent.viewed=true;
                if(app.appType.equals(DxM_Game.DesktopAppType.MAIL)){
                    searchButton(gameEvent.getId()).getCells().get(0).setActor(new Image(app.game.gameLoader.getPicture("MAIL_index_button_open")));
                }

            }
        });
        eventButtons.add(button);
    }

    //-----------------------------------------------------------------------------Body_Constructors
    private Table createBody(DxM_Game.BrowserURL browserURL, GameEvent gameEvent) {
        Table bodyTable;
        bodyTable = gameLoader.superPool.obtain_table(getSkin());
        switch (browserURL){
            case FACEBOOK:
                bodyTable.setBackground("progress-bar-startup");
                if (gameEvent!=null){
                    facebookBodyConstruct(gameEvent, bodyTable);
                }else {
                    //--------------------------------------------------------------------main about:blank page
                    bodyTable.setName("blank");
                }
                webBodies.add(bodyTable);
                break;
            case GITHUB:
                bodyTable.setBackground("white-rect");
                if (gameEvent!=null){
                    githubBodyConstruct(gameEvent, bodyTable);
                }else {
                    //--------------------------------------------------------------------main about:blank page
                    bodyTable.setName("blank");
                }
                webBodies.add(bodyTable);
                break;
            case BLANK:
                break;

            case MAIL:
                bodyTable.setBackground("white-rect");
                if (gameEvent!=null){
                   mailBodyConstruct(gameEvent, bodyTable);
                }else {
                    //--------------------------------------------------------------------main about:blank page
                    bodyTable.setName("blank");
                }
                webBodies.add(bodyTable);
                break;
            case NEWS:
                bodyTable.setBackground("white-rect");
                currentBody= newsBodyConstruct(bodyTable);
                updatePage=true;
                webBodies.add(bodyTable);
                break;
            default:
                bodyTable.setBackground("white-rect");
                webBodies.add(bodyTable);
                break;
        }
        return  bodyTable;
    }
    private Table facebookBodyConstruct(GameEvent gameEvent, Table bodyTable) {
        bodyTable.add(gameLoader.superPool.obtain_image(gameEvent.getImages()[FACEBOOK_COVER_IMG])).growX().align(Align.top).colspan(3).pad(1,1,1,1);
        bodyTable.setName(gameEvent.getId()+"_body");
        bodyTable.row();
        bodyTable.add(gameLoader.superPool.obtain_image(gameEvent.getImages()[FACEBOOK_PROFILE_IMG])).align(Align.topLeft).pad(0,5,20,5);
        bodyTable.add(gameEvent.getTextBlocs()[FACEBOOK_NAME_TXT]).align(Align.left).pad(10,10,10,10);
        bodyTable.add().growX();
        bodyTable.row();
        bodyTable.add();
        Label label = new Label(gameEvent.getTextBlocs()[FACEBOOK_TEXT_TXT],getSkin());
        Label.LabelStyle stylenew = new Label.LabelStyle();
        stylenew.font=getSkin().get(Label.LabelStyle.class).font;
        stylenew.fontColor=getSkin().get(Label.LabelStyle.class).fontColor;
        stylenew.background= getSkin().getDrawable("white-rect");
        label.setStyle(stylenew);
        label.setWrap(true);
        bodyTable.add(label).align(Align.topLeft).pad(10,10,10,10).fillX().colspan(2).pad(20,20,60,60);
        bodyTable.row().growY();
        bodyTable.add().fill();
        bodyTable.row();
        bodyTable.add();
        return createEventButtons(gameEvent,bodyTable,gameEvent.getTextBlocs()[FACEBOOK_BUTTONA_TXT],gameEvent.getTextBlocs()[FACEBOOK_BUTTONB_TXT]);
    }
    private Table githubBodyConstruct(GameEvent gameEvent, Table bodyTable){
        bodyTable.add(gameLoader.superPool.obtain_image(gameEvent.getImages()[GITHUB_COVER_IMG])).growX().align(Align.top).colspan(3).pad(1,1,1,1);
        bodyTable.setName(gameEvent.getId()+"_body");
        bodyTable.row();
        bodyTable.add(gameLoader.superPool.obtain_image(gameEvent.getImages()[GITHUB_IMG])).align(Align.topLeft).pad(0,5,20,5);
        bodyTable.add(gameEvent.getTextBlocs()[MAIL_FROM_TXT]).align(Align.left).pad(10,10,10,10);
        bodyTable.add().growX();
        bodyTable.row();
        bodyTable.add();
        Label label = gameLoader.superPool.obtain_label(gameEvent.getTextBlocs()[MAIL_TEXT_TXT],getSkin());
        label.setWrap(true);
        bodyTable.add(label).align(Align.topLeft).pad(10,10,10,10).growX().colspan(2).pad(20,20,20,20);
        bodyTable.row().growY();
        bodyTable.add().fill();
        bodyTable.row();
        bodyTable.add();
        return createEventButtons(gameEvent,bodyTable,gameEvent.getTextBlocs()[MAIL_BUTTON_A_TXT],gameEvent.getTextBlocs()[MAIL_BUTTON_B_TXT]);
    }
    private Table newsBodyConstruct(Table bodyTable){
        bodyTable.setBackground("white-rect");
        if(assignedEvents.notEmpty()){
            Array<GameEvent> newNews = new Array<>();
            Array<GameEvent> oldNews= new Array<>();
            bodyTable.row();
            for(GameEvent newsEvent: assignedEvents){
                if(newsEvent.getEventState().equals(DxM_Game.EventState.PENDING)){
                    newNews.add(newsEvent);
                }else{
                    oldNews.add(newsEvent);

                }
            }
            for(GameEvent newEvent: newNews){
                final Label label = new Label(newEvent.getTitle(), getSkin(),"title");
                label.setFontScale(0.3f);
                bodyTable.add();
                bodyTable.add(label).align(Align.top).padTop(20).colspan(2).expandX().row();
                bodyTable.add(gameLoader.superPool.obtain_image(newEvent.getImages()[0])).align(Align.topLeft).pad(10,2,10,2);
                final Label textLabel =  gameLoader.superPool.obtain_label(newEvent.getTextBlocs()[1],getSkin());
                textLabel.setWrap(true);
                bodyTable.add(textLabel).align(Align.left).fillX().colspan(2).pad(10,10,10,10);
                bodyTable.row();
                createEventButtons(newEvent,bodyTable,newEvent.getTextBlocs()[3],newEvent.getTextBlocs()[4]);
                bodyTable.row();
            }
            for(GameEvent newEvent: oldNews){
                final Label label = new Label(newEvent.getTitle(), getSkin(),"title");
                label.setFontScale(0.3f);
                bodyTable.add();
                bodyTable.add(label).align(Align.top).padTop(20).colspan(2).row();
                bodyTable.add(gameLoader.superPool.obtain_image(newEvent.getImages()[0])).align(Align.topLeft).pad(10,2,10,2);
                final Label textLabel =  gameLoader.superPool.obtain_label(newEvent.getTextBlocs()[1],getSkin());
                textLabel.setWrap(true);
                bodyTable.add(textLabel).align(Align.left).growX().colspan(2).pad(10,10,10,10);
                bodyTable.row();
                createEventButtons(newEvent,bodyTable,newEvent.getTextBlocs()[3],newEvent.getTextBlocs()[4]);
                bodyTable.row();
            }
        }
        return bodyTable;
    }
    private Table mailBodyConstruct(GameEvent gameEvent, Table bodyTable){
        bodyTable.setBackground("progress-bar-startup");
        bodyTable.row().padTop(10);
        // create subject
        bodyTable.add("Subject").pad(10,10,10,10);
        Label subject= gameLoader.superPool.obtain_label(gameEvent.getTextBlocs()[MAIL_SUBJ_TXT],getSkin());
        subject.setWrap(true);
        Table subTable = gameLoader.superPool.obtain_table(getSkin());
        subTable.background("white-rect");
        subTable.add(subject).grow();
        bodyTable.add(subTable).growX().pad(10,10,10,10).row();
        //create from
        bodyTable.row().padTop(10);
        bodyTable.add("From").pad(10,10,10,10);
        subject= gameLoader.superPool.obtain_label(gameEvent.getTextBlocs()[MAIL_FROM_TXT],getSkin());
        subject.setWrap(true);
        subTable = gameLoader.superPool.obtain_table(getSkin());
        subTable.background("white-rect");
        subTable.add(subject).grow();
        bodyTable.add(subTable).growX().pad(10,10,10,10).row();
        //CREATE BODY MESSAGE
        bodyTable.row().padTop(10);
        bodyTable.add("Message").pad(10,10,10,10);

        subTable =gameLoader.superPool.obtain_table(getSkin());
        subTable.background("white-rect");
        subTable.pad(10,20,10,10).row();
        subject= gameLoader.superPool.obtain_label(gameEvent.getTextBlocs()[MAIL_TEXT_TXT],getSkin());
        subject.setWrap(true);
        subTable.add(subject).grow().row();

        bodyTable.add(subTable).grow().pad(10,10,10,10).row();
        bodyTable.add();
        Table buttonTable = createEventButtons(gameEvent,gameLoader.superPool.obtain_table(getSkin()),gameEvent.getTextBlocs()[MAIL_BUTTON_A_TXT],gameEvent.getTextBlocs()[MAIL_BUTTON_B_TXT]);
        buttonTable.add();
        bodyTable.add(buttonTable);
        return bodyTable;

    }


    //------------------------------------------privateMethods
    private Table searchBody(GameEvent gameEvent) {

        for (int i=0; i<webBodies.size;i++){
            if (gameEvent != null) {
                if (webBodies.get(i).getName().equalsIgnoreCase(gameEvent.getId())) {
                    return webBodies.get(i);
                }
            }
        }
        return null;
    }
    private void order_indexButtons(Array<Button> pending, Array<Button> accepted, Table index) {
        for (Button button: pending){
            index.add(button).fillX().align(Align.left).row();
        }
        for (Button button: accepted){
            index.add(button).fillX().align(Align.left).row();
        }

    }
    private void uncheckButtons() {
        for (int i=0; i<eventButtons.size;i++){
            if(this.webPageURL.equals(DxM_Game.BrowserURL.MAIL)){
                String name= eventButtons.get(i).getName();
                name= name.replace("_button","");
                GameEvent gameEvent= gameLoader.eventManager.getGameEvent_fromMainList(name);
                if (gameEvent.getEventState().equals(DxM_Game.EventState.PENDING)) {
                    eventButtons.get(i).getCells().get(0).setActor(gameLoader.superPool.obtain_image("unreadMAIL_index_button")).expandY();

                }else{
                    eventButtons.get(i).getCells().get(0).setActor(gameLoader.superPool.obtain_image("MAIL_index_button")).expandY();
                }

            }
           eventButtons.get(i).setChecked(false);
        }
    }
    private Button searchButton(String id) {
        for (int i=0; i<eventButtons.size;i++){
            Button currentButton = eventButtons.get(i);
            if(currentButton.getName()!=null){
                if(currentButton.getName().contains(id)){
                    return currentButton;
                }
            }
        }
        return null;
    }
    private Table createEventButtons(GameEvent gameEvent, Table bodyTable,String buttonAtxt,String buttonBtxt) {
        if (gameEvent.getEventState().equals(DxM_Game.EventState.PENDING)) {
            final TextButton buttonA = gameLoader.superPool.obtain_textButton("  "+buttonAtxt+"  ", getSkin());
            final TextButton buttonB = gameLoader.superPool.obtain_textButton("  "+buttonBtxt+"  ", getSkin());
            buttonA.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);

                    if(gameEvent.getEventCost(true)> gameLoader.getCurrentMoney()||gameEvent.getEventCost(false) > gameLoader.getCurrentReputation()){
                        Dialog dialog = new Dialog("", getSkin());
                        dialog.getTitleTable().reset();
                        Label label = new Label("ERR_NOT_ENOUGH_FOUNDS", getSkin());
                        label.setAlignment(Align.bottom);
                        dialog.getTitleTable().add(label).expand();
                        dialog.text("\n Hey "+ gameLoader.getUserName()+" ,\n \n  You don't have enough founds!   \n \n Current Money: "+gameLoader.getCurrentMoney()+"\n Current Reputation: "+gameLoader.getCurrentReputation()+" \n\n").button("...ok", true)
                                .key(Input.Keys.ENTER, true).key(Input.Keys.ESCAPE, false).show(app.desktop);
                    }else {
                        answer_event_page(gameEvent, gameEvent.handle_eventAnswer(true));
                    }
                }
            });
            buttonB.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    if(gameEvent.isDoubleAnswer()&&(!gameEvent.isVirus())){
                        if(gameEvent.getEventCost(true) > gameLoader.getCurrentMoney()||gameEvent.getEventCost(false) > gameLoader.getCurrentReputation()){
                            Dialog dialog = new Dialog("", getSkin());
                            dialog.getTitleTable().reset();
                            Label label = new Label("ERR_NOT_ENOUGH_FOUNDS", getSkin());
                            label.setAlignment(Align.bottom);
                            dialog.getTitleTable().add(label).expand();
                            dialog.text("\n Hey "+ gameLoader.getUserName()+" ,\n \n  You don't have enough founds!   \n \n Current Money: "+gameLoader.getCurrentMoney()+"\n Current Reputation: "+gameLoader.getCurrentReputation()+" \n\n").button("...ok", true)
                                    .key(Input.Keys.ENTER, true).key(Input.Keys.ESCAPE, false).show(app.desktop);
                        }else {
                            answer_event_page(gameEvent, gameEvent.handle_eventAnswer(false));
                        }
                    }
                    else {
                        answer_event_page(gameEvent, gameEvent.handle_eventAnswer(false));
                    }
                }
            });
            Table subTable= new Table(getSkin());
            subTable.add("COST: "+ gameEvent.printCost(true) + " / " +gameEvent.printCost(false));
            subTable.add(buttonA).align(Align.bottomLeft).pad(20,20,20,20);
            subTable.add(buttonB).align(Align.bottomLeft).pad(20,20,20,20);
            bodyTable.add(subTable).colspan(4).align(Align.center);
        }
        else {
            ResultStruct res = gameLoader.find_resultsAnswer(gameEvent.getId());
            if(res!=null) {
                int k = ((int) (res.ratio * 100));
                Label label = new Label("ML prediction: " + res.result.toUpperCase() + "\nConfidence " + k + "% \nYour Answer was : " + res.playerResult.toUpperCase(), gameLoader.getSkin("BIOS"));
                label.setFontScale(0.5f, 0.6f);
                label.setWrap(true);
                if (res.result.equalsIgnoreCase(res.playerResult)) {
                    label.setColor(Color.CHARTREUSE);
                } else {
                    label.setColor(Color.RED);
                }

                bodyTable.add(label).align(Align.bottomLeft).pad(20, 20, 20, 20).expandX().colspan(2);
            }else{
                Label label = gameLoader.superPool.obtain_label(" Done! ", getSkin());
                bodyTable.add(label).align(Align.bottomLeft).pad(20, 20, 20, 20).expandX().colspan(2);
            }

        }
        return bodyTable;
    }
    private void answer_event_page(GameEvent gameEvent, boolean confirm) {
        if (confirm) {
            webBodies.removeValue(currentBody,true);
            currentBody.clear();
            app.toogle_IconNotification(false);
            updatePage=true;
            updateIndex=true;
            update();
        } else {
            currentBody.clear();
            eventButtons.removeValue(searchButton(gameEvent.getId()), true);
            assignedEvents.removeValue(gameEvent, true);
            app.toogle_IconNotification(false);
            updatePage = true;
            reLoadIndex();
        }
    }
    public void add_gameEvent(GameEvent gameEvent){
        if(gameEvent.getDecade().ordinal()+1>=gameLoader.getCurrentDecade().ordinal()){
            this.assignedEvents.insert(0,gameEvent);
            updateIndex = true;
            updatePage = true;
        }
    }

    private Table createHead(DxM_Game.BrowserURL browserURL) {
        Label label;
        Table table;

        switch (browserURL){
            case FACEBOOK:
                table =gameLoader.superPool.obtain_table(this.getSkin());
                table.setBackground("progress-bar-startup");
                table.add(gameLoader.superPool.obtain_image("Face"));
                label = new Label("FaceMash.", getSkin(),"medium","white");
                label.setFontScale(2.f);
                table.add(label).padLeft(20.0f).padRight(0.0f).growX().fillX();
                break;
            case GITHUB:
                table =gameLoader.superPool.obtain_table(this.getSkin());
                table.setBackground("black");
                table.add(app.game.gameLoader.superPool.obtain_image("GitHub")).align(Align.center);
                label = new Label("_GitHub.", getSkin(),"title","white");
                label.setFontScale(0.5f);
                table.add(label).padLeft(0.0f).padRight(0.0f).growX().fillX().colspan(2);
                break;
            case BLANK:
                table=gameLoader.superPool.obtain_table(getSkin());

                break;
            case WIKI:
                table =gameLoader.superPool.obtain_table(this.getSkin());
                table.setBackground("white");
                table.add(gameLoader.superPool.obtain_image("Wiki")).padLeft(10);
                label = new Label("DxM_IAGame Wiki", getSkin(),"title");
                label.setFontScale(0.3f);
                table.add(label).padLeft(10.0f).padRight(0.0f).growX().fillX().colspan(2);
                break;
            case MAIL:
                table =gameLoader.superPool.obtain_table(this.getSkin());
                table.setBackground(app.game.gameLoader.getPicture("MailBCKGRND"));
                table.add(gameLoader.superPool.obtain_image("MailHead")).align(Align.left).fillX();

                break;
            case NEWS:
                table =gameLoader.superPool.obtain_table(this.getSkin());
                table.setBackground(app.game.gameLoader.getPicture("NewsBCKGRND"));
                table.add(gameLoader.superPool.obtain_image("NewsHead")).align(Align.left).fillX();
                break;
            default:
                table=new Table(getSkin());
                break;
        }
        return table;

    }
    private void reLoadIndex(){
        if(this.indexBox!=null){
            this.indexBox.getActor().clear();
            Cell cell = this.getCell(indexBox);
//            this.indexBox.clear();
//            for (Table body : webBodies) {
//                body.clear();
//            }
            eventButtons.clear();
            webBodies.clear();
            this.indexBox.setActor(createIndex(this.webPageURL));
            indexBox.layout();
            cell.setActor(indexBox).growY();
        }

    }

    @Override
    public void reset() {
        super.reset();
        webPageURL=null;
        app=null;
        for(Table body: webBodies){
            webBodies.removeValue(body,true);
            body.clearChildren();
            gameLoader.superPool.free(body);
        }
        for(Button bttn: eventButtons){
            eventButtons.removeValue(bttn,true);
            bttn.clearChildren();
            gameLoader.superPool.free(bttn);
        }
        gameLoader.superPool.free(currentBody);
        indexBox.clear();
        bodySP.clear();
        currentEvent=null;
        for( GameEvent gameEvent: assignedEvents){
            assignedEvents.removeValue(gameEvent,true);
            gameEvent.dispose();
        }
        updateIndex=false;
        updatePage=false;
        gameLoader.superPool.free((Actor) blue_button_bckgrnd);
    }
}
