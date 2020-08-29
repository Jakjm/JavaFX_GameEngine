package gameObjects;

import hitshape.Box;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
public class Wall extends SolidObject{
	public Color c;
	public double sizeX, sizeY;
	private static final Color DEFAULT_COLOR = Color.RED;
	public Wall(double x,double y,double sizeX,double sizeY,double angle,Color c) {
		super(x,y,angle,new Box(sizeX,sizeY));
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.c = c;
		shape.updatePoints(objectAffine);
	}
	public Wall(double x,double y,double sizeX,double sizeY,double angle) {
		this(x,y,sizeX,sizeY,angle,DEFAULT_COLOR);
	}
	public Wall(double x,double y,double sizeX,double sizeY) {
		this(x,y,sizeX,sizeY,0.0,DEFAULT_COLOR);
	}
	@Override
	public void drawObject(GraphicsContext gc) {
		gc.setFill(c);
		gc.fillRect(-sizeX / 2.0,-sizeY / 2.0, sizeX,sizeY);
		gc.setStroke(Color.GREEN);
		gc.strokeRect(-sizeX / 2.0,-sizeY / 2.0, sizeX,sizeY);
	}
}
