package graphics.projection;

import java.awt.*;
import java.util.ArrayList;

/** A class representing a 2D Polygon
 * @author Benjamin Cohen-Wang
 */
public class PolygonProjection
{
	/** The collection of points on the screen comprising this projection */
	private Point2D[] points;
	
	/** The color this projection is drawn with */
	private Color color;
	
	/** The drawing priority of this polygon */
	private double priority;
	
	/** The incline at which the polygon represented by this projection is */
	private double incline;
	
	/** The value representing whether or not this instance is being highlighted */
	private boolean highlighted;
	
	/** The color with which this instance is highlighted */
	private final Color highlightColor;
	
	/** Parameterized constructor, initializes point array of instance to given array of points and the priority to 0
	 * 
	 * @param points	The Point2D array this instance will be initialized to
	 */
	public PolygonProjection(Point2D[] points)
	{
		this(points, 0, 0, Color.LIGHT_GRAY);
	}
	
	/** Parameterized constructor, initializes point array of instance to given array of points the priority to the given priority, incline, and color
	 * 
	 * @param points	the Point2D array this instance will be initialized to
	 * @param priority	the priority this instance will be initialized to have
	 * @param incline	the incline this instance will be set to
	 * @param color		the color this projection is drawn with
	 */
	public PolygonProjection(Point2D[] points, double priority, double incline, Color color)
	{
		this(points, priority, incline, color, new Color(200, 232, 255));
	}
	
	/** Parameterized constructor, initializes point array of instance to given array of points, the priority to the given priority, incline, and color
	 * 
	 * @param points	the Point2D array this instance will be initialized to
	 * @param priority	the priority this instance will be initialized to have
	 * @param incline	the incline this instance will be set to
	 * @param color		the color this projection is drawn with
	 * @param highlightColor	the color the highlight of this projection is drawn with
	 */
	public PolygonProjection(Point2D[] points, double priority, double incline, Color color, Color highlightColor)
	{
		this.points = points;
		this.priority = priority;
		this.incline = incline;
		this.color = color;
		this.highlightColor = highlightColor;
		this.highlighted = false;
	}
	
	/** Returns a rotated instance of PolygonProjection rotated about the origin through the given angle
	 * 
	 * @param angle	the angle rotated through
	 * @return an instance of PolygonProjection that is a rotated form of this instance
	 */
	public PolygonProjection rotate(double angle)
	{
		return rotate(new Point2D(0, 0), angle);
	}
	
	/** Returns a rotated instance of PolygonProjection rotated about the given point through the given angle
	 * 
	 * @param point	the point in the 2D graph rotated about
	 * @param angle	the angle rotated through
	 * @return an instance of PolygonProjection that is a rotated form of this instance
	 */
	public PolygonProjection rotate(Point2D point, double angle)
	{
		Point2D[] rotatedPoints = new Point2D[points.length];
		
		for(int i = 0; i < points.length; i ++)
		{
			double xCoord = points[i].getX() - point.getX();
			double yCoord = points[i].getY() - point.getY();
			double cos = Math.cos(angle);
			double sin = Math.sin(angle);
			rotatedPoints[i] = new Point2D(xCoord*cos - yCoord*sin + point.getX(), xCoord*sin + yCoord*cos + point.getY());
		}
		
		return new PolygonProjection(rotatedPoints, priority, incline, color);
	}
	
	/** Translated this projected by the given values
	 * 
	 * @param xInc	the x-increment to be translated by
	 * @param yInc	the y-increment to be translated by
	 * @return a translated instance
	 */
	public PolygonProjection translate(int xInc, int yInc)
	{
		Point2D[] translatedPoints = new Point2D[points.length];
		for(int i = 0; i < points.length; i ++)
		{
			translatedPoints[i] = new Point2D(points[i].getX() + xInc, points[i].getY() + yInc);
		}
		return new PolygonProjection(translatedPoints, priority, incline, color);
	}
	
	/** Properly highlights this instance based on the cursor location
	 * 
	 * @param cursorCoord	the location of the cursor on the screen
	 */
	public void highlight(Point2D coord)
	{	
		int[] xPoints = new int[points.length];
		int[] yPoints = new int[points.length];
		
		for(int i = 0; i < points.length; i ++)
		{
			xPoints[i] = (int)points[i].getX();
			yPoints[i] = (int)points[i].getY();
		}
		
		Polygon thisPoly = new Polygon(xPoints, yPoints, points.length);
		
		highlighted = thisPoly.contains(coord.getX(), coord.getY());
	}
	
	/** Draws this instance on to the given Graphics object
	 * 
	 * @param graphics	the graphics object drawn on
	 */
	public void draw(Graphics graphics)
	{
		Color currentColor = graphics.getColor();
		
		Color inclinedColor = darken(color, Math.min(30*incline, 50));
		
		graphics.setColor(inclinedColor);
		
		int[] xPoints = new int[points.length];
		int[] yPoints = new int[points.length];
		
		for(int i = 0; i < points.length; i ++)
		{
			xPoints[i] = (int)points[i].getX();
			yPoints[i] = (int)points[i].getY();
		}
		
		graphics.fillPolygon(xPoints, yPoints, points.length);
		
		graphics.setColor(highlighted ? highlightColor : darken(inclinedColor, 15));
		
		graphics.drawPolygon(xPoints, yPoints, points.length);
		
		/*
		 * Triangulization:
		 * graphics.drawLine(xPoints[0], yPoints[0], xPoints[2], yPoints[2]);
		 */
		
		graphics.setColor(currentColor);
	}
	
	/**
	 * @return the array of 2D points comprising this PolygonProjection
	 */
	public Point2D[] getPoints()
	{
		return points;
	}
	
	/**
	 * @return the priority of this instance
	 */
	public double getPriority()
	{
		return priority;
	}
	
	/**
	 * @return the incline of this instance
	 */
	public double getIncline()
	{
		return incline;
	}
	
	/** A method returning a darkened version of the given color
	 * 
	 * @param initial the color whose darkened version will be returned
	 * @param factor the factor controlling by how much the color will be darkened
	 * @return
	 */
	private static Color darken(Color initial, double factor)
	{
		int red = initial.getRed();
		int green = initial.getGreen();
		int blue = initial.getBlue();
		
		red = (int) (red < factor ? 0 : red - factor);
		green = (int) (green < factor ? 0 : green - factor);
		blue = (int) (blue < factor ? 0 : blue - factor);
		
		return new Color(red, green, blue);
	}
	
	/** A method returning a brightened version of the given color
	 * 
	 * @param initial the color whose brightened version will be returned
	 * @param factor the factor controlling by how much the color will be darkened
	 * @return
	 */
	private static Color brighten(Color initial, double factor)
	{
		int red = initial.getRed();
		int green = initial.getGreen();
		int blue = initial.getBlue();
		
		red = (int) (red + factor > 255 ? 255 : red + factor);
		green = (int) (green + factor > 255 ? 255 : green + factor);
		blue = (int) (blue + factor > 255 ? 255 : blue + factor);
		
		return new Color(red, green, blue);
	}
	
	/**	Sorts the given array list based on priority
	 * 
	 * @param polygons	the polygon projection array list to be sorted
	 * @return a sorted ArrayList of PolygonProjecton objects from the given array
	 */
	public static ArrayList<PolygonProjection> sort(ArrayList<PolygonProjection> polygons)
	{
		ArrayList<PolygonProjection> sorted = new ArrayList<PolygonProjection>();
		sorted.add(polygons.get(0));
		for(int i = 1; i < polygons.size(); i ++)
		{
			for(int j = sorted.size() - 1; j >= 0; j --)
			{
				if(polygons.get(i).priority >= sorted.get(j).priority)
				{
					sorted.add(j + 1, polygons.get(i));
					break;
				}
				else if(j == 0)
				{
					sorted.add(0, polygons.get(i));
					break;
				}
			}
		}
		return sorted;
	}
	
	/**	Sorts the given array based on priority
	 * 
	 * @param polygons	the polygon projection array to be sorted
	 * @return a sorted ArrayList of PolygonProjecton objects from the given array
	 */
	public static ArrayList<PolygonProjection> sort(PolygonProjection[] polygons)
	{
		ArrayList<PolygonProjection> sorted = new ArrayList<PolygonProjection>();
		sorted.add(polygons[0]);
		for(int i = 1; i < polygons.length; i ++)
		{
			for(int j = sorted.size() - 1; j >= 0; j --)
			{
				if(polygons[i].priority >= sorted.get(j).priority)
				{
					sorted.add(j + 1, polygons[i]);
					break;
				}
				else if(j == 0)
				{
					sorted.add(0, polygons[i]);
					break;
				}
			}
		}
		return sorted;
	}
}
