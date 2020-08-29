package hitshape;

import javafx.geometry.Point2D;
import javafx.scene.transform.Affine;

/**
 * Class for Hitshape boxes.
 * @author jordan
 */
public class Box extends Hitshape{
	public Box(double size) {
		this(size,size);
	}
	public Box(double sizeX,double sizeY) {
		super(generateBox(sizeX,sizeY));
	}
	public Box(double size,Affine affine) {
		this(size,size,affine);
	}
	public Box(double sizeX,double sizeY,Affine affine) {
		super(generateBox(sizeX,sizeY),affine);
	}
	private static final Point2D [] generateBox(double sizeX,double sizeY) {
		Point2D[] points = new Point2D[4];
		double halfX = sizeX / 2.0;
		double halfY = sizeY / 2.0;
		points[0] = new Point2D(-halfX,-halfY);
		points[1] = new Point2D(halfX,-halfY);
		points[2] = new Point2D(halfX,halfY);
		points[3] = new Point2D(-halfX,halfY);
		return points;
	}
}
