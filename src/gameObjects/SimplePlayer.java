package gameObjects;

import hitshape.Box;
import hitshape.Circle;
import hitshape.Hitshape;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SimplePlayer extends GameEntity{
	
	/**Constants for maximum speed, acceleration...*/
	public static final double MAX_SPEED = 30, MAX_ANGULAR_SPEED = 20, THRUST = 0.50;
	public static final double JUMP_THRUST = -15;
	public SimplePlayer(double x,double y,double angle) {
		super(x,y,angle,new SimplePlayerHitshape());
		this.shape.updatePoints(objectAffine);
	}
	public SimplePlayer(double x,double y) {
		this(x,y,0.0);
	}

	@Override
	public void update(Wall[] wallList, double gravity) {
		super.update(wallList,MAX_SPEED,MAX_ANGULAR_SPEED,gravity);
	}
	
	@Override
	public void drawObject(GraphicsContext gc) {
		gc.setFill(Color.BLUE);
		gc.fillRect(-30,-50,60,100);
		gc.setFill(Color.DARKGREY);
		gc.fillOval(-20,-100,40,40);
	}
	private static class SimplePlayerHitshape extends Hitshape{
		public SimplePlayerHitshape() {
			super(new Hitshape[] {new Circle(20.0,6),new Box(60,100)},new Point2D [] {new Point2D(0,-80),new Point2D(0,0)});
		}
	}
}
