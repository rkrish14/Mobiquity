package com.mobiquity.codechallenge;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.mobiquity.challenge.onrampchallenge.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;


public class TakePhotoActivity extends Activity implements OnClickListener{
	
	private ImageView img;
	private ImageButton mShareButton;
	private String fileName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);
		
		img = (ImageView)findViewById(R.id.img);
		mShareButton=(ImageButton)findViewById(R.id.share);
	
		
		fileName = getIntent().getExtras().getString("fileName","JPG");
		new GetDropboxImageTask(TakePhotoActivity.this, fileName,img).execute();
		
		mShareButton.setOnClickListener(this);
	
			
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.share:
			shareFileOnFacebook(v);
			break;
		
		default:
			break;
		}
		
	}
	
	private void shareFileOnFacebook(View v) {
		File storageDir = Environment.getExternalStorageDirectory();
	    File file=new File(storageDir, fileName);
		Bitmap icon = BitmapFactory.decodeFile(file.getAbsolutePath());;
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("image/jpeg");
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
		try {
		    f.createNewFile();
		    FileOutputStream fo = new FileOutputStream(f);
		    fo.write(bytes.toByteArray());
		} catch (IOException e) {                       
		        e.printStackTrace();
		}
		final String path=""+getResources().getString(R.string.path);
		shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
		PackageManager pm = v.getContext().getPackageManager();
		List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
		for (final ResolveInfo app : activityList) {
		    if ((app.activityInfo.name).contains("facebook")) {
		        final ActivityInfo activity = app.activityInfo;
		        final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
		        shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		        shareIntent.setComponent(name);
		        v.getContext().startActivity(shareIntent);
		        break;
		   }
		}
	}
}
