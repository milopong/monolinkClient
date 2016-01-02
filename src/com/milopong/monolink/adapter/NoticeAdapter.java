package com.milopong.monolink.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.milopong.monolink.R;
import com.milopong.monolink.activity.NoticeInfoActivity;
import com.milopong.monolink.model.Notice;

public class NoticeAdapter extends BaseAdapter {
	private List<Notice> noticeList;
	
	public NoticeAdapter() {
		this.noticeList = new ArrayList<Notice>();
	}


	@Override
	public int getCount() {
		return noticeList.size();
	}

	@Override
	public Notice getItem(int position) {
		return noticeList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int pos = position;
		final Context context = parent.getContext();

		TextView titleTv = null;

		CustomHolder holder = null;

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.layout_notice,
					parent, false);
			
			titleTv = (TextView) convertView.findViewById(R.id.title_tv);
			holder = new CustomHolder();
			holder.titleTv = titleTv;

			convertView.setTag(holder);
		} else {
			holder = (CustomHolder) convertView.getTag();
			titleTv = holder.titleTv;
		}


		titleTv.setText(noticeList.get(position).getName());
		

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intentNoticeInfoActivity = new Intent(v.getContext(), NoticeInfoActivity.class);
				intentNoticeInfoActivity.putExtra("url", getItem(pos).getUrl());
				v.getContext().startActivity(intentNoticeInfoActivity);
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
	
	public void add(Notice aNotice){
		Notice notice = new Notice();
		notice.setNo(aNotice.getNo());
		notice.setName(aNotice.getName());
		notice.setUrl(aNotice.getUrl());
		
		noticeList.add(notice);
	}



	private class CustomHolder {
		TextView titleTv;
	}

}
