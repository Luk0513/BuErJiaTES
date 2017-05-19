package com.fierce.buerjiates.views;

/**
 * Created by win7 on 2017/3/6.
 */

public interface IActiveDeviceView {
    void showToasAct(String msg);

    String getDeviceId();

    String getDeviceKey();

    void onSucceed();

    void onOFF();

}
