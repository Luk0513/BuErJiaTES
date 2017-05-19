package com.fierce.buerjiates.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by win7 on 2017/3/7.
 *
 * 接受系统广播：设备开机完成
 * APP开机自启
 *
 */

public class CustomBroadcastReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Intent myApp = context.getPackageManager().getLaunchIntentForPackage("com.fierce.buerjiates");
        context.startActivity(myApp);
    }
}
