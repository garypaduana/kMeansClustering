package com.gpaduana.kmeans.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClusteringInfo {

    private List<Point> points = Collections.synchronizedList(new ArrayList<Point>());
    private List<Point> clusters = Collections.synchronizedList(new ArrayList<Point>());
    private Map<Point, List<Point>> clusterToPointsMap = Collections.synchronizedMap(new HashMap<Point, List<Point>>());
    private int desiredClusterSize = 1;

    // Visual touch notifications
    private Map<Point, Long> touchPointsByBirth = Collections.synchronizedMap(new HashMap<Point, Long>());

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

    public ClusteringInfo clone() {
        ClusteringInfo clone = new ClusteringInfo();
        clone.getPoints().addAll(this.points);
        clone.getClusters().addAll(this.clusters);
        clone.getClusterToPointMap().putAll(this.clusterToPointsMap);
        clone.setDesiredClusterSize(this.desiredClusterSize);
        clone.getTouchPointsByBirth().putAll(this.touchPointsByBirth);
        return clone;
    }

    public int getDesiredClusterSize() {
        return desiredClusterSize;
    }

    public void setDesiredClusterSize(int desiredClusterSize) {
        this.desiredClusterSize = desiredClusterSize;
    }

    public Map<Point, Long> getTouchPointsByBirth() {
        return touchPointsByBirth;
    }

    public void setTouchPointsByBirth(Map<Point, Long> touchPointsByBirth) {
        this.touchPointsByBirth = touchPointsByBirth;
    }
}