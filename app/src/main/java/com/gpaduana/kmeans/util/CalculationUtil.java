package com.gpaduana.kmeans.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.DisplayMetrics;

import com.gpaduana.kmeans.domain.Point;

public class CalculationUtil {
	
	public static final int convertFontToDp(DisplayMetrics metrics, float size){
    	return (int) (metrics.density * size + 0.5f);
	}

	public static List<List<Point>> findEdges(List<Point> points, Point centroid){
		
		List<List<Point>> edges = new ArrayList<List<Point>>();
		
		Point a = centroid;
		Point b = findFarthestPoint(points, centroid);
		Point first = new Point(b.getX(), b.getY());
		Point last = b;
		Point c = null;
		int count = 0;
		
		do{
			double maxAngle = 0.0;
			
			for(Point potential : points){
				if(potential.equals(a) || potential.equals(b) || potential.equals(last)) continue;
				double ab = findDistance(a, b);
				double bc = findDistance(b, potential);
				double ca = findDistance(potential, a);
				
				Map<Double, Double> angles = findAngles(ab, bc, ca);
				
				// verify clockwise direction by taking cross product
				double crossProduct = (b.getX()-a.getX())*(potential.getY()-a.getY()) -
									  (b.getY()-a.getY())*(potential.getX()-a.getX());
				
				if(crossProduct <= 0){
					continue;
				}
				
				if(angles.get(ca) > maxAngle){
					maxAngle = angles.get(ca);
					c = potential;
				}
			}
			if(c == null){
				throw new RuntimeException("Could not find valid point in perimeter!");
			}
			
			// a, b, and c are known, draw line from b to c
			List<Point> edge = new ArrayList<Point>();
			edge.add(b);
			edge.add(c);
			edges.add(edge);
			
			last = b;
			b = new Point(c.getX(), c.getY());
			c = null;
			count++;
			if(count > points.size()){
				break;
			}
		}while(!first.equals(b));
		
		return edges;
	}

    /**
     * Finds the centroid (average x, average y) for a collection of points.
     *
     * @param points
     * @return
     */
	public static Point findCentroid(List<Point> points){
		float xSum = 0.0f;
		float ySum = 0.0f;

		for(Point p : points){
			xSum += p.getX();
			ySum += p.getY();
		}
		
		if(points.size() == 0){
			return null;
		}
		
		return new Point(xSum / points.size(), ySum / points.size());
	}

	public static Map<Double, Double> findAngles(double a, double b, double c){
        Map<Double, Double> sideToAngleMap = new HashMap<Double, Double>();
		
		sideToAngleMap.put(a, Math.toDegrees(Math.acos((b*b + c*c - a*a) / (2*b*c))));
		sideToAngleMap.put(b, Math.toDegrees(Math.acos((a*a + c*c - b*b) / (2*a*c))));
		sideToAngleMap.put(c, 180 - sideToAngleMap.get(a) - sideToAngleMap.get(b));

        return sideToAngleMap;
	}

    /**
     * Finds the point farthest from the centroid.
     *
     * @param points
     * @param centroid
     * @return
     */
	public static Point findFarthestPoint(List<Point> points, Point centroid){
		Point farthestPoint = null;
		double max = Double.MIN_VALUE;
		for(Point p : points){
			double distance = findDistance(p, centroid);
			if(distance > max){
				max = distance;
				farthestPoint = p;
			}
		}
		return farthestPoint;
	}

    /**
     * Finds the point closest to the centroid.
     *
     * @param points
     * @param centroid
     * @return
     */
	public static Point findNearestPoint(List<Point> points, Point centroid){
		Point nearestPoint = null;
		double min = Double.MAX_VALUE;
		for(Point p : points){
			double distance = findDistance(p, centroid);
			if(distance < min){
				min = distance;
				nearestPoint = p;
			}
		}
		return nearestPoint;
	}

    /**
     * Finds the distance between two points.
     *
     * @param a
     * @param b
     * @return
     */
	public static double findDistance(Point a, Point b){
		return Math.sqrt((Math.pow((a.getX() - b.getX()), 2) +
						  Math.pow((a.getY() - b.getY()), 2)));
	}
}
