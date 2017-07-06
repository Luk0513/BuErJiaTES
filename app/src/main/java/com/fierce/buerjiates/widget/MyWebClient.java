package com.fierce.buerjiates.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by win7 on 2017/2/20.
 */

public class MyWebClient extends WebViewClient {

    Context context;

    public MyWebClient(Context context) {
        this.context = context;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        Log.e("TAG", "开始加载中");
        //Toast.makeText(context, "开始加载", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        Log.e("TAG", "加载完成");
        //Toast.makeText(context, "加载完成", Toast.LENGTH_SHORT).show();

        /**
         * 解决 video 不自动播放的问题
         */
        view.loadUrl("javascript:(function() " +
                "{ var videos = document.getElementsByTagName('video');" +
                " for(var i=0;i<videos.length;i++){" +
                "videos[i].play();" +
                "}" +
                "})()");
    }

}
