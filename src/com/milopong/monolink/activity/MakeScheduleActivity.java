package com.milopong.monolink.activity;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.milopong.monolink.R;
import com.milopong.monolink.model.MapItem;
import com.milopong.monolink.model.Member;
import com.milopong.monolink.utils.MonoURL;
import com.milopong.monolink.utils.Utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

public class MakeScheduleActivity extends ActionBarActivity implements OnClickListener {

	private String lineEnd = "\r\n";
	private String twoHyphens = "--";
	private String boundary = "*****";

	private File file;
	private static final int SELECT_PLACE = 1;
	private static final int SELECT_FRIEND = 2;
	private static final int PICK_FROM_CAMERA = 3;
	private static final int PICK_FROM_ALBUM = 4;
	private Bitmap image_bitmap;
	private Uri mImageCaptureUri;

	private EditText scheduleName;
	private Button scheduleStartTime;
	private Button scheduleEndTime;
	private Button selectFriendButton;
	private Button selectPlaceButton;
	private ImageView scheduleIv;
	private EditText scheduleMemo;
	private Button makeSchedule;
	private double latitude;
	private double longitude;
	private String detailPlace;
	private String startDate, endDate;
	private ArrayList<Member> selectedFriend;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule_make);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		scheduleName = (EditText) this.findViewById(R.id.ed_schedule_name);

		scheduleStartTime = (Button) this.findViewById(R.id.btn_schedule_start);
		scheduleStartTime.setOnClickListener(this);

		scheduleEndTime = (Button) this.findViewById(R.id.btn_schedule_end);
		scheduleEndTime.setOnClickListener(this);

		selectFriendButton = (Button) this.findViewById(R.id.btn_schedule_friend);
		selectFriendButton.setOnClickListener(this);

		selectPlaceButton = (Button) this.findViewById(R.id.btn_schedule_place);
		selectPlaceButton.setOnClickListener(this);

		scheduleIv = (ImageView) this.findViewById(R.id.schedule_photo);
		scheduleIv.setOnClickListener(this);

		scheduleMemo = (EditText) this.findViewById(R.id.ed_schedule_memo);
		makeSchedule = (Button) this.findViewById(R.id.bt_make_schedule);
		makeSchedule.setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {

			switch (requestCode) {

			// 장소 선택
			case SELECT_PLACE:
				MapItem result = (MapItem) data.getSerializableExtra("result");
				selectPlaceButton.setText(result.getTitle());
				latitude = result.getLatitude();
				longitude = result.getLongitude();
				detailPlace = result.getAddress();
				break;

			// 친구 선택
			case SELECT_FRIEND:
				selectedFriend = new ArrayList<Member>();
				selectedFriend = (ArrayList<Member>) data.getSerializableExtra("selectedFriends");
				String sSelectedFriend = null;
				for (int i = 0; i < selectedFriend.size(); i++) {
					if (i == 0) {
						sSelectedFriend = selectedFriend.get(i).getName().toString();

					} else {
						sSelectedFriend += ", " + selectedFriend.get(i).getName().toString();
					}
					selectFriendButton.setText(sSelectedFriend);
				}
				break;

			case PICK_FROM_ALBUM:
				if (resultCode == Activity.RESULT_OK) {
					try {
						mImageCaptureUri = data.getData();
						image_bitmap = Images.Media.getBitmap(getContentResolver(), mImageCaptureUri);
						String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
						file = new File(Environment.getExternalStorageDirectory(), url);
						FileOutputStream out = new FileOutputStream(file);

						int width = image_bitmap.getWidth();
						int height = image_bitmap.getHeight();

						int photoWidth = (int) (326 * Resources.getSystem().getDisplayMetrics().density);
						int photoHeight = (int) (100 * Resources.getSystem().getDisplayMetrics().density);

						int convertWidth = photoWidth;
						int convertHeight = photoWidth * height / width;

						image_bitmap = Bitmap.createScaledBitmap(image_bitmap, convertWidth, convertHeight, true);
						image_bitmap.compress(CompressFormat.JPEG, 70, out);
						image_bitmap = Bitmap.createBitmap(image_bitmap, 0, convertHeight / 4, photoWidth, photoHeight);

						// 배치해놓은 ImageView에 set
						scheduleIv.setImageBitmap(image_bitmap);

					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;

			case PICK_FROM_CAMERA:
				if (resultCode == Activity.RESULT_OK) {
					try {
						image_bitmap = Images.Media.getBitmap(getContentResolver(), mImageCaptureUri);
						FileOutputStream out = new FileOutputStream(file);

						int width = image_bitmap.getWidth();
						int height = image_bitmap.getHeight();

						int photoWidth = (int) (326 * Resources.getSystem().getDisplayMetrics().density);
						int photoHeight = (int) (100 * Resources.getSystem().getDisplayMetrics().density);

						int convertWidth = photoWidth;
						int convertHeight = photoWidth * height / width;

						image_bitmap = Bitmap.createScaledBitmap(image_bitmap, convertWidth, convertHeight, true);
						image_bitmap.compress(CompressFormat.JPEG, 70, out);
						image_bitmap = Bitmap.createBitmap(image_bitmap, 0, convertHeight / 4, photoWidth, photoHeight);

						// 배치해놓은 ImageView에 set
						scheduleIv.setImageBitmap(image_bitmap);

					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			}
		}

	}

	/**
	 * 카메라에서 이미지 가져오기
	 */
	private void doTakePhotoAction() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// 임시로 사용할 파일의 경로를 생성
		String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
		file = new File(Environment.getExternalStorageDirectory(), url);
		mImageCaptureUri = Uri.fromFile(file);
		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
		startActivityForResult(intent, PICK_FROM_CAMERA);
	}

	/**
	 * 앨범에서 이미지 가져오기
	 */
	private void doTakeAlbumAction() {
		// 앨범 호출
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
		startActivityForResult(intent, PICK_FROM_ALBUM);
	}

	public void registSchedule(RequestParams params) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(MonoURL.SERVER_URL + MonoURL.REGIST_SCHEDULE, params, new AsyncHttpResponseHandler() {
			String response = null;
			JSONObject obj = null;

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				try {
					response = new String(arg2, "UTF-8");
					obj = new JSONObject(response);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					String status = obj.getString("status");

					if (status.equals("success")) {
						Toast.makeText(getApplicationContext(), "일정 생성 성공", Toast.LENGTH_LONG).show();

						if (scheduleIv.getDrawable() != null) {
							String urlString = MonoURL.SERVER_URL + MonoURL.SCHEDULE_PHOTO_UPLOAD;
							String absolutePath = file.toString();
							DoFileUpload(urlString, obj.getInt("no"), absolutePath);
						}
						Intent intent_0 = new Intent(MakeScheduleActivity.this, MainActivity.class);
						intent_0.putExtra("menu", 0);
						startActivity(intent_0);
						Intent intent = getIntent();
						setResult(RESULT_OK, intent);
						finish();
					} else {
						Toast.makeText(getApplicationContext(), "일정 생성 실패", Toast.LENGTH_LONG).show();
					}

				} catch (JSONException e) {
					e.printStackTrace();

				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
			}
		});
	}

	String dateTimeParcer(int data) {
		String parcedData = null;

		if (data / 10 < 1) {
			parcedData = "0" + String.valueOf(data);
		} else {
			parcedData = String.valueOf(data);
		}

		return parcedData;

	}

	public void DoFileUpload(String apiUrl, int scheduleNo, String absolutePath) {
		HttpFileUpload(apiUrl, scheduleNo, absolutePath);
	}

	public void HttpFileUpload(String urlString, int scheduleNo, String fileName) {
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
				params.put("no", scheduleNo);
				saveImage(params);
			}

		} catch (Exception e) {
			Log.d("Test", "exception " + e.getMessage());
		}
	}

	public void saveImage(RequestParams params) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(MonoURL.SERVER_URL + MonoURL.SCHEDULE_PHOTO_SAVE, params, new AsyncHttpResponseHandler() {
			String response = null;
			JSONObject obj = null;

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				try {
					response = new String(arg2, "UTF-8");
					obj = new JSONObject(response);

					if (obj.getString("stauts").equals("success")) {
						File f = new File(mImageCaptureUri.getPath());
						if (f.exists()) {
							f.delete();
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

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.btn_schedule_start:
			final View startDialogView = View.inflate(MakeScheduleActivity.this, R.layout.date_time_picker, null);

			startDialogView.findViewById(R.id.frame_layout_date_btn).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startDialogView.findViewById(R.id.frame_layout_date).setVisibility(LinearLayout.VISIBLE);
					startDialogView.findViewById(R.id.frame_layout_time).setVisibility(LinearLayout.INVISIBLE);
				}
			});

			startDialogView.findViewById(R.id.frame_layout_time_btn).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startDialogView.findViewById(R.id.frame_layout_date).setVisibility(LinearLayout.INVISIBLE);
					startDialogView.findViewById(R.id.frame_layout_time).setVisibility(LinearLayout.VISIBLE);
				}
			});

			final AlertDialog alertDialog = new AlertDialog.Builder(MakeScheduleActivity.this).create();

			startDialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {

					DatePicker datePicker = (DatePicker) startDialogView.findViewById(R.id.date_picker);
					TimePicker timePicker = (TimePicker) startDialogView.findViewById(R.id.time_picker);

					Calendar calendar = new GregorianCalendar(datePicker.getYear(),

							datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
							timePicker.getCurrentMinute());

					int year = datePicker.getYear();
					int month = datePicker.getMonth() + 1;
					int day = datePicker.getDayOfMonth();

					int hour = timePicker.getCurrentHour();
					int minute = timePicker.getCurrentMinute();

					String parcedMonth = dateTimeParcer(month);
					String parcedDay = dateTimeParcer(day);
					String parcedHour = dateTimeParcer(hour);
					String parcedMinute = dateTimeParcer(minute);

					startDate = String.valueOf(year) + "-" + parcedMonth + "-" + parcedDay + " " + parcedHour + ":"
							+ parcedMinute;
					scheduleStartTime.setTextSize(14);
					scheduleStartTime.setTextColor(Color.parseColor("#6D6E70"));

					if (hour > 12) {
						hour -= 12;
						scheduleStartTime.setText(
								year + "년 " + parcedMonth + "/" + parcedDay + " PM" + parcedHour + ":" + parcedMinute);
					} else if (hour == 12) {
						scheduleStartTime.setText(
								year + "년 " + parcedMonth + "/" + parcedDay + " PM" + parcedHour + ":" + parcedMinute);
					} else if (hour == 24) {
						scheduleStartTime.setText(
								year + "년 " + parcedMonth + "/" + parcedDay + " AM" + parcedHour + ":" + parcedMinute);
					}

					else {
						scheduleStartTime.setText(
								year + "년 " + parcedMonth + "/" + parcedDay + " AM" + parcedHour + ":" + parcedMinute);
					}

					alertDialog.dismiss();
				}
			});

			alertDialog.setView(startDialogView);
			alertDialog.show();
			break;

		case R.id.btn_schedule_end:
			final View endDialogView = View.inflate(MakeScheduleActivity.this, R.layout.date_time_picker, null);

			endDialogView.findViewById(R.id.frame_layout_date_btn).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					endDialogView.findViewById(R.id.frame_layout_date).setVisibility(LinearLayout.VISIBLE);
					endDialogView.findViewById(R.id.frame_layout_time).setVisibility(LinearLayout.GONE);
				}
			});

			endDialogView.findViewById(R.id.frame_layout_time_btn).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					endDialogView.findViewById(R.id.frame_layout_date).setVisibility(LinearLayout.GONE);
					endDialogView.findViewById(R.id.frame_layout_time).setVisibility(LinearLayout.VISIBLE);
				}
			});

			final AlertDialog alertDialog1 = new AlertDialog.Builder(MakeScheduleActivity.this).create();

			endDialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {

					DatePicker datePicker = (DatePicker) endDialogView.findViewById(R.id.date_picker);
					TimePicker timePicker = (TimePicker) endDialogView.findViewById(R.id.time_picker);

					Calendar calendar = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(),
							datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute());

					int year = datePicker.getYear();
					int month = datePicker.getMonth() + 1;
					int day = datePicker.getDayOfMonth();

					int hour = timePicker.getCurrentHour();
					int minute = timePicker.getCurrentMinute();

					long endTime = calendar.getTimeInMillis();
					String parcedMonth = dateTimeParcer(month);
					String parcedDay = dateTimeParcer(day);
					String parcedHour = dateTimeParcer(hour);
					String parcedMinute = dateTimeParcer(minute);

					endDate = String.valueOf(year) + "-" + parcedMonth + "-" + parcedDay + " " + parcedHour + ":"
							+ parcedMinute;
					scheduleEndTime.setTextSize(14);
					scheduleEndTime.setTextColor(Color.parseColor("#6D6E70"));

					if (hour > 12) {
						hour -= 12;
						scheduleEndTime.setText(
								year + "년 " + parcedMonth + "/" + parcedDay + " PM" + parcedHour + ":" + parcedMinute);
					} else if (hour == 12) {
						scheduleEndTime.setText(
								year + "년 " + parcedMonth + "/" + parcedDay + " PM" + parcedHour + ":" + parcedMinute);
					} else if (hour == 24) {
						scheduleEndTime.setText(
								year + "년 " + parcedMonth + "/" + parcedDay + " AM" + parcedHour + ":" + parcedMinute);
					}

					else {
						scheduleEndTime.setText(
								year + "년 " + parcedMonth + "/" + parcedDay + " AM" + parcedHour + ":" + parcedMinute);
					}
					alertDialog1.dismiss();
				}
			});
			alertDialog1.setView(endDialogView);
			alertDialog1.show();
			break;

		case R.id.btn_schedule_place:
			Intent selectPlaceActivity = new Intent(MakeScheduleActivity.this, SelectPlaceActivity.class);
			startActivityForResult(selectPlaceActivity, 1);
			break;

		case R.id.btn_schedule_friend:
			Intent selectFriendActivity = new Intent(MakeScheduleActivity.this, SelectFriendActivity.class);
			startActivityForResult(selectFriendActivity, 2);
			break;

		case R.id.bt_make_schedule:
			if (scheduleStartTime.getText().equals("시작시간")) {
				Toast.makeText(getApplicationContext(), "시작 시간을 입력하세요.", Toast.LENGTH_LONG).show();
				break;
			} else if (scheduleEndTime.getText().equals("종료시간")) {
				Toast.makeText(getApplicationContext(), "종료 시간을 입력하세요.", Toast.LENGTH_LONG).show();
				break;
			} else if (selectPlaceButton.getText().equals("장소를 입력해 주세요")) {
				Toast.makeText(getApplicationContext(), "장소를 선택해주세요.", Toast.LENGTH_LONG).show();
				break;
			} else {
				RequestParams params = new RequestParams();
				String email = MainActivity.sharedpreferences.getString(Utility.Email, "");
				String name = scheduleName.getText().toString();
				String startTime = startDate;
				String endTime = endDate;
				String place = selectPlaceButton.getText().toString();

				String memo = scheduleMemo.getText().toString();
				String tag = "";
				StringTokenizer st = new StringTokenizer(memo, " ");
				while (st.hasMoreTokens()) {
					String value = st.nextToken();
					if (value.startsWith("#")) {
						tag = tag + value;
					}
				}

				String sLongitude = String.valueOf(longitude);
				String sLatitude = String.valueOf(latitude);
				String status = "host";

				params.add("email", email);
				params.add("name", name);
				params.add("startTime", startTime);
				params.add("endTime", endTime);
				params.add("place", place);
				params.add("detailPlace", detailPlace);
				params.add("longitude", sLongitude);
				params.add("latitude", sLatitude);
				params.add("memo", memo);
				params.add("tag", tag);
				params.add("status", status);
				Gson gson = new Gson();
				String out = gson.toJson(selectedFriend);
				params.put("members", out);
				registSchedule(params);
				break;

			}

		case R.id.schedule_photo:
			DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					doTakePhotoAction();
				}
			};

			DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					doTakeAlbumAction();
				}
			};

			DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			};

			new AlertDialog.Builder(this).setTitle("업로드할 이미지 선택").setPositiveButton("사진촬영", cameraListener)
					.setNeutralButton("앨범선택", albumListener).setNegativeButton("취소", cancelListener).show();
		}

	}

}
