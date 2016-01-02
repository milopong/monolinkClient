package com.milopong.monolink.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.milopong.monolink.R;
import com.milopong.monolink.fragment.EventFragment;
import com.milopong.monolink.fragment.FriendFragment;
import com.milopong.monolink.fragment.HomeFragment;
import com.milopong.monolink.fragment.NotificationFragment;
import com.milopong.monolink.model.Member;
import com.milopong.monolink.model.Notification;
import com.milopong.monolink.model.Schedule;
import com.milopong.monolink.utils.MonoURL;
import com.milopong.monolink.utils.Utility;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
	private Fragment mContent;
	private ArrayList<Notification> notifications = null;
	private ArrayList<Schedule> schedules = null;
	private Toolbar toolbar;
	private ActionBarDrawerToggle dtToggle;
	private DrawerLayout dlDrawer;
	public static SharedPreferences sharedpreferences;

	protected TextView textTitle;
	public EditText searchEt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		sharedpreferences = getSharedPreferences(Utility.MyPREFERENCES, Context.MODE_PRIVATE);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
				.threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs().build();
		ImageLoader.getInstance().init(config);
		textTitle = (TextView) findViewById(R.id.textTitle);
		searchEt = (EditText) findViewById(R.id.inputSearch);

		notifications = new ArrayList<Notification>();
		schedules = new ArrayList<Schedule>();

		RequestParams params = new RequestParams();
		String email = sharedpreferences.getString(Utility.Email, "");
		params.add("email", email);
		selectSchedule(params);
		selectNotification(params);

		// 메인 페이지 설정
		if (savedInstanceState != null) {
			mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
		}

		if (mContent == null) {
			mContent = new HomeFragment(schedules);
			setActionBarContent("home", mContent);
		}
		textTitle.setText(" 내 일정");
		searchEt.setHint("내 일정을 검색하세요");
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		dlDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		dtToggle = new ActionBarDrawerToggle(this, dlDrawer, R.string.app_name, R.string.app_name);
		dlDrawer.setDrawerListener(dtToggle);
		getSupportFragmentManager().beginTransaction().replace(R.id.main_container, mContent).commit();
	}

	public void selectSchedule(RequestParams params) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(MonoURL.SERVER_URL + MonoURL.GET_ALL_SCHEDULE, params, new AsyncHttpResponseHandler() {
			String response = null;
			JSONObject obj = null;

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				try {
					response = new String(arg2, "UTF-8");
					obj = new JSONObject(response);
					JSONArray schedules = obj.getJSONArray("schedules");

					if (schedules.length() != 0) {
						for (int i = 0; i < schedules.length(); i++) {
							Schedule aSchedule = new Schedule();
							Member host = new Member();
							host.setName(schedules.getJSONObject(i).getJSONObject("host").getString("name"));
							host.setPhoto(schedules.getJSONObject(i).getJSONObject("host").getString("photo"));

							JSONArray participants = obj
									.getJSONArray("participants" + schedules.getJSONObject(i).getInt("no"));
							ArrayList<Member> lParticipants = new ArrayList<Member>();

							if (participants.length() != 0) {
								for (int j = 0; j < participants.length(); j++) {
									Member participant = new Member();
									participant.setNo(participants.getJSONObject(j).getInt("no"));
									participant.setName(participants.getJSONObject(j).getString("name"));
									participant.setEmail(participants.getJSONObject(j).getString("email"));
									participant.setPhone(participants.getJSONObject(j).getString("phone"));
									participant.setPhoto(participants.getJSONObject(j).getString("photo"));
									lParticipants.add(participant);
								}
							}

							aSchedule.setNo(schedules.getJSONObject(i).getInt("no"));
							aSchedule.setName(schedules.getJSONObject(i).getString("name"));
							aSchedule.setHost(host);
							aSchedule.setTag(schedules.getJSONObject(i).getString("tag"));
							aSchedule.setStartTime(schedules.getJSONObject(i).getString("startTime"));
							aSchedule.setEndTime(schedules.getJSONObject(i).getString("endTime"));
							aSchedule.setPlace(schedules.getJSONObject(i).getString("place"));
							aSchedule.setDetailPlace(schedules.getJSONObject(i).getString("detailPlace"));
							aSchedule.setLongitude(schedules.getJSONObject(i).getString("longitude"));
							aSchedule.setLatitude(schedules.getJSONObject(i).getString("latitude"));
							aSchedule.setParticipants(lParticipants);
							aSchedule.setMemo(schedules.getJSONObject(i).getString("memo"));
							aSchedule.setStatus(schedules.getJSONObject(i).getString("status"));
							aSchedule.setBookmark(schedules.getJSONObject(i).getString("bookmark"));
							MainActivity.this.schedules.add(aSchedule);
						}

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

	public void selectNotification(RequestParams params) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(MonoURL.SERVER_URL + MonoURL.GET_NOTIFICATION, params, new AsyncHttpResponseHandler() {
			String response = null;
			JSONObject obj = null;

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				try {
					response = new String(arg2, "UTF-8");
					obj = new JSONObject(response);
					JSONArray aNotifications = obj.getJSONArray("notifications");
					for (int i = 0; i < aNotifications.length(); i++) {
						Notification aNotification = new Notification();
						int no = aNotifications.getJSONObject(i).getInt("no");
						String type = aNotifications.getJSONObject(i).getString("type");
						String photo = aNotifications.getJSONObject(i).getJSONObject("member_sender")
								.getString("photo");

						String senderName = aNotifications.getJSONObject(i).getJSONObject("member_sender")
								.getString("name");
						String message = aNotifications.getJSONObject(i).getString("message");
						int scheduleNo = aNotifications.getJSONObject(i).getInt("scheduleNo");
						aNotification.setNo(no);
						aNotification.setType(type);
						aNotification.setPhoto(photo);
						aNotification.setSenderName(senderName);
						aNotification.setMessage(message);
						aNotification.setScheduleNo(scheduleNo);
						notifications.add(aNotification);
					}
					boolean check;
					if (notifications.size() == 0) {
						check = false;
					} else {
						check = true;
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

	public void setActionBarContent(String title, final Fragment fragment) {

		if (title.equals("home")) {
			searchEt.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					String text = searchEt.getText().toString().toLowerCase(Locale.getDefault());

					((HomeFragment) fragment).findText(text);
				}
			});
		}

		else if (title.equals("friendList")) {

			textTitle.setText("  친구");
			searchEt.setHint("연락처/이름 검색");
			searchEt.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					String text = searchEt.getText().toString().toLowerCase(Locale.getDefault());

					((FriendFragment) fragment).findText(text, "friendList");
				}
			});

		} else if (title.equals("shareSchedule")) {
			textTitle.setText("공유일정");
			searchEt.setHint("이름,관심사를 검색하세요.");
			searchEt.addTextChangedListener(new

			TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					String text = searchEt.getText().toString().toLowerCase(Locale.getDefault());
					((FriendFragment) fragment).findText(text, "shareSchedule");

				}

			});
		} else if (title.equals("event")) {
			searchEt.addTextChangedListener(new

			TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					String text = searchEt.getText().toString().toLowerCase(Locale.getDefault());
					((EventFragment) fragment).findText(text);

				}

			});
		}
	}

	public void switchContent(String title) {
		if (title.equals("HomeFragment")) {
			Fragment homeFragment = new HomeFragment(schedules);
			setActionBarContent("home", homeFragment);
			textTitle.setText(" 내 일정");
			searchEt.setHint("내 일정을 검색하세요.");
			getSupportFragmentManager().beginTransaction().replace(R.id.main_container, homeFragment).commit();
		}

		else if (title.equals("FriendFragment")) {
			final FriendFragment friendFragment = new FriendFragment();
			setActionBarContent("shareSchedule", friendFragment);
			getSupportFragmentManager().beginTransaction().replace(R.id.main_container, friendFragment).commit();

		} else if (title.equals("EventFragment")) {
			EventFragment eventFragment = new EventFragment();
			setActionBarContent("event", eventFragment);
			textTitle.setText(" 이벤트");
			searchEt.setHint("이벤트를 검색하세요.");
			getSupportFragmentManager().beginTransaction().replace(R.id.main_container, eventFragment).commit();
		} else if (title.equals("NotificationFragment")) {
			NotificationFragment notificationFragment = new NotificationFragment(notifications);
			setActionBarContent("notification", notificationFragment);
			textTitle.setText("  알림");
			searchEt.setHint("알림을 검색하세요");
			getSupportFragmentManager().beginTransaction().replace(R.id.main_container, notificationFragment).commit();
		} else if (title.equals("SetupActivity")) {
			Intent intentSetup = new Intent(this, SetupActivity.class);
			startActivity(intentSetup);
		}

	}

}
