package com.milopong.monolink.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.milopong.monolink.R;
import com.milopong.monolink.map.WorkaroundMapFragment;
import com.milopong.monolink.model.Member;
import com.milopong.monolink.model.Schedule;
import com.milopong.monolink.ui.de.hdodenhof.circleimageview.CircleImageView;
import com.milopong.monolink.utils.MonoURL;
import com.milopong.monolink.utils.Utility;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ScheduleInfoActivity extends FragmentActivity implements
		OnClickListener {
	private final static String REQUEST = "joinSchedule.do";
	private final static String REQUEST1 = "rejectSchedule.do";
	private final static String REQUEST2 = "joinOpenSchedule.do";

	private SharedPreferences sharedPreferences;

	private Schedule schedule;

	private ScrollView mScrollView;
	private Button shareBtn;
	private Button bookmarkBtn;

	private ImageView photoIv;

	private ArrayList<Member> participants;
	private LinearLayout participantsLinear;
	private String phoneNumber;

	private TextView startDate1Tv, startDate2Tv;
	private TextView endDate1Tv, endDate2Tv;

	private TextView memoTv;

	private TextView placeTv;
	private TextView detailPlaceTv;

	private GoogleMap mMap;
	private MarkerOptions mMarker;

	private DisplayImageOptions options;

	private LinearLayout acceptReject;
	private Button acceptBtn, rejectBtn;
	private Button openAcceptBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_schedule_info);

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs().build();
		ImageLoader.getInstance().init(config);

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_profile)
				.showImageForEmptyUri(R.drawable.default_profile)
				.showImageOnFail(R.drawable.default_profile)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.build();

		sharedPreferences = getSharedPreferences(Utility.MyPREFERENCES,
				Context.MODE_PRIVATE);

		Intent intent = getIntent();
		schedule = new Schedule();
		schedule = (Schedule) intent.getSerializableExtra("schedule");

		shareBtn = (Button) findViewById(R.id.shareBtn);
		bookmarkBtn = (Button) findViewById(R.id.bookmarkBtn);

		photoIv = (ImageView) findViewById(R.id.photoIv);

		participantsLinear = (LinearLayout) findViewById(R.id.layout_schedule_frined);

		startDate1Tv = (TextView) findViewById(R.id.startDate1Tv);
		startDate2Tv = (TextView) findViewById(R.id.startDate2Tv);

		endDate1Tv = (TextView) findViewById(R.id.endDate1Tv);
		endDate2Tv = (TextView) findViewById(R.id.endDate2Tv);

		memoTv = (TextView) findViewById(R.id.memoTv);

		placeTv = (TextView) findViewById(R.id.placeTv);
		detailPlaceTv = (TextView) findViewById(R.id.detailPlaceTv);

		if (mMap == null) {
			mMap = ((WorkaroundMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			mMap.getUiSettings().setZoomControlsEnabled(true);
			mScrollView = (ScrollView) findViewById(R.id.scrollMap);

			((WorkaroundMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map))
					.setListener(new WorkaroundMapFragment.OnTouchListener() {
						@Override
						public void onTouch() {
							mScrollView
									.requestDisallowInterceptTouchEvent(true);
						}
					});

		}

		mMarker = new MarkerOptions();

		acceptReject = (LinearLayout) findViewById(R.id.layout_accept_reject);
		acceptBtn = (Button) findViewById(R.id.acceptBtn);
		rejectBtn = (Button) findViewById(R.id.rejectBtn);
		openAcceptBtn = (Button) this.findViewById(R.id.open_acceptBtn);

		// 공유, 북마크 이벤트 등록
		shareBtn.setOnClickListener(this);
		bookmarkBtn.setOnClickListener(this);

		if (schedule.getPhoto() != null) {
			photoIv.setVisibility(View.VISIBLE);
			String photo = MonoURL.SERVER_URL + "images/schedule/"
					+ schedule.getPhoto();
			ImageLoader.getInstance().displayImage(photo, photoIv, options);
		}

		// 참여자 지정
		participants = new ArrayList<Member>();
		participants = schedule.getParticipants();

		for (int i = 0; i < participants.size(); i++) {

			LinearLayout friendLayout = new LinearLayout(
					ScheduleInfoActivity.this);
			friendLayout.setId(R.id.layout_id);
			friendLayout.setTag(i);
			LayoutParams firiendParams = new LayoutParams(200, 350);
			if (i != 0) {
				firiendParams.setMarginStart(50);
			}
			friendLayout.setLayoutParams(firiendParams);
			friendLayout.setOrientation(LinearLayout.VERTICAL);
			friendLayout.setOnClickListener(this);

			// 친구 프로필 사진
			CircleImageView friendImage = new CircleImageView(
					ScheduleInfoActivity.this);
			LayoutParams imageParams = new LayoutParams(200, 200);
			String profile = MonoURL.SERVER_URL + "images/profile/"
					+ participants.get(i).getPhoto();
			friendImage.setLayoutParams(imageParams);
			ImageLoader.getInstance().displayImage(profile, friendImage,
					options);

			// 친구 이름
			TextView friendText = new TextView(ScheduleInfoActivity.this);
			LayoutParams textParams = new LayoutParams(200, 100);
			textParams.setMargins(40, 10, 0, 0);
			friendText.setLayoutParams(textParams);
			friendText.setTextColor(Color.parseColor("#000000"));
			friendText.setText(participants.get(i).getName());

			friendLayout.addView(friendImage);
			friendLayout.addView(friendText);

			participantsLinear.addView(friendLayout);
		}

		// 시작 시간
		Calendar cal = Calendar.getInstance();
		cal = Utility.StringToCal(schedule.getStartTime());
		int month =cal.get(Calendar.MONTH) + 1 ;
		startDate1Tv.setText(cal.get(Calendar.YEAR) + "."
				+month + "." + cal.get(Calendar.DATE));
		
		String minute = String.valueOf(cal.get(Calendar.MINUTE));
		if (minute.length() == 1) {
			minute = "0" + minute;
		}
		if (cal.get(Calendar.AM_PM) == 0) {
			startDate2Tv.setText("AM   " + cal.get(Calendar.HOUR) + ":"
					+ minute);
		} else if (cal.get(Calendar.AM_PM) == 1) {
			startDate2Tv.setText("PM   " + cal.get(Calendar.HOUR) + ":"
					+ minute);
		}

		// 종료 시간
		cal = Utility.StringToCal(schedule.getEndTime());
		month =cal.get(Calendar.MONTH) + 1 ;
		endDate1Tv.setText(cal.get(Calendar.YEAR) + "."
				+ month+ "." + cal.get(Calendar.DATE));
		
		minute = String.valueOf(cal.get(Calendar.MINUTE));
		if (minute.length() == 1) {
			minute = "0" + minute;
		}
		if (cal.get(Calendar.AM_PM) == 0) {
			endDate2Tv.setText("AM   " + cal.get(Calendar.HOUR) + ":"
					+ minute);
		} else if (cal.get(Calendar.AM_PM) == 1) {
			endDate2Tv.setText("PM   " + cal.get(Calendar.HOUR) + ":"
					+ minute);
		}

		// 메모
		String memo = schedule.getMemo();
		memoTv.setText(memo);

		// 장소
		String place = schedule.getPlace();
		placeTv.setText(place);

		// 상세 장소
		String detailPlace = schedule.getDetailPlace();
		detailPlaceTv.setText(detailPlace);

		// MapView 장소
		Double latitude = Double.parseDouble(schedule.getLatitude());
		Double longitude = Double.parseDouble(schedule.getLongitude());

		LatLng startingPoint = new LatLng(latitude, longitude);
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingPoint, 16));
		mMarker.position(startingPoint);
		mMarker.title(schedule.getPlace());
		mMarker.snippet(schedule.getDetailPlace());
		mMarker.icon(BitmapDescriptorFactory
				.fromResource(R.drawable.map_pin_blue));
		mMap.addMarker(mMarker).showInfoWindow();

		// 참석 거절
		String status = schedule.getStatus();
		Log.d("aa", "status:" + status);

		if (status.equals("guest")) {
			acceptReject.setVisibility(View.VISIBLE);
			acceptBtn.setOnClickListener(this);
			rejectBtn.setOnClickListener(this);
		} else if (status.equals("open") || status.equals("friend")) {
			openAcceptBtn.setVisibility(View.VISIBLE);
			openAcceptBtn.setOnClickListener(this);
		}

	}

	public void joinSchedule(RequestParams params, final String nNo) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(MonoURL.SERVER_URL + REQUEST, params,
				new AsyncHttpResponseHandler() {
					String response = null;
					JSONObject obj = null;

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						try {
							response = new String(arg2, "UTF-8");
							obj = new JSONObject(response);
							if (obj.getInt("status") != 0) {
								Toast.makeText(getApplicationContext(),
										"참석 하셨습니다.", Toast.LENGTH_SHORT).show();
								Intent intent = getIntent();
								intent.putExtra("nNo", nNo);
								setResult(RESULT_OK, intent);
								finish();

							} else {
								Toast.makeText(getApplicationContext(),
										"이미 참가한 일정입니다.", Toast.LENGTH_SHORT)
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

	public void rejectSchedule(RequestParams params, final String nNo) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(MonoURL.SERVER_URL + REQUEST1, params,
				new AsyncHttpResponseHandler() {
					String response = null;
					JSONObject obj = null;

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						try {
							response = new String(arg2, "UTF-8");
							obj = new JSONObject(response);
							if (obj.getString("status").equals("success")) {
								Intent intent = getIntent();
								intent.putExtra("nNo", nNo);
								setResult(RESULT_OK, intent);
								finish();

							} else {
								Toast.makeText(getApplicationContext(), "삭제실패",
										Toast.LENGTH_SHORT).show();
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

	public void joinOpenSchedule(RequestParams params) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(MonoURL.SERVER_URL + REQUEST2, params,
				new AsyncHttpResponseHandler() {
					String response = null;
					JSONObject obj = null;

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						try {
							response = new String(arg2, "UTF-8");
							obj = new JSONObject(response);
							if (obj.getString("status").equals("success")) {
								Intent intent = getIntent();
								setResult(RESULT_OK, intent);
								finish();
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

	@Override
	public void onClick(View v) {

		int no = schedule.getNo();
		String sNo = String.valueOf(no);
		RequestParams params = new RequestParams();
		String email = sharedPreferences.getString(Utility.Email, "");
		String nNo = getIntent().getStringExtra("nNo");
		switch (v.getId()) {

		case R.id.acceptBtn:
			params.add("sNo", sNo);
			params.add("email", email);

			joinSchedule(params, nNo);
			break;

		case R.id.rejectBtn:
			Toast.makeText(getApplicationContext(), "거절 하셨습니다.",
					Toast.LENGTH_SHORT).show();

			params.add("email", email);
			params.add("sNo", sNo);
			params.add("nNo", nNo);
			rejectSchedule(params, nNo);
			break;

		case R.id.open_acceptBtn:
			Toast.makeText(getApplicationContext(), "참석 하셨습니다.",
					Toast.LENGTH_SHORT).show();
			params.add("email", email);
			params.add("sNo", sNo);
			joinOpenSchedule(params);
			break;

		case R.id.layout_id:
			ImageView profileIv;
			TextView nameTv,
			phoneTv;
			Button call,
			message;
			int i = (Integer) v.getTag();

			final AlertDialog alertDialog = new AlertDialog.Builder(this,
					R.style.MyCustomTheme).create();
			final View callLinear = View.inflate(this,
					R.layout.popup_call_friend, null);

			profileIv = (ImageView) callLinear.findViewById(R.id.profile);
			nameTv = (TextView) callLinear.findViewById(R.id.name);
			phoneTv = (TextView) callLinear.findViewById(R.id.phone);

			call = (Button) callLinear.findViewById(R.id.bt_call);
			call.setOnClickListener(new onContactButtonClick());

			message = (Button) callLinear.findViewById(R.id.bt_message);
			message.setOnClickListener(new onContactButtonClick());

			WindowManager.LayoutParams lParams = new WindowManager.LayoutParams();
			lParams.copyFrom(alertDialog.getWindow().getAttributes());
			lParams.dimAmount = 0.5f;
			lParams.width = LayoutParams.WRAP_CONTENT;
			lParams.height = LayoutParams.WRAP_CONTENT;

			String photo = MonoURL.SERVER_URL + "images/"
					+ participants.get(i).getPhoto();
			String name = participants.get(i).getName();
			phoneNumber = participants.get(i).getPhone();

			ImageLoader.getInstance().displayImage(photo, profileIv, options);
			nameTv.setText(name);
			phoneTv.setText(phoneNumber);

			alertDialog.setView(callLinear);
			alertDialog.show();
			Window window = alertDialog.getWindow();
			window.setAttributes(lParams);
			window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

			break;

		}

	}

	class onContactButtonClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_call:
				Intent myIntent = new Intent(Intent.ACTION_CALL,
						Uri.parse("tel:" + phoneNumber));
				startActivity(myIntent);
				break;

			case R.id.bt_message:
				Intent sendIntent = new Intent(Intent.ACTION_VIEW);
				sendIntent.putExtra("address", phoneNumber);
				sendIntent.setType("vnd.android-dir/mms-sms");
				startActivity(sendIntent);
				break;

			}
		}
	}

}
