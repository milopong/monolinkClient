package com.milopong.monolink.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.milopong.monolink.R;
import com.milopong.monolink.utils.MonoURL;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class NoticeInfoActivity extends ActionBarActivity implements OnClickListener {

	private ImageView noticeIv;
	private DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice_info);
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs().build();
		ImageLoader.getInstance().init(config);
		

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_profile)
				.showImageForEmptyUri(R.drawable.default_profile)
				.showImageOnFail(R.drawable.default_profile)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.build();

		noticeIv = (ImageView) findViewById(R.id.noticeIv);

		Intent intent = getIntent();
		String url = intent.getExtras().getString("url");

		String notice =MonoURL.SERVER_URL + "images/notice/" + url + ".png";
		
		//MemoryCacheUtils.removeFromCache(notice, ImageLoader.getInstance().getMemoryCache());
		//DiskCacheUtils.removeFromCache(notice, ImageLoader.getInstance().getDiskCache());
		ImageLoader.getInstance().displayImage(notice, noticeIv, options);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		}

	}
}
