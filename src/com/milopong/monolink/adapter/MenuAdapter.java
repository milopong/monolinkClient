package com.milopong.monolink.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.milopong.monolink.R;
import com.milopong.monolink.model.Menu;

public class MenuAdapter extends ArrayAdapter<Menu> {

	public MenuAdapter(Context context) {
		super(context, 0);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.row, null);
		}
		ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
		icon.setBackgroundResource(getItem(position).iconRes);

		return convertView;
	}
}
