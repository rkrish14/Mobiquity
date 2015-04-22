package com.mobiquity.codechallenge;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mobiquity.challenge.onrampchallenge.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditTextActivity extends Activity implements OnClickListener{

	private Button mSaveTextButton;
	private EditText mEditor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text_editor);
		
		mSaveTextButton=(Button)findViewById(R.id.textsavebutton);
		mEditor=(EditText)findViewById(R.id.textedit);
		mSaveTextButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.textsavebutton:
			String text=mEditor.getText().toString();
			
			if(text!=null){
				// Create an image file name
			    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			    String fileName =  timeStamp + ".txt";
			    File storageDir = Environment.getExternalStorageDirectory();
			    File file=new File(storageDir, fileName);
			    try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}

			    // Save a file: path for use with ACTION_VIEW intents
			    String mCurrentPath = "" + file.getAbsolutePath();
			    
			    new DropboxTask(this,mCurrentPath, fileName).execute();
			}else{
				Toast.makeText(this, "Please enter some text", Toast.LENGTH_LONG).show();
			}
			break;

		default:
			break;
		}	
	}
}
