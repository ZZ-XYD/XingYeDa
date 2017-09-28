package com.jovision.account;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.view.PercentLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class JVPlayActivity extends BaseActivity implements IHandlerNotify, IHandlerLikeNotify {

    protected MyHandler handler = new MyHandler(this);
    private IHandlerNotify handlerNotify = this;
    @Bind(R.id.play_code_rate_show)
    PercentLinearLayout playCodeRateShow;
    @Bind(R.id.play_code_rate_text)
    TextView playCodeRate;
    @Bind(R.id.jv_address)
    TextView jvAddress;
    @Bind(R.id.jv_talkback)
    ImageView jvTalkback;
    @Bind(R.id.jv_talkbacks)
    ImageView jvTalkbacks;
    @Bind(R.id.jv_up)
    ImageView jvUp;
    @Bind(R.id.jv_down)
    ImageView jvDown;
    @Bind(R.id.jv_left)
    ImageView jvLeft;
    @Bind(R.id.jv_right)
    ImageView jvRight;
    @Bind(R.id.jvmy_console)
    RelativeLayout jvmyConsole;
    @Bind(R.id.linkstate)
    TextView linkState;


    private int channelIndex;
    private Device device;
    private Channel channel;
    //视频播放surface
    @Bind(R.id.playsurface)
    SurfaceView playSurface;
    private SurfaceHolder surfaceHolder;
    private String mCameraId;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ((EHomeApplication) getApplication()).setCurrentNotifyer(this);
        mCameraId = getIntent().getExtras().getString("cameraId");
        type = getIntent().getExtras().getString("type");
        initUi();
        initSettings();
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
        jvAddress.setText(getIntent().getExtras().getString("cameraName"));
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
        if ("generalCamera".equals(type)) {
            jvmyConsole.setVisibility(View.GONE);
        } else if ("shakingCamera".equals(type)) {
            jvmyConsole.setVisibility(View.VISIBLE);
            jvUp.setOnTouchListener(new LongClickListener());
            jvDown.setOnTouchListener(new LongClickListener());
            jvLeft.setOnTouchListener(new LongClickListener());
            jvRight.setOnTouchListener(new LongClickListener());
        }

//        playSurface = (SurfaceView) findViewById(R.id.playsurface);

        ViewGroup.LayoutParams para = playSurface.getLayoutParams();
//        para.width=;//修改宽度
        para.height = mScreenH / 2;//修改高度
        playSurface.setLayoutParams(para);
        surfaceHolder = playSurface.getHolder();

        surfaceHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (!channel.isConnected()) {
                    linkState.setVisibility(View.VISIBLE);
                    linkState.setText(R.string.connecting);
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


    }

    @Override
    public void onNotify(int what, int arg1, int arg2, Object obj) {
        handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
    }

    @Override
    public void onHandler(int what, int arg1, int arg2, Object obj) {
        switch (what) {
//            case AppConsts.CALL_DOWNLOAD: {// 远程回放文件下载
//
//                Log.e("CALL_DOWNLOAD", "what=" + what + ";arg1=" + arg1 + ";arg2=" + arg2+ ";obj=" + obj.toString());

//                switch (arg2) {
//                    case JVNetConst.JVN_RSP_DOWNLOADDATA: {// 下载进度
//                        // 进度{"length":2230204,"size":204800}
//                        //length是总大小，size是每次下载的大小，size累加起来等于length
//                        Log.e("JVN_RSP_DOWNLOADDATA", "what=" + what + ";arg1=" + arg1 + ";arg2=" + arg2+ ";obj=" + obj.toString());
//                        break;
//                    }
//                    case JVNetConst.JVN_RSP_DOWNLOADOVER: {// 下载完成
//                        Toast.makeText(JVPlayActivity.this, "下载完成", Toast.LENGTH_LONG).show();
//                        break;
//                    }
//                    case JVNetConst.JVN_RSP_DOWNLOADE: {// 下载失敗
//                        Toast.makeText(JVPlayActivity.this, "下载失敗", Toast.LENGTH_LONG).show();
//                        break;
//                    }
//                }

//                break;
//            }


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
                                    Toast.makeText(JVPlayActivity.this, streamMsg, Toast.LENGTH_LONG).show();
                                    Log.e("uartbaut", streamMsg);

                                    break;
                                }
                                case JVNetConst.JVN_STREAM_INFO: {//码流数据回调
                                    String streamText = dataObj.getString(AppConsts.TAG_MSG);
                                    Log.e("streamText", streamText);
                                    HashMap<String, String> streamMap = JniUtil.genMsgMap(streamText);
                                    int mobileQuality = Integer.parseInt(streamMap.get(AppConsts.TAG_STREAM));

//                                    bSntp = Integer.parseInt(streamMap.get(AppConsts.TAG_BSNTP));
//                                    recFileLength = Integer.parseInt(streamMap.get(AppConsts.TAG_RECFILELENGTH));
//                                    bRecEnable = Integer.parseInt(streamMap.get(AppConsts.TAG_BRECENABLE));


//                                    pushSwitch = Integer.parseInt(streamMap.get(AppConsts.TAG_SET_DEV_SAFE_STATE));
//                                    alarmSwitch = Integer.parseInt(streamMap.get(AppConsts.TAG_SET_MDENABLE_STATE));
//                                    sensity = Integer.parseInt(streamMap.get(AppConsts.TAG_NMDSENSITIVITY));


                                    channel.setStreamIndex(mobileQuality);

//                                    String streamMsg = "mobileQuality=" + mobileQuality
//                                            + ";bSntp=" + bSntp
//                                            + ";recFileLength=" + recFileLength
//                                            + ";bRecEnable=" + bRecEnable
//                                            + ";pushSwitch=" + pushSwitch
//                                            + ";alarmSwitch=" + alarmSwitch
//                                            + ";sensity=" + sensity;
//                                    Toast.makeText(JVPlayActivity.this, streamMsg, Toast.LENGTH_LONG).show();
//                                    Log.e("streamMsg", streamMsg);
                                    break;
                                }
                                case JVNetConst.EX_STORAGE_ACCESS: {//刷新设备sd卡状态
                                    Log.e("SDCard----", obj.toString());
//                                    {"extend_arg1":64,"extend_arg2":0,"extend_arg3":0,"extend_type":1,"flag":7,"msg":"nStorage=1;[STORAGE1];nTotalSize=15135;nUsedSize=7573;nStatus=4;","packet_count":3,"packet_id":0,"packet_length":0,"packet_type":6}

                                    String sdCardText = dataObj.getString(AppConsts.TAG_MSG);
                                    HashMap<String, String> sdCardMap = JniUtil.genMsgMap(sdCardText);
                                    int diskExist = Integer.parseInt(sdCardMap.get(AppConsts.TAG_DISK));
                                    if (1 == diskExist) {
                                        Toast.makeText(JVPlayActivity.this, "有SD卡", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(JVPlayActivity.this, "无SD卡", Toast.LENGTH_LONG).show();
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
//                        Toast.makeText(JVPlayActivity.this, "主控同意对讲了！", Toast.LENGTH_SHORT).show();
                        if (channel.isSingleVoice()) {//单向对讲
//                            sendingBtn.setVisibility(View.VISIBLE);
//                            Log.e(TAG, "JVN_RSP_CHATACCEPT-主控同意对讲了！longClicking=" + longClicking);
//                            if (longClicking) {
//                                Toast.makeText(JVPlayActivity.this, "您现在可以说话了！", Toast.LENGTH_SHORT).show();
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
//                        Toast.makeText(JVPlayActivity.this, "收到chatstop", Toast.LENGTH_SHORT).show();
//                        if (channel.isVoiceCalling()) {//正在对讲，停止对讲
//                        } else {//设备已在其他客户端开启对讲
//                            Toast.makeText(JVPlayActivity.this, "设备已在其他客户端开启对讲", Toast.LENGTH_SHORT).show();
//                        }
                        channel.setVoiceCalling(false);
                        break;
                    }
                }
                break;
            }
        }
    }


    class LongClickListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            int cmd = 0;
            switch (v.getId()) {
                case R.id.jv_up: // up
                    cmd = JVNetConst.JVN_YTCTRL_U;
                    break;
                case R.id.jv_down: // down
                    cmd = JVNetConst.JVN_YTCTRL_D;
                    break;
                case R.id.jv_left: // left
                    cmd = JVNetConst.JVN_YTCTRL_L;
                    break;
                case R.id.jv_right:// right
                    cmd = JVNetConst.JVN_YTCTRL_R;
                    break;
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
                    boolean b = channel.isConnected();
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

    @OnClick({R.id.jv_voice, R.id.jv_talkback, R.id.jv_talkbacks, R.id.jv_up, R.id.jv_down, R.id.jv_left, R.id.jv_right
            , R.id.play_code_rate_text, R.id.play_super_definition, R.id.play_high_definition, R.id.play_fluency_definition,R.id.jv_play_rewind})
    public void onViewClicked(View view) {
        switch (view.getId()) {
//            case R.id.jv_back:
//                JVPlayJVActivity.this.finish();
//                break;
            case R.id.jv_voice:
                capture();
                break;
            case R.id.jv_talkback:
                singleCall();
                break;
            case R.id.jv_talkbacks:
                doubleCall();
                break;
            case R.id.jv_up:
                break;
            case R.id.jv_down:
                break;
            case R.id.jv_left:
                break;
            case R.id.jv_right:
                break;
            case R.id.jv_play_rewind:
//                BaseUtils.startActivities(mContext,VideoRewindActivity.class,bundle);
                if (null != channel && channel.isConnected()) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("IndexOfChannel", channel.getIndex());
                    bundle.putInt("ChannelOfChannel", channel.getChannel());
                    bundle.putInt("DeviceType", channel.getParent().getDeviceType());
                    bundle.putBoolean("isJFH", channel.getParent().isJFH());
                    BaseUtils.startActivities(mContext,VideoRewindActivity.class,bundle);
                }
                break;
            case R.id.play_code_rate_text:
                if (playCodeRateShow.isShown()) {
                    playCodeRateShow.setVisibility(View.GONE);
                }else{
                    playCodeRateShow.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.play_super_definition:
                playCodeRate.setText("超清");
                JniUtil.changeStream(channelIndex, 1);
                if (playCodeRateShow.isShown()) {
                    playCodeRateShow.setVisibility(View.GONE);
                }
                break;
            case R.id.play_high_definition:
                playCodeRate.setText("高清");
                JniUtil.changeStream(channelIndex, 2);
                if (playCodeRateShow.isShown()) {
                    playCodeRateShow.setVisibility(View.GONE);
                }
                break;
            case R.id.play_fluency_definition:
                playCodeRate.setText("标清");
                JniUtil.changeStream(channelIndex, 3);
                if (playCodeRateShow.isShown()) {
                    playCodeRateShow.setVisibility(View.GONE);
                }
                break;
        }
    }

//    //修改码流
//    public void changeStream() {
//        int streamIndex = channel.getStreamIndex();
//        int changeIndex = --streamIndex < 1 ? 3 : streamIndex;
//
//        switch (changeIndex) {
//            case 1: {
////                playCodeRate.setText("超清");
////                Toast.makeText(JVPlayActivity.this, "切换到高清，changeIndex=" + changeIndex, Toast.LENGTH_SHORT).show();
//                break;
//            }
//            case 2: {
////                playCodeRate.setText("高清");
////                Toast.makeText(JVPlayActivity.this, "切换到标清，changeIndex=" + changeIndex, Toast.LENGTH_SHORT).show();
//                break;
//            }
//            case 3: {
////                playCodeRate.setText("标清");
////                Toast.makeText(JVPlayActivity.this, "切换到流畅，=" + changeIndex, Toast.LENGTH_SHORT).show();
//                break;
//            }
//        }
//        JniUtil.changeStream(channelIndex, changeIndex);
//    }

    //抓拍
    public void capture() {
        if (JniUtil.capture(channelIndex)) {
            String fileName = System.currentTimeMillis() + AppConsts.IMAGE_JPG_KIND;
            Toast.makeText(JVPlayActivity.this, "抓拍成功,保存" + AppConsts.CAPTURE_PATH + fileName, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(JVPlayActivity.this, "抓拍失败", Toast.LENGTH_SHORT).show();
        }
    }

    //声音开关
    public void sound() {
        if (JniUtil.isPlayAudio(channelIndex)) {
            JniUtil.stopAudioMonitor(channelIndex);
            Toast.makeText(JVPlayActivity.this, "关闭声音", Toast.LENGTH_SHORT).show();
        } else {
            JniUtil.startAudioMonitor(channelIndex);
            Toast.makeText(JVPlayActivity.this, "打开声音", Toast.LENGTH_SHORT).show();
        }
    }

    //单向对讲
    public void singleCall() {
        channel.setSingleVoice(true);
        if (channel.isVoiceCalling()) {
            jvTalkback.setBackgroundResource(R.mipmap.jv_talkback);
            channel.setSingleVoice(false);
            channel.setVoiceCalling(false);
            JniUtil.stopVoiceCall(channelIndex);
            JniUtil.stopAudioMonitor(channelIndex);
//            sendingBtn.setVisibility(View.GONE);
        } else {
            jvTalkback.setBackgroundResource(R.mipmap.jv_talkback3);
            channel.setSingleVoice(true);
            channel.setVoiceCalling(true);
            JniUtil.startVoiceCall(channelIndex, false);
            JniUtil.startAudioMonitor(channelIndex);
        }
//        if (channel.isSingleVoice()) {
//            jvTalkback.setBackgroundResource(R.drawable.jv_talkback3);
//        }else {
//            jvTalkback.setBackgroundResource(R.drawable.jv_talkback);
//        }

    }

    //双向对讲
    public void doubleCall() {
        channel.setSingleVoice(false);
        if (channel.isVoiceCalling()) {
            jvTalkbacks.setBackgroundResource(R.mipmap.jv_talkbacks);
//            channel.setSingleVoice(false);
//            channel.setVoiceCalling(false);
            JniUtil.stopVoiceCall(channelIndex);
            JniUtil.stopAudioMonitor(channelIndex);
        } else {
            jvTalkbacks.setBackgroundResource(R.mipmap.jv_talkbacks3);
//            channel.setSingleVoice(true);
//            channel.setVoiceCalling(true);
            JniUtil.startVoiceCall(channelIndex, true);
            JniUtil.startAudioMonitor(channelIndex);
        }
//        if (channel.isSendingVoice()) {
//            jvTalkbacks.setBackgroundResource(R.drawable.jv_talkbacks3);
//        }else {
//            jvTalkbacks.setBackgroundResource(R.drawable.jv_talkbacks);
//        }
    }

    protected class MyHandler extends Handler {

        private JVPlayActivity mActivity;

        public MyHandler(JVPlayActivity activity) {
            mActivity = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            mActivity.handlerNotify.onHandler(msg.what, msg.arg1, msg.arg2, msg.obj);
            super.handleMessage(msg);
        }

    }
}

