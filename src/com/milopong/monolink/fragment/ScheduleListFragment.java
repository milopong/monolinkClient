package com.milopong.monolink.fragment;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.milopong.monolink.R;
import com.milopong.monolink.activity.MainActivity;
import com.milopong.monolink.activity.ScheduleInfoActivity;
import com.milopong.monolink.model.FriendGroup;
import com.milopong.monolink.model.Member;
import com.milopong.monolink.model.Schedule;
import com.milopong.monolink.model.ScheduleGroup;
import com.milopong.monolink.utils.ImageOption;
import com.milopong.monolink.utils.MonoURL;
import com.milopong.monolink.utils.TimeAscCompare;
import com.milopong.monolink.utils.Utility;
import com.milopong.monolink.utils.Utility.Mode;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ScheduleListFragment extends Fragment {
	private ScheduleAdapter scheduleAdapter;
	private ExpandableListView scheduleListView;
	private ArrayList<ScheduleGroup> expListItems = new ArrayList<ScheduleGroup>();
	private static final String group_names[] = { "오늘", "내일", "3일 후", "일주일 후", "한달 후" };
	private Mode mode = Mode.LIST;
	private ArrayList<ScheduleGroup> arrayList;

	public ScheduleListFragment(Mode mode) {
		this.mode = mode;
		this.arrayList = new ArrayList<ScheduleGroup>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_schedule_list, null);
		return v;

	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initialize(mode);
		scheduleListView = (ExpandableListView) getView().findViewById(R.id.exp_list);

		scheduleAdapter = new ScheduleAdapter(getActivity(), expListItems);
		scheduleListView.setAdapter((BaseExpandableListAdapter) scheduleAdapter);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 1) {
				initialize(mode);
			} else if (requestCode == 2) {
				initialize(mode);
			}
		}
	}

	public void initialize(Mode mode) {
		RequestParams params;
		params = new RequestParams();
		params.add("email", MainActivity.sharedpreferences.getString(Utility.Email, ""));

		expListItems.clear();
		arrayList.clear();
		if (mode == Mode.LIST) {
			selectSchedule(params);
		} else if (mode == Mode.FAVORITE) {
			selectFavoriteSchedule(params);
		}

	}

	public void findSchedule(String text) {
		scheduleAdapter.filter(text);
	}

	public void addList(ArrayList<Schedule> today, ArrayList<Schedule> tomorrow, ArrayList<Schedule> threedays,
			ArrayList<Schedule> week, ArrayList<Schedule> month) {
		for (int i = 0; i < group_names.length; i++) {
			ScheduleGroup scheduleGroup = new ScheduleGroup();
			scheduleGroup.setName(group_names[i]);
			if (group_names[i].equals("오늘")) {
				Collections.sort(today, new TimeAscCompare());
				scheduleGroup.setItems(today);
			} else if (group_names[i].equals("내일")) {
				Collections.sort(tomorrow, new TimeAscCompare());
				scheduleGroup.setItems(tomorrow);
			} else if (group_names[i].equals("3일 후")) {
				Collections.sort(threedays, new TimeAscCompare());
				scheduleGroup.setItems(threedays);
			} else if (group_names[i].equals("일주일 후")) {
				Collections.sort(week, new TimeAscCompare());
				scheduleGroup.setItems(week);
			} else if (group_names[i].equals("한달 후")) {
				Collections.sort(month, new TimeAscCompare());
				scheduleGroup.setItems(month);
			}
			if (scheduleGroup.getItems().size() != 0) {
				expListItems.add(scheduleGroup);
				arrayList.add(scheduleGroup);
			}

		}

		scheduleAdapter.notifyDataSetChanged();
		if (expListItems.size() != 0)
			scheduleListView.expandGroup(0);
	}

	public void selectSchedule(RequestParams params) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(MonoURL.SERVER_URL + MonoURL.GET_TODAY_SCHEDULE, params, new AsyncHttpResponseHandler() {
			String response = null;
			JSONObject obj = null;

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

				ArrayList<Schedule> today = new ArrayList<Schedule>();
				ArrayList<Schedule> tomorrow = new ArrayList<Schedule>();
				ArrayList<Schedule> threedays = new ArrayList<Schedule>();
				ArrayList<Schedule> week = new ArrayList<Schedule>();
				ArrayList<Schedule> month = new ArrayList<Schedule>();

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

							Date date = new Date();
							Calendar cal = Calendar.getInstance();
							cal.setTime(date);
							cal.set(Calendar.HOUR, 0);
							cal.set(Calendar.MINUTE, 0);
							cal.set(Calendar.SECOND, 0);
							cal.set(Calendar.HOUR_OF_DAY, 0);

							Calendar cal1 = Calendar.getInstance();
							cal1 = Utility.StringToCal(aSchedule.getStartTime());

							long d1 = cal.getTime().getTime();
							long d2 = cal1.getTime().getTime();

							int days = (int) ((d2 - d1) / (1000 * 60 * 60 * 24));

							if (days == 0) {
								aSchedule.setType("오늘");
								today.add(aSchedule);
							} else if (days == 1) {
								aSchedule.setType("내일");
								tomorrow.add(aSchedule);
							} else if (days > 1 && days < 7) {
								aSchedule.setType("3일후");
								threedays.add(aSchedule);
							} else if (days > 6 && days < 30) {
								aSchedule.setType("일주일후");
								week.add(aSchedule);
							} else {
								aSchedule.setType("한달후");
								month.add(aSchedule);
							}
						}
						addList(today, tomorrow, threedays, week, month);
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

	public void selectFavoriteSchedule(RequestParams params) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(MonoURL.SERVER_URL + MonoURL.GET_FAVORITE_SCHEDULE, params, new AsyncHttpResponseHandler() {
			String response = null;
			JSONObject obj = null;

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

				ArrayList<Schedule> today = new ArrayList<Schedule>();
				ArrayList<Schedule> tomorrow = new ArrayList<Schedule>();
				ArrayList<Schedule> threedays = new ArrayList<Schedule>();
				ArrayList<Schedule> week = new ArrayList<Schedule>();
				ArrayList<Schedule> month = new ArrayList<Schedule>();

				try {
					response = new String(arg2, "UTF-8");
					obj = new JSONObject(response);
					JSONArray schedules = obj.getJSONArray("schedules");
					JSONArray events = obj.getJSONArray("events");
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
									participant
											.setNo(participants.getJSONObject(j).getJSONObject("member").getInt("no"));
									participant.setName(
											participants.getJSONObject(j).getJSONObject("member").getString("name"));
									participant.setEmail(
											participants.getJSONObject(j).getJSONObject("member").getString("email"));
									participant.setPhone(
											participants.getJSONObject(j).getJSONObject("member").getString("phone"));
									participant.setPhoto(
											participants.getJSONObject(j).getJSONObject("member").getString("photo"));
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
							aSchedule.setBookmark("true");

							Date date = new Date();
							Calendar cal = Calendar.getInstance();
							cal.setTime(date);
							cal.set(Calendar.HOUR, 0);
							cal.set(Calendar.MINUTE, 0);
							cal.set(Calendar.SECOND, 0);
							cal.set(Calendar.HOUR_OF_DAY, 0);

							Calendar cal1 = Calendar.getInstance();
							cal1 = Utility.StringToCal(aSchedule.getStartTime());

							long d1 = cal.getTime().getTime();
							long d2 = cal1.getTime().getTime();

							int days = (int) ((d2 - d1) / (1000 * 60 * 60 * 24));

							if (days == 0) {
								aSchedule.setType("오늘");
								today.add(aSchedule);
							} else if (days == 1) {
								aSchedule.setType("내일");
								tomorrow.add(aSchedule);
							} else if (days > 1 && days < 7) {
								aSchedule.setType("3일후");
								threedays.add(aSchedule);
							} else if (days > 6 && days < 30) {
								aSchedule.setType("일주일후");
								week.add(aSchedule);
							} else {
								aSchedule.setType("한달후");
								month.add(aSchedule);
							}
						}
					}

					if (events.length() != 0) {
						for (int i = 0; i < events.length(); i++) {
							Schedule aSchedule = new Schedule();

							Member host = new Member();
							host.setName(events.getJSONObject(i).getString("company"));

							JSONArray participants = obj
									.getJSONArray("participants" + events.getJSONObject(i).getInt("no"));
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

							aSchedule.setNo(events.getJSONObject(i).getInt("no"));
							aSchedule.setName(events.getJSONObject(i).getString("name"));
							aSchedule.setHost(host);
							aSchedule.setTag(events.getJSONObject(i).getString("tag"));
							aSchedule.setStartTime(events.getJSONObject(i).getString("startTime"));
							aSchedule.setEndTime(events.getJSONObject(i).getString("endTime"));
							aSchedule.setPlace(events.getJSONObject(i).getString("place"));
							aSchedule.setDetailPlace(events.getJSONObject(i).getString("detailPlace"));
							aSchedule.setLongitude(events.getJSONObject(i).getString("longitude"));
							aSchedule.setLatitude(events.getJSONObject(i).getString("latitude"));
							aSchedule.setParticipants(lParticipants);
							aSchedule.setMemo(events.getJSONObject(i).getString("memo"));
							aSchedule.setStatus("event");
							aSchedule.setBookmark("true");

							Date date = new Date();
							Calendar cal = Calendar.getInstance();
							cal.setTime(date);
							cal.set(Calendar.HOUR, 0);
							cal.set(Calendar.MINUTE, 0);
							cal.set(Calendar.SECOND, 0);
							cal.set(Calendar.HOUR_OF_DAY, 0);

							Calendar cal1 = Calendar.getInstance();
							cal1 = Utility.StringToCal(aSchedule.getStartTime());

							long d1 = cal.getTime().getTime();
							long d2 = cal1.getTime().getTime();

							int days = (int) ((d2 - d1) / (1000 * 60 * 60 * 24));

							if (days == 0) {
								aSchedule.setType("오늘");
								today.add(aSchedule);
							} else if (days == 1) {
								aSchedule.setType("내일");
								tomorrow.add(aSchedule);
							} else if (days > 1 && days < 7) {
								aSchedule.setType("3일후");
								threedays.add(aSchedule);
							} else if (days > 6 && days < 30) {
								aSchedule.setType("일주일후");
								week.add(aSchedule);
							} else {
								aSchedule.setType("한달후");
								month.add(aSchedule);
							}
						}
					}

					addList(today, tomorrow, threedays, week, month);

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

	public void deleteHostSchedule(RequestParams params) {
		AsyncHttpClient client = new AsyncHttpClient();

		client.get(MonoURL.SERVER_URL + MonoURL.DELETE_HOST_SCHEDULE, params, new AsyncHttpResponseHandler() {
			String response = null;
			JSONObject obj = null;

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				try {
					response = new String(arg2, "UTF-8");
					obj = new JSONObject(response);

					if (obj.getString("status").equals("success")) {
						initialize(mode);
						Toast.makeText(getActivity(), "삭제성공", Toast.LENGTH_SHORT).show();
						Intent intent_0 = new Intent(getActivity(), MainActivity.class);
						intent_0.putExtra("menu", 0);
						startActivity(intent_0);
						getActivity().finish();

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
						Toast.makeText(getActivity(), "즐겨찾기에 추가 되었습니다.", Toast.LENGTH_SHORT).show();
						initialize(mode);
					} else if (obj.getString("status").equals("unRegist")) {
						Toast.makeText(getActivity(), "즐겨찾기가 삭제 되었습니다.", Toast.LENGTH_SHORT).show();
						initialize(mode);
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

	public void showSchedule(Schedule schedule) {
		Intent intentScheduleInfoActivity = new Intent(getActivity(), ScheduleInfoActivity.class);
		Schedule aSchedule = new Schedule();
		aSchedule = schedule;
		intentScheduleInfoActivity.putExtra("schedule", aSchedule);
		startActivityForResult(intentScheduleInfoActivity, 2);
	}

	public class ScheduleAdapter extends BaseExpandableListAdapter {
		private Context context;
		private ArrayList<ScheduleGroup> groups;

		public ScheduleAdapter(Context context, ArrayList<ScheduleGroup> groups) {
			this.context = context;
			this.groups = groups;
		}

		public void filter(String charText) {
			charText = charText.toLowerCase(Locale.getDefault());
			if (charText.length() == 0) {
				groups.clear();
				groups.addAll(arrayList);
				notifyDataSetChanged();
				scheduleListView.expandGroup(0);

			} else {
				ScheduleGroup scheduleGroup = new ScheduleGroup();
				scheduleGroup.setName("검색 결과");

				ArrayList<Schedule> results = new ArrayList<Schedule>();
				for (int i = 0; i < arrayList.size(); i++) {
					for (Schedule aSchedule : arrayList.get(i).getItems()) {
						if (aSchedule.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
							results.add(aSchedule);
						}
					}
				}
				scheduleGroup.setItems(results);
				groups.clear();
				notifyDataSetChanged();
				groups.add(scheduleGroup);
				scheduleListView.expandGroup(0);
				notifyDataSetChanged();

			}
			notifyDataSetChanged();
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			ArrayList<Schedule> chList = groups.get(groupPosition).getItems();
			return chList.get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {

			final Schedule child = (Schedule) getChild(groupPosition, childPosition);

			if (convertView == null) {
				LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = mInflater.inflate(R.layout.layout_schedule_list, null);
			}

			ImageView photoIv = (ImageView) convertView.findViewById(R.id.photoIv);
			TextView participantsTv = (TextView) convertView.findViewById(R.id.participants);
			TextView nameTv = (TextView) convertView.findViewById(R.id.name);
			TextView remainTimeTv = (TextView) convertView.findViewById(R.id.time1);
			TextView timeTv = (TextView) convertView.findViewById(R.id.time);
			ImageView favoriteIv = (ImageView) convertView.findViewById(R.id.favorite);

			/* 참여자 사진 */
			String profile = null;
			if (child.getParticipants().size() != 0) {
				profile = MonoURL.SERVER_URL + "images/profile/" + child.getParticipants().get(0).getPhoto();
			}
			ImageLoader.getInstance().displayImage(profile, photoIv, ImageOption.options);

			/* 참석인원 */
			String participants = null;
			for (int i = 0; i < child.getParticipants().size(); i++) {

				if (i == 0) {
					participants = child.getParticipants().get(i).getName();
				} else if (i == 1) {
					participants += ", " + child.getParticipants().get(i).getName();
				} else {
					int num = child.getParticipants().size() - 2;
					participants += " 외 " + num + "명";
					break;
				}
			}
			participantsTv.setText(participants);

			/* 일정 이름 */
			nameTv.setText(child.getName());

			/* 잔여 시간 */
			if (getGroup(0).getName().equals("오늘") && getGroup(0).getItems().get(0).getNo() == child.getNo()) {
				// 현재시간
				Calendar cal = Calendar.getInstance();
				Date date = new Date();
				cal.setTime(date);

				// 시작시간
				Calendar cal1 = Calendar.getInstance();
				cal1 = Utility.StringToCal(child.getStartTime());

				long d1 = cal.getTimeInMillis();
				long d2 = cal1.getTimeInMillis();
				int result = (int) ((d2 - d1) / (1000 * 60));

				remainTimeTv.setText("-" + String.valueOf(result) + "분");
			}

			/* 일정 시간 */
			Calendar cal = Calendar.getInstance();
			cal = Utility.StringToCal(child.getStartTime());
			if (child.getType().equals("오늘") || child.getType().equals("내일")) {
				String minute = String.valueOf(cal.get(Calendar.MINUTE));
				if (minute.length() == 1) {
					minute = "0" + minute;
				}
				if (cal.get(Calendar.AM_PM) == 0) {
					timeTv.setText("AM   " + cal.get(Calendar.HOUR) + ":" + minute);
				} else if (cal.get(Calendar.AM_PM) == 1) {
					timeTv.setText("PM   " + cal.get(Calendar.HOUR) + ":" + minute);
				}
			} else {
				int month = cal.get(Calendar.MONTH) + 1;
				timeTv.setText(cal.get(Calendar.YEAR) + "/" + month + "/" + cal.get(Calendar.DAY_OF_MONTH));
			}

			/* 즐겨찾기 */
			if (child.getBookmark().equals("true")) {
				favoriteIv.setBackgroundResource(R.drawable.checked_favorite);
			} else {
				favoriteIv.setBackgroundResource(R.drawable.unchecked_favorite);
			}

			favoriteIv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					RequestParams params = new RequestParams();
					if (child.getStatus().equals("event")) {
						params.put("eventNo", child.getNo());
					} else {
						params.put("scheduleNo", child.getNo());
					}
					params.put("email", MainActivity.sharedpreferences.getString(Utility.Email, ""));
					if (child.getBookmark().equals("true")) {
						params.add("status", "unregist");
					} else {
						params.add("status", "regist");
					}

					updateFavorite(params);
				}

			});

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showSchedule(child);
				}
			});

			convertView.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {

					final AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.MyCustomTheme)
							.create();
					final View dialogView = View.inflate(getActivity(), R.layout.popup_schedule, null);

					LayoutParams params = new LayoutParams();
					params.copyFrom(alertDialog.getWindow().getAttributes());
					params.dimAmount = 0.5f;
					params.width = LayoutParams.WRAP_CONTENT;
					params.height = LayoutParams.WRAP_CONTENT;

					TextView scheduleName = (TextView) dialogView.findViewById(R.id.schedule_name);
					scheduleName.setText(child.getName());

					dialogView.findViewById(R.id.schedule_bookmark).setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							int no = child.getNo();
							String sNo = String.valueOf(no);
							RequestParams params = new RequestParams();
							params.add("sNo", sNo);
							updateFavorite(params);
							alertDialog.dismiss();
						}
					});

					dialogView.findViewById(R.id.schedule_delete).setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							int no = child.getNo();
							scheduleAdapter.notifyDataSetChanged();

							String sNo = String.valueOf(no);
							RequestParams params = new RequestParams();
							params.add("sNo", sNo);

							deleteHostSchedule(params);
							alertDialog.dismiss();

						}
					});
					alertDialog.setView(dialogView);
					alertDialog.show();
					Window window = alertDialog.getWindow();
					window.setAttributes(params);
					window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
					window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
					return true;
				}
			});

			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			ArrayList<Schedule> chList = groups.get(groupPosition).getItems();
			return chList.size();
		}

		@Override
		public ScheduleGroup getGroup(int groupPosition) {
			return groups.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return groups.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			ScheduleGroup group = (ScheduleGroup) getGroup(groupPosition);
			if (convertView == null) {
				LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inf.inflate(R.layout.schedule_group_item, null);
			}

			TextView tv = (TextView) convertView.findViewById(R.id.group_name);
			tv.setText(group.getName());
			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}

}
