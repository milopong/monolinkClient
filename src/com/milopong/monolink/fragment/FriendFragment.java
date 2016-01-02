package com.milopong.monolink.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.milopong.monolink.R;
import com.milopong.monolink.activity.MainActivity;
import com.milopong.monolink.activity.MakeScheduleActivity;
import com.milopong.monolink.ui.capricorn.ArcMenu;

public class FriendFragment extends Fragment implements OnClickListener {
	private Fragment mContent;
	private ArcMenu arcMenu;
	private Button registBtn;
	private static final int[] ITEM_DRAWABLES = { R.drawable.home_detail_list,
			R.drawable.home_detail_favorite };
	ShareScheduleFragment shareScheduleFragment;
	FriendListFragment friendListFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mContent = getChildFragmentManager().getFragment(
					savedInstanceState, "mContent");
		}
		if (mContent == null){
			mContent = new ShareScheduleFragment();
			shareScheduleFragment = (ShareScheduleFragment) mContent;
		}
		View v = inflater.inflate(R.layout.fragment_friend, null);

		getChildFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mContent).commit();
		

		return v;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		arcMenu = (ArcMenu) getView().findViewById(R.id.arc_menu);
		arcMenu.mHintView.setImageResource(R.drawable.home_favorite);
		initArcMenu(arcMenu, ITEM_DRAWABLES);
		arcMenu.post(new Runnable() {
			@Override
			public void run() {
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
						arcMenu.getLayoutParams());
				params.addRule(RelativeLayout.ALIGN_PARENT_END);
				params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				int widthMargin = (int) (arcMenu.getWidth() / -4.5f);
				int widthHeight = (int) (arcMenu.getHeight() / 4f);
				params.setMargins(0, 0, widthMargin, widthHeight);
				arcMenu.setLayoutParams(params);
			}
		});
		registBtn = (Button) getView().findViewById(R.id.registBtn);
		registBtn.setOnClickListener(this);
	}
	public void findText(String content, String title){
		if(title.equals("friendList")){
			friendListFragment.findFriend(content);
		}else if(title.equals("shareSchedule")){
			shareScheduleFragment.findSchedule(content);
		}
		
		
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
						switchContent("friendList");
						arcMenu.mHintView.setImageResource(R.drawable.home_list);
						break;
					case 1:
						switchContent("shareSchedule");
						arcMenu.mHintView.setImageResource(R.drawable.home_favorite);
						break;
					}
				}
			});
		}
	}

	public void switchContent(String title) {
		if (title.equals("friendList")) {
			friendListFragment = new FriendListFragment();
			getChildFragmentManager().beginTransaction()
					.replace(R.id.content_frame, friendListFragment).commit();
			if (getActivity() == null)
				return;
			
			if (getActivity() instanceof MainActivity) {
				MainActivity fca = (MainActivity) getActivity();
				fca.setActionBarContent("friendList",this);
			}
		} else if (title.equals("shareSchedule")) {
			shareScheduleFragment = new ShareScheduleFragment();
			getChildFragmentManager().beginTransaction()
					.replace(R.id.content_frame, shareScheduleFragment)
					.commit();
			if (getActivity() == null)
				return;
			if (getActivity() instanceof MainActivity) {
				MainActivity fca = (MainActivity) getActivity();
				fca.setActionBarContent("shareSchedule",this);
			}
		}
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
