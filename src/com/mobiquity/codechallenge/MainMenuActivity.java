package com.mobiquity.codechallenge;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.mobiquity.challenge.onrampchallenge.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainMenuActivity extends Activity{

	//To handle call back to onActivityResult method
	private static final int REQUEST_TAKE_PHOTO = 1;
	
	//To get shared preferences
	final static private String ACCOUNT_PREFS_NAME="Prefs";
	
	//Path and file name of photo taken
	private String mCurrentPhotoPath, imageFileName;
	
	//To get location of photo
	private LocationManager locationManager;
	private LocationListener locationListener;
	private String cityName;
	
	//Activity Widgets
	private ListView list;

	//Arrays to setup ListView using CustomAdapter Class
	private String[] items={ "Take Photo", "View Photos", "Audio Clips","Text Editor"};
    private int[] icons = {R.drawable.clipartcamera,R.drawable.clipartgallery,
    		R.drawable.clipartsound,R.drawable.cliparttext};
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		
		setUpLocationManager();
		
		//Setting up ListView using Custom Adapter
		list = (ListView) findViewById(R.id.listView1);
		CustomAdapter adapter = new CustomAdapter(this, items, icons);
		list.setAdapter(adapter);

		list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	switch( position )
			    {
            	case 0:
            		//User wants to take a photo
            		dispatchTakePictureIntent();
            		break;
            	case 1:
            		//User wants to see list of photos in the folder
            		Intent i=new Intent(MainMenuActivity.this,PhotoListActivity.class);
            		startActivity(i);
            		break;
            	case 2:
            		//User wants to record Audio and save it
            		Intent i1=new Intent(MainMenuActivity.this,RecordAudio.class);
            		startActivity(i1);
            		break;
            	case 3:
            		//User wants to write a text note and save it
            		Intent i2=new Intent(MainMenuActivity.this,EditTextActivity.class);
            		startActivity(i2);
            		break;
			    }
            }
		});
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	      switch (item.getItemId()) {
	      case R.id.logout:
	    	  //User wants to logout
	    	  logOut();
	            return true;
	      }
		return false;
	}
	 
	private void logOut() {
		// Remove credentials from the session
		SigninActivity.mDBApi.getSession().unlink();
		
		// Clear our stored keys
		clearKeys();
		
		//Go back to LoginActivity
		Intent logoutIntent= new Intent(MainMenuActivity.this,SigninActivity.class);
		startActivity(logoutIntent);
		finish();
	}
	private void clearKeys() {
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
	}
	
	private void dispatchTakePictureIntent() {
	    Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	    // Ensure that there's a camera activity to handle the intent
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        // Create the File where the photo should go
	        File photoFile = null;
	        try {
	            photoFile = createImageFile();
	        } catch (IOException ex) {
        		Log.d("error", "check");
	        }
	        // Continue only if the File was successfully created
	        if (photoFile != null) {
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
	                    Uri.fromFile(photoFile));
	            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
	        }
	    }
	}
	
	private File createImageFile() throws IOException {
	    // Create an image file name with time stamp, city name and .jpg extension
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    imageFileName = "JPEG_"+cityName+ "_" + timeStamp + ".jpg";
	    File storageDir = Environment.getExternalStorageDirectory();
	    File file=new File(storageDir, imageFileName);
	    file.createNewFile();

	    // Save file path for use with ACTION_VIEW intents
	    mCurrentPhotoPath = "" + file.getAbsolutePath();
	    return file;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
	    if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) { 
	       	//Calling AsyncTask DropboxTask class to make Http calls using put method
	       	new DropboxTask(this,mCurrentPhotoPath, imageFileName).execute();
	    }  
	 } 
		
	private void setUpLocationManager() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Define a listener that responds to location updates
		locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {
		      // Called when a new location is found by the network location provider.
		      getCurrentCity(location);
		    }

		    public void onStatusChanged(String provider, int status, Bundle extras) {}

		    public void onProviderEnabled(String provider) {}

		    public void onProviderDisabled(String provider) {}
		  };

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0,locationListener );
		Location lastKnownLocation = getLastKnownLocation();
		getCurrentCity(lastKnownLocation);
	}
	
	private Location getLastKnownLocation() {
	    List<String> providers = locationManager.getProviders(true);
	    Location bestLocation = null;
	    for (String provider : providers) {
	        Location l = locationManager.getLastKnownLocation(provider);
	        if (l == null) {
	            continue;
	        }
	        if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
	            bestLocation = l;
	        }
	    }
	    if (bestLocation == null) {
	        return null;
	    }
	    return bestLocation;
	}
	
	private void getCurrentCity(Location location) {
		
		/*------- To get city name from coordinates -------- */
		Geocoder gcd = new Geocoder(this, Locale.getDefault());
	    List<Address> addresses;
	    try {
	        addresses = gcd.getFromLocation(location.getLatitude(),
	            		location.getLongitude(), 1);
	        cityName = addresses.get(0).getLocality();
	        Log.d("address and city name", ""+addresses.get(0)+""+cityName);
	     }catch (IOException e) {
	            e.printStackTrace();
	     }
	}
	
	@Override
	protected void onPause() {
		locationManager.removeUpdates(locationListener);
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		locationManager.removeUpdates(locationListener);
		super.onDestroy();
	}
}

