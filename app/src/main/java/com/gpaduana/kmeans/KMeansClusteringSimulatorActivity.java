package com.gpaduana.kmeans;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.gpaduana.kmeans.domain.ClusteringInfo;
import com.gpaduana.kmeans.domain.Point;
import com.gpaduana.kmeans.task.KMeansClusteringTask;
import com.gpaduana.kmeans.view.SimulationView;

public class KMeansClusteringSimulatorActivity extends SherlockActivity implements OnTouchListener {
    
	private SimulationView simulationView;

	private ClusteringInfo clusteringInfo = new ClusteringInfo();
	private KMeansClusteringTask kMeansClustering = new KMeansClusteringTask();
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        simulationView = new SimulationView(this, this);
        simulationView.setOnTouchListener(this);
        setContentView(simulationView);
    }

	@Override
	protected void onPause() { 	
		kMeansClustering.cancel(true);
		simulationView.pause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		simulationView.resume();
		super.onResume();
		
	}

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		Point p = new Point(motionEvent.getX(), motionEvent.getY());

		clusteringInfo.getPoints().add(p);
        clusteringInfo.getTouchPointsByBirth().put(p, System.nanoTime());
		refreshDataSet();
		
		return false;
	}
	
	private void refreshDataSet(){
		kMeansClustering.cancel(true);
		kMeansClustering = new KMeansClusteringTask();
		kMeansClustering.execute(clusteringInfo.clone());
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getSupportMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_node:
                addNode();
                break;
            case R.id.remove_node:
            	removeNode();
            	break;
            case R.id.clear_points:
                clearPoints();
                break;
        }	
        return super.onOptionsItemSelected(item);
    }

    /**
     * If cluster size is greater than or larger than 2, subtract 1 cluster
     */
	private void removeNode() {
		System.out.println("size: " + clusteringInfo.getDesiredClusterSize());
		clusteringInfo.setDesiredClusterSize(
				clusteringInfo.getDesiredClusterSize() -1);
		
		if(clusteringInfo.getDesiredClusterSize() == 0){
			clusteringInfo.setDesiredClusterSize(1);
		}
		refreshDataSet();
	}

	private void addNode() {
		clusteringInfo.setDesiredClusterSize(
				clusteringInfo.getDesiredClusterSize() + 1);
		
		if(clusteringInfo.getDesiredClusterSize() > clusteringInfo.getPoints().size()){
			clusteringInfo.setDesiredClusterSize(clusteringInfo.getPoints().size());
		}
		refreshDataSet();
	}

    private void clearPoints(){
        kMeansClustering.cancel(true);
        clusteringInfo = new ClusteringInfo();
        refreshDataSet();
    }

	public ClusteringInfo getClusteringInfo() {
		return clusteringInfo;
	}

	public void setClusteringInfo(ClusteringInfo clusteringInfo) {
		this.clusteringInfo = clusteringInfo;
	}
	
	public KMeansClusteringTask getProcessing(){
		return kMeansClustering;
	}
	
	public void setProcessing(KMeansClusteringTask processing){
		this.kMeansClustering = processing;
	}

	public SimulationView getSimulationView() {
		return simulationView;
	}

	public void setSimulationView(SimulationView simulationView) {
		this.simulationView = simulationView;
	}
}