package com.milopong.monolink.fragment;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.milopong.monolink.R;
import com.milopong.monolink.activity.CalendarActivity;
import com.milopong.monolink.activity.MainActivity;
import com.milopong.monolink.model.FriendGroup;
import com.milopong.monolink.model.Member;
import com.milopong.monolink.model.Person;
import com.milopong.monolink.model.Schedule;
import com.milopong.monolink.utils.ImageOption;
import com.milopong.monolink.utils.MonoURL;
import com.milopong.monolink.utils.Utility;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

public class FriendListFragment extends Fragment {
	private FriendAdapter friendAdapter;
	private ExpandableListView friendListView;
	private ArrayList<FriendGroup> expListItems = new ArrayList<FriendGroup>();
	private ArrayList<FriendGroup> arrayList;
	private static final String group_names[] = { "친구추천", "친구" };

	public FriendListFragment() {
		this.arrayList = new ArrayList<FriendGroup>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_friend_list, null);
		return v;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initialize();
		friendAdapter = new FriendAdapter(getActivity(), expListItems);
		friendListView = (ExpandableListView) getView().findViewById(R.id.exp_list);
		friendListView.setFastScrollEnabled(true);
		friendListView.setAdapter(friendAdapter);
	}

	public void initialize() {
		List<Person> contactsList = new ArrayList<Person>();
		RequestParams params;
		params = new RequestParams();
		params.add("email", MainActivity.sharedpreferences.getString(Utility.Email, ""));

		try {
			contactsList = selectMyPhoneFriendAll();
			Gson gson = new Gson();
			String out = gson.toJson(contactsList);
			params.put("members", out);

		} catch (Exception e) {
			e.printStackTrace();
		}
		expListItems.clear();
		arrayList.clear();
		selectFriend(params);

	}

	public void findFriend(String text) {
		friendAdapter.filter(text);
	}

	public List<Person> selectMyPhoneFriendAll() throws Exception {
		List<Person> contactsList = new ArrayList<Person>();
		// 주소록 URI
		Uri people = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		// 검색할 컬럼 정하기
		String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone._ID,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER };

		@SuppressWarnings("deprecation")
		Cursor cursor = getActivity().managedQuery(people, projection, null, null, null); // 전화번호부
		// 가져오기

		int end = cursor.getCount(); // 전화번호부의 갯수 세기

		String[] name = new String[end]; // 전화번호부의 이름을 저장할 배열 선언
		String[] phone = new String[end];

		int count = 0;

		if (cursor.moveToFirst()) {
			// 컬럼명으로 컬럼 인덱스 찾기
			int idIndex = cursor.getColumnIndex(Phone._ID);
			int nameIndex = cursor.getColumnIndex(Phone.DISPLAY_NAME);
			int phoneIndex = cursor.getColumnIndex(Phone.NUMBER);

			do {
				int id = cursor.getInt(idIndex);
				name[count] = cursor.getString(nameIndex);
				phone[count] = cursor.getString(phoneIndex);

				Person aMember = new Person(name[count], phone[count]);

				contactsList.add(aMember);
				count++;

			} while (cursor.moveToNext() || count > end);
		}

		return contactsList;
	}

	public void addFriend(RequestParams params) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(MonoURL.SERVER_URL + MonoURL.ADD_FRIEND, params, new AsyncHttpResponseHandler() {
			String response = null;
			JSONObject obj = null;

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

				try {
					response = new String(arg2, "UTF-8");
					obj = new JSONObject(response);

					if (obj.getString("status").equals("success")) {
						Toast.makeText(getActivity(), "친구로 등록되었습니다.", Toast.LENGTH_SHORT).show();

						initialize();
					} else {
						Toast.makeText(getActivity(), "친구등록을 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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

	public void selectFriend(RequestParams params) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(MonoURL.SERVER_URL + MonoURL.GET_FRIEND, params, new AsyncHttpResponseHandler() {
			String response = null;
			JSONObject obj = null;

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				ArrayList<Member> arr1 = new ArrayList<Member>();
				ArrayList<Member> arr2 = new ArrayList<Member>();

				try {
					response = new String(arg2, "UTF-8");
					obj = new JSONObject(response);
					JSONArray friends = obj.getJSONArray("friends");
					JSONArray recommend = obj.getJSONArray("recommend");

					JSONArray input = null;

					for (int i = 0; i < 2; i++) {
						switch (i) {
						case 0:
							input = recommend;
							break;
						case 1:
							input = friends;
							break;
						}
						for (int j = 0; j < input.length(); j++) {
							Member aMember = new Member();
							aMember.setNo(input.getJSONObject(j).getInt("no"));
							aMember.setName(input.getJSONObject(j).getString("name"));
							aMember.setPhoto(input.getJSONObject(j).getString("photo"));
							aMember.setPhone(input.getJSONObject(j).getString("phone"));
							aMember.setEmail(input.getJSONObject(j).getString("email"));
							aMember.setSex(input.getJSONObject(j).getString("sex"));
							aMember.setBirth(input.getJSONObject(j).getString("birth"));

							if (i == 0) {
								if (arr1.size() < 5) {
									arr1.add(aMember);
								}
							}

							if (i == 1) {
								arr2.add(aMember);
							}

						}
					}
					for (int i = 0; i < group_names.length; i++) {
						FriendGroup friendGroup = new FriendGroup();
						friendGroup.setName(group_names[i]);
						if (group_names[i].equals("친구추천")) {
							friendGroup.setItems(arr1);
						} else if (group_names[i].equals("친구")) {
							friendGroup.setItems(arr2);
						}
						if (friendGroup.getItems().size() != 0) {
							expListItems.add(friendGroup);
							arrayList.add(friendGroup);
						}
					}

					friendAdapter.notifyDataSetChanged();

					friendListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
						public boolean onGroupClick(ExpandableListView arg0, View itemView, int itemPosition,
								long itemId) {
							if (expListItems.size() != 0)
								friendListView.expandGroup(itemPosition);
							return true;
						}
					});

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

	public void selectFriendSchedule(RequestParams params) {
		AsyncHttpClient client = new AsyncHttpClient();
		final ArrayList<Schedule> arraySchedules = new ArrayList<Schedule>();
		client.get(MonoURL.SERVER_URL + MonoURL.GET_FRIEND_SCHEDULE, params, new AsyncHttpResponseHandler() {
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
							arraySchedules.add(aSchedule);
						}

					}

					Intent calendarActivityIntent = new Intent(getActivity(), CalendarActivity.class);
					Log.d("aa", "arrayList Size:"+arraySchedules.size());
					calendarActivityIntent.putExtra("schedules", arraySchedules);
					calendarActivityIntent.putExtra("mode", 1);
					startActivity(calendarActivityIntent);
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

	public class FriendAdapter extends BaseExpandableListAdapter implements SectionIndexer {
		private Context context;
		private ArrayList<FriendGroup> groups;
		private String mSections = "ㄱㄴㄷㄹㅁㅂㅅㅇㅈㅊㅋㅌㅍㅎAFJOSZ#";

		public FriendAdapter(Context context, ArrayList<FriendGroup> groups) {
			this.context = context;
			this.groups = groups;
		}

		public void filter(String charText) {

			charText = charText.toLowerCase(Locale.getDefault());
			if (charText.length() == 0) {
				groups.clear();
				groups.addAll(arrayList);
				notifyDataSetChanged();

				for (int i = 0; i < expListItems.size(); i++) {
					friendListView.expandGroup(i);
				}

			} else {
				FriendGroup friendGroup = new FriendGroup();
				friendGroup.setName("검색 결과");

				ArrayList<Member> results = new ArrayList<Member>();
				for (Member aMember : arrayList.get(arrayList.size() - 1).getItems()) {
					if (aMember.getPhone().contains(charText)) {
						results.add(aMember);
					}
					if (aMember.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
						results.add(aMember);
					}
					friendGroup.setItems(results);
				}
				groups.clear();
				groups.add(friendGroup);
				friendListView.expandGroup(0);
				notifyDataSetChanged();

			}
			notifyDataSetChanged();
		}

		@Override
		public int getGroupCount() {
			return groups.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			ArrayList<Member> chList = groups.get(groupPosition).getItems();
			return chList.size();
		}

		@Override
		public FriendGroup getGroup(int groupPosition) {
			return groups.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			ArrayList<Member> chList = groups.get(groupPosition).getItems();
			return chList.get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			FriendGroup group = (FriendGroup) getGroup(groupPosition);

			if (convertView == null) {
				LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inf.inflate(R.layout.friend_group_item, null);
			}
			TextView tv = (TextView) convertView.findViewById(R.id.group_name);
			tv.setText(group.getName());
			for (int i = 0; i < expListItems.size(); i++) {
				friendListView.expandGroup(i);
			}
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {

			final Member child = (Member) getChild(groupPosition, childPosition);

			if (convertView == null) {
				LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = mInflater.inflate(R.layout.layout_friend_list, null);
			}

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					RequestParams params = new RequestParams();
					params.add("userEmail", MainActivity.sharedpreferences.getString(Utility.Email, ""));
					params.add("friendEmail", child.getEmail());
					selectFriendSchedule(params);
				}
			});

			ImageView photoIv = (ImageView) convertView.findViewById(R.id.friend_photo_iv);
			TextView nameTv = (TextView) convertView.findViewById(R.id.friend_name_tv);
			final Button addBtn = (Button) convertView.findViewById(R.id.friend_add_btn);

			/* 친구사진 */
			String profile = MonoURL.SERVER_URL + "images/profile/" + child.getPhoto();
			ImageLoader.getInstance().displayImage(profile, photoIv, ImageOption.options);

			/* 친구 이름 */
			nameTv.setText(child.getName());

			if (getGroup(0).getName().equals("친구추천") && getGroup(0).getItems().contains(child)) {
				addBtn.setBackgroundResource(R.drawable.add_btn);
				addBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						RequestParams params = new RequestParams();
						params.add("userEmail", MainActivity.sharedpreferences.getString(Utility.Email, ""));
						params.add("friendEmail", child.getEmail());
						addFriend(params);
					}
				});
			} else {
				addBtn.setBackgroundResource(android.R.color.transparent);
			}

			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		@Override
		public Object[] getSections() {
			String[] sections = new String[mSections.length()];
			for (int i = 0; i < mSections.length(); i++)
				sections[i] = String.valueOf(mSections.charAt(i));
			return sections;
		}

		@Override
		public int getPositionForSection(int section) {

			return friendListView.getFlatListPosition(ExpandableListView.getPackedPositionForGroup(section));
		}

		@Override
		public int getSectionForPosition(int groupPosition) {
			return 0;
		}

	}
}
