package com.jovision.account;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.jovision.AppConsts;
import com.jovision.JniUtil;
import com.jovision.base.IHandlerLikeNotify;
import com.jovision.base.IHandlerNotify;
import com.jovision.play.RemoteVideo;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.EHomeApplication;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.MyLog;
import com.xingyeda.ehome.util.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoRewindActivity extends BaseActivity implements IHandlerNotify, IHandlerLikeNotify {

    protected MyHandler handler = new MyHandler(this);
    @Bind(R.id.rewind_swipeLayout)
    SwipeRefreshLayout mSwipeLayout;
    private IHandlerNotify handlerNotify = this;
    @Bind(R.id.rewind_today)
    TextView today;
    @Bind(R.id.rewind_three_days)
    TextView threeDays;
    @Bind(R.id.rewind_select)
    TextView select;

    @Bind(R.id.rewind_title)
    TextView rewindTitle;
    @Bind(R.id.rewind_recycler_view)
    RecyclerView rewindRecyclerView;
    @Bind(R.id.rewind_no_data)
    ImageView rewindNoData;

    private String date = "";
    private Calendar rightNow = Calendar.getInstance();
    private int mYear;
    private int mMonth;
    private int mDay;

    private int deviceType;// 设备类型
    private int indexOfChannel;// 通道index
    private int channelOfChannel;// 通道号
    private boolean isJFH;// 是否带帧头

    private ArrayList<RemoteVideo> videoList;
    private VideoRemoteAdapter adapter;
    private ArrayList<RemoteVideo> List;
    private boolean isClear = false;
    private int frequency = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_rewind);
        ButterKnife.bind(this);
        ((EHomeApplication) getApplication()).setCurrentNotifyer(this);
        deviceType = getIntent().getExtras().getInt("DeviceType", 0);
        indexOfChannel = getIntent().getExtras().getInt("IndexOfChannel", 0);
        channelOfChannel = getIntent().getExtras().getInt("ChannelOfChannel", 0);
        isJFH = getIntent().getExtras().getBoolean("isJFH", false);

        rewindRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rewindRecyclerView.addItemDecoration(new SpaceItemDecoration(20));

        select.setOnTouchListener(onTouchListener);


        mSwipeLayout.setColorSchemeResources(R.color.theme_orange);
        mSwipeLayout.post(new Runnable() {
            @Override
            public void run() {
                mYear = rightNow.get(Calendar.YEAR);
                mMonth = rightNow.get(Calendar.MONTH) + 1;
                mDay = rightNow.get(Calendar.DAY_OF_MONTH);

                date = String.format(AppConsts.REMOTE_SEARCH_FORMATTER, mYear,
                        mMonth, mDay, mYear, mMonth, mDay);


                JniUtil.checkRemoteData(indexOfChannel, date);
                    mSwipeLayout.setRefreshing(true);
            }
        });

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                date = String.format(AppConsts.REMOTE_SEARCH_FORMATTER, mYear,
                        mMonth, mDay, mYear, mMonth, mDay);
                JniUtil.checkRemoteData(indexOfChannel, date);
            }
        });

//        mYear = rightNow.get(Calendar.YEAR);
//        mMonth = rightNow.get(Calendar.MONTH) + 1;
//        mDay = rightNow.get(Calendar.DAY_OF_MONTH);
//
//        date = String.format(AppConsts.REMOTE_SEARCH_FORMATTER, mYear,
//                mMonth, mDay, mYear, mMonth, mDay);
//
//
//        JniUtil.checkRemoteData(indexOfChannel, date);

    }

    @OnClick({R.id.rewind_back, R.id.rewind_today, R.id.rewind_three_days, R.id.rewind_select})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rewind_back:
                finish();
                break;
            case R.id.rewind_today:
                today.setTextColor(getResources().getColor(R.color.white));
                today.setBackgroundResource(R.color.theme_orange);
                threeDays.setTextColor(getResources().getColor(R.color.theme_orange));
                threeDays.setBackgroundResource(R.color.playback);
                select.setTextColor(getResources().getColor(R.color.theme_orange));
                select.setBackgroundResource(R.color.playback);
                mYear = rightNow.get(Calendar.YEAR);
                mMonth = rightNow.get(Calendar.MONTH) + 1;
                mDay = rightNow.get(Calendar.DAY_OF_MONTH);
                isClear = true;
                date = String.format(AppConsts.REMOTE_SEARCH_FORMATTER, mYear, mMonth, mDay, mYear, mMonth, mDay);
                JniUtil.checkRemoteData(indexOfChannel, date);
                break;
            case R.id.rewind_three_days:
                mSwipeLayout.setRefreshing(true);
                rewindRecyclerView.setVisibility(View.VISIBLE);
                rewindNoData.setVisibility(View.GONE);
                threeDays.setTextColor(getResources().getColor(R.color.white));
                threeDays.setBackgroundResource(R.color.theme_orange);
                today.setTextColor(getResources().getColor(R.color.theme_orange));
                today.setBackgroundResource(R.color.playback);
                select.setTextColor(getResources().getColor(R.color.theme_orange));
                select.setBackgroundResource(R.color.playback);
                mYear = rightNow.get(Calendar.YEAR);
                mMonth = rightNow.get(Calendar.MONTH) + 1;
                mDay = rightNow.get(Calendar.DAY_OF_MONTH);
                for (int i = 0; i < 3; i++) {
                    isClear = true;
                    frequency++;
                    date = String.format(AppConsts.REMOTE_SEARCH_FORMATTER, mYear, mMonth, mDay - i, mYear, mMonth, mDay - i);
                    JniUtil.checkRemoteData(indexOfChannel, date);
                }
                break;
//            case R.id.rewind_select:
//                select.setTextColor(getResources().getColor(R.color.white));
//                select.setBackgroundResource(R.color.theme_orange);
//                today.setTextColor(getResources().getColor(R.color.theme_orange));
//                today.setBackgroundResource(R.color.playback);
//                threeDays.setTextColor(getResources().getColor(R.color.theme_orange));
//                threeDays.setBackgroundResource(R.color.playback);
//                break;
        }
    }

    @Override
    public void onHandler(int what, int arg1, int arg2, Object obj) {
        switch (what) {
            case AppConsts.CALL_CHECK_RESULT: {// 查询远程回放数据
                byte[] pBuffer = (byte[]) obj;
                videoList = JniUtil.getRemoteList(pBuffer, deviceType, channelOfChannel);
                Log.i("videoList", videoList + "");
                if (null != videoList && 0 != videoList.size()) {

                    if (frequency > 0) {
                        switch (frequency) {
                            case 3:
                                frequency--;
                                for (RemoteVideo remoteVideo : videoList) {
                                    remoteVideo.year = mYear;
                                    remoteVideo.month = mMonth;
                                    remoteVideo.day = mDay;
                                }
                                break;
                            case 2:
                                frequency--;
                                for (RemoteVideo remoteVideo : videoList) {
                                    remoteVideo.year = mYear;
                                    remoteVideo.month = mMonth;
                                    remoteVideo.day = mDay - 1;
                                }
                                break;
                            case 1:
                                frequency--;
                                for (RemoteVideo remoteVideo : videoList) {
                                    remoteVideo.year = mYear;
                                    remoteVideo.month = mMonth;
                                    remoteVideo.day = mDay - 2;
                                }
                                break;
                        }
                    } else {
                        for (RemoteVideo remoteVideo : videoList) {
                            remoteVideo.year = mYear;
                            remoteVideo.month = mMonth;
                            remoteVideo.day = mDay;
                        }
                    }
                    if (null != List && 0 != List.size()) {
                        if (isClear) {
                            List.clear();
                            isClear = false;
                        }
                        for (RemoteVideo remoteVideo : videoList) {
//                            if (!remoteVideo.remoteKind.equals("N")) {
                            List.add(remoteVideo);
//                            }
                        }
                    } else {
//                        List = new ArrayList<>();
//                        for (RemoteVideo remoteVideo : videoList) {
//                            if (!remoteVideo.remoteKind.equals("N")) {
//                                List.add(remoteVideo);
//                            }
//                        }
                        List = videoList;
                    }
                    rewindRecyclerView.setVisibility(View.VISIBLE);
                    rewindNoData.setVisibility(View.GONE);
                    if (mSwipeLayout!=null) {
                        mSwipeLayout.setRefreshing(false);
                    }
                    adapter = new VideoRemoteAdapter(mContext, List);
                    rewindRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    adapter.longItem(new VideoRemoteAdapter.ItemClick() {
                        @Override
                        public void itemClick(View view, int position) {
                            RemoteVideo videoBean = List.get(position);
                            String acBuffStr = JniUtil.getPlayFileString(videoBean, isJFH, deviceType, videoBean.year, videoBean.month, videoBean.day, position);
//                            Log.i("itemClick",+deviceType + videoBean.year + videoBean.month + videoBean.day + position+"");
                            String BuffStr = acBuffStr.replace("-67", "00");
                            String Str1 = BuffStr.substring(0, BuffStr.lastIndexOf("/") + 1);
                            String Str2 = BuffStr.substring(BuffStr.lastIndexOf("/") + 2);


                            if (null != acBuffStr && !"".equalsIgnoreCase(acBuffStr)) {
                                Bundle bundle = new Bundle();
                                bundle.putInt("IndexOfChannel", indexOfChannel);
//                                bundle.putString("acBuffStr", Str1+"M"+Str2);
                                bundle.putString("acBuffStr", acBuffStr);
                                bundle.putString("remoteKind", videoBean.remoteKind);
                                MyLog.i("回放视频的参数" + bundle.toString());
                                BaseUtils.startActivities(mContext, VideoRemotePlayActivity.class, bundle);
                            }

                        }
                    });
                } else {
//                    adapter = new VideoRemoteAdapter(mContext, videoList);
//                    rewindRecyclerView.setAdapter(adapter);
//                    adapter.notifyDataSetChanged();
                    rewindRecyclerView.setVisibility(View.GONE);
                    rewindNoData.setVisibility(View.VISIBLE);
                }
                break;
            }
        }

    }

    /**
     * 日历轻触事件
     */
    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View arg0, MotionEvent arg1) {
            if (MotionEvent.ACTION_DOWN == arg1.getAction()) {
                new DatePickerDialog(VideoRewindActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            public void onDateSet(DatePicker arg0, int y,
                                                  int m, int d) {

                                select.setTextColor(getResources().getColor(R.color.white));
                                select.setBackgroundResource(R.color.theme_orange);
                                today.setTextColor(getResources().getColor(R.color.theme_orange));
                                today.setBackgroundResource(R.color.playback);
                                threeDays.setTextColor(getResources().getColor(R.color.theme_orange));
                                threeDays.setBackgroundResource(R.color.playback);
                                select.setText(y + "-" + (++m) + "-" + d);
                                mYear = y;
                                mMonth = m++;
                                mDay = d;
                                isClear = true;
                                date = String.format(AppConsts.REMOTE_SEARCH_FORMATTER, mYear, mMonth, mDay, mYear, mMonth, mDay);
                                mSwipeLayout.setRefreshing(true);
                                rewindRecyclerView.setVisibility(View.VISIBLE);
                                rewindNoData.setVisibility(View.GONE);
                                JniUtil.checkRemoteData(indexOfChannel, date);
                            }
                        }, rightNow.get(Calendar.YEAR),
                        rightNow.get(Calendar.MONTH),
                        rightNow.get(Calendar.DAY_OF_MONTH)).show();
            }
            return true;
        }

    };

    @Override
    public void onNotify(int what, int arg1, int arg2, Object obj) {
        handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));

    }

    protected class MyHandler extends Handler {

        private VideoRewindActivity activity;

        public MyHandler(VideoRewindActivity activity) {
            this.activity = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            activity.handlerNotify.onHandler(msg.what, msg.arg1, msg.arg2, msg.obj);
            super.handleMessage(msg);
        }

    }

}
