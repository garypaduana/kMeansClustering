package com.gpaduana.kmeans.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.DisplayMetrics;

import com.gpaduana.kmeans.domain.Point;
import com.gpaduana.kmeans.domain.Point.Type;

public class CalculationUtil {
	
	public static final int convertFontToDp(DisplayMetrics metrics, float size){
    	return (int) (metrics.density * size + 0.5f);
	}
	
	public static List<List<Point>> findEdges(List<Point> points, Point centroid, Type t){
		
		List<List<Point>> edges = new ArrayList<List<Point>>();
		
		Point a = centroid;
		Point b = findFarthestPoint(points, centroid, t);
		Point first = new Point(b.getX(t), b.getY(t), 0);
		Point last = b;
		Point c = null;
		int count = 0;
		
		do{
			double maxAngle = 0.0;
			
			for(Point potential : points){
				if(potential.equals(a) || potential.equals(b) || potential.equals(last)) continue;
				double ab = findDistance(a, b, t);
				double bc = findDistance(b, potential, t);
				double ca = findDistance(potential, a, t);
				
				Map<Double, Double> angles = findAngles(ab, bc, ca);
				
				// verify clockwise direction by taking cross product
				double crossProduct = (b.getX(t)-a.getX(t))*(potential.getY(t)-a.getY(t)) -
									  (b.getY(t)-a.getY(t))*(potential.getX(t)-a.getX(t));
				
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
			b = new Point(c.getX(t), c.getY(t), 0);
			c = null;
			count++;
			if(count > points.size()){
				break;
			}
		}while(!first.equals(b));
		
		return edges;
	}
	
	public static Point findCentroid(List<Point> points){
		float xSum = 0.0f;
		float ySum = 0.0f;
		float zSum = 0.0f;
		
		for(Point p : points){
			xSum += p.getX();
			ySum += p.getY();
			zSum += p.getZ();
		}
		
		if(points.size() == 0){
			return null;
		}
		
		return new Point(xSum / points.size(), ySum / points.size(), zSum / points.size());
	}
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	    
	    if (height > reqHeight || width > reqWidth) {
	        if (width > height) {
	            inSampleSize = Math.round((float)height / (float)reqHeight);
	        } else {
	            inSampleSize = Math.round((float)width / (float)reqWidth);
	        }
	    }
	    return inSampleSize;
	}
		
	public static Map<Double, Double> findAngles(double a, double b, double c){
		Map<Double, Double> sideToAngleMap = new HashMap<Double, Double>();
		
		sideToAngleMap.put(a, Math.toDegrees(Math.acos((b*b + c*c - a*a) / (2*b*c))));
		sideToAngleMap.put(b, Math.toDegrees(Math.acos((a*a + c*c - b*b) / (2*a*c))));
		sideToAngleMap.put(c, 180 - sideToAngleMap.get(a) - sideToAngleMap.get(b));
		
		return sideToAngleMap;
	}
	
	public static Point findFarthestPoint(List<Point> points, Point centroid, Type t){
		Point farthestPoint = null;
		double max = Double.MIN_VALUE;
		for(Point p : points){
			double distance = findDistance(p, centroid, t);
			if(distance > max){
				max = distance;
				farthestPoint = p;
			}
		}
		return farthestPoint;
	}
	
	public static Point findNearestPoint(List<Point> points, Point centroid, Type t){
		Point nearestPoint = null;
		double min = Double.MAX_VALUE;
		for(Point p : points){
			double distance = findDistance(p, centroid, t);
			if(distance < min){
				min = distance;
				nearestPoint = p;
			}
		}
		return nearestPoint;
	}
	
	public static double findDistance(Point a, Point b, Type t){
		return Math.sqrt((Math.pow((a.getX(t) - b.getX(t)), 2) +
						  Math.pow((a.getY(t) - b.getY(t)), 2) +
						  Math.pow((a.getZ(t) - b.getZ(t)), 2)));
	}
}
