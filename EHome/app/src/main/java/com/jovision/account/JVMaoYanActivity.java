package com.jovision.account;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.jovision.FileUtil;
import com.jovision.JVNetConst;
import com.jovision.Jni;
import com.jovision.JniUtil;
import com.jovision.PlayUtil;
import com.jovision.base.IHandlerLikeNotify;
import com.jovision.base.IHandlerNotify;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.EHomeApplication;
import com.xingyeda.ehome.bean.InformationBase;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.LogUtils;
import com.xingyeda.ehome.util.LogcatHelper;
import com.xingyeda.ehome.view.PercentLinearLayout;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;

import static android.R.attr.version;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static com.baidu.location.b.g.W;
import static com.baidu.location.b.g.w;
import static com.jovision.JVNetConst.CALL_CATEYE_ADDRESS;
import static com.jovision.JVNetConst.CALL_CATEYE_ONLINE;
import static com.jovision.PlayUtil.checkStreamRemoteImage;
import static com.xingyeda.ehome.R.string.versions;
import static com.xingyeda.ehome.base.BaseActivity.mScreenH;


public class JVMaoYanActivity extends BaseActivity implements IHandlerNotify, IHandlerLikeNotify,RadioGroup.OnCheckedChangeListener {

    public static final String SD_CARD_PATH = Environment
            .getExternalStorageDirectory().getPath() + File.separator;
    public static final String CAT_STREAM_PATH = SD_CARD_PATH + "0strMedia0"
            + File.separator + "catstream" + File.separator;

    protected MyHandler handler = new MyHandler(this);


    private IHandlerNotify handlerNotify = this;

    private int bellLight;//感应门铃按键灯开关：0关，1开
    private int alarmType;//报警类型：0图片，1视频
    private int pirEnable;//红外感应开关：0关，1开
    private int gSensor;//重力感应：0关，1开
    private int mDetect;//移动侦测：0关，1开
    //SD卡总大小，已使用，存储分辨率，录像时间，自动覆盖开关
    private int totalSize,  autoSwitch;
    //    网络校时开关，时间显示格式，时区
    private int mSntp; //0关闭，1开启
//    //当前码流 1：超清  2：高清  3：流畅
//    private int currentStream = 0;
    //是否正在录像
    private boolean recording = false;
    //是否正在监听
    private boolean enableSound = false;




    private TextView linkState;//视频连接状态
    private boolean suspendSwitch = false;
    private JSONObject allJson;
    private final int CALL_CATEYE_CONNECTED = 0xD3;//视频连接成功
    private final int CALL_CATEYE_DISCONNECTED = 0xD5;//视频断开
    private final int CALL_NEW_PICTURE = 0xA9;//I帧
    int window = 0;

    //     /mnt/misc/20150101/A01080000.jpg
//    String ystNum = "C200273165";
        String ystNum ;
    SurfaceView playSurface;//
    SurfaceHolder surfaceHolder;//
    private boolean isConnected = false;//是否已连接

    private TextView mAddress;
    private TextView mCodeRate;
    private TextView mSuper;
    private TextView mHigh;
    private TextView mFluency;
//    private TextView mBack;
    private ImageView mJVMYPhotograph;
    private ImageView mJVMYTalkback;
//    private ImageView mRewind;
    private  String type;
    private  String time;
    private PercentLinearLayout mLayout;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String devNum = getIntent().getExtras().getString("cameraId");
        type = getIntent().getExtras().getString("type");
        if ("guest".equals(type)) {
            time = getIntent().getExtras().getString("time");
        }
        ystNum = devNum;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ((EHomeApplication) getApplication()).setCurrentNotifyer(this);

        initSettings();
        initUi();
    }

    protected void initSettings() {
    }


    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
//                case  R.id.jvmy_back:
//                    finish();
//                    break;
                case  R.id.jvmv_voice:
                    capture();

                    break;
                case  R.id.jvmy_talkbacks:
                    call();
                    break;
//                case  R.id.maoyan_rewind:
//                    Bundle bundle = new Bundle();
//                    bundle.putString("type","maoyan");
//                    BaseUtils.startActivities(mContext,VideoRewindActivity.class,bundle);
//                    break;
                case  R.id.maoyan_code_rate_text:
                    if (mLayout.isShown()) {
                        mLayout.setVisibility(View.GONE);
                    }else{
                    mLayout.setVisibility(View.VISIBLE);
                    }
                    break;
                case  R.id.maoyan_super_definition:
                    currentStream = 1;
                    PlayUtil.streamCatChangeStream(window, currentStream);
                    if (mLayout.isShown()) {
                        mLayout.setVisibility(View.GONE);
                    }
                    mCodeRate.setText("超清");
                    break;
                case  R.id.maoyan_high_definition:
                    currentStream = 2;
                    PlayUtil.streamCatChangeStream(window, currentStream);
                    if (mLayout.isShown()) {
                        mLayout.setVisibility(View.GONE);
                    }
                    mCodeRate.setText("高清");
                    break;

                case  R.id.maoyan_fluency_definition:
                    currentStream = 3;
                    PlayUtil.streamCatChangeStream(window, currentStream);
                    if (mLayout.isShown()) {
                        mLayout.setVisibility(View.GONE);
                    }
                    mCodeRate.setText("流畅");
                    break;


            }
        }
    };

    private void init() {
        setContentView(R.layout.activity_maoyan_play);
        FileUtil.createDirectory(new File(CAT_STREAM_PATH));
        playSurface = (SurfaceView) findViewById(R.id.maoyan_playsurface);


        mAddress = (TextView) findViewById(R.id.jv_maoyan_address);
        mCodeRate = (TextView) findViewById(R.id.maoyan_code_rate_text);
        mSuper = (TextView) findViewById(R.id.maoyan_super_definition);
        mHigh = (TextView) findViewById(R.id.maoyan_high_definition);
        mFluency = (TextView) findViewById(R.id.maoyan_fluency_definition);
        mLayout = (PercentLinearLayout) findViewById(R.id.maoyan_code_rate_show);
        mAddress.setText(ystNum);
//        mBack = (TextView) findViewById(R.id.jvmy_back);
        mJVMYPhotograph = (ImageView) findViewById(R.id.jvmv_voice);
        mJVMYTalkback = (ImageView) findViewById(R.id.jvmy_talkbacks);
//        mRewind = (ImageView) findViewById(R.id.maoyan_rewind);



//        mBack.setOnClickListener(listener);
        mJVMYPhotograph.setOnClickListener(listener);
        mJVMYTalkback.setOnClickListener(listener);
//        mRewind.setOnClickListener(listener);
        mCodeRate.setOnClickListener(listener);
        mSuper.setOnClickListener(listener);
        mHigh.setOnClickListener(listener);
        mFluency.setOnClickListener(listener);





//        ViewGroup.LayoutParams para = playSurface.getLayoutParams();
////        para.width=;//修改宽度
//        para.height = mScreenH / 2;//修改高度
//        playSurface.setLayoutParams(para);

        surfaceHolder = playSurface.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (isConnected) {
                    PlayUtil.resumeSurface(window, surfaceHolder.getSurface());
                } else {
                    linkState.setText("连接中...");
                    int result = PlayUtil.streamCatConnect(window, surfaceHolder.getSurface(), ystNum);

                    if (0 == result) {
                        linkState.setText("连接成功");
//                        int  a = PlayUtil.startStreamCatDownload(window,"/mnt/20150101/A01080000.jpg");
//			            LogUtils.d(a+"");
                    } else if (-1 == result) {
                        linkState.setText("连接失败");
                    }
//                    makeText(JVMaoYanActivity.this, ystNum + "--连接开始", LENGTH_SHORT).show();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
        linkState = (TextView) findViewById(R.id.linkState);
    }



    protected void initUi() {
        FileUtil.createDirectory(new File(CAT_STREAM_PATH));
        int ret1 = PlayUtil.searchStreamCatOnLineServer(CAT_STREAM_PATH,
                new String[]{ystNum});
        if (ret1 == -1) {//获取地址失败
            Toast.makeText(JVMaoYanActivity.this, "获取地址失败", Toast.LENGTH_SHORT).show();
        } else if (ret1 == 0) {//本地没有，需去服务器请求数据

        } else {//本地保存，直接取得
//            Toast.makeText(Test1.this, "获取本地地址-调用猫眼上线", Toast.LENGTH_SHORT).show();
            PlayUtil.streamCatOnLine(ystNum, CAT_STREAM_PATH);
        }
    }

    @Override
    protected void onResume() {
        if (isConnected) {
            PlayUtil.resumeSurface(window, surfaceHolder.getSurface());
        }
        ((EHomeApplication) getApplication()).setCurrentNotifyer(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (isConnected) {
            PlayUtil.pauseSurface(window);
        }
        super.onPause();
    }
    //当前码流 1：超清  2：高清  3：流畅
    private int currentStream = 0;

    //码流切换按钮
    public void changeStream() {
        Log.e("CATCAT", "currentStream:" + currentStream);
        int changeStream = ++currentStream > 3 ? 1 : currentStream;
        currentStream = changeStream;
        Log.e("CATCAT", "change To:" + changeStream);
        PlayUtil.streamCatChangeStream(window, changeStream);
    }

    @Override
    public void onHandler(int what, int arg1, int arg2, Object obj) {
//        Log.e(getLocalClassName(), "onHandler: " + getLocalClassName());
        switch (what) {
            case CALL_CATEYE_ADDRESS://获取设备地址回调
                PlayUtil.streamCatOnLine(ystNum, CAT_STREAM_PATH);
//                showDialog("猫眼设备是否在线...");
                break;
            case CALL_CATEYE_ONLINE://获取猫眼设备是否在线回调

//                Toast.makeText(JVMaoYanActivity.this, "获取猫眼设备是否在线回调结果，arg2="+arg2, Toast.LENGTH_SHORT).show();
                switch (arg2) {
                    case -1:
                        Looper.prepare();
                        Toast.makeText(JVMaoYanActivity.this,
                                "与服务器通信失败!", Toast.LENGTH_LONG).show();
                        Looper.loop();
                        return;
                    case 0:
                        Looper.prepare();
                        Toast.makeText(JVMaoYanActivity.this,
                                "设备不在线!", Toast.LENGTH_LONG).show();
                        Looper.loop();
                        return;
                    case 1:
                        break;
                }
                init();

            case CALL_CATEYE_CONNECTED: {//连接结果
                //arg1:窗口   arg2:1 连接成功;2 连接失败
                if (1 == arg2) {
                    linkState.setText("连接成功，缓冲中...");
//                    makeText(JVMaoYanActivity.this, ystNum + "--连接成功", LENGTH_SHORT).show();
                } else if (2 == arg2) {
                    linkState.setText("连接失败");
//                    makeText(JVMaoYanActivity.this, ystNum + "--连接失败", LENGTH_SHORT).show();
                }
                break;
            }
            case CALL_CATEYE_DISCONNECTED: {//异常断开
                isConnected = false;
                linkState.setText("连接异常断开");
//                makeText(JVMaoYanActivity.this, ystNum + "异常断开", LENGTH_SHORT).show();
                PlayUtil.streamCatDisconnect(window);
                break;
            }
            case CALL_NEW_PICTURE: {//视频连接出图
                isConnected = true;
                linkState.setVisibility(View.GONE);
//                makeText(JVMaoYanActivity.this, ystNum + "--出图像", LENGTH_SHORT).show();
//                if ("guest".equals(type)) {
//                    call();
//                    String capturePath = LogcatHelper.getPATH_LOGCAT() + "/"+System.currentTimeMillis() + ".jpg";
//                    PlayUtil.capture(window, capturePath);
//                    InformationBase base = new InformationBase();
//                    base.setmImage(capturePath);
//                    base.updateAll("mTime = ? ",time);
//                }

                break;
            }
            //流媒体猫眼，设置协议回调
            case JVNetConst.CALL_CATEYE_SENDDATA: {
                try {
                    org.json.JSONObject object = new org.json.JSONObject(obj.toString());
                    int nType = object.getInt("nPacketType");
                    int nCmd = object.getInt("nCmd");
                    switch (nCmd) {
                        //码流切换回调
                        case JVNetConst.SRC_REMOTE_CMD: {
                            int result = object.getInt("result");
                            if (1 == result) {

//                                Toast.makeText(JVMaoYanActivity.this, "码流切换成功了！", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }


                        //对讲回调
                        case JVNetConst.SRC_CHAT: {
                            int result = object.getInt("result");
                            if (nType == JVNetConst.SRC_EX_START_CHAT) {//开启对讲
                                if (result == 1) {//成功
//                                    Toast.makeText(JVMaoYanActivity.this, "主控同意对讲了！", Toast.LENGTH_SHORT).show();
                                    Log.e("CATCAT", "开启对讲--成功：主控同意对讲了！开始发送音频");
                                    PlayUtil.startRecordSendAudio(window);
                                    doubleCalling = true;
                                } else if (result == 0) {//失败
                                    Log.e("CATCAT", "开启对讲--失败");
                                }
                            } else if (nType == JVNetConst.SRC_EX_STOP_CHAT) {//结束对讲
                                if (result == 1) {//成功
                                    Log.e("CATCAT", "关闭对讲--成功");
//                                    Toast.makeText(JVMaoYanActivity.this, "收到对讲结束回调！", Toast.LENGTH_SHORT).show();
                                    if (doubleCalling) {//正在对讲，停止对讲
                                        Log.e("CATCAT", "正在对讲_结束对讲");
                                    } else {//设备已在其他客户端开启对讲
                                        Log.e("CATCAT", "设备已在其他客户端开启对讲");
                                        PlayUtil.closeSound(window);
                                        Toast.makeText(JVMaoYanActivity.this, "设备已在其他客户端开启对讲！", Toast.LENGTH_SHORT).show();
                                    }
                                    doubleCalling = false;
                                } else if (result == 0) {//失败
                                    Log.e("CATCAT", "关闭对讲--失败");
                                }
                            }
                            break;
                        }
                        default: {
                            try {
                                allJson = JSONObject.parseObject(obj.toString());
                                analyzeParams(allJson);
                            } catch (Exception e) {
                                e.printStackTrace();
                                makeText(JVMaoYanActivity.this,
                                        "Json数据解析错误", LENGTH_SHORT).show();
                            }
                            break;
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            }


            default:
                break;
        }
    }


    @Override
    public void onNotify(int what, int arg1, int arg2, Object obj) {
        handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
    }



    //抓拍按钮
    public void capture() {
        String capturePath = LogcatHelper.getPATH_LOGCAT() + "/"+System.currentTimeMillis() + ".jpg";
//        String capturePath = SD_CARD_PATH + System.currentTimeMillis() + ".jpg";
        PlayUtil.capture(window, capturePath);
        Toast.makeText(this, "抓拍成功,保存"+capturePath, Toast.LENGTH_SHORT).show();
    }

    //是否正在对讲
    private boolean doubleCalling = false;
    //双向对讲按钮
    public void call() {

        if (doubleCalling) {
            mJVMYTalkback.setBackgroundResource(R.mipmap.jvmy_talkback);
            stopDoubleCall();
            Toast.makeText(this, "关闭对讲", Toast.LENGTH_SHORT).show();
        } else {
            mJVMYTalkback.setBackgroundResource(R.mipmap.jvmy_talkback3);
            startDoubleCall();
            Toast.makeText(this, "开启对讲", Toast.LENGTH_SHORT).show();
        }
    }
    //开启对讲
    public void startDoubleCall() {
        PlayUtil.startStreamVoiceCall(window);
        PlayUtil.openSound(window);

    }
    //关闭对讲
    public void stopDoubleCall() {
        PlayUtil.stopStreamVoiceCall(window);
//        PlayUtil.stopRecordSendAudio(connectIndex);
        PlayUtil.closeSound(window);
    }

    @Override
    public void onBackPressed() {
        if (doubleCalling) {
            stopDoubleCall();
            Toast.makeText(this, "关闭对讲", Toast.LENGTH_SHORT).show();
        }
        if (recording) {
            PlayUtil.stopRecord();
            Toast.makeText(this, "停止录像", Toast.LENGTH_SHORT).show();
            recording = false;
        }
        if (enableSound) {
            enableSound = false;
            Toast.makeText(this, "关闭声音", Toast.LENGTH_SHORT).show();
        }

        PlayUtil.streamCatDisconnect(window);
        super.onBackPressed();
    }
    private void analyzeParams(JSONObject json) {

        String data = json.getString("data");
        int cmd = json.getInteger("nCmd");
        int packetType = json.getInteger("nPacketType");
        int result = json.getInteger("result");
        String reason = json.getString("reason");

        switch (cmd) {
            case JVNetConst.SRC_PARAM_ALL:
                analyzeAll(JSONObject.parseObject(data));
                break;
            case JVNetConst.SRC_TIME://设备时间相关
                switch (packetType) {
                    case JVNetConst.SRC_EX_GETSYSTIME://获取到设备时间
                        if (result == 0) {
                            Toast.makeText(JVMaoYanActivity.this,
                                    "获取失败，reason：" + reason, Toast.LENGTH_SHORT).show();
                            break;
                        }
                        break;
                    case JVNetConst.SRC_EX_SETSYSTIME://修改设备时间回调
                        break;
                    case JVNetConst.SRC_EX_SETTIME_ZONE://修改设备时区回调
                        if (result == 0) {
                            Toast.makeText(JVMaoYanActivity.this,
                                    "设置失败，reason：" + reason, Toast.LENGTH_SHORT).show();
                            break;
                        }
                        break;
                    case JVNetConst.SRC_EX_SETTIME_FORMAT://修改设备时间显示格式回调
                        if (result == 0) {
                            Toast.makeText(JVMaoYanActivity.this,
                                    "设置失败，reason：" + reason, Toast.LENGTH_SHORT).show();
                            break;
                        }
                        break;
                    case JVNetConst.SRC_EX_SETTIME_SNTP://设置设备时间是否开启网络校对回调
                        if (result == 0) {
                            Toast.makeText(JVMaoYanActivity.this,
                                    "设置失败，reason：" + reason, Toast.LENGTH_SHORT).show();
                            break;
                        }
                        mSntp = (mSntp + result) % 2;
                        break;
                }
                break;
            case JVNetConst.SRC_CHAT:
                break;
            case JVNetConst.SRC_FIRMUP:
                break;
            case JVNetConst.SRC_STORAGE://SD卡相关回调
                switch (packetType) {
                    case JVNetConst.SRC_EX_STORAGE_RESOLUTION://存储分辨率设置回调

                        break;
                    case JVNetConst.SRC_EX_STORAGE_RECORDTIME://录像时间设置回调

                        break;
                    case JVNetConst.SRC_EX_STORAGE_AUTOSWITCH://自动覆盖开关设置回调
                        autoSwitch = (autoSwitch + result) % 2;
                        break;
                }
                break;
            case JVNetConst.SRC_DISPLAY://显示设置回调
                switch (packetType) {
                    case JVNetConst.SRC_EX_DISPLAY_BELLLIGHT://感应门铃按键灯开关设置回调
                        if (result == 0) {
                            makeText(JVMaoYanActivity.this,
                                    "设置失败，reason：" + reason, LENGTH_SHORT).show();
                            break;
                        }
                        bellLight = (bellLight + result) % 2;
                        break;
                    case JVNetConst.SRC_EX_DISPLAY_SUSPENDTIME://休眠时间设置回调
                        break;
                }
                break;
            case JVNetConst.SRC_INTELLIGENCE://智能设置相关
                switch (packetType) {
                    case JVNetConst.SRC_EX_INTELLIGENCE_ALARMTYPE://报警类型
                        if (result == 0) {
                            makeText(JVMaoYanActivity.this,
                                    "设置失败，reason：" + reason, LENGTH_SHORT).show();
                            break;
                        }
                        alarmType = (alarmType + result) % 2;
                        break;
                    case JVNetConst.SRC_EX_INTELLIGENCE_PIR://红外感应
                        if (result == 0) {
                            makeText(JVMaoYanActivity.this,
                                    "设置失败，reason：" + reason, LENGTH_SHORT).show();
                            break;
                        }
                        pirEnable = (pirEnable + result) % 2;
                        break;
                    case JVNetConst.SRC_EX_INTELLIGENCE_GSENSOR://重力感应
                        if (result == 0) {
                            makeText(JVMaoYanActivity.this,
                                    "设置失败，reason：" + reason, LENGTH_SHORT).show();
                            break;
                        }
                        gSensor = (gSensor + result) % 2;
                        break;
                    case JVNetConst.SRC_EX_INTELLIGENCE_MDETECT://移动侦测
                        if (result == 0) {
                            makeText(JVMaoYanActivity.this,
                                    "设置失败，reason：" + reason, LENGTH_SHORT).show();
                            break;
                        }
                        mDetect = (mDetect + result) % 2;
                        break;
                }
                break;
            case JVNetConst.SRC_ABOUTEYE://关于猫眼
                break;
            default:
                if (result == 0) {
                    makeText(JVMaoYanActivity.this,
                            "错误，reason = " + reason, LENGTH_SHORT).show();
                }
        }
    }

    private void analyzeAll(JSONObject data) {
        bellLight = data.getInteger("bBellLight");
        alarmType = data.getInteger("bAlarmType");
        pirEnable = data.getInteger("bPirEnable");
        gSensor = data.getInteger("bGsensorEnable");
        mDetect = data.getInteger("bMDetect");
        totalSize = data.getInteger("nTotalSize");
        autoSwitch = data.getInteger("bAutoSwitch");
        mSntp = data.getInteger("bSntp");

        // 获取当前码流 1：高清 2：标清 3：流畅
        currentStream = data.getInteger("nMobileQuality");
        Log.v("CATCAT", "currentStream="
                + currentStream);





    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (!suspendSwitch) {
            return;
        }
        suspendSwitch = false;
    }


    protected class MyHandler extends Handler {

        private JVMaoYanActivity activity;

        public MyHandler(JVMaoYanActivity activity) {
            this.activity = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            activity.handlerNotify.onHandler(msg.what, msg.arg1, msg.arg2, msg.obj);
            super.handleMessage(msg);
        }

    }


}