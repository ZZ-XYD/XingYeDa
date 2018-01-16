package com.xingyeda.ehome.life;

import android.annotation.SuppressLint;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xingyeda.ehome.R;
import com.xingyeda.ehome.tenement.AdvertisementActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BusinessFragment extends Fragment {

    @Bind(R.id.business_web_view)
    WebView businessWebView;
    private View rootView;

    private String mUrl = "http://service.xyd999.com/appview.html";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_business, container, false);
        }
        ButterKnife.bind(this, rootView);
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }

        businessWebView.setWebViewClient(new WebViewClient() {
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
                //handler.cancel(); // Android默认的处理方式
                handler.proceed();  // 接受所有网站的证书
                //handleMessage(MessageBean msg); // 进行其他处理
            }
        });

        this.event();

        businessWebView.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && businessWebView.canGoBack()) {
                    handler.sendEmptyMessage(1);
                    return true;
                }
                return false;
                }
            });
        return rootView;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    webViewGoBack();
                    break;
            }
        }
    };
    private void webViewGoBack() {
        businessWebView.goBack();
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void event()
    {
        //设置WebView属性，能够执行Javascript脚本
        businessWebView.getSettings().setJavaScriptEnabled(true);
        businessWebView.getSettings().setDomStorageEnabled(true);
        //加载需要显示的网页
        businessWebView.loadUrl(mUrl.replaceAll("&amp;","&"));
        //设置Web视图
        businessWebView.setWebViewClient(new HelloWebViewClient());
    }


//    public void onKeyDown(int keyCode, KeyEvent event) {
//
//        if ((keyCode == KeyEvent.KEYCODE_BACK) && businessWebView.canGoBack()) {
//            businessWebView.goBack(); //goBack()表示返回WebView的上一页面
//        }
//
//    }
//        if (keyCode == KeyEvent.KEYCODE_BACK)
//        {
//            if (businessWebView.canGoBack())
//            {
//                businessWebView.goBack(); //goBack()表示返回WebView的上一页面
//                return true;
//            }
//        }
//        return false;
//    }
    //Web视图
    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {


            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
