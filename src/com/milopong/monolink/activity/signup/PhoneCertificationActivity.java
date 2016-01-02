package com.milopong.monolink.activity.signup;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.milopong.monolink.R;
import com.milopong.monolink.activity.LoadingActivity;
import com.milopong.monolink.utils.MonoURL;
import com.milopong.monolink.utils.Utility;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PhoneCertificationActivity extends ActionBarActivity implements
		OnClickListener {
	TextView textTitle;
	EditText phoneEt, authKeyEt;
	Button nextBtn;
	String authKey;
	String email, phone, name, password;
	Boolean nextCheck;
	SharedPreferences sharedpreferences;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_certification);
		sharedpreferences = getSharedPreferences(Utility.MyPREFERENCES,
				Context.MODE_PRIVATE);
		Intent intent = getIntent();
		email = intent.getStringExtra("email");
		phone = intent.getStringExtra("phone");
		name = intent.getStringExtra("name");
		password = intent.getStringExtra("password");
		authKey = intent.getStringExtra("authKey");
		textTitle = (TextView)findViewById(R.id.textTitle);
		textTitle.setText("본인인증");
		phoneEt = (EditText) findViewById(R.id.phone_et);
		phoneEt.setText(phone);
		authKeyEt = (EditText) findViewById(R.id.authkey_et);
		authKeyEt.setText(authKey);
		nextBtn = (Button) findViewById(R.id.next_btn);
		nextBtn.setOnClickListener(this);
		
		nextCheck = true;
		nextBtn.setBackgroundColor(Color.parseColor("#9CD6F5"));
		authKeyEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.length() >= 6) {
					nextCheck = true;
					nextBtn.setBackgroundColor(Color.parseColor("#9CD6F5"));
				} else {
					nextCheck = false;
					nextBtn.setBackgroundColor(Color.parseColor("#6D6E70"));
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if (s.length() >= 6) {
					nextCheck = true;
					nextBtn.setBackgroundColor(Color.parseColor("#9CD6F5"));
				} else {
					nextCheck = false;
					nextBtn.setBackgroundColor(Color.parseColor("#6D6E70"));
				}

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		

	}

	public void registMember(RequestParams params) {

		AsyncHttpClient client = new AsyncHttpClient();
		client.get(MonoURL.SERVER_URL + MonoURL.REGIST_MEMBER, params,
				new AsyncHttpResponseHandler() {
					String response = null;
					JSONObject obj = null;

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						try {
							response = new String(arg2, "UTF-8");
							obj = new JSONObject(response);
							String status = null;
							status = obj.getString("status");

							if (status.equals("success")) {
								Intent intentLoadingActivity = new Intent(
										PhoneCertificationActivity.this,
										LoadingActivity.class);

								SharedPreferences.Editor editor = sharedpreferences
										.edit();

								editor.putString(Utility.Name, name);
								editor.putString(Utility.Phone, phone);
								editor.putString(Utility.Email, email);
								editor.commit();
								Toast.makeText(getApplicationContext(),
										"회원가입에 성공했습니다. ", Toast.LENGTH_LONG).show();
								startActivity(intentLoadingActivity);
								finish();

							} else if (obj.getString("status").equals("fail")) {
								Toast.makeText(getApplicationContext(),
										"회원가입 실패 ", Toast.LENGTH_LONG).show();
							}
						}

						catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
					}

				});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.next_btn:
			if (nextCheck == true) {
				if (authKey.equals(authKeyEt.getText().toString())) {
					RequestParams params = new RequestParams();
					params.put("email", email);
					params.put("phone", phone);
					params.put("password", password);
					params.put("name", name);
					registMember(params);
				} else {
					Toast.makeText(getApplicationContext(), "인증번호가 일치하지 않습니다.",
							Toast.LENGTH_LONG).show();
				}
			}
			break;

		/*
		 * case R.id.cancel_btn: Intent intentPhoneCheckActivity = new Intent(
		 * PhoneCertificationActivity.this, PhoneCheckActivity.class);
		 * intentPhoneCheckActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		 * startActivity(intentPhoneCheckActivity); break;
		 */

		}

	}

}
