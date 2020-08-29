package gameObjects;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class MovingObject extends GameObject{
	/**The translational velocity of the moving object during the current frame.
	 * It is measured in pixels per frame.
	 */
	public Point2D velocity;
	
	/**The translational acceleration of the moving object.
	 * It is measured in pixels per frame per frame. 
	 */
	public Point2D acceleration;
	
	/**The angular velocity of the moving object.
	 * It is measured in degrees per frame.
	 */
	public double angularVelocity;
	/**The angular acceleration of the moving object.
	 * It is measured in degrees per frame per frame.
	 */
	public double angularAcceleration;
	
	/**
	 * Initializes the Moving Object with its center at (x,y), its angle set to 0.0.
	 * @param x - the horizontal position the moving object should start from.
	 * @param y - the vertical position the moving object should start from. 
	 */
	public MovingObject(double x, double y) {
		this(x,y,0.0);
	}
	/**
	 * Initializes the Moving Object with its center at (x,y), its angle set to angle. 
	 * @param x - the horizontal position the moving object should start from.
	 * @param y - the vertical position the moving object should start from. 
	 * @param angle - the initial angle of the Moving Object. 
	 */
	public MovingObject(double x,double y,double angle) {
		super(x,y,angle);
		init();
	}
	/**
	 * Initializes the linear and angular motion parameters of the moving object (velocity and acceleration). 
	 */
	private void init() {
		this.velocity = new Point2D(0,0);
		this.acceleration = new Point2D(0,0);
		angularVelocity = 0.0;
		angularAcceleration = 0.0;
	}
	/**Updates the MovingObject based on its translational and angular acceleration/velocity.
	 * @param maxSpeed - the maximum translational speed of the moving object. 
	 * @param maxAngularSpeed - the maximum angular speed of the moving object. 
	 * @return whether the angle or location of the moving object changed.  (Equivalent to whether the affine of the object was changed).
	 */
	public boolean move(double maxSpeed,double maxAngularSpeed) {
		/*The square of the maximum velocity.*/
		double maxVelocitySq = maxSpeed * maxSpeed;
		
		//Ratio used to trim down the velocity....
		double ratio;
		/*
		 *Section1: Translational velocity and acceleration applied.
		 *Limits the displacement and velocity at the end of the frame if they exceed the maximum translational speed.
		 */
		
		//Calculate the translational displacement during the current frame.
		Point2D displacement = this.velocity.add(this.acceleration.multiply(0.5));
		Point2D endFrameVelocity = this.velocity.add(this.acceleration);
	
		
		//If the displacement during the current frame exceeds the maximum speed, limit it to the maximum speed.
		if(displacement.dotProduct(displacement) > maxVelocitySq) {
			ratio = maxSpeed / displacement.magnitude();
			displacement = displacement.multiply(ratio);
		}
		
		//If the velocity at the end of the frame exceeds the maximum speed, limit it to the maximum speed.
		if(endFrameVelocity.dotProduct(endFrameVelocity) > maxVelocitySq) {
			ratio = maxSpeed / endFrameVelocity.magnitude();
			endFrameVelocity = endFrameVelocity.multiply(ratio);
		}
		
		//Add the displacement during the frame to our location, and update the velocity to the velocity at the end of the frame. 
		this.location = this.location.add(displacement);
		this.velocity = endFrameVelocity;
		
		/*
		 * Section2: Angular velocity and acceleration applied.
		 * Limits the angular displacement and velocity at the end of the frame if they exceed the maximum angular speed.
		 */
		double angularDisplacement = this.angularVelocity + 0.5 * this.angularAcceleration;
		double endFrameAngularVelocity = this.angularVelocity + this.angularAcceleration;
		
		/*Absolute values of the angular velocity at the end of the frame, and the angular displacement.*/
		double absoluteDisplacement = Math.abs(angularDisplacement);
		double endFrameAngularSpeed = Math.abs(endFrameAngularVelocity);
		
		//If the angular displacement exceeds our maximum speed, we need to reduce it to the maximum angular speed.
		if(absoluteDisplacement > maxAngularSpeed) {
			ratio = maxAngularSpeed / absoluteDisplacement;
			angularDisplacement *= ratio;
		}
		
		//If the angular velocity at the end of the frame is too big, we need to reduce it to the maximum angular speed.
		if(endFrameAngularSpeed > maxAngularSpeed) {
			ratio = maxAngularSpeed / endFrameAngularSpeed;
			endFrameAngularVelocity *= ratio;
		}
		
		//Add the displacement during the frame to our angle, and update the angular velocity to the angular velocity at the end of the frame.
		this.angle += angularDisplacement;
		this.angularVelocity = endFrameAngularVelocity; 
		
	
		//We need to update the affine of the moving object iff the displacement during the frame was non zero, or the angular velocity was non zero. 
		boolean affineChange = (angularDisplacement != 0.0) || (displacement.dotProduct(displacement) != 0.0);
		//Only update the affine if we need to. 
		if(affineChange)this.updateAffine();
		
		//Return whether the affine was changed. 
		return affineChange;
	}
}
