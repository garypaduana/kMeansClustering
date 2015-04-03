package com.gpaduana.kmeans.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;


public class ClusteringInfo {
	
	private List<Point> points = Collections.synchronizedList(new ArrayList<Point>());
	private List<Point> clusters = Collections.synchronizedList(new ArrayList<Point>());
	private Map<Point, List<Point>> clusterToPointsMap = Collections.synchronizedMap(new HashMap<Point, List<Point>>());
	private int desiredClusterSize = 1;
	private boolean bitmapAvailable = false;
	
	public ClusteringInfo() {
		// TODO Auto-generated constructor stub
	}

	public List<Point> getPoints() {
		return points;
	}
	public void setPoints(List<Point> points) {
		this.points = points;
	}
	public Map<Point, List<Point>> getClusterToPointMap() {
		return clusterToPointsMap;
	}
	public void setClusterToPointsMap(Map<Point, List<Point>> clusterToPointMap) {
		this.clusterToPointsMap = clusterToPointMap;
	}

	public List<Point> getClusters() {
		return clusters;
	}

	public void setClusters(List<Point> clusters) {
		this.clusters = clusters;
	}
	
	public ClusteringInfo clone(){
		ClusteringInfo clone = new ClusteringInfo();
		clone.getPoints().addAll(this.points);
		clone.getClusters().addAll(this.clusters);
		clone.getClusterToPointMap().putAll(this.clusterToPointsMap);
		clone.setDesiredClusterSize(this.desiredClusterSize);
		return clone;
	}

	public int getDesiredClusterSize() {
		return desiredClusterSize;
	}

	public void setDesiredClusterSize(int desiredClusterSize) {
		this.desiredClusterSize = desiredClusterSize;
	}
	
	public boolean isBitmapAvailable() {
		return bitmapAvailable;
	}

	public void setBitmapAvailable(boolean bitmapAvailable) {
		this.bitmapAvailable = bitmapAvailable;
	}
}
