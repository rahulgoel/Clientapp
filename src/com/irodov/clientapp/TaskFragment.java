package com.irodov.clientapp;


	import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

	/**
	 * TaskFragment manages a single background task and retains itself across
	 * configuration changes.
	 */
	public class TaskFragment extends Fragment {
	  private static final String TAG = TaskFragment.class.getSimpleName();
	  private static final boolean DEBUG = true; // Set this to false to disable logs.
	  ProgressDialog pDialogLogin;
	  /**
	   * Callback interface through which the fragment can report the task's
	   * progress and results back to the Activity.
	   */
	  static interface TaskCallbacks {
	    void onPreExecute();
	    void onProgressUpdate(int percent);
	    void onCancelled();
	    void onPostExecute();
	  }

	  private TaskCallbacks mCallbacks;
	  private fbLogin mTask;
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
	    if (!(activity instanceof TaskCallbacks)) {
	      throw new IllegalStateException("Activity must implement the TaskCallbacks interface.");
	    }

	    // Hold a reference to the parent Activity so we can report back the task's
	    // current progress and results.
	    mCallbacks = (TaskCallbacks) activity;
	  }

	  /**
	   * This method is called once when the Fragment is first created.
	   */
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    if (DEBUG) Log.i(TAG, "onCreate(Bundle)");
	    super.onCreate(savedInstanceState);
	    
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
	  public void start() {
	    if (!mRunning) {
	      mTask = new fbLogin();
	      mTask.execute();
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

	  class fbLogin extends AsyncTask<String, String, Boolean> {
	    	protected void onPreExecute() {
			//	setContentView(R.layout.loading); 
	    		 mCallbacks.onPreExecute();
	    	      mRunning = true;
		  }
			protected Boolean doInBackground(String... params) {
				 boolean value1=false;
				 Activity a=getActivity();
				 assert a!=null;
				Session.openActiveSession(a, true, new Session.StatusCallback() {
					 
		            // callback when session changes state
		            @Override
		            public void call(Session session, SessionState state, Exception exception) {
		            	if (session.isOpened()) {
		            		
		                    // make request to the /me API
		                    Request.newMeRequest(session, new Request.GraphUserCallback() {
		
		                      // callback after Graph API response with user object
		                      @Override
		                      public void onCompleted(GraphUser user, Response response) {
		                        if (user != null) {
		                        /*  TextView welcome = (TextView) findViewById(R.id.welcome);
		                          welcome.setText("Hello " + user.getName() + "!");*/  // TODO check this later
		            
		                        	
		                        	//             userFunction.logoutUser(getApplicationContext());
		               //             db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json.getString(KEY_UID), json_user.getString(KEY_CREATED_AT));                        
		                             
		                            // Launch Dashboard Screen
		                          	MainActivity.Logged=true;
		                          	
		                          	Intent dashboard = new Intent(getActivity().getApplicationContext(), MainActivity.class);
		                             
		                            // Close all views before launching Dashboard
		                            dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		                            startActivity(dashboard);
		                             
		                            // Close Login Screen
		                            getActivity().finish();
		                        }
		                        else{
		                        //	loginErrorMsg.setText("Cannot login from FB");
		                        	Log.d("fb err","Canot login from fb in task frag,ent");
		                        }
		                      }
		                    }).executeAsync();
		            	}
		            }
		          });
				return value1;
			}
	    
			 protected void onPostExecute() {
				   mCallbacks.onPostExecute();
				   mRunning = false;
				 
			 }
	    }	
	
}
