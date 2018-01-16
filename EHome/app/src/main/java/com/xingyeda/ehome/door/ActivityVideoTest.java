package com.xingyeda.ehome.door;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.xingyeda.ehome.ActivityGuide;
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
import com.xingyeda.ehome.util.MyLog;
import com.xingyeda.ehome.util.NetUtils;
import com.xingyeda.ehome.util.SharedPreUtil;
import com.xingyeda.ehome.view.PercentLinearLayout;
import com.xingyeda.ehome.wifiOnOff.MainActivity;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECInitParams;
import com.yuntongxun.ecsdk.ECVoIPCallManager;
import com.yuntongxun.ecsdk.ECVoIPCallManager.CallType;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.ecsdk.VideoRatio;
import com.yuntongxun.ecsdk.VoipMediaChangedInfo;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;

import static android.drm.DrmStore.Action.PLAY;

/**
 * @ClassName: ActivityVideo
 * @Description: 监控呼叫界面
 * @ 李达龙
 * @date 2016-7-6
 */
public class ActivityVideoTest extends BaseActivity {
    private static final String LOG_TAG = ActivityVideoTest.class.getSimpleName();

    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    private static final int PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1;

    private RtcEngine mRtcEngine;//  教程步骤 1

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() { // 教程步骤1  回调
        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) { //  教程 步骤 5  远端视频接收解码回调
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mCallTimer != null) {
                        mCallTimer.cancel();
                    }
                    setupRemoteVideo(uid);
                    if (mRtcEngine != null) {
                        mRtcEngine.setSpeakerphoneVolume(0);//音量为0
                    }
                    if (mIsVolume!=null) {
                        mIsVolume.setImageResource(R.mipmap.not_volume);
                    }
                    if (mLoading!=null) {
                        mLoading.setVisibility(View.GONE);
                        mAnimation.stop();
                    }
//                    connectRtmp(mJie+"&state=1");
                }
            });
        }

        @Override
        public void onUserOffline(int uid, int reason) { //  教程步骤 7 其他用户离开当前频道回调
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserLeft();
                }
            });
        }

        @Override
        public void onUserMuteVideo(final int uid, final boolean muted) { //  教程步骤 10  其他用户已停发/已重发视频流回调
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserVideoMuted(uid, muted);
                }
            });
        }
    };
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
    @Bind(R.id.video_loading)
    FrameLayout mLoading;
    @Bind(R.id.no_monitoring)
    ImageView mMonitor_abnormal;
    @Bind(R.id.volume)
    RelativeLayout mVolume;
    @Bind(R.id.is_volume)
    ImageView mIsVolume;
    @Bind(R.id.is_call)
    ImageView mCall;
    @Bind(R.id.video_logingimg)
    ImageView mLgingImg;
    @Bind(R.id.VerticalSeekBar)
    SeekBar mVerticalSeekBar;


    private AnimationDrawable mAnimation;
    private static int currVolume = 0;

    private String mDongshuId;
    private String mEquipmentId;
    private String mAddressData;
    private String mHousenum;
    private String mType;
    private String mJie;
    private String mRtmp;
    private String mTime;

    private android.media.MediaPlayer mMediaPlayer;
    private Vibrator mVibrator;// 震动
    private boolean mIsPlayer;// 是否播放音乐
    private boolean mIsvibrator;// 是否震动
    private boolean mIsWifi;
    private boolean mIs3gAnd4;
    private boolean mIsCall;


    private boolean mIsMute  = true;

    private boolean mIsStart = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_video_test);
        ButterKnife.bind(this);
        init();
        //声网初始化
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO) && checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA)) {
            initAgoraEngineAndJoinChannel();
        }
//        //关闭声音
//        CloseSpeaker();



    }


    //初始化声网引擎和加入频道
    private void initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine();     //  教程步骤 1
        setupVideoProfile();         //  教程步骤 2
        setupLocalVideo();           //  教程步骤 3
        joinChannel();               //  教程步骤 4
    }

    //自我检查权限
    public boolean checkSelfPermission(String permission, int requestCode) {
        Log.i(LOG_TAG, "自我检查权限 " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.i(LOG_TAG, "请求权限的结果 " + grantResults[0] + " " + requestCode);

        switch (requestCode) {
            case PERMISSION_REQ_ID_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA);
                } else {
                    showLongToast("没有许可 " + Manifest.permission.RECORD_AUDIO);
                    finish();
                }
                break;
            }
            case PERMISSION_REQ_ID_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initAgoraEngineAndJoinChannel();
                } else {
                    showLongToast("没有许可 " + Manifest.permission.CAMERA);
                    finish();
                }
                break;
            }
        }
    }

    public final void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finishAll();

        leaveChannel();
        RtcEngine.destroy();//销毁引擎实例
        mRtcEngine = null;
    }

    // 教程步骤 10   本地视频开关
    public void onLocalVideoMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            //本地视频上传
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            //本地视频不上传
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }

        mRtcEngine.muteLocalVideoStream(iv.isSelected());

        FrameLayout container = (FrameLayout) findViewById(R.id.local_video_view_container);
        SurfaceView surfaceView = (SurfaceView) container.getChildAt(0);
        surfaceView.setZOrderMediaOverlay(!iv.isSelected());
        surfaceView.setVisibility(iv.isSelected() ? View.GONE : View.VISIBLE);
    }

    // 教程步骤 9  本地音频静音（声控制）
    public void onLocalAudioMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            //解静音
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            //静音
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }

        mRtcEngine.muteLocalAudioStream(iv.isSelected());
    }

    //  教程步骤 8 摄像头切换
    public void onSwitchCameraClicked(View view) {
        mRtcEngine.switchCamera();
    }

    //  教程步骤 6  挂断
    public void onEncCallClicked(View view) {
        finish();
    }

    //  教程步骤 1
    private void initializeAgoraEngine() {
        try {
            /**
             * 创建 RtcEngine 对象。
             * 上下文
             * appid
             * 加入回调
             */
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));

            throw new RuntimeException("需要检查rtc sdk init致命错误\n" + Log.getStackTraceString(e));
        }
    }

    //  教程步骤 2
    private void setupVideoProfile() {
        mRtcEngine.enableVideo();//打开视频模式
        /**
         * 设置本地视频属性
         * 视频属性
         * 是否交换宽高
         */
        mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_360P, false);
    }

    //  教程步骤 3
    private void setupLocalVideo() {
        FrameLayout container = (FrameLayout) findViewById(R.id.local_video_view_container);
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());//创建渲染视图 ----上下文
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);//将渲染视图添加进去
        /**
         * 设置本地视频显示属性
         * VideoCanvas
         *        视频显示视窗
         *        视频显示模式  RENDER_MODE_HIDDEN (1): 如果视频尺寸与显示视窗尺寸不一致，则视频流会按照显示视窗的比例进行周边裁剪或图像拉伸后填满视窗。
         RENDER_MODE_FIT(2): 如果视频尺寸与显示视窗尺寸不一致，在保持长宽比的前提下，将视频进行缩放后填满视窗。
         *        本地用户 ID，与 joinChannel 方法中的 uid 保持一致
         */
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE, 0));
    }

    //  教程步骤 4
    private void joinChannel() {
        /**
         * 房间钥匙：可以为空
         * 房间名称：也就是通道
         * 房间信息：也就是通道信息
         * 用户id ：会在onJoinChannelSuccess返回
         */
        mRtcEngine.muteLocalAudioStream(mIsMute);//静音
        mRtcEngine.joinChannel(null, mEquipmentId, "equipmentId", 0); // 如果你不指定uid,我们会为您生成的uid

    }

    //    //  教程步骤 5  设置远端视频显示属性 （连接）
    private void setupRemoteVideo(int uid) {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);

        if (container.getChildCount() >= 1) {
            return;
        }

        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        container.addView(surfaceView);
        /**
         * 设置本地视频显示属性
         * VideoCanvas
         *        视频显示视窗
         *        视频显示模式
         *        本地用户 ID，与 joinChannel 方法中的 uid 保持一致
         */
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE, uid));

        surfaceView.setTag(uid); // 马克的目的
//        View tipMsg = findViewById(R.id.quick_tips_when_use_agora_sdk); // 可选的界面
//        tipMsg.setVisibility(View.GONE);
    }

    //  教程步骤 6  离开频道
    private void leaveChannel() {
        mRtcEngine.leaveChannel();//离开频道
    }

    //  教程步骤 7  远程用户离开  （挂断）
    private void onRemoteUserLeft() {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);
        container.removeAllViews();

//        View tipMsg = findViewById(R.id.quick_tips_when_use_agora_sdk); // 可选的界面
//        tipMsg.setVisibility(View.VISIBLE);
    }

    //  教程步骤 10   远程用户已停发/已重发视频流
    private void onRemoteUserVideoMuted(int uid, boolean muted) {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);

        SurfaceView surfaceView = (SurfaceView) container.getChildAt(0);

        Object tag = surfaceView.getTag();
        if (tag != null && (Integer) tag == uid) {
            surfaceView.setVisibility(muted ? View.GONE : View.VISIBLE);
        }
    }

    private Timer mCallTimer;//进入页面未处理计时器
    private Timer mTimer;//时间计时器
    private static final int CALLTIMER = 0;
    private static final int RESTARTRTMP = 1;
    private static final int TIMERTASK = 2;
    private void init(){
        //加载动画
        mAnimation = (AnimationDrawable) mLgingImg.getBackground();
        mAnimation.start();

        //来电处理
        TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new OnePhoneStateListener(),
                PhoneStateListener.LISTEN_CALL_STATE);

        //传入数据
        Bundle bundle = getIntent().getExtras();
        mDongshuId = bundle.getString("dongshu");
        mEquipmentId = bundle.getString("eid");
        mHousenum = bundle.getString("housenum");
        mAddressData = bundle.getString("addressData");
        mType = bundle.getString("type");
        mJie = bundle.getString("jie");
        mRtmp = bundle.getString("rtmp");
        mTime = bundle.getString("time");
        if (mType.equals("monitor")) {
            mHangup.setVisibility(View.GONE);
            mConnect.setVisibility(View.GONE);
        }
        PLAYAddress.setText(mAddressData);//设置显示地址

        mIsPlayer = SharedPreUtil.getBoolean(mContext, "vocality");
        mIsvibrator = SharedPreUtil.getBoolean(mContext, "shake");
        mIsWifi = SharedPreUtil.getBoolean(mContext, "wifi");
        mIs3gAnd4 = SharedPreUtil.getBoolean(mContext, "3gAnd4g");
        mIsCall = SharedPreUtil.getBoolean(mContext, "receivecall");

        mCallTimer = new Timer(true);
        if (mType.equals("dial")) {//呼叫处理
            voiceAndShake();
            mCallTimer.schedule(mCallTimerTask, 25000);
        } else if (mType.equals("monitor")) {//监控处理
            mCallTimer.schedule(mCallTimerTask, 60000);
            //声音seekbar监控
            mVerticalSeekBar.setOnSeekBarChangeListener(Listener);
            RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) mVerticalSeekBar.getLayoutParams();
            linearParams.width = mScreenW / 4;
            mVerticalSeekBar.setLayoutParams(linearParams);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mCall.getLayoutParams();
            params.leftMargin = (mScreenW / 4) * 3;
            mCall.setLayoutParams(params);
            mVolume.setVisibility(View.VISIBLE);
            mCall.setVisibility(View.VISIBLE);
            mBack.setVisibility(View.VISIBLE);
        }

    }

    private TimerTask mCallTimerTask = new TimerTask() {
        public void run() {
            if (mType.equals("dial")) {
                mHandler.sendEmptyMessage(CALLTIMER);
            } else if (mType.equals("monitor")) {
                mHandler.sendEmptyMessage(RESTARTRTMP);
            }
        }
    };

    private int mCount = 0;
    private void clock(){
        mCount = 0;
        if (mTimer!=null) {
            mTimer.cancel();
        }
        mTimer = new Timer(true);
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mCount += 1;
            Message message = new Message();
            message.what = TIMERTASK;
            message.obj = getStandardTime(mCount);
            mHandler.sendMessage(message);
            }
        }, 1000,1000);

    }

    @OnClick({R.id.video_back, R.id.is_volume, R.id.is_call, R.id.video_connect, R.id.video_open_door, R.id.video_hangup})
    public void onClick(View v) {
        if (mCallTimer != null) {
            mCallTimer.cancel();
        }
        switch (v.getId()) {
            case R.id.video_back://返回
                finish();
                break;
            case R.id.video_connect://接通
                if (mRtcEngine != null) {
                    mRtcEngine.setSpeakerphoneVolume(255);//音量为0
                    mRtcEngine.setEnableSpeakerphone(true);
                    mRtcEngine.muteLocalAudioStream(false);//静音
                }
                if (mType.equals("dial")) {
                    InformationBase informationBase = new InformationBase();
                    informationBase.setmMessage_status(1);
                    informationBase.setmIsExamine(1);
                    informationBase.updateAll("mTime = ?", mTime);
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
                            if (mJie!=null) {
                                connectRtmp(mJie+"&state=0");
                            }
                        }
                    mConnect.setVisibility(View.GONE);
                    clock();
                }
                break;
            case R.id.video_hangup://挂断
                finish();
                break;
            case R.id.video_open_door://开门
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
                }
                openDoor();
                break;
            case R.id.is_volume://声音进度条的显示
                if (mVerticalSeekBar.isShown()) {
                    mVerticalSeekBar.setVisibility(View.INVISIBLE);
                } else {
                    mVerticalSeekBar.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.is_call://监控呼叫
                if (mIsMute) {
                    if (mRtcEngine != null) {
                        mRtcEngine.setSpeakerphoneVolume(255);//音量为0
                        mRtcEngine.setEnableSpeakerphone(true);
                        mRtcEngine.muteLocalAudioStream(false);//静音
                    }
                    mTimerText.setVisibility(View.VISIBLE);
                    clock();
                    mCall.setImageResource(R.mipmap.hanguping);
                    mIsMute = false;
                    mRtcEngine.muteLocalAudioStream(mIsMute);
                }else{
                    if (mTimer!=null) {
                        mTimer.cancel();
                    }
                    if (mRtcEngine != null) {
                        mRtcEngine.setSpeakerphoneVolume(0);//音量为0
                        mRtcEngine.setEnableSpeakerphone(false);
                        mRtcEngine.muteLocalAudioStream(true);//静音
                    }
                    mTimerText.setVisibility(View.GONE);
                    mCall.setImageResource(R.mipmap.hangup_not);
                    mIsMute = true;
                    mRtcEngine.muteLocalAudioStream(mIsMute);
                }
                break;
        }

    }
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) { switch (msg.what) {
            case CALLTIMER:
                notificationClient();
                showNotification();
                finish();
                break;
            case RESTARTRTMP:
                finish();
                break;
            case TIMERTASK:
                if (mTimerText != null) {
                    mTimerText.setText((String) msg.obj);
                }
                break;
        }

        }

    };

private void finishAll(){
    if (mCallTimer != null) {
        mCallTimer.cancel();
    }
    if (mTimer != null) {
        mTimer.cancel();
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
        closeDoor();
    }

}
    //媒体播放
    private void voiceAndShake() {
        if (mIsPlayer) {
            // 开始播放音乐
            if (mMediaPlayer != null) {
                mMediaPlayer.start();
            }
            mMediaPlayer = android.media.MediaPlayer.create(mContext, R.raw.ring);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();

        }

        if (mIsvibrator) {
            // 震动
            mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {1000, 1000, 1000, 1000};
            mVibrator.vibrate(pattern, 2);
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
            mRtcEngine.setSpeakerphoneVolume(progress*(255/100));
//            mVideoView.setVolume(size / 100, size / 100);
            if (size > 0) {
                mIsVolume.setImageResource(R.mipmap.volume);
                mRtcEngine.setEnableSpeakerphone(true);
            } else {
                mIsVolume.setImageResource(R.mipmap.not_volume);
                mRtcEngine.setEnableSpeakerphone(false);
            }

        }
    };

//时间的显示格式
    public String getStandardTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss",
                Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        Date date = new Date(timestamp * 1000);
        sdf.format(date);
        return sdf.format(date);
    }


    // 打开扬声器
    @SuppressWarnings("deprecation")
    public void OpenSpeaker() {
        try {
            AudioManager audioManager = (AudioManager) ActivityVideoTest.this.getSystemService(Context.AUDIO_SERVICE);
            // 判断扬声器是否在打开
            audioManager.setMode(AudioManager.ROUTE_SPEAKER);
            // 获取当前通话音量
            currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            if (!audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(true);
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                                AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 关闭扬声器
    public void CloseSpeaker() {
        try {
            AudioManager audioManager = (AudioManager) ActivityVideoTest.this
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                if (audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currVolume, AudioManager.STREAM_VOICE_CALL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //来电处理
    class OnePhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            LogUtils.i("[Listener]电话号码:" + incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    LogUtils.i("[Listener]等待接电话:" + incomingNumber);
                    finish();
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
    private void closeDoor() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", SharedPreUtil.getString(mContext, "userId"));
        params.put("eid", mEquipmentId);
        OkHttp.get(mContext, ConnectPath.CLOSEDOOR_PATH, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }));
    }
    private void connectRtmp(String jie) {
        OkHttp.get(mContext, jie, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
            }
        }));
    }
    //通知服务器未接通
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
    //在通知栏发送未接通消息
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

}
