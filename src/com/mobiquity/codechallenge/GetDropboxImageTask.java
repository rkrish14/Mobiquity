package com.mobiquity.codechallenge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.exception.DropboxException;

public class GetDropboxImageTask extends AsyncTask<Void, Void, Void> {
	
	private String fileName;
	private ProgressDialog dialog;
	private Activity activity;
	private ImageView img;
	private File file;
	
	public GetDropboxImageTask(Activity activity,String fileName, ImageView img) {
		this.fileName=fileName;
		this.setActivity(activity);
		dialog= new ProgressDialog(activity);
		this.img=img;
	}
	@Override
	protected void onPreExecute() {
		dialog.setMessage("Downloading Files");
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
		super.onPreExecute();
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		File storageDir = Environment.getExternalStorageDirectory();
	    file=new File(storageDir, fileName);
		FileOutputStream outputStream;
		try {
			outputStream = new FileOutputStream(file);
			DropboxFileInfo info = SigninActivity.mDBApi.getFile("/Photos/"+fileName, null, outputStream, null);
			Log.d("check", "The file's rev is: " + info.getMetadata().rev);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DropboxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		if(file.exists()){
		    Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
		    img.setImageBitmap(myBitmap);
		}
		if (dialog.isShowing()) {
            dialog.dismiss();
        }
		super.onPostExecute(result);
	}
	public Activity getActivity() {
		return activity;
	}
	public void setActivity(Activity activity) {
		this.activity = activity;
	}
}
