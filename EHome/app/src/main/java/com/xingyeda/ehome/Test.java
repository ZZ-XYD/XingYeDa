package com.xingyeda.ehome;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xiaomi.mipush.sdk.MiPushClient;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.bean.UserInfo;
import com.xingyeda.ehome.util.LogUtils;
import com.xingyeda.ehome.util.SharedPreUtil;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import static com.xingyeda.ehome.door.DoorFragment.ACTION_NAME;

public class Test extends BaseActivity {

   @Bind(R.id.time)
    TextView time;
   @Bind(R.id.count)
    TextView count;
   @Bind(R.id.count1)
    TextView count1;
   @Bind(R.id.count2)
    TextView count2;
   @Bind(R.id.count3)
    TextView count3;
   @Bind(R.id.count4)
    TextView count4;
   @Bind(R.id.count5)
    TextView count5;
   @Bind(R.id.button)
    Button button;
   @Bind(R.id.button2)
    Button button2;

    public static final String ACTION_PUSH = "Push";
    public static final String ACTION_JPUSH = "JPush";
    public static final String ACTION_JPUSH1 = "JPush1";
    public static final String ACTION_JPUSH2 = "JPush2";
    public static final String ACTION_JPUSH3 = "JPush3";
    public static final String ACTION_JPUSH4 = "JPush4";
    public static final String ACTION_JPUSH5 = "JPush5";
    public static final String ACTION_JPUSH6 = "JPush6";

    public static final String ACTION_MI = "MiPush";
    public static final String ACTION_MI1 = "MiPush1";
    public static final String ACTION_MI2 = "MiPush2";
    public static final String ACTION_MI3 = "MiPush3";
    public static final String ACTION_MI4 = "MiPush4";
    public static final String ACTION_MI5 = "MiPush5";
   @Bind(R.id.textView18)
    TextView textView18;
   @Bind(R.id.textView20)
    TextView textView20;
   @Bind(R.id.mi2)
    TextView mi2;
   @Bind(R.id.mi)
    TextView mi;
   @Bind(R.id.mi3)
    TextView mi3;
   @Bind(R.id.mi4)
    TextView mi4;
   @Bind(R.id.mi5)
    TextView mi5;
   @Bind(R.id.mi1)
    TextView mi1;
   @Bind(R.id.mi_state)
    TextView miState;
   @Bind(R.id.mi_register)
    TextView miRegister;
   @Bind(R.id.push)
    TextView push;

    private int pushs = 0;
    private int i = 0;
    private int i1 = 0;
    private int i2 = 0;
    private int i3 = 0;
    private int i4 = 0;
    private int i5 = 0;
    private int m = 0;
    private int m1 = 0;
    private int m2 = 0;
    private int m3 = 0;
    private int m4 = 0;
    private int m5 = 0;

    private Timer mTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        registerBoradcastReceiver();

        mTimer = new Timer(true);
        time.setText(0 + "");
        mTimer.schedule(mTimerTask, 1000, 1000);
        push.setText(pushs+"");
        count.setText(i + "");
        count1.setText(i1 + "");
        count2.setText(i2 + "");
        count3.setText(i3 + "");
        count4.setText(i4 + "");
        count5.setText(i5 + "");
        mi.setText(m + "");
        mi1.setText(m1 + "");
        mi2.setText(m2 + "");
        mi3.setText(m3 + "");
        mi4.setText(m4 + "");
        mi5.setText(m5 + "");
        textView18.setText(SharedPreUtil.getString(mContext, "jpush"));
        miRegister.setText(SharedPreUtil.getString(mContext,"mipush"));


        textView20.setText(true + "");
        miState.setText(SharedPreUtil.getString(mContext,"State"));
    }

    private int mCount = 0;
    private TimerTask mTimerTask = new TimerTask() {
        public void run() {
            mCount += 1;
            Message message = new Message();
            message.what = 1;
            message.obj = mCount + "";
            mHandler.sendMessage(message);
        }
    };
//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            time.setText((String) msg.obj);
//        }
//    };

    @OnClick({R.id.button, R.id.button2, R.id.button3, R.id.button4,R.id.mi_stop, R.id.mi_duration, R.id.mi_start, R.id.mi_reset,R.id.reset})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button:
                JPushInterface.stopPush(mContext);
                break;
            case R.id.button2:
                JPushInterface.resumePush(mContext);
                break;
            case R.id.button3:
                i = 0;
                i1 = 0;
                i2 = 0;
                i3 = 0;
                i4 = 0;
                i5 = 0;
                count.setText(i + "");
                count1.setText(i1 + "");
                count2.setText(i2 + "");
                count3.setText(i3 + "");
                count4.setText(i4 + "");
                count5.setText(i5 + "");
                break;
            case R.id.button4:
                mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS1));
                break;

            case R.id.mi_stop:
                MiPushClient.pausePush(Test.this, null);
                break;
            case R.id.mi_duration:
                UserInfo userInfo = mEhomeApplication.getmCurrentUser();
                MiPushClient.setAlias(mContext, userInfo.getmRemarksPhone(), null);
                MiPushClient.subscribe(mContext,"p_" + userInfo.getmPhone(),null);
                MiPushClient.subscribe(mContext,"n_" + userInfo.getmUsername(),null);
                MiPushClient.subscribe(mContext,"x_" + userInfo.getmXiaoqu().getmCommunityId(),null);
                MiPushClient.subscribe(mContext,"q_" + userInfo.getmXiaoqu().getmPeriodsId(),null);
                MiPushClient.subscribe(mContext,"d_" + userInfo.getmXiaoqu().getmUnitId(),null);
                MiPushClient.subscribe(mContext,"m_" + userInfo.getmXiaoqu().getmHouseNumberId(),null);
                break;
            case R.id.mi_start:
                MiPushClient.resumePush(Test.this, null);
                break;
            case R.id.mi_reset:
                m = 0;
                m1 = 0;
                m2 = 0;
                m3 = 0;
                m4 = 0;
                m5 = 0;
                mi.setText( m+ "");
                mi1.setText(m1 + "");
                mi2.setText(m2 + "");
                mi3.setText(m3 + "");
                mi4.setText(m4 + "");
                mi5.setText(m5 + "");
                break;
            case R.id.reset:
                pushs = 0;
                push.setText(pushs+"");
                i = 0;
                i1 = 0;
                i2 = 0;
                i3 = 0;
                i4 = 0;
                i5 = 0;
                count.setText(i + "");
                count1.setText(i1 + "");
                count2.setText(i2 + "");
                count3.setText(i3 + "");
                count4.setText(i4 + "");
                count5.setText(i5 + "");
                m = 0;
                m1 = 0;
                m2 = 0;
                m3 = 0;
                m4 = 0;
                m5 = 0;
                mi.setText( m+ "");
                mi1.setText(m1 + "");
                mi2.setText(m2 + "");
                mi3.setText(m3 + "");
                mi4.setText(m4 + "");
                mi5.setText(m5 + "");
                break;
        }
    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    LogUtils.i(logs);
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    SharedPreUtil.put(mContext, "push", code + "   " + alias + "   " + tags);
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    // 延迟 60 秒来调用 Handler 设置别名
                    PushAliasAndTags bean = new PushAliasAndTags(alias, tags);
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS1), 1000 * 60);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
            }
//			ExampleUtil.showToast(logs, getApplicationContext());
        }
    };
    private static final int MSG_SET_ALIAS1 = 1002;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS1:
                    LogUtils.i("Set alias in handler");
                    // 调用 JPush 接口来设置别名。
//                    PushAliasAndTags bean = (PushAliasAndTags) msg.obj;
                    JPushInterface.setAliasAndTags(getApplicationContext(),
                            "",
                            null,
                            mAliasCallback);
                    break;
                case 1:
                    time.setText((String) msg.obj);
                    break;
                default:
                    LogUtils.i("Unhandled msg - " + msg.what);
//					Log.i(TAG, "Unhandled msg - " + msg.what);
            }
        }
    };

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(ACTION_PUSH);
        myIntentFilter.addAction(ACTION_JPUSH);
        myIntentFilter.addAction(ACTION_JPUSH1);
        myIntentFilter.addAction(ACTION_JPUSH2);
        myIntentFilter.addAction(ACTION_JPUSH3);
        myIntentFilter.addAction(ACTION_JPUSH4);
        myIntentFilter.addAction(ACTION_JPUSH5);
        myIntentFilter.addAction(ACTION_JPUSH6);
        myIntentFilter.addAction(ACTION_MI);
        myIntentFilter.addAction(ACTION_MI1);
        myIntentFilter.addAction(ACTION_MI2);
        myIntentFilter.addAction(ACTION_MI3);
        myIntentFilter.addAction(ACTION_MI4);
        myIntentFilter.addAction(ACTION_MI5);

        // 注册广播
        mContext.registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_PUSH)) {
                pushs++;
                push.setText(pushs + "");
            }else if (action.equals(ACTION_JPUSH)) {
                i++;
                count.setText(i + "");
            }else if (action.equals(ACTION_JPUSH1)) {
                i1++;
                count1.setText(i1 + "");
            } else if (action.equals(ACTION_JPUSH2)) {
                i2++;
                count2.setText(i2 + "");
            } else if (action.equals(ACTION_JPUSH3)) {
                i3++;
                count3.setText(i3 + "");
            } else if (action.equals(ACTION_JPUSH4)) {
                i4++;
                count4.setText(i4 + "");
            } else if (action.equals(ACTION_JPUSH5)) {
                i5++;
                count5.setText(i5 + "");
            }  else if (action.equals(ACTION_JPUSH6)) {
                textView20.setText(intent.getBundleExtra("jpush") + "");
            } else if (action.equals(ACTION_MI)) {
                m++;
                mi.setText(m + "");
            } else if (action.equals(ACTION_MI1)) {
                m1++;
                mi1.setText(m1 + "");
            } else if (action.equals(ACTION_MI2)) {
                m2++;
                mi2.setText(m2 + "");
            } else if (action.equals(ACTION_MI3)) {
                m3++;
                mi3.setText(m3 + "");
            } else if (action.equals(ACTION_MI4)) {
                m4++;
                mi4.setText(m4 + "");
            } else if (action.equals(ACTION_MI5)) {
                m5++;
                mi5.setText(m5 + "");
            }
        }

    };

//    @OnClick(R.id.button4)
//    public void onViewClicked() {
//    }


}
