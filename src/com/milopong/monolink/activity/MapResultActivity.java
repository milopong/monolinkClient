package com.milopong.monolink.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import com.milopong.monolink.R;
import com.milopong.monolink.adapter.MapResultAdapter;
import com.milopong.monolink.adapter.NoticeAdapter;
import com.milopong.monolink.model.MapItem;
import com.milopong.monolink.model.Notice;

public class MapResultActivity extends Activity implements OnClickListener {

	private Button backBtn;
	private ListView mapResult_ListView;
	private MapResultAdapter mapResult_Adapter;
	private Intent intent;
	private ArrayList<MapItem> mapResults;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_map_result);

		backBtn = (Button) findViewById(R.id.backBtn);
		backBtn.setOnClickListener(this);
		intent = getIntent();
		mapResults = new ArrayList<MapItem>();

		mapResults = (ArrayList<MapItem>) intent
				.getSerializableExtra("mapResults");
		
		String searchText = intent.getStringExtra("searchText");
		backBtn.setText(searchText);

		mapResult_ListView = (ListView) findViewById(R.id.list_view);
		mapResult_Adapter = new MapResultAdapter(mapResults);
		mapResult_ListView.setAdapter(mapResult_Adapter);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.backBtn:
			finish();
			break;
		}

	}

}
