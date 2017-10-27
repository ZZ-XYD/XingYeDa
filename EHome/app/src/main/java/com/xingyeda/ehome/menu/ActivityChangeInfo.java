package com.xingyeda.ehome.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ldl.dialogshow.dialog.listener.OnBtnClickL;
import com.ldl.dialogshow.dialog.widget.NormalDialog;
import com.xingyeda.ehome.ActivityHomepage;
import com.xingyeda.ehome.ActivityLogin;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.adapter.XiaoquAdapter;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.base.LitePalUtil;
import com.xingyeda.ehome.bean.HomeBean;
import com.xingyeda.ehome.bean.InformationBase;
import com.xingyeda.ehome.bean.UserInfo;
import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.http.okhttp.BaseStringCallback;
import com.xingyeda.ehome.http.okhttp.CallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.SharedPreUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.xingyeda.ehome.AcivityRegister.isPhoneNumberValid;


public class ActivityChangeInfo extends BaseActivity {

    @Bind(R.id.change_info_title)
    TextView mTitle;
    @Bind(R.id.change_info_save)
    TextView mSave;
    @Bind(R.id.change_edit)
    EditText mContent;
    @Bind(R.id.change_view)
    View mView;
    @Bind(R.id.change_listview)
    ListView mListView;
    @Bind(R.id.change_loading)
    FrameLayout changeLoading;

    private String mStrContent;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_info);
        ButterKnife.bind(this);

        mStrContent = getIntent().getExtras().getString("type");
        id = getIntent().getExtras().getString("id");
        if (mStrContent.equals("name")) {
            mTitle.setText("修改姓名");
            mContent.setHint("请输入新名字");
        } else if (mStrContent.equals("beiyong")) {
            mTitle.setText("修改备用号码");
            mContent.setHint("请输入新号码");
        } else if (mStrContent.equals("community")) {
            mTitle.setText("修改默认小区");
            mListView.setVisibility(View.VISIBLE);
            mContent.setVisibility(View.GONE);
            mView.setVisibility(View.GONE);
            mSave.setVisibility(View.GONE);
            init();
        } else if (mStrContent.equals("park")) {
            mTitle.setText("修改停车场呢称");
            mContent.setHint("请输入停车场呢称");
        }else if (mStrContent.equals("login")) {
            mTitle.setText("完善信息");
            mContent.setHint("请输入手机号码");
        }

        changeLoading.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }

        });


    }

    private void init() {

        final List<HomeBean> list = new ArrayList<HomeBean>();
        for (HomeBean bean : LitePalUtil.getCommunityList()) {
            if (bean.getmType().equals("1")) {
                list.add(bean);
            }
        }
        XiaoquAdapter adapter = new XiaoquAdapter(mContext, list);
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                HomeBean mBean = list.get(position);
                if (!mBean.equals(LitePalUtil.getHomeBean())) {
//                    LitePalUtil.setHomeBean(mBean);
                    changeXiaoqu(mBean);
                } else {
                    DialogShow.showHintDialog(mContext, "您当前选择小区是默认小区，请重新选择");
                }
            }
        });
    }

    private void changeXiaoqu(final HomeBean bean) {
        changeLoading.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", SharedPreUtil.getString(mContext, "userId", ""));
        params.put("hid", bean.getmHouseNumberId());
        OkHttp.get(mContext, ConnectPath.CHANGEXIAOQU_PATH, params,
                new BaseStringCallback(mContext, new CallbackHandler<String>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        changeLoading.setVisibility(View.GONE);
                        LitePalUtil.updateHomeBean(bean);
                        BaseUtils.showShortToast(mContext, R.string.set_prosperity);
                        SharedPreUtil.put(mContext, "eid",
                                bean.getmEquipmentId());
                        SharedPreUtil.put(mContext, "dongshu",
                                bean.getmUnitId());
                        init();
                    }

                    @Override
                    public void parameterError(JSONObject response) {
                        changeLoading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure() {
                        changeLoading.setVisibility(View.GONE);
                    }
                }));

    }

    @OnClick({R.id.change_info_back, R.id.change_info_save})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_info_back:
                if (mStrContent.equals("login")) {
                    BaseUtils.startActivity(mContext, ActivityLogin.class);
                }
                    ActivityChangeInfo.this.finish();
                break;
            case R.id.change_info_save:
                if (mContent.getText().toString() != null && mContent.getText().toString().length() != 0) {
                    if (mStrContent.equals("name")) {
                        if (Stringlength(mContent.getText().toString()) <= 16) {
                            if (checkAccountMark(mContent.getText().toString())) {
                                modification();
                            } else {
                                DialogShow.showHintDialog(mContext, "用户名包换特殊字符");
                            }
                        } else {
                            DialogShow.showHintDialog(mContext, "用户名过长");
                        }
                    } else if (mStrContent.equals("beiyong")) {
                        if (mContent.getText().toString().equals("")) {
                            DialogShow.showHintDialog(mContext, "电话号码不能为空");
                        } else if (!isPhoneNumberValid(mContent.getText().toString())) {
                            DialogShow.showHintDialog(mContext, "请输入正确的手机号码!");
                        } else {
                            modification();
                        }
                    } else if (mStrContent.equals("park")) {
                        if (Stringlength(mContent.getText().toString()) <= 16) {
                            HomeBean homeBean = new HomeBean();
                            homeBean.setmParkNickName(mContent.getText().toString());
                            homeBean.updateAll("mParkId = ?", id);
                            BaseUtils.showShortToast(mContext, "修改成功");
                            ActivityChangeInfo.this.finish();
                        } else {
                            DialogShow.showHintDialog(mContext, "呢称过长");
                        }
                    }else if (mStrContent.equals("login")) {
                        if (mContent.getText().toString().equals("")) {
                            DialogShow.showHintDialog(mContext, "电话号码不能为空");
                        } else if (!isPhoneNumberValid(mContent.getText().toString())) {
                            DialogShow.showHintDialog(mContext, "请输入正确的手机号码!");
                        } else {
                            uploadphone();
                        }
                    }
                } else {
                    if (mStrContent.equals("name")) {
                        DialogShow.showHintDialog(mContext, "输入名字为空");
                    } else if (mStrContent.equals("beiyong")) {
                        DialogShow.showHintDialog(mContext, "输入号码为空");
                    } else if (mStrContent.equals("park")) {
                        DialogShow.showHintDialog(mContext, "输入呢称为空");
                    }else if (mStrContent.equals("login")) {
                        DialogShow.showHintDialog(mContext, "输入号码为空");
                    }

                }
                break;
        }
    }

    private void uploadphone() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        params.put("phone", mContent.getText().toString());
        OkHttp.get(mContext, ConnectPath.UPDATEPHONE_PATH, params,
                new BaseStringCallback(mContext, new CallbackHandler<String>() {

                    @Override
                    public void parameterError(JSONObject response) {
                        ActivityChangeInfo.this.finish();
                    }

                    @Override
                    public void onResponse(JSONObject response) {
                        UserInfo info = new UserInfo();
                        info.setmName(mContent.getText().toString());
                        LitePalUtil.setUserInfo(info);
                            final NormalDialog dialog = DialogShow.showSelectDialog(mContext, "上传成功", 1, new String[]{"确定"});
                            dialog.setOnBtnClickL(new OnBtnClickL() {
                                @Override
                                public void onBtnClick() {
                                    UserInfo info = new UserInfo();
                                    info.setmPhone(mContent.getText().toString());
                                    LitePalUtil.setUserInfo(info);
                                    BaseUtils.startActivity(mContext, ActivityHomepage.class);
                                    dialog.superDismiss();
                                    ActivityChangeInfo.this.finish();
                                }

                            });
                    }

                    @Override
                    public void onFailure() {

                    }
                }));
    }

    private void modification() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", SharedPreUtil.getString(mContext, "userId", ""));
        params.put(mStrContent, mContent.getText().toString());
        OkHttp.get(mContext, ConnectPath.MODIFICATION_PATH, params,
                new BaseStringCallback(mContext, new CallbackHandler<String>() {

                    @Override
                    public void parameterError(JSONObject response) {
                        ActivityChangeInfo.this.finish();
                    }

                    @Override
                    public void onResponse(JSONObject response) {
                            UserInfo info = new UserInfo();
                        if (mStrContent.equals("name")) {
                            info.setmName(mContent.getText().toString());
                            final NormalDialog dialog = DialogShow.showSelectDialog(mContext, "修改成功", 1, new String[]{"确定"});
                            dialog.setOnBtnClickL(new OnBtnClickL() {

                                @Override
                                public void onBtnClick() {
                                    dialog.superDismiss();
                                    ActivityChangeInfo.this.finish();
                                }

                            });
                        } else if (mStrContent.equals("beiyong")) {
                            info.setmRemarksPhone(mContent.getText().toString());
                            final NormalDialog dialog = DialogShow.showSelectDialog(mContext, "修改成功", 1, new String[]{"确定"});
                            dialog.setOnBtnClickL(new OnBtnClickL() {

                                @Override
                                public void onBtnClick() {
                                    dialog.superDismiss();
                                    ActivityChangeInfo.this.finish();
                                }

                            });
                        }
                        LitePalUtil.setUserInfo(info);
                    }

                    @Override
                    public void onFailure() {

                    }
                }));

    }


    // 监听返回按钮
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (changeLoading.isShown()) {
                changeLoading.setVisibility(View.GONE);
                return false;
            } else {
                if (mStrContent.equals("login")) {
                    BaseUtils.startActivity(mContext, ActivityLogin.class);
                }
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }


    /**
     * 验证用户名只包含字母，数字，中文
     *
     * @param account
     * @return
     */
    public static boolean checkAccountMark(String account) {
        String all = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$";
        Pattern pattern = Pattern.compile(all);
        return pattern.matches(all, account);
    }

    public static int Stringlength(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
        for (int i = 0; i < value.length(); i++) {
            /* 获取一个字符 */
            String temp = value.substring(i, i + 1);
            /* 判断是否为中文字符 */
            if (temp.matches(chinese)) {
                /* 中文字符长度为2 */
                valueLength += 2;
            } else {
                /* 其他字符长度为1 */
                valueLength += 1;
            }
        }
        return valueLength;
    }


}
