package com.xingyeda.ehome.door;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.xingyeda.ehome.R;
import com.xingyeda.ehome.adapter.SeekAdapter;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.bean.Xiaoqu;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;

public class ActivityXiaoquSeek extends BaseActivity {
	@Bind(R.id.seek_back)
	TextView mBack;
	@Bind(R.id.seek_list)
	ListView mDataList;
	@Bind(R.id.edit_search)
	EditText mSearch;

	private List<Xiaoqu> mDatas;
	private List<Xiaoqu> mDatasSet;

	public final static int RESULT_CODE = 200;
	private SeekAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_xiaoqu_seek);
		ButterKnife.bind(this);
		mDatas = new ArrayList<Xiaoqu>();
		init();
		editSearch();
	}

	private void init() {
		OkHttp.get(mContext,ConnectPath.XIAOQU_PATH, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONArray xiaoqu_list = (JSONArray) response
							.get("obj");
					if (xiaoqu_list != null
							&& xiaoqu_list.length() != 0) {
						for (int i = 0; i < xiaoqu_list.length(); i++) {
							JSONObject jobj = xiaoqu_list
									.getJSONObject(i);
							Xiaoqu xiaoqu = new Xiaoqu();
							xiaoqu.setmId(jobj.has("id") ? jobj
									.getString("id") : "");
							xiaoqu.setmName(jobj.has("name") ? jobj
									.getString("name") : "");
							mDatas.add(xiaoqu);
						}
							mDatasSet = mDatas;

					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}));

		mDataList.setOnItemClickListener(itemClickListener);
	}

	@OnClick({ R.id.seek_back })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.seek_back:
			ActivityXiaoquSeek.this.finish();
			break;
		}
	}

	private void editSearch() {
		mSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable str) {
				String strs = str.toString();
				if (mSearch.getText().length() == 0) {
					mDatasSet = mDatas;
//					mAdapter = new SeekAdapter(mContext, mDatasSet);
//					mDataList.setAdapter(mAdapter);
				} else {
					ArrayList<Xiaoqu> list_is = new ArrayList<Xiaoqu>();
					for (int i = 0; i < mDatas.size(); i++) {
						if (mDatas.get(i).getmName().contains(strs)) {
							list_is.add(mDatas.get(i));
						}
					}
					mDatasSet = list_is;
					mAdapter = new SeekAdapter(mContext, mDatasSet);
					mDataList.setAdapter(mAdapter);
				}
			}
		});
	}

	private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			if (mDatasSet!=null) {
				
			Intent intent = new Intent();
			intent.putExtra("id", mDatasSet.get(position).getmId());
			intent.putExtra("name", mDatasSet.get(position).getmName());
			setResult(RESULT_CODE, intent);
			finish();
			}

		}
	};
}
