package com.mobiquity.codechallenge;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;

public class DropboxTask extends AsyncTask<Void, Void, Void>{

	private static final String AUDIO_DIRECTORY="/AudioFiles/";
	private static final String IMAGE_DIRECTORY="/Photos/";
	private static final String TEXT_NOTES_DIRECTORY="/TextFiles/";
	
	private String mCurrentPhotoPath,fileName;
	private ProgressDialog dialog;
	
	Activity activity;
    
	public DropboxTask(Activity activity,String path, String fileName) {
		dialog=new ProgressDialog(activity);
		this.mCurrentPhotoPath=path;
		this.fileName=fileName;
		this.activity=activity;
        
	}

	@Override
	protected void onPreExecute() {
		dialog.setMessage("Uploading Files");
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        super.onPreExecute();
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		
		uploadFiles();
		return null;
	}

	public void uploadFiles(){
		File file = new File(mCurrentPhotoPath);
    	FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(file);
			if(fileName.contains("3gp")){
				putFileInDirectory(file, inputStream,AUDIO_DIRECTORY);
			}else if(fileName.contains("jpg")){
				putFileInDirectory(file, inputStream,IMAGE_DIRECTORY);
			}else if(fileName.contains("txt")){
				putFileInDirectory(file, inputStream,TEXT_NOTES_DIRECTORY);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DropboxException e) {
			e.printStackTrace();
		}
	}

	private void putFileInDirectory(File file, FileInputStream inputStream,String directory)
			throws DropboxException {
		Entry response = SigninActivity.mDBApi.putFile(directory+""+fileName, inputStream,
				file.length(), null, null);
		Log.i("DbExampleLog", "The uploaded file's rev is: " + response.rev);
	}
	
	
	
	@Override
	protected void onPostExecute(Void result) {
		if (dialog.isShowing()) {
            dialog.dismiss();
        }
		if(fileName.contains("3gp")||fileName.contains("txt")){
			activity.finish();
		}
		super.onPostExecute(result);
	}


}
