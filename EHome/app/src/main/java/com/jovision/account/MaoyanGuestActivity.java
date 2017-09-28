package com.jovision.account;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.SharedPreUtil;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MaoyanGuestActivity extends BaseActivity {

    private String id ;
    private String time ;
    private MediaPlayer mMediaPlayer;
    private Vibrator mVibrator;// 震动

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maoyan_guest);
        ButterKnife.bind(this);
        id = getIntent().getStringExtra("id");
        time = getIntent().getStringExtra("time");
        voiceAndShake();
    }

    @OnClick({R.id.maoyan_cancel, R.id.maoyan_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.maoyan_cancel:
                finish();
                break;
            case R.id.maoyan_ok:
                Bundle bundle = new Bundle();
                bundle.putString("cameraId",id);
                bundle.putString("type","guest");
                BaseUtils.startActivities(mContext,JVMaoYanActivity.class,bundle);
                finish();
                break;
        }
    }
    private void voiceAndShake() {
        if (SharedPreUtil.getBoolean(mContext,"maoyan_vocality")) {

            // 开始播放音乐
            if (mMediaPlayer != null) {
                mMediaPlayer.start();
            }
            mMediaPlayer = android.media.MediaPlayer.create(mContext, R.raw.ring);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        }

        if (SharedPreUtil.getBoolean(mContext,"maoyan_shake")) {

            // 震动
            mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {1000, 1000, 1000, 1000};
            mVibrator.vibrate(pattern, 2);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mVibrator != null) {
            mVibrator.cancel();
        }
    }
}
