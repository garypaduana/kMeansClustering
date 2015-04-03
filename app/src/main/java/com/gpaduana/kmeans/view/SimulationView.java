package com.gpaduana.kmeans.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.gpaduana.kmeans.KMeansClusteringSimulatorActivity;
import com.gpaduana.kmeans.R;
import com.gpaduana.kmeans.domain.ClusteringInfo;
import com.gpaduana.kmeans.domain.Point;
import com.gpaduana.kmeans.overlay.TextOverlay;
import com.gpaduana.kmeans.util.CalculationUtil;

public class SimulationView extends SurfaceView implements Runnable {

	private Thread thread;
	private SurfaceHolder surfaceHolder;
	private boolean valid = true;
	private KMeansClusteringSimulatorActivity parent = null;
	private Paint black = new Paint();
	private Paint red = new Paint();
	private Paint blackThin = new Paint();
	private Map<Point, List<List<Point>>> edges = new HashMap<Point, List<List<Point>>>();
	private int lastPointCount = -1;
	private int lastNodeCount = -1;
	private Bitmap image = null;
	
	public SimulationView(Context context, KMeansClusteringSimulatorActivity parent) {
		super(context);
		this.parent = parent;
		surfaceHolder = getHolder();
		black.setARGB(255, 0, 0, 0);
		black.setStrokeWidth(6.0f);
		
		blackThin.setARGB(255, 0, 0, 0);
		blackThin.setStrokeWidth(3.0f);
		
		red.setARGB(255, 255, 0, 0);
		red.setStrokeWidth(12.0f);
		
		resume();
	}

	@Override
	public void run() {
		while(valid){
			// process view
			if(surfaceHolder.getSurface().isValid()){
				Canvas canvas = surfaceHolder.lockCanvas();
				ClusteringInfo ci = this.parent.getProcessing().getClusteringInfo();
				
				canvas.drawARGB(145, 240, 240, 240);
				boolean trans = false;
				
				Point.Type t = Point.Type.DIMEN;
				
				if(this.image != null){
		        	canvas.drawBitmap(this.image, 0, 0, null);
		        	trans = true;
				}
				
				for(Point cluster : ci.getClusters()){
					canvas.drawPoint(cluster.getX(t),
									 cluster.getY(t), red);
				}
				
				for(Point p : ci.getPoints()){
					canvas.drawPoint(p.getX(t), p.getY(t), black);
				}
				
				for(Point cluster : ci.getClusterToPointMap().keySet()){					
					if(ci.getClusterToPointMap().get(cluster).size() >= 3){
						if(!edges.containsKey(cluster) || 
						   ci.getPoints().size() != lastPointCount || 
						   ci.getClusters().size() != lastNodeCount){
														
							edges.put(cluster, CalculationUtil.findEdges(
								ci.getClusterToPointMap().get(cluster), cluster, t));
						}
						
						for(List<Point> edge : edges.get(cluster)){
							canvas.drawLine(edge.get(0).getX(t), edge.get(0).getY(t),
											edge.get(1).getX(t), edge.get(1).getY(t), blackThin);
						}
					}
					else{
						List<Point> children = ci.getClusterToPointMap().get(cluster);
						if(children != null){
							for(int i = 0, j = 1; j < children.size(); i++, j++){
								canvas.drawLine(children.get(i).getX(t),
												children.get(i).getY(t),
												children.get(j).getX(t),
												children.get(j).getY(t), blackThin);
							}
						}
					}
				}
				
				// Draw text
				updateTextOverlays(canvas, ci);
				
				surfaceHolder.unlockCanvasAndPost(canvas);
				
				lastNodeCount = ci.getClusters().size();
				lastPointCount = ci.getPoints().size();
			}
		}
	}
	
	public void pause(){
		valid = false;
		
		while(true){
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		thread = null;
	}
	
	public void resume(){
		valid = true;
		thread = new Thread(this);
		thread.start();
	}

	private void updateTextOverlays(Canvas canvas, ClusteringInfo clusteringInfo){
    	int strokeWidth = CalculationUtil.convertFontToDp(getResources().getDisplayMetrics(), 4);
    	int padding = CalculationUtil.convertFontToDp(getResources().getDisplayMetrics(), 4);
    	
    	int y = getResources().getDimensionPixelSize(R.dimen.tickerFontSize) + padding;
    	TextOverlay points = new TextOverlay("Points: " + clusteringInfo.getPoints().size(),
			getResources().getDimensionPixelSize(R.dimen.tickerFontSize),
			padding, y,	255, 0, 0, 0, strokeWidth, Paint.Align.LEFT);

    	points.draw(canvas);
    	
    	// Update location, comment counts
    	y += getResources().getDimensionPixelSize(R.dimen.tickerFontSize) + padding;
    	TextOverlay nodes = new TextOverlay("Nodes: " + clusteringInfo.getDesiredClusterSize(),
    			getResources().getDimensionPixelSize(R.dimen.tickerFontSize),
    			padding, y,	255, 0, 0, 0, strokeWidth, Paint.Align.LEFT);
    	nodes.draw(canvas);
	}
	
	public void setImage(Bitmap image){
		this.image = image;
	}

	public Bitmap getImage(){
		return image;
	}
}
