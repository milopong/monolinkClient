package com.milopong.monolink.fragment;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.milopong.monolink.R;
import com.milopong.monolink.activity.MainActivity;
import com.milopong.monolink.adapter.MenuAdapter;
import com.milopong.monolink.model.Menu;
import com.milopong.monolink.utils.ImageOption;
import com.milopong.monolink.utils.MonoURL;
import com.milopong.monolink.utils.Utility;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore.Images;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MenuListFragment extends ListFragment implements OnClickListener {

	private String lineEnd = "\r\n";
	private String twoHyphens = "--";
	private String boundary = "*****";
	final int REQ_CODE_SELECT_IMAGE = 100;

	private ImageView profileImageView;
	private TextView nameTv;
	private Bitmap image_bitmap;

	public MenuListFragment() {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites()
				.detectNetwork().penaltyLog().build());
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_leftmenu, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		profileImageView = (ImageView) getView().findViewById(R.id.profile);
		profileImageView.setOnClickListener(this);

		nameTv = (TextView) getView().findViewById(R.id.name);

		MenuAdapter adapter = new MenuAdapter(getActivity());
		adapter.add(new Menu(R.drawable.left_home));
		adapter.add(new Menu(R.drawable.left_friend));
		adapter.add(new Menu(R.drawable.left_event));
		adapter.add(new Menu(R.drawable.left_notification));
		adapter.add(new Menu(R.drawable.left_setup));
		

		setListAdapter(adapter);

		String profile = MonoURL.SERVER_URL + "images/profile/"
				+ MainActivity.sharedpreferences.getString(Utility.Photo, "");
		ImageLoader.getInstance().displayImage(profile, profileImageView, ImageOption.options);
		nameTv.setText(MainActivity.sharedpreferences.getString(Utility.Name, ""));
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQ_CODE_SELECT_IMAGE) {
			if (resultCode == Activity.RESULT_OK) {
				try {
					Uri selphotoUri = data.getData();
					// 이미지 데이터를 비트맵으로 받아온다.
					image_bitmap = Images.Media.getBitmap(getActivity().getContentResolver(), selphotoUri);
					File file = new File(Environment.getExternalStorageDirectory(), "monoProfile.jpg"); // 크기가
																										// 수정된
																										// 파일을
																										// 저장할
																										// 경로!
					FileOutputStream out = new FileOutputStream(file);

					int height = image_bitmap.getHeight();
					int width = image_bitmap.getWidth();

					if (width > 1000) {
						/*
						 * Bitmap이미지의 크기를 가로사이즈1000을 기준으로 비율에 맞게 조정하고 파일로저장! 조정된
						 * 가로, 세로의 크기를 각각 height, width에 저장한다!
						 */
						int convertWidth = 1000;
						double x = width / 1000;
						int convertHeight = (int) (height / x);
						image_bitmap = Bitmap.createScaledBitmap(image_bitmap, convertWidth, convertHeight, true);
					}
					image_bitmap.compress(CompressFormat.JPEG, 70, out);

					// 배치해놓은 ImageView에 set
					profileImageView.setImageBitmap(image_bitmap);

					String urlString = MonoURL.SERVER_URL + MonoURL.PROFILE_UPLOAD;
					String absolutePath = file.toString();
					DoFileUpload(urlString, absolutePath);

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void DoFileUpload(String apiUrl, String absolutePath) {
		HttpFileUpload(apiUrl, "", absolutePath);
	}

	public void HttpFileUpload(String urlString, String param, String fileName) {
		try {

			FileInputStream mFileInputStream = new FileInputStream(fileName);
			URL connectUrl = new URL(urlString);
			// open connection
			HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			// write data
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes(
					"Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName + "\"" + lineEnd);
			dos.writeBytes(lineEnd);

			int bytesAvailable = mFileInputStream.available();
			int maxBufferSize = 1024;
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);

			byte[] buffer = new byte[bufferSize];
			int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

			Log.d("Test", "image byte is " + bytesRead);

			// read image
			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = mFileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
			}

			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			// close streams
			Log.e("Test", "File is written");
			mFileInputStream.close();
			dos.flush(); // finish upload...

			// get response
			int ch;
			InputStream is = conn.getInputStream();
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			String s = b.toString();
			dos.close();

			if (s.equals("success")) {
				RequestParams params = new RequestParams();
				params.add("email", MainActivity.sharedpreferences.getString(Utility.Email, ""));
				saveImage(params);
			}

		} catch (Exception e) {
			Log.d("Test", "exception " + e.getMessage());
		}
	}

	public void saveImage(RequestParams params) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(MonoURL.SERVER_URL + MonoURL.PROFILE_SAVE, params, new AsyncHttpResponseHandler() {
			String response = null;
			JSONObject obj = null;

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				try {
					response = new String(arg2, "UTF-8");
					obj = new JSONObject(response);

					if (obj.getString("stauts").equals("success")) {
						Toast.makeText(getActivity().getApplicationContext(), "이미지 저장에 성공 했습니다.", Toast.LENGTH_LONG)
								.show();
					} else {
						Toast.makeText(getActivity().getApplicationContext(), "이미지 저장에 실패 했습니다.", Toast.LENGTH_LONG)
								.show();
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
	public void onListItemClick(ListView l, View v, int position, long id) {
		String title = null;
		switch (position) {
		case 0:
			title = "HomeFragment";
			break;
		case 1:
			title = "FriendFragment";
			break;
		case 2:
			title = "EventFragment";
			break;
		case 3:
			title = "NotificationFragment";
			break;
		case 4:
			title = "SetupActivity";
			break;

		case 5:
			title = "NoticeActivity";
			break;
		}

		if (title != null)
			switchFragment(title);
		super.onListItemClick(l, v, position, id);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.profile:
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
			intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
			break;

		}

	}

	private void switchFragment(String name) {
		if (getActivity() == null)
			return;
		if (getActivity() instanceof MainActivity) {
			MainActivity fca = (MainActivity) getActivity();
			fca.switchContent(name);
		}
	}

}
