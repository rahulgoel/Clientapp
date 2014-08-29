package com.irodov.clientapp;


import java.util.HashMap;
 
import org.json.JSONException;
import org.json.JSONObject;
 
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
 


import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.irodov.clientapp.DatabaseHandler;
import com.irodov.clientapp.UserFunctions;
 
public class LoginActivity extends FragmentActivity implements TaskFragment.TaskCallbacks {
    Button btnLogin;
    Button btnLinkToRegister;
    EditText inputEmail;
    EditText inputPassword;
    TextView loginErrorMsg;
    Button facebook;
    // JSON Response node names
    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";
    private static String KEY_ERROR_MSG = "error_msg";
    private static String KEY_UID = "uid";
    private static String KEY_NAME = "name";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";
    ProgressDialog pDialogLogin;
    
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final boolean DEBUG = true; // Set this to false to disable logs.

    private static final String KEY_CURRENT_PROGRESS = "current_progress";
    private static final String KEY_PERCENT_PROGRESS = "percent_progress";
    private static final String TAG_TASK_FRAGMENT = "task_fragment";

    private TaskFragment mTaskFragment;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
 
        // Importing all assets like buttons, text fields
        inputEmail = (EditText) findViewById(R.id.loginEmail);
        inputPassword = (EditText) findViewById(R.id.loginPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        loginErrorMsg = (TextView) findViewById(R.id.login_error);
        facebook= (Button)findViewById(R.id.welcome);
        		
        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {
        	
            public void onClick(View view) {
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                UserFunctions userFunction = new UserFunctions();
                JSONObject json = userFunction.loginUser(email, password);      
                //  System.out.println("We reach here");
              //  ProgressDialog pd = new ProgressDialog(LoginActivity.this);
             //   pd.setMessage("loading");
              //  pd.show();
                
             //   System.out.println("We reach here1");
                // check for login response
                try {
                         if (json.getString(KEY_SUCCESS) != null) {
                    	 DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                    	JSONObject 	json_user = json.getJSONObject("user");
                    	loginErrorMsg.setText("");
                        String res = json.getString(KEY_SUCCESS); 
                        if(Integer.parseInt(res) == 1){
                            // user successfully logged in
                            // Store user details in SQLite Database
                             
                            //  Clear all previous data in database
                            userFunction.logoutUser(getApplicationContext());
                            db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json.getString(KEY_UID), json_user.getString(KEY_CREATED_AT));                        
                             MainActivity.Logged=true;
                            // Launch Dashboard Screen
                            Intent dashboard = new Intent(getApplicationContext(), MainActivity.class);
                             
                            // Close all views before launching Dashboard
                            dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(dashboard);
                             
                            // Close Login Screen
                            finish();
                        }else{
                            // Error in login
                            loginErrorMsg.setText("Incorrect username/password");
                        }
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                catch (Exception e){
                	loginErrorMsg.setText("SERVER May not be running");
                	Log.e("Caught","NULL"+e.toString());
                }
                 
            }
        });
 
        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
        facebook.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View view){
    //    		new fblogin().execute();
        		 if (mTaskFragment.isRunning()) {
        	          mTaskFragment.cancel();
        	        } else {
        	          mTaskFragment.start();
        	        }
        	}
        });
	    
        FragmentManager fm = getSupportFragmentManager();
        mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);

        // If the Fragment is non-null, then it is being retained
        // over a configuration change.
        if (mTaskFragment == null) {
          mTaskFragment = new TaskFragment();
          fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
        }
        
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }
    
    /*class fblogin extends AsyncTask<String, String, Boolean> {
    	protected void onPreExecute() {
		//	setContentView(R.layout.loading); 
            pDialogLogin = new ProgressDialog(LoginActivity.this);
            pDialogLogin.setMessage(Html.fromHtml("<b>Search</b><br/>Logging with Fb"));
            pDialogLogin.setIndeterminate(false);
            pDialogLogin.setCancelable(false);
            pDialogLogin.show();
	  }
		protected Boolean doInBackground(String... params) {
			 boolean value1=false;
			Session.openActiveSession(LoginActivity.this, true, new Session.StatusCallback() {
				 
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
	                          TextView welcome = (TextView) findViewById(R.id.welcome);
	                          welcome.setText("Hello " + user.getName() + "!");
	             //             userFunction.logoutUser(getApplicationContext());
	               //             db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json.getString(KEY_UID), json_user.getString(KEY_CREATED_AT));                        
	                             
	                            // Launch Dashboard Screen
	                          	MainActivity.Logged=true;
	                          	
	                          	Intent dashboard = new Intent(getApplicationContext(), MainActivity.class);
	                             
	                            // Close all views before launching Dashboard
	                            dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	                            startActivity(dashboard);
	                             
	                            // Close Login Screen
	                            finish();
	                        }
	                        else{
	                        	loginErrorMsg.setText("Cannot login from FB");
	                        	
	                        }
	                      }
	                    }).executeAsync();
	            	}
	            }
	          });
			return value1;
		}
    
		 protected void onPostExecute() {
			 pDialogLogin.dismiss();
		 }
    }
*/
	@Override
	public void onPreExecute() {
		// TODO Auto-generated method stub
		pDialogLogin = new ProgressDialog(this);
        pDialogLogin.setMessage(Html.fromHtml("<b>Search</b><br/>Logging with Fb"));
      //  pDialogLogin.setIndeterminate(false);
     //   pDialogLogin.setCancelable(false);
        pDialogLogin.show();
	}
	
	@Override
	public void onProgressUpdate(int percent) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onCancelled() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onPostExecute() {
		// TODO Auto-generated method stub
		pDialogLogin.dismiss();
	}
}