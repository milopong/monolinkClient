package com.milopong.monolink.activity.signup;

import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.milopong.monolink.R;
import com.milopong.monolink.utils.MonoURL;
import com.milopong.monolink.utils.Utility;

import android.content.Intent;
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

public class PhoneCheckActivity extends ActionBarActivity implements
		OnClickListener {

	TextView textTitle;
	EditText nameEt, phoneEt;
	Button nextBtn;
	String email, phone, name, password;
	Boolean phoneCheck = false, nameCheck = false;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_check);
		Intent intent = getIntent();
		email = intent.getStringExtra("email");
		password = intent.getStringExtra("password");

		textTitle = (TextView)findViewById(R.id.textTitle);
		textTitle.setText("본인인증");
		nameEt = (EditText) findViewById(R.id.name_et);
		nameEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.length() > 0) {
					nameCheck = true;
					nextCheck();
				} else {
					nameCheck = false;
					nextCheck();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		phoneEt = (EditText) findViewById(R.id.phone_et);
		phoneEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				if (s.length() >= 11) {
					phoneCheck = true;
					nextCheck();
				} else {
					phoneCheck = false;
					nextCheck();

				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		nextBtn = (Button) findViewById(R.id.next_btn);
		nextBtn.setOnClickListener(this);
	}

	public void nextCheck() {
		if (phoneCheck == true && nameCheck == true) {
			nextBtn.setBackgroundColor(Color.parseColor("#9CD6F5"));
		} else {
			nextBtn.setBackgroundColor(Color.parseColor("#6D6E70"));
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.next_btn:
			if (phoneCheck == true && nameCheck == true) {
				phone = phoneEt.getText().toString();
				RequestParams params = new RequestParams();

				if (Utility.isNotNull(phone)) {
					if (Utility.phoneValidate(phone)
							&& Utility.isCorrenctLength(phone, 11, 11)) {

						params.put("phone", phone);
						phoneCheck(params);

					} else {
						Toast.makeText(getApplicationContext(),
								"올바른 폰번호를 입력하세요.", Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(getApplicationContext(), "폰번호를 입력해주세요.",
							Toast.LENGTH_LONG).show();
				}
			}
			break;
		}
	}

	public void phoneCheck(RequestParams params) {

		AsyncHttpClient client = new AsyncHttpClient();
		client.get(MonoURL.SERVER_URL + MonoURL.PHONE_CHECK, params,
				new AsyncHttpResponseHandler() {
					String response = null;
					JSONObject obj = null;

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						try {
							response = new String(arg2, "UTF-8");
							obj = new JSONObject(response);

							String status = null;
							String authKey = null;
							name = nameEt.getText().toString();
							StringTokenizer st = new StringTokenizer(obj
									.getString("status"), "#");

							status = st.nextToken();
							authKey = st.nextToken();

							if (status.equals("success")) {
								Intent intentPhoneCertificationActivity = new Intent(
										PhoneCheckActivity.this,
										PhoneCertificationActivity.class);

								intentPhoneCertificationActivity.putExtra(
										"name", name);
								intentPhoneCertificationActivity.putExtra(
										"email", email);
								intentPhoneCertificationActivity.putExtra(
										"phone", phone);
								intentPhoneCertificationActivity.putExtra(
										"password", password);
								intentPhoneCertificationActivity.putExtra(
										"authKey", authKey);
								startActivity(intentPhoneCertificationActivity);

							} else if (status.equals("duplication")) {
								Toast.makeText(getApplicationContext(),
										"Phone 중복입니다. ", Toast.LENGTH_LONG)
										.show();
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
}
