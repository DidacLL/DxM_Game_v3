package com.devXmachina.dxmgame.gamelogic;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Pool;
import com.devXmachina.dxmgame.DxM_Game;
import com.devXmachina.dxmgame.GameLoader;
import com.devXmachina.dxmgame.desktopActors.AppPage;
import com.devXmachina.dxmgame.desktopActors.DesktopGameApp;
import com.devXmachina.dxmgame.desktopActors.PopNotification;
import com.devXmachina.dxmgame.screens.GameDesktop;


public class SuperPool {
    private Pool<Table> tablePool;
    private Pool<ImageButton> imageButtonPool;
    private Pool<TextButton> textButtonPool;
    private Pool<Button> buttonPool;
    private Pool<Label> labelPool;
    private Pool<AppPage> desktopWebPagePool;
    private Pool<Image> imagePool;
    private Pool<Window> windowPool;
    private Pool<PopNotification> popNotificationPool;
    private Pool<DesktopGameApp> desktopGameAppPool;
    private Skin desktopSkin, menuSkin;

    GameLoader gameLoader;
    private DxM_Game game;

    public SuperPool(GameLoader gameLoader, DxM_Game game) {
        this.gameLoader = gameLoader;
        this.game=game;
        desktopSkin = gameLoader.getSkin("desktop");
        menuSkin = gameLoader.getSkin("browser");
        createPools();
    }

    private void createPools(){
        tablePool = new Pool<Table>() {
            @Override
            protected Table newObject() {
                return new Table(desktopSkin);
            }
        };

        imagePool = new Pool<Image>() {
            @Override
            protected Image newObject() {
                return new Image();
            }
        };
        textButtonPool= new Pool<TextButton>() {
            @Override
            protected TextButton newObject() {
                return new TextButton("",desktopSkin);
            }
        };
        labelPool= new Pool<Label>() {
            @Override
            protected Label newObject() {
                return new Label("",desktopSkin);
            }
        };
        desktopWebPagePool = new Pool<AppPage>() {
            @Override
            protected AppPage newObject() {
                return new AppPage(desktopSkin, DxM_Game.BrowserURL.BLANK,null,gameLoader);//todo simple constructor
            }
        };
        popNotificationPool=new Pool<PopNotification>() {
            @Override
            protected PopNotification newObject() {
                return new PopNotification(game, game.getDesktop(), null,"","",desktopSkin);
            }
        };
        windowPool = new Pool<Window>() {
            @Override
            protected Window newObject() {
                return new Window("",desktopSkin);
            }
        };
        buttonPool = new Pool<Button>() {
            @Override
            protected Button newObject() {
                return new Button();
            }
        };
        imageButtonPool = new Pool<ImageButton>() {
            @Override
            protected ImageButton newObject() {
                ImageButton img= new ImageButton(desktopSkin);
                img.setSkin(null);
                return img;

            }
        };
        desktopGameAppPool=new Pool<DesktopGameApp>() {
            @Override
            protected DesktopGameApp newObject() {
                return new DesktopGameApp(game, game.getDesktop(), null);
            }
        };
    }

    //------------------------------------------------------------------------------------FREE_()

    public void free(Table obj){
        tablePool.free(obj);
    }
    public void free(Image obj){
        imagePool.free(obj);
    }
    public void free(ImageButton obj){
        imageButtonPool.free(obj);
    }
    public void free(TextButton obj){
        textButtonPool.free(obj);
    }
    public void free(Button obj){
        buttonPool.free(obj);
    }
    public void free(Label obj){
        labelPool.free(obj);
    }
    public void free(AppPage appPage){
        desktopWebPagePool.free(appPage);//todo truly destroy it
    }
    public void free(Window obj){
        windowPool.free(obj);
    }
    public void free(PopNotification obj){
        popNotificationPool.free(obj);
    }
    public void free(DesktopGameApp desktopGameApp) { desktopGameAppPool.free(desktopGameApp);   }
    public void free(Actor actor){
        actor.getStage().getActors().removeValue(actor,true);
    }


    //------------------------------------------------------------------------------------OBTAIN_()
    public Table obtain_table(Skin skin) {
        Table table= tablePool.obtain();
        table.setSkin(skin);
        return table;
    }
    public ImageButton obtain_imageButton( String imageName,int images) {
        ImageButton elem = imageButtonPool.obtain();
        elem.clear();
        elem.setSkin(null);
        if (images>4){images=4;}
        switch (images) {
            case (4):
                elem.getStyle().imageChecked = gameLoader.getPicture(imageName + "d");
            case 3:
                elem.getStyle().imageOver = gameLoader.getPicture(imageName + "c");
            case 2:
                elem.getStyle().imageDown = gameLoader.getPicture(imageName + "b");
            case 1:
            default:
                elem.getStyle().imageUp = gameLoader.getPicture(imageName);
        }
        return elem;
    }
    public ImageButton obtain_imageButton(String text, Skin skin) {
        ImageButton elem = imageButtonPool.obtain();
        elem.setSkin(skin);
        elem.add(text);
        return elem;
    }
    public TextButton obtain_textButton(String text, Skin skin) {
        TextButton elem = textButtonPool.obtain();
        elem.setSkin(skin);
        elem.setText(text);
        return elem;
    }
    public Button obtain_button(Skin skin, String style) {
        Button button = buttonPool.obtain();
        button.setSkin(skin);
        button.setStyle(skin.get(style, Button.ButtonStyle.class));
        return button;
    }
    public Button obtain_button(Skin skin) {
        Button button = buttonPool.obtain();
        button.setSkin(skin);
        button.setStyle(skin.get(Button.ButtonStyle.class));
        return button;
    }
    public Label obtain_label(String text, Skin skin) {
        Label elem = labelPool.obtain();
        elem.setStyle(skin.get(Label.LabelStyle.class));
        elem.setText(text);
        return elem;
    }
    public AppPage obtain_appPage(DxM_Game.BrowserURL url, DesktopGameApp app) {
        AppPage elem = desktopWebPagePool.obtain();
        elem.clear();
        elem.construct_AppPage(url,app,gameLoader);
        return elem;
    }
    public Image obtain_image(String name) {
        Image img = imagePool.obtain();
        img.setDrawable(gameLoader.getPicture(name));
        return img;
    }
    public Image obtain_image(String name, Skin skin){
        Image img = imagePool.obtain();
        img.setDrawable(skin,name);
        return img;

    }
    public Window obtain_window(String title,Skin skin ) {
        Window elem = windowPool.obtain();
        elem.clear();
        elem.setSkin(skin);
        elem.getTitleLabel().setText(title);
        return elem;
    }
    public PopNotification obtain_popNotification(DxM_Game game, GameDesktop desktop, GameEvent gameEvent, String title, String text) {
        PopNotification elem = popNotificationPool.obtain();
        elem.clear();
        elem.construct_notification(game,desktop,gameEvent,title,text);
        return elem;
    }

    public DesktopGameApp obtain_DesktopApp(DxM_Game game, GameDesktop desktop, DxM_Game.DesktopAppType type) {
        DesktopGameApp app= desktopGameAppPool.obtain();
        app.construct_gameApp(game,desktop,type);
        return app;
    }

}
