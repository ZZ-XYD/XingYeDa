package com.xingyeda.ehome.tenement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.xingyeda.ehome.R;
import com.xingyeda.ehome.adapter.AnnunciateAdapter;
import com.xingyeda.ehome.adapter.ComplainAdapter;
import com.xingyeda.ehome.adapter.MaintainAdapter;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.bean.AnnunciateBean;
import com.xingyeda.ehome.bean.BeanComplainHistory;
import com.xingyeda.ehome.bean.BeanMaintainHistory;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.view.CustomListView;
import com.xingyeda.ehome.view.CustomListView.OnLoadMoreListener;
import com.xingyeda.ehome.view.CustomListView.OnRefreshListener;
/**
 * @ClassName: ActivityHistory
 * @Description: 建议和保修历史界面
 * @author 李达龙
 * @date 2016-7-6
 */
@SuppressLint("HandlerLeak")
public class ActivityHistory extends BaseActivity
{
   @Bind(R.id.hisory_title)
     TextView mTitle;
   @Bind(R.id.history_listview)
     CustomListView mHisListview;
   @Bind(R.id.history_back)
     TextView mBack;
   @Bind(R.id.history_hint)
   ImageView mHint;
    private static String TYPE;
    private String mPath;
    private List<BeanComplainHistory> mComplainList;
    private List<BeanMaintainHistory> mMaintainList;
    private List<AnnunciateBean> mAnnunciateList;
    private ComplainAdapter mComplainAdapter;
    private AnnunciateAdapter mAnnunciateAdapter;
    private MaintainAdapter mMaintainAdapter;

    private static final int TOUSU = 1;
    private static final int WEIXIU = 2;
    private static final int AC_DATA = 3;
    private static final int LOAD_DATA_FINISH = 10;
    private static final int REFRESH_DATA_FINISH = 11;
    private int mPageIndex = 1;
    private int mSum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_history);
        ButterKnife.bind(this);
        this.init();

    }

    @SuppressWarnings("static-access")
    private void init()
    {
        this.TYPE = getIntent().getExtras().getString("type");
        this.mComplainList = new ArrayList<>();
        this.mMaintainList = new ArrayList<>();
        this.mAnnunciateList = new ArrayList<>();
        if (TYPE.equals("tousu"))
        {
            mTitle.setText(R.string.suggested_history);
            this.mPath = ConnectPath.GETCOMPLAIN_PATH;
            complainDataLoad(TYPE, mPath, "0", "15", 0);
        }
        else if (TYPE.equals("weixiutype"))
        {
            mTitle.setText(R.string.maintain_history);
            this.mPath = ConnectPath.GETSERVICE_PATH;
            maintainDataLoad(TYPE, mPath, "0", "15", 0);
        }
        else if (TYPE.equals("annunciate"))
        {
            mTitle.setText(R.string.notification);
            this.mPath = ConnectPath.ANNUNCIATE_PATH;
            annunciateDataLoad(TYPE, mPath, "0", "15", 0);

        }

    }

    /**
     * 通知
     * @param type
     * @param path
     * @param pageIndex
     * @param pageSize
     * @param time
     */
    private void annunciateDataLoad(String type, String path, String pageIndex,
            String pageSize, final int time)
    {
    	Map<String,String> params = new HashMap<String, String>();
        params.put("uid", mEhomeApplication.getmCurrentUser().getmId());
        params.put("pageIndex", pageIndex);
        params.put("pageSize", pageSize);
        OkHttp.get(mContext,ConnectPath.ANNUNCIATE_PATH, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
			@Override
			public void onResponse(JSONObject response) {
				 try
                 {
                         JSONObject jsonObject = (JSONObject) response
                                 .get("obj");
                         JSONArray ad_List = (JSONArray) jsonObject
                                 .get("list");
                         if (ad_List != null && ad_List.length() != 0)
                         {
                             for (int i = 0; i < ad_List.length(); i++)
                             {
                                 JSONObject jobj = ad_List
                                         .getJSONObject(i);
                                 AnnunciateBean bean = new AnnunciateBean();
                                 bean.setmTitle(jobj.getString("title"));
                                 bean.setmContent(jobj
                                         .getString("content"));
                                 bean.setmTime(jobj
                                         .getString("createTime"));
                                 mAnnunciateList.add(bean);
                             }
                             if (ad_List.length()!=0)
                             {
                                 mSum+=15;
                             }
                         }
                         Message msg = new Message();
                         msg.obj = mAnnunciateList;
                         if (time == 0)
                         {
                             msg.what = AC_DATA;
                         }
                         else if (time == 1)
                         {
                             msg.what = REFRESH_DATA_FINISH;
                         }
                         else if (time == 2)
                         {
                             msg.what = LOAD_DATA_FINISH;
                         }
                         mHandler.sendMessage(msg);
                 }
                 catch (JSONException e)
                 {
                     e.printStackTrace();
                 }
			}
		}));
        
       
    }
        @OnClick(R.id.history_back)
        public void onClick(View v)
        {
                ActivityHistory.this.finish();
        }

    private Handler mHandler = new Handler()
    {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
            case TOUSU:
                complainDatasLoad((List<BeanComplainHistory>) msg.obj,0);
                break;
            case WEIXIU:
                maintainDatasLoad((List<BeanMaintainHistory>) msg.obj,0);
                break;
            case AC_DATA:
                AnnunciateDatasLoad((List<AnnunciateBean>) msg.obj,0);
                break;
            case REFRESH_DATA_FINISH:
                if (mComplainAdapter != null)
                {
                    complainDatasLoad((List<BeanComplainHistory>) msg.obj,0);
                }
                else if (mMaintainAdapter!=null)
                {
                    maintainDatasLoad((List<BeanMaintainHistory>) msg.obj,0);
                }
                else if (mAnnunciateAdapter!=null)
                {
                    AnnunciateDatasLoad((List<AnnunciateBean>) msg.obj,0);
                }
                mHisListview.onRefreshComplete(); // 下拉刷新完成
                break;
            case LOAD_DATA_FINISH:
                if (mComplainAdapter != null)
                {
                    complainDatasLoad((List<BeanComplainHistory>) msg.obj,1);
                }
                else if (mMaintainAdapter!=null)
                {
                    maintainDatasLoad((List<BeanMaintainHistory>) msg.obj,1);
                }
                else if (mAnnunciateAdapter!=null)
                {
                    AnnunciateDatasLoad((List<AnnunciateBean>) msg.obj,1);
                }
                mHisListview.onLoadMoreComplete(); // 加载更多完成
                break;
            }
        }

    };
    private void AnnunciateDatasLoad(List<AnnunciateBean> list,int type)
    {
        if (list != null && list.size() !=0)
        {
            mHisListview.setVisibility(View.VISIBLE);
            mHint.setVisibility(View.GONE);
        }
        else
        {
            mHisListview.setVisibility(View.GONE);
            mHint.setVisibility(View.VISIBLE);
        }
        
        if (type == 0) {
            mAnnunciateAdapter = new AnnunciateAdapter(ActivityHistory.this, list);
            mHisListview.setAdapter(mAnnunciateAdapter);
	}
        mAnnunciateAdapter.notifyDataSetChanged();
        mHisListview.setOnItemClickListener(itemClickListener);

        if (mAnnunciateList.size() !=0 && mAnnunciateList.size() >= mSum)
        {
            if (list.size()>14)
            {
        // 加载更多
        mHisListview.setOnLoadListener(new OnLoadMoreListener()
        {

            @Override
            public void onLoadMore()
            {
                mPageIndex += 1;
                annunciateDataLoad(TYPE, mPath, mPageIndex + "", "15", 2);
            }
        });
            }
        }
        else {
            if (mAnnunciateList.size() !=0 && list.size()>14) {
        	mHisListview.setOnLoadMoreText();
        	mHisListview.removeFooteView();
	    }
        }
        // 下拉刷新
        mHisListview.setOnRefreshListener(new OnRefreshListener()
        {

            @Override
            public void onRefresh()
            {
                int size = mAnnunciateList.size();
                mAnnunciateList.clear();
                annunciateDataLoad(TYPE, mPath, "0", size + "", 1);
            }
        });
        
    }

    private void complainDatasLoad(List<BeanComplainHistory> list,int type)
    {
        if (list != null && list.size()!=0)
        {
            mHisListview.setVisibility(View.VISIBLE);
            mHint.setVisibility(View.GONE);
        }
        else
        {
            mHisListview.setVisibility(View.GONE);
            mHint.setVisibility(View.VISIBLE);
        }
        if (type == 0) {
            mComplainAdapter = new ComplainAdapter(this, list);
            mHisListview.setAdapter(mComplainAdapter);
	}
        mComplainAdapter.notifyDataSetChanged();

        mHisListview.setOnItemClickListener(itemClickListener);
        if (mComplainList.size()!=0 && mComplainList.size() >= mSum)
        {
            if (list.size()>14)
            {
        // 加载更多
        mHisListview.setOnLoadListener(new OnLoadMoreListener()
        {

            @Override
            public void onLoadMore()
            {
                mPageIndex += 1;
                complainDataLoad(TYPE, mPath, mPageIndex + "", "10", 2);
            }
        });
            }
        }
        else {
            if (mComplainList.size()!=0 && list.size()>14)
            {
        	mHisListview.setOnLoadMoreText();
        	mHisListview.removeFooteView();
            }
        }
        // 下拉刷新
        mHisListview.setOnRefreshListener(new OnRefreshListener()
        {

            @Override
            public void onRefresh()
            {
                int size = mComplainList.size();
                mComplainList.clear();
                complainDataLoad(TYPE, mPath, "0", size + "", 1);
            }
        });
    }

    private void maintainDatasLoad(List<BeanMaintainHistory> list,int type)
    {
        if (list != null && list.size()!=0)
        {
            mHisListview.setVisibility(View.VISIBLE);
            mHint.setVisibility(View.GONE);
        }
        else
        {
            mHisListview.setVisibility(View.GONE);
            mHint.setVisibility(View.VISIBLE);
        }
        if (type == 0) {
            mMaintainAdapter= new MaintainAdapter(this, list);
            mHisListview.setAdapter(mMaintainAdapter);
	}
        mMaintainAdapter.notifyDataSetChanged();

        mHisListview.setOnItemClickListener(itemClickListener);
        if (mMaintainList.size() !=0 &&mMaintainList.size() >= mSum)
        {
            if (list.size()>14)
            {
                
        // 加载更多
        mHisListview.setOnLoadListener(new OnLoadMoreListener()
        {

            @Override
            public void onLoadMore()
            {
                mPageIndex += 1;
                maintainDataLoad(TYPE, mPath, mPageIndex + "", "15", 2);
            }
        });
            }
        }
        else {
            if (mMaintainList.size() !=0 && list.size()>14)
            {
        	mHisListview.setOnLoadMoreText();
                mHisListview.removeFooteView();
            }
        }
        
        // 下拉刷新
        mHisListview.setOnRefreshListener(new OnRefreshListener()
        {

            @Override
            public void onRefresh()
            {
                int size = mMaintainList.size();
                mMaintainList.clear();
                maintainDataLoad(TYPE, mPath, "0", size + "", 1);
            }
        });
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener()
    {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id)
        {
            Bundle bundle = new Bundle();
            if (TYPE.equals("tousu"))
            {
                BeanComplainHistory bean = (BeanComplainHistory) mHisListview
                        .getItemAtPosition(position);
                mEhomeApplication.setmComplaiBean(bean);
                bundle.putString("bean", "tousu");
                bundle.putString("title", bean.getmTitle());
                bundle.putString("content", bean.getmContent());
                bundle.putString("time", bean.getmTime());
                bundle.putStringArrayList("imageList", bean.getmImageList());

            }
            else if (TYPE.equals("weixiutype"))
            {
                BeanMaintainHistory bean = (BeanMaintainHistory) mHisListview
                        .getItemAtPosition(position);
                mEhomeApplication.setmMaintainBean(bean);
                bundle.putString("bean", "weixiutype");
                bundle.putString("title", bean.getmTitle());
                bundle.putString("content", bean.getmContent());
                bundle.putString("time", bean.getmTime());
                bundle.putStringArrayList("imageList", bean.getmImageList());
            }
            else if (TYPE.equals("annunciate"))
            {
                AnnunciateBean bean = (AnnunciateBean) mHisListview
                        .getItemAtPosition(position);
                bundle.putString("title", bean.getmTitle());
                bundle.putString("content", bean.getmContent());
                bundle.putString("time", bean.getmTime());
                bundle.putString("bean", "annunciate");
                bundle.putStringArrayList("imageList", null);
            }
            BaseUtils.startActivities(ActivityHistory.this,Notice_Activity.class, bundle);
        }

    };

    @Override
    protected void onResume()
    {

        super.onResume();
    }

    // 投诉历史
    private void complainDataLoad(final String type, String path,
            String pageIndex, String pageSize, final int time)
    {
    	Map<String,String> params = new HashMap<String, String>();
        params.put("pageIndex", pageIndex);
        params.put("pageSize", pageSize);
        params.put("uid", mEhomeApplication.getmCurrentUser().getmId());
        OkHttp.get(mContext,path, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
			@Override
			public void onResponse(JSONObject response) {
				 try
                 {
                         JSONArray obj = (JSONArray) response.get("obj");
                         if (obj != null && obj.length() != 0)
                         {
                             for (int i = 0; i < obj.length(); i++)
                             {
                                 BeanComplainHistory bean = new BeanComplainHistory();
                                 JSONObject jobj = obj.getJSONObject(i);
                                 bean.setmTitle(jobj.getString("title"));
                                 bean.setmTime(jobj.getString("time"));
                                 bean.setmContent(jobj
                                         .getString("content"));
                                 bean.setmType(type);
                                 if (jobj.has("fileList")) {
                                 	JSONArray list = (JSONArray) jobj.get("fileList");
                                 	if (list != null && list.length() != 0)
                                     {
                                 		List<String> paths = new ArrayList<String>();
                                 		for (int j = 0; j < list.length(); j++) {
                                 			JSONObject listobj = list.getJSONObject(j);
                                 			 paths.add(listobj.has("path")?listobj.getString("path"):"");
											}
                                 		 bean.setmImageList(paths);
                                     }
				    
                                 }
                                 mComplainList.add(bean);
                             }
                             mSum+=obj.length();
                         }
                         Message msg = new Message();
                         if (time == 0)
                         {
                             msg.what = TOUSU;
                         }
                         else if (time == 1)
                         {
                             msg.what = REFRESH_DATA_FINISH;
                         }
                         else if (time == 2)
                         {
                             msg.what = LOAD_DATA_FINISH;
                         }
                         msg.obj = mComplainList;
                         mHandler.sendMessage(msg);

                 }
                 catch (JSONException e)
                 {
                     e.printStackTrace();
                 }
			}
		}));
    }

    // 维修历史
    private void maintainDataLoad(final String type, String path,
            String pageIndex, String pageSize, final int time)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", mEhomeApplication.getmCurrentUser().getmId());
        params.put("pageIndex", pageIndex);
        params.put("pageSize", pageSize);
        OkHttp.get(mContext,path, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
			@Override
			public void onResponse(JSONObject response) {
				try
                {
                        BeanMaintainHistory bean;
                        JSONArray obj = (JSONArray) response.get("obj");
                        if (obj != null && obj.length() != 0)
                        {
                            for (int i = 0; i < obj.length(); i++)
                            {
                                bean = new BeanMaintainHistory();
                                JSONObject cObjct = (JSONObject) obj
                                        .get(i);
                                JSONObject jobj = (JSONObject) cObjct
                                        .get("report");

                                bean.setmTitle(jobj.getString("title"));
                                bean.setmTime(jobj.getString("time"));
                                bean.setmContent(jobj
                                        .getString("content"));
                                bean.setmType(type);
                                if (jobj.has("fileList")) {
                                	JSONArray list = (JSONArray) jobj.get("fileList");
                                	if (list != null && list.length() != 0)
                                    {
                                		List<String> paths = new ArrayList<String>();
                                		for (int j = 0; j < list.length(); j++) {
                                			JSONObject listobj = list.getJSONObject(j);
                                			 paths.add(listobj.has("path")?listobj.getString("path"):"");
										}
                                		 bean.setmImageList(paths);
                                    }
                                	
                                	
                                }

                                mMaintainList.add(bean);
                            }
                            mSum+=obj.length();
                        }
                        Message msg = new Message();
                        if (time == 0)
                        {
                            msg.what = WEIXIU;
                        }
                        else if (time == 1)
                        {
                            msg.what = REFRESH_DATA_FINISH;
                        }
                        else if (time == 2)
                        {
                            msg.what = LOAD_DATA_FINISH;
                        }
                        msg.obj = mMaintainList;
                        mHandler.sendMessage(msg);

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
			}
		}));
   }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}
