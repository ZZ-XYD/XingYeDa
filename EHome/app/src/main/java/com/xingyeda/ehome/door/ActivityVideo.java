package com.xingyeda.ehome.door;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;


import com.xingyeda.ehome.ActivityGuide;
import com.xingyeda.ehome.ActivityHomepage;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.bean.InformationBase;
import com.xingyeda.ehome.http.okhttp.BaseStringCallback;
import com.xingyeda.ehome.http.okhttp.CallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.LogUtils;
import com.xingyeda.ehome.util.NetUtils;
import com.xingyeda.ehome.util.SharedPreUtil;
import com.xingyeda.ehome.zhibo.ActivityShareMain;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECVoIPCallManager;
import com.yuntongxun.ecsdk.ECVoIPCallManager.CallType;
import com.yuntongxun.ecsdk.VideoRatio;
import com.yuntongxun.ecsdk.VoipMediaChangedInfo;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;

/**
 * @ClassName: ActivityVideo
 * @Description: 监控呼叫界面
 * @ 李达龙
 * @date 2016-7-6
 */
public class ActivityVideo extends BaseActivity {

    @Bind(R.id.video_vitamio_videoView)
    VideoView mVideoView;
    @Bind(R.id.video_open_door)
    ImageView mOpenDoor;
    @Bind(R.id.video_hangup)
    LinearLayout mHangup;
    @Bind(R.id.video_connect)
    LinearLayout mConnect;
    @Bind(R.id.video_address)
    TextView PLAYAddress;
    @Bind(R.id.video_door_timer_text)
    TextView mTimerText;
    @Bind(R.id.video_back)
    TextView mBack;
    //	@Bind(R.id.video_door_upload_text)
//	TextView mUploadText;
    @Bind(R.id.video_loading)
    FrameLayout mLoading;
    @Bind(R.id.no_monitoring)
    ImageView mMonitor_abnormal;
    @Bind(R.id.volume)
    RelativeLayout mVolume;
    //	@Bind(R.id.r_volume)
//	RelativeLayout mRVolume;
    @Bind(R.id.is_volume)
    ImageView mIsVolume;
    @Bind(R.id.is_call)
    ImageView mCall;
    @Bind(R.id.video_logingimg)
    ImageView mLgingImg;
    @Bind(R.id.VerticalSeekBar)
    SeekBar mVerticalSeekBar;

    private Timer mTimer;
    private Timer mCallTimer;
    private Timer mOverTimeTimer;

    private android.media.MediaPlayer mMediaPlayer;
    private Vibrator mVibrator;// 震动
    private boolean mIsPlayer;// 是否播放音乐
    private boolean mIsvibrator;// 是否震动
    private boolean mIsWifi;
    private boolean mIs3gAnd4;
    private boolean mIsCall;

    private boolean mIsCallout;
    private boolean mIsCallOk = false;

    private static final int PLAY = 1;
    private static final int START = 2;
    private static final int TIMERTASK = 3;
    private static final int CALLTIMER = 4;
    private static final int RESTARTRTMP = 5;
    private static final int TIMER = 6;

    private String mDongshuId;
    private String mEquipmentId;
    private String mAddressData;
    private String mHousenum;
    private String mType;
    private String mJie;
    //	private String mEcho;
    private String mRtmp;
    private String mTime;

    private boolean mIsPlayerRtmp = true;
    private boolean mIsNormal = true;
    // private boolean mIscount = true;
    private boolean mIsStart = true;
    private boolean mIsConnect = true;
    private boolean mIsGetRtmp;
    private boolean mIsCall1 = true;
    // private boolean mIsFinish = true;

    private String mCallId;
    private static int currVolume = 0;
    //	private DBManager mDbManager;
    private AnimationDrawable mAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        SharedPreUtil.put(mContext, "isFinish", false);

        Vitamio.isInitialized(getApplicationContext());
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);
        mAnimation = (AnimationDrawable) mLgingImg.getBackground();
        mAnimation.start();
        mIsCallout = false;
//		mDbManager = new DBManager(mContext);
        TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new OnePhoneStateListener(),
                PhoneStateListener.LISTEN_CALL_STATE);

        Bundle bundle = getIntent().getExtras();
        this.mDongshuId = bundle.getString("dongshu");
        this.mEquipmentId = bundle.getString("eid");
        this.mHousenum = bundle.getString("housenum");
        this.mAddressData = bundle.getString("addressData");
        this.mType = bundle.getString("type");
        this.mJie = bundle.getString("jie");
//		this.mEcho = bundle.getString("echo");
        this.mRtmp = bundle.getString("rtmp");
        this.mTime = bundle.getString("time");
        if (mType.equals("monitor")) {
            mHangup.setVisibility(View.GONE);
            mConnect.setVisibility(View.GONE);
        }

        this.init();
        this.event();

        CloseSpeaker();
        preparePlay();

    }

    private void event() {
        ECVoIPCallManager callInterface = ECDevice.getECVoIPCallManager();
        if (callInterface != null) {
            callInterface
                    .setOnVoIPCallListener(new ECVoIPCallManager.OnVoIPListener() {
                        @Override
                        public void onCallEvents(
                                ECVoIPCallManager.VoIPCall voipCall) {
                            // 处理呼叫事件回调
                            if (voipCall == null) {
                                LogUtils.e("SDKCoreHelper",
                                        "处理调用事件错误,voipCall为空");
                                return;
                            }
                            // 根据不同的事件通知类型来处理不同的业务
                            ECVoIPCallManager.ECCallState callState = voipCall.callState;

                            switch (callState) {
                                case ECCALL_PROCEEDING:
                                    LogUtils.i("正在连接服务器处理呼叫请求");
                                    // 正在连接服务器处理呼叫请求
                                    break;
                                case ECCALL_ALERTING:
                                    LogUtils.i("呼叫到达对方客户端，对方正在振铃");
                                    // 呼叫到达对方客户端，对方正在振铃
                                    break;
                                case ECCALL_ANSWERED:
                                    LogUtils.i("对方接听本次呼叫");
                                    mIsCallOk = true;
                                    if (mType.equals("dial")) {
                                        connectRtmp(mJie);
                                        mConnect.setVisibility(View.GONE);
                                        // mIscount = false;
//									mUploadText.setVisibility(View.GONE);
                                        if (mOverTimeTimer != null) {
                                            mOverTimeTimer.cancel();
                                        }
                                        // 对方接听本次呼叫
//									mStart.setVisibility(View.GONE);
                                        mTimerText.setText("00:00:00");
                                        mTimer.schedule(mTimerTask, 1000, 1000); // 延时1000ms后执行，1000ms执行一次
                                    } else if (mType.equals("monitor")) {
                                        mTimerText.setVisibility(View.VISIBLE);
                                        mTimerText.setText("接通中...");
                                        mTimer = new Timer(true);
                                        mTimerText.setText("00:00:00");
                                        mCount = 0;
                                        mTimer.schedule(new TimerTask() {

                                            @Override
                                            public void run() {
                                                mCount += 1;
                                                Message message = new Message();
                                                message.what = TIMERTASK;
                                                message.obj = getStandardTime(mCount);
                                                mHandler.sendMessage(message);
                                            }
                                        }, 1000, 1000);
                                        mCall.setImageResource(R.mipmap.hanguping);
                                        mIsCall1 = true;
                                    }
                                    break;
                                case ECCALL_FAILED:
                                    LogUtils.i("本次呼叫失败，根据失败原因播放提示音:"
                                            + voipCall.reason);
//								for (int i = 0; i < 3; i++) {
//
//								}
                                    if (mIsCallOk) {
                                        closeDoor();
                                        if (171504 == voipCall.reason) {
                                            BaseUtils.showLongToast(mContext, "网络状况不太好哦。");
                                        } else if (171506 == voipCall.reason) {
                                            BaseUtils.showLongToast(mContext, "未知错误");
                                        } else if (175480 == voipCall.reason) {
                                            BaseUtils.showLongToast(mContext, "设备正在使用。");
                                        } else if (170486 == voipCall.reason) {
//									closeDoor();
                                            BaseUtils.showLongToast(mContext, "设备正在使用中。");
                                        } else if (175486 == voipCall.reason) {
//									closeDoor();
                                        } else {
                                            BaseUtils.showLongToast(mContext, "错误代码 : "
                                                    + voipCall.reason);
                                        }
                                        if (mType.equals("monitor")) {
                                            mCall.setImageResource(R.mipmap.callout);
                                            mIsCallout = false;
                                            mIsCall1 = true;
                                            mTimerText.setVisibility(View.VISIBLE);
                                            mTimerText.setText("呼叫失败");
                                        }

                                        // 本次呼叫失败，根据失败原因播放提示音
                                        mTimerText.setText("呼叫无法接通，请稍后再试");
                                    }
                                    break;
                                case ECCALL_RELEASED:
                                    if (mType.equals("monitor")) {
                                        mTimerText.setVisibility(View.GONE);
                                    }
                                    ECDevice.getECVoIPCallManager().releaseCall(
                                            mCallId);
                                    LogUtils.i("通话释放[完成一次呼叫]");
                                    // 通话释放[完成一次呼叫]
                                    break;
                                default:
                                    Log.e("SDKCoreHelper", "handle call event error , callState " + callState);
                                    break;
                            }
                        }

                        @Override
                        public void onSwitchCallMediaTypeRequest(String arg0,
                                                                 CallType arg1) {
                        }

                        @Override
                        public void onSwitchCallMediaTypeResponse(String arg0,
                                                                  CallType arg1) {
                        }

                        @Override
                        public void onVideoRatioChanged(VideoRatio arg0) {
                        }

                        @Override
                        public void onDtmfReceived(String arg0, char arg1) {
                        }

                        @Override
                        public void onMediaDestinationChanged(
                                VoipMediaChangedInfo arg0) {

                        }

                    });
        }
    }

    //	private String show;
    private int count = 0;

    private void preparePlay() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mIsNormal) {
                    // 要做的事情
                    if (mVideoView != null) {
                        if (mVideoView.isPlaying()) {
                            mVideoView.setVolume(0, 0);
                            OpenSpeaker();
                            mIsVolume.setImageResource(R.mipmap.not_volume);
                            mIsNormal = false;
                            LogUtils.i("mRtmp已经播放" + System.currentTimeMillis());
                            // if (mTimer != null) {
                            // mTimer.cancel();
                            // }
                            mLoading.setVisibility(View.GONE);
                            mAnimation.stop();
                            mHandler.sendEmptyMessage(START);
                            mHandler.removeCallbacks(this);
//							mUploadText.setVisibility(View.INVISIBLE);
                            if (mType.equals("monitor")) {
                                if (mCallTimer != null) {
                                    mCallTimer.cancel();
                                }
                                mTimerText.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            if (count >= 6) {
                                count = 0;
                            } else {
                                count++;
//								show = "";
//								for (int i = 0; i < count; i++) {
//									show += ".";
//								}
                            }
//							mUploadText.setText(show);
                            mVideoView.start();
                            preparePlay();
                        }
                    }
                }
            }
        };
        mHandler.postDelayed(runnable, 500);
    }

    // private void count() {
    // Runnable countRunnable = new Runnable() {
    //
    // @Override
    // public void run() {
    // if (mIscount) {
    // if (count >= 6) {
    // count = 0;
    // } else {
    // count++;
    // show = "";
    // for (int i = 0; i < count; i++) {
    // show += ".";
    // }
    // }
    // mUploadText.setText(show);
    // count();
    // } else {
    // mHandler.removeCallbacks(this);
    // }
    // }
    //
    // };
    //
    // mHandler.postDelayed(countRunnable, 1000);
    // }

    private void voiceAndShake() {
        if (mIsPlayer) {
            // 开始播放音乐
            if (mMediaPlayer != null) {
                mMediaPlayer.start();
            }
            mMediaPlayer = android.media.MediaPlayer.create(mContext, R.raw.ring);
//			mMediaPlayer.reset();
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();

//			try {
//				mMediaPlayer.setDataSource(ActivityVideo.this, RingtoneManager
//						.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
//				mMediaPlayer.prepare();
//				mMediaPlayer.start();
//			} catch (IllegalStateException e) {
//	            e.printStackTrace();
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        }
        }

        if (mIsvibrator) {
            // 震动
            mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {1000, 1000, 1000, 1000};
            mVibrator.vibrate(pattern, 2);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (mType.equals("dial")) {
                if (mCallTimer != null) {
                    mCallTimer.cancel();
                }
                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
                if (mVibrator != null) {
                    mVibrator.cancel();
                }
                CloseSpeaker();
                closeDoor();
                if (mTimer != null) {
                    mTimer.cancel();
                }
            } else if (mType.equals("monitor")) {
//				ECDevice.getECVoIPCallManager().releaseCall(mCallId);
                if (mCallTimer != null) {
                    mCallTimer.cancel();
                }
            }
            // mIsFinish = false;
            finishThis();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void init() {

        mVerticalSeekBar.setOnSeekBarChangeListener(Listener);
        if (mType.equals("dial")) {
            mTimer = new Timer(true);
            mOverTimeTimer = new Timer(true);
            mCallTimer = new Timer(true);
            mCallTimer.schedule(mCallTimerTask, 25000);
        } else if (mType.equals("monitor")) {
            mTimer = new Timer(true);
            mCallTimer = new Timer(true);
            mOverTimeTimer = new Timer(true);
            mCallTimer.schedule(mCallTimerTask, 60000);
        }

        PLAYAddress.setText(mAddressData);

        this.mIsPlayer = SharedPreUtil.getBoolean(mContext, "vocality");
        this.mIsvibrator = SharedPreUtil.getBoolean(mContext, "shake");
        this.mIsWifi = SharedPreUtil.getBoolean(mContext, "wifi");
        this.mIs3gAnd4 = SharedPreUtil.getBoolean(mContext, "3gAnd4g");
        this.mIsCall = SharedPreUtil.getBoolean(mContext, "receivecall");

        if (mType.equals("dial")) {
            mEhomeApplication.addMap(mEquipmentId, mRtmp);
            LogUtils.i("mRtmp开始播放" + System.currentTimeMillis());
            if (mIsWifi && NetUtils.isWifi(mContext)) {
                playfunction(mRtmp);
            } else if (mIs3gAnd4 && NetUtils.isConnected(mContext)) {
                playfunction(mRtmp);
            }
            LogUtils.i("声音  :" + mIsPlayer + "         震动  :" + mIsvibrator);
            voiceAndShake();
//			videoCallBack();
        } else if (mType.equals("monitor")) {
//			RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) mVolume.getLayoutParams();
//			params.rightMargin = mScreenW/5;
//			mVolume.setLayoutParams(params);
            RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) mVerticalSeekBar.getLayoutParams();
            linearParams.width = mScreenW / 4;
            mVerticalSeekBar.setLayoutParams(linearParams);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mCall.getLayoutParams();
            params.leftMargin = (mScreenW / 4) * 3;
            mCall.setLayoutParams(params);
//			mRVolume.setVisibility(View.VISIBLE);
            mVolume.setVisibility(View.VISIBLE);
            mCall.setVisibility(View.VISIBLE);
            mBack.setVisibility(View.VISIBLE);
            // if (mIsCallout) {
            // mCall.setImageResource(R.drawable.hangup);
            // }else {
            // mCall.setImageResource(R.drawable.callout);
            // }
//			mCancel.setText("关闭");
            Map<String, String> map = mEhomeApplication.getMap();
            Iterator<Map.Entry<String, String>> entries = map.entrySet()
                    .iterator();
            while (entries.hasNext()) {
                Map.Entry<String, String> entry = entries.next();
                if (mDongshuId.equals(entry.getKey())) {
                    if (entry.getValue() != null
                            && entry.getValue().length() > 0) {
                        mIsGetRtmp = false;
                        playfunction(entry.getValue());
                    } else {
                        mIsGetRtmp = true;
                    }
                } else {
                    mIsGetRtmp = true;
                }
            }
            if (mIsGetRtmp) {
                getRtmp();
            }
            // playfunction("rtmp://play.bcelive.com/live/lss-ggks5kzxkfdx42i9");
//			mStart.setText("开门 ");
//			mTimerText.setText("连接中");
//			mConfirm.setVisibility(View.GONE);
        }

        if (mEhomeApplication.ismIsWifi()) {
            mIsPlayerRtmp = mIsWifi;
        } else if (mEhomeApplication.ismIsMobile()) {
            mIsPlayerRtmp = mIs3gAnd4;
        }

    }

    private void getRtmp() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("dongshu", mDongshuId);
        OkHttp.get(mContext, ConnectPath.VIDEO_PATH, params, new BaseStringCallback(mContext, new CallbackHandler<String>() {

            @Override
            public void parameterError(JSONObject response) {
                if (mTimer != null) {
                    mTimer.cancel();
                }
                mIsNormal = false;
                if (mCallTimer != null) {
                    mCallTimer.cancel();
                }
                if (mMonitor_abnormal != null) {
                    mMonitor_abnormal.setVisibility(View.VISIBLE);
                }
                if (mLoading != null) {
                    mLoading.setVisibility(View.GONE);
                    mAnimation.stop();
                }
            }

            @Override
            public void onResponse(JSONObject response) {
                try {
                    mEhomeApplication.addMap(mEquipmentId,
                            response.getString("obj"));
                    Message message = new Message();
                    message.what = PLAY;
                    message.obj = response.getString("obj");
                    mHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure() {
                mIsNormal = false;
            }
        }));
    }

    @OnClick({R.id.video_back, R.id.is_volume, R.id.is_call, R.id.video_connect, R.id.video_open_door, R.id.video_hangup})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_back:
                mIsNormal = false;
                // mVideoView.stopPlayback();
                if (mType.equals("dial")) {
                    CloseSpeaker();
                    closeDoor();
                    if (mTimer != null) {
                        mTimer.cancel();
                    }
                } else if (mType.equals("monitor")) {
                    if (ECDevice.isInitialized() && mCallId != null) {
                        ECDevice.getECVoIPCallManager().releaseCall(mCallId);
                    }
                }
                // mIsFinish = false;
                finishThis();
                break;
            case R.id.video_connect:
                if (mType.equals("dial")) {

                    InformationBase informationBase = new InformationBase();
                    informationBase.setmMessage_status(1);
                    informationBase.setmIsExamine(1);
                    informationBase.updateAll("mTime = ?", mTime);
//				mDbManager.updateConnect(mTime);
//				mDbManager.updateCheck(mTime);
                    if (mIsConnect) {
                        mIsConnect = false;
                        if (mCallTimer != null) {
                            mCallTimer.cancel();
                        }
                        mOverTimeTimer.schedule(mOverTimeTimerTask, 10000);
                        if (mMediaPlayer != null) {
                            mMediaPlayer.stop();
                            mMediaPlayer.release();
                            mMediaPlayer = null;
                        }
                        if (mVibrator != null) {
                            mVibrator.cancel();
                        }
                        if (mIsCall) {
                            mTimerText.setText("连接中");
                            // count();
                            // connectRtmp(mJie);
                            if (mCallId != null) {
                                ECDevice.getECVoIPCallManager().releaseCall(
                                        mCallId);
                                mCallId = null;
                            }
                            if (ECDevice.isInitialized()) {
                                mCallId = ECDevice.getECVoIPCallManager().makeCall(CallType.VOICE, mEquipmentId);
                                LogUtils.i("eid : " + mEquipmentId);
                                LogUtils.i("CallId : " + mCallId);
                                if (!ECDevice.getECVoIPSetupManager().getLoudSpeakerStatus()) {
                                    ECDevice.getECVoIPSetupManager().enableLoudSpeaker(
                                            true);
                                }
                            }
                        }
                    }
                }
                break;
            case R.id.video_hangup:
                if (mCallTimer != null) {
                    mCallTimer.cancel();
                }
                if (mType.equals("dial")) {
                    if (mMediaPlayer != null) {
                        mMediaPlayer.stop();
                        mMediaPlayer.release();
                        mMediaPlayer = null;
                    }
                    if (mVibrator != null) {
                        mVibrator.cancel();
                    }
                    CloseSpeaker();
                    closeDoor();
                    if (mTimer != null) {
                        mTimer.cancel();
                    }
                    if (mOverTimeTimer != null) {
                        mOverTimeTimer.cancel();
                    }
                } else if (mType.equals("monitor")) {
                    if (mCallId != null) {
                        ECDevice.getECVoIPCallManager().releaseCall(mCallId);
                    }
                }
                // mIsFinish = false;
                finishThis();
                break;
            case R.id.video_open_door:
                if (mType.equals("dial")) {
                    if (mMediaPlayer != null) {
                        mMediaPlayer.stop();
                        mMediaPlayer.release();
                        mMediaPlayer = null;
                    }
                    if (mVibrator != null) {
                        mVibrator.cancel();
                    }
                    CloseSpeaker();

                    InformationBase informationBase = new InformationBase();
                    informationBase.setmDoor_status(1);
                    informationBase.updateAll("mTime=?", mTime);
//				mDbManager.updateOpenDoor(mTime);
                }
                openDoor();
                break;
            case R.id.is_volume:
                if (mVerticalSeekBar.isShown()) {
                    mVerticalSeekBar.setVisibility(View.INVISIBLE);
                } else {
                    mVerticalSeekBar.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.is_call:
                if (mIsCall1) {
                    if (mIsCallout) {
                        mCall.setImageResource(R.mipmap.hangup_not);
                        mIsCallout = false;
                        if (mTimer != null) {
                            mTimer.cancel();
                        }
                        closeDoor();
                        ECDevice.getECVoIPCallManager().releaseCall(mCallId);
                    } else {
                        mTimerText.setVisibility(View.VISIBLE);
                        mTimerText.setText("呼叫中...");
                        mCall.setImageResource(R.mipmap.hangup);
                        mHandler.postDelayed(new Runnable() {
                            public void run() {
                                mIsCallout = true;
                                mIsCall1 = false;
                                if (mCallId != null) {
                                    ECDevice.getECVoIPCallManager().releaseCall(
                                            mCallId);
                                    mCallId = null;
                                }
                                if (ECDevice.isInitialized()) {
                                    mCallId = ECDevice.getECVoIPCallManager().makeCall(CallType.VOICE, mEquipmentId);
                                }
                            }
                        }, 3000);
                    }
                }
                break;
        }

    }

    private OnSeekBarChangeListener Listener = new OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            float size = progress;
            mVideoView.setVolume(size / 100, size / 100);
            if (size > 0) {
                mIsVolume.setImageResource(R.mipmap.volume);
            } else {
                mIsVolume.setImageResource(R.mipmap.not_volume);
            }
            // verticalText.setText(Integer.toString(progress));

        }
    };

    private int mCount = 0;
    private TimerTask mTimerTask = new TimerTask() {
        public void run() {
            mCount += 1;
            Message message = new Message();
            message.what = TIMERTASK;
            message.obj = getStandardTime(mCount);
            mHandler.sendMessage(message);
        }
    };
    public TimerTask mOverTimeTimerTask = new TimerTask() {
        public void run() {
            mHandler.sendEmptyMessage(TIMER);
        }
    };
    private TimerTask mCallTimerTask = new TimerTask() {
        public void run() {
            if (mType.equals("dial")) {
                mHandler.sendEmptyMessage(CALLTIMER);
            } else if (mType.equals("monitor")) {
                mHandler.sendEmptyMessage(RESTARTRTMP);
            }
        }
    };

//    private void restartRtmp() {
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("eid", mEquipmentId);
//        OkHttp.get(mContext, ConnectPath.RESTARTRTMP_PATH, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
//            @Override
//            public void onResponse(JSONObject response) {
//            }
//        }));
//    }

    public String getStandardTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss",
                Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        Date date = new Date(timestamp * 1000);
        sdf.format(date);
        return sdf.format(date);
    }

    private void connectRtmp(String jie) {
        OkHttp.get(mContext, jie, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
            }
        }));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (SharedPreUtil.getBoolean(mContext, "isFinish")) {
            finishThis();
        }
        // if (mIsFinish) {
        // finishThis();
        // }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCallId != null) {
            ECDevice.getECVoIPCallManager().releaseCall(mCallId);
        }
        for (Activity activity : mEhomeApplication.getActivityStack()) {
            if (activity.getClass().equals(ActivityGuide.class)) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            }
        }
        if (mType.equals("dial")) {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            if (mVibrator != null) {
                mVibrator.cancel();
            }
            CloseSpeaker();
            if (mTimer != null) {
                mTimer.cancel();
            }
            if (mCallTimer != null) {
                mCallTimer.cancel();
            }
            ButterKnife.unbind(this);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PLAY:
                    playfunction((String) msg.obj);
                    break;
                case TIMERTASK:
                    if (mTimerText != null) {
                        mTimerText.setText((String) msg.obj);
                    }
                    break;
                case CALLTIMER:
                    if (mMediaPlayer != null) {
                        mMediaPlayer.stop();
                        mMediaPlayer.release();
                        mMediaPlayer = null;
                    }
                    if (mVibrator != null) {
                        mVibrator.cancel();
                    }
                    CloseSpeaker();
                    closeDoor();
                    notificationClient();
                    showNotification();
                    // mIsFinish = false;
                    finishThis();
                    break;
                case RESTARTRTMP:
//				if (mVideoView!=null) {
                    if (!mVideoView.isPlaying()) {
                        if (mMonitor_abnormal != null) {
                            mMonitor_abnormal.setVisibility(View.VISIBLE);
                            mTimerText.setText("设备连接异常");
                        }
                    }
//				}
                    mIsNormal = false;
                    if (mLoading != null) {
                        mLoading.setVisibility(View.GONE);
                        mAnimation.stop();
                    }
                    // mVideoView.stopPlayback();
//				if (mUploadText!=null) {
//					mUploadText.setVisibility(View.GONE);
//				}
                    BaseUtils.showShortToast(ActivityVideo.this,
                            R.string.playback_failed);
//                    restartRtmp();
                    break;
                case TIMER:
                    if (mVideoView != null) {
                        if (!mVideoView.isPlaying()) {
                            mMonitor_abnormal.setVisibility(View.VISIBLE);
                        }
                    }
                    mLoading.setVisibility(View.GONE);
                    mAnimation.stop();
                    mIsNormal = false;
                    // mVideoView.stopPlayback();
                    mTimerText.setText("监控被占用，请稍后再试");
                    if (mType.equals("dial")) {
                        mTimerText.setText("重新连接中...");
                        if (mCallId != null) {
                            ECDevice.getECVoIPCallManager().releaseCall(
                                    mCallId);
                            mCallId = null;
                        }
                        ECDevice.getECVoIPCallManager().releaseCall(mCallId);
                        if (ECDevice.isInitialized()) {
                            mCallId = ECDevice.getECVoIPCallManager().makeCall(CallType.VOICE, mEquipmentId);
                        }
                    } else if (mType.equals("monitor")) {
                    }
//				mUploadText.setVisibility(View.GONE);
                    break;
            }
        }

    };

    private void notificationClient() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", SharedPreUtil.getString(mContext, "userId", ""));
        params.put("eid", mEquipmentId);
        OkHttp.get(mContext, ConnectPath.NOTIFICATION_CLIENT_PATH, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
            }
        }));
    }

    private void playfunction(String path) {
        if (path == "") {
            BaseUtils.showShortToast(mContext, R.string.play_address_error);
            return;
        } else {
            rtmpPlay(path);
        }

    }

    private void rtmpPlay(String path) {
        LogUtils.i("RTMP的播放地址 : " + path);
        if (mIsPlayerRtmp) {
            if (mVideoView != null) {

                mVideoView.setBackgroundResource(0);
                mVideoView.setVideoPath(path);
                mVideoView.setBufferSize(128);
                mVideoView.requestFocus();
                mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0);
                mVideoView
                        .setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {
                                mediaPlayer.setPlaybackSpeed(1.0f);
                            }
                        });
            }
        }
    }

//	private void videoCallBack() {
//		OkHttp.get(mContext,mEcho, new BaseStringCallback(mContext, new CallbackHandler<String>() {
//			
//			@Override
//			public void parameterError(JSONObject response) {
//			}
//			
//			@Override
//			public void onResponse(JSONObject response) {
//			}
//			
//			@Override
//			public void onFailure() {
//			}
//		}));
//	}

    private void closeDoor() {
        if (mCallId != null) {
            ECDevice.getECVoIPCallManager().releaseCall(mCallId);
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", SharedPreUtil.getString(mContext, "userId"));
        params.put("eid", mEquipmentId);
        OkHttp.get(mContext, ConnectPath.CLOSEDOOR_PATH, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mCallId != null) {
                    ECDevice.getECVoIPCallManager()
                            .releaseCall(mCallId);
                }
            }
        }));
    }

    private void openDoor() {
        LogUtils.i("开门和关门设置 : " + mIsStart);
        if (mIsStart) {
            mIsStart = false;
            // 5秒之后开门和关门设置可用
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    mIsStart = true;
                    if (mOpenDoor != null) {
                        mOpenDoor.setImageDrawable(getResources().getDrawable(R.mipmap.open_door));
                    }
                }
            }, 5000);
            Map<String, String> params = new HashMap<String, String>();
            params.put("uid", SharedPreUtil.getString(mContext, "userId"));
            params.put("eid", mEquipmentId);
            params.put("dongshu", mDongshuId);
            params.put("housenum", mHousenum);
            OkHttp.get(mContext, ConnectPath.OPENDOOR_PATH, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
                @Override
                public void onResponse(JSONObject response) {
                    BaseUtils.showShortToast(mContext, R.string.open_door_prosperity);
                    mOpenDoor.setImageDrawable(getResources().getDrawable(R.mipmap.open_door_open));
                }
            }));

        }
    }

    // 打开扬声器
    @SuppressWarnings("deprecation")
    public void OpenSpeaker() {
        try {

            AudioManager audioManager = (AudioManager) ActivityVideo.this
                    .getSystemService(Context.AUDIO_SERVICE);
            ;

            // 判断扬声器是否在打开
            audioManager.setMode(AudioManager.ROUTE_SPEAKER);

            // 获取当前通话音量
            currVolume = audioManager
                    .getStreamVolume(AudioManager.STREAM_VOICE_CALL);

            if (!audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(true);

                audioManager
                        .setStreamVolume(
                                AudioManager.STREAM_VOICE_CALL,
                                audioManager
                                        .getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                                AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 关闭扬声器
    public void CloseSpeaker() {

        try {
            AudioManager audioManager = (AudioManager) ActivityVideo.this
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                if (audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setStreamVolume(
                            AudioManager.STREAM_VOICE_CALL, currVolume,
                            AudioManager.STREAM_VOICE_CALL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showNotification() {
        long[] VIBRATE = {0, 500};
        Intent resultIntent = new Intent();
        resultIntent
                .setAction("com.sec.android.app.simrecord.CLEAR_NOTI_ACTION");
        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(this, 0,
                resultIntent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.mipmap.ic_launcher) // 设置图标
                .setContentTitle(getResources().getString(R.string.call_not_connect)) // 设置标题
                .setContentText(getResources().getString(R.string.call_not_connect_hint)) // 设置在下拉菜单中的显示内容
                .setVibrate(VIBRATE) // 设置通知到来时的震动提示
                .setTicker(getResources().getString(R.string.not_connect_message)) // 设置在最顶端的显示内容
                .setAutoCancel(true).setContentIntent(resultPendingIntent); // 设置点击通知时，要触发的activity或者broadcast

        @SuppressWarnings("static-access")
        NotificationManager mNotifiManager = (NotificationManager) this
                .getSystemService(this.NOTIFICATION_SERVICE);
        mNotifiManager.notify(0, mBuilder.build()); // 发出通知
    }

    class OnePhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            LogUtils.i("[Listener]电话号码:" + incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    LogUtils.i("[Listener]等待接电话:" + incomingNumber);
                    if (mType.equals("dial")) {
                        if (mCallTimer != null) {
                            mCallTimer.cancel();
                        }
                        if (mMediaPlayer != null) {
                            mMediaPlayer.stop();
                            mMediaPlayer.release();
                            mMediaPlayer = null;
                        }
                        if (mVibrator != null) {
                            mVibrator.cancel();
                        }
                        CloseSpeaker();
                        closeDoor();
                        if (mTimer != null) {
                            mTimer.cancel();
                        }

                    } else if (mType.equals("monitor")) {
                        if (mCallTimer != null) {
                            mCallTimer.cancel();
                        }
                        closeDoor();
                    }
                    // mIsFinish = false;
                    finishThis();
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    LogUtils.i("[Listener]电话挂断:" + incomingNumber);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    LogUtils.i("[Listener]通话中:" + incomingNumber);
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    private void finishThis() {
        // mEhomeApplication.setmIsCallOut(false);
        mIsNormal = false;
        // mVideoView.stopPlayback();
        if (null != mVideoView) {
            if (!mIsNormal && !mVideoView.isPlaying()) {
                ActivityVideo.this.finish();
            }
            if (!mIsNormal && mVideoView.isPlaying()) {
                ActivityVideo.this.finish();
            }
        }
    }


}
