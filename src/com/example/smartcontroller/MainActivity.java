package com.example.smartcontroller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	private static final String TAG = "Smart Controller";

	private static final boolean D = true;

	private BluetoothAdapter mBluetoothAdapter = null;

	private BluetoothSocket btSocket = null;

	private OutputStream outStream = null;
	private InputStream inStream = null;

	private final BroadcastReceiver mBroadcastReceiver = new MyBroadcastReceiver ();
	
	Button button, buttonConnect;
	ImageButton imageButton;
	ToggleButton toggleButton1;
	ToggleButton toggleButton2;
	ToggleButton toggleButton3;
	ToggleButton toggleButton4;
	EditText editText; 
	Handler handler;

	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static String address = "20:13:11:07:24:55";

	protected static final int RESULT_SPEECH = 1;

	/** Called when the activity is first created. */

 
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		editText = (EditText)findViewById(R.id.editText1);
		editText.setFocusable(false);
		handler = new MyHandler();
		
		
		button=(Button)findViewById(R.id.btn1);
		button.setOnClickListener(new Button.OnClickListener(){
 
			public void onClick(View v) {
				boolean ret = BT_Send("$GetTH@");
				
			}
		});
		button=(Button)findViewById(R.id.btn2);
		button.setOnClickListener(new Button.OnClickListener(){
 
			public void onClick(View v) { 
				boolean ret = BT_Send("$GetLI@");

			}
		}); 
		button=(Button)findViewById(R.id.btn3);
		button.setOnClickListener(new Button.OnClickListener(){
 
			public void onClick(View v) { 
				boolean ret = BT_Send("$GetGI@");

			}
		}); 
		button=(Button)findViewById(R.id.btn4);
		button.setOnClickListener(new Button.OnClickListener(){
 
			public void onClick(View v) { 
				boolean ret = BT_Send("$GetSen@");
			}
		}); 
		buttonConnect=(Button)findViewById(R.id.btnConnect);
		buttonConnect.setOnClickListener(new Button.OnClickListener(){
 
			public void onClick(View v) {
				BT_Connect();
			}
		});

		imageButton=(ImageButton)findViewById(R.id.btnVoice);
		imageButton.setOnClickListener(new Button.OnClickListener(){
 
			public void onClick(View v) {
				StartSpeak();
			}
		}); 
		toggleButton1=(ToggleButton)findViewById(R.id.toggleButton1);
		toggleButton1.setOnClickListener(new OnClickListener(){
 
			public void onClick(View arg0) { 
				boolean ret;
				if (toggleButton1.isChecked()) {
					ret = BT_Send("$L1On@");
					if (ret) {

					}
					else {
						toggleButton1.setChecked(!toggleButton1.isChecked()); 
					}
				}
				else
				{
					ret = BT_Send("$L1Off@");
					if (ret) {

					}
					else {
						toggleButton1.setChecked(!toggleButton1.isChecked()); 
					}
				}
			}

		});

		toggleButton2=(ToggleButton)findViewById(R.id.toggleButton2);
		toggleButton2.setOnClickListener(new OnClickListener(){
 
			public void onClick(View arg0) { 
				boolean ret;
				if (toggleButton2.isChecked()) {
					ret = BT_Send("$L2On@");
					if (ret) {

					}
					else {
						toggleButton2.setChecked(!toggleButton2.isChecked()); 					}
				}
				else
				{
					ret = BT_Send("$L2Off@");
					if (ret) {

					}
					else {
						toggleButton2.setChecked(!toggleButton2.isChecked()); 					}
				}
			}

		});

		toggleButton3=(ToggleButton)findViewById(R.id.toggleButton3);
		toggleButton3.setOnClickListener(new OnClickListener(){
 
			public void onClick(View arg0) { 
				boolean ret;
				if (toggleButton3.isChecked()) {
					ret = BT_Send("$L3On@");
					if (ret) {

					}
					else {
						toggleButton3.setChecked(!toggleButton3.isChecked()); 					}
				}
				else
				{
					ret = BT_Send("$L3Off@");
					if (ret) {

					}
					else {
						toggleButton3.setChecked(!toggleButton3.isChecked()); 					}
				}
			}

		});

		toggleButton4=(ToggleButton)findViewById(R.id.toggleButton4);
		toggleButton4.setOnClickListener(new OnClickListener(){
 
			public void onClick(View arg0) { 
				boolean ret;
				if (toggleButton4.isChecked()) {
					ret = BT_Send("$L4On@");
					if (ret) {

					}
					else {
						toggleButton4.setChecked(!toggleButton4.isChecked()); 					}
				}
				else
				{
					ret = BT_Send("$L4Off@");
					if (ret) {

					}
					else {
						toggleButton4.setChecked(!toggleButton4.isChecked()); 					}
				}
			}

		});

		if (D)
			Log.e(TAG, "+++ ON CREATE +++");
	}
 

	public void onStart() {

		super.onStart();
		if (D) Log.e(TAG, "++ ON START ++");
		
		BT_Connect();
		
	}
 

	public void onResume() {

		super.onResume();
		if (D) Log.e(TAG, "+ ON Resume +");
	}
 
	public void onPause() {

		super.onPause();

		if (D)
			Log.e(TAG, "- ON PAUSE -");

	} 
	public void onStop() {

		super.onStop();

		if (D)Log.e(TAG, "-- ON STOP --");

		try {
			unregisterReceiver(mBroadcastReceiver);
		}
		catch (IllegalArgumentException e){
			
		}
		if (timer != null) {
			timer.cancel();
			timer = null;
		}


		if (outStream != null) {
			try {
				outStream.flush();
			} catch (IOException e) {
				Log.e(TAG, "ON PAUSE: Couldn't flush output stream.", e);
			}

		}

		if (btSocket != null)
		{
			try {
				btSocket.close();
				DisplayToast("蓝牙连接已断开");
			} catch (IOException e2) {

				DisplayToast("套接字关闭失败！");
			}
		}
		

	} 

	public void onDestroy() {

		super.onDestroy();

		if (D) Log.e(TAG, "--- ON DESTROY ---");

	} 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case RESULT_SPEECH: {
			if (resultCode == RESULT_OK && null != data) {

				ArrayList<String> text = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

				editText.setText(text.get(0));
				RecognizeEngine(editText.getText().toString());
			}
			break;
		}

		}
	}

	public class MyBroadcastReceiver extends BroadcastReceiver { 
		public void onReceive(Context context, Intent intent) { 
			if (intent.getAction().equals("android.bluetooth.device.action.ACL_DISCONNECTED")) {
				ConnectionBroken();
			}
				
		}
	}
	
	class MyHandler extends Handler { 
		public void handleMessage(Message msg) {
			TextView textView;
			int GI;
			Log.e("test", "handler");
			String result = msg.getData().getString("message");
			Log.e(TAG, "res :"+result);
			if (result.length()>3) {
				Log.e(TAG, "1");
				if (result.charAt(0)=='$') {
					Log.e(TAG, "2:"+result.substring(1, 3)+";");
					
					if (result.substring(1, 3).equals("TH")) {
						textView = (TextView)findViewById(R.id.textView10);
						textView.setText("" + result.substring(3, 5) + "℃");
						textView = (TextView)findViewById(R.id.textView11);
						textView.setText("" + result.substring(6, 8) + "%");
					}
					
					if (result.substring(1, 3).equals("LI")) {
						textView = (TextView)findViewById(R.id.textView12);
						textView.setText("" + result.substring(3, result.length()-1) + " lx");
					}
					
					if (result.substring(1, 3).equals("GI")) {
						textView = (TextView)findViewById(R.id.textView13);
						GI = Integer.parseInt(result.substring(3, result.length()-1));
						
						textView.setText("" + result.substring(3, result.length()-1) + "%");
						if (GI<15) textView.setTextColor(getResources().getColor(R.color.blue)); else textView.setTextColor(getResources().getColor(R.color.red));
					}
					
					if (result.substring(1, 3).equals("SS")) {
						
						textView = (TextView)findViewById(R.id.textView14);
						if (result.substring(3, 4).equals("1")) {
							textView.setText("开");
							textView.setTextColor(getResources().getColor(R.color.red));
						}
						else{
							textView.setText("关");
							textView.setTextColor(getResources().getColor(R.color.blue));
						}
						
						textView = (TextView)findViewById(R.id.textView15);
						
						if (result.substring(4, 5).equals("1")) {
							textView.setText("开");
							textView.setTextColor(getResources().getColor(R.color.red));
						}
						else{
							textView.setText("关");
							textView.setTextColor(getResources().getColor(R.color.blue));
						}
					}

				}

			}
		}
	}

	public void DisplayToast(String str)
	{
		Toast toast=Toast.makeText(this, str, Toast.LENGTH_LONG);
		toast.show();
	}

	public void BT_Connect() {

		TextView textViewStatus = (TextView)findViewById(R.id.textView1);

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (mBluetoothAdapter == null) { 
			textViewStatus.setText("蓝牙设备不可用，请打开蓝牙！");
			textViewStatus.setTextColor(getResources().getColor(R.color.red));
			return;
		}

		if (!mBluetoothAdapter.isEnabled()) {
			Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivity(intent);
		}

		textViewStatus.setText("正在连接....");
		textViewStatus.setTextColor(getResources().getColor(R.color.black));
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		try {

			btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);

		} catch (IOException e) {

			DisplayToast("套接字创建失败！");
			textViewStatus.setText("套接字创建失败！");
			textViewStatus.setTextColor(getResources().getColor(R.color.red));
		}
		mBluetoothAdapter.cancelDiscovery();

		try {

			btSocket.connect();
			DisplayToast("已连接智能控制器");
			textViewStatus.setText("已连接");
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
			registerReceiver(mBroadcastReceiver, filter); 
			setTimer();
			imageButton.setVisibility(View.VISIBLE);
			buttonConnect.setVisibility(View.INVISIBLE);
			StartSpeak();
			BT_Send("$GetTH@");
			BT_Send("$GetLI@");
			BT_Send("$GetGI@");
			BT_Send("$GetSen@");

		} catch (IOException e) {
			try {
				btSocket.close();

			} catch (IOException e2) {

				DisplayToast("连接没有建立，无法关闭套接字！");
				textViewStatus.setText("连接没有建立，无法关闭套接字！");
			}
			DisplayToast("连接失败");
			textViewStatus.setText("连接失败");
			textViewStatus.setTextColor(getResources().getColor(R.color.red));
		}
	}

	public boolean BT_Send(String message)
	{
		byte[] msgBuffer;
		if (btSocket == null) {
			DisplayToast("请先建立连接");
			return false;
		}
		try {
			outStream = btSocket.getOutputStream();
		} catch (IOException e) {
			Log.e(TAG, "ON RESUME: Output stream creation failed.", e);
			return false;
		}

		msgBuffer = message.getBytes();

		try {

			outStream.write(msgBuffer);

		} catch (IOException e) {

			Log.e(TAG, "ON RESUME: Exception during write.", e);
			ConnectionBroken();
			return false;

		}
		return true;
	}


	Timer timer;
	TimerTask task;
	public void setTimer() {
		task = new TimerTask(){
			int msgBuff, msgBuffc, rec;
			String str;	
			boolean flash = false;
			public void run() {
				msgBuffc = 0;
				try {
					inStream = btSocket.getInputStream();
					msgBuffc = inStream.available();
				} catch (IOException e) {
					Log.e(TAG, "ON RESUME: Input stream creation failed.", e);
				}
				str = "";
				rec = 0;
				for(; msgBuffc > 0 ; msgBuffc --)
				{
					try { 

						msgBuff = inStream.read(); 

					} catch (IOException e) { 
						Log.e(TAG, "IO exc");
						continue;
					} 
					if (msgBuff == (int)'$') rec = 1;
					if (rec == 1) str = str + (char)msgBuff;
					if (msgBuff == (int)'@') {
						rec = 0;
						Log.e(TAG, "message:"+str);
						Message result = handler.obtainMessage();
						Bundle data = new Bundle();
						data.putString("message", str);
						result.setData(data);
						handler.sendMessage(result);
						str = "";
					}
				}

			}
		};
		timer = new Timer();
		timer.schedule(task, 100, 1000); 
	}

	private void RecognizeEngine(String str) {
	
	}

	public void StartSpeak() {

		Intent intent = new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);  
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh");
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "请说指令");

		try {
			startActivityForResult(intent, RESULT_SPEECH);
			editText.setText("");
		} catch (ActivityNotFoundException a) {
			Toast t = Toast.makeText(getApplicationContext(),
					"Ops! Your device doesn't support Speech to Text",
					Toast.LENGTH_SHORT);
			t.show();
		}
	}
	
	private void ConnectionBroken() {
		btSocket = null;
	}

}