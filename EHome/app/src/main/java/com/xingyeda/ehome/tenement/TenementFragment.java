package com.xingyeda.ehome.tenement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.ldl.okhttp.OkHttpUtils;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.adapter.LifeAdpater;
import com.xingyeda.ehome.base.EHomeApplication;
import com.xingyeda.ehome.base.LitePalUtil;
import com.xingyeda.ehome.bean.LifeBean;
import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.menu.SetActivity;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.MyLog;
import com.xingyeda.ehome.util.SharedPreUtil;
import com.xingyeda.ehome.view.LineGridView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author 李达龙
 * @ClassName: TenementFragment
 * @Description: 物业管理界面
 * @date 2016-7-6
 */
@SuppressLint("HandlerLeak")
public class TenementFragment extends Fragment {


    @Bind(R.id.share_more)
    ImageView shareMore;

    private Context mContext;
    private View rootView;
    private EHomeApplication mApplication;


    @Bind(R.id.life_gridview)
    LineGridView mGridView;
    @Bind(R.id.life_nodata)
    ImageView mNoData;
    private static final int CONTENT = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.tenement_fragment, container, false);
        }
        ButterKnife.bind(this, rootView);
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        MyLog.i("TenementFragment启动");
        this.mContext = this.getActivity();
        mApplication = (EHomeApplication) ((Activity) mContext).getApplication();


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mApplication.getmLife_List() != null) {
            mHandler.sendEmptyMessage(CONTENT);
        } else {
            mGridView.setVisibility(View.GONE);
            mNoData.setVisibility(View.VISIBLE);
        }
        if (shareMore!=null) {
            if (SharedPreUtil.getBoolean(mContext, "isAnnunciate")) {
                shareMore.setBackgroundResource(R.mipmap.add_white2);
            }else{
                shareMore.setBackgroundResource(R.mipmap.add_white);
            }
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONTENT:
                    setList();
                    break;
            }

        }

        ;
    };

    private void setList() {
        final List<LifeBean> list = mApplication.getmLife_List();
        if (list != null && !list.isEmpty()) {
            mGridView.setVisibility(View.VISIBLE);
            mNoData.setVisibility(View.GONE);
            LifeAdpater adpater = new LifeAdpater(mContext, list, mGridView.getMeasuredHeight() / 3);
            mGridView.setAdapter(adpater);
            adpater.notifyDataSetChanged();
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    LifeBean bean = list.get(position);
                    if (bean.getmType().equals("1")) {
                        Bundle bundle = new Bundle();
                        bundle.putString("url", bean.getmContent());
                        bundle.putString("type", bean.getmName());
                        BaseUtils.startActivities(mContext,
                                AdvertisementActivity.class, bundle);
                    } else if (bean.getmType().equals("2")) {
                        Uri uri = Uri.parse("tel:" + bean.getmContent());
                        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                        startActivity(intent);
                    } else {
                        DialogShow.showHintDialog(mContext, "暂无数据，敬请期待");
                    }
                }
            });
        } else {
            mGridView.setVisibility(View.GONE);
            mNoData.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.life_convenience, R.id.share_more})
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.life_convenience:
                if (LitePalUtil.getHomeBean() != null) {
                    BaseUtils.startActivity(mContext, CallCenterActivity.class);
                } else {

                }
                break;
            case R.id.share_more:
                bundle.putString("type", "suggest");
                BaseUtils.startActivities(mContext, SetActivity.class, bundle);
                break;
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OkHttpUtils.getInstance().cancelTag(this);
        ButterKnife.unbind(getActivity());
        MyLog.i("TenementFragment销毁");
    }

}
