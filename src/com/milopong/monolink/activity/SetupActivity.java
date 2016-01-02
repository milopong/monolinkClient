package com.milopong.monolink.activity;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.milopong.monolink.R;
import com.milopong.monolink.utils.MonoURL;
import com.milopong.monolink.utils.Utility;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SetupActivity extends ActionBarActivity implements OnClickListener {
	final static String REQUEST = "logout.do";
	private SharedPreferences sharedpreferences;
	private TextView noticeTv;
	private Button logoutBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		sharedpreferences = this.getSharedPreferences(Utility.MyPREFERENCES, Context.MODE_PRIVATE);

		logoutBtn = (Button) findViewById(R.id.logout);
		logoutBtn.setOnClickListener(this);
		noticeTv = (TextView) findViewById(R.id.noticeTv);
		noticeTv.setOnClickListener(this);
	}

	public void logout(RequestParams params) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(MonoURL.SERVER_URL + REQUEST, params, new AsyncHttpResponseHandler() {
			String response = null;
			JSONObject obj = null;

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				try {
					response = new String(arg2, "UTF-8");
					obj = new JSONObject(response);

					if (obj.getString("status").equals("success")) {
						removeAllPreferences();
						Intent intentLoadingActivity = new Intent(SetupActivity.this, LoadingActivity.class);
						startActivity(intentLoadingActivity);
						SetupActivity.this.finish();
					} else {
					}

				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				
			}

		});

	}

	private void removeAllPreferences() {
		SharedPreferences.Editor editor = sharedpreferences.edit();
		editor.clear();
		editor.commit();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		
		case R.id.noticeTv:
			Intent intentNotice = new Intent(this, NoticeActivity.class);
			startActivity(intentNotice);
			break;

		case R.id.logout:
			String email = sharedpreferences.getString(Utility.Email, "");
			RequestParams params = new RequestParams();
			params.add("email", email);
			logout(params);
			break;
		}

	}
}
