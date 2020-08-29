package launcher;

import gameObjects.GameObject;
import gameObjects.SimplePlayer;
import gameObjects.SolidObject;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import level.Level;

public class GameManager {
	boolean rightKey, leftKey, upKey, downKey;
	boolean eKey, qKey, spaceKey;
	boolean jumpTrigger;
	
	
	public Level currentLevel;
	public Point2D offset;
	/**The position at which the player should be located on the screen of the game.*/
	public Point2D PLAYER_POSITION = new Point2D(Launcher.SCREEN_X * 0.5,Launcher.SCREEN_Y * 0.7);
	
	public KeyPressObserver keyPressManager;
	public KeyLiftObserver keyLiftManager;
	public GameManager() {
		currentLevel = new Level();
		keyPressManager = new KeyPressObserver();
		keyLiftManager = new KeyLiftObserver();
	}
	public void update(GraphicsContext gameGraphics) {
		
		double thrustX = 0.0;
		double thrustY = 0.0;
		if(rightKey) {
			thrustX = SimplePlayer.THRUST;
		}
		else if(leftKey) {
			thrustX = -SimplePlayer.THRUST;
		}
		
		
		if(jumpTrigger && currentLevel.player.onGround) {
			thrustY = SimplePlayer.JUMP_THRUST;
			currentLevel.player.doubleJump = true;
		}
		else if(jumpTrigger && currentLevel.player.doubleJump) {
			thrustY = -currentLevel.player.velocity.getY() + SimplePlayer.JUMP_THRUST;
			currentLevel.player.doubleJump = false;
		}
		//Reset the jump trigger that is set by releasing the space key. 
		jumpTrigger = false; 
		
		if(eKey) {
			currentLevel.player.angularAcceleration = 0.5;
		}
		else if(qKey) {
			currentLevel.player.angularAcceleration = -0.5;
		}
		else {
			currentLevel.player.angularAcceleration = 0;
		}
		
		
		currentLevel.player.acceleration = new Point2D(thrustX,thrustY);
		currentLevel.player.update(currentLevel.wallList,Level.GRAVITY);
		gameGraphics.setFill(Color.WHITE);
		gameGraphics.fillRect(0,0,Launcher.SCREEN_X,Launcher.SCREEN_Y);
		
		draw(gameGraphics);
	}
	public void draw(GraphicsContext gc) {
		Point2D offset = currentLevel.player.location.subtract(PLAYER_POSITION);
		currentLevel.drawGame(gc,offset);
	}
	
	
	
	/**
	 * This class implements the method that handles when keys are pressed.
	 * @author jordan
	 */
	public class KeyPressObserver implements EventHandler<KeyEvent>{
		
		public void handle(KeyEvent e) {
			if(e.getCode() == KeyCode.RIGHT) {
				rightKey = true;
			}
			else if(e.getCode() == KeyCode.LEFT) {
				leftKey = true;
			}
			else if(e.getCode() == KeyCode.UP) {
				upKey = true;
			}
			else if(e.getCode() == KeyCode.DOWN) {
				downKey = true; 
			}
			else if(e.getCode() == KeyCode.E) {
				eKey = true;
			}
			else if(e.getCode() == KeyCode.Q) {
				qKey = true;
			}
			else if(e.getCode() == KeyCode.SPACE) {
				spaceKey = true;
			}
		}
	}
	
	/**
	 * This class implements the method that handles when keys are released. 
	 * @author jordan
	 */
	public class KeyLiftObserver implements EventHandler<KeyEvent>{
		public void handle(KeyEvent e) {
			if(e.getCode() == KeyCode.RIGHT) {
				rightKey = false;
			}
			else if(e.getCode() == KeyCode.LEFT) {
				leftKey = false;
			}
			else if(e.getCode() == KeyCode.UP) {
				upKey = false;
			}
			else if(e.getCode() == KeyCode.DOWN) {
				downKey = false; 
			}
			else if(e.getCode() == KeyCode.E) {
				eKey = false;
			}
			else if(e.getCode() == KeyCode.Q) {
				qKey = false; 
			}
			else if(e.getCode() == KeyCode.SPACE) {
				spaceKey = false;
				jumpTrigger = true;
			}
			else if(e.getCode() == KeyCode.S) {
				SolidObject.drawHitshape = !SolidObject.drawHitshape;
				GameObject.drawLocation = !GameObject.drawLocation;
			}
		}
	}
	/**
	 * Draws the given point on the graphics context, with a radius of 5, and a color of purple.
	 * @param gc
	 * @param x
	 * @param y
	 */
	public static void drawPoint(GraphicsContext gc,double x, double y) {
		drawPoint(gc,x,y,5,Color.PURPLE);
	}
	/**
	 * Draws the given point on the graphics context, with the specified color and radius. 
	 * @param gc
	 * @param x
	 * @param y
	 * @param radius
	 * @param fill
	 */
	public static void drawPoint(GraphicsContext gc,double x, double y, double radius, Color fill) {
		gc.setStroke(fill);
		gc.setLineWidth(3);
		gc.strokeOval(x-radius,y-radius,2*radius,2*radius);
		
	}
}
