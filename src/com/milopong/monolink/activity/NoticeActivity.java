package com.milopong.monolink.activity;

import com.milopong.monolink.R;
import com.milopong.monolink.adapter.NoticeAdapter;
import com.milopong.monolink.model.Notice;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

public class NoticeActivity extends ActionBarActivity implements OnClickListener {

	private ListView notice_ListView;
	private NoticeAdapter notice_Adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		notice_ListView = (ListView) findViewById(R.id.list_view);
		notice_Adapter = new NoticeAdapter();
		notice_ListView.setAdapter(notice_Adapter);
		addList();

	}

	public void addList() {
		Notice notice = new Notice();
		notice.setNo(1);
		notice.setName("1차 업데이트 알림");
		notice.setUrl("1");
		notice_Adapter.add(notice);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		}

	}

}
