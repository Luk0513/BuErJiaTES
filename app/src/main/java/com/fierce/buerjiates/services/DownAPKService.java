package com.fierce.buerjiates.services;

import android.app.IntentService;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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
        String apkUrl = intent.getStringExtra("url");
        downloadUtil.download(apkUrl, "update", new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(Object o) {
                final File apkFile = (File) o;
                AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
                builder.setTitle("更新提示：")
                        .setMessage("发现新版本安装包！" +
                                "\n请立即更新")
                        .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                installApk(apkFile);
                            }
                        });
                builder.setCancelable(false);
                builder.show();
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        downloadUtil = DownloadUtil.get();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    //打开APK程序代码
    private void installApk(File file) {
        // TODO Auto-generated method stub
        Log.e("OpenFile", file.getName());
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }
}
