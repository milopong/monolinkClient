package com.milopong.monolink.utils;

import com.milopong.monolink.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class ImageOption {
	
	public static final DisplayImageOptions options = new DisplayImageOptions.Builder()
	.showImageOnLoading(R.drawable.default_profile)
	.showImageForEmptyUri(R.drawable.default_profile)
	.showImageOnFail(R.drawable.default_profile).cacheInMemory(true)
	.cacheOnDisk(true)
	.considerExifParams(true).build();
}
