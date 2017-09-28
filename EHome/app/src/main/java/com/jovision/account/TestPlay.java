package com.jovision.account;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jovision.AppConsts;
import com.jovision.JVNetConst;
import com.jovision.Jni;
import com.jovision.JniUtil;
import com.jovision.base.IHandlerLikeNotify;
import com.jovision.base.IHandlerNotify;
import com.jovision.bean.Channel;
import com.jovision.bean.Device;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.EHomeApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.xingyeda.ehome.R.string.sound;

/**
 * Created by LDL on 2017/5/27.
 */

public class TestPlay extends BaseActivity implements IHandlerNotify, IHandlerLikeNotify {

    protected MyHandler handler = new MyHandler(this);
    private IHandlerNotify handlerNotify = this;

    /**
     * 云台按钮事件
     */
    ImageView.OnClickListener imageOnClickListener = new ImageView.OnClickListener() {
        @Override
        public void onClick(View arg0) {

        }
    };
    //连接的设备和通道
    private boolean isApConnect;
    private int channelIndex;
    private Device device;
    private Channel channel;
    //视频播放surface
    private SurfaceView playSurface;
    private TextView linkState;
    private SurfaceHolder surfaceHolder;
    private Button sendingBtn;//单向对讲，长按发送语音数据
    private ImageView autoimage, zoomIn, zoomout, scaleSmallImage,
            scaleAddImage, upArrow, downArrow, leftArrow, rightArrow;
    private LinearLayout ytLayout;
    private boolean longClicking = false;//对讲长按说话状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ((EHomeApplication) getApplication()).setCurrentNotifyer(this);
        initUi();
        initSettings();
    }

    /**
     * 单向对讲用功能
     */
    View.OnTouchListener callOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View arg0, MotionEvent arg1) {
            if (!channel.isSingleVoice()) {
                return false;
            }
            if (arg1.getAction() == MotionEvent.ACTION_UP
                    || arg1.getAction() == MotionEvent.ACTION_HOVER_MOVE) {
                longClicking = false;
                JniUtil.startAudioMonitor(channelIndex);
                sendingBtn.setText(R.string.press_to_talk);
                //停止本地录音
                JniUtil.stopRecordSendAudio(channelIndex);
            }

            return false;
        }

    };
    /**
     * 单向对讲用功能
     */
    View.OnLongClickListener callOnLongClickListener = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(View arg0) {
            //长按且非移动  单向对讲  按住说话
            if (channel.isSingleVoice()) {
                longClicking = true;
                JniUtil.stopAudioMonitor(channelIndex);
                sendingBtn.setText(R.string.lose_to_stop);
                JniUtil.startRecordSendAudio(channelIndex);
            }
            return true;
        }

    };

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


        isApConnect = false;

        String devNum = "H27319279";
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

    protected void initUi() {
        setContentView(R.layout.activity_play);
        ButterKnife.bind(this);

        playSurface = (SurfaceView) findViewById(R.id.playsurface);
        linkState = (TextView) findViewById(R.id.linkstate);
        surfaceHolder = playSurface.getHolder();

        surfaceHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                linkState.setVisibility(View.VISIBLE);
                linkState.setText(R.string.connecting);
                if (!channel.isConnected()) {
                    connect(channel, holder.getSurface());

                } else if (channel.isConnected()
                        && channel.isPaused()) {
                    boolean result = JniUtil.resumeVideo(channelIndex, holder.getSurface());
                    channel.setPaused(false);

                    if (result) {
                        boolean resumeRes = JniUtil.resumeSurface(channelIndex, holder.getSurface());
                        if (resumeRes) {
                            linkState.setVisibility(View.GONE);
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

//        sendingBtn = (Button) findViewById(R.id.sending);
//        sendingBtn.setOnTouchListener(callOnTouchListener);
//        sendingBtn.setOnLongClickListener(callOnLongClickListener);
//        ytLayout = (LinearLayout) findViewById(R.id.ytlayout);
//        autoimage = (ImageView) findViewById(R.id.autoimage);
//        zoomIn = (ImageView) findViewById(R.id.zoomin);
//        zoomout = (ImageView) findViewById(R.id.zoomout);
//        scaleSmallImage = (ImageView) findViewById(R.id.scaleSmallImage);
//        scaleAddImage = (ImageView) findViewById(R.id.scaleAddImage);
//        upArrow = (ImageView) findViewById(R.id.upArrow);
//        downArrow = (ImageView) findViewById(R.id.downArrow);
//        leftArrow = (ImageView) findViewById(R.id.leftArrow);
//        rightArrow = (ImageView) findViewById(R.id.rightArrow);
//
//        autoimage.setOnClickListener(imageOnClickListener);
//        zoomIn.setOnClickListener(imageOnClickListener);
//        zoomout.setOnClickListener(imageOnClickListener);
//        scaleSmallImage.setOnClickListener(imageOnClickListener);
//        scaleAddImage.setOnClickListener(imageOnClickListener);
//        upArrow.setOnClickListener(imageOnClickListener);
//        downArrow.setOnClickListener(imageOnClickListener);
//        leftArrow.setOnClickListener(imageOnClickListener);
//        rightArrow.setOnClickListener(imageOnClickListener);
//
//        autoimage.setOnTouchListener(new LongClickListener());
//        zoomIn.setOnTouchListener(new LongClickListener());
//        zoomout.setOnTouchListener(new LongClickListener());
//        scaleSmallImage.setOnTouchListener(new LongClickListener());
//        scaleAddImage.setOnTouchListener(new LongClickListener());
//        upArrow.setOnTouchListener(new LongClickListener());
//        downArrow.setOnTouchListener(new LongClickListener());
//        leftArrow.setOnTouchListener(new LongClickListener());
//        rightArrow.setOnTouchListener(new LongClickListener());
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
            JniUtil.connectDevice(channel, surface, "", isApConnect);
        }
    }

    @Override
    public void onNotify(int what, int arg1, int arg2, Object obj) {

        if (null != obj) {
            Log.e("PlayOnNotify", "what=" + what + ";arg1=" + arg1 + ";arg2=" + arg2 + ";obj=" + obj.toString());
        } else {
            Log.e("OnNotify", "what=" + what + ";arg1=" + arg1 + ";arg2=" + arg2);
        }

        handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
    }

    @Override
    public void onHandler(int what, int arg1, int arg2, Object obj) {

        switch (what) {
            case AppConsts.CALL_DOWNLOAD: {// 远程回放文件下载

                Log.e("CALL_DOWNLOAD", "what=" + what + ";arg1=" + arg1 + ";arg2=" + arg2 + ";obj=" + obj.toString());

                switch (arg2) {
                    case JVNetConst.JVN_RSP_DOWNLOADDATA: {// 下载进度
                        // 进度{"length":2230204,"size":204800}
                        //length是总大小，size是每次下载的大小，size累加起来等于length
                        Log.e("JVN_RSP_DOWNLOADDATA", "what=" + what + ";arg1=" + arg1 + ";arg2=" + arg2 + ";obj=" + obj.toString());
                        break;
                    }
                    case JVNetConst.JVN_RSP_DOWNLOADOVER: {// 下载完成
//                        Toast.makeText(JVPlayActivity.this, "下载完成", Toast.LENGTH_LONG).show();
                        break;
                    }
                    case JVNetConst.JVN_RSP_DOWNLOADE: {// 下载失敗
//                        Toast.makeText(JVPlayActivity.this, "下载失敗", Toast.LENGTH_LONG).show();
                        break;
                    }
                }

                break;
            }


            case AppConsts.CALL_CONNECT_CHANGE: {
                switch (arg2) {
                    case 1:
                        linkState.setText(R.string.connect_ok);
                        channel.setConnected(true);
                        break;

                    case 2:
                        linkState.setText(R.string.error2);
                        channel.setConnected(false);
                        channel.setPaused(true);
                        break;
                    case 4:
                        linkState.setText(R.string.error4);
                        channel.setConnected(false);
                        channel.setPaused(true);
                        break;
                    case 6:
                        linkState.setText(R.string.error6);
                        channel.setConnected(false);
                        channel.setPaused(true);
                        break;
                    case 7:
                        linkState.setText(R.string.error7);
                        channel.setConnected(false);
                        channel.setPaused(true);
                        break;

                    case 5:
                        linkState.setText(R.string.error5);
                        channel.setConnected(false);
                        channel.setPaused(true);
                        break;
                    case 8:
                        linkState.setText(R.string.error8);
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
                linkState.setVisibility(View.GONE);
                channel.setConnected(true);
                channel.setPaused(false);
                break;
            }

            case AppConsts.CALL_NORMAL_DATA: {
                linkState.setText(R.string.o_ok);
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
//                                    what=165;arg1=0;arg2=81;obj={"extend_arg1":1200,"extend_arg2":14,"extend_arg3":0,"extend_type":43,"flag":0,
//                                            "msg":"uartbaut=1200;","packet_count":18,"packet_id":0,"packet_length":0,"packet_type":6}
                                    String streamText = dataObj.getString(AppConsts.TAG_MSG);
                                    HashMap<String, String> streamMap = JniUtil.genMsgMap(streamText);
                                    int uartbaut = Integer.parseInt(streamMap.get(AppConsts.TAG_UARTBAUT));

                                    String streamMsg = "串口波特率:uartbaut=" + uartbaut;
//                                    Toast.makeText(JVPlayActivity.this, streamMsg, Toast.LENGTH_LONG).show();
                                    Log.e("uartbaut", streamMsg);

                                    break;
                                }
                                case JVNetConst.JVN_STREAM_INFO: {//码流数据回调
                                    String streamText = dataObj.getString(AppConsts.TAG_MSG);
                                    Log.e("streamText", streamText);
                                    HashMap<String, String> streamMap = JniUtil.genMsgMap(streamText);
                                    int mobileQuality = Integer.parseInt(streamMap.get(AppConsts.TAG_STREAM));

                                    bSntp = Integer.parseInt(streamMap.get(AppConsts.TAG_BSNTP));
                                    recFileLength = Integer.parseInt(streamMap.get(AppConsts.TAG_RECFILELENGTH));
                                    bRecEnable = Integer.parseInt(streamMap.get(AppConsts.TAG_BRECENABLE));


                                    pushSwitch = Integer.parseInt(streamMap.get(AppConsts.TAG_SET_DEV_SAFE_STATE));
                                    alarmSwitch = Integer.parseInt(streamMap.get(AppConsts.TAG_SET_MDENABLE_STATE));
                                    sensity = Integer.parseInt(streamMap.get(AppConsts.TAG_NMDSENSITIVITY));


                                    channel.setStreamIndex(mobileQuality);

                                    String streamMsg = "mobileQuality=" + mobileQuality
                                            + ";bSntp=" + bSntp
                                            + ";recFileLength=" + recFileLength
                                            + ";bRecEnable=" + bRecEnable
                                            + ";pushSwitch=" + pushSwitch
                                            + ";alarmSwitch=" + alarmSwitch
                                            + ";sensity=" + sensity;
//                                    Toast.makeText(JVPlayActivity.this, streamMsg, Toast.LENGTH_LONG).show();
                                    Log.e("streamMsg", streamMsg);
                                    break;
                                }
                                case JVNetConst.EX_STORAGE_ACCESS: {//刷新设备sd卡状态
                                    Log.e("SDCard----", obj.toString());
//                                    {"extend_arg1":64,"extend_arg2":0,"extend_arg3":0,"extend_type":1,"flag":7,"msg":"nStorage=1;[STORAGE1];nTotalSize=15135;nUsedSize=7573;nStatus=4;","packet_count":3,"packet_id":0,"packet_length":0,"packet_type":6}

                                    String sdCardText = dataObj.getString(AppConsts.TAG_MSG);
                                    HashMap<String, String> sdCardMap = JniUtil.genMsgMap(sdCardText);
                                    int diskExist = Integer.parseInt(sdCardMap.get(AppConsts.TAG_DISK));
//                                    if (1 == diskExist) {
//                                        Toast.makeText(JVPlayActivity.this, "有SD卡", Toast.LENGTH_LONG).show();
//                                    } else {
//                                        Toast.makeText(JVPlayActivity.this, "无SD卡", Toast.LENGTH_LONG).show();
//                                    }

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
//                        Toast.makeText(JVPlayActivity.this, "主控同意对讲了！", Toast.LENGTH_SHORT).show();
                        if (channel.isSingleVoice()) {//单向对讲
                            sendingBtn.setVisibility(View.VISIBLE);
//                            Log.e(TAG, "JVN_RSP_CHATACCEPT-主控同意对讲了！longClicking=" + longClicking);
                            if (longClicking) {
//                                Toast.makeText(JVPlayActivity.this, "您现在可以说话了！", Toast.LENGTH_SHORT).show();
                            } else {//手指已经离开
                                sendingBtn.setText(R.string.press_to_talk);
                                //停止本地录音
                                JniUtil.stopRecordSendAudio(channelIndex);
                            }
                        } else {
                            JniUtil.startRecordSendAudio(channelIndex);
                            JniUtil.resumeAudio(channelIndex);
                        }
                        channel.setVoiceCalling(true);//设置成正在对讲状态
                        break;
                    }

                    // 暂停语音聊天
                    case JVNetConst.JVN_CMD_CHATSTOP: {
//                        Toast.makeText(JVPlayActivity.this, "收到chatstop", Toast.LENGTH_SHORT).show();
                        if (channel.isVoiceCalling()) {//正在对讲，停止对讲
                        } else {//设备已在其他客户端开启对讲
//                            Toast.makeText(JVPlayActivity.this, "设备已在其他客户端开启对讲", Toast.LENGTH_SHORT).show();
                        }
                        channel.setVoiceCalling(false);
                        break;
                    }
                }
                break;
            }
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

    /******************************
     * 按钮点击事件
     ***********************************/


    //声音开关
    public void sound() {
        if (JniUtil.isPlayAudio(channelIndex)) {
            JniUtil.stopAudioMonitor(channelIndex);
            Toast.makeText(TestPlay.this, "关闭声音", Toast.LENGTH_SHORT).show();
        } else {
            JniUtil.startAudioMonitor(channelIndex);
            Toast.makeText(TestPlay.this, "打开声音", Toast.LENGTH_SHORT).show();
        }
    }

    //单向对讲
    public void singleCall() {
        channel.setSingleVoice(true);
        if (channel.isVoiceCalling()) {
            JniUtil.stopVoiceCall(channelIndex);
            JniUtil.stopAudioMonitor(channelIndex);
            sendingBtn.setVisibility(View.GONE);
        } else {
            JniUtil.startVoiceCall(channelIndex, false);
            JniUtil.startAudioMonitor(channelIndex);
        }

    }

    //双向对讲
    public void doubleCall() {
        channel.setSingleVoice(false);
        if (channel.isVoiceCalling()) {
            JniUtil.stopVoiceCall(channelIndex);
            JniUtil.stopAudioMonitor(channelIndex);
        } else {
            JniUtil.startVoiceCall(channelIndex, true);
            JniUtil.startAudioMonitor(channelIndex);
        }
    }

    boolean isTwoRecord = true;

//    //录像
//    public void record(View view) {
//        if (JniUtil.checkRecord(channelIndex)) {
////            ((Button) findViewById(R.id.record)).setText("停止录像");
//            JniUtil.stopRecord(channelIndex);
//            Toast.makeText(TestPlay.this, "停止录像", Toast.LENGTH_SHORT).show();
//        } else {
//            JniUtil.startRecord(channelIndex, isTwoRecord);
//            Toast.makeText(TestPlay.this, "开始录像", Toast.LENGTH_SHORT).show();
////            ((Button) findViewById(R.id.record)).setText("开始录像");
//        }
//    }

//    //修改码流
//    public void changeStream(View view) {
//        int streamIndex = channel.getStreamIndex();
//        int changeIndex = --streamIndex < 1 ? 3 : streamIndex;
//
//        switch (changeIndex) {
//            case 1: {
//                Toast.makeText(TestPlay.this, "切换到高清，changeIndex=" + changeIndex, Toast.LENGTH_SHORT).show();
//                break;
//            }
//            case 2: {
//                Toast.makeText(TestPlay.this, "切换到标清，changeIndex=" + changeIndex, Toast.LENGTH_SHORT).show();
//                break;
//            }
//            case 3: {
//                Toast.makeText(TestPlay.this, "切换到流畅，=" + changeIndex, Toast.LENGTH_SHORT).show();
//                break;
//            }
//        }
//        JniUtil.changeStream(channelIndex, changeIndex);
//    }

//    //远程回放
//    public void remotePlay(View view) {
//        if (null != channel && channel.isConnected()) {
//            Intent remoteIntent = new Intent();
////            remoteIntent.setClass(TestPlay.this, JVRemoteListActivity.class);
//            remoteIntent.putExtra("IndexOfChannel", channel.getIndex());
//            remoteIntent.putExtra("ChannelOfChannel",
//                    channel.getChannel());
//            remoteIntent.putExtra("DeviceType", channel.getParent()
//                    .getDeviceType());
//            remoteIntent.putExtra("isJFH", channel.getParent().isJFH());
////            JVPlayActivity.this.startActivity(remoteIntent);
//        }
//    }


//    //查询卡上的所有录像
//    public void checkVideo(View view) {
//        /**
//         * type = 0 查询卡上录像
//         * start 从第几个文件index（从0开始）
//         * count 查询几个问件
//         *
//         * 回调在onHandler里面，what = AppConsts.CALL_TEXT_DATA， arg2 = JVNetConst.JVN_RSP_TEXTDATA， flag = JVNetConst.EX_FILE_IN_SDCARD
//         *
//         * 回调数据例如：what=165;arg1=0;arg2=81;obj={"extend_arg1":162,"extend_arg2":0,"extend_arg3":0,"extend_type":1,"flag":7,"msg":"type=020170317-113805-423017.mp4;20170317-113819-015811.mp4;20170318-010740-897336.mp4;20170318-011042-429208.mp4;20170318-011346-380268.mp4;20170318-011650-60548","packet_count":25,"packet_id":0,"packet_length":0,"packet_type":6}
//         */
//        Toast.makeText(JVPlayActivity.this, "查询卡上的所有录像", Toast.LENGTH_LONG).show();
//        Jni.sendString(channel.getIndex(), JVNetConst.JVN_RSP_TEXTDATA, true,
//                JVNetConst.RC_EX_CHECK_FILE, JVNetConst.EX_DQP_CHECK_FILE, "type=0;start=4;count=8;");
//    }

//    //查询卡上的所有照片
//    public void checkImg(View view) {
//        /**
//         * type = 1 查询卡上照片
//         * start 从第几个文件index（从0开始）
//         * count 查询几个问件
//         *
//         * 回调在onHandler里面，what = AppConsts.CALL_TEXT_DATA， arg2 = JVNetConst.JVN_RSP_TEXTDATA， flag = JVNetConst.EX_FILE_IN_SDCARD
//         *
//         * 回调数据例如：what=165;arg1=0;arg2=81;obj={"extend_arg1":1080,"extend_arg2":0,"extend_arg3":0,"extend_type":1,"flag":7,"msg":"20150101-000009-894500.jpg;20150101-000010-182737.jpg;20150101-000010-418024.jpg;20150101-000013-303390.jpg;20150101-000013-480278.jpg;20150101-000013-666948.jpg;20150101-000050-173391.jpg;20150101-000050-336718.jpg;20150101-000050-683437.jpg;20150101-000104-263584.jpg;20150101-000104-442894.jpg;20150101-000104-615323.jpg;20150101-000150-453097.jpg;20150101-000150-650873.jpg;20150101-000151-033151.jpg;20150101-000242-603753.jpg;20150101-000242-823258.jpg;20150101-000242-985407.jpg;20150101-000312-023615.jpg;20150101-000312-226420.jpg;20150101-000312-396053.jpg;20150101-000345-026584.jpg;20150101-000345-225702.jpg;20150101-000345-469053.jpg;20170317-110835-443050.jpg;20170317-110836-162922.jpg;20170317-110836-343003.jpg;20170317-110905-286821.jpg;20170317-110905-494335.jpg;20170317-110906-127153.jpg;20170317-111734-564797.jpg;20170317-111735-138914.jpg;20170317-111735-260117.jpg;20170317-111735-376138.jpg;20170317-111735-489948.jpg;20170318-010736-114755.jpg;20170318-010736-726375.jpg;20170318-010736-804368.jpg;20170318-010736-885608.jpg;20170318-010737-005685.jpg;","packet_count":25,"packet_id":0,"packet_length":0,"packet_type":6}
//         */
//        Toast.makeText(JVPlayActivity.this, "查询卡上的所有照片", Toast.LENGTH_LONG).show();
//        Jni.sendString(channel.getIndex(), JVNetConst.JVN_RSP_TEXTDATA, true,
//                JVNetConst.RC_EX_CHECK_FILE, JVNetConst.EX_DQP_CHECK_FILE, "type=1;start=2;count=5;");
//    }

//    //播放卡上的录像
//    public void playVideo(View view) {
//        if (null != channel && channel.isConnected()) {
//
//            /**
//             * acBuffStr 是录像在设备存储卡上的位置 例如 /rec/00/VIDEO/20170317-113805-423017.mp4
//             */
//            String acBuffStr = AppConsts.DEV_VIDEO_PATH + "20170317-113805-423017" + AppConsts.VIDEO_MP4_KIND;
//            if (null != acBuffStr && !"".equalsIgnoreCase(acBuffStr)) {
//                Intent intent = new Intent();
//                intent.setClass(this,
//                        JVRemotePlayBackActivity.class);
//                intent.putExtra("IndexOfChannel", channel.getIndex());
//                intent.putExtra("acBuffStr", acBuffStr);
//                this.startActivity(intent);
//            }
//        }
//
//    }

//    //下载卡上的文件，此接口既可以下载视频也可以下载照片
//    public void downFile(View view) {
//        if (null != channel && channel.isConnected()) {
//
//            /**
//             * imagePath 是文件在设备存储卡上的位置； 录像的路径 例如 /rec/00/VIDEO/20170317-113805-423017.mp4 ；照片的路径 例如 /rec/00/IMAGE/20150101-000010-182737.jpg
//             *
//             * savePath 是文件保存到手机端的完整路径，包括文件名和后缀
//             *
//             * 下载的回调在 onHandler里面 what = JVNetConst.JVN_RSP_DOWNLOADDATA ，下载进度和下载成功失败分别有回调，处理即可
//             */
//            String imagePath = "/rec/00/VIDEO/20170317-113805-423017.mp4";
//            String savePath = AppConsts.DOWNLOAD_PATH + "20170317-113805-423017.mp4";
//
//            Log.e("downImg", "imagePath=" + imagePath + ";savePath=" + savePath);
//            JniUtil.startRemoteDownload(channel.getIndex(), imagePath.getBytes(), savePath);
//        }
//    }


    //抓拍
    public void capture(View view) {
        if (JniUtil.capture(channelIndex)) {
            Toast.makeText(TestPlay.this, "抓拍成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(TestPlay.this, "抓拍失败", Toast.LENGTH_SHORT).show();
        }
    }

//    //云台
//    public void ptz(View view) {
//        if (View.VISIBLE == ytLayout.getVisibility()) {
//            ytLayout.setVisibility(View.GONE);
//        } else {
//            ytLayout.setVisibility(View.VISIBLE);
//        }
//    }

//    //串口波特率设置
//    public void changeRate(View view) {
////        Toast.makeText(JVPlayActivity.this, "串口波特率设置成功", Toast.LENGTH_LONG).show();
////        新的串口波特率设置（推荐）
//        Jni.sendString(channel.getIndex(),
//                JVNetConst.JVN_RSP_TEXTDATA, true,
//                JVNetConst.RC_EX_COMTRANS, JVNetConst.EX_COMTRANS_SET,
//                String.format(AppConsts.FORMATTER_UARTBAUT, 9600));
//
//////老的串口波特率设置
////        Jni.sendSuperBytes(channel.getIndex(), JVNetConst.JVN_RSP_TEXTDATA, true, 0x12,
////                0x2A, 38400, 0, 0,
////                null, 0);
//    }

//    //串口波特率获取
//    public void getRate(View view) {
//        Toast.makeText(TestPlay.this, "串口波特率获取成功", Toast.LENGTH_LONG).show();
//        Jni.sendString(channel.getIndex(),
//                JVNetConst.JVN_RSP_TEXTDATA, true,
//                JVNetConst.RC_EX_COMTRANS, JVNetConst.EX_COMTRANS_GET,
//                "");
//    }

//    //设置分辨率 码率 帧率 ok
//    public void changeFPS(View view) {
//        Jni.sendString(channel.getIndex(),
//                JVNetConst.JVN_RSP_TEXTDATA, false, 0,
//                AppConsts.TYPE_SET_PARAM, "[CH1];width=512;height=288;framerate=10;bitrate=450;");
//    }

//    //wifi 信道修改：ok
//    public void changeWifi(View view) {
//        Jni.sendString(channel.getIndex(),
//                JVNetConst.JVN_RSP_TEXTDATA, false, 0,
//                AppConsts.TYPE_SET_PARAM, "channel=6;");
//
//
//    }

//    //wifi ssid 前缀修改：ok
//    public void changeSsid(View view) {
//        Jni.sendString(channel.getIndex(),
//                JVNetConst.JVN_RSP_TEXTDATA, false, 0,
//                AppConsts.TYPE_SET_PARAM, "wifiname=newipc;");
//    }

//    //修改图像参数 对比度 亮度 ok
//    public void changeContrast(View view) {
//        Jni.sendString(channel.getIndex(),
//                JVNetConst.JVN_RSP_TEXTDATA, false, 0,
//                AppConsts.TYPE_SET_PARAM, "brightness=150;contrast=50;");
//    }

//    //图像旋转 支持 0 90 180 270 度
//    public void changeOrientation(View view) {
//        Jni.sendString(channel.getIndex(),
//                JVNetConst.JVN_RSP_TEXTDATA, false, 0,
//                AppConsts.TYPE_SET_PARAM, "rotate=90;");
//    }

    private int currentScreen = 1;

//    //单屏双屏切换
//    public void VR(View view) {
//        if (1 == currentScreen) {
//            currentScreen = 2;
//            Toast.makeText(JVPlayActivity.this, "显示双屏", Toast.LENGTH_LONG).show();
//            Jni.setSurfaceStat(channel.getIndex(), 2, 0);
//        } else {
//            currentScreen = 1;
//            Toast.makeText(JVPlayActivity.this, "显示单屏", Toast.LENGTH_LONG).show();
//            Jni.setSurfaceStat(channel.getIndex(), 1, 0);
//        }

//    }

    private int recFileLength = 0;//录像时间间隔

    /**
     * 录像时间间隔设置
     *
     * @param view
     */
//    public void recordSpace(View view) {
//        recFileLength += 5;
//        Jni.sendString(channel.getIndex(),
//                JVNetConst.JVN_RSP_TEXTDATA, true,
//                JVNetConst.RC_EX_STORAGE, JVNetConst.EX_STORAGE_REC,
//                String.format(AppConsts.FORMATTER_RECFILELENGTH, recFileLength));
//
//        Toast.makeText(JVPlayActivity.this, "录像时间间隔:" + recFileLength, Toast.LENGTH_LONG).show();
//
//    }


    private int bRecEnable = 0;//录像状态  1：开启   0：关闭

    /**
     * SD卡录像开关
     *
     * @param view
     */
//    public void SDCardSwitch(View view) {
//
//        if (0 == bRecEnable) {
//            bRecEnable = 1;
//            Toast.makeText(JVPlayActivity.this, "开启录像", Toast.LENGTH_LONG).show();
//        } else {
//            bRecEnable = 0;
//            Toast.makeText(JVPlayActivity.this, "关闭录像", Toast.LENGTH_LONG).show();
//        }
//        Jni.sendString(channel.getIndex(),
//                JVNetConst.JVN_RSP_TEXTDATA, true,
//                JVNetConst.RC_EX_STORAGE, JVNetConst.EX_STORAGE_REC,
//                String.format(AppConsts.FORMATTER_BRECENABLE, bRecEnable));
//    }


    /**
     * wifi信号强度获取
     *
     * @param view
     */
//    public void wifiStrength(View view) {
//        Toast.makeText(JVPlayActivity.this, "Wifi信号强度获取成功", Toast.LENGTH_LONG).show();
//        Jni.sendString(channel.getIndex(),
//                JVNetConst.JVN_RSP_TEXTDATA, true,
//                JVNetConst.RC_EX_NETWORK, JVNetConst.EX_STA_GET_APINFO, "");
//    }

    private int bSntp = 0;//校时字段：1启用，0禁用

    /**
     * 时间同步
     *
     * @param view
     */
//    public void synchro(View view) {
//
//        if (1 == bSntp) {
//            bSntp = 0;
//            Toast.makeText(JVPlayActivity.this, "关闭自动校时", Toast.LENGTH_LONG).show();
//        } else {
//            bSntp = 1;
//            Toast.makeText(JVPlayActivity.this, "打开自动校时", Toast.LENGTH_LONG).show();
//        }
//
//        String param = String.format(AppConsts.FORMATTER_BSNTP, bSntp);
//        Jni.sendString(channel.getIndex(),
//                JVNetConst.JVN_RSP_TEXTDATA, false, 0,
//                AppConsts.TYPE_SET_PARAM, param);
//    }


    /**
     * 分贝报警
     *
     * @param view
     */
//    public void dbAlarm(View view) {
//        Jni.sendString(channel.getIndex(),
//                JVNetConst.JVN_RSP_TEXTDATA, true,
//                AppConsts.RC_EX_IVP, AppConsts.EX_IVP_ASD_SUBMIT,
//                String.format(AppConsts.FORMATTER_DBALARM, 1, 20, 30, 1, 1, 1));
//        Toast.makeText(JVPlayActivity.this, "分贝报警设置成功", Toast.LENGTH_LONG).show();
//    }


    /**
     * 时间设置
     *
     * @param view
     */
//    public void setTime(View view) {
//        if (1 == bSntp) {
//            Toast.makeText(JVPlayActivity.this, "手动设置时间前，请关闭网络校时（时间同步）", Toast.LENGTH_LONG).show();
//        } else {
//            int type = 0;//0是MM/DD/YYYY，1是YYYY-MM-DD，2是DD/MM/YYYY
//            String currentTime = "2017-03-17 19:08:11";//此时间格式是固定的，不需要按照上面的格式设置
//            String time = type + ":" + currentTime;
//            //命令调用完即表示设置成功
//            Jni.sendSuperBytes(channel.getIndex(), JVNetConst.JVN_RSP_TEXTDATA, false,
//                    time.getBytes().length, JVNetConst.RC_SETSYSTEMTIME, 0, 0, 0,
//                    time.getBytes(), time.getBytes().length);
//        }
//    }


    /**
     * 获取是否有SD卡
     *
     * @param view
     */
//    public void sdcard(View view) {
//        Jni.sendString(channel.getIndex(), JVNetConst.JVN_RSP_TEXTDATA, true,
//                JVNetConst.RC_EX_STORAGE, JVNetConst.EX_STORAGE_ACCESS, null);
//    }

//    （3）RC_EX_STORAGE->（9）EX_STORAGE_ACCESS
//            disk_exist=1存在
//=0不存在
//


    /**
     * 开始设备录像
     * 录像开启;开启bRecEnable=1，关闭=0
     *
     * @param view
     */
//    public void startRecord(View view) {
//        Toast.makeText(JVPlayActivity.this, "开始录像", Toast.LENGTH_LONG).show();
//        Jni.sendString(channel.getIndex(), JVNetConst.JVN_RSP_TEXTDATA, true,
//                JVNetConst.RC_EX_STORAGE, JVNetConst.EX_STORAGE_REC, "bRecEnable=1");
//
//    }

    /**
     * 结束设备录像
     * 录像结束;开启bRecEnable=1，关闭=0
     *
     * @param view
     */
//    public void stopRecord(View view) {
//        Toast.makeText(JVPlayActivity.this, "停止录像", Toast.LENGTH_LONG).show();
//        Jni.sendString(channel.getIndex(), JVNetConst.JVN_RSP_TEXTDATA, true,
//                JVNetConst.RC_EX_STORAGE, JVNetConst.EX_STORAGE_REC, "bRecEnable=0");
//    }

    /**
     * 开始设备抓拍
     *
     * @param view
     */
    public void devcapture(View view) {
        Toast.makeText(TestPlay.this, "连拍5张", Toast.LENGTH_LONG).show();
        Jni.sendString(channel.getIndex(), JVNetConst.JVN_RSP_TEXTDATA, true,
                JVNetConst.RC_EX_SNAPSHOT, JVNetConst.EX_SNAPSHOT_SET, "snapshotNum=5");
    }


    int pushSwitch = 0;

    /**
     * 报警推送开关
     *
     * @param view
     */
    public void pushSwitch(View view) {

        if (pushSwitch == 0) {
            Toast.makeText(TestPlay.this, "打开报警推送开关", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(TestPlay.this, "关闭报警推送开关", Toast.LENGTH_LONG).show();
        }

        //1:开   0:关
        JniUtil.setDevSafeState(channel.getIndex(), pushSwitch == 0 ? 1 : 0);
    }


    int alarmSwitch = 0;

    /**
     * 移动侦测开关
     *
     * @param view
     */
    public void alarmSwitch(View view) {

        if (alarmSwitch == 0) {
            Toast.makeText(TestPlay.this, "打开移动侦测开关", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(TestPlay.this, "关闭移动侦测开关", Toast.LENGTH_LONG).show();
        }

        //1:开   0:关
        JniUtil.setMotionDetection(channel.getIndex(), alarmSwitch == 0 ? 1 : 0);
    }

    int sensity = 0;

    /**
     * 移动侦测灵敏度
     *
     * @param view
     */
    public void senAlarm(View view) {
        Toast.makeText(TestPlay.this, "移动侦测灵敏度设置为76", Toast.LENGTH_LONG).show();
        //移动侦测灵敏度范围0-100
        JniUtil.setMDSensitivity(channel.getIndex(), 76);
    }

//    @OnClick({R.id.jv_voice, R.id.jv_talkback, R.id.jv_talkbacks})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.jv_voice:
//                sound();
//                break;
//            case R.id.jv_talkback:
//                singleCall();
//                break;
//            case R.id.jv_talkbacks:
//                doubleCall();
//                break;
//        }
//    }

    /**
     * 长按--云台事件
     */
    class LongClickListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            int cmd = 0;
            switch (v.getId()) {
//                case R.id.jv_up: // up
//                    cmd = JVNetConst.JVN_YTCTRL_U;
//                    break;
//                case R.id.jv_down: // down
//                    cmd = JVNetConst.JVN_YTCTRL_D;
//                    break;
//                case R.id.jv_left: // left
//                    cmd = JVNetConst.JVN_YTCTRL_L;
//                    break;
//                case R.id.jv_right:// right
//                    cmd = JVNetConst.JVN_YTCTRL_R;
//                    break;
//                case R.id.autoimage: // auto
//                    if (action == MotionEvent.ACTION_DOWN) {
//                        if (channel.isAuto()) {// 已经开启自动巡航，发送关闭命令
//                            cmd = JVNetConst.JVN_YTCTRL_AT;
//                            channel.setAuto(false);
//                        } else {// 发开始命令
//                            cmd = JVNetConst.JVN_YTCTRL_A;
//                            channel.setAuto(true);
//                        }
//                    }
//                    break;
//                case R.id.zoomout: // bb+
//                    cmd = JVNetConst.JVN_YTCTRL_BBD;
//                    break;
//                case R.id.zoomin: // bb-
//                    cmd = JVNetConst.JVN_YTCTRL_BBX;
//                    break;
//                case R.id.scaleAddImage: // bj+
//                    cmd = JVNetConst.JVN_YTCTRL_BJD;
//                    break;
//                case R.id.scaleSmallImage: // bj-
//                    cmd = JVNetConst.JVN_YTCTRL_BJX;
//                    break;
            }
            try {
                if (action == MotionEvent.ACTION_DOWN) {
                    if (channel != null && channel.isConnected()) {
                        JniUtil.sendCtrlCMDLongPush(channelIndex,
                                cmd, true, 50);
                    }
                } else if (action == MotionEvent.ACTION_UP) {

                    if (channel != null && channel.isConnected()) {
                        JniUtil.sendCtrlCMDLongPush(channelIndex,
                                cmd, false, 50);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }


    @Override
    protected void onPause() {
        JniUtil.pauseSurface(channelIndex);
        super.onPause();
    }

    protected class MyHandler extends Handler {

        private TestPlay activity;

        public MyHandler(TestPlay activity) {
            this.activity = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            activity.handlerNotify.onHandler(msg.what, msg.arg1, msg.arg2, msg.obj);
            super.handleMessage(msg);
        }

    }

}
