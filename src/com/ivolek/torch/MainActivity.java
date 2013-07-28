package com.ivolek.torch;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.WindowManager;

public class MainActivity extends Activity 
{

	Camera camera;
	Boolean afsluiter = true;
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		final MainActivity me = this;
		Intent service = new Intent(me, FlashlightService.class);
		me.startService(service); 		
		
		setContentView(R.layout.activity_main);
//		camera = Camera.open();
//		Parameters p = camera.getParameters();
//		p.setFlashMode(Parameters.FLASH_MODE_TORCH);
//		camera.setParameters(p);
//		camera.startPreview();
		super.onCreate(savedInstanceState);
		//getWindow().addFlags(PowerManager.PARTIAL_WAKE_LOCK);
		//Scherm aanblijven.
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		WindowManager.LayoutParams layout = getWindow().getAttributes();
		layout.screenBrightness = -1;
		getWindow().setAttributes(layout);
		new Thread()
		{
			public void run()
			{
				try
				{
					Thread.sleep(600000);
				}
				catch(Exception e){}
				lockDevice();
				Intent service = new Intent(me, FlashlightService.class);
				me.stopService(service); 
				System.exit(0);
			}
		}.start();


	}

	@Override
	public void onBackPressed() 
	{
		// TODO Auto-generated method stub;
		super.onBackPressed();

		lockDevice();
		Intent service = new Intent(this, FlashlightService.class);
		this.stopService(service); 
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

//		lockDevice();
		Intent service = new Intent(this, FlashlightService.class);
		this.stopService(service); 
	}   
	
	protected static final int REQUEST_ENABLE = 0;
	DevicePolicyManager devicePolicyManager;
	ComponentName adminComponent;
	public  void lockDevice()
	{
		adminComponent = new ComponentName(MainActivity.this, Darclass.class);
		devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		
         if (!devicePolicyManager.isAdminActive(adminComponent)) {

             Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
             intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
             startActivityForResult(intent, REQUEST_ENABLE);
         } else {
		devicePolicyManager.lockNow();
		}

	}
}
