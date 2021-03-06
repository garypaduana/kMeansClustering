package com.gpaduana.kmeans.task;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.AsyncTask;

import com.gpaduana.kmeans.domain.ClusteringInfo;
import com.gpaduana.kmeans.domain.Point;
import com.gpaduana.kmeans.util.CalculationUtil;

public class KMeansClusteringTask extends AsyncTask<ClusteringInfo, ClusteringInfo, ClusteringInfo>{
			
	private ClusteringInfo clusteringInfo = new ClusteringInfo();
	private float movementTolerance = 10.0f;
	private int iterations = 0;
	
	@Override
	protected ClusteringInfo doInBackground(ClusteringInfo... params) {

        if(isCancelled()){
            return null;
        }

		int count = params.length;
		if(count != 1){
			throw new IllegalArgumentException("Incorrect parameter size!");
		}

		ClusteringInfo ci = params[0];

        if(ci.getClusters().size() != ci.getDesiredClusterSize()) {
            ci.getClusters().clear();

            // initialize the clusters to a few of the first points
            for (int i = 0; i < ci.getDesiredClusterSize(); i++) {
                if (ci.getPoints().size() > 0) {
                    ci.getClusters().add(
                            new Point(ci.getPoints().get(i).getX(),
                                    ci.getPoints().get(i).getY()));
                }
            }
        }

		double totalMovement = Double.MAX_VALUE;
		
		while(totalMovement > movementTolerance){

            if(isCancelled()){
				return null;
			}
			// Assume everything worked great!
			totalMovement = 0.0;
			
			// Clear all points that were assigned to a cluster center
			ci.getClusterToPointMap().clear();
			
			for(Point c : ci.getClusters()){
				ci.getClusterToPointMap().put(c, new ArrayList<Point>());
			}
			
			// reassign all points to a new cluster center
			for(Point p : ci.getPoints()){
				if(isCancelled()){
					return null;
				}
				double minDistance = Double.MAX_VALUE;
				Point nearestCluster = null;
				for(Point c : ci.getClusters()){
					double distance = CalculationUtil.findDistance(p, c);
					if(distance < minDistance || nearestCluster == null){
						nearestCluster = c;
						minDistance = distance;
					}
				}
				ci.getClusterToPointMap().get(nearestCluster).add(p);
			}
			
			List<Point> movedClusters = new ArrayList<Point>();
			List<Point> newClusters = new ArrayList<Point>();
			
			// Recalculate cluster centers
			for(Point c : ci.getClusters()){
				
				if(isCancelled()){
					return null;
				}
				
				float xCenter = 0.0f;
				float yCenter = 0.0f;

				for(Point child : ci.getClusterToPointMap().get(c)){
					xCenter += child.getX();
					yCenter += child.getY();
				}
				
				Point newCenter = null;
				
				if(ci.getClusterToPointMap().get(c).size() > 0){
					xCenter = xCenter / ci.getClusterToPointMap().get(c).size();
					yCenter = yCenter / ci.getClusterToPointMap().get(c).size();

					newCenter = new Point(xCenter, yCenter);
				}
				else{
					newCenter = c;
				}
				
				totalMovement += CalculationUtil.findDistance(newCenter, c);
				
				if(!newCenter.equals(c)){
					movedClusters.add(c);
					newClusters.add(newCenter);
				}
			}
						
			for(int i = 0; i < movedClusters.size(); i++){
				ci.getClusterToPointMap().put(newClusters.get(i), new ArrayList<Point>());
				for(Point movingPoint : ci.getClusterToPointMap().get(movedClusters.get(i))){
					ci.getClusterToPointMap().get(newClusters.get(i)).add(movingPoint);
				}
				ci.getClusters().add(newClusters.get(i));
			}
			
			for(Point toRemove : movedClusters){
				ci.getClusterToPointMap().remove(toRemove);
				ci.getClusters().remove(toRemove);
			}
			
			iterations++;
			publishProgress(ci.clone());
		}
		return ci.clone();
		
	}	
	
	@Override
	protected void onPostExecute(ClusteringInfo result) {
		this.clusteringInfo = result;
	}
	
	@Override
	protected void onProgressUpdate(ClusteringInfo... values) {
		clusteringInfo = values[0];
	}
	
	public ClusteringInfo getClusteringInfo(){
		return clusteringInfo.clone();
	}
}
