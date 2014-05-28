package com.irodov.clientapp;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    TextView textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check if GPS enabled
        final GPSTracker gpsTracker = new GPSTracker(this);
        Button refresh = (Button)findViewById(R.id.Refresh);
    
        if (gpsTracker.canGetLocation())
        {
            String stringLatitude = String.valueOf(gpsTracker.latitude);
            textview = (TextView)findViewById(R.id.fieldLatitude);
            textview.setText(stringLatitude);

            String stringLongitude = String.valueOf(gpsTracker.longitude);
            textview = (TextView)findViewById(R.id.fieldLongitude);
            textview.setText(stringLongitude);

            String country = gpsTracker.getCountryName(this);
            textview = (TextView)findViewById(R.id.fieldCountry);
            textview.setText(country);

            String city = gpsTracker.getLocality(this);
            textview = (TextView)findViewById(R.id.fieldCity);
            textview.setText(city);

            String postalCode = gpsTracker.getPostalCode(this);
            textview = (TextView)findViewById(R.id.fieldPostalCode);
            textview.setText(postalCode);

            String addressLine = gpsTracker.getAddressLine(this);
            textview = (TextView)findViewById(R.id.fieldAddressLine);
            textview.setText(addressLine);
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
			}
		});
	
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.varna_lab_geo_locations, menu);
        return true;
    }

}