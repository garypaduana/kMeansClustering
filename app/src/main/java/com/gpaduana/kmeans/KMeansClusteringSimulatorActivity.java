package com.gpaduana.kmeans;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.gpaduana.kmeans.domain.ClusteringInfo;
import com.gpaduana.kmeans.domain.Point;
import com.gpaduana.kmeans.task.BitmapTask;
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
		// TODO Auto-generated method stub
		kMeansClustering.cancel(true);
		simulationView.pause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		simulationView.resume();
		super.onResume();
		
	}

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		Point p = new Point(motionEvent.getX(), motionEvent.getY(), 0);
		clusteringInfo.getPoints().add(p);
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
            case R.id.process_image:
            	pickImage();
            	break;
        }	
        return super.onOptionsItemSelected(item);
    }
    
    private void pickImage() {
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intent.setType("image/*");
		startActivityForResult(intent, 1);
	}
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 1 && resultCode == Activity.RESULT_OK){
			String path = getRealPathFromURI(data.getData(), this);
			BitmapTask bwt = new BitmapTask(this, path, simulationView.getWidth(), simulationView.getHeight());
			bwt.execute();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
    
    @SuppressWarnings("deprecation")
	public static String getRealPathFromURI(Uri contentUri, Activity activity) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = activity.managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
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
		
		if(clusteringInfo.getDesiredClusterSize() > clusteringInfo.getPoints().size() &&
				!clusteringInfo.isBitmapAvailable()){
			clusteringInfo.setDesiredClusterSize(clusteringInfo.getPoints().size());
		}
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