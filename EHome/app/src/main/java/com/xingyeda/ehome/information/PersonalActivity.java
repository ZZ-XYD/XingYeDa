package com.xingyeda.ehome.information;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.jovision.account.JVInformationActivity;
import com.jovision.account.JVPlayInformationActivity;
import com.xingyeda.ehome.ActivityHomepage;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.adapter.InformationAdapter;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.bean.InformationBase;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.SharedPreUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("InlinedApi")
public class PersonalActivity extends BaseActivity {


    @Bind(R.id.community_swipereLayout)
    SwipeRefreshLayout mSwipeLayout;
    @Bind(R.id.message_no_datas)
    ImageView mNoDatas;
    @Bind(R.id.datas_recyclerview)
    RecyclerView datasRecyclerview;

    private List<InformationBase> mList;

    private InformationAdapter mAdapter;
    private boolean mIsRefresh;
    private boolean mLoad = false;
    private static boolean isShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        ButterKnife.bind(this);
        datasRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        mIsRefresh = true;
        isShow = false;
        init();


    }

    private void init() {
        mList = DataSupport.where("mUserId = ?", SharedPreUtil.getString(mContext, "userId", "")).order("mTime desc").find(InformationBase.class);
        addAdapter(mList, 0);
        mLoad = true;
        mSwipeLayout.setOnRefreshListener(listener);
        mSwipeLayout.setColorSchemeColors(getResources().getColor(R.color.theme_orange));

    }

    @OnClick({R.id.personal_back})
    public void clickListener(View view) {
        switch (view.getId()) {
            case R.id.personal_back:
                BaseUtils.startActivity(this, ActivityHomepage.class);
                PersonalActivity.this.finish();
                break;
        }
    }

    private SwipeRefreshLayout.OnRefreshListener listener = new SwipeRefreshLayout.OnRefreshListener() {

        @Override
        public void onRefresh() {
            if (mIsRefresh) {
                mIsRefresh = false;
                mList = DataSupport.where("mUserId = ?", SharedPreUtil.getString(mContext, "userId", "")).order("mTime desc").find(InformationBase.class);
                addAdapter(mList, 0);
                if (mList == null || mList.isEmpty() || mList.size() == 0) {
                    mNoDatas.setVisibility(View.VISIBLE);
                } else {
                    mNoDatas.setVisibility(View.GONE);
                }
                mSwipeLayout.setRefreshing(false);
                mIsRefresh = true;
            }
        }
    };

    private void addAdapter(List<InformationBase> list, int type) {
        if (type == 0) {
            mAdapter = new InformationAdapter(mContext, list);
            datasRecyclerview.setAdapter(mAdapter);
        }
        mAdapter.notifyDataSetChanged();
        if (mAdapter != null) {
            mAdapter.delete(new InformationAdapter.Delete() {
                @Override
                public void onclick(View view, int position) {
                    DataSupport.deleteAll(InformationBase.class, "mTime = ?", mList.get(position).getmTime());
                    mList.remove(position);
                    mList = DataSupport.where("mUserId = ?", SharedPreUtil.getString(mContext, "userId", "")).order("mTime desc").find(InformationBase.class);
                    mAdapter.notifyDataSetChanged();
                    addAdapter(mList, 0);
                    if (mList == null || mList.isEmpty() || mList.size() == 0) {
                        mNoDatas.setVisibility(View.VISIBLE);
                    } else {
                        mNoDatas.setVisibility(View.GONE);
                    }
                }
            });
            mAdapter.clickItem(new InformationAdapter.ClickItem() {
                @Override
                public void onclick(View view, int position) {
                    if (isShow) {
                        InformationBase item = mList.get(position);
                        boolean isChecked = item.isChecked();
                        if (isChecked) {
                            item.setChecked(false);
                        } else {
                            item.setChecked(true);
                        }
                        mAdapter.notifyDataSetChanged();
                    } else {
                        InformationBase bean = mList.get(position);

                        InformationBase informationBase = new InformationBase();
                        informationBase.setmIsExamine(1);
                        informationBase.updateAll("mTime = ?", bean.getmTime());

                        Bundle bundle = new Bundle();
                        if (bean.getmZhongWeiType() == null) {
                            bundle.putString("type", "individual");
                            bundle.putString("title", bean.getmTitle());
                            bundle.putString("time", bean.getmTime());
                            bundle.putString("content", bean.getmContent());
                            bundle.putString("image", bean.getmImage());
                            bundle.putString("message", bean.getmMessage_status() + "");
                            bundle.putString("door", bean.getmDoor_status() + "");
                            bundle.putString("initiator", bean.getmName());
                            bundle.putString("receiver", bean.getmReceiver());
                            BaseUtils.startActivities(mContext, InformationActivity.class,
                                    bundle);
                        } else if (bean.getmZhongWeiType().equals("2")) {
                            bundle.putString("id", bean.getmZhongWeiId());
                            bundle.putString("title", bean.getmTitle());
                            bundle.putString("time", bean.getmTime());
                            bundle.putString("image", bean.getmImage());
                            bundle.putString("imageType", bean.getImageType() + "");
                            bundle.putString("imageSite", bean.getmZhongWeiImage());
                            BaseUtils.startActivities(mContext, JVPlayInformationActivity.class, bundle);
                        } else {
                            bundle.putString("id", bean.getmZhongWeiId());
                            bundle.putString("title", bean.getmTitle());
                            bundle.putString("time", bean.getmTime());
                            bundle.putString("image", bean.getmImage());
                            bundle.putString("imageType", bean.getImageType() + "");
                            bundle.putString("imageSite", bean.getmZhongWeiImage());
                            BaseUtils.startActivities(mContext, JVInformationActivity.class, bundle);
                        }
                    }
                }
            });
        }
    }

    public void onResume() {
        super.onResume();
        if (mLoad) {
            List<InformationBase> list = DataSupport.where("mUserId = ?", SharedPreUtil.getString(mContext, "userId", "")).order("mTime desc").find(InformationBase.class);
            mList.clear();
            mList.addAll(list);
            addAdapter(mList, 1);
        }
        if (mList == null || mList.isEmpty() || mList.size() == 0) {
            mNoDatas.setVisibility(View.VISIBLE);
        } else {
            mNoDatas.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
