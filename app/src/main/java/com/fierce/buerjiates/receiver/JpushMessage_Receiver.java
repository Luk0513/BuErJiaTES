package com.fierce.buerjiates.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fierce.buerjiates.config.MyApp;
import com.fierce.buerjiates.utils.mlog;

import cn.jpush.android.api.JPushInterface;

/**
 * @Author : Lukang
 * >>Live and learn<<
 * @PackegeName : com.fierce.buerjiates.receiver
 * @ProjectName : BuErJiaTES
 * @Date :  2017-07-17
 */

public class JpushMessage_Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        mlog.e("收到通知");
        //接收到自定义消息 处理事件 by：lukang
        if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            MyApp.getInstance().updateAPP();//更新APP
            MyApp.getInstance().sendJpushBrocads();
        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            mlog.e("[MyReceiver]" + intent.getAction() + " connected:" + connected);
        }
    }
}
