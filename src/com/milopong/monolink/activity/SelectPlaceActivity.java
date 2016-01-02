package com.milopong.monolink.activity;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.milopong.monolink.R;
import com.milopong.monolink.map.OnFinishSearchListener;
import com.milopong.monolink.map.Searcher;
import com.milopong.monolink.model.MapItem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class SelectPlaceActivity extends ActionBarActivity implements
		OnClickListener, OnEditorActionListener {
	private EditText mEditTextQuery;
	private GoogleMap mMap;
	private MarkerOptions mMarker;
	private MapItem result;
	private Button registBtn;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_select);
		Toolbar toolbar =(Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		
		mEditTextQuery = (EditText) findViewById(R.id.inputSearch); // 검색창
		mEditTextQuery.setOnEditorActionListener(this);

		mMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		LatLng startingPoint = new LatLng(37.498053, 127.027664);

		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingPoint, 16));
		mMarker = new MarkerOptions();
		registBtn = (Button) findViewById(R.id.regist);
		registBtn.setOnClickListener(this);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {

				result = (MapItem) data.getSerializableExtra("result");
				LatLng startingPoint = new LatLng(result.latitude,
						result.longitude);
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
						startingPoint, 16));
				mMarker.position(startingPoint);
				mMarker.title(result.title);
				mMarker.snippet(result.address);
				mMarker.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.map_pin_blue));
				mMap.addMarker(mMarker).showInfoWindow();
			}
		}
	}

	private void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mEditTextQuery.getWindowToken(), 0);
	}

	private void showToast(final String text) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(SelectPlaceActivity.this, text,
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void showResult(List<MapItem> itemList) {
		Intent mapListActivity = new Intent(SelectPlaceActivity.this,
				MapResultActivity.class);
		mapListActivity.putExtra("searchText", mEditTextQuery.getText().toString());
		mapListActivity.putExtra("mapResults", (ArrayList<MapItem>) itemList);
		startActivityForResult(mapListActivity, 1);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.regist:
			if (result == null) {
				Toast.makeText(getApplicationContext(), "장소를 선택해주세요",
						Toast.LENGTH_SHORT).show();
			} else {
				Intent intent = getIntent();
				intent.putExtra("result", result);
				setResult(RESULT_OK, intent);
				finish();
			}
			break;
		}

	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_SEARCH) {
			String query = mEditTextQuery.getText().toString();
			if (query == null || query.length() == 0) {
				showToast("검색어를 입력하세요.");
				return false;
			}

			hideSoftKeyboard();

			// 중심좌표
			double latitude = 37.498053;
			double longitude = 127.027664;

			// 반경
			int radius = 10000;
			int page = 1; // 페이지 번호 (1 ~ 3). 한페이지에 15개
			String apikey = "256e45e0425b967343e3fea7d6aa96e0";

			Searcher searcher = new Searcher();
			searcher.searchKeyword(getApplicationContext(), query, latitude,
					longitude, radius, page, apikey,
					new OnFinishSearchListener() {
						@Override
						public void onSuccess(List<MapItem> itemList) {
							showResult(itemList); // 검색 결과 보여줌
						}

						@Override
						public void onFail() {
							showToast("API_KEY의 제한 트래픽이 초과되었습니다.");
						}
					});
		}
		return false;
	}

}
