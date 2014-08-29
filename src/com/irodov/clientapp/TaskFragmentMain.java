package com.irodov.clientapp;

import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.irodov.clientapp.TaskFragment.fbLogin;

public class TaskFragmentMain extends Fragment {
	  private static final String TAG = TaskFragmentMain.class.getSimpleName();
	  private static final boolean DEBUG = true; // Set this to false to disable logs.
	  ProgressDialog pDialog;
	  GPSTracker gpsTracker;
	    // Google Places
	    GooglePlaces googlePlaces;
	    
	    // Places List
	    PlacesList nearPlaces;
	    ListView lv;
	  /**
	   * Callback interface through which the fragment can report the task's
	   * progress and results back to the Activity.
	   */
	  static interface TaskCallbacksMain {
	    void onPreExecute();
	    void onProgressUpdate(int percent);
	    void onCancelled();
	    void onPostExecute(PlacesList p);
	  }

	  private TaskCallbacksMain mCallbacks;
	  private LoadPlaces mTask;
	  private boolean mRunning;

	  /**
	   * Hold a reference to the parent Activity so we can report the task's current
	   * progress and results. The Android framework will pass us a reference to the
	   * newly created Activity after each configuration change.
	   */
	  @Override
	  public void onAttach(Activity activity) {
	    if (DEBUG) Log.i(TAG, "onAttach(Activity)");
	    super.onAttach(activity);
	    if (!(activity instanceof TaskCallbacksMain)) {
	      throw new IllegalStateException("Activity must implement the TaskCallbacks interface.");
	    }

	    // Hold a reference to the parent Activity so we can report back the task's
	    // current progress and results.
	    mCallbacks = (TaskCallbacksMain) activity;
	  }

	  /**
	   * This method is called once when the Fragment is first created.
	   */
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    if (DEBUG) Log.d(TAG, "onCreate(Bundle)1st pass");
	    super.onCreate(savedInstanceState);
	     gpsTracker = new GPSTracker(getActivity());
	     googlePlaces =new GooglePlaces();
	    setRetainInstance(true);
	  }

	  /**
	   * Note that this method is <em>not</em> called when the Fragment is being
	   * retained across Activity instances. It will, however, be called when its
	   * parent Activity is being destroyed for good (such as when the user clicks
	   * the back button, etc.).
	   */
	  @Override
	  public void onDestroy() {
	    if (DEBUG) Log.i(TAG, "onDestroy()");
	    super.onDestroy();
	    cancel();
	  }

	  /*****************************/
	  /***** TASK FRAGMENT API *****/
	  /*****************************/

	  /**
	   * Start the background task.
	   */
	  public void start(String s) {
	    if (!mRunning) {
	      mTask = new LoadPlaces();
	      Log.d("strinf11","its :"+ s);
	      mTask.execute(s);
	      mRunning = true;
	    }
	  }

	  /**
	   * Cancel the background task.
	   */
	  public void cancel() {
	    if (mRunning) {
	      mTask.cancel(false);
	      mTask = null;
	      mRunning = false;
	    }
	  }

	  /**
	   * Returns the current state of the background task.
	   */
	  public boolean isRunning() {
	    return mRunning;
	  }

	  class LoadPlaces extends AsyncTask<String, String, String> {
	    	 
	        /**
	         * Before starting background thread Show Progress Dialog
	         * */
	        @Override
	        protected void onPreExecute() {
	        	Log.d("TaksFragamin","Why arent we here?");
	        	gpsTracker = new GPSTracker(getActivity());
	        	mCallbacks.onPreExecute();
	        	Log.d("TaskFragmentMain","LoadPlaces"+gpsTracker.getLatitude());
	        	mRunning = true;
	        	        }
	 
	        /**
	         * getting Places JSON
	         * */
	        protected String doInBackground(String... places) {
	            // creating Places class object
	            //googlePlaces = new GooglePlaces();
	        
	        	Log.d("Back","are we ever:" + gpsTracker.getLatitude());
	             
	            try {
	            	
	                // Separeate your place types by PIPE symbol "|"
	                // If you want all types places make it as null
	                // Check list of types supported by google
	                // 
	                String types = places[0]; // Listing places only cafes, restaurants
	                 
	                // Radius in meters - increase this value if you don't find any places
	                double radius = 1000; // 1000 meters 
	                 
	                // get nearest places
	                nearPlaces = googlePlaces.search(gpsTracker.getLatitude(),
	                        gpsTracker.getLongitude(), radius, types);
	                 
	 
	            } catch (Exception e) {
	            	Log.e("nearplaces",e.toString());
	                e.printStackTrace();
	            }
	            return null;
	        }
	    
	        /**
	         * After completing background task Dismiss the progress dialog
	         * and show the data in UI
	         * Always use runOnUiThread(new Runnable()) to update UI from background
	         * thread, otherwise you will get error
	         * **/
	        protected void onPostExecute(String file_url) {
	            // dismiss the dialog after getting all products
	        	mCallbacks.onPostExecute(nearPlaces);
	        	mRunning = false;
	        
	        }   
	    }

	  
	  
}
