package com.milopong.monolink.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.milopong.monolink.R;
import com.milopong.monolink.activity.MakeScheduleActivity;
import com.milopong.monolink.model.Notification;

public class NotificationFragment extends Fragment implements OnClickListener {
	private Fragment mContent;
	private Button registBtn;
	private ArrayList<Notification> notifications;

	public NotificationFragment(ArrayList<Notification> notifications) {
		this.notifications = new ArrayList<Notification>();
		this.notifications = notifications;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mContent = getChildFragmentManager().getFragment(
					savedInstanceState, "mContent");
		}

		if (mContent == null)
			mContent = new NotificationListFragment(notifications);
		View v = inflater.inflate(R.layout.fragment_notification, null);

		getChildFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mContent).commit();

		return v;

	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registBtn = (Button) getView().findViewById(R.id.registBtn);
		registBtn.setOnClickListener(this);
	}

	
	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.registBtn:
			Intent IntentMakeSchedule = new Intent(getActivity(),
					MakeScheduleActivity.class);
			startActivityForResult(IntentMakeSchedule, 1);
			break;
		}

	}

}
