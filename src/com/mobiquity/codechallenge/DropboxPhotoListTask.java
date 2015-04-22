package com.mobiquity.codechallenge;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import com.dropbox.client2.DropboxAPI.Entry;

public class DropboxPhotoListTask  extends AsyncTask<Void, Void, ArrayList<String>>{

	//delegate to get back results from DropboxPhotoListTask
	public AsynchronousTaskRes delegate=null;
	private PhotoListActivity photoActivity;
	
	private ProgressDialog dialog;
	private ArrayList<String> filenames;
	
	public DropboxPhotoListTask(Activity activity,ListView list) {
		dialog=new ProgressDialog(activity);
		setPhotoActivity(new PhotoListActivity());
	}

	@Override
	protected void onPreExecute() {
		dialog.setMessage("Downloading Image");
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
		super.onPreExecute();
	}
	
	@Override
	protected ArrayList<String> doInBackground(Void... params) {
		ArrayList<String> result=getPhotoList();
		return result;
	}

	public ArrayList<String> getPhotoList(){	
		try {
			filenames = new ArrayList<String>();
		    Entry dropboxDir = SigninActivity.mDBApi.metadata("/Photos/", 0, null, true, null);
		    if (dropboxDir.isDir) {
		        List<Entry> contents = dropboxDir.contents;
		        if (contents != null) {
		            for (int i = 0; i < contents.size(); i++) {
		                Entry e = contents.get(i);
		                String a = e.fileName();
		                filenames.add(a);
		                Log.d("dropbox", "FileName:" + a);
		            }
		            return filenames;
		        }
		    }
		} catch (Exception ex) {
		    Log.d("dropbox", "ERROR"+ex);
		}
		return null;	
	}
	
	@Override
	protected void onPostExecute(ArrayList<String> result){		
		if(delegate!=null){
            delegate.postResult(result);
        }else{
            Log.e("ApiAccess", "You have not assigned AsyncTaskResponse delegate");
        }
		if (dialog.isShowing()) {
            dialog.dismiss();
        }
		super.onPostExecute(result);
	}

	public PhotoListActivity getPhotoActivity() {
		return photoActivity;
	}

	public void setPhotoActivity(PhotoListActivity photoActivity) {
		this.photoActivity = photoActivity;
	}
}
