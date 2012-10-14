package de.uni_potsdam.hpi.bpt.promnicat.correctionModule.wrapper;

import math.geom2d.Box2D;
import math.geom2d.Point2D;
import math.geom2d.polygon.HRectangle2D;
import math.geom2d.polygon.Rectangle2D;
import de.uni_potsdam.hpi.bpt.ai.diagram.Bounds;
import de.uni_potsdam.hpi.bpt.ai.diagram.Point;

/**
 * contains convenience methods for using the meth.geom2d library
 * @author Christian Kieschnick
 *
 */
public abstract class GeomUtil {

	/**
	 * shortcut method to convert easily from {@link de.uni_potsdam.hpi.bpt.ai.diagram.Point} to {@link math.geom2D.Point2D}  
	 * @param point
	 * @return
	 */
	public static Point2D to(Point point){
		return new Point2D(point.getX(), point.getY());
	}
	
	/**
	 * shortcut method to convert easily from {@link math.geom2D.Point2D} to {@link de.uni_potsdam.hpi.bpt.ai.diagram.Point} 
	 * @param point
	 * @return
	 */
	public static Point to(Point2D point){
		return new Point(point.getX(), point.getY());
	}
	
	/**
	 * shortcut method to add Points (of potentially different types)
	 * @param first
	 * @param second
	 * @return
	 */
	public static Point2D add(Point2D first, Point second){
		return new Point2D(
				first.getX() + second.getX(), 
				first.getY() + second.getY());
	}
	
	/**
	 * shortcut method to add Points (of potentially different types)
	 * @param first
	 * @param second
	 * @return
	 */
	public static Point add(Point first, Point second){
		return new Point(
				first.getX() + second.getX(), 
				first.getY() + second.getY());
	}
	
	/**
	 * subtract to points (x1 - x2, y1 - y2) 
	 * @param first
	 * @param second
	 * @return
	 */
	public static Point sub(Point first, Point second){
		return new Point(
				first.getX() - second.getX(),
				first.getY() - second.getY());
	}
	
	/**
	 * determine the center between the given points
	 * @param first
	 * @param second
	 * @return
	 */
	public static Point center(Point first, Point second){
		return new Point(
				(first.getX() + second.getX()) / 2,
				(first.getY() + second.getY()) / 2);
	}
	
	/**
	 * determine the center between the given points
	 * @param first
	 * @param second
	 * @return
	 */
	public static Point2D center(Point2D first, Point2D second){
		return to(center(to(first),to(second)));
	}
	
	/**
	 * determine if two points are equal
	 * @param first
	 * @param second
	 * @return
	 */
	public static boolean areEqual(Point2D first, Point2D second){
		return first.getX() == second.getX() 
				&& first.getY() == second.getY();
	}
	
	/**
	 * create an Rectangle2D given by the upper left and lower right corner
	 * @param upperLeft
	 * @param lowerRight
	 * @return
	 */
	public static Rectangle2D toRectangle2D(Point2D upperLeft, Point2D lowerRight){
		return new Rectangle2D(
				upperLeft.getX(), 
				upperLeft.getY(), 
				lowerRight.getX() - upperLeft.getX(), 
				lowerRight.getY() - upperLeft.getY());
	}
	
	/**
	 * create an Rectangle2D given by the upper left and lower right corner
	 */
	public static Rectangle2D toRectangle2D(Point upperLeft, Point lowerRight){
		return new Rectangle2D(
				upperLeft.getX(), 
				upperLeft.getY(), 
				lowerRight.getX() - upperLeft.getX(), 
				lowerRight.getY() - upperLeft.getY());
	}
	
	/**
	 * transform the Box2D to Bounds
	 */
	public static Bounds toBounds(Box2D boundingBox){
		HRectangle2D bounds = boundingBox.getAsRectangle();
		return new Bounds(
				new Point(bounds.getMinX(), bounds.getMinY()),
				new Point(bounds.getMaxX(), bounds.getMaxY()));
	}
}
