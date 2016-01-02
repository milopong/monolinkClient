package com.milopong.monolink.activity.signup;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.milopong.monolink.R;
import com.milopong.monolink.utils.BCrypt;
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

public class EmailPassCheckActivity extends ActionBarActivity implements
		OnClickListener {

	private TextView textTitle;
	private EditText emailEt;
	private EditText passwordEt, rePasswordEt;
	private Button nextBtn;

	private Boolean emailCheck = false, passCheck = false;
	String email, phone, bPassword, aPassword, rPassword;
	int passwordLength;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_email_pass_check);

		textTitle = (TextView)findViewById(R.id.textTitle);
		textTitle.setText("계정정보");
		emailEt = (EditText) findViewById(R.id.e_mail_et);
		emailEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.length() >= 1) {
					emailCheck = true;
					nextCheck();
				} else {
					emailCheck = false;
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

		passwordEt = (EditText) findViewById(R.id.password_et);
		passwordEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.length() >= 6) {
					passwordLength = s.length();
				} else {
					passCheck = false;
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

		rePasswordEt = (EditText) findViewById(R.id.repassword_et);
		rePasswordEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				if (passwordLength != 0 && passwordLength == s.length()) {
					passCheck = true;
					nextCheck();
				} else {
					passCheck = false;
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
		if (emailCheck == true && passCheck == true) {
			nextBtn.setBackgroundColor(Color.parseColor("#9CD6F5"));
		} else {
			nextBtn.setBackgroundColor(Color.parseColor("#6D6E70"));
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.next_btn:
			if (emailCheck == true && passCheck == true) {
				bPassword = passwordEt.getText().toString();
				rPassword = rePasswordEt.getText().toString();

				if (bPassword.equals(rPassword)) {
					aPassword = BCrypt.hashpw(bPassword, BCrypt.gensalt(12));

					email = emailEt.getText().toString();
					RequestParams params = new RequestParams();

					if (Utility.isNotNull(email)) {
						if (Utility.emailValidate(email)) {
							params.put("email", email);
							emailCheck(params);
						} else {
							Toast.makeText(getApplicationContext(),
									"E-Mail 형식이 맞지 않습니다.", Toast.LENGTH_LONG)
									.show();
						}
					} else {
						Toast.makeText(getApplicationContext(),
								"E-Mail을 입력해주세요.", Toast.LENGTH_LONG).show();
					}
				}

				else {
					Toast.makeText(getApplicationContext(),
							"비밀번호 재확인이 일치하지 않습니다.", Toast.LENGTH_LONG).show();
				}
				break;

			}
		}

	}

	public void emailCheck(RequestParams params) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(MonoURL.SERVER_URL + MonoURL.EMAIL_CHECK, params,
				new AsyncHttpResponseHandler() {
					String response = null;
					JSONObject obj = null;

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						try {
							response = new String(arg2, "UTF-8");
							obj = new JSONObject(response);

							String status = obj.getString("status");

							if (status.equals("success")) {
								Intent intentPhoeCheckActivity = new Intent(
										EmailPassCheckActivity.this,
										PhoneCheckActivity.class);
								intentPhoeCheckActivity
										.putExtra("email", email);
								intentPhoeCheckActivity
										.putExtra("password", aPassword);
								startActivity(intentPhoeCheckActivity);

							} else if (status.equals("duplication")) {
								Toast.makeText(getApplicationContext(),
										"Email 중복입니다. ", Toast.LENGTH_LONG)
										.show();
							}

						} catch (UnsupportedEncodingException e) {
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
