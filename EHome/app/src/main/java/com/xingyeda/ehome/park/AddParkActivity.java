package com.xingyeda.ehome.park;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.percent.PercentRelativeLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldl.dialogshow.dialog.entity.DialogMenuItem;
import com.ldl.dialogshow.dialog.listener.OnOperItemClickL;
import com.ldl.dialogshow.dialog.widget.NormalListDialog;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.bean.HomeBean;
import com.xingyeda.ehome.bean.SentryBean;
import com.xingyeda.ehome.bean.Xiaoqu;
import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.door.ActivityXiaoquSeek;
import com.xingyeda.ehome.http.okhttp.BaseStringCallback;
import com.xingyeda.ehome.http.okhttp.CallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.MyLog;
import com.xingyeda.ehome.view.PriorityDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.xingyeda.ehome.AcivityRegister.isPhoneNumberValid;
import static com.xingyeda.ehome.door.ActivityAddAddress.REQUEST_CODE;

public class AddParkActivity extends BaseActivity {

    @Bind(R.id.park_xiaoqu_text)
    TextView parkXiaoquText;//小区
    @Bind(R.id.park_watchhouse_text)
    TextView parkWatchhouseText;//岗亭

    @Bind(R.id.park_name)
    EditText parkName;//姓名
    @Bind(R.id.park_phone)
    EditText parkPhone;//电话
    @Bind(R.id.park_address)
    EditText parkAddress;//地址

    @Bind(R.id.park_personage_information)
    PercentRelativeLayout parkPersonageInformation;//个人信息页面

    @Bind(R.id.park_car_brand)
    EditText parkCarBrand;//车品牌
    @Bind(R.id.park_car_colour)
    EditText parkCarColour;//车颜色
    @Bind(R.id.park_car_type)
    EditText parkCarType;//车型号
    @Bind(R.id.park_car_number)
    EditText parkCarNumber;//车牌号
    @Bind(R.id.park_car_data)
    EditText parkCarData;//登记日期

    @Bind(R.id.park_car_information)
    PercentRelativeLayout parkCarInformation;//车辆信息页面
    @Bind(R.id.park_driving_license)
    ImageView parkDrivingLicense;
    @Bind(R.id.park_loading)
    FrameLayout parkLoading;


    private List<Xiaoqu> mDatas;
    private List<SentryBean> mSentryDatas;
    private String mCommunityId;
    private String mSentryId;
    private int mXiaoquPosition;
    private int mSentryPosition;
    private Calendar rightNow = Calendar.getInstance();
    private String pathImage = ""; // 选择图片路径
    private File mFile;
    private KeyboardUtil keyboardUtil;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_park);
        ButterKnife.bind(this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mDatas = new ArrayList<>();
        mSentryDatas = new ArrayList<>();
        mXiaoquPosition = 0;
        parkCarData.setOnTouchListener(onTouchListener);
        getXiaoqu();
        parkName.setText(mEhomeApplication.getmCurrentUser().getmName());
        parkPhone.setText(mEhomeApplication.getmCurrentUser().getmPhone());
        parkCarNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
//                    // 此处为得到焦点时的处理内容
//                    if (imm != null) {
//                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
//                    }
//                    if(keyboardUtil == null){
//                        keyboardUtil = new KeyboardUtil(AddParkActivity.this, parkCarNumber);
//                        keyboardUtil.hideSoftInputMethod();
//                        keyboardUtil.showKeyboard();
//                    }else{
//                            keyboardUtil.hideSoftInputMethod();
//                            keyboardUtil.showKeyboard();
//                    }
                } else {
                    // 此处为失去焦点时的处理内容
                    if (keyboardUtil.isShow()) {
                        keyboardUtil.hideKeyboard();
                    }
                }

            }

        });
        parkCarNumber.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                // 此处为得到焦点时的处理内容
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                if (keyboardUtil == null) {
                    keyboardUtil = new KeyboardUtil(AddParkActivity.this, parkCarNumber);
                    keyboardUtil.hideSoftInputMethod();
                    keyboardUtil.showKeyboard();
                } else {
                    keyboardUtil.hideSoftInputMethod();
                    keyboardUtil.showKeyboard();
                }
                return false;
            }
        });
    }

    @OnClick({R.id.park_back, R.id.park_next_step, R.id.park_driving_license, R.id.park_submit, R.id.park_xiaoqu, R.id.park_watchhouse})
    public void onViewClicked(View view) {
        List<String> dialogList = new ArrayList<String>();
        switch (view.getId()) {
            case R.id.park_back://返回
                if (parkPersonageInformation.isShown()) {
                    finish();
                } else {
                    parkPersonageInformation.setVisibility(View.VISIBLE);
                    parkCarInformation.setVisibility(View.GONE);
                }
                break;
            case R.id.park_next_step://下一步
                if (mCommunityId == null) {
                    DialogShow.showHintDialog(mContext, "小区不能为空");
                } else if (mSentryId == null) {
                    DialogShow.showHintDialog(mContext, "岗亭不能为空");
                } else if (parkName.getText().toString().equals("")) {
                    DialogShow.showHintDialog(mContext, "姓名不能为空");
                } else if (parkPhone.getText().toString().equals("")) {
                    DialogShow.showHintDialog(mContext, "电话号码不能为空");
                } else if (!isPhoneNumberValid(parkPhone.getText().toString())) {
                    DialogShow.showHintDialog(mContext, "请输入正确的手机号码!");
                } else if (parkAddress.getText().toString().equals("")) {
                    DialogShow.showHintDialog(mContext, "住址不能为空");
                } else {
                    parkPersonageInformation.setVisibility(View.GONE);
                    parkCarInformation.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.park_driving_license://行驶证图片
                getImage();
                break;
            case R.id.park_submit://提交
                if (parkCarBrand.getText().toString().equals("")) {
                    DialogShow.showHintDialog(mContext, "车辆品牌不能为空");
                } else if (parkCarColour.getText().toString().equals("")) {
                    DialogShow.showHintDialog(mContext, "车辆颜色不能为空");
                } else if (parkCarType.getText().toString().equals("")) {
                    DialogShow.showHintDialog(mContext, "车辆型号不能为空");
                } else if (parkCarNumber.getText().toString().equals("")) {
                    DialogShow.showHintDialog(mContext, "车牌号不能为空");
                } else if (parkCarData.getText().toString().equals("")) {
                    DialogShow.showHintDialog(mContext, "登记日期不能为空");
                } else if (pathImage.equals("")) {
                    DialogShow.showHintDialog(mContext, "行驶证图片不能为空");
                } else {
                    parkAdd();
                }
                break;
            case R.id.park_xiaoqu://小区
                if (null != mDatas && !mDatas.isEmpty()) {
                    if (mDatas.size() == 1) {
                        startActivityForResult(new Intent(mContext, ActivityXiaoquSeek.class), REQUEST_CODE);
                    } else {
                        dialogList.clear();
                        for (int i = 0; i < mDatas.size(); i++) {
                            dialogList.add(mDatas.get(i).getmName());
                        }
                        priorityDialog(dialogList, "xiaoqu");
                    }
                }
                break;
            case R.id.park_watchhouse://岗亭
                if (null != mSentryDatas && !mSentryDatas.isEmpty()) {
                    dialogList.clear();
                    for (int i = 0; i < mSentryDatas.size(); i++) {
                        dialogList.add(mSentryDatas.get(i).getName());
                    }
                    priorityDialog(dialogList, "sentry");
                }
                break;
        }
    }


    private void getXiaoqu() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("longitude", mEhomeApplication.getLongitude() + "");
        params.put("latitude", mEhomeApplication.getLatitude() + "");
        OkHttp.get(mContext, ConnectPath.XIAOQU_PATH, params, new ConciseStringCallback(
                mContext, new ConciseCallbackHandler<String>() {
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
                        if (mEhomeApplication.getmCurrentUser().getmXiaoqu() != null) {
                            HomeBean bean = mEhomeApplication.getmCurrentUser().getmXiaoqu();
                            parkXiaoquText.setText(bean.getmCommunity());
                            mCommunityId = bean.getmCommunityId();
                            parkAddress.setText(bean.getmCommunity() + bean.getmPeriods() + bean.getmUnit() + bean.getmHouseNumber());
                        } else {
                            parkXiaoquText.setText(mDatas.get(0).getmName());
                            mCommunityId = mDatas.get(0).getmId();
                        }
//                        mHandler.sendEmptyMessage(XIAOQU);
                        getSentry(mCommunityId);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    private void getSentry(String id) {
        Map<String, String> params = new HashMap<>();
        params.put("xiaoqu", id);
        OkHttp.get(mContext, ConnectPath.QUERY_SENTRY, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    mSentryDatas.clear();
                    parkWatchhouseText.setText("");
                    JSONArray sentry = (JSONArray) response
                            .get("obj");
                    if (sentry != null && sentry.length() != 0) {
                        for (int i = 0; i < sentry.length(); i++) {
                            JSONObject jobj = sentry.getJSONObject(i);
                            SentryBean bean = new SentryBean();
                            bean.setId(jobj.has("id") ? jobj.getString("id") : "");
                            bean.setName(jobj.has("name") ? jobj.getString("name") : "");
                            mSentryDatas.add(bean);
                        }
                        parkWatchhouseText.setText(mSentryDatas.get(0).getName());
                        mSentryId = mSentryDatas.get(0).getId();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));

    }

    private void parkAdd() {
        MyLog.i("停车场添加");
        parkLoading.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put("uid", mEhomeApplication.getmCurrentUser().getmId());//用户id
        params.put("communityId", mCommunityId);//小区id
        params.put("pid", mSentryId);//岗亭id
        params.put("parkName", parkName.getText().toString());//姓名
        params.put("parkPhone", parkPhone.getText().toString());//电话
        params.put("parkAddress", parkAddress.getText().toString());//地址
        params.put("carBrand", parkCarBrand.getText().toString());//车辆品牌
        params.put("carColour", parkCarColour.getText().toString());//车辆颜色
        params.put("carType", parkCarType.getText().toString());//车辆型号
        params.put("carNumber", parkCarNumber.getText().toString());//车牌号
        params.put("carData", parkCarData.getText().toString());//登记日期
        OkHttp.uploadFile(mContext, ConnectPath.BIND_SENTRY, "drivingLicense", pathImage, params, mFile, new BaseStringCallback(mContext, new CallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("status").equals("200")) {
                        parkLoading.setVisibility(View.GONE);
                        BaseUtils.showShortToast(mContext, R.string.add_prosperity);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void parameterError(JSONObject response) {
                parkLoading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure() {
                parkLoading.setVisibility(View.GONE);
            }
        }));

    }

    private void priorityDialog(List<String> list, final String type) {
        final PriorityDialog dlg = new PriorityDialog(mContext,
                R.style.dialog_priority, list, type);
        dlg.show();
        dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                if (null != type) {
                    if (type.equals("xiaoqu")) {
                        mXiaoquPosition = dlg.getPosition();
                        mCommunityId = mDatas.get(Integer.valueOf(mXiaoquPosition)).getmId();
                        parkXiaoquText.setText(mDatas.get(Integer.valueOf(mXiaoquPosition)).getmName());
                        getSentry(mCommunityId);
                    } else if (type.equals("sentry")) {
                        mSentryPosition = dlg.getPosition();
                        mSentryId = mSentryDatas.get(Integer.valueOf(mSentryPosition)).getId();
                        parkWatchhouseText.setText(mSentryDatas.get(Integer.valueOf(mSentryPosition)).getName());
                    }
                }
            }
        });
    }


    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View arg0, MotionEvent arg1) {
            if (keyboardUtil.isShow()) {
                keyboardUtil.hideKeyboard();
            }
            if (MotionEvent.ACTION_DOWN == arg1.getAction()) {
                new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker arg0, int y, int m, int d) {
                        parkCarData.setText(y + "-" + (++m) + "-" + d);
                    }
                }, rightNow.get(Calendar.YEAR),
                        rightNow.get(Calendar.MONTH),
                        rightNow.get(Calendar.DAY_OF_MONTH)).show();
            }
            return true;
        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 打开图片
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case IMAGE_OPEN:
                    Uri uri = data.getData();
                    if (!TextUtils.isEmpty(uri.getAuthority())) {
                        // 查询选择图片
                        Cursor cursor = getContentResolver().query(uri,
                                new String[]{MediaStore.Images.Media.DATA},
                                null, null, null);
                        // 返回 没找到选择图片
                        if (null == cursor) {
                            return;
                        }
                        // 光标移动至开头 获取图片路径
                        cursor.moveToFirst();
                        pathImage = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        mFile = new File(pathImage);
                        parkDrivingLicense.setImageBitmap(BitmapFactory.decodeFile(pathImage));
                    }
                    break;

                case IMAGE_CAMERA:
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    mFile = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
                    try {
                        BufferedOutputStream bos = new BufferedOutputStream(
                                new FileOutputStream(mFile));
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                        bos.flush();
                        bos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // mUpload.setImageBitmap(bitmap);
                    pathImage = mFile.getAbsoluteFile().toString();
                    parkDrivingLicense.setImageBitmap(BitmapFactory.decodeFile(pathImage));
                    break;
            }

        } // end if 打开图片
        else if (resultCode == ActivityXiaoquSeek.RESULT_CODE) {
            if (data == null)
                return;
            parkXiaoquText.setText(data.getExtras().getString("name"));
            mXiaoquPosition = 0;
            getSentry(data.getExtras().getString("id"));
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (keyboardUtil != null) {
                if (keyboardUtil.isShow()) {
                    keyboardUtil.hideKeyboard();
                } else {
                    if (parkPersonageInformation.isShown()) {
                        finish();
                    } else {
                        parkPersonageInformation.setVisibility(View.VISIBLE);
                        parkCarInformation.setVisibility(View.GONE);
                    }
                }
            } else {
                if (parkPersonageInformation.isShown()) {
                    finish();
                } else {
                    parkPersonageInformation.setVisibility(View.VISIBLE);
                    parkCarInformation.setVisibility(View.GONE);
                }
            }
        }
        return false;
    }

    private final int IMAGE_OPEN = 1;
    private final int IMAGE_CAMERA = 2;

    private void getImage() {
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
                        intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, IMAGE_OPEN);
                        break;
                    case 1:
                        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, IMAGE_CAMERA);
                        break;
                }
                dialog.dismiss();
            }
        });
    }


}
