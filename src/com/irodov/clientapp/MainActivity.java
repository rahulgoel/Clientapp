package com.irodov.clientapp;


import java.util.ArrayList;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.facebook.Session;

public class MainActivity extends FragmentActivity {
	UserFunctions userFunctions ;
    TextView textview;
    GPSTracker gpsTracker;
    private MenuItem settings;
    private boolean isResumed = false;
    public static boolean Logged;
    // flag for Internet connection status
    boolean isInternetPresent = false;
 
    // Connection detector class
    ConnectionDetector cd;
     
    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();
 
    // Google Places
    GooglePlaces googlePlaces;
 
    // Places List
    PlacesList nearPlaces;
 
    // GPS Location
    GPSTracker gps;
 
    // Button
    Button btnShowOnMap;
 
    // Progress dialog
    ProgressDialog pDialog;
     
    // Places Listview
    ListView lv;
     
    // ListItems data
    ArrayList<HashMap<String, String>> placesListItems = new ArrayList<HashMap<String,String>>();
     
     
    // KEY Strings
    public static String KEY_REFERENCE = "reference"; // id of the place
    public static String KEY_NAME = "name"; // name of the place
    public static String KEY_VICINITY = "vicinity"; // Place area name
 
//	FragmentManager fm = getSupportFragmentManager();
  //  Fragment fragment_setting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {    
    	super.onCreate(savedInstanceState);
        userFunctions= new UserFunctions();
        Log.d("Debug","we are here1");
        cd = new ConnectionDetector(getApplicationContext());
      //  fragment_setting = fm.findFragmentById(R.id.userSettingsFragment);
      //  FragmentTransaction transaction= fm.beginTransaction();
      //  transaction.hide(fragment_setting);
     //   transaction.commit();
     
        Logged=userFunctions.isUserLoggedIn(getApplicationContext());
        Logged= isLoggedIn();
        if(Logged){
        	googlePlaces =new GooglePlaces();
        	Log.d("Debug","we are here2");
        	setContentView(R.layout.activity_main);
        	isInternetPresent = cd.isConnectingToInternet();
        	Log.d("Debug","we are here25");
        	if (!isInternetPresent) {
        		// Internet Connection is not present
        		alert.showAlertDialog(MainActivity.this, "Internet Connection Error",
        				"Please connect to working Internet connection", false);
        		// stop executing code by return
        		return;
        	}
        	Log.d("Debug","we are here23");
        	gpsTracker  = new GPSTracker(this);
        	// check if GPS enabled
        	Log.d("Debug","we are here3");
        	Button refresh = (Button)findViewById(R.id.Refresh);
        	Button logout = (Button)findViewById(R.id.Logout);
        	if (gpsTracker.canGetLocation())
        	{
        		setString(gpsTracker); //set text: see function below
        		Log.d("Your Location", "latitude:" + gpsTracker.getLatitude() + ", longitude: " + gpsTracker.getLongitude());
        	}
	        else
	        {
	            // can't get location
	            // GPS or Network is not enabled
	            // Ask user to enable GPS/network in settings
	            gpsTracker.showSettingsAlert();         
	        }
        	 lv = (ListView) findViewById(R.id.list);
             
             // button show on map
             btnShowOnMap = (Button) findViewById(R.id.btn_show_map);
      
             // calling background Async task to load Google Places
             // After getting places from Google all the data is shown in listview
             new LoadPlaces().execute();
             Log.d("Debug","we are here");
             /** Button click event for shown on map */
             btnShowOnMap.setOnClickListener(new View.OnClickListener() {
             
                 @Override
                 public void onClick(View v) {
                     Intent i = new Intent(getApplicationContext(),
                             PlacesMapActivity.class);
                     // Sending user current geo location
                     i.putExtra("user_latitude", Double.toString(gps.getLatitude()));
                     i.putExtra("user_longitude", Double.toString(gps.getLongitude()));
                      
                     // passing near places to map activity
                     i.putExtra("near_places", nearPlaces);
                     // staring activity
                     startActivity(i);
                 }
             });
        	
	        refresh.setOnClickListener(new View.OnClickListener()  
	        {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					gpsTracker.updateGPSCoordinates();
					setString(gpsTracker);
				}
			});
	        logout.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					userFunctions.logoutUser(getApplicationContext());
                    Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                    login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(login);
                    // Closing dashboard screen
                    finish();	
				}
			});	        	       
	    }
        else{
        	 Intent login = new Intent(getApplicationContext(), LoginActivity.class);
             login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             startActivity(login);
             // Closing dashboard screen
             finish();
        }
        
    }
    @Override
    public void onResume() {
        super.onResume();
        isResumed = true;
    }
    class LoadPlaces extends AsyncTask<String, String, String> {
    	 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage(Html.fromHtml("<b>Search</b><br/>Loading Places..."));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        /**
         * getting Places JSON
         * */
        protected String doInBackground(String... args) {
            // creating Places class object
            //googlePlaces = new GooglePlaces();
             Log.d("Back","are we ever:"+gpsTracker.getLatitude());
             
            try {
                // Separeate your place types by PIPE symbol "|"
                // If you want all types places make it as null
                // Check list of types supported by google
                // 
                String types = "cafe|restaurant"; // Listing places only cafes, restaurants
                 
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
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed Places into LISTVIEW
                     * */
                    // Get json response status
                	String status="";
                	try{
                	 status = nearPlaces.status;
                	}
                	catch(Exception e){
                		Log.e("MainactivityN","NULL:"+e.toString());
                	}
                    // Check for all possible status
                    if(status.equals("OK")){
                        // Successfully got places details
                        if (nearPlaces.results != null) {
                            // loop through each place
                            for (Place p : nearPlaces.results) {
                                HashMap<String, String> map = new HashMap<String, String>();
                                 
                                // Place reference won't display in listview - it will be hidden
                                // Place reference is used to get "place full details"
                                map.put(KEY_REFERENCE, p.reference);
                                 
                                // Place name
                                map.put(KEY_NAME, p.name);
                                 
                                 
                                // adding HashMap to ArrayList
                                placesListItems.add(map);
                            }
                            // list adapter
                            ListAdapter adapter = new SimpleAdapter(MainActivity.this, placesListItems,
                                    R.layout.list_item,
                                    new String[] { KEY_REFERENCE, KEY_NAME}, new int[] {
                                            R.id.reference, R.id.name });
                             
                            // Adding data into listview
                            lv.setAdapter(adapter);
                        }
                    }
                    else if(status.equals("ZERO_RESULTS")){
                        // Zero results found
                        alert.showAlertDialog(MainActivity.this, "Near Places",
                                "Sorry no places found. Try to change the types of places",
                                false);
                    }
                    else if(status.equals("UNKNOWN_ERROR"))
                    {
                        alert.showAlertDialog(MainActivity.this, "Places Error",
                                "Sorry unknown error occured.",
                                false);
                    }
                    else if(status.equals("OVER_QUERY_LIMIT"))
                    {
                        alert.showAlertDialog(MainActivity.this, "Places Error",
                                "Sorry query limit to google places is reached",
                                false);
                    }
                    else if(status.equals("REQUEST_DENIED"))
                    {
                        alert.showAlertDialog(MainActivity.this, "Places Error",
                                "Sorry error occured. Request is denied",
                                false);
                    }
                    else if(status.equals("INVALID_REQUEST"))
                    {
                        alert.showAlertDialog(MainActivity.this, "Places Error",
                                "Sorry error occured. Invalid Request",
                                false);
                    }
                    else
                    {
                        alert.showAlertDialog(MainActivity.this, "Places Error",
                                "Sorry error occured.",
                                false);
                    }
                }
            });

        }   
    }
    @Override
    public void onPause() {
        super.onPause();
        isResumed = false;
    }
    public boolean isLoggedIn() {
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            return true;
        } else {
            return false;
        }
    }
    public void setString(GPSTracker gpsTracker){
    	  String stringLatitude = String.valueOf(gpsTracker.latitude);
          textview = (TextView)findViewById(R.id.fieldLatitude);
          textview.setText("The Latitude is:"+stringLatitude);

          String stringLongitude = String.valueOf(gpsTracker.longitude);
          textview = (TextView)findViewById(R.id.fieldLongitude);
          textview.setText("The Longitude is:"+stringLongitude);

          String country = gpsTracker.getCountryName(this);
          textview = (TextView)findViewById(R.id.fieldCountry);
          textview.setText("The Country is:"+country);

          String city = gpsTracker.getLocality(this);
          textview = (TextView)findViewById(R.id.fieldCity);
          textview.setText("The locality is:"+city);

          String postalCode = gpsTracker.getPostalCode(this);
          textview = (TextView)findViewById(R.id.fieldPostalCode);
          textview.setText("The postal Code is"+postalCode);

          String addressLine = gpsTracker.getAddressLine(this);
          textview = (TextView)findViewById(R.id.fieldAddressLine);
          textview.setText("The address line is:"+addressLine);
    }


    


 

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // only add the menu when the selection fragment is showing
        try{
    //	if (fragment_setting.isVisible()) {
            if (menu.size() == 0) {
                settings = menu.add(R.string.settings);
            }
            return true;
      //  } else {
         //   menu.clear();
        //   settings = null;
     //   }
       }
        catch(Exception e){
        	Log.e("MainActivity","NULL IT SEEMS"+ e.toString());
        }
        return false;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.equals(settings)) {
        	setContentView(R.layout.menu);
         //   showFragment(fragment_setting, true);
            return true;
        }
   //     else showFragment(fragment_setting, false);
        return false;
    }
       

}
