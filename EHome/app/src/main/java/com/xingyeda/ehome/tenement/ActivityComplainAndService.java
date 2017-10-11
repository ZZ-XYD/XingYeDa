package com.xingyeda.ehome.tenement;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import butterknife.Bind;
import okhttp3.Call;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.ldl.dialogshow.dialog.entity.DialogMenuItem;
import com.ldl.dialogshow.dialog.listener.OnBtnClickL;
import com.ldl.dialogshow.dialog.listener.OnOperItemClickL;
import com.ldl.dialogshow.dialog.widget.NormalDialog;
import com.ldl.dialogshow.dialog.widget.NormalListDialog;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.util.BaseUtils;
import com.ldl.okhttp.OkHttpUtils;
import com.ldl.okhttp.builder.PostFormBuilder;
import com.ldl.okhttp.callback.StringCallback;

/**
 * @ClassName: ActivityComplainAndService
 * @Description: 添加建议和保修界面
 * @author 李达龙
 * @date 2016-7-6
 */
@SuppressLint("HandlerLeak")
public class ActivityComplainAndService extends BaseActivity {
	private static final int TYPES = 1;

	@Bind(R.id.RT_cas_spin)
	Spinner mSpinType;
	@Bind(R.id.RT_cas_content)
	EditText mAdd_Content;
	@Bind(R.id.RT_cas_submit)
	Button mBut_Submit;
	@Bind(R.id.RT_cas_title)
	TextView mTitle;
	@Bind(R.id.RT_cas_history)
	TextView mHistory;
	@Bind(R.id.RT_cas_title_edit)
	EditText mEditTitle;

	@Bind(R.id.RT_cas_back)
	TextView mBack;
	@Bind(R.id.RT_loading)
	View mProgressBar;
	@Bind(R.id.RT_cas_gridView)
	GridView mGvImage;

	private List<String> mType;
	// 下拉框适配器
	private ArrayAdapter<String> mTypeAdapter;

	private final int IMAGE_OPEN = 1;
	private final int IMAGE_CAMERA = 2;
	private String pathImage; // 选择图片路径
	private Bitmap bmp; // 导入临时图片
	private ArrayList<HashMap<String, Object>> imageItem;
	private SimpleAdapter simpleAdapter; // 适配器
	// 上传图片的file
	private List<File> mImageFile;

	private static String TYPE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.complain_and_service);
		ButterKnife.bind(this);

		this.init();
	}

	@SuppressWarnings("static-access")
	private void init() {
		this.TYPE = getIntent().getExtras().getString("type");
		this.mImageFile = new ArrayList<File>();


		if (TYPE.equals("tousu")) {
			mTitle.setText(R.string.suggested);
			mHistory.setText(R.string.suggested_record);
		} else if (TYPE.equals("weixiutype")) {
			mTitle.setText(R.string.maintain);
			mHistory.setText(R.string.maintain_record);
		}

		bmp = BitmapFactory.decodeResource(getResources(),
				R.mipmap.gridview_addpic); // 加号
		imageItem = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("itemImage", bmp);
		imageItem.add(map);
		simpleAdapter = new SimpleAdapter(this, imageItem,
				R.layout.griditem_addpic, new String[] { "itemImage" },
				new int[] { R.id.imageView1 });
		simpleAdapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				if (view instanceof ImageView && data instanceof Bitmap) {
					ImageView i = (ImageView) view;
					i.setImageBitmap((Bitmap) data);
					return true;
				}
				return false;
			}
		});
		mGvImage.setAdapter(simpleAdapter);

		mGvImage.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				if (imageItem.size() == 6) { // 第一张为默认图片
					if (position == 0) {
						BaseUtils.showShortToast(mContext,
								R.string.picture_for_5);
					} else {
						dialog(position);
					}

				} else if (position == 0) { // 点击图片位置为+ 0对应0张图片
//					final String[] items = new String[] { "从相册选择", "拍照" };
					ArrayList<DialogMenuItem> list = new ArrayList<DialogMenuItem>();
					list.add(new DialogMenuItem("从相册选择", R.mipmap.select_image));
					list.add(new DialogMenuItem("拍照", R.mipmap.photograph));
					final NormalListDialog dialog = DialogShow.showListDialog(mContext, list);
					dialog.itemTextSize(18).setOnOperItemClickL(new OnOperItemClickL() {
						
						@Override
						public void onOperItemClick(AdapterView<?> parent, View view, int position,
								long id) {
							Intent intent;
							switch (position) {
							case 0:
								// 选择图片
								intent = new Intent(
										Intent.ACTION_PICK,
										MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
								startActivityForResult(intent,
										IMAGE_OPEN);
								// 通过onResume()刷新数据
								break;
							case 1:
								intent = new Intent(
										MediaStore.ACTION_IMAGE_CAPTURE);
								startActivityForResult(intent,
										IMAGE_CAMERA);
								break;
							}
							dialog.dismiss();
						}
					});
				} else {
					dialog(position);
				}

			}
		});
	}

	// 获取图片路径 响应startActivityForResult
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 打开图片
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case IMAGE_OPEN:
				Uri uri = data.getData();
				if (!TextUtils.isEmpty(uri.getAuthority())) {
					// 查询选择图片
					Cursor cursor = getContentResolver().query(uri,
							new String[] { MediaStore.Images.Media.DATA },
							null, null, null);
					// 返回 没找到选择图片
					if (null == cursor) {
						return;
					}
					// 光标移动至开头 获取图片路径
					cursor.moveToFirst();
					pathImage = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					File file = new File(pathImage);
					mImageFile.add(file.getAbsoluteFile());
				}
				break;

			case IMAGE_CAMERA:
				Bundle bundle = data.getExtras();
				Bitmap bitmap = (Bitmap) bundle.get("data");
				File file = new File(Environment.getExternalStorageDirectory(),
						System.currentTimeMillis() + ".jpg");
				try {
					BufferedOutputStream bos = new BufferedOutputStream(
							new FileOutputStream(file));
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
					bos.flush();
					bos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				// mUpload.setImageBitmap(bitmap);
				mImageFile.add(file.getAbsoluteFile());
				pathImage = file.getAbsoluteFile().toString();
				break;
			}

		} // end if 打开图片
	}

	/*
	 * Dialog对话框提示用户删除操作 position为删除图片位置
	 */
	@OnClick({ R.id.RT_cas_submit, R.id.RT_cas_back, R.id.RT_cas_history })
	public void onClick(View v) {
		String title = mEditTitle.getText().toString();
		String content = mAdd_Content.getText().toString();
		switch (v.getId()) {
		case R.id.RT_cas_submit:
			if (title == null || title.trim().equals("")) {
				DialogShow.showHintDialog(mContext, getResources().getString(R.string.RT_cas_title_hint));
			} else if (content == null || title.trim().equals("")) {
				DialogShow.showHintDialog(mContext, getResources().getString(R.string.input_content));
			} else {
				mProgressBar.setVisibility(View.VISIBLE);
				// 投诉
				if (TYPE.equals("tousu")) {
					upload(ConnectPath.ADDTOUSU_PATH, title, content);
				} else if (TYPE.equals("weixiutype")) {
					upload(ConnectPath.ADDWEIXIU_PATH, title, content);
				}
			}
			break;
		case R.id.RT_cas_back:
			ActivityComplainAndService.this.finish();
			break;
		case R.id.RT_cas_history:
			Bundle bundle = new Bundle();
			bundle.putString("type", TYPE);
			BaseUtils.startActivities(ActivityComplainAndService.this, ActivityHistory.class, bundle);
			ActivityComplainAndService.this.finish();
			break;

		}
	}

	private Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TYPES:
				spinnerType((List<String>) msg.obj);
				break;
			}
		}

	};

	protected void spinnerType(List<String> list) {
		this.mTypeAdapter = new ArrayAdapter<String>(this,
				R.layout.spinner_add_item, list);
		mSpinType.setAdapter(mTypeAdapter);
		mSpinType.setSelection(0, true);
		mSpinType
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						// mKey =position+"";
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}

				});

	}

	@Override
	protected void onResume() {

		this.mType = new ArrayList<String>();

		typeDatas(TYPE);

		if (!TextUtils.isEmpty(pathImage)) {
			Bitmap addbmp = BitmapFactory.decodeFile(pathImage);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", addbmp);
			imageItem.add(map);
			simpleAdapter = new SimpleAdapter(this, imageItem,
					R.layout.griditem_addpic, new String[] { "itemImage" },
					new int[] { R.id.imageView1 });
			simpleAdapter.setViewBinder(new ViewBinder() {
				@Override
				public boolean setViewValue(View view, Object data,
						String textRepresentation) {
					if (view instanceof ImageView && data instanceof Bitmap) {
						ImageView i = (ImageView) view;
						i.setImageBitmap((Bitmap) data);
						return true;
					}
					return false;
				}
			});
			mGvImage.setAdapter(simpleAdapter);
			simpleAdapter.notifyDataSetChanged();
			// 刷新后释放防止手机休眠后自动添加
			pathImage = null;
		}
		super.onResume();
	}

	private void typeDatas(String type) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", type);
		OkHttp.get(mContext,ConnectPath.COMPLAINANDSERVICE_PATH, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
						JSONArray obj = (JSONArray) response.get("obj");
						if (obj != null && obj.length() != 0) {
							for (int i = 0; i < obj.length(); i++) {
								JSONObject jobj = obj.getJSONObject(i);
								mType.add(jobj.getString("strvalue"));
							}
						}
						Message msg = new Message();
						msg.what = TYPES;
						msg.obj = mType;
						mHandler.sendMessage(msg);
				} catch (JSONException e) {
					e.printStackTrace();
				}				
			}
		}));
		
	}

	protected void dialog(final int position) {
		
		final NormalDialog dialog = DialogShow.showSelectDialog(mContext, getResources().getString(R.string.remove_picture));
		dialog.setOnBtnClickL(new OnBtnClickL() {
			
			@Override
			public void onBtnClick() {
				dialog.dismiss();
			}
		},new OnBtnClickL() {
			
			@Override
			public void onBtnClick() {
				imageItem.remove(position);
				simpleAdapter.notifyDataSetChanged();				
				dialog.dismiss();
			}
		});
	}

	private void upload(String url, String title, String content) {
		 Map<String, String> params = new HashMap<String, String>();
		 params.put("uid", mEhomeApplication.getmCurrentUser().getmId());
		 params.put("title", title);
		 params.put("content", content);

		if (mImageFile.size() > 0) {
				PostFormBuilder postBuilder = OkHttpUtils.post();
				for (int i = 0; i < mImageFile.size(); i++) {
					File file = new File(mImageFile.get(i), "");
					if (!file.exists()) {
						Toast.makeText(mContext, "文件不存在，请修改文件路径",
								Toast.LENGTH_SHORT).show();
						return;
					}
					postBuilder.addFile("file"+i,getFileName(mImageFile.get(i).toString()), file);
				}
				postBuilder.url(url).params(params).build()
						.execute(new StringCallback() {
							@Override
							public void onError(Call call, Exception e,int id) {
								mProgressBar.setVisibility(View.GONE);
								BaseUtils.showShortToast(mContext,
										R.string.connection_timeout);
							}

							@Override
							public void onResponse(String response,int id) {
								BaseUtils.showShortToast(mContext,
										R.string.uploaded_successfully);
								mProgressBar.setVisibility(View.GONE);
								ActivityComplainAndService.this.finish();
							}
						});
		} else {
			OkHttpUtils
					.post()
					.url(url)
					.params(params)
					.build().execute(new StringCallback() {
						@Override
						public void onError(Call call, Exception e,int id) {
							mProgressBar.setVisibility(View.GONE);
							BaseUtils.showShortToast(mContext,
									R.string.connection_timeout);
						}

						@Override
						public void onResponse(String response,int id) {
							BaseUtils.showShortToast(mContext,
									R.string.uploaded_successfully);
							mProgressBar.setVisibility(View.GONE);
							ActivityComplainAndService.this.finish();
						}

					});
		}
	}

	public String getFileName(String pathandname) {

		int start = pathandname.lastIndexOf("/");
		if (start != -1) {
			return pathandname.substring(start + 1);
		} else {
			return null;
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ButterKnife.unbind(this);
	}


	private final static String ENCODE = "UTF-8";

	/**
	 * URL 转码
	 * 
	 * @return String
	 * @author lifq
	 * @date 2015-3-17 下午04:10:28
	 */
	public static String getURLEncoderString(String str) {
		String result = "";
		if (null == str) {
			return "";
		}
		try {
			result = java.net.URLEncoder.encode(str, ENCODE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

}
