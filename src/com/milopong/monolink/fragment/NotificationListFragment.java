package com.milopong.monolink.fragment;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.milopong.monolink.R;
import com.milopong.monolink.activity.ScheduleInfoActivity;
import com.milopong.monolink.model.Member;
import com.milopong.monolink.model.Notification;
import com.milopong.monolink.model.Schedule;
import com.milopong.monolink.utils.ImageOption;
import com.milopong.monolink.utils.MonoURL;
import com.milopong.monolink.utils.Utility;
import com.nostra13.universalimageloader.core.ImageLoader;

public class NotificationListFragment extends ListFragment {

	private static final String REQUEST = "selectScheduleByNo.do";
	private NotificationAdapter notificationAdapter;
	private ListView notificationListView;
	private SharedPreferences sharedpreferences;
	private ArrayList<Notification> notifications = new ArrayList<Notification>();

	public NotificationListFragment(List<Notification> notifications) {
		this.notifications = (ArrayList<Notification>) notifications;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_notification_list, null);

	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		sharedpreferences = getActivity().getSharedPreferences(
				Utility.MyPREFERENCES, Context.MODE_PRIVATE);
		notificationAdapter = new NotificationAdapter();
		setListAdapter(notificationAdapter);
		notificationListView = getListView();
		notificationListView.setAdapter(notificationAdapter);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		getActivity();
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 1) {
				String nNo = data.getStringExtra("nNo");
				int no = Integer.valueOf(nNo);
				notificationAdapter.remove(no);
			}
		}
	}

	public void selectScheduleByNo(RequestParams params, final String nNo) {
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
							Schedule aSchedule = new Schedule();
							JSONObject schedule = obj.getJSONObject("schedule");

							if (schedule != null) {
								int no = schedule.getInt("no");

								String name = schedule.getString("name");

								String hostName = schedule.getJSONObject(
										"member").getString("name");
								String hostPhoto = schedule.getJSONObject(
										"member").getString("photo");

								Member host = new Member();
								host.setName(hostName);
								host.setPhoto(hostPhoto);

								String startTime = schedule
										.getString("startTime");
								String tag = schedule.getString("tag");
								String endTime = schedule.getString("endTime");
								String place = schedule.getString("place");
								String detailPlace = schedule
										.getString("detailPlace");
								String longitude = schedule
										.getString("longitude");
								String latitude = schedule
										.getString("latitude");
								String memo = schedule.getString("memo");
								String status = "guest";

								JSONArray participants = obj
										.getJSONArray("participants");
								ArrayList<Member> lParticipants = new ArrayList<Member>();
								if (participants.length() != 0) {
									for (int j = 0; j < participants.length(); j++) {
										Member participant = new Member();

										int memberNo = participants
												.getJSONObject(j).getInt("no");

										String memberName = participants
												.getJSONObject(j).getString(
														"name");
										String memberEmail = participants
												.getJSONObject(j).getString(
														"email");
										String memberPhone = participants
												.getJSONObject(j).getString(
														"phone");
										String memberPhoto = participants
												.getJSONObject(j).getString(
														"photo");
										participant.setNo(memberNo);
										participant.setName(memberName);
										participant.setEmail(memberEmail);
										participant.setPhone(memberPhone);
										participant.setPhoto(memberPhoto);
										lParticipants.add(participant);
									}
								}

								aSchedule.setNo(no);
								aSchedule.setName(name);
								aSchedule.setHost(host);
								aSchedule.setTag(tag);
								aSchedule.setStartTime(startTime);
								aSchedule.setEndTime(endTime);
								aSchedule.setPlace(place);
								aSchedule.setDetailPlace(detailPlace);
								aSchedule.setLongitude(longitude);
								aSchedule.setLatitude(latitude);
								aSchedule.setParticipants(lParticipants);
								aSchedule.setMemo(memo);
								aSchedule.setStatus(status);
								showSchedule(aSchedule, nNo);

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

	public void showSchedule(Schedule schedule, String nNo) {
		Intent intentScheduleInfoActivity = new Intent(getActivity(),
				ScheduleInfoActivity.class);
		Schedule aSchedule = new Schedule();
		aSchedule.setNo(schedule.getNo());
		aSchedule.setName(schedule.getName());
		aSchedule.setHost(schedule.getHost());
		aSchedule.setTag(schedule.getTag());
		aSchedule.setStartTime(schedule.getStartTime());
		aSchedule.setEndTime(schedule.getEndTime());
		aSchedule.setPlace(schedule.getPlace());
		aSchedule.setDetailPlace(schedule.getDetailPlace());
		aSchedule.setLatitude(schedule.getLatitude());
		aSchedule.setLongitude(schedule.getLongitude());
		aSchedule.setParticipants(schedule.getParticipants());
		aSchedule.setMemo(schedule.getMemo());
		aSchedule.setStatus(schedule.getStatus());

		intentScheduleInfoActivity.putExtra("nNo", nNo);
		intentScheduleInfoActivity.putExtra("schedule", aSchedule);
		startActivityForResult(intentScheduleInfoActivity, 1);

	}

	private class NotificationAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return notifications.size();
		}

		@Override
		public Notification getItem(int position) {
			return notifications.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ImageView photoIv = null;
			TextView nameTv = null;
			TextView messageTv = null;
			Button addBtn;
			ViewHolder viewHolder = null;

			if (convertView == null) {
				LayoutInflater mInflater = (LayoutInflater) getActivity()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = mInflater.inflate(R.layout.layout_notification,
						null);

				photoIv = (ImageView) convertView
						.findViewById(R.id.friend_photo_iv);
				nameTv = (TextView) convertView.findViewById(R.id.nameTv);
				messageTv = (TextView) convertView.findViewById(R.id.message);
				addBtn = (Button) convertView.findViewById(R.id.addBtn);

				viewHolder = new ViewHolder();
				viewHolder.photoIv = photoIv;
				viewHolder.nameTv = nameTv;
				viewHolder.messageTv = messageTv;
				viewHolder.addBtn = addBtn;
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
				photoIv = viewHolder.photoIv;
				nameTv = viewHolder.nameTv;
				messageTv = viewHolder.messageTv;
				addBtn = viewHolder.addBtn;
			}

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String no = String.valueOf(getItem(position)
							.getScheduleNo());
					String nNo = String.valueOf(getItem(position).getNo());
					RequestParams params = new RequestParams();
					params.add("sNo", no);
					selectScheduleByNo(params, nNo);
				}
			});

			String profile = MonoURL.SERVER_URL + "images/profile/"
														
					+ notifications.get(position).getPhoto();
			ImageLoader.getInstance().displayImage(profile, photoIv,
					ImageOption.options);
			viewHolder.nameTv.setText(getItem(position).getSenderName());
			viewHolder.messageTv.setText(getItem(position).getMessage());

			if (getItem(position).getType().equals("joinOpenSchedule")) {
				addBtn.setVisibility(View.VISIBLE);
				addBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						final AlertDialog alertDialog = new AlertDialog.Builder(
								getActivity(), R.style.MyCustomTheme).create();
						final View dialogView = View.inflate(getActivity(),
								R.layout.popup_schedule, null);

						LayoutParams params = new LayoutParams();
						params.copyFrom(alertDialog.getWindow().getAttributes());
						params.dimAmount = 0.5f;
						params.width = LayoutParams.WRAP_CONTENT;
						params.height = LayoutParams.WRAP_CONTENT;
						alertDialog.setView(dialogView);
						alertDialog.show();
						Window window = alertDialog.getWindow();
						window.setAttributes(params);
						window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
						window.setBackgroundDrawable(new ColorDrawable(
								Color.TRANSPARENT));

					}
				});
			}

			return convertView;
		}

		public void remove(int no) {

			for (int i = 0; i < notifications.size(); i++) {
				if (notifications.get(i).getNo() == no) {
					notifications.remove(i);
					notifyDataSetChanged();
				}
			}

		}

	}

	public static class ViewHolder {
		public ImageView photoIv;
		public TextView nameTv;
		public TextView messageTv;
		public Button addBtn;

	}

}