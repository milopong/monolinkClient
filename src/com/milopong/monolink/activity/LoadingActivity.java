package com.milopong.monolink.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.milopong.monolink.R;
import com.milopong.monolink.activity.signup.ClauseActivity;
import com.milopong.monolink.gcm.PreferenceUtil;
import com.milopong.monolink.utils.MonoURL;
import com.milopong.monolink.utils.Utility;

public class LoadingActivity extends Activity implements AnimationListener,
		OnClickListener {

	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private static final String SENDER_ID = "60589542645";
	private static final String REQUEST = "login.do";

	private GoogleCloudMessaging _gcm;
	private String _regId;
	private ProgressDialog prgDialog;
	private ImageView imageAnimation;
	private Animation anim;
	private TextView signUp;
	private ImageView faceBookLogin, kakaoLogin, emailLogin, findPassword;

	private String email, password;

	private SharedPreferences sharedpreferences;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		startActivity(new Intent(this,Splash.class));

		if (checkPlayServices()) {
			_gcm = GoogleCloudMessaging.getInstance(this);
			_regId = getRegistrationId();

			if (TextUtils.isEmpty(_regId))
				registerInBackground();
			else
				Log.d("aa", "regid_id exist:" + _regId);
		} else {
			Log.d("aa",
					"MainActivity.java | onCreate |No valid Google Play Services APK found.|");
		}
		// display received msg
		String msg = getIntent().getStringExtra("msg");
		if (!TextUtils.isEmpty(msg))
			Log.d("aa", "msg");

		signUp = (TextView) findViewById(R.id.sign_up);
		signUp.setOnClickListener(this);
		
		findPassword = (ImageView) findViewById(R.id.find_password);
		findPassword.setOnClickListener(this);

		faceBookLogin = (ImageView) findViewById(R.id.facebook_image);

		kakaoLogin = (ImageView) findViewById(R.id.kakao_image);

		emailLogin = (ImageView) findViewById(R.id.email_image);
		emailLogin.setOnClickListener(this);

		signUp.setVisibility(View.INVISIBLE);
		faceBookLogin.setVisibility(View.INVISIBLE);
		kakaoLogin.setVisibility(View.INVISIBLE);
		emailLogin.setVisibility(View.INVISIBLE);
		findPassword.setVisibility(View.INVISIBLE);

		imageAnimation = (ImageView) findViewById(R.id.logo_image);

		anim = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.animation_fade_in);
		anim.setAnimationListener(this);

		imageAnimation.setVisibility(View.VISIBLE);
		imageAnimation.startAnimation(anim);

		sharedpreferences = getSharedPreferences(Utility.MyPREFERENCES,
				Context.MODE_PRIVATE);

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		// display received msg
		String msg = intent.getStringExtra("msg");
		Log.i("MainActivity.java | onNewIntent", "|" + msg + "|");
		if (!TextUtils.isEmpty(msg))
			Log.d("aa", msg);
	}

	private boolean checkPlayServices() {

		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.d("aa",
						"LoadingActivity.java checkPlayservie this device is no supported");
				finish();
			}
			return false;
		}
		return true;

	}

	// registration id를 가져온다.
	private String getRegistrationId() {
		String registrationId = PreferenceUtil
				.instance(getApplicationContext()).regId();
		if (TextUtils.isEmpty(registrationId)) {
			Log.d("aa",
					"MainActivity.java | getRegistrationId |Registration not found.|");
			return "";
		}
		int registeredVersion = PreferenceUtil
				.instance(getApplicationContext()).appVersion();
		int currentVersion = getAppVersion();
		if (registeredVersion != currentVersion) {
			Log.d("aa",
					"MainActivity.java | getRegistrationId |App version changed.|");
			return "";
		}
		return registrationId;
	}

	private int getAppVersion() {
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	// gcm 서버에 접속해서 registration id를 발급받는다.
	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (_gcm == null) {
						_gcm = GoogleCloudMessaging
								.getInstance(getApplicationContext());
					}
					_regId = _gcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + _regId;

					// For this demo: we don't need to send it because the
					// device
					// will send upstream messages to a server that echo back
					// the
					// message using the 'from' address in the message.

					// Persist the regID - no need to register again.
					storeRegistrationId(_regId);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
				}

				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Log.d("aa", "MainActivity.java | onPostExecute |" + msg + "|");
			}
		}.execute(null, null, null);
	}

	// registraion id를 preference에 저장한다.
	private void storeRegistrationId(String regId) {
		int appVersion = getAppVersion();
		Log.d("aa", "MainActivity.java | storeRegistrationId |"
				+ "Saving regId on app version " + appVersion + "|");
		PreferenceUtil.instance(getApplicationContext()).putRedId(regId);
		PreferenceUtil.instance(getApplicationContext()).putAppVersion(
				appVersion);
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if (animation == anim) {

			signUp.setVisibility(View.VISIBLE);
			faceBookLogin.setVisibility(View.VISIBLE);
			kakaoLogin.setVisibility(View.VISIBLE);
			emailLogin.setVisibility(View.VISIBLE);
			findPassword.setVisibility(View.VISIBLE);

		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {

	}

	@Override
	public void onAnimationStart(Animation animation) {
		loginSessionCheck();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.sign_up:
			Intent intentClauseActivity = new Intent(LoadingActivity.this,
					ClauseActivity.class);

			startActivity(intentClauseActivity);
			break;

		case R.id.email_image:
			Context mContext = getApplicationContext();
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(LAYOUT_INFLATER_SERVICE);

			View layout = inflater.inflate(R.layout.popup_login,
					(ViewGroup) findViewById(R.id.layout_root));
			AlertDialog.Builder aDialog = new AlertDialog.Builder(this,
					R.style.MyCustomTheme);
			aDialog.setView(layout);
			final Dialog ad = aDialog.create();
			LayoutParams params = new LayoutParams();
			params.copyFrom(ad.getWindow().getAttributes());
			params.dimAmount=0.5f;
			params.width = LayoutParams.WRAP_CONTENT;
			params.height = LayoutParams.WRAP_CONTENT;

			final EditText emailEt = (EditText) layout
					.findViewById(R.id.emailEt);
			final EditText passwordEt = (EditText) layout
					.findViewById(R.id.passwordEt);
			final Button confirmBtn = (Button) layout
					.findViewById(R.id.confirmBtn);
			confirmBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					email = emailEt.getText().toString();
					password = passwordEt.getText().toString();

					RequestParams params = new RequestParams();
					params.add("email", email);
					params.add("password", password);
					params.add("gcmId", _regId);

					invokeWS(params);
				}
			});
			final Button cancelBtn = (Button) layout
					.findViewById(R.id.cancelBtn);
			cancelBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ad.dismiss();
				}
			});

			ad.show();// 보여줌!
			Window window = ad.getWindow();
			window.setAttributes(params);
			window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			break;
		}

	}

	public void invokeWS(RequestParams params) {
		AsyncHttpClient client = new AsyncHttpClient();

		client.get(MonoURL.SERVER_URL+REQUEST,
				params, new AsyncHttpResponseHandler() {
					String response = null;
					JSONObject obj = null;

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						try {
							response = new String(arg2, "UTF-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						if (response.equals("")) {
							Toast.makeText(getApplicationContext(),
									"해당 Email의 회원이 존재하지 않습니다.",
									Toast.LENGTH_LONG).show();
						} else {
							try {
								obj = new JSONObject(response);
								SharedPreferences.Editor editor = sharedpreferences
										.edit();
								JSONObject member;

								member = obj.getJSONObject("member");
								String name = member.getString("name");
								String photo = member.getString("photo");
								String phone = member.getString("phone");
								String email = member.getString("email");
								String birth = member.getString("birth");
								String sex = member.getString("sex");

								editor.putString(Utility.Name, name);
								editor.putString(Utility.Phone, phone);
								editor.putString(Utility.Email, email);
								editor.putString(Utility.Photo, photo);
								editor.putString(Utility.Birth, birth);
								editor.putString(Utility.Sex, sex);
								editor.commit();

								Intent intentMainActivity = new Intent(
										LoadingActivity.this,
										MainActivity.class);
								startActivity(intentMainActivity);
								finish();

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
					}

				});

	}

	public void loginSessionCheck() {
		String name = sharedpreferences.getString(Utility.Name, "");

		if (!name.equals("")) {

			Intent intentMainActivity = new Intent(LoadingActivity.this,
					MainActivity.class);
			startActivity(intentMainActivity);
			finish();

		}
	}

}
