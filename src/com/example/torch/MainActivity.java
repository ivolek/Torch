package com.example.torch;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.view.WindowManager;

public class MainActivity extends Activity 
{

	Camera camera;
	Boolean afsluiter = true;
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		setContentView(R.layout.activity_main);
		camera = Camera.open();
		Parameters p = camera.getParameters();
		p.setFlashMode(Parameters.FLASH_MODE_TORCH);
		camera.setParameters(p);
		camera.startPreview();
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
				camera.release();
				lockDevice();
				System.exit(0);
			}
		}.start();


	}

	@Override
	public void onBackPressed() 
	{
		// TODO Auto-generated method stub;
		super.onBackPressed();
		camera.release();
		lockDevice();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		camera.release();

		lockDevice();
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
