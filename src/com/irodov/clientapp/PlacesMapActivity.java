package com.irodov.clientapp;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class PlacesMapActivity extends  FragmentActivity{
	PlacesList nearPlaces;
    // Nearest places
 
    // Map view
    //MapView mapView;
    MapFragment mMapFragment;
    // Map overlay items
  //  List<Overlay> mapOverlays; //stuff from v1
    
    private GoogleMap mMap; 
    AddItemizedOverlay itemizedOverlay;
 
//    GeoPoint geoPoint;
    // Map controllers
 //   MapController mc;
     
    double latitude;
    double longitude;
   // OverlayItem overlayitem;
 

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_places);
 
        // Getting intent data
        Intent i = getIntent();
         
        // Users current geo location
        String user_latitude = i.getStringExtra("user_latitude");
        String user_longitude = i.getStringExtra("user_longitude");
         
        // Nearplaces list
        nearPlaces = (PlacesList) i.getSerializableExtra("near_places");
        Log.e("Places","Lati:"+user_latitude);
       /*  mMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map, mMapFragment);
        fragmentTransaction.commit(); */
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        setUpMapIfNeeded();
        //mapView = (MapView) findViewById(R.id.mapView);
        
        //mapView.setBuiltInZoomControls(true);
       // mapOverlays = mapView.getOverlays();
         
        // Geopoint to place on map
       // geoPoint = new GeoPoint((int) (Double.parseDouble(user_latitude) * 1E6),
         //       (int) (Double.parseDouble(user_longitude) * 1E6));
     
        final LatLng Pune = new LatLng( (Double.parseDouble(user_latitude) ), 
        								 (Double.parseDouble(user_longitude)));
        Marker pune = mMap.addMarker(new MarkerOptions()
                                  .position(Pune)
                                  .title("You")
                                 .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Pune,15));
        // Drawable marker icon
//        Drawable drawable_user = this.getResources()
//                .getDrawable(R.drawable.mark_red);
//         
//        itemizedOverlay = new AddItemizedOverlay(drawable_user, this);
//         
//        // Map overlay item
//        overlayitem = new OverlayItem(geoPoint, "Your Location",
//                "That is you!");
// 
//        itemizedOverlay.addOverlay(overlayitem);
//         
//        mapOverlays.add(itemizedOverlay);
//        itemizedOverlay.populateNow();
         
//        // Drawable marker icon
//        Drawable drawable = this.getResources()
//                .getDrawable(R.drawable.mark_blue);
//         
//        itemizedOverlay = new AddItemizedOverlay(drawable, this);
// 
        //mc = mapView.getController();
 
        // These values are used to get map boundary area
        // The area where you can see all the markers on screen
        int minLat = Integer.MAX_VALUE;
        int minLong = Integer.MAX_VALUE;
        int maxLat = Integer.MIN_VALUE;
        int maxLong = Integer.MIN_VALUE;
 
        // check for null in case it is null
        if (nearPlaces.results != null) {
            // loop through all the places
            for (Place place : nearPlaces.results) {
                latitude = place.geometry.location.lat; // latitude
                longitude = place.geometry.location.lng; // longitude
                 
  /*              // Geopoint to place on map
                geoPoint = new GeoPoint((int) (latitude * 1E6),
                        (int) (longitude * 1E6));
  */               
                // Map overlay item
//                overlayitem = new OverlayItem(geoPoint, place.name,
//                        place.vicinity);
// 
//                itemizedOverlay.addOverlay(overlayitem);
//                 
                final LatLng temp = new LatLng( ((latitude) ), 
						 ((longitude)));  //check for int cast
                Marker nearby = mMap.addMarker(new MarkerOptions()
                  .position(temp)
                  .title(place.name)
                  .snippet(place.vicinity)
                 .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                 
                // calculating map boundary area
                minLat  = (int) Math.min( latitude, minLat );
                minLong = (int) Math.min( longitude, minLong);
                maxLat  = (int) Math.max( latitude, maxLat );
                maxLong = (int) Math.max( longitude, maxLong );
            }
//            mapOverlays.add(itemizedOverlay);
             
            // showing all overlay items
//            itemizedOverlay.populateNow();
        }
         
        
        final LatLng pos = new LatLng((maxLat + minLat)/2, (maxLong + minLong)/2 );
      
        /*mMap.setOnCameraChangeListener(new OnCameraChangeListener() {
                public void onCameraChange(CameraPosition arg0) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 13));
                }
            });
        */
        // Adjusting the zoom level so that you can see all the markers on map
 //       mapView.getController().zoomToSpan(Math.abs( minLat - maxLat ), Math.abs( minLong - maxLong ));
         
        // Showing the center of the map
     //   mc.animateTo(new GeoPoint((maxLat + minLat)/2, (maxLong + minLong)/2 ));
   //     mapView.postInvalidate();
 
    }
 
//check here in future   
//	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
//	@SuppressLint("NewApi")
	private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                                .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                // The Map is verified. It is now safe to manipulate the map.

            }
        }
    }
   
   
    
    protected boolean isRouteDisplayed() {
        return false;
    }
 
}