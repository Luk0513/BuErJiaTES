package com.fierce.buerjiates.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fierce.buerjiates.config.MyApp;
import com.fierce.buerjiates.utils.DownloadUtil;

public class DownAPKService extends IntentService {

    DownloadUtil downloadUtil;

    public DownAPKService() {
        super("DownAPKService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (!MyApp.getInstance().getisRunning()) {
            Log.e("TAG", "onHandleIntent: <<<<<" + "下载……");
            downloadApk(intent);
            MyApp.getInstance().saveisRunning(true);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        downloadUtil = DownloadUtil.get();
        return super.onStartCommand(intent, flags, startId);
    }

    private void downloadApk(@Nullable final Intent intent) {
        String apkUrl = intent.getStringExtra("url");
        downloadUtil.download(apkUrl, "update", new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(Object o) {
                Log.e("TAG", "onDownloadSuccess: <><><><><><<<<>>>");
                Intent in = new Intent("PupoState");
                in.putExtra("isDone", true);
                sendBroadcast(in);
                MyApp.getInstance().saveisRunning(false);
            }

            @Override
            public void onDownloading(int progress) {

            }

            @Override
            public void onDownloadFailed() {
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
