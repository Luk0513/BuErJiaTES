package com.fierce.buerjiates.presents;

import com.fierce.buerjiates.interfaces.IBeanCallback;
import com.fierce.buerjiates.interfaces.IRequestInterface;
import com.fierce.buerjiates.models.RequestInterfaceModel;
import com.fierce.buerjiates.views.IgetTuangouView;

/**
 * @Author : Lukang
 * >>Live and learn<<
 * @PackegeName : com.fierce.buerjiates.presents
 * @ProjectName : BuErJiaTES
 * @Date :  2017-07-11
 */

public class IgetTuanGouPricePresent {
    IgetTuangouView view;
    IRequestInterface modle;

    public IgetTuanGouPricePresent(IgetTuangouView view) {
        this.view = view;
        modle = new RequestInterfaceModel();
    }

    public void getTGPrice(String goodsSN, String categoryId) {
        modle.getTuanGouPrice("user", "login", "eb4da74744abacfe2ad8e37b184cf8e2", goodsSN, categoryId, new IBeanCallback() {
            @Override
            public void onSuccesd(Object o) {
                view.getTGPriceSucceed(o);
            }

            @Override
            public void onError(String msg) {
                view.getTGPriceFailure(msg);
            }
        });
    }
}
