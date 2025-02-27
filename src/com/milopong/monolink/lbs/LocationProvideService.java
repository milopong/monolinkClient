package com.milopong.monolink.lbs;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class LocationProvideService extends Service implements 
		LocationListener {

	private final Context mContext;
	// 현재 GPS 사용유무
	boolean isGPSEnabled = false;
	// 네트워크 사용유무
	boolean isNetworkEnabled = false;
	// GPS 상태값
	boolean isGetLocation = false;
	Location location;
	double lat; // 위도
	double lon; // 경도
	// 최소 GPS 정보 업데이트 거리 10미터
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;

	// 최소 GPS 정보 업데이트 시간 밀리세컨이므로 1분
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
	protected LocationManager locationManager;

	public LocationProvideService(Context context) {
		this.mContext = context;
		getLocation();
	}

	public Location getLocation() {
		try {
			locationManager = (LocationManager)mContext.getSystemService(LOCATION_SERVICE);

			// GPS 정보 가져오기
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// 현재 네트워크 상태 값 알아오기
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				Log.d("aa","herererer??");
				// GPS 와 네트워크사용이 가능하지 않을때 소스 구현
			} else {
				Log.d("aa","GpsEnabled:"+isGPSEnabled);
				Log.d("aa","NetworkEnabled:"+isNetworkEnabled);
				this.isGetLocation = true;
				// 네트워크 정보로 부터 위치값 가져오기
				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						
						if (location != null) {
							// 위도 경도 저장
							lat = location.getLatitude();
							lon = location.getLongitude();
						}
					}
				}

				if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								lat = location.getLatitude();
								lon = location.getLongitude();
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return location;
	}

	// GPS 종료
	public void stopUsingGPS() {
		if (locationManager != null) {
			locationManager.removeUpdates(LocationProvideService.this);
		}
	}

	public double getLatitude() {
		if (location != null) {
			lat = location.getLatitude();
		}
		return lat;
	}

	/**
	 * 경도값을 가져옵니다.
	 * */
	public double getLongitude() {
		if (location != null) {
			lon = location.getLongitude();
		}
		return lon;
	}

	/**
	 * GPS 나 wife 정보가 켜져있는지 확인합니다.
	 * */
	public boolean isGetLocation() {
		return this.isGetLocation;
	}

	/**
	 * GPS 정보를 가져오지 못했을때 설정값으로 갈지 물어보는 alert 창      *
	 */
	/*public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
		alertDialog.setTitle("GPS 사용유무셋팅");
		alertDialog.setMessage("GPS 셋팅이 되지 않았을수도 있습니다. 설정창으로 가시겠습니까?");
		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						mContext.startActivity(intent);

					}
				});
		// Cancle 하면 종료 합니다.
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		alertDialog.show();
	}*/

	

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

}
