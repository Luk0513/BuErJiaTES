package com.fierce.buerjiates.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fierce.buerjiates.utils.DownloadUtil;

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
        Log.e("TAG", "onHandleIntent: :::::::::::::DownAPKService:::::::::后台服务");
        downloadApk(intent);
    }

    private void downloadApk(@Nullable final Intent intent) {
        String apkUrl = intent.getStringExtra("url");
        downloadUtil.download(apkUrl, "update", new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(Object o) {
                File apkFile = (File) o;
                Log.e("TAG", "onDownloadSuccess: <><><><><><<<<>>>");
                Intent in = new Intent("PupoState");
                String path = apkFile.getAbsolutePath();
                in.putExtra("isDone", true);
                sendBroadcast(in);
//                installApk(apkFile);
            }

            @Override
            public void onDownloading(int progress) {

            }

            @Override
            public void onDownloadFailed() {
                Intent in = new Intent("PupoState");
                sendBroadcast(in);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        downloadUtil = DownloadUtil.get();
        return super.onStartCommand(intent, flags, startId);
    }

//    //打开APK程序代码
//    private void installApk(File file) {
//        Log.e("OpenFile", file.getName());
//        Intent intent = new Intent();
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setAction(android.content.Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.fromFile(file),
//                "application/vnd.android.package-archive");
//        startActivity(intent);
//    }


}
