package com.xingyeda.ehome.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.xingyeda.ehome.R;
import com.xingyeda.ehome.door.ActivityAddAddress;
import com.xingyeda.ehome.door.ActivityXiaoquSeek;

public class PriorityDialog extends Dialog {
	
	private Context mContext;
	private List<String> mData;
	private GridView mDlg_Grid = null;
	private int mPosition;
	private Button mButton;
	private String mType;

	public PriorityDialog(Context context) {
		super(context);
		mContext = context;
	}
	
	public PriorityDialog(Context context, int theme, List<String> list, String str) {
		super(context, theme);
		this.mContext = context;
		this.mData = list;
		mType = str;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.priority_dialog);
		mDlg_Grid = (GridView) findViewById(R.id.dialog_gridview);
		mButton = (Button) findViewById(R.id.dialog_button);
		if (mType.equals("xiaoqu")) {
			mButton.setVisibility(View.VISIBLE);	
		}
		if (null!=mData && !mData.isEmpty()) {
			if (mData.size()<4) {
				mDlg_Grid.setNumColumns(mData.size());
			}
			else {
				mDlg_Grid.setNumColumns(4);
			}
		}
		
		SimpleAdapter adapter = new SimpleAdapter(mContext,
				getPriorityList(mData), //数据源
				R.layout.dialog_grid_item,
				new String[] { "list_value" },
				new int[] { R.id.item_text });
		mDlg_Grid.setAdapter(adapter);
		mDlg_Grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
//						Toast.makeText(context, "" + arg2, Toast.LENGTH_SHORT).show();// 显示信息;
						mPosition = arg2;
						PriorityDialog.this.dismiss();

					}
				});
		mButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((Activity) mContext).startActivityForResult(new Intent(mContext,ActivityXiaoquSeek.class), ActivityAddAddress.REQUEST_CODE);
				PriorityDialog.this.dismiss();
			}
		});
	}
		public int getPosition() {
			return mPosition;
		}
		
		private List<HashMap<String, Object>> getPriorityList(List<String> list) {
			List<HashMap<String, Object>> priorityList = new ArrayList<HashMap<String, Object>>();
			for (int i = 0; i < list.size(); i++) {
				HashMap<String, Object> a = new HashMap<String, Object>();
				a.put("list_value", list.get(i));
				priorityList.add(a);
			}
			return priorityList;
		}
	

}
