{XX_DxM_IAGame_ReadMe_XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
{ 												
{	File: ReadMe.txt													
{	Author: Dídac Llorens Bravo		
{	Subject: Treball en equip a la xarxa
{	Course: 20211							
{	Description: ReadMe for the first implementation 
{					of the IAGame game structure, based on libGDX framework.
{	Contact: ddcllrnsbrv@uoc.edu						
{ 														
{ XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

LibGDX structure: 
		
		core/assets --> Aqui guardem tots els arxius que volguem utilitzar (imatges, textos, skins, etc... ) i ES LA CARPETA ARREL 
							(p.ex la path a la carpeta core/assets/skin/os8ui es: 'Gdx.files.internal("skin/os8ui/OS Eight.json")' )
		
		game.pause() --> es crida quan volem que es pausi el joc (p.ex: al pitjar el botó de pause
		
		Game --create()--> S'executa NOMES quan iniciem l'aplicació, aqui fem el load, i iniciem totes les classes, si hi ha savedData la carreguem, sino newGame
		
		Game --render()--> Screen.render() --> 
							En el nostre joc delega en 2 metodes:
				
								GameController.updateGame() 
									te 3 "blocs" :
									1_prepare: tot alló que hem d'actualitzar abans de saber quina pantalla hem d'actualitzar.
										p.ex: si has pitjat el botó de pause, haurem de canviar a menuScreen abans d'actualitzarla
									
									2_Screen.act(): Selecciona i asigna la Screen segons el game.gameState, i executa el seu metode draw()
													que alhora crida als metodes draw() de cada Actor que té la Screen
									
									3_PostScreen: Aqui posariem tot alló que necessitem que s'actualitzi després de tots els actors
										p.ex: si necessitem que un determinat so es reprodueixi just quan choquem contra un objecte, primer 
												hauriem d'actualitzar el player, i els objectes, i si han xocat reprodueix el so.
								
								RenderManager.startRender()
									te 3 "blocs" cada un ha de tenir el seu batch:
									1_Background: Tot el que volguem que es dibuixi darrera la screen (el background pot ser també uun actor o simplement
													imprimir una imatge o animació aqui)
									2_Screen.draw(): Dibuixa tots els Actor de la Screen actual per ordre de Z(profunditat) 
														cridant al metode draw() de cada Actor. te el seu propi Batch
									
									3_Top: Tot el que volguem sobreimprimir a la screen (p.ex: debug shapes)
									
#MOST VALUED METHODS

		gameObject.act() /  gameObject.draw() --> defineix que fan i com es dibuixen cada un dels game objects
		game.pause() -> pausa el joc
		
#TODO
	Com a principals metodes a implementar tenim:
	
	
	Actor.act() 
	 per cada 'Actor' (GameObject) s'ha d'implementar el seu metode act() que es a on l'actualitzarem a cada iteració
									(actualitzem, posició, estat, si esta actiu o no, l'animació, etc..)

	Actor.draw()
	 per cada 'Actor' (GameObject) s'ha d'implementar el seu metode draw() que es el que dibuixarem al render		
									(agafa el Screen.getBatch() per dibuixar, per exempel el Sprite, o una imatge o una forma, o el text, etc..)
									
	Scene2D.UI implementation	
			Tot aixó de Actor(el nostre GameObject) i Stage( les nostres Screen son també Stage) es de la llibreria Scene2D que
			té molts metodes per gestionar objectes (com per exemple comprobar colisions) i elements d'una UI (textbox, button, etc...)
			
			TODO!! Per poder crear elements de la UI necessitem tenir una SKIN 
				per aixó fa falta descarregar el programa libGDX TexturePacker empaquetar les imatges a utilitzar i crear el json
				
				
			Tutorials interessants:
			
				https://github.com/raeleus/skin-composer/wiki/From-the-Ground-Up-00:-Scene2D-Primer
				
	MenuScreen -> per començar implementar el mainGameMenu() que creara els botons i elements del menu principal (play, sound, fullscreen, nom del jugador si el sap, etc..)
	
					despres el newGameScreen() per al menú del primer cop que jugues (on et demana el nom i el guarda i en un futur farem el test)
								pauseGameMenu() que serà com el main, pero amb la opció de resume per tornar al joc.
								
								
								
								
								
								
								
Thanks to :

Commodore64 libGDX Skin : 	 https://ray3k.wordpress.com/artwork/commodore-64-ui-skin-for-libgdx/ by ray3k

os8UI libGDX Skin: 	https://github.com/raeleus/OS-Eight-UI-Skin-Example/releases  by raeleus

free-icons-pack Col·lecció d'icones en format PNG:	 https://pablogamedev.itch.io/free-icons by PabloGameDev


libGDX is licensed under Apache 2.0 meaning you can use it free of charge, without strings attached in commercial and non-commercial projects. 

WEKA is licensed under the GNU General Public license (GPL 2.0 for Weka 3.6) and (GPL 3.0 for Weka > 3.7.5). Any derivative work obtained under this license must be licensed under the GPL if this derivative work is distributed to a third party.

DxM_Game està publicat sota una llicència Creative Commons Reconocimiento-NoComercial-CompartirIgual 4.0 Internacional

								
