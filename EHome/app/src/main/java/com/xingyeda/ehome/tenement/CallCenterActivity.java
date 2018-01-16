package com.xingyeda.ehome.tenement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.jovision.JniUtil;
import com.ldl.okhttp.callback.StringCallback;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.base.LitePalUtil;
import com.xingyeda.ehome.http.okhttp.BaseStringCallback;
import com.xingyeda.ehome.http.okhttp.CallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.push.ExampleUtil;
import com.xingyeda.ehome.push.TagAliasOperatorHelper;
import com.xingyeda.ehome.util.AESUtils;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.LogUtils;
import com.xingyeda.ehome.util.SharedPreUtil;
import com.xingyeda.ehome.util.SpaceItemDecoration;
import com.xingyeda.ehome.zhibo.ActivitySharePlay;
import com.xingyeda.ehome.zhibo.MessageAdapter;
import com.xingyeda.ehome.zhibo.MessageBean;

import org.json.JSONException;
import org.json.JSONObject;

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
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;
import okhttp3.Call;

import static com.xingyeda.ehome.push.TagAliasOperatorHelper.ACTION_ADD;
import static com.xingyeda.ehome.push.TagAliasOperatorHelper.ACTION_DELETE;
import static com.xingyeda.ehome.push.TagAliasOperatorHelper.ACTION_GET;
import static com.xingyeda.ehome.push.TagAliasOperatorHelper.sequence;

public class CallCenterActivity extends BaseActivity {

    @Bind(R.id.zb_playsurface_layout)
    PercentRelativeLayout zbPlaysurfaceLayout;
    @Bind(R.id.video_vitamio_videoView)
    VideoView mVideoView;
    @Bind(R.id.zb_play_recylerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.zb_edit)
    EditText zbEdit;

    private String mId;
    private InputMethodManager manager;
    private List<MessageBean> mList = new ArrayList<>();
    private MessageAdapter mAdapter;

    private String mPlaySite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Vitamio.isInitialized(getApplicationContext());
        setContentView(R.layout.activity_call_center);
        ButterKnife.bind(this);
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mId = LitePalUtil.getHomeBean().getmCommunityId();

        ViewGroup.LayoutParams para = zbPlaysurfaceLayout.getLayoutParams();
//        para.width=;//修改宽度
        para.height = mScreenH / 3 ;//修改高度
        zbPlaysurfaceLayout.setLayoutParams(para);

        mAnimation = (AnimationDrawable) mLgingImg.getBackground();
        mAnimation.start();

        setTag(1, "room_" + mId);
        init();
        enter();
        registerBoradcastReceiver();

        getPlaySite();

    }
    @Bind(R.id.no_monitoring)
    ImageView mMonitor_abnormal;
    @Bind(R.id.video_loading)
    FrameLayout mLoading;
    @Bind(R.id.video_logingimg)
    ImageView mLgingImg;

    private AnimationDrawable mAnimation;


    //获取播放地址
    public void getPlaySite() {
        Map<String,String> params = new HashMap<>();
        params.put("xId",mId);
        OkHttp.get(mContext, ConnectPath.PROPERTYLIVE_PATH, params,new BaseStringCallback(mContext, new CallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
                {
                    try {
                        JSONObject jobj = response.getJSONObject("obj");
                        mPlaySite = jobj.has("liveUrl") ? jobj.getString("liveUrl") : "";
                        if (!mPlaySite.equals("")) {
                            playfunction(mPlaySite);
                            preparePlay();
                        } else {
                            if (mLoading!=null) {
                                mLoading.setVisibility(View.GONE);
                            }
                            if (mMonitor_abnormal != null) {
                                mMonitor_abnormal.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void parameterError(JSONObject response) {
                if (mAnimation != null) {
                    mAnimation.stop();
                }
                if (mLoading!=null) {
                    mLoading.setVisibility(View.GONE);
                }
                if (mMonitor_abnormal != null) {
                    mMonitor_abnormal.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure() {
                if (mAnimation != null) {
                    mAnimation.stop();
                }
                if (mLoading!=null) {
                    mLoading.setVisibility(View.GONE);
                }
                if (mMonitor_abnormal != null) {
                    mMonitor_abnormal.setVisibility(View.VISIBLE);
                }
            }
        }));
    }

    private void preparePlay() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                    if (mVideoView != null) {
                        if (mVideoView.isPlaying()) {
                            if (mAnimation != null) {
                                mAnimation.stop();
                            }
                            if (mLoading!=null) {
                                mLoading.setVisibility(View.GONE);
                            }
                        } else {
                            mVideoView.start();
                            preparePlay();
                        }
                    }
                }
        };
        new Handler().postDelayed(runnable, 500);
    }

    @OnClick({R.id.zb_send, R.id.share_play_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
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
        }
    }

    //进入
    private void enter() {
        Map<String, String> params = new HashMap<>();
        if (LitePalUtil.getUserInfo() != null) {
            params.put("uid", SharedPreUtil.getString(mContext, "userId"));
        } else {
            params.put("uid", "");
            params.put("regKey", JPushInterface.getRegistrationID(mContext));
        }
        params.put("roomId", mId);
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
            params.put("uid", SharedPreUtil.getString(mContext, "userId"));
        } else {
            params.put("uid", "");
            params.put("regKey", JPushInterface.getRegistrationID(mContext));
        }
        params.put("roomId", mId);
        OkHttp.get(mContext, ConnectPath.CAMERA_ROOM_EXIT, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }));

    }
    private void sendShareMessage(String msg) {
        Map<String, String> params = new HashMap<>();
        if (LitePalUtil.getUserInfo() != null) {
            params.put("uid", SharedPreUtil.getString(mContext, "userId"));
        } else {
            params.put("uid", "");
            params.put("regKey", JPushInterface.getRegistrationID(mContext));
        }
        params.put("roomId", mId);
        params.put("content", msg);
        if (zbEdit != null) {
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

    private void init() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(10));
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
        TagAliasOperatorHelper.TagAliasBean tagAliasBean = new TagAliasOperatorHelper.TagAliasBean();
        tagAliasBean.action = action;
        sequence++;
        tagAliasBean.tags = tags;
        tagAliasBean.isAliasAction = false;
        TagAliasOperatorHelper.getInstance().handleAction(getApplicationContext(), sequence, tagAliasBean);
    }




    private void playfunction(String path) {
        if(path == null || path.length() <= 0){
            BaseUtils.showShortToast(mContext, R.string.play_address_error);
            return;
        } else {
            rtmpPlay(path);
        }

    }

    private void rtmpPlay(String path) {
        LogUtils.i("RTMP的播放地址 : " + path);
            if (mVideoView != null) {

                mVideoView.setBackgroundResource(0);
                mVideoView.setVideoPath(path);
                mVideoView.setBufferSize(128);
                mVideoView.requestFocus();
                mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0);
                mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {
                                mediaPlayer.setPlaybackSpeed(1.0f);
                            }
                        });
                mVideoView.start();
            }
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
                mList.add(new MessageBean(name, content));
                mAdapter = new MessageAdapter(mList);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);

            }

        }
    };
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        setTag(2, "room_" + mId);
        exit();
        unregisterReceiver(mBroadcastReceiver);
    }


}
