package com.xingyeda.ehome.door;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.ldl.dialogshow.dialog.listener.OnBtnClickL;
import com.ldl.dialogshow.dialog.widget.NormalDialog;
import com.xingyeda.ehome.ActivityHomepage;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.bean.DongshuData;
import com.xingyeda.ehome.bean.QishuData;
import com.xingyeda.ehome.bean.XiaoquData;
import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.SharedPreUtil;
import com.xingyeda.ehome.view.PriorityDialog;

/**
 * @author 李达龙
 * @ClassName: ActivityAddAddress
 * @Description: 添加绑定界面
 * @date 2016-7-6
 */
public class ActivityAddAddress extends BaseActivity {

    // 下拉框
    @Bind(R.id.door_xiaoqu)
    Button mCommunity; // 小区
    @Bind(R.id.door_qishu)
    Button mPeriods; // 期数
    @Bind(R.id.door_dongshu)
    Button mUnit; // 单元（栋数）
    @Bind(R.id.door_user_type)
    Button mUserType;
    @Bind(R.id.door_add_doornub)
    EditText mEditText;
    @Bind(R.id.door_add_back)
    TextView mBack;
    @Bind(R.id.door_add_submit)
    Button mSiteSubmit;
    @Bind(R.id.add_SN)
    LinearLayout mSN;
    @Bind(R.id.add_code)
    EditText mCode;
    @Bind(R.id.add_SNcode)
    TextView mSNCode;
    @Bind(R.id.add_hint)
    TextView mHint;

    private String mCommunityId;
    private String mPeriodsId;
    private String mUnitId;
    private String mIdentityStr = null;

    private List<XiaoquData> mDatas;
    private static final int XIAOQU = 1;
    private static final int QISHU = 2;
    private static final int DONGSHU = 3;
    public final static int REQUEST_CODE = 200;
    private int mXiaoquPosition;
    private int mQishuPosition;
    private List<String> userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_add_address);
        ButterKnife.bind(this);
        mXiaoquPosition = 0;
        mQishuPosition = 0;
        mDatas = new ArrayList<XiaoquData>();
        userType = new ArrayList<String>();
        getXiaoqu();
        init();
    }

    private void init() {
        userType.add("业主");
        userType.add("租客");
        userType.add("家属");
        // mUserType.setText(userType.get(0));
        // mIdentityStr = "1";
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
                            XiaoquData xiaoqu = new XiaoquData();
                            xiaoqu.setmId(jobj.has("id") ? jobj
                                    .getString("id") : "");
                            xiaoqu.setmName(jobj.has("name") ? jobj
                                    .getString("name") : "");
                            mDatas.add(xiaoqu);
                        }
                        if (mCommunity!=null) {
                            mCommunity.setText(mDatas.get(0).getmName());
                        }
                        mCommunityId = mDatas.get(0).getmId();
                        mHandler.sendEmptyMessage(XIAOQU);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    private void getQishu(String id) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        OkHttp.get(mContext, ConnectPath.QISHU_PATH, params, new ConciseStringCallback(
                mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray xiaoqu_list = (JSONArray) response
                            .get("obj");
                    List<QishuData> qishuDatas = new ArrayList<QishuData>();
                    if (xiaoqu_list != null
                            && xiaoqu_list.length() != 0) {
                        for (int i = 0; i < xiaoqu_list.length(); i++) {
                            JSONObject jobj = xiaoqu_list
                                    .getJSONObject(i);
                            QishuData qishu = new QishuData();
                            qishu.setmId(jobj.has("id") ? jobj
                                    .getString("id") : "");
                            qishu.setmName(jobj.has("name") ? jobj
                                    .getString("name") : "");
                            qishuDatas.add(qishu);
                        }
                        if (mPeriods!=null) {
                            mPeriods.setText(qishuDatas.get(0).getmName());
                        }
                        mPeriodsId = qishuDatas.get(0).getmId();

                    }
                    mDatas.get(mXiaoquPosition).setQishu(qishuDatas);
                    Message msg = new Message();
                    msg.what = QISHU;
                    mHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    private void getDongshu(String xiaoquId, String qishuId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", qishuId);
        OkHttp.get(mContext, ConnectPath.DONGSHU_PATH, params, new ConciseStringCallback(
                mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray xiaoqu_list = (JSONArray) response
                            .get("obj");
                    List<DongshuData> dongshuDatas = new ArrayList<DongshuData>();
                    if (xiaoqu_list != null
                            && xiaoqu_list.length() != 0) {
                        for (int i = 0; i < xiaoqu_list.length(); i++) {
                            JSONObject jobj = xiaoqu_list
                                    .getJSONObject(i);
                            DongshuData dongshu = new DongshuData();
                            dongshu.setmId(jobj.has("id") ? jobj
                                    .getString("id") : "");
                            dongshu.setmName(jobj.has("name") ? jobj
                                    .getString("name") : "");
                            dongshuDatas.add(dongshu);
                        }
                        if (mUnit!=null) {
                            mUnit.setText(dongshuDatas.get(0).getmName());
                        }
                        mUnitId = dongshuDatas.get(0).getmId();
                    }
                    mDatas.get(mXiaoquPosition).getQishu()
                            .get(mQishuPosition)
                            .setDongshu(dongshuDatas);
                    Message msg = new Message();
                    msg.what = DONGSHU;
                    mHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case XIAOQU:
                    getQishu(mDatas.get(mXiaoquPosition).getmId());
                    break;
                case QISHU:
                    getDongshu(
                            mDatas.get(mXiaoquPosition).getmId(),
                            mDatas.get(mXiaoquPosition).getQishu()
                                    .get(mQishuPosition).getmId());
                    break;
            }
        }
    };

    @OnClick({R.id.door_add_submit, R.id.door_add_back, R.id.door_xiaoqu,
            R.id.door_qishu, R.id.door_dongshu, R.id.door_user_type})
    public void onClick(View v) {
        String houseNumber = mEditText.getText().toString();
        List<String> dialogList = new ArrayList<String>();
        switch (v.getId()) {
            case R.id.door_add_submit:
                if (mIdentityStr == null) {
                    DialogShow.showHintDialog(mContext,
                            getResources().getString(R.string.select_identity));
                } else if (houseNumber == null || houseNumber.equals("")) {
                    DialogShow.showHintDialog(mContext,
                            getResources()
                                    .getString(R.string.not_null_house_number));
                    // DialogUtils.getHintDialog(mContext,R.string.not_null_house_number);
                } else if (houseNumber.equals("9999")) {
                DialogShow.showHintDialog(mContext, "门牌号9999错误");
                }else if (mCode.getText().toString() == null || mCode.getText().toString().equals("")) {
                    if (!mIdentityStr.equals("1")) {
                        DialogShow.showHintDialog(mContext, "code不能为空");
                    } else {
                        DialogShow.showHintDialog(mContext, "设备SN不能为空");
                    }
                } else {
                    if (mCommunityId!=null) {
                        DialogShow.showHintDialog(mContext, "请选择小区");
                    }else if (mPeriodsId!=null){
                        DialogShow.showHintDialog(mContext, "请选择期数");
                    } else if (mUnitId!=null) {
                        DialogShow.showHintDialog(mContext, "请选择栋数");
                    }else{
                        submit(houseNumber);
                    }
                }
                break;
            case R.id.door_add_back:
                ActivityAddAddress.this.finish();
                break;
            case R.id.door_xiaoqu:
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
            case R.id.door_qishu:
                if (null != mDatas && !mDatas.isEmpty()) {
                    if (null != mDatas.get(mXiaoquPosition).getQishu()
                            && !mDatas.get(mXiaoquPosition).getQishu().isEmpty()) {
                        dialogList.clear();
                        for (int i = 0; i < mDatas.get(mXiaoquPosition).getQishu()
                                .size(); i++) {
                            dialogList.add(mDatas.get(mXiaoquPosition).getQishu()
                                    .get(i).getmName());
                        }
                        priorityDialog(dialogList, "qishu");
                    }
                }
                break;
            case R.id.door_dongshu:
                if (null != mDatas && !mDatas.isEmpty()) {
                    if (null != mDatas.get(mXiaoquPosition).getQishu()
                            && !mDatas.get(mXiaoquPosition).getQishu().isEmpty()) {
                        if (null != mDatas.get(mXiaoquPosition).getQishu()
                                .get(mQishuPosition).getDongshu()
                                && !mDatas.get(mXiaoquPosition).getQishu()
                                .get(mQishuPosition).getDongshu().isEmpty()) {
                            dialogList.clear();
                            for (int i = 0; i < mDatas.get(mXiaoquPosition)
                                    .getQishu().get(mQishuPosition).getDongshu()
                                    .size(); i++) {
                                dialogList.add(mDatas.get(mXiaoquPosition)
                                        .getQishu().get(mQishuPosition)
                                        .getDongshu().get(i).getmName());
                            }
                            priorityDialog(dialogList, "dongshu");
                        }
                    }
                }
                break;
            case R.id.door_user_type:
                priorityDialog(userType, "userType");
                break;
        }
    }

    private void priorityDialog(List<String> list, final String type) {
        final PriorityDialog dlg = new PriorityDialog(mContext,
                R.style.dialog_priority, list, type);
        dlg.show();
        dlg.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                if (null != type) {
                    if (type.equals("xiaoqu")) {
                        mXiaoquPosition = dlg.getPosition();
                        mCommunityId = mDatas.get(
                                Integer.valueOf(mXiaoquPosition)).getmId();
                        mCommunity.setText(mDatas.get(
                                Integer.valueOf(mXiaoquPosition)).getmName());
                        getQishu(mDatas.get(mXiaoquPosition).getmId());
                    } else if (type.equals("qishu")) {
                        mQishuPosition = dlg.getPosition();
                        mPeriodsId = mDatas.get(mXiaoquPosition).getQishu()
                                .get(Integer.valueOf(mQishuPosition)).getmId();
                        mPeriods.setText(mDatas.get(mXiaoquPosition).getQishu()
                                .get(Integer.valueOf(mQishuPosition))
                                .getmName());
                        getDongshu(
                                mDatas.get(mXiaoquPosition).getmId(),
                                mDatas.get(mXiaoquPosition).getQishu()
                                        .get(mQishuPosition).getmId());
                    } else if (type.equals("dongshu")) {
                        // int i =dlg.getPosition()
                        mUnitId = mDatas.get(mXiaoquPosition).getQishu()
                                .get(mQishuPosition).getDongshu()
                                .get(dlg.getPosition()).getmId();
                        mUnit.setText(mDatas.get(mXiaoquPosition).getQishu()
                                .get(mQishuPosition).getDongshu()
                                .get(dlg.getPosition()).getmName());
                    } else if (type.equals("userType")) {
                        mIdentityStr = dlg.getPosition() + 1 + "";
                        // DialogShow.showHintDialog(mContext,mIdentityStr);
                        // DialogUtils.getDialog(mContext, mIdentityStr);
                        mSN.setVisibility(View.VISIBLE);
                        if (!mIdentityStr.equals("1")) {
                            mSNCode.setText("Code");
                            mCode.setHint("请输入业主Code");
                            mHint.setVisibility(View.VISIBLE);
                            mHint.setText("注 ： Code码请于业主app“我”界面上方的Code码中提取");
                        } else {
                            mSNCode.setText("设备SN");
                            mHint.setVisibility(View.VISIBLE);
                            mHint.setText("注 ： 业主设备SN码请于单元门口机右上角提取");
                            mCode.setHint(R.string.SNCode_hint);
                        }
                        mUserType.setText(userType.get(dlg.getPosition()));
                    }
                }
            }
        });
    }

    private void submit(String houseNumber) {

        while (houseNumber.length() < 4) {
            houseNumber = "0" + houseNumber;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", SharedPreUtil.getString(mContext, "userId", ""));
        params.put("xiaoqu", mCommunityId);
        params.put("qishu", mPeriodsId);
        params.put("dongshu", mUnitId);
        params.put("housenum", houseNumber);
        params.put("type", mIdentityStr);
        if (mIdentityStr.equals("1")) {
            params.put("sn", mCode.getText().toString());
        } else {
            params.put("sNcode", mCode.getText().toString());
        }
        params.put("clientType", "1");
        OkHttp.get(mContext, ConnectPath.BIND_PATH, params, new ConciseStringCallback(
                mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("status").equals("200")) {

                        BaseUtils.showShortToast(mContext,
                                R.string.add_prosperity);
                        final NormalDialog dialog = DialogShow.showSelectDialog(mContext, getResources().getString(R.string.wait_check), 1, new String[]{getResources().getString(R.string.confirm)});
                        dialog.setOnBtnClickL(new OnBtnClickL() {

                            @Override
                            public void onBtnClick() {
                                dialog.superDismiss();
                                BaseUtils.startActivity(mContext,ActivityHomepage.class);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        // 如果data等于null返回
        if (resultCode == ActivityXiaoquSeek.RESULT_CODE) {

            if (data == null)
                return;
            // String id=data.getExtras().getString("id");
            // String name=data.getExtras().getString("name");
            mCommunity.setText(data.getExtras().getString("name"));
            mXiaoquPosition = 0;
            getQishu(data.getExtras().getString("id"));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}
