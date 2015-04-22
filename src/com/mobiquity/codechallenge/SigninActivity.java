package com.mobiquity.codechallenge;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.mobiquity.challenge.onrampchallenge.R;

public class SigninActivity extends Activity implements OnClickListener{

	final static private String APP_KEY = "2sd4s8nnnakest0";
	final static private String APP_SECRET = "he61v104kj1y3zm";
	
	final static private String ACCESS_KEY_NAME = "AccessKey";
	final static private String ACCESS_SECRET_NAME = "AccessSecret";
	final static private String ACCOUNT_PREFS_NAME="Prefs";
	
	static DropboxAPI<AndroidAuthSession> mDBApi;

	//Activity widgets
	private Button loginButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//Get session after checking if user is already authenticated
		AndroidAuthSession session=buildSession();
		mDBApi = new DropboxAPI<AndroidAuthSession>(session);
		//initializing widgets
		loginButton=(Button)findViewById(R.id.login);
		loginButton.setOnClickListener(this);
	}

	private AndroidAuthSession buildSession() {
		AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session;
		//Get Shared Preferences if already exists
		String[] accessPair=getKeys();
		if(accessPair!=null){
			AccessTokenPair accessToken = new AccessTokenPair(accessPair[0],accessPair[1]);
			session = new AndroidAuthSession(appKeys, accessToken);
		}else{
			session = new AndroidAuthSession(appKeys);
		}
		return session;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
	      case R.id.login:
	    	  //Check if user is connected to internet
	    	  if(isOnline()){
	    		  mDBApi.getSession().startOAuth2Authentication(SigninActivity.this);  
	    	  }else{
	    		  Toast.makeText(this, "No Network Access", Toast.LENGTH_LONG).show();
	    	  }
	        break;
	      }
		
	}

	protected void onResume() {
	    super.onResume();

	    if (mDBApi.getSession().authenticationSuccessful()) {
	        try {
	            // Required to complete auth, sets the access token on the session
	            mDBApi.getSession().finishAuthentication();
	            //Store Access token key and secret
	            storeAuth(mDBApi.getSession());
	            //Launch MenuActivity
	            Intent newIntent= new Intent(this,MainMenuActivity.class);
	            startActivity(newIntent);
	            finish();
	        } catch (IllegalStateException e) {
	            Log.i("DbAuthLog", "Error authenticating", e);
	        }
	    }
	}	

	private void storeAuth(AndroidAuthSession session) {
	    // Store the OAuth 2 access token, if there is one.
	    String oauth2AccessToken = session.getOAuth2AccessToken();
	    if (oauth2AccessToken != null) {
	        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
	        Editor edit = prefs.edit();
	        edit.putString(ACCESS_KEY_NAME, "oauth2:");
	        edit.putString(ACCESS_SECRET_NAME, oauth2AccessToken);
	        edit.commit();
	        return;
	    }
	}
	
	private String[] getKeys() {
	    SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
	    String key = prefs.getString(ACCESS_KEY_NAME, null);
	    String secret = prefs.getString(ACCESS_SECRET_NAME, null);
	    if (key != null && secret != null) {
	        String[] ret = new String[2];
	        ret[0] = key;
	        ret[1] = secret;
	        return ret;
	    } else {
	        return null;
	    }
	}
	
	public boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    return netInfo != null && netInfo.isConnectedOrConnecting();
	}
}
