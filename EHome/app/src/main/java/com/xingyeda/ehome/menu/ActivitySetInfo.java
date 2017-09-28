package com.xingyeda.ehome.menu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.jovision.account.ActivityAddCamera;
import com.ldl.dialogshow.dialog.entity.DialogMenuItem;
import com.ldl.dialogshow.dialog.listener.OnOperItemClickL;
import com.ldl.dialogshow.dialog.widget.NormalListDialog;
import com.ldl.imageloader.core.ImageLoader;
import com.ldl.imageloader.core.assist.FailReason;
import com.ldl.imageloader.core.listener.ImageLoadingListener;
import com.ldl.okhttp.OkHttpUtils;
import com.ldl.okhttp.callback.StringCallback;
import com.xingyeda.ehome.ActivityHomepage;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.bean.HomeBean;
import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.door.ActivityAddAddress;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.SharedPreUtil;
import com.xingyeda.ehome.view.MaskedImage;
import com.xingyeda.ehome.zxing.android.CaptureActivity;

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
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

import static android.R.attr.type;


public class ActivitySetInfo extends BaseActivity {

    @Bind(R.id.info_user_photo)
    MaskedImage mInfo_Photo;// 设置头像
    @Bind(R.id.info_name)
    View mInfo_Name;// 呢称
    @Bind(R.id.info_alternate)
    View mInfo_Alternate; // 备用号码
    //   @Bind(R.id.info_spn_community)
//    Spinner mCommunity;// 默认小区
    //@Bind(R.id.info_community)
    // View mInfo_Community;//修改默认小区
    //@Bind(R.id.info_change_pwd)
    // Button mChange_Pwd;// 修改密码
    @Bind(R.id.info_name_text)
    TextView mNameText;// 呢称显示
    @Bind(R.id.info_alternate_text)
    TextView mAlternateText;// 备用号码显示
    @Bind(R.id.info_phone)
    TextView mInfoPhone;// 用户号码
    @Bind(R.id.info_username)
    TextView mUserName;// 用户名
    @Bind(R.id.info_usertype)
    TextView mUserType;// 用户类型
    @Bind(R.id.info_community_text)
    TextView mCommunity;// 默认小区
    //   @Bind(R.id.info_amend)
//    Button mAmend;
    @Bind(R.id.setinfo_back)
    TextView mBack;
    @Bind(R.id.info_share_type)
    TextView infoShareType;//分享类型
    private HomeBean mBean;
//    private String mName;// 呢称
//    private String mAlternate;// 备用号码

    // 图片压缩进的文件
    private File mFile;
    // 上传图片的file
//    private List<File> mFiles;
    private Bitmap mBitmap;

    // 请求码
    private static final int IMAGE_REQUEST_CODE = 0;// 打开相册请求码
    private static final int CAMERA_REQUEST_CODE = 1;// 拍照请求码
    private static final int RESULT_REQUEST_CODE = 2;// 结果请求码
    // private File tempFile = new
    // File(Environment.getExternalStorageDirectory(),
    // getPhotoFileName());
    private File tempFile;
    private File mImageFile;

//    private ArrayAdapter<HomeBean> mAdapter;

    // private UserInfo mUserInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_information);
        ButterKnife.bind(this);

        if (SharedPreUtil.getString(mContext,"share_type").equals("")) {
            SharedPreUtil.put(mContext,"share_type",2+"");
            infoShareType.setText(R.string.door_add_relation);
        }else{
           String shareType = SharedPreUtil.getString(mContext,"share_type");
            if (shareType.equals("2")) {
                infoShareType.setText(R.string.door_add_relation);
            } else if (shareType.equals("3")) {
                infoShareType.setText(R.string.door_add_tenant);
            }
        }
        // this.mUserInfo = mEhomeApplication.getmCurrentUser();
        this.init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBean = mEhomeApplication.getmCurrentUser().getmXiaoqu();
        this.mNameText.setText(mEhomeApplication.getmCurrentUser().getmName());
//	mName = mEhomeApplication.getmCurrentUser().getmName();

//	this.mAlternate = mEhomeApplication.getmCurrentUser().getmRemarksPhone();
        this.mAlternateText.setText(mEhomeApplication.getmCurrentUser()
                .getmRemarksPhone());
        if (mBean != null) {
            mCommunity.setText(mBean.getmCommunity() + mBean.getmPeriods() + mBean.getmUnit() + mBean.getmHouseNumber());
        } else {
            mCommunity.setText(R.string.door_add_hint);
        }
        this.mInfoPhone
                .setText(mEhomeApplication.getmCurrentUser().getmPhone());
        this.mUserName.setText(mEhomeApplication.getmCurrentUser()
                .getmUsername());
        if (mEhomeApplication.getmCurrentUser().getmXiaoqu() != null) {
            String type = mEhomeApplication.getmCurrentUser().getmXiaoqu()
                    .getmIdentityType();
            if (type.equals("1")) {
                this.mUserType.setText(R.string.door_add_owner);
            } else if (type.equals("2")) {
                this.mUserType.setText(R.string.door_add_relation);
            } else if (type.equals("3")) {
                this.mUserType.setText(R.string.door_add_tenant);
            }
        } else {
            this.mUserType.setText(R.string.door_add_hint);
        }

    }

    private void init() {
        mBean = mEhomeApplication.getmCurrentUser().getmXiaoqu();
//	this.mFiles = new ArrayList<File>();
        if (mBean != null) {
            mCommunity.setText(mBean.getmCommunity() + mBean.getmPeriods() + mBean.getmUnit() + mBean.getmHouseNumber());
        } else {
            mCommunity.setText(R.string.door_add_hint);
        }
        if (mEhomeApplication.getmCurrentUser() != null) {
            if (mEhomeApplication.getmCurrentUser().getmHeadPhotoUrl() == null) {
                mInfo_Photo.setImageResource(R.mipmap.head);
            } else if (mEhomeApplication.getmCurrentUser().getmHeadPhoto() != null) {
                mInfo_Photo.setImageBitmap(mEhomeApplication.getmCurrentUser()
                        .getmHeadPhoto());
            } else {
                if (mEhomeApplication.getmCurrentUser().getmHeadPhotoUrl().startsWith("http")) {

                    ImageLoader.getInstance().loadImage(mEhomeApplication.getmCurrentUser()
                            .getmHeadPhotoUrl(), new ImageLoadingListener() {

                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view,
                                                    FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            mEhomeApplication.getmCurrentUser().setmHeadPhoto(loadedImage);
                            if (mInfo_Photo != null) {
                                mInfo_Photo.setImageBitmap(loadedImage);
                            }
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });
//				OkHttp.getImage(mContext, mEhomeApplication.getmCurrentUser()
//						.getmHeadPhotoUrl(), new BitmapCallback() {
//					@Override
//					public void onResponse(Bitmap bitmap, int id) {
//						mEhomeApplication.getmCurrentUser().setmHeadPhoto(bitmap);
//						if (mInfo_Photo!=null) {
//							mInfo_Photo.setImageBitmap(bitmap);
//						}
//					}
//
//					@Override
//					public void onError(Call call, Exception e, int id) {
////						mHead.setImageResource(R.drawable.head);
//					}
//
//				});
                }

            }
        }
//    	if (mEhomeApplication.getmCurrentUser().getmHeadPhoto() != null) {
//    	    mInfo_Photo.setImageBitmap(mEhomeApplication.getmCurrentUser().getmHeadPhoto());
//    	} else if (mEhomeApplication.getmCurrentUser().getmHeadPhotoUrl() == null) {
//	    mInfo_Photo.setImageResource(R.drawable.head);
//    	} else {
//	    OkHttpUtils.get().url(mEhomeApplication.getmCurrentUser().getmHeadPhotoUrl())
//            .build().execute(new BitmapCallback()
//            {
//
//                @Override
//                public void onResponse(Bitmap bitmap)
//                {
//                    mEhomeApplication.getmCurrentUser().setmHeadPhoto(bitmap);
//                    mInfo_Photo.setImageBitmap(bitmap);
//                }
//
//		@Override
//		public void onError(Call call, Exception e) {
//		    
//		}
//
//            });
//	    
//    	}
//	    ImageLoader.getInstance().displayImage(
//		    ConnectPath.IMAGE_PATH
//			    + mEhomeApplication.getmCurrentUser()
//				    .getmHeadPhotoUrl(), mInfo_Photo);

        // if (mEhomeApplication.getmCurrentUser().getmXiaoquList()==null)
        // {
        // mEhomeApplication.getmCurrentUser().getmXiaoquList().add(new
        // Xiaoqu("暂无数据", null));
        // }
//	if (mEhomeApplication.getmCurrentUser().getmXiaoquList() != null
//		&& mEhomeApplication.getmCurrentUser().getmXiaoquList().size() != 0) {
//	    this.mAdapter = new ArrayAdapter<HomeBean>(this,
//		    R.layout.spinner_item, mEhomeApplication.getmCurrentUser()
//			    .getmXiaoquList());
//	    mCommunity.setAdapter(mAdapter);
//	    for (int i = 0; i < mEhomeApplication.getmCurrentUser()
//		    .getmXiaoquList().size(); i++) {
//		if (mEhomeApplication
//			.getmCurrentUser()
//			.getmXiaoquList()
//			.get(i)
//			.equals(mEhomeApplication.getmCurrentUser()
//				.getmXiaoqu())) {
//		    mCommunity.setSelection(i, true);
//		}
//	    }
//	    mCommunity.setOnItemSelectedListener(spnListener);
//	    // mCommunity.setSelection(R.id.info_community);
//	}

    }

//    private AdapterView.OnItemSelectedListener spnListener = new AdapterView.OnItemSelectedListener() {
//
//	@Override
//	public void onItemSelected(AdapterView<?> parent, View view,
//		int position, long id) {
//	    mBean = mEhomeApplication.getmCurrentUser().getmXiaoquList()
//		    .get(position);
//	    if (!mBean.equals(mEhomeApplication.getmCurrentUser().getmXiaoqu())) {
//			changeXiaoqu();
//		}
//	    mEhomeApplication.getmCurrentUser().setmXiaoqu(mBean);
//	}

//	@Override
//	public void onNothingSelected(AdapterView<?> parent) {
//	}
//
//    };

    @OnClick({R.id.info_user_photo, R.id.info_name, R.id.info_alternate, R.id.setinfo_back,R.id.share_type_layout
            //R.id.info_community
    })
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            // 设置头像
            case R.id.info_user_photo:
                uploadHeadPhoto();
                break;
            // 修改呢称
            case R.id.info_name:
//	    setInfo(R.id.info_name);
                bundle.putString("type", "name");
                BaseUtils.startActivities(mContext, ActivityChangeInfo.class, bundle);
                break;
            // 修改备用号码
            case R.id.info_alternate:
                bundle.putString("type", "beiyong");
                BaseUtils.startActivities(mContext, ActivityChangeInfo.class, bundle);
//	    setInfo(R.id.info_alternate);
                break;
//	case R.id.info_community:
//		if (mEhomeApplication.getmCurrentUser().getmXiaoquList().size()==0) {
//			DialogShow.showHintDialog(mContext, "请先绑定小区");
//		}else if (mEhomeApplication.getmCurrentUser().getmXiaoquList().size()==1) {
//			DialogShow.showHintDialog(mContext, "不可修改当前默认小区");
//		}else {
//			bundle.putString("type", "community");
//			BaseUtils.startActivities(mContext, ActivityChangeInfo.class, bundle);
//		}
//		break;
            // // 修改密码
            // case R.id.info_change_pwd:
            // EHomeUtils.startActivity(ActivitySetInfo.this,
            // ActivityChangePassword.class);
            // break;
            // 提交修改
//	case R.id.info_amend:
//	    if (mEhomeApplication.getmCurrentUser().getmXiaoquList() != null
//		    && mEhomeApplication.getmCurrentUser().getmXiaoquList()
//			    .size() != 0) {
//		changeXiaoqu();// 更改默认小区
//		mEhomeApplication.getmCurrentUser().setmXiaoqu(mBean);
//	    }
//	    modification();// 修改资料
            // EHomeUtils.startActivity(ActivitySetInfo.this,
            // ActivityHomepage.class);
            // ActivitySetInfo.this.finish();
//	    break;
            // 返回
            case R.id.setinfo_back:
                BaseUtils.startActivity(mContext, ActivityHomepage.class);
                ActivitySetInfo.this.finish();
                break;
            case R.id.share_type_layout:
                ArrayList<DialogMenuItem> list = new ArrayList<DialogMenuItem>();
                list.add(new DialogMenuItem("家属", 0));
                list.add(new DialogMenuItem("租客", 0));
                final NormalListDialog dialog = DialogShow.showListDialog(mContext,
                        list);
                dialog.titleTextSize_SP(18).itemTextSize(18).isTitleShow(true)
                        .title("请操作").setOnOperItemClickL(new OnOperItemClickL() {

                    @Override
                    public void onOperItemClick(AdapterView<?> parent,
                                                View view, int positions, long id) {
                        Bundle bundle = new Bundle();
                        switch (positions) {
                            case 0:// 家属
                                SharedPreUtil.put(mContext,"share_type",2+"");
                                infoShareType.setText(R.string.door_add_relation);
                                break;
                            case 1:// 租客
                                SharedPreUtil.put(mContext,"share_type",3+"");
                                infoShareType.setText(R.string.door_add_tenant);
                                break;
                        }
                        dialog.dismiss();

                    }
                });
                break;

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            BaseUtils.startActivity(ActivitySetInfo.this,
                    ActivityHomepage.class);
            ActivitySetInfo.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

//    private void setInfo(final int id) {
//	final EditText msg = new EditText(this);
//	new AlertDialog.Builder(this).setTitle(R.string.input)
//		.setIcon(android.R.drawable.ic_dialog_info).setView(msg)
//		.setPositiveButton(R.string.confirm, new OnClickListener() {
//		    @Override
//		    public void onClick(DialogInterface dialog, int which) {
//			String info = msg.getText().toString();
//			switch (id) {
//			// 修改呢称
//			case R.id.info_name:
//			    mName = info;
//			    // mEhomeApplication.getmCurrentUser().setmName(info);
//			    mNameText.setText(info);
//			    break;
//			// 修改备用号码
//			case R.id.info_alternate:
//			    // mEhomeApplication.getmCurrentUser().setmRemarksPhone(info);
//			    mAlternate = info;
//			    mAlternateText.setText(info);
//			    break;
//
//			}
//		    }
//		}).setNegativeButton(R.string.cancel, new OnClickListener() {
//		    @Override
//		    public void onClick(DialogInterface dialog, int which) {
//
//		    }
//		}).show();
//    }

//    // 修改信息
//    private void modification() {
//    	Map<String, String> params = new HashMap<String, String>();
//	params.put("id", mEhomeApplication.getmCurrentUser().getmId());
//	params.put("name", mName);
//	params.put("beiyong", mAlternate);
//	OkHttp.get(ConnectPath.MODIFICATION_PATH, params, new BaseStringCallback(mContext, new CallbackHandler<String>() {
//		
//		@Override
//		public void parameterError(JSONObject response) {
//			ActivitySetInfo.this.finish();
//		}
//		
//		@Override
//		public void onResponse(JSONObject response) {
//			mEhomeApplication.getmCurrentUser().setmName(
//					mName);
//				mEhomeApplication.getmCurrentUser()
//					.setmRemarksPhone(mAlternate);
//				BaseUtils.startActivity(ActivitySetInfo.this,
//					ActivityHomepage.class);
//				ActivitySetInfo.this.finish();
//		}
//		
//		@Override
//		public void onFailure() {
//			
//		}
//	}));
//
//    }

    private void uploadHeadPhoto() {
//	final String[] items = new String[] { "从相册选择","拍照" };
        ArrayList<DialogMenuItem> list = new ArrayList<DialogMenuItem>();
        list.add(new DialogMenuItem("从相册选择", R.mipmap.select_image));
        list.add(new DialogMenuItem("拍照", R.mipmap.photograph));
        final NormalListDialog dialog = DialogShow.showListDialog(mContext, list);
        dialog.itemTextSize(18).setOnOperItemClickL(new OnOperItemClickL() {

            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                switch (position) {
                    case 0:
                        // 从相册中选择
                        Intent intent = new Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, IMAGE_REQUEST_CODE);
                        break;
                    case 1:
                        // 拍照
                        Intent intentFromCamera = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        if (hasSdcard()) {
                            // 指定调用相机拍照后照片的储存路径
                            tempFile = new File(Environment
                                    .getExternalStorageDirectory(),
                                    getPhotoFileName());
                            intentFromCamera.putExtra(
                                    MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(tempFile));

                        }
                        startActivityForResult(intentFromCamera,
                                CAMERA_REQUEST_CODE);
                        break;
                }
                dialog.dismiss();
            }
        });
//	new AlertDialog.Builder(this)
//		.setTitle(R.string.set_head_portrait)
//		.setItems(items, new DialogInterface.OnClickListener() {
//
//		    @Override
//		    public void onClick(DialogInterface arg0, int which) {
//			switch (which) {
//			case 0:
//			    // 从相册中选择
//			    Intent intent = new Intent(
//				    Intent.ACTION_PICK,
//				    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//			    startActivityForResult(intent, IMAGE_REQUEST_CODE);
//			    break;
//			case 1:
//			    // 拍照
//			    Intent intentFromCamera = new Intent(
//				    MediaStore.ACTION_IMAGE_CAPTURE);
//			    if (hasSdcard()) {
//				// 指定调用相机拍照后照片的储存路径
//				tempFile = new File(Environment
//					.getExternalStorageDirectory(),
//					getPhotoFileName());
//				intentFromCamera.putExtra(
//					MediaStore.EXTRA_OUTPUT,
//					Uri.fromFile(tempFile));
//
//			    }
//			    startActivityForResult(intentFromCamera,
//				    CAMERA_REQUEST_CODE);
//			    break;
//			}
//		    }
//		})
//		.setNegativeButton(R.string.cancel,
//			new DialogInterface.OnClickListener() {
//
//			    @Override
//			    public void onClick(DialogInterface arg0, int arg1) {
//				// 隐藏对话框,释放对话框所占的资源
//				arg0.dismiss();
//			    }
//			}).show();
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
        Intent intent = new Intent("com.android.Camera.action.CROP");
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
        file();
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            mBitmap = photo;
            saveBitmapFile(mImageFile, photo);
//	    uploadHead();// 上传头像
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void file() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        mFile = new File(Environment.getExternalStorageDirectory(),
                df.format(new java.util.Date()) + ".zip");
        if (!mFile.exists()) {
            try {
                mFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
    }

    private void uploadHead() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", mEhomeApplication.getmCurrentUser().getmId());
        File file = new File(mImageFile, "");
        if (!file.exists()) {
            Toast.makeText(mContext, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpUtils.post()
                .addFile("mFile", getFileName(mImageFile.toString()), file)
                .url(ConnectPath.UPLOADHEAD_PATH)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        BaseUtils.showShortToast(mContext, R.string.upload_failed);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        mEhomeApplication.getmCurrentUser().setmHeadPhoto(mBitmap);
                        mInfo_Photo.setImageBitmap(mBitmap);
                        BaseUtils.showShortToast(mContext, R.string.uploaded_successfully);
                    }

                });
    }

    public String getFileName(String pathandname) {

        int start = pathandname.lastIndexOf("/");
        if (start != -1) {
            return pathandname.substring(start + 1);
        } else {
            return null;
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String path = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = ActivitySetInfo.this.getContentResolver().query(
                contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            path = cursor.getString(columnIndex);
        }
        cursor.close();
        return path;
    }

    //    // 修改默认小区
//    private void changeXiaoqu() {
//    	Map<String, String> params = new HashMap<String, String>();
//	params.put("uid", mEhomeApplication.getmCurrentUser().getmId());
//	params.put("hid", mBean.getmHouseNumberId());
//	OkHttp.get(ConnectPath.CHANGEXIAOQU_PATH, params, new BaseStringCallback(mContext, new CallbackHandler<String>() {
//		
//		@Override
//		public void parameterError(JSONObject response) {
//		}
//		
//		@Override
//		public void onResponse(JSONObject response) {
//			mEhomeApplication.getmCurrentUser().setmXiaoqu(mBean);
//			BaseUtils.showShortToast(mContext,R.string.set_prosperity);
//			SharedPreUtil.put(mContext, "eid",mBean.getmEquipmentId());
//			SharedPreUtil.put(mContext, "dongshu",mBean.getmUnitId());		
//		
//		}
//		
//		@Override
//		public void onFailure() {
//		}
//	}));
//	
//    }
    public void saveBitmapFile(File file, Bitmap bitmap) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        uploadHead();// 上传头像
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}
