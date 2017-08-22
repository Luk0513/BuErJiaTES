package com.fierce.buerjiates.presents;

import com.fierce.buerjiates.interfaces.IBeanCallback;
import com.fierce.buerjiates.interfaces.IRequestInterface;
import com.fierce.buerjiates.models.RequestInterfaceModel;
import com.fierce.buerjiates.views.IVerifyView;

/**
 * @Author : Lukang
 * >>Live and learn<<
 * blog : http://blog.csdn.net/fight_0513
 * @PackegeName : com.fierce.buerjiates.presents
 * @ProjectName : BuErJiaTES
 * @Date :  2017-08-22
 */

public class IVerifyPresent {
    IVerifyView verifyView;
    IRequestInterface modle;

    public IVerifyPresent(IVerifyView verifyView) {
        this.verifyView = verifyView;
        modle = new RequestInterfaceModel();
    }

    public void verify(String code) {
        modle.verify(code, new IBeanCallback() {
            @Override
            public void onSuccesd(Object o) {
                verifyView.verifySucceed(o.toString());
            }

            @Override
            public void onError(String msg) {
                verifyView.verifyFailure(msg);

            }
        });
    }
}
