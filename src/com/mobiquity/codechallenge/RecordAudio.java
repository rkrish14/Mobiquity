package com.mobiquity.codechallenge;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RecordAudio extends Activity
{
    private static final String LOG_TAG = "AudioRecordTest";
 
    private String mFileName = null;
    private String audioFileName=null;
    private double start_time=0;

    private RecordButton mRecordButton = null;
    private MediaRecorder mRecorder = null;
    private SaveButton mSaveButton = null;
    
    private TextView time;
    

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        }else{
            stopRecording();
        }
    }

    //Handler to update audio recording time
    final Handler handler = new Handler();
    Runnable update_runnable = new Runnable() {
        public void run() {
            updateTextView();
            handler.postDelayed(update_runnable, 1000);
    }

    //Update TextView
	private void updateTextView() {
			double timeElapsed;
			start_time=start_time+0.01d;
			Log.d("timeElapsed", ""+start_time);
			if(start_time>0.60){
				timeElapsed=(((int)(start_time*100)/60))+(((start_time*100)%60)/100);
			}else{
				timeElapsed=start_time;
			}
			time.setText(""+timeElapsed);
		}
    };
    
    private void startRecording() {
    	time.setText("0.00s");
    	start_time=0.00d;
    	
    	handler.postDelayed(update_runnable, 1000);
    	
    	mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    audioFileName = timeStamp + ".3gp";
        mFileName += "/"+audioFileName;
        
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
    	if(mRecorder!=null){
    		mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
    	}
        handler.removeCallbacks(update_runnable);
    }

    class RecordButton extends Button {
        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    setText("Stop recording");
                } else {
                    setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Context ctx) {
            super(ctx);
            setText("Start recording");
            setOnClickListener(clicker);
        }
    }

    class SaveButton extends Button {
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
            	if(mFileName!=null && audioFileName!=null){
            		 new DropboxTask(RecordAudio.this, mFileName, audioFileName).execute();
            		 mFileName=null;
            		 audioFileName=null;
            	}
            	else{
            		Log.d("Error Recording Audio File", "error");
            	}
            }
            
        };

        public SaveButton(Context ctx) {
            super(ctx);
            setText("Save");
            setOnClickListener(clicker);
        }
    }


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        mRecordButton = new RecordButton(this);
        ll.addView(mRecordButton,
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0));
        time=new TextView(this);
        time.setPadding(10, 10, 10, 10);
        ll.addView(time,
        	new LinearLayout.LayoutParams(
        	    ViewGroup.LayoutParams.WRAP_CONTENT,
        	    ViewGroup.LayoutParams.WRAP_CONTENT,
        	    0));
        mSaveButton = new SaveButton(this);
        ll.addView(mSaveButton,
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0));
        setContentView(ll);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    	handler.removeCallbacks(update_runnable);
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    	handler.removeCallbacks(update_runnable);
    }
}