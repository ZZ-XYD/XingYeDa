package com.xingyeda.ehome.zhibo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldl.dialogshow.dialog.listener.OnBtnClickL;
import com.ldl.dialogshow.dialog.widget.NormalDialog;
import com.xingyeda.ehome.ActivityLogin;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.adapter.InformationAdapter;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.bean.InformationBase;
import com.xingyeda.ehome.bean.SeekHistoryBase;
import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.view.PercentLinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.id.list;
import static org.litepal.crud.DataSupport.order;

public class ActivityShareSeek extends BaseActivity {

    @Bind(R.id.share_seek_import)
    EditText shareSeekImport;
    @Bind(R.id.share_seek_no_datas)
    ImageView shareSeekNoDatas;
    @Bind(R.id.share_seek_recyclerview)
    RecyclerView shareSeekRecyclerview;
    @Bind(R.id.share_seek_swipereLayout)
    SwipeRefreshLayout shareSeekSwipereLayout;
    @Bind(R.id.share_seek_layout)
    DrawerLayout shareSeekLayout;
    @Bind(R.id.share_seek_but)
    ImageView shareSeekBut;
    @Bind(R.id.seek_history_recyclerview)
    RecyclerView recyclerview;

    private List<Camera> cameraList = new ArrayList<>();
    private CameraAdapter adapter;
    // 记录最后可见条目
    private int lastVisible;
    private GridLayoutManager mLayoutManager;
    // 加载页数
    int addmoreTimes = 1;
    private String mContent;
    private InputMethodManager mInputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_seek);
        ButterKnife.bind(this);
        init();
        mLayoutManager = new GridLayoutManager(mContext, 2);
        shareSeekRecyclerview.setLayoutManager(mLayoutManager);
        shareSeekRecyclerview.setHasFixedSize(true);
        shareSeekRecyclerview.setItemAnimator(new DefaultItemAnimator());

        recyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        shareSeekImport.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

        shareSeekImport.setInputType(EditorInfo.TYPE_CLASS_TEXT);

        shareSeekImport.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId,KeyEvent event)  {
                if (actionId==EditorInfo.IME_ACTION_SEND ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER))
                    {
                        if (!TextUtils.isEmpty(shareSeekImport.getText())) {
                              mContent = shareSeekImport.getText().toString();
                              shareSeekImport.setText(mContent);
                              if (isRepetition(mContent)) {
                                  SeekHistoryBase base = new SeekHistoryBase(mContent,getDateToString(System.currentTimeMillis()));
                                  base.save();
                              }
                              getShareList("1", "10", shareSeekImport.getText().toString());
                              shareSeekImport.setFocusable(false);//失去焦点
                              if (mInputMethodManager.isActive()) {
                                  mInputMethodManager.hideSoftInputFromWindow(shareSeekImport.getWindowToken(), 0);// 隐藏输入法
                              }
                          }
                    return true;
                    }
                return false;
                }
            });
    }

    private void init() {
        shareSeekSwipereLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                addmoreTimes = 1;
                getShareList("1", "10", mContent);
            }
        });
        shareSeekRecyclerview.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 不在滑动和最后可见条目是脚布局时加载更多
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisible + 1 == adapter.getItemCount()) {
                    addmoreTimes++;
                    addShareList(addmoreTimes + "", "4", mContent);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisible = mLayoutManager.findLastVisibleItemPosition();
            }

        });

        shareSeekSwipereLayout.setColorSchemeResources(R.color.theme_orange);


        shareSeekImport.setOnFocusChangeListener(new View.
                OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    shareSeekBut.setVisibility(View.VISIBLE);
                    shareSeekLayout.setVisibility(View.GONE);
                    recyclerview.setVisibility(View.VISIBLE);
                    query();
                } else {
                    // 此处为失去焦点时的处理内容
//                    shareSeekBut.setVisibility(View.GONE);
                    recyclerview.setVisibility(View.GONE);
                    shareSeekLayout.setVisibility(View.VISIBLE);


                }
            }
        });

    }
    private List<SeekHistoryBase> allSeek;
    private void query(){
//        allSeek = DataSupport.where("mUserId = ?", mEhomeApplication.getmCurrentUser().getmId()).find(SeekHistoryBase.class);
//        allSeek = DataSupport.findAll(SeekHistoryBase.class);
        allSeek = DataSupport.order("mTime desc").find(SeekHistoryBase.class);
        SeekAdapter adapter = new SeekAdapter(allSeek);
        recyclerview.setAdapter(adapter);
        adapter.clickItem(new SeekAdapter.ClickItem() {
            @Override
            public void onclick(View view, int position) {
                getShareList("1", "10", allSeek.get(position).getName());
                SeekHistoryBase Base = new SeekHistoryBase();
                Base.setmTime(getDateToString(System.currentTimeMillis()));
                Base.updateAll("name = ?", allSeek.get(position).getName());
                shareSeekImport.setText(allSeek.get(position).getName());
                shareSeekImport.setFocusable(false);//失去焦点
                if (mInputMethodManager.isActive()) {
                    mInputMethodManager.hideSoftInputFromWindow(shareSeekImport.getWindowToken(), 0);// 隐藏输入法
                }
            }
        });
        adapter.delete(new SeekAdapter.Delete() {
            @Override
            public void onclick() {
                final NormalDialog dialog = DialogShow.showSelectDialog(mContext,"是否清除历史数据",2,new String[] { getResources().getString(R.string.cancel),getResources().getString(R.string.confirm)});
                dialog.setOnBtnClickL(new OnBtnClickL() {

                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                },new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        DataSupport.deleteAll(SeekHistoryBase.class);
                        query();
                        dialog.dismiss();
                    }
                });
            }
        });
        adapter.longClickItem(new SeekAdapter.LongClickItem() {
            @Override
            public void longClickItem(View view, final int position) {
                final NormalDialog dialog = DialogShow.showSelectDialog(mContext,"是否删除数据",2,new String[] { getResources().getString(R.string.cancel),getResources().getString(R.string.confirm)});
                dialog.setOnBtnClickL(new OnBtnClickL() {

                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                },new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        DataSupport.deleteAll(SeekHistoryBase.class,"name=?",allSeek.get(position).getName());
                        query();
                        dialog.dismiss();
                    }
                });
            }
        });

    }

    private void getShareList(String pageIndex, String pageSize, String content) {
        shareSeekBut.setVisibility(View.GONE);
//        recyclerview.setVisibility(View.GONE);
//        shareSeekLayout.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put("index", pageIndex);
        params.put("size", pageSize);
        params.put("gs", content);
        OkHttp.get(mContext, ConnectPath.GET_SHARE_CAMERA, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("obj")) {
                        cameraList.clear();
                        JSONObject jobj = (JSONObject) response.get("obj");
                        if (jobj.has("list")) {
                            JSONArray jan = (JSONArray) jobj.get("list");
                            if (jan != null && jan.length() != 0) {
                                for (int i = 0; i < jan.length(); i++) {
                                    Camera camera = new Camera();
                                    JSONObject jobjCamera = jan.getJSONObject(i);
                                    camera.setmRoomId(jobjCamera.has("id") ? jobjCamera.getString("id") : "");
                                    camera.setmName(jobjCamera.has("name") ? jobjCamera.getString("name") : "");
                                    camera.setmEquipmentId(jobjCamera.has("cid") ? jobjCamera.getString("cid") : "");
                                    camera.setmDescribe(jobjCamera.has("describe") ? jobjCamera.getString("describe") : "");
                                    camera.setmImagePath(jobjCamera.has("img") ? jobjCamera.getString("img") : "");
                                    cameraList.add(camera);
                                }

                            }
                        }
                        shareSeekSwipereLayout.setRefreshing(false);
                        if (cameraList != null && !cameraList.isEmpty()) {
                            adapter = new CameraAdapter(cameraList);
                            shareSeekRecyclerview.setVisibility(View.VISIBLE);
                            shareSeekNoDatas.setVisibility(View.GONE);
                            shareSeekRecyclerview.setAdapter(adapter);
                            adapter.clickItem(new CameraAdapter.ClickItem() {
                                @Override
                                public void onclick(View view, int position) {
                                    Camera camera = cameraList.get(position);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("name", camera.getmName());
                                    bundle.putString("equipmentId", camera.getmEquipmentId());
                                    bundle.putString("roomId", camera.getmRoomId());
                                    bundle.putString("describe", camera.getmDescribe());
                                    BaseUtils.startActivities(mContext, ActivitySharePlay.class, bundle);
                                }
                            });
                        } else {
                            shareSeekRecyclerview.setVisibility(View.GONE);
                            shareSeekNoDatas.setVisibility(View.VISIBLE);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }));
    }

    private void addShareList(String pageIndex, String pageSize, String content) {
        Map<String, String> params = new HashMap<>();
        params.put("index", pageIndex);
        params.put("size", pageSize);
        params.put("gs", content);
        OkHttp.get(mContext, ConnectPath.GET_SHARE_CAMERA, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("obj")) {
                        JSONObject jobj = (JSONObject) response.get("obj");
                        if (jobj.has("list")) {
                            JSONArray jan = (JSONArray) jobj.get("list");
                            if (jan != null && jan.length() != 0) {
                                for (int i = 0; i < jan.length(); i++) {
                                    Camera camera = new Camera();
                                    JSONObject jobjCamera = jan.getJSONObject(i);
                                    camera.setmRoomId(jobjCamera.has("id") ? jobjCamera.getString("id") : "");
                                    camera.setmName(jobjCamera.has("name") ? jobjCamera.getString("name") : "");
                                    camera.setmEquipmentId(jobjCamera.has("cid") ? jobjCamera.getString("cid") : "");
                                    camera.setmDescribe(jobjCamera.has("describe") ? jobjCamera.getString("describe") : "");
                                    camera.setmImagePath(jobjCamera.has("img") ? jobjCamera.getString("img") : "");
                                    cameraList.add(camera);
                                }

                            }
                        }
                        adapter.notifyItemInserted(cameraList.size());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }));
    }

    @OnClick({R.id.share_seek_back, R.id.share_seek_but,R.id.share_seek_import})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.share_seek_import://输入框
                shareSeekImport.setFocusable(true);//设置输入框可聚集
                shareSeekImport.setFocusableInTouchMode(true);//设置触摸聚焦
                shareSeekImport.requestFocus();//请求焦点
                shareSeekImport.findFocus();//获取焦点
                mInputMethodManager.showSoftInput(shareSeekImport, InputMethodManager.SHOW_FORCED);// 显示输入法
                break;
            case R.id.share_seek_back:
                finish();
                break;
            case R.id.share_seek_but:
                if (!TextUtils.isEmpty(shareSeekImport.getText())) {
                    mContent = shareSeekImport.getText().toString();
                    if (isRepetition(mContent)) {
//                    SeekHistoryBase base = new SeekHistoryBase(mContent,mEhomeApplication.getmCurrentUser().getmId());
                    SeekHistoryBase base = new SeekHistoryBase(mContent,getDateToString(System.currentTimeMillis()));
                    base.save();
                    }
                    getShareList("1", "10", shareSeekImport.getText().toString());
                    shareSeekImport.setFocusable(false);//失去焦点
                    if (mInputMethodManager.isActive()) {
                        mInputMethodManager.hideSoftInputFromWindow(shareSeekImport.getWindowToken(), 0);// 隐藏输入法
                    }
                }
                break;
        }
    }
    private boolean isRepetition(String content){
        if(allSeek!=null && !allSeek.isEmpty()){
            for (SeekHistoryBase base : allSeek) {
                if (content.equals(base.getName())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mInputMethodManager.isActive()) {
            mInputMethodManager.hideSoftInputFromWindow(shareSeekImport.getWindowToken(), 0);// 隐藏输入法
        }
    }

    public static String getDateToString(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sf.format(d);
    }

    //
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//            View v = getCurrentFocus();
//            if (isShouldHideInput(v, ev)) {
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                if (imm != null) {
//                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                }
//            }
//            return super.dispatchTouchEvent(ev);
//        } // 必不可少，否则所有的组件都不会有TouchEvent了
//        if (getWindow().superDispatchTouchEvent(ev)) {
//            return true;
//        }
//        return onTouchEvent(ev);
//    }
//
//    public boolean isShouldHideInput(View v, MotionEvent event) {
//        if (v != null && (v instanceof EditText)) {
//            int[] leftTop = {0, 0};
//            //获取输入框当前的location位置
//            v.getLocationInWindow(leftTop);
//            int left = leftTop[0];
//            int top = leftTop[1];
//            int bottom = top + v.getHeight();
//            int right = left + v.getWidth();
//            if (event.getX() > left && event.getX() < right
//                    && event.getY() > top && event.getY() < bottom) {
//                // 点击的是输入框区域，保留点击EditText的事件
//                return false;
//            } else {
//                //使EditText触发一次失去焦点事件
//                v.setFocusable(false);
////                v.setFocusable(true); //这里不需要是因为下面一句代码会同时实现这个功能
//                v.setFocusableInTouchMode(true);
//                return true;
//            }
//        }
//        return false;
//    }


}
