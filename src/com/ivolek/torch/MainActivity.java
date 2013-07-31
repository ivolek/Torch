package com.ivolek.torch;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

public class MainActivity extends Activity 
{

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		final MainActivity me = this;
		Intent service = new Intent(me, FlashlightService.class);
		me.startService(service); 
		setContentView(R.layout.activity_main);
		super.onCreate(savedInstanceState);
		//getWindow().addFlags(PowerManager.PARTIAL_WAKE_LOCK);
		//Scherm aanblijven.
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		WindowManager.LayoutParams layout = getWindow().getAttributes();
		layout.screenBrightness = -1;
		getWindow().setAttributes(layout);
		
		registerReceiver(destroyReceiver, new IntentFilter("destroy"));
	}

	private final BroadcastReceiver destroyReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			lockDevice();
			unregisterReceiver(this);
			finish();   
		}
	};

	@Override
	public void onBackPressed() 
	{
		// TODO Auto-generated method stub;
		super.onBackPressed();		
		try{
			unregisterReceiver(destroyReceiver);}
		catch(Exception e){}
		lockDevice(); 
	}
	
	@Override
	protected void onStop() 
	{
		Log.i("TAG", "KKKKKKKKKKKK");

		super.onStop();
		try{
			unregisterReceiver(destroyReceiver);}
		catch(Exception e){}		
		ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);

		List< ActivityManager.RunningTaskInfo > taskInfo = am.getRunningTasks(1); 
		if (!taskInfo.get(0).topActivity.getPackageName().equals("com.ivolek.torch")){
//			Intent service = new Intent(this, FlashlightService.class);
//			this.stopService(service); 
//			finish();
			lockDevice();
		}
	}

	protected static final int REQUEST_ENABLE = 0;
	DevicePolicyManager devicePolicyManager;

	ComponentName adminComponent;
	public  void lockDevice()
	{
		try{
			unregisterReceiver(destroyReceiver);}
		catch(Exception e){}
		
		Intent service = new Intent(this, FlashlightService.class);
		this.stopService(service); 
		
		adminComponent = new ComponentName(MainActivity.this, Darclass.class);
		devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

		if (!devicePolicyManager.isAdminActive(adminComponent)) {

			Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
			startActivityForResult(intent, REQUEST_ENABLE);
		} else {
			devicePolicyManager.lockNow();
		}
		finish();
	}
}
