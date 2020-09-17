package com.mijia.app.ui.other.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.blankj.utilcode.util.StringUtils;
import com.handong.framework.account.AccountHelper;
import com.handong.framework.base.BaseActivity;
import com.handong.framework.base.BaseViewModel;
import com.mijia.app.AdvertisingActivity;
import com.mijia.app.MainActivity;
import com.mijia.app.R;
import com.mijia.app.constants.Constants;
import com.mijia.app.databinding.ActivityWebViewBinding;

public class WebViewActivity extends BaseActivity<ActivityWebViewBinding, BaseViewModel> {

    private String webUrl = "";

    private String title = "";

    private boolean isAdvertsing = false;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_web_view;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        webUrl = getIntent().getStringExtra(Constants.WEBURL);
        title = getIntent().getStringExtra("title");
        isAdvertsing = getIntent().getBooleanExtra("isAdvertsing", false);

        if (!StringUtils.isEmpty(title)) {
            mBinding.tvTitle.setText(title);
        }

        WebSettings webSettings = mBinding.webView.getSettings();
        // 设置可以与js交互，为了防止资源浪费，我们可以在Activity
        // 的onResume中设置为true，在onStop中设置为false
        webSettings.setJavaScriptEnabled(true);

        //设置自适应屏幕，两者合用
        //将图片调整到适合webview的大小
        webSettings.setUseWideViewPort(true);
        // 缩放至屏幕的大小
        webSettings.setLoadWithOverviewMode(true);

        //设置编码格式
        webSettings.setDefaultTextEncodingName("utf-8");

        // 设置允许JS弹窗
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        //设置缓存的模式
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//
//        WebViewClient mWebviewclient = new WebViewClient() {
//
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                return super.shouldOverrideUrlLoading(view, url);
//            }
//
//            @Override
//            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                handler.proceed();
//            }
//
//            @Override
//            public void onReceivedError(WebView view, int errorCode,
//                                        String description, String failingUrl) {
//                // TODO Auto-generated method stub
//                super.onReceivedError(view, errorCode, description, failingUrl);
//            }
//        };
//        mBinding.webView.setWebViewClient(mWebviewclient);

        mBinding.webView.setWebViewClient(new WebViewClient() {
            //覆盖shouldOverrideUrlLoading 方法
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
                super.onReceivedError(webView, webResourceRequest, webResourceError);
                if (webResourceRequest.isForMainFrame()) {//是否是为 main frame创建
                    mBinding.webView.loadUrl("about:blank");// 避免出现默认的错误界面
                    mBinding.webView.loadUrl(webUrl);// 加载自定义错误页面

                }
            }

        });

        mBinding.webView.getSettings().setJavaScriptEnabled(true);
        mBinding.webView.getSettings().setAppCacheEnabled(true);
        //设置 缓存模式
        mBinding.webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        // 开启 DOM storage API 功能
        mBinding.webView.getSettings().setDomStorageEnabled(true);

        mBinding.webView.loadUrl(webUrl);

        mBinding.ivReturn.setOnClickListener(v -> {
            if (isAdvertsing) {
                goNext();
            } else {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isAdvertsing) {
            goNext();
        } else {
            finish();
        }
    }

    private void goNext() {
        if (StringUtils.isEmpty(AccountHelper.getUserId())) {
            startActivity(new Intent(WebViewActivity.this, LoginActivity.class));
        } else {
            startActivity(new Intent(WebViewActivity.this, MainActivity.class));
        }
        finish();
    }
}
