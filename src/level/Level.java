package level;

import gameObjects.GameEntity;
import gameObjects.SimplePlayer;
import gameObjects.Wall;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Level {
	public Wall [] wallList;
	public GameEntity player;
	/**The value of gravity in the game, measured in pixels per frame.*/
	public static final double GRAVITY = 0.65;
	public Level() {
		wallList = new Wall[8];
		
		wallList[0] = new Wall(640,20,1280,40);  //Ceiling.
		wallList[1] = new Wall(1280,700,2560,40); //Floor.
		wallList[2] = new Wall(20,360,40,640); //Left wall.
		wallList[3] = new Wall(2540,360,40,640); //Right wall.
		
		//Ramp.
		wallList[4] = new Wall(1000,620,500,10,15,Color.GREEN);
		wallList[5] = new Wall(689,555,200,10,0.0,Color.GREEN);
		wallList[6] = new Wall(590,615,10,130,0.0,Color.GREEN);
		
		wallList[7] = new Wall(1800,620,600,5,-15);
		
		player = new SimplePlayer(600,400);
	}
	public void drawGame(GraphicsContext gc) {
		drawGame(gc, Point2D.ZERO);
	}
	public void drawGame(GraphicsContext gc,Point2D offset) {
		for(int i = 0;i < wallList.length;++i) {
			wallList[i].draw(gc,offset);
		}
		player.draw(gc,offset);
	}
}
