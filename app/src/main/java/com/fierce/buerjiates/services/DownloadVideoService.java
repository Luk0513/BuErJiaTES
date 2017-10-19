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

public class DownloadVideoService extends IntentService {

    DownloadUtil downloadUtil;

    public DownloadVideoService() {
        super("DownAPKService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (!MyApp.getInstance().getDownloadVideoIsRunning()) {
            mlog.e("o>>>>>Video下载ing……");
            //删除本地apk文件
            File downloadFile = new File(Environment.getExternalStorageDirectory(), "adVideo");
            File[] files = new File(downloadFile.getAbsolutePath()).listFiles();
            if (files != null && files.length != 0) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                        file.deleteOnExit();
                    }
                }
            }
            downloadViedo(intent);
            MyApp.getInstance().saveDownloadVideoIsRunning(true);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        downloadUtil = DownloadUtil.get();
        return super.onStartCommand(intent, flags, startId);
    }

    private void downloadViedo(@Nullable final Intent intent) {
        String videoUrl = intent.getStringExtra("videourl");
        DownloadUtil.get().download(videoUrl, "adVideo", new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(Object o) {
//                Log.e("TAG", "onDownloadSuccess: ______下载完成______");
                MyApp.getInstance().saveDownloadVideoIsRunning(false);
            }

            @Override
            public void onDownloading(int progress) {

                //视频加载中

            }

            @Override
            public void onDownloadFailed() {
//                Log.e("TAG", "onDownloadFailed: ______下载失败______");
                MyApp.getInstance().saveDownloadVideoIsRunning(false);
            }
        });
    }

}
