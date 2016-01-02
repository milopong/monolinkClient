package com.milopong.monolink.activity.signup;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.milopong.monolink.R;
import com.milopong.monolink.activity.MainActivity;
import com.milopong.monolink.utils.MonoURL;
import com.milopong.monolink.utils.Utility;

public class PrivacyActivity extends ActionBarActivity implements
		OnClickListener {

	String email, phone, password, name, birth, sex;
	EditText nameEt, birthEt;
	Button feMale, male;
	Button nextBtn;
	Boolean nextCheck;
	SharedPreferences sharedpreferences;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_privacy_check);

		nameEt = (EditText) findViewById(R.id.name_et);
		birthEt = (EditText) findViewById(R.id.birth_et);

		feMale = (Button) findViewById(R.id.female_btn);
		feMale.setOnClickListener(this);

		male = (Button) findViewById(R.id.male_btn);
		male.setOnClickListener(this);

		nextBtn = (Button) findViewById(R.id.next_btn);
		nextBtn.setOnClickListener(this);

		Intent intent = getIntent();

		email = intent.getStringExtra("email");
		phone = intent.getStringExtra("phone");
		password = intent.getStringExtra("password");

		Log.d("aa", "email:" + email);
		Log.d("aa", "phone:" + phone);
		Log.d("aa", "password:" + password);

		nextCheck = true;
		sharedpreferences = getSharedPreferences(Utility.MyPREFERENCES,
				Context.MODE_PRIVATE);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.female_btn:
			sex = "female";
			break;

		case R.id.male_btn:
			sex = "male";
			break;

		case R.id.next_btn:
			if (nextCheck == true) {

				name = nameEt.getText().toString();
				birth = birthEt.getText().toString();

				RequestParams params = new RequestParams();

				params.put("email", email);
				params.put("phone", phone);
				params.put("password", password);
				params.put("name", name);
				params.put("birth", birth);
				params.put("sex", sex);

				registMember(params);
			}

			break;

		}

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
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}
						String status = null;
						try {
							// JSON Object
							status = obj.getString("staus");

							if (status.equals("success")) {
								Intent intentMainActivity = new Intent(
										PrivacyActivity.this,
										MainActivity.class);

								SharedPreferences.Editor editor = sharedpreferences
										.edit();

								editor.putString(Utility.Name, name);
								editor.putString(Utility.Phone, phone);
								editor.putString(Utility.Email, email);
								editor.commit();
								startActivity(intentMainActivity);

							} else if (obj.getString("status").equals("fail")) {
								Toast.makeText(getApplicationContext(),
										"회원가입 실패 ", Toast.LENGTH_LONG).show();
							}

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
