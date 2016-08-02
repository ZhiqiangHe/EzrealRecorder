package com.ezreal.recorder;

import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.net.rtp.AudioStream;
import android.os.Handler;
import android.os.Message;

/**
 * 媒体
 * @author Ezreal He
 *
 * @Date 2014年11月11日 上午11:16:24
 */
public class MediaHelper {
	
	private MediaRecorder mRecorder = null;
	private MediaPlayer mPlayer = null;
	private int maxRecTime = 20 * 1000;
	private Handler mHandler = null;
	
	public MediaHelper(Handler handler){
		this.mHandler = handler;
	}
	
	public void startRecord(String filePath){
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		mRecorder.setOutputFile(filePath);
		mRecorder.setMaxDuration(maxRecTime);
		
		mRecorder.setOnErrorListener(new OnErrorListener() {
			@Override
			public void onError(MediaRecorder mr, int what, int extra) {
				mRecorder.reset();
			}
		});

		try {
			mRecorder.prepare();
			mRecorder.start();
//			countTime();
		} catch (Exception e) {
			e.printStackTrace();
			mRecorder.release();
		} 
	}
	
	public void stopRecord(){
		if (mRecorder != null) {
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
		}
		isRun = false;
	}
	
	public void startPlayer(String fileName){
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mPlayer.setDataSource(fileName);
			mPlayer.setOnPreparedListener(new OnPreparedListener() {
				
				@Override
				public void onPrepared(MediaPlayer mp) {
					// TODO Auto-generated method stub
					mp.start();
				}
			});
			mPlayer.prepareAsync();
//			mPlayer.start();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stopPlayer(){
		if(mPlayer != null && mPlayer.isPlaying()){
			mPlayer.stop();
		}
	}
	
	private int times = 0;
	private boolean isRun = true;
	private void countTime(){
		
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				isRun = true;
				times = 0;
				while(isRun){
					try {
						Thread.sleep(100);
						times ++;
						Message msg = Message.obtain(mHandler);
						msg.what = 9999;
						msg.arg1 = times;
						mHandler.sendMessage(msg);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}}).start();
	}
}
