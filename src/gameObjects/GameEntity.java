package gameObjects;

import java.util.ArrayList;

import hitshape.Hitshape;
import hitshape.Hitshape.CollisionData;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import launcher.GameManager;
import launcher.Launcher;

public abstract class GameEntity extends SolidObject{
	public boolean onGround = false; 
	public boolean doubleJump = false;
	public ArrayList<CollisionData> collisions;
	public GameEntity(double x, double y, double angle, Hitshape shape) {
		super(x, y, angle, shape);
	}
	/**
	 * Sets the position and shape of this game entity.
	 * @param x - the initial x location of the game entity.
	 * @param y - the initial y location of the game entity.
	 * @param shape - the shape of the game entity.
	 */
	public GameEntity(double x, double y, Hitshape shape) {
		this(x,y,0.0,shape);
	}
	
	/**Checks for collisions between the game entity and walls and corrects any collisions that have occurred.*/
	private void updateWalls(Wall [] wallList) {
		onGround = false;
		collisions = new ArrayList<CollisionData>();
		for(int i = 0;i < wallList.length;++i) {
			CollisionData data = this.checkCollision(wallList[i]);
			if(data != null) {
				double YtoXRatio = data.colAxis.getY() / Math.abs(data.colAxis.getX());
				//If any of the collisions are aimed upwards, we can consider the player to be on the ground.
				if(YtoXRatio < -0.8) {
					onGround = true;
				}
				
				collisions.add(data);
			}
		}
	}
	public void draw(GraphicsContext gc,Point2D offset) {
		super.draw(gc,offset);
		if(SolidObject.drawHitshape) {
			Affine translation = new Affine();
			translation.appendTranslation(-offset.getX(),-offset.getY());
			gc.setTransform(translation);
			for(CollisionData data : collisions) {
				for(Point2D v : data.hitpoints) {
					GameManager.drawPoint(gc,v.getX(),v.getY(),10,Color.AQUAMARINE);
				}
			}
			gc.setTransform(new Affine());
		}
	}
	public abstract void update(Wall [] wallList,double gravity);
	protected void update(Wall [] wallList) {
		this.update(wallList,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,0.0);
	}
	protected void update(Wall [] wallList, double maximumSpeed,double maximumAngularSpeed,double gravity) {
		//Adds the gravity acceleration to the acceleration.
		this.acceleration = this.acceleration.add(0,gravity);
		boolean affineUpdated = super.move(maximumSpeed,maximumAngularSpeed);
		if(affineUpdated) {
			shape.updatePoints(objectAffine);
		}
		updateWalls(wallList);
	}
	protected void update(Wall [] wallList, double maximumSpeed,double maximumAngularSpeed) {
		this.update(wallList,maximumSpeed,maximumAngularSpeed,0.0);
	}
}
