package com.fierce.buerjiates.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fierce.buerjiates.config.MyApp;

import cn.jpush.android.api.JPushInterface;

/**
 * @Author : Lukang
 * >>Live and learn<<
 * @PackegeName : com.fierce.buerjiates.receiver
 * @ProjectName : BuErJiaTES
 * @Date :  2017-07-17
 */

public class JpushMessage_Receiver extends BroadcastReceiver {
    private static final String TAG = "JpushMessage_Receiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        //接收到自定义消息 处理事件 by：lukang
        if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            MyApp.getInstance().updateAPP();//更新APP
            MyApp.getInstance().sendJpushBrocads();
        }else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
//            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
//            Toast.makeText(MyApp.getInstance().getContext(),connected+"",Toast.LENGTH_LONG);
//            Log.e(TAG, "[MyReceiver]" + intent.getAction() +" connected:"+connected);
        }
    }


}
