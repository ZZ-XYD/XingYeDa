package com.xingyeda.ehome.zhibo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.ldl.okhttp.OkHttpUtils;
import com.ldl.okhttp.callback.StringCallback;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.base.EHomeApplication;
import com.xingyeda.ehome.base.LitePalUtil;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.push.ExampleUtil;
import com.xingyeda.ehome.push.TagAliasOperatorHelper;
import com.xingyeda.ehome.util.AESUtils;
import com.xingyeda.ehome.util.SharedPreUtil;
import com.xingyeda.ehome.util.SpaceItemDecoration;
import com.xingyeda.ehome.view.PercentLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.onekeyshare.OnekeyShare;
import okhttp3.Call;

import static com.xingyeda.ehome.R.id.linkState;
import static com.xingyeda.ehome.push.TagAliasOperatorHelper.ACTION_ADD;
import static com.xingyeda.ehome.push.TagAliasOperatorHelper.ACTION_DELETE;
import static com.xingyeda.ehome.push.TagAliasOperatorHelper.ACTION_GET;
import static com.xingyeda.ehome.push.TagAliasOperatorHelper.TagAliasBean;
import static com.xingyeda.ehome.push.TagAliasOperatorHelper.sequence;


public class ActivitySharePlay extends BaseActivity implements IHandlerNotify, IHandlerLikeNotify {

    protected MyHandler handler = new MyHandler(this);
    @Bind(R.id.zb_playsurface_layout)
    PercentRelativeLayout zbPlaysurfaceLayout;
    //    @Bind(R.id.zb_content)
//    TextView zbContent;
//    @Bind(R.id.zb_scrollview)
//    ScrollView zbScrollview;
    @Bind(R.id.zb_edit)
    EditText zbEdit;
    @Bind(R.id.share_play_title)
    TextView sharePlayTitle;
    @Bind(R.id.describe)
    TextView describe;
    @Bind(R.id.zb_play_recylerView)
    RecyclerView mRecyclerView;
    private IHandlerNotify handlerNotify = this;
    @Bind(R.id.zb_play_code_rate_show)
    PercentLinearLayout playCodeRateShow;
    @Bind(R.id.zb_play_code_rate_text)
    TextView playCodeRate;
//    @Bind(R.id.zb_linkstate)
//    TextView linkState;
    @Bind(R.id.zb_linkstate)
    ImageView mLgingImg;

    private AnimationDrawable mAnimation;
    private int channelIndex;
    private Device device;
    private Channel channel;
    //视频播放surface
    @Bind(R.id.zb_playsurface)
    SurfaceView playSurface;
    private SurfaceHolder surfaceHolder;
    private String type;
    private InputMethodManager manager;
    private String mTitle;
    private String mEquipmentId;
    private String mRoomId;
    private String mDescribe;
    private String mUserName;
    private String enSharePassword = "";

    public static final String ACTION_NAME = "chatMessage";

    private List<MessageBean> mList = new ArrayList<>();
    private MessageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ((EHomeApplication) getApplication()).setCurrentNotifyer(this);
        mTitle = getIntent().getExtras().getString("name");
        mRoomId = getIntent().getExtras().getString("roomId");
        mDescribe = getIntent().getExtras().getString("describe");


        setTag(1, "room_" + mRoomId);
//        setTag(1, "q123456");
        registerBoradcastReceiver();
        enter();
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        initUi();
        initSettings();


    }


    public void setTag(int type, String tag) {
        Set<String> tags = null;
        int action = -1;
        switch (type) {
            case 1:
                //增加tag
                tags = getInPutTags(tag);
                if (tags == null) {
                    return;
                }
                action = ACTION_ADD;
                break;
            case 2:
                tags = getInPutTags(tag);
                if (tags == null) {
                    return;
                }
                action = ACTION_DELETE;
                break;
            case 3:
                action = ACTION_GET;
                break;
        }
        TagAliasBean tagAliasBean = new TagAliasBean();
        tagAliasBean.action = action;
        sequence++;
        tagAliasBean.tags = tags;
        tagAliasBean.isAliasAction = false;
        TagAliasOperatorHelper.getInstance().handleAction(getApplicationContext(), sequence, tagAliasBean);
    }

    /**
     * 获取输入的tags
     */
    private Set<String> getInPutTags(String tag) {
        // 检查 tag 的有效性
        if (TextUtils.isEmpty(tag)) {
            Toast.makeText(getApplicationContext(), R.string.error_tag_empty, Toast.LENGTH_SHORT).show();
            return null;
        }

        // ","隔开的多个 转换成 Set
        String[] sArray = tag.split(",");
        Set<String> tagSet = new LinkedHashSet<String>();
        for (String sTagItme : sArray) {
            if (!ExampleUtil.isValidTagAndAlias(sTagItme)) {
                Toast.makeText(getApplicationContext(), R.string.error_tag_gs_empty, Toast.LENGTH_SHORT).show();
                return null;
            }
            tagSet.add(sTagItme);
        }
        if (tagSet.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.error_tag_empty, Toast.LENGTH_SHORT).show();
            return null;
        }
        return tagSet;
    }

    //进入
    private void enter() {
        Map<String, String> params = new HashMap<>();
        if (LitePalUtil.getUserInfo() != null) {
            params.put("uid", SharedPreUtil.getString(mContext,"userId"));
        } else {
            params.put("uid", "");
            params.put("regKey", JPushInterface.getRegistrationID(mContext));
        }
        params.put("roomId", mRoomId);
        OkHttp.get(mContext, ConnectPath.CAMERA_ROOM_ENTER, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }));

    }

    //退出
    private void exit() {
        Map<String, String> params = new HashMap<>();
        if (LitePalUtil.getUserInfo() != null) {
            params.put("uid", SharedPreUtil.getString(mContext,"userId"));
        } else {
            params.put("uid", "");
            params.put("regKey", JPushInterface.getRegistrationID(mContext));
        }
        params.put("roomId", mRoomId);
        OkHttp.get(mContext, ConnectPath.CAMERA_ROOM_EXIT, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }));

    }

    private void sendShareMessage(String msg) {
        Map<String, String> params = new HashMap<>();
        if (LitePalUtil.getUserInfo() != null) {
            params.put("uid", SharedPreUtil.getString(mContext,"userId"));
        } else {
            params.put("uid", "");
            params.put("regKey", JPushInterface.getRegistrationID(mContext));
        }
        params.put("roomId", mRoomId);
        params.put("content", msg);
        if (zbEdit!=null) {
            zbEdit.setText("");
        }
        OkHttp.get(mContext, ConnectPath.CAMERA_SEND_MESSAGE, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
//                zbContent.append("我说:" + zbEdit.getText().toString() + "\n");
//                zbEdit.setText("");
            }
        }));

    }

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(ActivitySharePlay.ACTION_NAME);
        // 注册广播
        mContext.registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ActivitySharePlay.ACTION_NAME)) {
                String name = intent.getExtras().getString("name");
                String content = intent.getExtras().getString("content");
                String time = intent.getExtras().getString("time");
                //接受消息
//                zbContent.append(name + ":" + content + "\n");
                mList.add(new MessageBean(name,content));
                mAdapter = new MessageAdapter(mList);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount()-1);

            }

        }

    };

    private void upload(String path) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("roomId", mRoomId);
        File file = new File(path);
        if (!file.exists()) {
            Toast.makeText(mContext, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpUtils.post()
                .addFile("img", getFileName(path.toString()), file)
                .url(ConnectPath.CAMERA_UPDATE_IMAGE)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
//                        BaseUtils.showShortToast(mContext, R.string.upload_failed);
                    }

                    @Override
                    public void onResponse(String response, int id) {
//                        BaseUtils.showShortToast(mContext, R.string.uploaded_successfully);
                    }

                });
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
        mEquipmentId = getIntent().getExtras().getString("equipmentId");
//        String devNum = "H27319279";
//        String devNum = "H25746598";
        String devNum = mEquipmentId;
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
        setContentView(R.layout.activity_share_play);
        ButterKnife.bind(this);


        mAnimation = (AnimationDrawable) mLgingImg.getBackground();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(10));

        sharePlayTitle.setText(mTitle);
        describe.setText(mDescribe);


        ViewGroup.LayoutParams para = zbPlaysurfaceLayout.getLayoutParams();
//        para.width=;//修改宽度
        para.height = mScreenH / 3;//修改高度
        zbPlaysurfaceLayout.setLayoutParams(para);

        surfaceHolder = playSurface.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (!channel.isConnected()) {
//                    linkState.setVisibility(View.VISIBLE);
//                    linkState.setText(R.string.connecting);
                    mAnimation.start();
                    connect(channel, holder.getSurface());

                } else if (channel.isConnected()
                        && channel.isPaused()) {
                    boolean result = JniUtil.resumeVideo(channelIndex, holder.getSurface());
                    channel.setPaused(false);
                    if (result) {
                        boolean resumeRes = JniUtil.resumeSurface(channelIndex, holder.getSurface());
                        if (resumeRes) {
//                            linkState.setVisibility(View.GONE);
                            mLgingImg.setVisibility(View.GONE);
                            mAnimation.stop();
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
            case AppConsts.CALL_CONNECT_CHANGE: {
                switch (arg2) {
                    case 1:
//                        linkState.setText(R.string.connect_ok);
                        channel.setConnected(true);
                        break;

                    case 2:
//                        linkState.setText(R.string.error2);
                        channel.setConnected(false);
                        channel.setPaused(true);
                        break;
                    case 4:
//                        linkState.setText(R.string.error4);
                        channel.setConnected(false);
                        channel.setPaused(true);
                        break;
                    case 6:
//                        linkState.setText(R.string.error6);
                        channel.setConnected(false);
                        channel.setPaused(true);
                        break;
                    case 7:
//                        linkState.setText(R.string.error7);
                        channel.setConnected(false);
                        channel.setPaused(true);
                        break;

                    case 5:
//                        linkState.setText(R.string.error5);
                        channel.setConnected(false);
                        channel.setPaused(true);
                        break;
                    case 8:
//                        linkState.setText(R.string.error8);
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
//                linkState.setVisibility(View.GONE);
                mLgingImg.setVisibility(View.GONE);
                mAnimation.stop();
                channel.setConnected(true);
                channel.setPaused(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        capture();
                        JniUtil.startAudioMonitor(channelIndex);
                        if (!JniUtil.isPlayAudio(channelIndex)) {
//                            JniUtil.stopAudioMonitor(channelIndex);
//                            Toast.makeText(ActivitySharePlay.this, "关闭声音", Toast.LENGTH_SHORT).show();
//                        } else {
                            JniUtil.startAudioMonitor(channelIndex);
//                            Toast.makeText(ActivitySharePlay.this, "打开声音", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 1000);
                break;
            }

            case AppConsts.CALL_NORMAL_DATA: {
//                linkState.setText(R.string.o_ok);

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
                                    String streamText = dataObj.getString(AppConsts.TAG_MSG);
                                    HashMap<String, String> streamMap = JniUtil.genMsgMap(streamText);
                                    int uartbaut = Integer.parseInt(streamMap.get(AppConsts.TAG_UARTBAUT));

                                    String streamMsg = "串口波特率:uartbaut=" + uartbaut;
                                    Toast.makeText(ActivitySharePlay.this, streamMsg, Toast.LENGTH_LONG).show();
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
                                        Toast.makeText(ActivitySharePlay.this, "有SD卡", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(ActivitySharePlay.this, "无SD卡", Toast.LENGTH_LONG).show();
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
                        if (channel.isSingleVoice()) {//单向对讲
                            JniUtil.startRecordSendAudio(channelIndex);
                            JniUtil.resumeAudio(channelIndex);
                            channel.setVoiceCalling(true);//设置成正在对讲状态
                            break;
                        }
                    }

                    //暂停语音聊天
                    case JVNetConst.JVN_CMD_CHATSTOP: {
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
//            switch (v.getId()) {
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
//            }
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
        super.onDestroy();
        ButterKnife.unbind(this);
        setTag(2, "room_" + mRoomId);
        exit();
        unregisterReceiver(mBroadcastReceiver);
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @OnClick({R.id.zb_play_code_rate_text, R.id.zb_play_super_definition, R.id.zb_play_high_definition, R.id.zb_play_fluency_definition, R.id.zb_send, R.id.share_play_back, R.id.share_play_share, R.id.describe})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.zb_play_code_rate_text:
                if (playCodeRateShow.isShown()) {
                    playCodeRateShow.setVisibility(View.GONE);
                } else {
                    playCodeRateShow.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.zb_play_super_definition:
                playCodeRate.setText("超清");
                JniUtil.changeStream(channelIndex, 1);
                if (playCodeRateShow.isShown()) {
                    playCodeRateShow.setVisibility(View.GONE);
                }
                break;
            case R.id.zb_play_high_definition:
                playCodeRate.setText("高清");
                JniUtil.changeStream(channelIndex, 2);
                if (playCodeRateShow.isShown()) {
                    playCodeRateShow.setVisibility(View.GONE);
                }
                break;
            case R.id.zb_play_fluency_definition:
                playCodeRate.setText("标清");
                JniUtil.changeStream(channelIndex, 3);
                if (playCodeRateShow.isShown()) {
                    playCodeRateShow.setVisibility(View.GONE);
                }
                break;
            case R.id.zb_send:
                hintKbTwo();
                String comtent = zbEdit.getText().toString();
                if (comtent != null && !"".equals(comtent)) {
                    sendShareMessage(comtent);
                }
                break;
            case R.id.share_play_back:
                onBackPressed();
                break;
            case R.id.describe:
                if (describe.getMaxLines() == 2) {
                    describe.setMaxLines(999);
                    describe.postInvalidate();
                } else {
                    describe.setMaxLines(2);
                    describe.postInvalidate();
                }
                break;
            case R.id.share_play_share:
                if (LitePalUtil.getUserInfo()!= null) {
                    mUserName = LitePalUtil.getUserInfo().getmUsername();//获取用户昵称
                    String uid = SharedPreUtil.getString(mContext,"userId");
                    mEquipmentId = getIntent().getExtras().getString("equipmentId");
                    mRoomId = getIntent().getExtras().getString("roomId");
                    String sharePassword = mEquipmentId + "|" + uid + "|" + mRoomId;
                    try {
                        enSharePassword = AESUtils.Encrypt(sharePassword, "1234567890123456");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (!enSharePassword.equals("")) {
                        showShare();
                    }
                } else {
                    showShortToast("请登录后再尝试进行分享");
                }
                break;
        }
    }

    //抓拍
    public void capture() {
        final String fileName = System.currentTimeMillis() + AppConsts.IMAGE_JPG_KIND;
        if (capture(channelIndex, fileName)) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    upload(AppConsts.CAPTURE_PATH + fileName);
                }
            }, 1000);

//            Toast.makeText(ActivitySharePlay.this, "抓拍成功,保存" + AppConsts.CAPTURE_PATH + fileName, Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(ActivitySharePlay.this, "抓拍失败", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean capture(int index, String path) {
        boolean res = Jni.screenshot(index, AppConsts.CAPTURE_PATH + path, 100);
        return res;
    }

    //声音开关
    public void sound() {
        if (JniUtil.isPlayAudio(channelIndex)) {
            JniUtil.stopAudioMonitor(channelIndex);
            Toast.makeText(ActivitySharePlay.this, "关闭声音", Toast.LENGTH_SHORT).show();
        } else {
            JniUtil.startAudioMonitor(channelIndex);
            Toast.makeText(ActivitySharePlay.this, "打开声音", Toast.LENGTH_SHORT).show();
        }
    }

    //单向对讲
    public void singleCall() {
        channel.setSingleVoice(true);
        if (channel.isVoiceCalling()) {
            channel.setSingleVoice(false);
            channel.setVoiceCalling(false);
            JniUtil.stopVoiceCall(channelIndex);
            JniUtil.stopAudioMonitor(channelIndex);
//            sendingBtn.setVisibility(View.GONE);
        } else {
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
//            channel.setSingleVoice(false);
//            channel.setVoiceCalling(false);
            JniUtil.stopVoiceCall(channelIndex);
            JniUtil.stopAudioMonitor(channelIndex);
        } else {
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

        private ActivitySharePlay mActivity;

        public MyHandler(ActivitySharePlay activity) {
            mActivity = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            mActivity.handlerNotify.onHandler(msg.what, msg.arg1, msg.arg2, msg.obj);
            super.handleMessage(msg);
        }

    }

    //此方法只是关闭软键盘
    private void hintKbTwo() {
        if (manager.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }


    //点击空白影藏软键盘
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }

    public String getFileName(String pathandname) {

        int start = pathandname.lastIndexOf("/");
        if (start != -1) {
            return pathandname.substring(start + 1);
        } else {
            return null;
        }
    }

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        oks.disableSSOWhenAuthorize();//关闭sso授权

//        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段0
        oks.setText("content", "【来自" + mUserName + "的直播观看邀请】,复制这条信息￥" + enSharePassword + "￥后打开创享E家进入直播间!");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(this);
    }
}