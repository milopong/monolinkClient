package com.milopong.monolink.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.milopong.monolink.R;
import com.milopong.monolink.activity.MakeScheduleActivity;
import com.milopong.monolink.model.Schedule;
import com.milopong.monolink.ui.capricorn.ArcMenu;
import com.milopong.monolink.utils.Utility.Mode;

public class HomeFragment extends Fragment implements OnClickListener {
	private Fragment mContent;
	private ArcMenu arcMenu;
	private Button registBtn;
	private ArrayList<Schedule> schedules;
	private static final int[] ITEM_DRAWABLES = { R.drawable.home_detail_list, R.drawable.home_detail_favorite,
			R.drawable.home_detail_calendar };
	private ScheduleListFragment scheduleListFragment;

	public HomeFragment(ArrayList<Schedule> schedules) {
		this.schedules = new ArrayList<Schedule>();
		this.schedules = schedules;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mContent = getChildFragmentManager().getFragment(savedInstanceState, "mContent");
		}

		if (mContent == null) {
			mContent = new ScheduleListFragment(Mode.LIST);
			scheduleListFragment = (ScheduleListFragment) mContent;
		}
		View v = inflater.inflate(R.layout.fragment_base, null);

		getChildFragmentManager().beginTransaction().replace(R.id.content_frame, mContent).commit();

		return v;

	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		arcMenu = (ArcMenu) getView().findViewById(R.id.arc_menu);
		initArcMenu(arcMenu, ITEM_DRAWABLES);
		arcMenu.post(new Runnable() {
			@Override
			public void run() {
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(arcMenu.getLayoutParams());
				params.addRule(RelativeLayout.ALIGN_PARENT_END);
				params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				int widthMargin = (int) (arcMenu.getWidth() / -4f);
				int widthHeight = (int) (arcMenu.getHeight() / 5f);
				params.setMargins(0, 0, widthMargin, widthHeight);
				arcMenu.setLayoutParams(params);
			}
		});

		registBtn = (Button) getView().findViewById(R.id.registBtn);
		registBtn.setOnClickListener(this);
	}

	

	private void initArcMenu(ArcMenu menu, int[] itemDrawables) {
		final int itemCount = itemDrawables.length;
		for (int i = 0; i < itemCount; i++) {
			ImageView item = new ImageView(getActivity());
			item.setImageResource(itemDrawables[i]);
			final int position = i;

			menu.addItem(item, new OnClickListener() {

				@Override
				public void onClick(View v) {
					switch (position) {
					case 0:
						switchContent("list");
						arcMenu.mHintView.setImageResource(R.drawable.home_list);
						break;
					case 1:
						switchContent("favorite");
						arcMenu.mHintView.setImageResource(R.drawable.home_favorite);
						break;
					case 2:
						switchContent("calendar");
						arcMenu.mHintView.setImageResource(R.drawable.home_calendar);
						break;
					}
				}
			});
		}
	}

	public void switchContent(String title) {
		if (title.equals("list")) {
			scheduleListFragment = new ScheduleListFragment(Mode.LIST);
			getChildFragmentManager().beginTransaction().replace(R.id.content_frame, scheduleListFragment).commit();
		} else if (title.equals("favorite")) {
			scheduleListFragment = new ScheduleListFragment(Mode.FAVORITE);
			getChildFragmentManager().beginTransaction().replace(R.id.content_frame, scheduleListFragment).commit();
		} else if (title.equals("calendar")) {
			CalendarFragment calendarFragment = new CalendarFragment(1, schedules);
			getChildFragmentManager().beginTransaction().replace(R.id.content_frame, calendarFragment).commit();
		}
	}
	public void findText(String content) {
		Log.d("aa", "home:" + content);
		scheduleListFragment.findSchedule(content);

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

}
