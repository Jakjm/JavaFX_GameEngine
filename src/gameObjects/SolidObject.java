package gameObjects;

import hitshape.Hitshape;
import hitshape.Hitshape.CollisionData;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public abstract class SolidObject extends MovingObject {
	public Hitshape shape; 
	public static boolean drawHitshape = false;
	public double radius;
	public static final double FRICTION_COEFFICIENT = 0.15;
	public SolidObject(double x,double y,Hitshape shape) {
		this(x,y,0.0,shape);
	}
	public SolidObject(double x,double y,double angle,Hitshape shape){
		super(x,y,angle);
		this.shape = shape; 
		getRadius();
	}
	private void getRadius() {
		double sum = 0.0;
		double average;
		for(int i = 0;i < shape.vertices.length;++i) {
			Point2D relativePoint = shape.POINTS[i];
			sum += relativePoint.dotProduct(relativePoint);
		}
		average = Math.sqrt(sum / shape.vertices.length);
		this.radius = average;
	}
	/**
	 * Checks if the solid object has collided with another solid object, and if it has,
	 * corrects the collision, and applies the normal force of the collision.
	 * @param other - the object that this solid object may have collided with.
	 * @param bounce - whether the object should bounce off the other object (whether *twice* the normal force of collision should be applied).
	 * @return the CollisionData, if a collision occured. 
	 */
	public CollisionData checkCollision(SolidObject other,boolean bounce) {
		CollisionData colData = this.shape.checkCollision(other.shape);
		if(colData != null) {
			//Correct the location so that a collision is no longer occuring. 
			this.location = this.location.add(colData.colAxis.multiply(colData.overlapAmount));
			
			//Update the affine of the object, and of the hitshape.
			this.updateAffine();
			this.shape.updatePoints(objectAffine);
			
			//Applying the normal force of collision to the solid object.
			double projection = this.velocity.dotProduct(colData.colAxis);
			projection /= colData.colAxis.dotProduct(colData.colAxis);
			if(bounce)projection *= 2.0;
			Point2D normalForce = colData.colAxis.multiply(projection);
			this.velocity = this.velocity.subtract(normalForce);
			this.acceleration = this.acceleration.subtract(normalForce);
			
			
			/*
			 *Calculating and applying the frictional force on the surface. 
			 */
			if(normalForce.dotProduct(normalForce) > 0) {
				double frictionalForce = normalForce.magnitude() * FRICTION_COEFFICIENT;
				Point2D perp = new Point2D(-normalForce.getY(),normalForce.getX());
				projection = velocity.dotProduct(perp);
				projection = projection / perp.dotProduct(perp);
				
				//If the object is moving too slowly along the surface, we need to slow it to a halt.
				if(Math.abs(projection) < frictionalForce) {
					frictionalForce = Math.abs(projection);
				}
				Point2D friction = perp.multiply(frictionalForce * Math.signum(projection));
				this.velocity = this.velocity.subtract(friction);
			}
		}
		return colData;
	}
	/**
	 * Checks if the solid object has collided with another solid object, and if it has,
	 * corrects the collision, and applies the normal force of the collision.
	 * @param other - the object that this solid object may have collided with.
	 * @return the CollisionData, if a collision occured. 
	 */
	public CollisionData checkCollision(SolidObject other) {
		return checkCollision(other,false);
	}
	
	public void draw(GraphicsContext gc) {
		this.draw(gc,Point2D.ZERO);
	}
	public void draw(GraphicsContext gc, Point2D offset) {
		super.draw(gc,offset);
		if(drawHitshape)shape.drawHitshape(gc,offset);
	}
}
