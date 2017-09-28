package com.jovision.account;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jovision.AppConsts;
import com.jovision.JVNetConst;
import com.jovision.JniUtil;
import com.jovision.base.IHandlerLikeNotify;
import com.jovision.base.IHandlerNotify;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.EHomeApplication;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoRemotePlayActivity extends BaseActivity implements IHandlerNotify, IHandlerLikeNotify {

    protected MyHandler handler = new MyHandler(this);
    @Bind(R.id.rewind_play_title)
    TextView rewindPlayTitle;
    @Bind(R.id.playsurface)
    SurfaceView playSurface;
    @Bind(R.id.linkstate)
    TextView linkStateTV;
    @Bind(R.id.playback_seekback)
    SeekBar progressBar;
    @Bind(R.id.playbackpause)
    ImageView pauseImgBtn;
    private IHandlerNotify handlerNotify = this;

    //views
//    private SurfaceView playSurface;// 视频播放view
    private SurfaceHolder holder;
    //    private TextView linkStateTV;// 连接状态
//    private SeekBar progressBar;
//    private ImageView pauseImgBtn;

    //    private SurfaceHolder holder;
    //intent 参数
    private int indexOfChannel;
    private String acBuffStr;
    private String remoteKind;

    private int totalProgress;// 总进度
    private Boolean isPaused = false;// 是否已暂停
    private int currentProgress = 0;// 当前进度
    private int seekProgress;// 手动进度

    /**
     * 远程回放进度条拖动事件
     */
    SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
            seekProgress = arg1;
        }

        @Override
        public void onStartTrackingTouch(SeekBar arg0) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar arg0) {
            currentProgress = seekProgress;
            JniUtil.seekTo(indexOfChannel, seekProgress);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_remote_play);
        ButterKnife.bind(this);
        ((EHomeApplication) getApplication()).setCurrentNotifyer(this);
        indexOfChannel = getIntent().getExtras().getInt("IndexOfChannel", 0);
        acBuffStr = getIntent().getExtras().getString("acBuffStr");
        remoteKind = getIntent().getExtras().getString("remoteKind");
        if ("A".equalsIgnoreCase(remoteKind)) {
            rewindPlayTitle.setText(R.string.video_alarm);
        } else if ("M".equalsIgnoreCase(remoteKind)) {
            rewindPlayTitle.setText(R.string.video_motion);
        } else if ("T".equalsIgnoreCase(remoteKind)) {
            rewindPlayTitle.setText(R.string.video_time);
        } else {
            rewindPlayTitle.setText(R.string.video_normal);
        }

        linkStateTV.setTextColor(Color.GREEN);
        linkStateTV.setVisibility(View.VISIBLE);
        linkStateTV.setText(R.string.connecting);

        progressBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

        holder = playSurface.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                JniUtil.pauseSurface(indexOfChannel);
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                boolean resumeRes = JniUtil.resumeSurface(indexOfChannel,
                        holder.getSurface());
                boolean enable = JniUtil.enableRemotePlay(indexOfChannel, true);
                if (enable) {
                    JniUtil.playRemoteFile(indexOfChannel, acBuffStr);
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
            }
        });

    }

    @Override
    public void onHandler(int what, int arg1, int arg2, Object obj) {
        switch (what) {
            case AppConsts.CALL_CONNECT_CHANGE: {//远程回放，视频断开的处理
                switch (arg2) {
                    // 2 -- 断开连接成功
                    case JVNetConst.DISCONNECT_OK:
                        // 4 -- 连接失败
                    case JVNetConst.CONNECT_FAILED:
                        // 6 -- 连接异常断开
                    case JVNetConst.ABNORMAL_DISCONNECT:
                        // 7 -- 服务停止连接，连接断开
                    case JVNetConst.SERVICE_STOP: {
                        Toast.makeText(VideoRemotePlayActivity.this, R.string.closed, Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }

            case AppConsts.CALL_PLAY_DATA: {// 远程回放数据
                linkStateTV.setVisibility(View.GONE);
                switch (arg2) {
                    case JVNetConst.JVN_DATA_O: {
                        if (0 == totalProgress) {
                            try {
                                JSONObject jobj;
                                jobj = new JSONObject(obj.toString());
                                if (null != jobj) {
                                    totalProgress = jobj.optInt("total");
                                    progressBar.setMax(totalProgress);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    }
                    case JVNetConst.JVN_DATA_I:
                    case JVNetConst.JVN_DATA_B:
                    case JVNetConst.JVN_DATA_P: {
                        currentProgress++;
                        progressBar.setProgress(currentProgress);
                        break;
                    }
                }

                break;
            }
            case AppConsts.CALL_PLAY_DOOMED: {// 远程回放结束
                if (AppConsts.PLAYBACK_DONE == arg2) {
                    this.finish();
                }
            }
            default:
                break;
        }

    }

    @Override
    public void onNotify(int what, int arg1, int arg2, Object obj) {
        handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
    }

    @Override
    public void onBackPressed() {
        JniUtil.stopRemoteFile(indexOfChannel);
        VideoRemotePlayActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JniUtil.enableRemotePlay(indexOfChannel, false);
    }

    @OnClick({R.id.rewind_play_back, R.id.playbackpause})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rewind_play_back:
                finish();
                break;
            case R.id.playbackpause:
                if (isPaused) {
                    isPaused = false;
                    pauseImgBtn.setImageResource(R.mipmap.video_stop_icon);
                    // 继续播放视频
                    JniUtil.goonPlay(indexOfChannel);
                } else {
                    isPaused = true;
                    // 暂停视频
                    JniUtil.pausePlay(indexOfChannel);
                    pauseImgBtn.setImageResource(R.mipmap.video_play_icon);
                }
                break;
        }
    }


    protected class MyHandler extends Handler {

        private VideoRemotePlayActivity activity;

        public MyHandler(VideoRemotePlayActivity activity) {
            this.activity = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            activity.handlerNotify.onHandler(msg.what, msg.arg1, msg.arg2, msg.obj);
            super.handleMessage(msg);
        }

    }
}
