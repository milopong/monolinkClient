package com.milopong.monolink.lbs;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BroadcastReceiverService extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

			ComponentName cName = new ComponentName(context.getPackageName(),
					LbsService.class.getName());

			
			ComponentName svcName = context.startService(new Intent()
					.setComponent(cName));

			if (svcName == null) {

				Log.d("aa", "실행안됨");

			} else {
				Log.d("aa", "실행됨");
			}

		}

	}
}