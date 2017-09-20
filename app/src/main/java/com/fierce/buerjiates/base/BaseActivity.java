package com.fierce.buerjiates.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.fierce.buerjiates.R;
import com.fierce.buerjiates.utils.mlog;
import com.fierce.buerjiates.widget.CustomDialog;

import java.io.File;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/3/11.
 */

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideNavigationBar();
        setContentView(getLayoutRes());
        ButterKnife.bind(this);
        initView();
    }

    protected abstract int getLayoutRes();

    protected abstract void initView();

    private APKBroadcastReceiver broadReceiver;

    public void showToast(final String msg) {
        //直接将弹出 Toast 方法方在子线程 提高方法的复用性
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, -250);
                toast.show();
            }
        });
    }

    public Activity getActivity() {
        return this;
    }

    /**
     * Android 4.4以上才有效果
     * 沉浸式状态栏 导航栏
     *
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            hideNavigationBar();
        }
        hideNavigationBar();
    }

    public void hideNavigationBar() {
        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar

        if (Build.VERSION.SDK_INT >= 19) {
            //SYSTEM_UI_FLAG_IMMERSIVE_STICKY: hide navigation bars - compatibility:
            // building API level is lower thatn 19, use magic number directly for
            // higher API target level
            uiFlags |= 0x00001000;
        } else {
            uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }

        getWindow().getDecorView().setSystemUiVisibility(uiFlags);
    }

    private Dialog updateDialog;//更新提示

    public class APKBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("Install")) {
                boolean isDone = intent.getBooleanExtra("isDone", false);//下载完成
                final String apkPath = intent.getStringExtra("apk");
                if (isDone) {
                    updateDialog = new CustomDialog.Builder(getActivity())
                            .setIsFloating(false)
                            .setIsFullscreen(false)
                            .setLayout(R.layout.update_dialog_layout)
                            .setViewOnClike(R.id.tv_update, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    installApk(apkPath);
                                }
                            })
                            .build();
                    updateDialog.show();
                }
            }
            if (intent.getAction().equals("BLE")) {
                //连接上蓝牙
                String s = intent.getStringExtra("data");
                mlog.e("0000");
                if (s != null) {
                    showToast(s);
                } else {
                }
            }
        }
    }

    private void registrBodcast() {
        IntentFilter intentFilter = new IntentFilter("BLE");
        intentFilter.addAction("Install");
        broadReceiver = new APKBroadcastReceiver();
        registerReceiver(broadReceiver, intentFilter);

    }

    //打开APK程序代码
    private void installApk(String apkPath) {
//        File downloadFile = new File(Environment.getExternalStorageDirectory(), "update");
//        File[] files = new File(downloadFile.getAbsolutePath()).listFiles();
        File apk = new File(apkPath);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apk),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registrBodcast();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadReceiver);
    }
}
