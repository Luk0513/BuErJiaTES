package com.fierce.buerjiates.ui;

import android.webkit.WebView;

import com.fierce.buerjiates.R;
import com.fierce.buerjiates.base.BaseActivity;
import com.fierce.buerjiates.widget.MyWebClient;

import butterknife.BindView;

/**
 * @Author : Lukang
 * >>Live and learn<<
 * @PackegeName : com.fierce.buerjiates.ui
 * @ProjectName : BuErJiaTES
 * @Date :  2017-07-06
 */

public class Lottery_activity extends BaseActivity {
    @BindView(R.id.wv_Dazhuanpan)
    WebView wvDazhuanpan;

    private MyWebClient webClient;

    @Override
    protected int getLayoutRes() {
        return R.layout.lottery_activity_laout;
    }

    @Override
    protected void initView() {
        initWebSetting();
    }

    private void initWebSetting() {
        webClient = new MyWebClient(this);
        wvDazhuanpan.setWebViewClient(webClient);
        wvDazhuanpan.getSettings().setSupportMultipleWindows(true);//支持多窗口
        wvDazhuanpan.getSettings().setJavaScriptEnabled(true);//支持JS
//        wb.getSettings().setAllowFileAccess(true);//支持访问文件数据
//        wb.getSettings().setAllowContentAccess(true);
//        wb.getSettings().setAllowUniversalAccessFromFileURLs(true);
        wvDazhuanpan.getSettings().setAllowFileAccessFromFileURLs(true);
        wvDazhuanpan.getSettings().setUseWideViewPort(true);//支持HTML的“viewport”标签
        wvDazhuanpan.loadUrl("file:///android_asset/index.html");
    }
}
