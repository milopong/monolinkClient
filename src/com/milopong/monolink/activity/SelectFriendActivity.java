package com.milopong.monolink.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.milopong.monolink.R;
import com.milopong.monolink.model.Member;
import com.milopong.monolink.ui.de.hdodenhof.circleimageview.CircleImageView;
import com.milopong.monolink.utils.MonoURL;
import com.milopong.monolink.utils.Utility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class SelectFriendActivity extends ActionBarActivity implements OnEditorActionListener, OnClickListener {
	private SharedPreferences sharedPreferences;
	private final static String REQUEST = "selectFriend.do";
	private EditText searchFriendEt;
	private Button inviteBtn;
	private RequestParams params = null;
	private ListView selectFriend_ListView;
	private SelectFriendAdapter selectFriend_Adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_friend);
		sharedPreferences = getSharedPreferences(Utility.MyPREFERENCES, Context.MODE_PRIVATE);
		String email = sharedPreferences.getString(Utility.Email, "");
		searchFriendEt = (EditText) findViewById(R.id.inputSearch);
		searchFriendEt.setOnEditorActionListener(this);
		searchFriendEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String text = searchFriendEt.getText().toString().toLowerCase(Locale.getDefault());
				selectFriend_Adapter.filter(text);

			}
		});

		inviteBtn = (Button) findViewById(R.id.invite);
		inviteBtn.setOnClickListener(this);

		params = new RequestParams();
		params.add("email", email);
		params.add("members", null);
		selectFriend(params);
		selectFriend_ListView = (ListView) findViewById(R.id.list_view);
		selectFriend_Adapter = new SelectFriendAdapter(SelectFriendActivity.this);
		selectFriend_ListView.setAdapter(selectFriend_Adapter);

	}

	public void selectFriend(RequestParams params) {
		if (selectFriend_Adapter != null) {
			selectFriend_Adapter.clear();
		}
		AsyncHttpClient client = new AsyncHttpClient();

		client.post(MonoURL.SERVER_URL + REQUEST, params, new AsyncHttpResponseHandler() {
			String response = null;
			JSONObject obj = null;

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				try {
					response = new String(arg2, "UTF-8");
					obj = new JSONObject(response);
					JSONArray input = obj.getJSONArray("friends");

					for (int j = 0; j < input.length(); j++) {
						Member aMember = new Member();
						aMember.setNo(input.getJSONObject(j).getInt("no"));
						aMember.setName(input.getJSONObject(j).getString("name"));
						aMember.setPhoto(input.getJSONObject(j).getString("photo"));
						aMember.setPhone(input.getJSONObject(j).getString("phone"));
						aMember.setEmail(input.getJSONObject(j).getString("email"));
						aMember.setSex(input.getJSONObject(j).getString("sex"));
						aMember.setBirth(input.getJSONObject(j).getString("birth"));
						selectFriend_Adapter.add(aMember);
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
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		return false;
	}

	@Override
	public void onClick(View v) {
		ArrayList<Member> selectedFriends = new ArrayList<Member>();
		switch (v.getId()) {

		case R.id.invite:
			selectedFriends = selectFriend_Adapter.selectAttendance();
			Log.d("aa", "Size:" + selectedFriends.size());
			Intent intent = getIntent();
			intent.putExtra("selectedFriends", selectedFriends);
			setResult(RESULT_OK, intent);
			finish();
			break;

		case R.id.backBtn:
			finish();
			break;
		}

	}

	public class SelectFriendAdapter extends BaseAdapter {
		private List<Member> friendList;
		private ArrayList<Member> arrayList;
		DisplayImageOptions options;
		private LayoutInflater inflater;
		private Context con;

		public SelectFriendAdapter(Context context) {
			this.friendList = new ArrayList<Member>();
			this.arrayList = new ArrayList<Member>();

			options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_profile)
					.showImageForEmptyUri(R.drawable.default_profile).showImageOnFail(R.drawable.default_profile)
					.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			con = context;
		}

		@Override
		public int getCount() {
			return friendList.size();
		}

		@Override
		public Object getItem(int position) {
			return friendList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final int pos = position;
			final Context context = parent.getContext();

			ImageView photoIv = null;
			TextView nameTv = null;
			CheckBox check = null;

			CustomHolder holder = null;

			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.layout_select_friend, parent, false);
				photoIv = (ImageView) convertView.findViewById(R.id.friend_photo_iv);
				nameTv = (TextView) convertView.findViewById(R.id.friend_name_tv);

				check = (CheckBox) convertView.findViewById(R.id.friend_check);

				holder = new CustomHolder();
				holder.photoIv = photoIv;
				holder.nameTv = nameTv;
				holder.check = check;

				convertView.setTag(holder);
			} else {
				holder = (CustomHolder) convertView.getTag();
				photoIv = holder.photoIv;
				nameTv = holder.nameTv;
				check = holder.check;
			}

			String profile = MonoURL.SERVER_URL + "images/profile/" + friendList.get(position).getPhoto();
			ImageLoader.getInstance().displayImage(profile, photoIv, options);

			nameTv.setText(friendList.get(position).getName());

			if (friendList.get(position).getCheck() == true) {
				check.setChecked(true);
			} else {
				check.setChecked(false);
			}

			check.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					CheckBox checkBox = (CheckBox) v;
					if (checkBox.isChecked()) {
						for (int i = 0; i < arrayList.size(); i++) {
							if (friendList.get(pos).getName().equals(arrayList.get(i).getName())) {
								arrayList.get(i).setCheck(true);
							}
						}
					} else {
						for (int i = 0; i < arrayList.size(); i++) {
							if (friendList.get(pos).getName().equals(arrayList.get(i).getName())) {
								arrayList.get(i).setCheck(false);

							}
						}
					}
				}
			});

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					Toast.makeText(context, String.valueOf(pos), Toast.LENGTH_SHORT).show();
				}
			});

			convertView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					return true;
				}
			});

			return convertView;
		}

		public void filter(String charText) {
			charText = charText.toLowerCase(Locale.getDefault());
			friendList.clear();
			if (charText.length() == 0) {
				friendList.addAll(arrayList);
			} else {
				for (Member aMember : arrayList) {
					if (aMember.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
						friendList.add(aMember);
					}

				}
			}
			notifyDataSetChanged();
		}

		public void add(Member member) {
			Member aMember = new Member();
			aMember = member;
			friendList.add(aMember);
			arrayList.add(aMember);
			notifyDataSetChanged();
		}

		public void clear() {
			friendList.clear();
			notifyDataSetChanged();
		}

		public ArrayList<Member> selectAttendance() {
			ArrayList<Member> selectedFriendList = new ArrayList<Member>();
			for (int i = 0; i < arrayList.size(); i++) {
				if (arrayList.get(i).getCheck() == true) {
					Member aMember = new Member();
					aMember = arrayList.get(i);
					selectedFriendList.add(aMember);
				}
			}
			return selectedFriendList;

		}

		private class CustomHolder {
			ImageView photoIv;
			TextView nameTv;
			CheckBox check;
		}

	}
}