package gameObjects;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import launcher.Launcher;

public abstract class GameObject {
	//The location of the game object. 
	public Point2D location;
	
	//The angle of the game object, in degrees.
	public double angle;
	public Affine objectAffine;
	public static boolean drawLocation = false;
	public GameObject(double x, double y) {
		this(x,y,0.0);
	}
	public GameObject(double x, double y, double angle) {
		this.location = new Point2D(x,y);
		this.angle = angle; 
		updateAffine();
	}
	/**Updates the affine of the gameobject. **/
	public void updateAffine() {
		objectAffine = new Affine();
		objectAffine.appendTranslation(this.location.getX(),this.location.getY());
		objectAffine.appendRotation(this.angle);
	}
	/**Defines how the game object should be drawn with its center at 0,0.**/
	public abstract void drawObject(GraphicsContext gc);
	public void draw(GraphicsContext gc) {
		this.draw(gc,Point2D.ZERO);
	}
	public void draw(GraphicsContext gc, Point2D offset) {
		//Set the affine of the graphics to the draw/update affine of the object.
		Affine drawAffine = new Affine();
		
		//Translate back by the screen offset, and apply the object affine.
		drawAffine.appendTranslation(-offset.getX(),-offset.getY());
		drawAffine.append(objectAffine);
		
		gc.setTransform(drawAffine);
		//gc.setTransform(objectAffine);
		drawObject(gc);
		//Set the affine back to normal. 
		gc.setTransform(Launcher.ZERO);
		if(drawLocation) {
			gc.setFill(Color.BLACK);
			gc.fillOval(location.getX() - offset.getX() - 4,location.getY() - offset.getY() - 4,8,8);
			gc.fillText(String.format("(%.1f,%.1f)",location.getX(),location.getY()),location.getX()-offset.getX(),location.getY()-offset.getY());
		}
	}
}
