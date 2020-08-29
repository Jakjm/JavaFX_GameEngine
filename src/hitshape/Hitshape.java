package hitshape;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import launcher.Launcher;

public abstract class Hitshape {
	/**The raw points of the hitshape, without location or angle applied.*/
	public final Point2D [] POINTS;
	
	/**The vertices of the hitshape in game space, with angle and location applied.*/
	public Point2D [] vertices;
	/**The edges of the hitshape, calculated using the vertices. */
	public Point2D [] edges; 
	
	/**
	 * Constructs the hitshape using the given points, without any translation or rotation.
	 * @param points - the points to construct the hitshape with. 
	 */
	public Hitshape(Point2D [] points) {
		this(points,new Affine());
	}
	public Hitshape(Hitshape [] arr,Point2D [] offset) {
		this(constructPoints(arr,offset));
	}
	public static Point2D[] constructPoints(Hitshape [] arr,Point2D [] offset) {
		Point2D center = new Point2D(0,0);
		
		int numVertices = 0;
		for(int i = 0;i < arr.length;++i) {
			numVertices += arr[i].POINTS.length;
		}
		Point2D [] pts = new Point2D[numVertices];
		int shapeNo = 0;
		int numWithinShape;
		for(int i = 0;i < pts.length;) {
			numWithinShape = 0;
			while(numWithinShape < arr[shapeNo].POINTS.length) {
				pts[i] = arr[shapeNo].POINTS[numWithinShape].add(offset[shapeNo]);
				++i;
				++numWithinShape;
			}
			++shapeNo;
		}
		
		Arrays.sort(pts, new Comparator<Point2D>() {
			public int compare(Point2D p1, Point2D p2) {
				return (int)Math.signum(p1.angle(Point2D.ZERO) - p2.angle(Point2D.ZERO));
			}
		});
		return pts;
	}
	/**
	 * Constructs the hitshape from the base points, with the given affine applied. 
	 * @param points - the points to construct the hitshape with.
	 * @param affine - the initial affine of the hitshape.
	 */
	public Hitshape(Point2D [] points,Affine affine) {
		this.POINTS = points;
		vertices = new Point2D[points.length];
		edges = new Point2D[points.length];
		updatePoints(affine);
	}
	
	/**
	 * Updates the points of the hitshape using the affine.
	 * This method should only be called when the affine has changed. 
	 * edge i = vertex i - vertex ((i - 1) mod numVertices)  
	 */
	public void updatePoints(Affine affine) {
		//Updating each of the points, and the edges. 
		vertices[0] = affine.transform(POINTS[0]);
		for(int i = 1;i < vertices.length; ++i) {
			vertices[i] = affine.transform(POINTS[i]);
			//Generate the current edge using the vertex behind this one. 
			edges[i] = vertices[i].subtract(vertices[i-1]);
		}
		//The first vertex is paired with the last vertex. 
		edges[0] = vertices[0].subtract(vertices[vertices.length - 1]);
	}
	
	public void drawHitshape(GraphicsContext gc, Point2D offset) {
		Affine translation = new Affine();
		translation.appendTranslation(-offset.getX(),-offset.getY());
		gc.setTransform(translation);
		drawHitshape(gc);
		Affine zero = new Affine();
		gc.setTransform(zero);
	}
	
	/**
	 * Draws the points that define the hitshape. 
	 * @param gc - the graphics context that the points should be drawn on. 
	 */
	public void drawHitshape(GraphicsContext gc) {
		double x,y;
		double coordX,coordY;
		gc.setFill(Color.BLACK);
		for(int i = 0;i < vertices.length;++i) {
			x = vertices[i].getX();
			y = vertices[i].getY();
			
		
			gc.fillRect(x-3,y-3,6,6);
			if(x > Launcher.SCREEN_X - 50) {
				coordX = x - 50;
			}
			else {
				coordX = x;
			}
			
			if(y > Launcher.SCREEN_Y - 10) {
				coordY = y - 10;
			}
			else {
				coordY = y;
			}
			gc.fillText(String.format("(%.1f,%.1f)",x,y),coordX,coordY);
		}
	}
	
	/**
	 * Check if a collision has occurred between this hitshape and the other one.
	 * @param other the hitshape that this hitshape may have collided with.
	 * @return a CollisionData object that helps resolve the collision if one occurred, or null if a collision did not occur between the two objects.
	 */
	public CollisionData checkCollision(Hitshape other) {
		double minOverlap = Double.POSITIVE_INFINITY;
		Point2D minAxis = null;
		Point2D currentNormal; 
		Projection thisProj, otherProj;
		
		double overlap1;
		double overlap2;
		
		Point2D surfacePoint = null;
		Point2D v1 = null, v2 = null;
		
		//This boolean indicates if the collision is measured from a surface of the other hitshape, or from our hitshape.
		//False means the other hitshape, true means ours.
		boolean surface = false;
		
		//Testing normals which are perpendicular to the sides of the other hitshape.
		for(int i = 0;i < other.edges.length;++i) {
			//Get the inverse of the current edge as the current axis to project the two shapes on.
			currentNormal = other.edges[i];
			currentNormal = new Point2D(currentNormal.getY(),-currentNormal.getX());
			
			thisProj = new Projection(currentNormal,this);
			otherProj = new Projection(currentNormal,other);
			
			overlap1 = otherProj.min - thisProj.max;
			overlap2 = otherProj.max - thisProj.min;
			if(overlap1 > 0.0 || overlap2 < 0.0) {
				return null;
			}
			else {
				//If overlap 1 is the smaller overlap (in terms of absolute value),
				//Check if it is smaller than the lowest overlap.
				if(-overlap1 < overlap2) {
					//We don't know if the minimum overlap is positive or negative. 
					if(-overlap1 < Math.abs(minOverlap)){
						minOverlap = overlap1;
						minAxis = currentNormal;
						
						
						//Define the left and right side of the surface.
						v2 = other.vertices[i];
						if(i == 0) {
							v1 = other.vertices[other.vertices.length - 1]; 
						}
						else {
							v1 = other.vertices[i - 1];
						}
					}
				}
				else {
					if(overlap2 < Math.abs(minOverlap)) {
						minOverlap = overlap2;
						minAxis = currentNormal;
						
						
						//Define the left and right side of the surface.
						v2 = other.vertices[i];
						if(i == 0) {
							v1 = other.vertices[other.vertices.length - 1]; 
						}
						else {
							v1 = other.vertices[i - 1];
						}
					}
				}
			}
		}
		
		
		
		
		//Testing normals which are perpendicular to the edges of this hitshape. 
		for(int i = 0;i < this.edges.length;++i) {
			//Get the inverse of the current edge as the current axis to project the two shapes on.
			currentNormal = edges[i];
			currentNormal = new Point2D(currentNormal.getY(),-currentNormal.getX());
			
			thisProj = new Projection(currentNormal,this);
			otherProj = new Projection(currentNormal,other);
			
			overlap1 = otherProj.min - thisProj.max;
			overlap2 = otherProj.max - thisProj.min;			
			//If the overlap is negative we need to flip it. 
			//Overlap1 must be non-positive, and overlap2 must be non-negative.
			if(overlap1 > 0.0 || overlap2 < 0.0) {
				return null;
			}
			else {
				//If overlap 1 is the smaller overlap (in terms of absolute value),
				//Check if it is smaller than the lowest overlap.
				if(-overlap1 < overlap2) {
					//We don't know if the minimum overlap is positive or negative. 
					if(-overlap1 < Math.abs(minOverlap)){
						minOverlap = overlap1;
						minAxis = currentNormal;
						
						//Define the left and right side of the surface.
						v2 = this.vertices[i];
						if(i == 0) {
							v1 = this.vertices[this.vertices.length - 1]; 
						}
						else {
							v1 = this.vertices[i - 1];
						}
						surface = true;
					}
				}
				else {
					if(overlap2 < Math.abs(minOverlap)) {
						minOverlap = overlap2;
						minAxis = currentNormal;
						
						
						//Define the left and right side of the surface.
						v2 = this.vertices[i];
						if(i == 0) {
							v1 = this.vertices[this.vertices.length - 1]; 
						}
						else {
							v1 = this.vertices[i - 1];
						}
						surface = true;
					}
				}
			}
		}
		//We always want a positive overlap - so we flip the vector and the overlap if the overlap is negative.
		//That way the net vector is the same. 
		if(minOverlap < 0) {
			minOverlap =- minOverlap;
			minAxis = minAxis.multiply(-1);
		}
		double surfaceProjection;
		if(surface) {
			v1 = v1.subtract(minAxis.multiply(minOverlap));
			v2 = v2.subtract(minAxis.multiply(minOverlap));
			surfaceProjection = v1.dotProduct(minAxis) / minAxis.dotProduct(minAxis);
			for(int i = 0;i < other.vertices.length;++i) {
				double projection = other.vertices[i].dotProduct(minAxis) / minAxis.dotProduct(minAxis);
			}
		}
		else {
			surfaceProjection = v1.dotProduct(minAxis) / minAxis.dotProduct(minAxis);
			for(int i = 0; i < this.vertices.length;++i) {
				Point2D adjustedPoint = this.vertices[i].subtract(minAxis.multiply(minOverlap));
				double projection = adjustedPoint.dotProduct(minAxis) / minAxis.dotProduct(minAxis);
			}
		}
		CollisionData data = new CollisionData(minAxis,minOverlap,surfacePoint,new Point2D [] {});
		return data;
	}
	/**
	 * 
	 * This class is used to calculate the minimum and maximum points of the hitshape when projected along the given axis. 
	
	 */
	private static class Projection{
		double min, max;
		/**
		 * @param axis - the axis that the shape is to be projected onto. 
		 * @param zero - the midpoint of the current edge. The projection should be measured from this point.
		 */
		public Projection(Point2D axis, Hitshape shape) {
			/**The minimum and maximum points that the shape projects to along the axis.*/
			double min = Double.POSITIVE_INFINITY;
			double max = Double.NEGATIVE_INFINITY;
			Point2D currentPoint;
			double dotProduct;
			double axisLengthSq = axis.dotProduct(axis);
			for(int i = 0;i < shape.vertices.length;++i) {
				currentPoint = shape.vertices[i];
				dotProduct = currentPoint.dotProduct(axis);
				if(dotProduct < min) {
					min = dotProduct;
				}
				if(dotProduct > max) {
					max = dotProduct;
				}
			}
			min /= axisLengthSq;
			max /= axisLengthSq;
			this.min = min;
			this.max = max;
			
		}
	}
	/**
	 * An object that stores information about a collision between two hitshapes. 
	 * @author jordan
	 * 
	 * The colAxis multiplied by the overlap amount is the motion required to resolve the collision.
	 */
	public class CollisionData{
		/**The axis of collision*/
		public Point2D colAxis;
		/**The amount of overlap along the collision axis.
		 * This should always be positive.
		 */
		public double overlapAmount;
		public Point2D surfacePoint;
		public Point2D [] hitpoints;
		

		public CollisionData(Point2D colAxis,double amount,Point2D midPoint,Point2D [] hitpoints) {
			this.colAxis = colAxis;
			this.overlapAmount = amount;
			this.surfacePoint = midPoint;
			
			this.hitpoints = hitpoints;
		}
		public String toString() {
			return String.format("Collision Axis: %s Overlap amount: %f",colAxis,overlapAmount);
		}
	}
}
