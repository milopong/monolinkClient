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
import com.milopong.monolink.model.Event;
import com.milopong.monolink.model.Member;
import com.milopong.monolink.ui.de.hdodenhof.circleimageview.CircleImageView;
import com.milopong.monolink.utils.ImageOption;
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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class EventInfoActivity extends ActionBarActivity implements OnClickListener {
	private final static String REQUEST = "joinEvent.do";

	private SharedPreferences sharedPreferences;

	private Event event;

	private ScrollView mScrollView;

	private ImageView photoIv;
	private TextView nameTv;
	private ImageView favoriteIv;

	private TextView tagTv;
	private TextView startDateTv;
	private TextView endDateTv;

	private ArrayList<Member> participants;
	private LinearLayout participantsLinear;
	private String phoneNumber;

	private TextView memoTv;

	private TextView placeTv;
	private TextView detailPlaceTv;

	private GoogleMap mMap;
	private MarkerOptions mMarker;

	private Button joinEventBtn;

	private DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_info);

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
				.threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs().build();
		ImageLoader.getInstance().init(config);

		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_profile)
				.showImageForEmptyUri(R.drawable.default_profile).showImageOnFail(R.drawable.default_profile)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();

		sharedPreferences = getSharedPreferences(Utility.MyPREFERENCES, Context.MODE_PRIVATE);
		
		Toolbar toolbar =(Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		Intent intent = getIntent();
		event = (Event) intent.getSerializableExtra("event");

		photoIv = (ImageView) findViewById(R.id.photoIv);
		nameTv = (TextView) findViewById(R.id.nameTv);

		favoriteIv = (ImageView) findViewById(R.id.favoriteIv);
		favoriteIv.setOnClickListener(this);

		tagTv = (TextView) findViewById(R.id.tagTv);

		startDateTv = (TextView) findViewById(R.id.startDateTv);
		endDateTv = (TextView) findViewById(R.id.endDateTv);

		participantsLinear = (LinearLayout) findViewById(R.id.layout_schedule_frined);

		memoTv = (TextView) findViewById(R.id.memoTv);

		placeTv = (TextView) findViewById(R.id.placeTv);
		detailPlaceTv = (TextView) findViewById(R.id.detailPlaceTv);

		if (mMap == null) {
			mMap = ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			mMap.getUiSettings().setZoomControlsEnabled(true);
			mScrollView = (ScrollView) findViewById(R.id.scrollMap);

			((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
					.setListener(new WorkaroundMapFragment.OnTouchListener() {
						@Override
						public void onTouch() {
							mScrollView.requestDisallowInterceptTouchEvent(true);
						}
					});

		}

		mMarker = new MarkerOptions();

		joinEventBtn = (Button) this.findViewById(R.id.joinEventBtn);

		// 이벤트 이미지 등록
		DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
		Bitmap photo = ImageLoader.getInstance()
				.loadImageSync(MonoURL.SERVER_URL + "images/event/" + event.getPhoto());
		int width = dm.widthPixels;
		int diw = photo.getWidth();
		int dih = photo.getHeight();
		float ratio = (float) diw / dih;

		int height =(int) (width / ratio);
		LinearLayout.LayoutParams layoutParams  = new LinearLayout.LayoutParams(width,height);
		photoIv.setLayoutParams(layoutParams);
		ImageLoader.getInstance().displayImage(
				MonoURL.SERVER_URL + "images/event/" + event.getPhoto(), photoIv,
				ImageOption.options);
				// viewHolder.photoIv.setBackgroundResource(R.color.base);


		// 이름
		nameTv.setText(event.getName());

		// 즐겨찾기 버튼
		if (event.getBookmark().equals("true")) {
			favoriteIv.setBackgroundResource(R.drawable.checked_favorite);
		} else {
			favoriteIv.setBackgroundResource(R.drawable.unchecked_favorite);
		}

		// tag
		tagTv.setText(event.getTag());

		// 시작 시간
		Calendar cal = Calendar.getInstance();
		cal = Utility.StringToCal(event.getStartTime());
		int month = cal.get(Calendar.MONTH) + 1;

		String minute = String.valueOf(cal.get(Calendar.MINUTE));
		if (minute.length() == 1) {
			minute = "0" + minute;
		}
		if (cal.get(Calendar.AM_PM) == 0) {
			startDateTv.setText("AM   " + cal.get(Calendar.HOUR) + ":" + minute);
		} else if (cal.get(Calendar.AM_PM) == 1) {
			startDateTv.setText("PM   " + cal.get(Calendar.HOUR) + ":" + minute);
		}

		// 종료 시간
		cal = Utility.StringToCal(event.getEndTime());
		month = cal.get(Calendar.MONTH) + 1;

		minute = String.valueOf(cal.get(Calendar.MINUTE));
		if (minute.length() == 1) {
			minute = "0" + minute;
		}
		if (cal.get(Calendar.AM_PM) == 0) {
			endDateTv.setText("AM   " + cal.get(Calendar.HOUR) + ":" + minute);
		} else if (cal.get(Calendar.AM_PM) == 1) {
			endDateTv.setText("PM   " + cal.get(Calendar.HOUR) + ":" + minute);
		}

		// 참여자 지정

		participants = new ArrayList<Member>();
		participants = event.getParticipants();

		for (int i = 0; i < participants.size(); i++) {

			LinearLayout friendLayout = new LinearLayout(EventInfoActivity.this);
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
			CircleImageView friendImage = new CircleImageView(EventInfoActivity.this);
			LayoutParams imageParams = new LayoutParams(200, 200);
			String profile = MonoURL.SERVER_URL + "images/profile/" + participants.get(i).getPhoto();
			friendImage.setLayoutParams(imageParams);
			ImageLoader.getInstance().displayImage(profile, friendImage, options);

			// 친구 이름
			TextView friendText = new TextView(EventInfoActivity.this);
			LayoutParams textParams = new LayoutParams(200, 100);
			textParams.setMargins(40, 10, 0, 0);
			friendText.setLayoutParams(textParams);
			friendText.setTextColor(Color.parseColor("#000000"));
			friendText.setText(participants.get(i).getName());

			friendLayout.addView(friendImage);
			friendLayout.addView(friendText);

			participantsLinear.addView(friendLayout);
		}

		// 메모
		String memo = event.getMemo();
		memoTv.setText(memo);

		// 장소
		String place = event.getPlace();
		placeTv.setText(place);

		// 상세 장소
		String detailPlace = event.getDetailPlace();
		detailPlaceTv.setText(detailPlace);

		// MapView 장소
		Double latitude = Double.parseDouble(event.getLatitude());
		Double longitude = Double.parseDouble(event.getLongitude());

		LatLng startingPoint = new LatLng(latitude, longitude);
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingPoint, 16));
		mMarker.position(startingPoint);
		mMarker.title(event.getPlace());
		mMarker.snippet(event.getDetailPlace());
		mMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin_blue));
		mMap.addMarker(mMarker).showInfoWindow();

		joinEventBtn.setOnClickListener(this);
	}

	public void joinOpenSchedule(RequestParams params) {
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
						Intent intent = getIntent();
						setResult(RESULT_OK, intent);
						intent.putExtra("position", -1);
						finish();
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
	
	public void updateFavorite(RequestParams params) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(MonoURL.SERVER_URL + MonoURL.UPDATE_FAVORITE, params, new AsyncHttpResponseHandler() {
			String response = null;
			JSONObject obj = null;

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				try {
					response = new String(arg2, "UTF-8");
					obj = new JSONObject(response);

					if (obj.getString("status").equals("regist")) {
						Toast.makeText(getBaseContext(), "즐겨찾기에 추가 되었습니다.", Toast.LENGTH_SHORT).show();
						favoriteIv.setBackgroundResource(R.drawable.checked_favorite);
					} else if (obj.getString("status").equals("unRegist")) {
						Toast.makeText(getBaseContext(), "즐겨찾기가 삭제 되었습니다.", Toast.LENGTH_SHORT).show();
						favoriteIv.setBackgroundResource(R.drawable.unchecked_favorite);
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

	@Override
	public void onClick(View v) {
		int no = event.getNo();
		String sNo = String.valueOf(no);
		RequestParams params = new RequestParams();
		String email = sharedPreferences.getString(Utility.Email, "");
		Button call, message;
		switch (v.getId()) {
		
		case R.id.favoriteIv:
			params.put("eventNo", event.getNo());
			params.put("email", email);
			if (event.getBookmark().equals("true")) {
				params.add("status", "unregist");
			} else {
				params.add("status", "regist");
			}

			updateFavorite(params);
			break;

		case R.id.joinEventBtn:
			Toast.makeText(getApplicationContext(), "참석 하셨습니다.", Toast.LENGTH_SHORT).show();
			params.add("email", email);
			params.add("sNo", sNo);
			joinOpenSchedule(params);
			break;
		case R.id.layout_id:

			int i = (Integer) v.getTag();
			final LinearLayout callLinear = (LinearLayout) View.inflate(EventInfoActivity.this,
					R.layout.popup_call_friend, null);

			call = (Button) callLinear.findViewById(R.id.bt_call);
			call.setOnClickListener(new onContactButtonClick());

			message = (Button) callLinear.findViewById(R.id.bt_message);
			message.setOnClickListener(new onContactButtonClick());

			AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
			alertbox.setTitle("친구에게 연락하기");
			alertbox.setView(callLinear);
			alertbox.create();
			alertbox.setNegativeButton("취소", null);
			AlertDialog mPopupDlg = alertbox.show();
			phoneNumber = participants.get(i).getPhone();
			break;

		}

	}

	class onContactButtonClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_call:
				Intent myIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
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
