package com.irodov.clientapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import com.facebook.*;
import com.facebook.model.*;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {
	UserFunctions userFunctions ;
    TextView textview;
    GPSTracker gpsTracker;
    private MenuItem settings;
    private boolean isResumed = false;
    public static boolean Logged;
//	FragmentManager fm = getSupportFragmentManager();
  //  Fragment fragment_setting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    //    fragment_setting=new Fragment();
    	super.onCreate(savedInstanceState);
        userFunctions= new UserFunctions();
       
      //  fragment_setting = fm.findFragmentById(R.id.userSettingsFragment);
      //  FragmentTransaction transaction= fm.beginTransaction();
      //  transaction.hide(fragment_setting);
     //   transaction.commit();
     
        Logged=userFunctions.isUserLoggedIn(getApplicationContext());
        Logged= isLoggedIn();
        if(Logged){
        	setContentView(R.layout.activity_main);
        	gpsTracker  = new GPSTracker(this);
        	// check if GPS enabled
        	
        	Button refresh = (Button)findViewById(R.id.Refresh);
        	Button logout = (Button)findViewById(R.id.Logout);
        	if (gpsTracker.canGetLocation())
        	{
        		setString(gpsTracker);
        	}
	        else
	        {
	            // can't get location
	            // GPS or Network is not enabled
	            // Ask user to enable GPS/network in settings
	            gpsTracker.showSettingsAlert();
	                 
	        }
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
    private void showFragment(Fragment frag, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
//        for (int i = 0; i < fragments.length; i++) {
//            if (i == fragmentIndex) {
//                transaction.show(fragments[i]);
//            } else {
//                transaction.hide(fragments[i]);
//            }
//        }
      //  transaction.show(frag);
        transaction.hide(frag);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
    

}
