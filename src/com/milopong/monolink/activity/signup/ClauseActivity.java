package com.milopong.monolink.activity.signup;

import com.milopong.monolink.R;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class ClauseActivity extends ActionBarActivity implements
		OnCheckedChangeListener, OnClickListener {

	CheckBox serviceCheckBox, privacyCheckBox;
	Button nextBtn;
	TextView textTitle;

	boolean serviceCheck = false;
	boolean privacyCheck = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clause);
		textTitle = (TextView)findViewById(R.id.textTitle);
		textTitle.setText("약관동의");

		serviceCheckBox = (CheckBox) findViewById(R.id.service_checkbox);
		serviceCheckBox.setOnCheckedChangeListener(this);

		privacyCheckBox = (CheckBox) findViewById(R.id.privavcy_checkbox);
		privacyCheckBox.setOnCheckedChangeListener(this);

		nextBtn = (Button) findViewById(R.id.next_btn);
		nextBtn.setOnClickListener(this);

	}

	public Boolean nextCheck() {
		if (serviceCheck == true && privacyCheck == true) {
			nextBtn.setBackgroundColor(Color.parseColor("#9CD6F5"));
			return true;
		} else {
			nextBtn.setBackgroundColor(Color.parseColor("#6D6E70"));
			return false;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.getId() == R.id.service_checkbox) {
			if (isChecked) {
				serviceCheck = true;
			} else {
				serviceCheck = false;
			}
		} else if (buttonView.getId() == R.id.privavcy_checkbox) {
			if (isChecked) {
				privacyCheck = true;

			} else {
				privacyCheck = false;
			}
		}
		nextCheck();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.next_btn:
			if (nextCheck()) {
				Intent intentEmailCheckActivity = new Intent(
						ClauseActivity.this, EmailPassCheckActivity.class);
				startActivity(intentEmailCheckActivity);
				break;
			}

		}

	}

}
