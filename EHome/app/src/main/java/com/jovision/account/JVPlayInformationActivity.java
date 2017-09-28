package com.jovision.account;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jovision.AppConsts;
import com.jovision.JVNetConst;
import com.jovision.JniUtil;
import com.jovision.base.IHandlerLikeNotify;
import com.jovision.base.IHandlerNotify;
import com.jovision.bean.Channel;
import com.jovision.bean.Device;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.EHomeApplication;
import com.xingyeda.ehome.bean.InformationBase;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.LogcatHelper;
import com.xingyeda.ehome.view.PercentLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.attr.type;
import static com.xingyeda.ehome.R.id.linkState;

public class JVPlayInformationActivity extends BaseActivity implements IHandlerNotify, IHandlerLikeNotify {

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

    private int channelIndex;
    private Device device;
    private Channel channel;
    //视频播放surface
    @Bind(R.id.jv_info_playsurface)
    SurfaceView playSurface;
    private SurfaceHolder surfaceHolder;
    private String mCameraId;
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
//        ((EHomeApplication) getApplication()).setCurrentNotifyer(this);
        mCameraId = getIntent().getExtras().getString("id");
        mTitle = getIntent().getExtras().getString("title");
        mTime = getIntent().getExtras().getString("time");
        mImage = getIntent().getExtras().getString("image");
        mImageType = getIntent().getExtras().getString("imageType");
        mImageSite = getIntent().getExtras().getString("imageSite");
        if (isExist(mImageSite)) {
            if ("1".equals(mImageType)) {
                setContentView(R.layout.activity_jvinformation);
                ButterKnife.bind(this);
                jvInfoTitle.setText(mCameraId);
                jvInfoTitleText.setText(mTitle);
                jvInfoTimeText.setText(mTime);
                jvInfoImage.setImageBitmap(getLoacalBitmap(mImageSite));
            }else if ("8".equals(mImageType)){
                ((EHomeApplication) getApplication()).setCurrentNotifyer(this);
                initUi();
                initSettings();
            }
        }else{
            ((EHomeApplication) getApplication()).setCurrentNotifyer(this);
            initUi();
            initSettings();
        }
    }


    /**
     * 获取设备的云视通组
     *
     * @param deviceNum
     */
    public static String getGroup(String deviceNum) {

        StringBuffer groupSB = new StringBuffer();
        if (!"".equalsIgnoreCase(deviceNum)) {
            for (int i = 0; i < deviceNum.length(); i++) {
                if (Character.isLetter(deviceNum.charAt(i))) { // 用char包装类中的判断字母的方法判断每一个字符
                    groupSB = groupSB.append(deviceNum.charAt(i));
                }
            }
        }
        return groupSB.toString();
    }

    /**
     * 获取设备的云视通组和号码
     *
     * @param deviceNum
     */
    public static int getYST(String deviceNum) {
        int yst = 0;

        StringBuffer ystSB = new StringBuffer();
        if (!"".equalsIgnoreCase(deviceNum)) {
            for (int i = 0; i < deviceNum.length(); i++) {
                if (Character.isDigit(deviceNum.charAt(i))) {
                    ystSB = ystSB.append(deviceNum.charAt(i));
                }
            }
        }

        if ("".equalsIgnoreCase(ystSB.toString())) {
            yst = 0;
        } else {
            yst = Integer.parseInt(ystSB.toString());
        }
        return yst;
    }

    protected void initSettings() {

//        String devNum = "H28087683";
        String devNum = mCameraId;
        String devUser = "";
        String devPwd = "";


        String group = "";
        int num = -1;

        //有云视通号码
        if (null != devNum && !devNum.equalsIgnoreCase("")) {
            group = getGroup(devNum);
            num = getYST(devNum);
        }


        device = new Device("", 0, group, num, devUser, devPwd, false, 1);
        channel = device.getChannelList().get(1 - 1);
    }
    @OnClick(R.id.jv_info_back)
    public void onViewClicked() {
        onBackPressed();
    }

    protected void initUi() {
        setContentView(R.layout.activity_jvinformation);
        ButterKnife.bind(this);
        jvInfoTitle.setText(mCameraId);
        jvInfoTitleText.setText(mTitle);
        jvInfoTimeText.setText(mTime);
        path = LogcatHelper.getPATH_LOGCAT() + "/"+System.currentTimeMillis() + ".jpg";
        surfaceHolder = playSurface.getHolder();

        surfaceHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (!channel.isConnected()) {
                    connect(channel, holder.getSurface());

                } else if (channel.isConnected()
                        && channel.isPaused()) {
                    boolean result = JniUtil.resumeVideo(channelIndex, holder.getSurface());
                    channel.setPaused(false);
                    if (result) {
                        boolean resumeRes = JniUtil.resumeSurface(channelIndex, holder.getSurface());
                        if (resumeRes) {
                        }
                    }

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

    @Override
    public void onNotify(int what, int arg1, int arg2, Object obj) {
        handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
    }

    @Override
    public void onHandler(int what, int arg1, int arg2, Object obj) {
        switch (what) {
            case AppConsts.CALL_DOWNLOAD: {// 远程回放文件下载

                Log.e("CALL_DOWNLOAD", "what=" + what + ";arg1=" + arg1 + ";arg2=" + arg2+ ";obj=" + obj.toString());

                switch (arg2) {
                    case JVNetConst.JVN_RSP_DOWNLOADDATA: {// 下载进度
                        // 进度{"length":2230204,"size":204800}
                        //length是总大小，size是每次下载的大小，size累加起来等于length
                        Log.e("JVN_RSP_DOWNLOADDATA", "what=" + what + ";arg1=" + arg1 + ";arg2=" + arg2+ ";obj=" + obj.toString());
                        break;
                    }
                    case JVNetConst.JVN_RSP_DOWNLOADOVER: {// 下载完成
                        jvInfoImage.setImageBitmap(getLoacalBitmap(path));
                        InformationBase base = new InformationBase();
                        base.setmZhongWeiImage(path);
                        base.setImageType(1);
                        base.updateAll("mTime = ? ",mTime);
//                        Toast.makeText(JVPlayInformationActivity.this, "下载完成", Toast.LENGTH_LONG).show();
                        break;
                    }
                    case JVNetConst.JVN_RSP_DOWNLOADE: {// 下载失敗
//                        Toast.makeText(JVPlayInformationActivity.this, "下载失敗", Toast.LENGTH_LONG).show();
                        break;
                    }
                }

                break;
            }
            case AppConsts.CALL_CONNECT_CHANGE: {
                switch (arg2) {
                    case 1:
                        channel.setConnected(true);
                        break;

                    case 2:
                        channel.setConnected(false);
                        channel.setPaused(true);
                        break;
                    case 4:
                        channel.setConnected(false);
                        channel.setPaused(true);
                        break;
                    case 6:
                        channel.setConnected(false);
                        channel.setPaused(true);
                        break;
                    case 7:
                        channel.setConnected(false);
                        channel.setPaused(true);
                        break;

                    case 5:
                        channel.setConnected(false);
                        channel.setPaused(true);
                        break;
                    case 8:
                        channel.setConnected(false);
                        channel.setPaused(true);
                        break;
                    default:
                        break;
                }
                break;
            }

            case AppConsts.CALL_FRAME_I_REPORT: {
//                linkState.setText(R.string.i_ok);
                channel.setConnected(true);
                channel.setPaused(false);
                break;
            }

            case AppConsts.CALL_NORMAL_DATA: {
                try {
                    JSONObject jobj;
                    jobj = new JSONObject(obj.toString());
                    int type = jobj.optInt("device_type");
                    if (null != jobj) {
                        channel.getParent().setDeviceType(type);
                        channel.getParent()
                                .setJFH(jobj.optBoolean("is_jfh"));
                        //音频比特率8的dvr不支持对讲
                        if (8 == jobj.getInt("audio_bit")
                                && AppConsts.DEVICE_TYPE_DVR == type) {
//                            connectChannel.setSupportVoice(false);
                        } else {
//                            connectChannel.setSupportVoice(true);
                        }
                        if (type == AppConsts.DEVICE_TYPE_IPC) {
                            // 请求文本聊天，文本聊天按通道发请求
                            JniUtil.requestTextChat(arg1);
                        }
                        if (null != channel && channel.isConnected()) {
                            JniUtil.startRemoteDownload(channel.getIndex(), mImage.getBytes(), path);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            case AppConsts.CALL_TEXT_DATA: {

                switch (arg2) {
                    case JVNetConst.JVN_RSP_TEXTACCEPT: {//同意文本聊天
                        // 获取主控码流信息请求
                        JniUtil.requestStreamData(arg1);
                        break;
                    }
                    case JVNetConst.JVN_RSP_TEXTDATA: {//文本数据
                        if (null != obj) {
                            Log.e("textdata", "what=" + what + ";arg1=" + arg1 + ";arg2=" + arg2 + ";obj=" + obj.toString());
                        } else {
                            Log.e("textdata", "what=" + what + ";arg1=" + arg1 + ";arg2=" + arg2 + ";obj=" + null);
                        }

                        String textData = obj.toString();
                        try {
                            JSONObject dataObj = new JSONObject(textData);
                            int flag = dataObj.getInt(AppConsts.TAG_FLAG);
                            switch (flag) {
                                case 0: {
                                    String streamText = dataObj.getString(AppConsts.TAG_MSG);
                                    HashMap<String, String> streamMap = JniUtil.genMsgMap(streamText);
                                    int uartbaut = Integer.parseInt(streamMap.get(AppConsts.TAG_UARTBAUT));

                                    String streamMsg = "串口波特率:uartbaut=" + uartbaut;
                                    Toast.makeText(JVPlayInformationActivity.this, streamMsg, Toast.LENGTH_LONG).show();
                                    Log.e("uartbaut", streamMsg);

                                    break;
                                }
                                case JVNetConst.JVN_STREAM_INFO: {//码流数据回调
                                    String streamText = dataObj.getString(AppConsts.TAG_MSG);
                                    Log.e("streamText", streamText);
                                    HashMap<String, String> streamMap = JniUtil.genMsgMap(streamText);
                                    int mobileQuality = Integer.parseInt(streamMap.get(AppConsts.TAG_STREAM));
                                    channel.setStreamIndex(mobileQuality);
                                    break;
                                }
                                case JVNetConst.EX_STORAGE_ACCESS: {//刷新设备sd卡状态
                                    Log.e("SDCard----", obj.toString());

                                    String sdCardText = dataObj.getString(AppConsts.TAG_MSG);
                                    HashMap<String, String> sdCardMap = JniUtil.genMsgMap(sdCardText);
                                    int diskExist = Integer.parseInt(sdCardMap.get(AppConsts.TAG_DISK));
                                    if (1 == diskExist) {
                                        Toast.makeText(JVPlayInformationActivity.this, "有SD卡", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(JVPlayInformationActivity.this, "无SD卡", Toast.LENGTH_LONG).show();
                                    }

                                    break;
                                }

                                case JVNetConst.EX_FILE_IN_SDCARD: {//设备卡上的文件回调
                                    //返回的数据只有文件名，文件路径须按照接口要求拼接
                                    Log.e("EX_FILE_IN_SDCARD", obj.toString());
                                    break;
                                }

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case JVNetConst.JVN_CMD_TEXTSTOP: {//不同意文本聊天

                        break;
                    }
                }
                break;
            }

            case AppConsts.CALL_CHAT_DATA: {
                switch (arg2) {
                    // 语音数据
                    case JVNetConst.JVN_RSP_CHATDATA: {
                        break;
                    }
                    // 同意语音请求
                    case JVNetConst.JVN_RSP_CHATACCEPT: {
//                        Toast.makeText(JVPlayInformationActivity.this, "主控同意对讲了！", Toast.LENGTH_SHORT).show();
                        if (channel.isSingleVoice()) {//单向对讲
//                            sendingBtn.setVisibility(View.VISIBLE);
//                            Log.e(TAG, "JVN_RSP_CHATACCEPT-主控同意对讲了！longClicking=" + longClicking);
//                            if (longClicking) {
//                                Toast.makeText(JVPlayInformationActivity.this, "您现在可以说话了！", Toast.LENGTH_SHORT).show();
//                            } else {//手指已经离开
//                                sendingBtn.setText(R.string.press_to_talk);
//                                停止本地录音
//                                JniUtil.stopRecordSendAudio(channelIndex);
//                            }
//                        } else {
                            JniUtil.startRecordSendAudio(channelIndex);
                            JniUtil.resumeAudio(channelIndex);
//                        }
                            channel.setVoiceCalling(true);//设置成正在对讲状态
                            break;
                        }
                    }

                    //暂停语音聊天
                    case JVNetConst.JVN_CMD_CHATSTOP: {
//                        Toast.makeText(JVPlayInformationActivity.this, "收到chatstop", Toast.LENGTH_SHORT).show();
//                        if (channel.isVoiceCalling()) {//正在对讲，停止对讲
//                        } else {//设备已在其他客户端开启对讲
//                            Toast.makeText(JVPlayInformationActivity.this, "设备已在其他客户端开启对讲", Toast.LENGTH_SHORT).show();
//                        }
                        channel.setVoiceCalling(false);
                        break;
                    }
                }
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    /**
     * 视频连接
     *
     * @param channel
     * @param surface
     * @return
     */
    private void connect(Channel channel, Surface surface) {
        if (null != channel) {
            JniUtil.connectDevice(channel, surface, "", false);
        }
    }


    /**
     * 断开所有视频
     */

    public void disconnect() {
        try {
            JniUtil.disconnectChannel(channelIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        disconnect();
        super.onBackPressed();
    }


    @Override
    protected void onPause() {
        JniUtil.pauseSurface(channelIndex);
        super.onPause();
    }
    protected class MyHandler extends Handler {

        private JVPlayInformationActivity activity;

        public MyHandler(JVPlayInformationActivity activity) {
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
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}

