package com.mobiquity.codechallenge;

import java.util.ArrayList;

import com.mobiquity.challenge.onrampchallenge.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class PhotoListActivity extends Activity implements AsynchronousTaskRes{
	
	private ListView list;
	private ArrayList<String> fileNames;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_list);
		
		list = (ListView) findViewById(R.id.listView2);
		
		DropboxPhotoListTask apiObj=new DropboxPhotoListTask(this,list);
		apiObj.delegate=this;
		apiObj.execute();
		
	}

	@Override
	public void postResult(ArrayList<String> result) {
		this.fileNames=result;
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, 
                android.R.layout.simple_list_item_1,fileNames );	  	
		list.setAdapter(arrayAdapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent i= new Intent(PhotoListActivity.this,TakePhotoActivity.class);
				i.putExtra("fileName", fileNames.get(position));
				startActivity(i);
			}
		});
		
	}
}
