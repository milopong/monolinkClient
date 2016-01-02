package com.milopong.monolink.lbs;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.loopj.android.http.RequestParams;

public class LbsService extends Service implements Runnable {

	private LocationProvideService locationProvideService;

	@Override
	public void onCreate() {
		super.onCreate();
		locationProvideService = new LocationProvideService(this);
		Thread myThread = new Thread(this);
		myThread.start();

	}

	@Override
	public void run() {
		while (true) {
			try {
				RequestParams params = new RequestParams();
				params.put("latitude", locationProvideService.getLatitude());
				params.put("longitude", locationProvideService.getLongitude());
				
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
