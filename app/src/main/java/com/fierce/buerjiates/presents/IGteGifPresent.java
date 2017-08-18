package com.fierce.buerjiates.presents;

import com.fierce.buerjiates.bean.Gift_Bean;
import com.fierce.buerjiates.interfaces.IBeanCallback;
import com.fierce.buerjiates.interfaces.IRequestInterface;
import com.fierce.buerjiates.models.RequestInterfaceModel;
import com.fierce.buerjiates.views.IGetGiftView;

/**
 * @Author : Lukang
 * >>Live and learn<<
 * blog : http://blog.csdn.net/fight_0513
 * @PackegeName : com.fierce.buerjiates.presents
 * @ProjectName : BuErJiaTES
 * @Date :  2017-08-18
 */

public class IGteGifPresent {

    IGetGiftView view;

    IRequestInterface requestInterface;

    public IGteGifPresent(IGetGiftView view) {
        this.view = view;
        requestInterface = new RequestInterfaceModel();
    }

    public void getGifts() {
        requestInterface.getGifts(new IBeanCallback<Gift_Bean>() {
            @Override
            public void onSuccesd(Gift_Bean giftBean) {
                view.getGiftSucceed(giftBean);
            }

            @Override
            public void onError(String msg) {

                view.getGiftFault(msg);
            }
        });
    }

    ;
}
