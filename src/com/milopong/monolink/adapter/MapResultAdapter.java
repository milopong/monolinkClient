package com.milopong.monolink.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
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
import com.milopong.monolink.activity.MapResultActivity;
import com.milopong.monolink.model.MapItem;

public class MapResultAdapter extends BaseAdapter {
	private List<MapItem> mapResults;
	
	public MapResultAdapter(ArrayList<MapItem> mapItems) {
		this.mapResults = new ArrayList<MapItem>();
		this.mapResults =mapItems;
	}


	@Override
	public int getCount() {
		return mapResults.size();
	}

	@Override
	public MapItem getItem(int position) {
		return mapResults.get(position);
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
		TextView addressTv = null;

		CustomHolder holder = null;

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.layout_map_result,
					parent, false);
			
			titleTv = (TextView) convertView.findViewById(R.id.title_tv);
			addressTv =(TextView) convertView.findViewById(R.id.address_tv);
			holder = new CustomHolder();
			holder.titleTv = titleTv;
			holder.addressTv = addressTv;
			convertView.setTag(holder);
		} else {
			holder = (CustomHolder) convertView.getTag();
			titleTv = holder.titleTv;
			addressTv = holder.addressTv;
		}


		titleTv.setText(mapResults.get(position).getTitle());
		addressTv.setText(mapResults.get(position).getAddress());
		

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intentMapResultActivity = new Intent(v.getContext(), MapResultActivity.class);
				intentMapResultActivity.putExtra("result",mapResults.get(pos));
				((Activity)context).setResult(((Activity)context).RESULT_OK, intentMapResultActivity);
				((Activity)context).finish();
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
	

	private class CustomHolder {
		TextView titleTv;
		TextView addressTv;
	}

}
