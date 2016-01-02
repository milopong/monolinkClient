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
import com.milopong.monolink.activity.EventInfoActivity;
import com.milopong.monolink.activity.MainActivity;
import com.milopong.monolink.model.Event;
import com.milopong.monolink.model.Member;
import com.milopong.monolink.model.Schedule;
import com.milopong.monolink.model.ScheduleGroup;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EventListFragment extends Fragment {

	private EventAdapter eventAdapter;
	private ListView eventListView;
	private ArrayList<Event> mData = new ArrayList<Event>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_event_list, null);

	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		eventListView = (ListView) getView().findViewById(R.id.list);
		eventAdapter = new EventAdapter();
		eventListView.setAdapter(eventAdapter);

	}
	
	@Override
	public void onResume() {
		super.onResume();
		initialize();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		getActivity();
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 1) {
			}
		}
	}

	public void initialize() {
		RequestParams params;
		params = new RequestParams();
		mData.clear();
		params.put("email", MainActivity.sharedpreferences.getString(Utility.Email, ""));
		selectEvent(params);
	}

	public void findEvent(String text) {
		eventAdapter.filter(text);
	}

	public void selectEvent(RequestParams params) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(MonoURL.SERVER_URL + MonoURL.GET_EVENT, params, new AsyncHttpResponseHandler() {
			String response = null;
			JSONObject obj = null;

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				try {
					response = new String(arg2, "UTF-8");
					obj = new JSONObject(response);

					JSONArray events = obj.getJSONArray("events");

					if (events.length() != 0) {
						for (int i = 0; i < events.length(); i++) {
							Event aEvent = new Event();
							JSONArray participants = obj
									.getJSONArray("participants" + events.getJSONObject(i).getInt("no"));

							ArrayList<Member> lParticipants = new ArrayList<Member>();

							for (int j = 0; j < participants.length(); j++) {
								Member participant = new Member();

								participant.setNo(participants.getJSONObject(j).getInt("no"));
								participant.setName(participants.getJSONObject(j).getString("name"));
								participant.setEmail(participants.getJSONObject(j).getString("email"));
								participant.setPhone(participants.getJSONObject(j).getString("phone"));
								participant.setPhoto(participants.getJSONObject(j).getString("photo"));
								lParticipants.add(participant);
							}

							aEvent.setNo(events.getJSONObject(i).getInt("no"));
							aEvent.setName(events.getJSONObject(i).getString("name"));
							aEvent.setStartTime(events.getJSONObject(i).getString("startTime"));
							aEvent.setEndTime(events.getJSONObject(i).getString("endTime"));
							aEvent.setPlace(events.getJSONObject(i).getString("place"));
							aEvent.setDetailPlace(events.getJSONObject(i).getString("detailPlace"));
							aEvent.setLongitude(events.getJSONObject(i).getString("longitude"));
							aEvent.setLatitude(events.getJSONObject(i).getString("latitude"));
							aEvent.setMemo(events.getJSONObject(i).getString("memo"));
							aEvent.setPhoto(events.getJSONObject(i).getString("photo"));
							aEvent.setTag(events.getJSONObject(i).getString("tag"));
							aEvent.setParticipants(lParticipants);
							aEvent.setCompany(events.getJSONObject(i).getString("company"));
							aEvent.setBookmark(events.getJSONObject(i).getString("bookmark"));
							eventAdapter.addItem(aEvent);
							eventAdapter.notifyDataSetChanged();
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
						initialize();
					} else if (obj.getString("status").equals("unRegist")) {
						Toast.makeText(getActivity(), "즐겨찾기가 삭제 되었습니다.", Toast.LENGTH_SHORT).show();
						initialize();
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

	public void showEvent(Event aEvent) {
		Intent intentEventInfoActivity = new Intent(getActivity(), EventInfoActivity.class);
		Event event = new Event();
		event = aEvent;
		intentEventInfoActivity.putExtra("event", event);
		startActivityForResult(intentEventInfoActivity, 2);

	}

	private class EventAdapter extends BaseAdapter {

		ArrayList<Event> arrayList = new ArrayList<Event>();

		public void addItem(Event event) {
			Event aEvent = new Event();
			aEvent = event;
			mData.add(aEvent);
			arrayList.add(aEvent);
			notifyDataSetChanged();
		}

		public void filter(String charText) {
			charText = charText.toLowerCase(Locale.getDefault());
			mData.clear();
			if (charText.length() == 0) {
				mData.addAll(arrayList);
			} else {
				for (Event aEvent : arrayList) {
					if (aEvent.getCompany().contains(charText)) {
						mData.add(aEvent);
					}
					if (aEvent.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
						mData.add(aEvent);
					}
					if (aEvent.getTag().toLowerCase(Locale.getDefault()).contains(charText)) {
						mData.add(aEvent);
					}

				}
			}
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Event getItem(int position) {
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
				LayoutInflater mInflater = (LayoutInflater) getActivity()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				viewHolder = new ViewHolder();

				convertView = mInflater.inflate(R.layout.layout_event_list, null);
				viewHolder.companyTv = (TextView) convertView.findViewById(R.id.hostTv);
				viewHolder.photoIv = (ImageView) convertView.findViewById(R.id.photoIv);
				viewHolder.nameTv = (TextView) convertView.findViewById(R.id.nameTv);
				viewHolder.favoriteIv = (ImageView) convertView.findViewById(R.id.favoriteIv);
				viewHolder.tagTv = (TextView) convertView.findViewById(R.id.tagTv);
				viewHolder.timeTv = (TextView) convertView.findViewById(R.id.timeTv);
				convertView.setTag(viewHolder);
			}
			viewHolder = (ViewHolder) convertView.getTag();
			/* 회사이름 */
			viewHolder.companyTv.setText(getItem(position).getCompany());

			/* 이벤트 이미지 */
			DisplayMetrics dm = getActivity().getApplicationContext().getResources().getDisplayMetrics();
			Bitmap photo = ImageLoader.getInstance()
					.loadImageSync(MonoURL.SERVER_URL + "images/event/" + mData.get(position).getPhoto());
			
			int width = dm.widthPixels;
			Log.d("aa","width:"+width);
			int diw = photo.getWidth();
			int dih = photo.getHeight();
			float ratio = (float) diw / dih;

			int height = (int) (width / ratio);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
			viewHolder.photoIv.setLayoutParams(layoutParams);
			ImageLoader.getInstance().displayImage(
					MonoURL.SERVER_URL + "images/event/" + mData.get(position).getPhoto(), viewHolder.photoIv,
					ImageOption.options);

			/* 이베트 이름 */
			viewHolder.nameTv.setText(getItem(position).getName());

			/* 즐겨찾기 버튼 */
			viewHolder.favoriteIv.setBackgroundResource(R.drawable.unchecked_favorite);
			if (getItem(position).getBookmark().equals("true")) {
				viewHolder.favoriteIv.setBackgroundResource(R.drawable.checked_favorite);
			} else {
				viewHolder.favoriteIv.setBackgroundResource(R.drawable.unchecked_favorite);
			}

			viewHolder.favoriteIv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					RequestParams params = new RequestParams();
					params.put("eventNo", getItem(position).getNo());
					params.put("email", MainActivity.sharedpreferences.getString(Utility.Email, ""));
					if (getItem(position).getBookmark().equals("true")) {
						params.add("status", "unregist");
					} else {
						params.add("status", "regist");
					}

					updateFavorite(params);
				}
			});

			/* 이벤트 태그 */
			viewHolder.tagTv.setText(getItem(position).getTag());

			/* 이벤트 시간 */
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
					showEvent(getItem(position));
				}
			});

			return convertView;
		}

	}

	public static class ViewHolder {
		public TextView companyTv;
		public ImageView photoIv;
		public TextView nameTv;
		public ImageView favoriteIv;
		public TextView tagTv;
		public TextView timeTv;
	}

}
