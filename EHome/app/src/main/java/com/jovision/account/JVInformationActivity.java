package com.jovision.account;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jovision.FileUtil;
import com.jovision.JVNetConst;
import com.jovision.PlayUtil;
import com.jovision.base.IHandlerLikeNotify;
import com.jovision.base.IHandlerNotify;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.EHomeApplication;
import com.xingyeda.ehome.bean.InformationBase;
import com.xingyeda.ehome.util.LogcatHelper;
import com.xingyeda.ehome.view.PercentLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.attr.path;
import static android.R.attr.type;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static com.jovision.JVNetConst.CALL_CATEYE_ADDRESS;
import static com.jovision.JVNetConst.CALL_CATEYE_CONNECTED;
import static com.jovision.JVNetConst.CALL_CATEYE_DISCONNECTED;
import static com.jovision.JVNetConst.CALL_CATEYE_ONLINE;
import static com.jovision.JVNetConst.CALL_NEW_PICTURE;
import static com.jovision.PlayUtil.startStreamCatDownload;
import static com.xingyeda.ehome.R.id.linkState;

public class JVInformationActivity extends BaseActivity implements IHandlerNotify, IHandlerLikeNotify, RadioGroup.OnCheckedChangeListener {

    public static final String SD_CARD_PATH = Environment
            .getExternalStorageDirectory().getPath() + File.separator;
    public static final String CAT_STREAM_PATH = SD_CARD_PATH + "0strMedia0"
            + File.separator + "catstream" + File.separator;

    protected MyHandler handler = new MyHandler(this);
    private IHandlerNotify handlerNotify = this;

    @Bind(R.id.jv_info_title)
    TextView jvInfoTitle;
    @Bind(R.id.jv_info_title_text)
    TextView jvInfoTitleText;
    @Bind(R.id.jv_info_time_text)
    TextView jvInfoTimeText;
    @Bind(R.id.jv_info_image)
    ImageView jvInfoImage;
    @Bind(R.id.jv_info_playsurface)
    SurfaceView jvInfoPlaysurface;

    private boolean isConnected = false;//是否已连接
    int window = 0;
    private boolean suspendSwitch = false;
    private com.alibaba.fastjson.JSONObject allJson;
    private int bellLight;//感应门铃按键灯开关：0关，1开
    private int alarmType;//报警类型：0图片，1视频
    private int pirEnable;//红外感应开关：0关，1开
    private int gSensor;//重力感应：0关，1开
    private int mDetect;//移动侦测：0关，1开
    private int totalSize,  autoSwitch;
    //    网络校时开关，时间显示格式，时区
    private int mSntp; //0关闭，1开启
    //当前码流 1：超清  2：高清  3：流畅
    private int currentStream = 0;

    private String ystNum;
    private SurfaceHolder surfaceHolder;
    private String mTitle;
    private String mTime;
    private String mImage;
    private String mImageType;
    private String path;
    private String mImageSite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        ystNum = getIntent().getExtras().getString("id");
        mTitle = getIntent().getExtras().getString("title");
        mTime = getIntent().getExtras().getString("time");
        mImage = getIntent().getExtras().getString("image");
        mImageType = getIntent().getExtras().getString("imageType");
        mImageSite = getIntent().getExtras().getString("imageSite");

        if (isExist(mImageSite)) {
        if ("1".equals(mImageType)) {
            setContentView(R.layout.activity_jvinformation);
            ButterKnife.bind(this);
            jvInfoTitle.setText(ystNum);
            jvInfoTitleText.setText(mTitle);
            jvInfoTimeText.setText(mTime);
            jvInfoImage.setImageBitmap(getLoacalBitmap(mImageSite));
        }else if ("8".equals(mImageType)){
        ((EHomeApplication) getApplication()).setCurrentNotifyer(this);
        initUi();
        }
        }else{
            ((EHomeApplication) getApplication()).setCurrentNotifyer(this);
            initUi();
        }
    }
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    @OnClick(R.id.jv_info_back)
    public void onViewClicked() {
        onBackPressed();
    }
    private void init() {
        setContentView(R.layout.activity_jvinformation);
        ButterKnife.bind(this);
        jvInfoTitle.setText(ystNum);
        jvInfoTitleText.setText(mTitle);
        jvInfoTimeText.setText(mTime);
        path = LogcatHelper.getPATH_LOGCAT() + "/"+System.currentTimeMillis() + ".jpg";

        FileUtil.createDirectory(new File(CAT_STREAM_PATH));
        surfaceHolder = jvInfoPlaysurface.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (isConnected) {
                    PlayUtil.resumeSurface(window, surfaceHolder.getSurface());
                } else {
//                    linkState.setText("连接中...");
                    int result = PlayUtil.streamCatConnect(window, surfaceHolder.getSurface(), ystNum);

                    if (0 == result) {
//                        linkState.setText("连接成功");
//                        int  a = PlayUtil.startStreamCatDownload(window,"/mnt/20150101/A01080000.jpg");
//			            LogUtils.d(a+"");
                    } else if (-1 == result) {
//                        linkState.setText("连接失败");
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
    }

    protected void initUi() {
        FileUtil.createDirectory(new File(CAT_STREAM_PATH));
        int ret1 = PlayUtil.searchStreamCatOnLineServer(CAT_STREAM_PATH,
                new String[]{ystNum});
        if (ret1 == -1) {//获取地址失败
            Toast.makeText(JVInformationActivity.this, "获取地址失败", Toast.LENGTH_SHORT).show();
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
    @Override
    public void onBackPressed() {
//        if (doubleCalling) {
//            stopDoubleCall();
//            Toast.makeText(this, "关闭对讲", Toast.LENGTH_SHORT).show();
//        }
//        if (recording) {
//            PlayUtil.stopRecord();
//            Toast.makeText(this, "停止录像", Toast.LENGTH_SHORT).show();
//            recording = false;
//        }
//        if (enableSound) {
//            enableSound = false;
//            Toast.makeText(this, "关闭声音", Toast.LENGTH_SHORT).show();
//        }

        PlayUtil.streamCatDisconnect(window);
        super.onBackPressed();
    }

    @Override
    public void onHandler(int what, int arg1, int arg2, Object obj) {
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
                        Toast.makeText(JVInformationActivity.this,
                                "与服务器通信失败!", Toast.LENGTH_LONG).show();
                        Looper.loop();
                        return;
                    case 0:
                        Looper.prepare();
                        Toast.makeText(JVInformationActivity.this,
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
//                    linkState.setText("连接成功，缓冲中...");
//                    makeText(JVMaoYanActivity.this, ystNum + "--连接成功", LENGTH_SHORT).show();
                } else if (2 == arg2) {
//                    linkState.setText("连接失败");
//                    makeText(JVMaoYanActivity.this, ystNum + "--连接失败", LENGTH_SHORT).show();
                }
                break;
            }
            case CALL_CATEYE_DISCONNECTED: {//异常断开
                isConnected = false;
//                linkState.setText("连接异常断开");
//                makeText(JVMaoYanActivity.this, ystNum + "异常断开", LENGTH_SHORT).show();
                PlayUtil.streamCatDisconnect(window);
                break;
            }
            case CALL_NEW_PICTURE: {//视频连接出图
                isConnected = true;
//                linkState.setVisibility(View.GONE);
//                makeText(JVMaoYanActivity.this, ystNum + "--出图像", LENGTH_SHORT).show();
//                String path = LogcatHelper.getPATH_LOGCAT() + "/"+System.currentTimeMillis() + ".jpg";
                PlayUtil.setDownloadFilePath(path);
                int returns =PlayUtil.startStreamCatDownload(window, mImage);
//                if (returns==0) {
//                    jvInfoImage.setImageBitmap(getLoacalBitmap(path));
//                    InformationBase base = new InformationBase();
//                    base.setmImage(path);
//                    base.setImageType(1);
//                    base.updateAll("mTime = ? ",mTime);
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
//                            int result = object.getInt("result");
//                            if (nType == JVNetConst.SRC_EX_START_CHAT) {//开启对讲
//                                if (result == 1) {//成功
////                                    Toast.makeText(JVMaoYanActivity.this, "主控同意对讲了！", Toast.LENGTH_SHORT).show();
//                                    Log.e("CATCAT", "开启对讲--成功：主控同意对讲了！开始发送音频");
//                                    PlayUtil.startRecordSendAudio(window);
//                                    doubleCalling = true;
//                                } else if (result == 0) {//失败
//                                    Log.e("CATCAT", "开启对讲--失败");
//                                }
//                            } else if (nType == JVNetConst.SRC_EX_STOP_CHAT) {//结束对讲
//                                if (result == 1) {//成功
//                                    Log.e("CATCAT", "关闭对讲--成功");
////                                    Toast.makeText(JVMaoYanActivity.this, "收到对讲结束回调！", Toast.LENGTH_SHORT).show();
//                                    if (doubleCalling) {//正在对讲，停止对讲
//                                        Log.e("CATCAT", "正在对讲_结束对讲");
//                                    } else {//设备已在其他客户端开启对讲
//                                        Log.e("CATCAT", "设备已在其他客户端开启对讲");
//                                        PlayUtil.closeSound(window);
//                                        Toast.makeText(JVMaoYanActivity.this, "设备已在其他客户端开启对讲！", Toast.LENGTH_SHORT).show();
//                                    }
//                                    doubleCalling = false;
//                                } else if (result == 0) {//失败
//                                    Log.e("CATCAT", "关闭对讲--失败");
//                                }
//                            }
                            break;
                        }
                        default: {
                            try {
                                allJson = com.alibaba.fastjson.JSONObject.parseObject(obj.toString());
                                analyzeParams(allJson);
                            } catch (Exception e) {
                                e.printStackTrace();
                                makeText(JVInformationActivity.this,
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
            // 下载完成回调
            case JVNetConst.CALL_CATEYE_DOWNLOADFINISHED: {
                Log.e("CATCAT", "--CALL_CATEYE_DOWNLOADFINISHED:arg1=" + arg1);
                // 返回结果处理
                switch (arg1) {
                    // 文件下载完毕
                    case JVNetConst.DOWN_SUCCESS: {
//                        Toast.makeText(JVInformationActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
                    jvInfoImage.setImageBitmap(getLoacalBitmap(path));
                    InformationBase base = new InformationBase();
                    base.setmZhongWeiImage(path);
                    base.setImageType(1);
                    base.updateAll("mTime = ? ",mTime);
                        break;
                    }
                    default:
                        //TODO: 16/11/17    下载失败
//                        Toast.makeText(JVInformationActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
            }


            default:
                break;
        }
    }

    private void analyzeParams(com.alibaba.fastjson.JSONObject json) {

        String data = json.getString("data");
        int cmd = json.getInteger("nCmd");
        int packetType = json.getInteger("nPacketType");
        int result = json.getInteger("result");
        String reason = json.getString("reason");

        switch (cmd) {
            case JVNetConst.SRC_PARAM_ALL:
                analyzeAll(com.alibaba.fastjson.JSONObject.parseObject(data));
                break;
            case JVNetConst.SRC_TIME://设备时间相关
                switch (packetType) {
                    case JVNetConst.SRC_EX_GETSYSTIME://获取到设备时间
                        if (result == 0) {
                            Toast.makeText(JVInformationActivity.this,
                                    "获取失败，reason：" + reason, Toast.LENGTH_SHORT).show();
                            break;
                        }
                        break;
                    case JVNetConst.SRC_EX_SETSYSTIME://修改设备时间回调
                        break;
                    case JVNetConst.SRC_EX_SETTIME_ZONE://修改设备时区回调
                        if (result == 0) {
                            Toast.makeText(JVInformationActivity.this,
                                    "设置失败，reason：" + reason, Toast.LENGTH_SHORT).show();
                            break;
                        }
                        break;
                    case JVNetConst.SRC_EX_SETTIME_FORMAT://修改设备时间显示格式回调
                        if (result == 0) {
                            Toast.makeText(JVInformationActivity.this,
                                    "设置失败，reason：" + reason, Toast.LENGTH_SHORT).show();
                            break;
                        }
                        break;
                    case JVNetConst.SRC_EX_SETTIME_SNTP://设置设备时间是否开启网络校对回调
                        if (result == 0) {
                            Toast.makeText(JVInformationActivity.this,
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
                            makeText(JVInformationActivity.this,
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
                            makeText(JVInformationActivity.this,
                                    "设置失败，reason：" + reason, LENGTH_SHORT).show();
                            break;
                        }
                        alarmType = (alarmType + result) % 2;
                        break;
                    case JVNetConst.SRC_EX_INTELLIGENCE_PIR://红外感应
                        if (result == 0) {
                            makeText(JVInformationActivity.this,
                                    "设置失败，reason：" + reason, LENGTH_SHORT).show();
                            break;
                        }
                        pirEnable = (pirEnable + result) % 2;
                        break;
                    case JVNetConst.SRC_EX_INTELLIGENCE_GSENSOR://重力感应
                        if (result == 0) {
                            makeText(JVInformationActivity.this,
                                    "设置失败，reason：" + reason, LENGTH_SHORT).show();
                            break;
                        }
                        gSensor = (gSensor + result) % 2;
                        break;
                    case JVNetConst.SRC_EX_INTELLIGENCE_MDETECT://移动侦测
                        if (result == 0) {
                            makeText(JVInformationActivity.this,
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
                    makeText(JVInformationActivity.this,
                            "错误，reason = " + reason, LENGTH_SHORT).show();
                }
        }
    }

    private void analyzeAll(com.alibaba.fastjson.JSONObject data) {
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
    public void onNotify(int what, int arg1, int arg2, Object obj) {
        handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (!suspendSwitch) {
            return;
        }
        suspendSwitch = false;
    }


    protected class MyHandler extends Handler {

        private JVInformationActivity activity;

        public MyHandler(JVInformationActivity activity) {
            this.activity = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            activity.handlerNotify.onHandler(msg.what, msg.arg1, msg.arg2, msg.obj);
            super.handleMessage(msg);
        }

    }
    private boolean isExist(String path){
        if (path==null){
            return  false;
        }else{
        File file = new File(path);
        if (file.exists()) {
            return true;
        }
        }
        return false;
    }
}
