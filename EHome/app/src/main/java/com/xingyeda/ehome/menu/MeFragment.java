package com.xingyeda.ehome.menu;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import butterknife.Bind;
import okhttp3.Call;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.ldl.dialogshow.dialog.entity.DialogMenuItem;
import com.ldl.dialogshow.dialog.listener.OnOperItemClickL;
import com.ldl.dialogshow.dialog.widget.NormalListDialog;
import com.ldl.imageloader.core.ImageLoader;
import com.ldl.imageloader.core.assist.FailReason;
import com.ldl.imageloader.core.listener.ImageLoadingListener;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.base.EHomeApplication;
import com.xingyeda.ehome.bean.PushBean;
import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.util.AppUtils;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.MyLog;
import com.xingyeda.ehome.view.MaskedImage;
import com.ldl.okhttp.OkHttpUtils;
import com.ldl.okhttp.callback.StringCallback;

import org.json.JSONObject;

import static com.xingyeda.ehome.base.BaseActivity.mEhomeApplication;

public class MeFragment extends Fragment {
	private View mView;
	@Bind(R.id.me_head)
	MaskedImage mHead;
	@Bind(R.id.me_user_name)
	TextView mName;
	@Bind(R.id.sn)
	TextView mSN;
	private Context mContext;
	private EHomeApplication mApplication;
	private Bitmap mBitmap;

//	private File mFile;
	private File tempFile;
	private File mImageFile;
	private static final int IMAGE_REQUEST_CODE = 0;// 打开相册请求码
	private static final int CAMERA_REQUEST_CODE = 1;// 拍照请求码
	private static final int RESULT_REQUEST_CODE = 2;// 结果请求码

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.mView = inflater.inflate(R.layout.fragment_me, container, false);
		ButterKnife.bind(this, mView);
		MyLog.i("MeFragment启动");
		mHead.setImageResource(R.mipmap.head);
		mContext = this.getActivity();
		mApplication = (EHomeApplication) ((Activity) mContext)
				.getApplication();
		init();
		return mView;
	}
	private void init() {
		if (mApplication.getmCurrentUser() != null) {
			if (mApplication.getmCurrentUser().getmHeadPhotoUrl() == null) {
				mHead.setImageResource(R.mipmap.head);
			}else if (mApplication.getmCurrentUser().getmHeadPhoto() != null) {
					mHead.setImageBitmap(mApplication.getmCurrentUser()
							.getmHeadPhoto());
				} else {
					if (mApplication.getmCurrentUser().getmHeadPhotoUrl().startsWith("http")) {

						ImageLoader.getInstance().loadImage(mApplication.getmCurrentUser().getmHeadPhotoUrl(),new ImageLoadingListener() {

							@Override
							public void onLoadingStarted(String imageUri, View view) {

							}

							@Override
							public void onLoadingFailed(String imageUri, View view,
														FailReason failReason) {

							}
							@Override
							public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
								mApplication.getmCurrentUser().setmHeadPhoto(loadedImage);
								if (mHead!=null) {
									mHead.setImageBitmap(loadedImage);
								}
							}

							@Override
							public void onLoadingCancelled(String imageUri, View view) {

							}
						});
					}

			}
			mName.setText(mApplication.getmCurrentUser().getmUsername());
			if (mApplication.getmCurrentUser().getmSNCode()==null || "".equals(mApplication.getmCurrentUser().getmSNCode())) {
				mSN.setText("Code ： " + "请绑定小区");
			}else {
				mSN.setText("Code ： " + mApplication.getmCurrentUser().getmSNCode());
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
			if (mApplication.getmCurrentUser() != null) {
				if (mApplication.getmCurrentUser().getmHeadPhoto() != null) {
					mHead.setImageBitmap(mApplication.getmCurrentUser()
							.getmHeadPhoto());
				} else if (mApplication.getmCurrentUser().getmHeadPhotoUrl() == null) {
					mHead.setImageResource(R.mipmap.head);
				} 
				}
	}

	@OnClick({ R.id.me_information, R.id.me_set, R.id.my_suggest,
			R.id.me_about, R.id.my_pay_fees, R.id.me_head })
	public void onClick(View v) {
		Bundle bundle = new Bundle();
		switch (v.getId()) {
		case R.id.me_information:
			BaseUtils.startActivity(mContext, ActivitySetInfo.class);
			break;
		case R.id.me_set:
			bundle.putString("type","set");
			BaseUtils.startActivities(mContext,SetActivity.class, bundle);
			break;
		case R.id.my_suggest:
			bundle.putString("type","suggest");
			BaseUtils.startActivities(mContext,SetActivity.class, bundle);
			break;
		case R.id.me_about:
			BaseUtils.startActivity(mContext, ActivityAbout.class);
			break;
		case R.id.my_pay_fees:
			DialogShow.showHintDialog(mContext, "该功能暂未开放，敬请期待");
			break;
		case R.id.me_head:
			uploadHeadPhoto();
			break;

		}

	}
	private void uploadHeadPhoto() {
		ArrayList<DialogMenuItem> list = new ArrayList<DialogMenuItem>();
		list.add(new DialogMenuItem("从相册选择", R.mipmap.select_image));
		list.add(new DialogMenuItem("拍照", R.mipmap.photograph));
		final NormalListDialog dialog = DialogShow.showListDialog(mContext,
				list);
		dialog.itemTextSize(18).setOnOperItemClickL(new OnOperItemClickL() {

			@Override
			public void onOperItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					// 从相册中选择
					Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, IMAGE_REQUEST_CODE);
					break;
				case 1:
					// 拍照
					Intent intentFromCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					if (hasSdcard()) {
						// 指定调用相机拍照后照片的储存路径
						tempFile = new File(Environment.getExternalStorageDirectory(), getPhotoFileName());
						intentFromCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
					}
					startActivityForResult(intentFromCamera,
							CAMERA_REQUEST_CODE);
					break;
				}
				dialog.dismiss();
			}
		});
		
	}

	/**
	 * 使用系统当前日期加以调整作为照片的名称
	 * 
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'img'_yyyyMMdd_HHmmss");

		return dateFormat.format(date) + ".jpg";
	}

	public static boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case IMAGE_REQUEST_CODE:
			if (data != null) {
				startPhotoZoom(data.getData());
				mImageFile = new File(getRealPathFromURI(data.getData()));
			}
			break;
		case CAMERA_REQUEST_CODE:
			startPhotoZoom(Uri.fromFile(tempFile));
			mImageFile = tempFile;
			break;
		case RESULT_REQUEST_CODE:
			if (data != null) {
				sentPicToNext(data);
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 调用系统裁剪功能：
	 * 
	 * @param fromFile
	 */
	private void startPhotoZoom(Uri fromFile) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(fromFile, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");

		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, RESULT_REQUEST_CODE);
	}

	/**
	 * 保存裁剪后的图片
	 * 
	 * @param data
	 */
	private void sentPicToNext(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
//			saveBitmapFile(mImageFile, photo);
			mBitmap = photo;
			uploadHead();// 上传头像
		}
	}

	private void uploadHead() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", mApplication.getmCurrentUser().getmId());
		File file = new File(mImageFile, "");
		if (!file.exists()) {
			Toast.makeText(mContext, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
			return;
		}

		OkHttpUtils.post()
				.addFile("mFile", getFileName(mImageFile.toString()), file)
				.url(ConnectPath.UPLOADHEAD_PATH).params(params).build()
				.execute(new StringCallback() {

					@Override
					public void onError(Call call, Exception e,int id) {
						BaseUtils.showShortToast(mContext,
								R.string.upload_failed);
					}

					@Override
					public void onResponse(String response,int id) {
						mApplication.getmCurrentUser().setmHeadPhoto(mBitmap);
						mHead.setImageBitmap(mBitmap);
						BaseUtils.showShortToast(mContext,
								R.string.uploaded_successfully);
					}

				});
	}

	public String getRealPathFromURI(Uri contentUri) {
		String path = null;
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = mContext.getContentResolver().query(contentUri, proj,
				null, null, null);
		if (cursor.moveToFirst()) {
			int columnIndex = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			path = cursor.getString(columnIndex);
		}
		cursor.close();
		return path;
	}

	public String getFileName(String pathandname) {

		int start = pathandname.lastIndexOf("/");
		if (start != -1) {
			return pathandname.substring(start + 1);
		} else {
			return null;
		}
	}
//	public void saveBitmapFile(File file,Bitmap bitmap){
//		 try {
//		 BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
//		 bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//		 bos.flush();
//		 bos.close();
//		 } catch (IOException e) {
//		 e.printStackTrace();
//		 }
//		 uploadHead();// 上传头像
//		 }

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		OkHttpUtils.getInstance().cancelTag(this);
		ButterKnife.unbind(this);
		MyLog.i("MeFragment销毁");
	}
}
