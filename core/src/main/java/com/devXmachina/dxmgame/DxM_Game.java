package com.devXmachina.dxmgame;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.devXmachina.dxmgame.gamelogic.PlayerData;
import com.devXmachina.dxmgame.screens.*;
/**XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX__DEV_X_MACHINA__XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
 *            LibGDX_Framework
 *            @see com.badlogic.gdx
 *
 *            @author VladScv,
 *            @version 0.3
 *XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX__DxM_GAME__XXXXXXXXXXXXXXXXXXXXXXXXXXX
 *          PACKAGE: com.dxm.dxmgame
 *          CLASS: DxM_Game
 *          DESCRIPTION: MainGame Class from ApplicationAdapter
 *XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
 */
/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

public class DxM_Game extends ApplicationAdapter {
	//----------------------------------- Game CONSTANTS
	public static final int MAXWIN_WIDTH = 640;
	public static final int MAXWIN_HEIGHT = 460;
	public static final int RESTWIN_WIDTH = 320;
	public static final int RESTWIN_HEIGHT = 230;

	//----------------------------------- ENUM DEFINITIONS
	public enum EventState {NULL, PENDING, ACCEPTED, DISCARDED, SUCCESS}
	public enum EventDecade {
		SEVENTIES, EIGHTIES, NINETIES, NOUGHTIES, TWENTY_TENS, TWENTY_TWENTIES;

		@Override
		public String toString() {
			switch (this) {

				case SEVENTIES:
					return "1970";
				case EIGHTIES:
					return "1980";
				case NINETIES:
					return "1990";
				case NOUGHTIES:
					return "2000";
				case TWENTY_TENS:
					return "2010";
				case TWENTY_TWENTIES:
					return "2020";
				default:
					return "FATAL_ERROR";
			}
		}
	}
	public enum GameState {DESKTOP_MODE, MENU_MODE, NEW_GAME, VIRUS, AUTO_PLAY}
	public enum DesktopAppType {MAIL, BROWSER, MANAGER,NOTEPAD,CONSOLE}
	public enum BrowserURL {
		FACEBOOK, GITHUB, BLANK, WIKI, MAIL, NEWS, MANAGER, CONSOLE;


		@Override
		public String toString() {
			switch (this) {
				case GITHUB:
					return "www.github.com";
				case WIKI:
					return "wwww.DxM.uoc.edu";
				case FACEBOOK:
					return "www.facemash.com";
				case BLANK:
					return "about:blank";
				case MAIL:
					return "www.msn.hotmail.com";
				case NEWS:
					return "www.dxm-news.feed.rss";
				default:
					return "err_";
			}
		}
	}
	//----------------------------------------------------------------------------------------------ATTRIBUTES
	private GameState gameState;
	public float renderCounter;
	// ------------STAGES-------------
	public FitViewport mainViewport;
	private GameVirus virus;
	private GameDesktop desktop;
	private AutoPlayScreen autoPlayScreen;
	private GameMenu menu;
	public GameLoader gameLoader;
	private NewGameMenu newGameMenu;
	private Stage currentStage;
	//-------------PLAYER DATA--------------
	public PlayerData data;
	//----------------------------------------------------------------------------------------------START METHODS
	@Override
	public void create() {
		gameLoader = new GameLoader(this);
		data = new PlayerData();
		gameStart();
	}
	public void gameStart() {
		mainViewport = new FitViewport(640, 480);
		menu = new GameMenu(mainViewport, this);
		setGameState(GameState.MENU_MODE);
		if (menu.isNewGame()) {
			newGameMenu = new NewGameMenu(mainViewport, this);
			menu.start();
		} else {
			start_desktop();
			menu.start();
		}
	}
	public void start_desktop() {
		desktop = new GameDesktop(mainViewport, this);
		setGameState(GameState.MENU_MODE);
		desktop.start();
		gameLoader.start();
	}
	//----------------------------------------------------------------------------------------------UPDATE METHODS
	@Override
public void render() {
		Gdx.gl.glClearColor(1f, 0.83529411764705882352941176470588f, 0.3098039215686274509803921568629f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		try {
			currentStage = getCurrentStage();
		} catch (Exception e) {
			setGameState(GameState.MENU_MODE);
			currentStage=menu;
		}
		currentStage.act(Gdx.graphics.getDeltaTime());
		currentStage.draw();
	}

	public void reloadMenu(boolean newGame) {
		this.menu=new GameMenu(mainViewport,this);
		this.menu.setNewGame(false);
		this.menu.start();
	}
	//----------------------------------------------------------------------------------------------UTILS METHODS
	//Returns the current active screen depending on the GameState
	public Stage getCurrentStage() {
		switch (this.gameState) {
			case NEW_GAME:
				return newGameMenu;
			case MENU_MODE:
				return menu;
			case DESKTOP_MODE:
				return desktop;
			case VIRUS:
				return virus;
			case AUTO_PLAY:
				return autoPlayScreen;
			default:
				throw new IllegalStateException("Unexpected value: " + this.gameState);
		}
	}
	//Public Method to change GameState
	public void setGameState(GameState state) {
		this.gameState = state;
		Gdx.input.setInputProcessor(getCurrentStage());
	}
	public GameDesktop getDesktop() {
		return this.desktop;
	}
	public GameMenu getMenu() {
		return this.menu;
	}
	public NewGameMenu getNewGame_stage() {
		return this.newGameMenu;
	}

	//----------------------------------------------------------------------------------------------SELF-METHODS
	@Override
	public void resize(int width, int height) {
		mainViewport.update(width, height);
		getCurrentStage().getViewport().update(width, height);
	}
	@Override
	public void dispose() {
		gameState=null;
		renderCounter=0;
		mainViewport=null;
		gameLoader.dispose();
//		newGameMenu.dispose();
		data.dispose();
		gameLoader=null;
		desktop.dispose();
		menu.dispose();
	}
	public void reset() {
		this.dispose();
		this.create();

	}
	public void updateManager(boolean full){
		desktop.getTaskManager().updateManager(full);

	}

	public void executeAutoplay() {
		this.autoPlayScreen = new AutoPlayScreen(mainViewport,this,gameLoader.getCurrentDecade());
		setGameState(GameState.AUTO_PLAY);
	}

	public void executeVirus(){
		this.virus=new GameVirus(mainViewport,this);
		setGameState(GameState.VIRUS);
	}
}