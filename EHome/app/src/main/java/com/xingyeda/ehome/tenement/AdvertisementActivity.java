package com.xingyeda.ehome.tenement;

import android.annotation.SuppressLint;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.BaseActivity;

public class AdvertisementActivity extends BaseActivity
{
   @Bind(R.id.web_view)
    WebView mAdWebView;
   @Bind(R.id.ad_address)
    TextView mAddres;
    private String mUrl;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advertisement_activity);
        ButterKnife.bind(this);
        mAdWebView.setWebViewClient(new WebViewClient() {
                         public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
                             //handler.cancel(); // Android默认的处理方式
                             handler.proceed();  // 接受所有网站的证书
                             //handleMessage(Message msg); // 进行其他处理
                         }
                    });

        //初始化
        this.init();
        //数据加载
        this.event();
    }  
   //初始化
    private void init()
    {
        //获取绑定数据
        this.mUrl=getIntent().getExtras().getString("url");
        mAddres.setText(getIntent().getExtras().getString("type"));
        
    }
    
    @OnClick({R.id.ad_back})
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ad_back:
			AdvertisementActivity.this.finish();
			break;
		
		}

	}

    
    
    @SuppressLint("SetJavaScriptEnabled")
    private void event()
    {
       //设置WebView属性，能够执行Javascript脚本  
        mAdWebView.getSettings().setJavaScriptEnabled(true);
        mAdWebView.getSettings().setDomStorageEnabled(true);
        //加载需要显示的网页
        mAdWebView.loadUrl(mUrl.replaceAll("&amp;","&"));
        //设置Web视图  
        mAdWebView.setWebViewClient(new HelloWebViewClient ());  
    }

    @Override
    //设置回退  
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (mAdWebView.canGoBack())
            {
                mAdWebView.goBack(); //goBack()表示返回WebView的上一页面  
                return true;  
            }
            else {
                finish();//关闭界面
            }
        }
        return false;
    }
    //Web视图  
    private class HelloWebViewClient extends WebViewClient {
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            view.loadUrl(url);
//            return true;
//        }
@Override
public boolean shouldOverrideUrlLoading(WebView view, String url) {


    return super.shouldOverrideUrlLoading(view, url);
}
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}
