package com.fierce.buerjiates.presents;


import com.fierce.buerjiates.models.RequestInterfaceModel;
import com.fierce.buerjiates.views.IActiveDeviceView;

import com.fierce.buerjiates.interfaces.IBeanCallback;
import com.fierce.buerjiates.interfaces.IRequestInterface;

/**
 * Created by win7 on 2017/3/6.
 */

public class IActdevicePresent {
    private static final String TAG = "IActdevicePresent";

    private IRequestInterface iRequestInterface;

    private IActiveDeviceView iActiveDeviceView;

    public IActdevicePresent(IActiveDeviceView iActiveDeviceView) {
        this.iActiveDeviceView = iActiveDeviceView;
        iRequestInterface = new RequestInterfaceModel();
    }


    public void acvDevice() {
        iRequestInterface.activeDevice(iActiveDeviceView.getDeviceId(), iActiveDeviceView.getDeviceKey(), new IBeanCallback() {
            @Override
            public void onSuccesd(Object o) {
                iActiveDeviceView.showToasAct("设备激活成功");
                iActiveDeviceView.onSucceed();
            }

            @Override
            public void onError(String msg) {
                if (msg.equals("2"))
                    iActiveDeviceView.onOFF();
                else if (msg.equals("0"))
                    iActiveDeviceView.showToasAct("请输入正确的设备ID 或者 密钥");
            }
        });

    }
}
