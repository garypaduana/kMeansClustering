package com.gpaduana.kmeans.task;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.gpaduana.kmeans.KMeansClusteringSimulatorActivity;
import com.gpaduana.kmeans.domain.ClusteringInfo;
import com.gpaduana.kmeans.domain.Point;
import com.gpaduana.kmeans.util.CalculationUtil;
import com.gpaduana.kmeans.view.SimulationView;

public class BitmapTask extends AsyncTask<Void, Integer, Bitmap> {
    private final KMeansClusteringSimulatorActivity parent;
    private int width, height;
    private String path;
    
    public BitmapTask(KMeansClusteringSimulatorActivity activity,
    		String path, int width, int height) {
    	
        this.parent = activity;
        this.width = width;
        this.height = height;
        this.path = path;
    }
    
    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Void... params) {
    	InputStream is = null;
    	Bitmap bitmap = null;
    	FileInputStream fis = null;
    	
    	if(isCancelled()){
    		return null;
    	}
    	
    	try{
	        is = new FileInputStream(path);
	        BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inJustDecodeBounds = true;
	        BitmapFactory.decodeStream(is, null, options);
	        // Calculate inSampleSize
	        options.inSampleSize = CalculationUtil.calculateInSampleSize(options, width, height);
	        options.inJustDecodeBounds = false;
	        options.inPurgeable = true;
	        is.close();
	        is = new FileInputStream(path);
	        return Bitmap.createScaledBitmap(BitmapFactory.decodeStream(is, null, options), width, height, true);
    	} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	finally{
    		try{
	    		if(is != null){
	    			is.close();
	    		}
	    		if(fis != null){
	    			fis.close();
	    		}
    		} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	return bitmap;
    }
    
    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(bitmap != null) {
           this.parent.getSimulationView().setImage(bitmap);
           
           	this.parent.getProcessing().cancel(true);
   			this.parent.setProcessing(new KMeansClusteringTask());
   			ClusteringInfo ci = this.parent.getClusteringInfo().clone();
   			ci.setBitmapAvailable(true);
   			
   			// If we should be processing a bitmap...
   			if(bitmap != null){
   				ci.getPoints().clear();
   				ci.getClusterToPointMap().clear();
   				
   				for(int x = 50; x < bitmap.getWidth(); x += 100){
   					for(int y = 50; y < bitmap.getHeight(); y += 100){
   						Point p = new Point(Color.red(bitmap.getPixel(x, y)),
   											Color.green(bitmap.getPixel(x, y)),
   											Color.blue(bitmap.getPixel(x, y)));

   						Point parent = new Point(x, y, 0);
   						p.setParent(parent);
   						
   						ci.getPoints().add(p);
   					}
   				}
   			}
   			
   			this.parent.getProcessing().execute(ci);
        }
    }
}
    
