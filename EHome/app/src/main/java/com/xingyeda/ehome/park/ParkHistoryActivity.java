package com.xingyeda.ehome.park;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jovision.account.JVInformationActivity;
import com.jovision.account.JVPlayInformationActivity;
import com.xingyeda.ehome.ActivityHomepage;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.adapter.InformationAdapter;
import com.xingyeda.ehome.adapter.ParkAdapter;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.bean.InformationBase;
import com.xingyeda.ehome.bean.ParkBean;
import com.xingyeda.ehome.information.InformationActivity;
import com.xingyeda.ehome.information.PersonalActivity;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.SharedPreUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.xingyeda.ehome.base.BaseActivity.mEhomeApplication;

public class ParkHistoryActivity extends BaseActivity {

    @Bind(R.id.park_recyclerview)
    RecyclerView mRecyclerview;
    @Bind(R.id.park_swipereLayout)
    SwipeRefreshLayout mSwipeLayout;
    @Bind(R.id.park_no_datas)
    ImageView mNoDatas;

    private List<ParkBean> mList;

    private ParkAdapter mAdapter;
    private boolean mIsRefresh;
    private boolean mLoad = false;
    private static boolean isShow;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_history);
        ButterKnife.bind(this);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        mIsRefresh = true;
        isShow = false;
        init();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void init() {
        mList = DataSupport.where("mUserId = ?", SharedPreUtil.getString(mContext, "userId", "")).order("mTime desc").find(ParkBean.class);
        addAdapter(mList, 0);
        mLoad = true;
        mSwipeLayout.setOnRefreshListener(listener);
        mSwipeLayout.setColorSchemeColors(getResources().getColor(R.color.theme_orange));

    }


    @OnClick(R.id.park_back)
    public void onViewClicked() {
        BaseUtils.startActivity(this, ActivityHomepage.class);
        ParkHistoryActivity.this.finish();
    }

    private SwipeRefreshLayout.OnRefreshListener listener = new SwipeRefreshLayout.OnRefreshListener() {

        @Override
        public void onRefresh() {
            if (mIsRefresh) {
                mIsRefresh = false;
                mList = DataSupport.where("mUserId = ?", SharedPreUtil.getString(mContext, "userId", "")).order("mTime desc").find(ParkBean.class);
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

    private void addAdapter(List<ParkBean> list, int type) {
        if (type == 0) {
            mAdapter = new ParkAdapter(mContext, list);
            mRecyclerview.setAdapter(mAdapter);
        }
        mAdapter.notifyDataSetChanged();
        if (mAdapter != null) {
            mAdapter.delete(new ParkAdapter.Delete() {
                @Override
                public void onclick(View view, int position) {
                    DataSupport.deleteAll(ParkBean.class, "mTime = ?", mList.get(position).getmTime());
                    mList.remove(position);
                    mList = DataSupport.where("mUserId = ?", SharedPreUtil.getString(mContext, "userId", "")).order("mTime desc").find(ParkBean.class);
                    mAdapter.notifyDataSetChanged();
                    addAdapter(mList, 0);
                    if (mList == null || mList.isEmpty() || mList.size() == 0) {
                        mNoDatas.setVisibility(View.VISIBLE);
                    } else {
                        mNoDatas.setVisibility(View.GONE);
                    }
                }
            });
            mAdapter.clickItem(new ParkAdapter.ClickItem() {
                @Override
                public void onclick(View view, int position) {
                    if (isShow) {
                        ParkBean item = mList.get(position);
                        boolean isChecked = item.isChecked();
                        if (isChecked) {
                            item.setChecked(false);
                        } else {
                            item.setChecked(true);
                        }
                        mAdapter.notifyDataSetChanged();
                    } else {
                        ParkBean bean = mList.get(position);

                        ParkBean parkBean = new ParkBean();
                        parkBean.setmIsExamine(1);
                        parkBean.updateAll("mTime = ?", bean.getmTime());

                        Bundle bundle = new Bundle();
                        bundle.putString("title", bean.getmTitle());
                        bundle.putString("time", bean.getmTime());
                        bundle.putString("content", bean.getmContent());
                        bundle.putString("image", bean.getmPicture());
                        BaseUtils.startActivities(mContext, ParkMessageActivity.class, bundle);
                    }
                }
            });
        }
    }

    public void onResume() {
        super.onResume();
        if (mLoad) {
            List<ParkBean> list = DataSupport.where("mUserId = ?", SharedPreUtil.getString(mContext, "userId", "")).order("mTime desc").find(ParkBean.class);
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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("ParkHistory Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
