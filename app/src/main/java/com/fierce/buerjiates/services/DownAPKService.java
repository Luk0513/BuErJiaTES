package com.fierce.buerjiates.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.fierce.buerjiates.config.MyApp;
import com.fierce.buerjiates.utils.DownloadUtil;
import com.fierce.buerjiates.utils.mlog;

import java.io.File;

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
            mlog.e("TAG", "onHandleIntent: <<<<<" + "下载……");
            //删除本地apk文件
            File downloadFile = new File(Environment.getExternalStorageDirectory(), "update");
            File[] files = new File(downloadFile.getAbsolutePath()).listFiles();
            if (files != null && files.length != 0) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                        file.deleteOnExit();
                    }
                }
            }
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
        final String apkUrl = intent.getStringExtra("url");
        downloadUtil.download(apkUrl, "update", new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(Object o) {

                mlog.e("onDownloadSuccess: <><><><><><<<<>>>");
                File apk = (File) o;
                String apkPath = apk.getAbsolutePath();
                Intent in = new Intent("Install");
                in.putExtra("isDone", true);
                in.putExtra("apk", apkPath);
                sendBroadcast(in);
                MyApp.getInstance().saveisRunning(false);
            }

            @Override
            public void onDownloading(int progress) {

            }

            @Override
            public void onDownloadFailed() {
                MyApp.getInstance().saveisRunning(false);
            }
        });
    }

}
