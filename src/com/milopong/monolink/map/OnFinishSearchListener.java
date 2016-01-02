package com.milopong.monolink.map;

import java.util.List;

import com.milopong.monolink.model.MapItem;


public interface OnFinishSearchListener {
	public void onSuccess(List<MapItem> itemList);
	public void onFail();
}
