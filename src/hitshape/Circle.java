package hitshape;

import javafx.geometry.Point2D;
import javafx.scene.transform.Affine;

/**
 * Class for hitshape spheres
 */
public class Circle extends Hitshape{
	/**
	 * Constructs a Circle hitshape with initial location and angle (0,0)
	 * @param radius - the radius of the circle.
	 * @param numPoints - the number of points in the circle. 
	 */
	public Circle(double radius, int numPoints) {
		super(generateCircle(radius,numPoints));
	}
	/**
	 * Constructs a Circle hitshape with a given orientation affine.
	 * @param radius - the radius of the circle.
	 * @param numPoints - the number of points in the circle. 
	 * @param affine - affine that specifies the location of the center of the object in game space, and the angle. 
	 */
	public Circle(double radius, int numPoints,Affine affine) {
		super(generateCircle(radius,numPoints),affine);
	}
	/**
	 * Generates the points that define a circular hitshape, centered at 0,0.
	 * @param radius
	 * @param numPoints
	 * @return
	 */
	private static Point2D [] generateCircle(double radius, int numPoints) {
		Point2D [] points = new Point2D[numPoints];
		double currentAngle = 0.0;
		double sliceSize = (Math.PI * 2.0) / numPoints;
		double pX, pY;
		
		for(int i = 0;i < numPoints;++i, currentAngle += sliceSize) {
			pX = Math.cos(currentAngle) * radius;
			pY = Math.sin(currentAngle) * radius;
			points[i] = new Point2D(pX,pY);
		}
		return points;
	}
	
}
