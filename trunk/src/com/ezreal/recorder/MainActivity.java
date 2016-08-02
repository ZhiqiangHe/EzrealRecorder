package com.ezreal.recorder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements OnClickListener {

	public static final int STOPPED = 0;
	public static final int RECORDING = 1;
	public static final String FILE_DIR = Environment.getExternalStorageDirectory()+"/_123Audio";

	private Button mBtnRecord, mBtnExit;
	private TextView mTxvResult;
	private RadioGroup mRadioGroup;
	private RadioButton mRadioButton0, mRadioButton1, mRadioButton2, mRadioButton3,
			mRadioButton4, mRadioButton5;
	private ListView mListView;
	private MyAdapter mAdapter;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private boolean isRecording = false;
	private ArrayList<ItemBean> mDataList = new ArrayList<ItemBean>();
	int status = STOPPED;

	// PcmRecorder recorderInstance = null;
	SpeexRecorder recorderInstance = null;
	String fileName = null;
	SpeexPlayer splayer = null;
	private int quality = 4;
	private int radioIndex = 0;
	
	MediaHelper mHelper = null;
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what == 9999){
				stopRecord();
			}
		};
	};
	
	public void onClick(View v) {
		if (v == mBtnRecord) {
			if (!isRecording) {
				startRecord();
			} else {
				stopRecord();
			}
		}else if(v == mBtnExit){
			//退出
			if(recorderInstance != null){
				recorderInstance.setRecording(false);
			}
			System.exit(0);
		}

	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mBtnRecord = (Button) findViewById(R.id.btn_main_start);
		mBtnRecord.setOnClickListener(this);
		mBtnExit = (Button) findViewById(R.id.btn_main_exit);
		mBtnExit.setOnClickListener(this);
		mTxvResult = (TextView) findViewById(R.id.txv_main_result);
		mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		mRadioButton0 = (RadioButton) findViewById(R.id.radio_button_main0);
		mRadioButton0.setOnCheckedChangeListener(mCheckedListener);
		mRadioButton1 = (RadioButton) findViewById(R.id.radio_button_main1);
		mRadioButton1.setOnCheckedChangeListener(mCheckedListener);
		mRadioButton2 = (RadioButton) findViewById(R.id.radio_button_main2);
		mRadioButton2.setOnCheckedChangeListener(mCheckedListener);
		mRadioButton3 = (RadioButton) findViewById(R.id.radio_button_main3);
		mRadioButton3.setOnCheckedChangeListener(mCheckedListener);
		mRadioButton4 = (RadioButton) findViewById(R.id.radio_button_main4);
		mRadioButton4.setOnCheckedChangeListener(mCheckedListener);
		mRadioButton5 = (RadioButton) findViewById(R.id.radio_button_main5);
		mRadioButton5.setOnCheckedChangeListener(mCheckedListener);
		mListView = (ListView) findViewById(R.id.lsv_main_file_list);
		mAdapter = new MyAdapter();
		mListView.setAdapter(mAdapter);
		mHelper = new MediaHelper(handler);
		refreshData();
	}
	
	private void refreshData(){
		File file = new File(FILE_DIR);
		if(!file.exists()){
			file.mkdirs();
			return;
		}
		if(!file.isDirectory()){
			return;
		}
		File[] listFiles = file.listFiles();
		if(listFiles == null || listFiles.length == 0){
			return;
		}
		mDataList.clear();
		for(File f : listFiles){
			ItemBean ib = new ItemBean();
			String name = f.getName();
			ib.name = name;
			ib.size = (int) f.length();
			ib.path = f.getAbsolutePath();
			ib.time = sdf.format(new Date(f.lastModified()));
			ib.compress = (String) name.subSequence(name.length() - 5, name.length() - 4);
			mDataList.add(ib);
		}
		mAdapter.notifyDataSetChanged();
	}
	
	private void startRecord(){
		fileName = FILE_DIR + "/" + System.currentTimeMillis() + "_" + radioIndex;
		if(mRadioButton0.isChecked()){
			//TODO 
			fileName += ".amr";
			try {
				File file = new File(fileName);
				if(!file.exists()){
					file.getParentFile().mkdirs();
					file.createNewFile();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			mHelper.startRecord(fileName);
		}else{
			fileName += ".spx";
			if (recorderInstance == null) {
				recorderInstance = new SpeexRecorder(fileName, quality);
	
				Thread th = new Thread(recorderInstance);
				th.start();
			}
			recorderInstance.setRecording(true);
		}
		mRadioGroup.setClickable(false);
		isRecording = true;
		mBtnRecord.setText("停止录音");
		mBtnRecord.setClickable(false);
		handler.sendEmptyMessageDelayed(9999, 10 * 1000);
	}
	
	private void stopRecord(){
		if(radioIndex == 0){
			mHelper.stopRecord();
		}else{
			recorderInstance.setRecording(false);
			recorderInstance = null;
		}
		isRecording = false;
		mBtnRecord.setText("开始录音");
		mBtnRecord.setClickable(true);
		mRadioGroup.setClickable(true);
		try {
			Thread.sleep(1500);
			refreshData();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private OnCheckedChangeListener mCheckedListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(buttonView == mRadioButton0){
				mRadioButton0.setChecked(isChecked);
				if(isChecked){
					radioIndex = 0;
				}
			}else if(buttonView == mRadioButton1){
				mRadioButton1.setChecked(isChecked);
				if(isChecked){
					quality = 8;
					radioIndex = 1;
				}
			}else if(buttonView == mRadioButton2){
				mRadioButton2.setChecked(isChecked);
				if(isChecked){
					quality = 6;
					radioIndex = 2;
				}
			}else if(buttonView == mRadioButton3){
				mRadioButton3.setChecked(isChecked);
				if(isChecked){
					quality = 4;
					radioIndex = 3;
				}
			}else if(buttonView == mRadioButton4){
				mRadioButton4.setChecked(isChecked);
				if(isChecked){
					quality = 2;
					radioIndex = 4;
				}
			}else if(buttonView == mRadioButton5){
				mRadioButton5.setChecked(isChecked);
				if(isChecked){
					quality = 1;
					radioIndex = 5;
				}
			}
		}
	};
	
	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mDataList.size();
		}

		@Override
		public ItemBean getItem(int position) {
			return mDataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh = null;
			if(convertView == null){
				vh = new ViewHolder();
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_listview, parent, false);
				vh.txvName = (TextView) convertView.findViewById(R.id.txv_item_listview_name);
				vh.txvContent = (TextView) convertView.findViewById(R.id.txv_item_listview_content);
				vh.btnPlay = (Button) convertView.findViewById(R.id.btn_item_listview_play);
				vh.btnDelete = (Button) convertView.findViewById(R.id.btn_item_listview_delete);
				vh.txvTime = (TextView) convertView.findViewById(R.id.txv_item_listview_time);
				vh.txvCompress = (TextView) convertView.findViewById(R.id.txv_item_listview_compress);
				convertView.setTag(vh);
			}else{
				vh = (ViewHolder) convertView.getTag();
			}
			final ItemBean item = getItem(position);
			vh.txvName.setText(item.name);
			vh.txvContent.setText("文件大小:"+ (item.size/1000.0f) + "KB");
			vh.btnPlay.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// play here........
					if(item.name.endsWith(".spx")){
						splayer = new SpeexPlayer(item.path);
						splayer.startPlay();
					}else if(item.name.endsWith(".amr")){
						mHelper.startPlayer(item.path);
					}
				}
			});
			vh.txvTime.setText(item.time);
			vh.txvCompress.setText("等级:"+item.compress);
			return convertView;
		}
		
		
		class ViewHolder{
			TextView txvName, txvContent, txvTime, txvCompress;
			Button btnPlay, btnDelete;
		}
	}
	
	class ItemBean{
		
		String name;//文件名
		int size;//文件大小
		String path;//文件路径
		String time;//日期时间
		String compress;//压缩级别
	}
}