package com.milopong.monolink.fragment;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.milopong.monolink.activity.MakeScheduleActivity;
import com.milopong.monolink.activity.ScheduleInfoActivity;
import com.milopong.monolink.model.Member;
import com.milopong.monolink.model.Schedule;
import com.milopong.monolink.utils.ImageOption;
import com.milopong.monolink.utils.MonoURL;
import com.milopong.monolink.utils.Utility;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ShareScheduleFragment extends Fragment implements OnClickListener {
	private OpenScheduleAdapter openScheduleAdapter;
	private ArrayList<Schedule> openSchedule = new ArrayList<Schedule>();
	private ListView openScheduleListView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_share_schedule_list, null);

	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initialize();
		openScheduleListView = (ListView) getView().findViewById(R.id.list);
		openScheduleAdapter = new OpenScheduleAdapter();
		openScheduleListView.setAdapter(openScheduleAdapter);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 1) {
				initialize();
			} else if (requestCode == 2) {
				initialize();
			}
		}
	}

	public void initialize() {
		RequestParams params;
		params = new RequestParams();
		params.add("email", MainActivity.sharedpreferences.getString(Utility.Email, ""));
		openSchedule.clear();
		selectOpenSchedule(params);
	}

	public void findSchedule(String text) {
		openScheduleAdapter.filter(text);
	}

	public void selectOpenSchedule(RequestParams params) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(MonoURL.SERVER_URL + MonoURL.GET_OPEN_SCHEDULE, params, new AsyncHttpResponseHandler() {
			String response = null;
			JSONObject obj = null;

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				try {
					response = new String(arg2, "UTF-8");
					obj = new JSONObject(response);
					JSONArray schedules = obj.getJSONArray("openSchedules");

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
							aSchedule.setPhoto(schedules.getJSONObject(i).getString("photo"));

							openScheduleAdapter.addItem(aSchedule);
							openScheduleAdapter.notifyDataSetChanged();
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

	public void showSchedule(Schedule schedule) {
		Intent intentScheduleInfoActivity = new Intent(getActivity(), ScheduleInfoActivity.class);
		Schedule aSchedule = new Schedule();
		aSchedule = schedule;
		intentScheduleInfoActivity.putExtra("schedule", aSchedule);
		startActivityForResult(intentScheduleInfoActivity, 2);
	}

	public class OpenScheduleAdapter extends BaseAdapter {
		private ArrayList<Schedule> mData;
		private ArrayList<Schedule> arrayList;

		public OpenScheduleAdapter() {
			this.mData = new ArrayList<Schedule>();
			this.arrayList = new ArrayList<Schedule>();

		}

		public void filter(String charText) {
			charText = charText.toLowerCase(Locale.getDefault());
			mData.clear();
			if (charText.length() == 0) {
				mData.addAll(arrayList);
			} else {
				for (Schedule aSchedule : arrayList) {
					if (aSchedule.getHost().getName().contains(charText)) {
						mData.add(aSchedule);
					}
					if (aSchedule.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
						mData.add(aSchedule);
					}
					if (aSchedule.getTag().toLowerCase(Locale.getDefault()).contains(charText)) {
						mData.add(aSchedule);
					}

				}
			}
			notifyDataSetChanged();
		}

		public void addItem(Schedule schedule) {
			Schedule aSchedule = new Schedule();
			aSchedule = schedule;
			mData.add(aSchedule);
			arrayList.add(aSchedule);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Schedule getItem(int position) {

			return mData.get(position);

		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;

			if (convertView == null) {
				viewHolder = new ViewHolder();

				LayoutInflater mInflater = (LayoutInflater) getActivity()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = mInflater.inflate(R.layout.layout_event_list, null);
				viewHolder.hostTv = (TextView) convertView.findViewById(R.id.hostTv);
				viewHolder.photoIv = (ImageView) convertView.findViewById(R.id.photoIv);
				viewHolder.nameTv = (TextView) convertView.findViewById(R.id.nameTv);
				viewHolder.favoriteIv = (ImageView) convertView.findViewById(R.id.favoriteIv);
				viewHolder.tagTv = (TextView) convertView.findViewById(R.id.tagTv);
				viewHolder.timeTv = (TextView) convertView.findViewById(R.id.timeTv);

				convertView.setTag(viewHolder);
			}

			viewHolder = (ViewHolder) convertView.getTag();
			/* 방장이름 */
			viewHolder.hostTv.setText(getItem(position).getHost().getName());

			/* 방장 사진 */
			viewHolder.photoIv.setVisibility(View.GONE);
			DisplayMetrics dm = getActivity().getApplicationContext().getResources().getDisplayMetrics();
			Bitmap photo = ImageLoader.getInstance()
					.loadImageSync(MonoURL.SERVER_URL + "images/schedule/" + mData.get(position).getPhoto());
			int width = dm.widthPixels;
			int diw = photo.getWidth();
			int dih = photo.getHeight();
			float ratio = (float) diw / dih;

			int height = (int) (width / ratio);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
			viewHolder.photoIv.setLayoutParams(layoutParams);
			ImageLoader.getInstance().displayImage(
					MonoURL.SERVER_URL + "images/schedule/" + mData.get(position).getPhoto(), viewHolder.photoIv,
					ImageOption.options);

			/* 일정 이름 */
			viewHolder.nameTv.setText(getItem(position).getName());

			/* 즐겨찾기 버튼 */
			viewHolder.favoriteIv.setBackgroundResource(R.drawable.unchecked_favorite);

			/* 일정 태그 */
			viewHolder.tagTv.setText(getItem(position).getTag());

			/* 일정 시간 */
			Calendar cal = Calendar.getInstance();
			cal = Utility.StringToCal(getItem(position).getStartTime());
			String minute = String.valueOf(cal.get(Calendar.MINUTE));
			if (minute.length() == 1) {
				minute = "0" + minute;
			}
			if (cal.get(Calendar.AM_PM) == 0) {
				viewHolder.timeTv.setText("AM   " + cal.get(Calendar.HOUR) + ":" + minute);
			} else if (cal.get(Calendar.AM_PM) == 1) {
				viewHolder.timeTv.setText("PM   " + cal.get(Calendar.HOUR) + ":" + minute);
			}

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showSchedule(getItem(position));
				}
			});

			return convertView;
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.registBtn:
			Intent IntentMakeSchedule = new Intent(getActivity(), MakeScheduleActivity.class);
			startActivityForResult(IntentMakeSchedule, 1);
			break;
		}

	}

	public static class ViewHolder {
		public TextView hostTv;
		public ImageView photoIv;
		public TextView nameTv;
		public TextView tagTv;
		public TextView timeTv;
		public ImageView favoriteIv;
	}

}
