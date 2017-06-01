package com.fierce.buerjiates.presents;

import com.fierce.buerjiates.interfaces.IBeanCallback;
import com.fierce.buerjiates.interfaces.IRequestInterface;
import com.fierce.buerjiates.models.RequestInterfaceModel;
import com.fierce.buerjiates.views.IGetGoodsPriceView;

/**
 * Created by win7 on 2017-05-16.
 */

public class IGetGoodsPricePresent {
    IGetGoodsPriceView getGoodsPriceView;
    IRequestInterface requestInterface;

    public IGetGoodsPricePresent(IGetGoodsPriceView getGoodsPriceView) {
        this.getGoodsPriceView = getGoodsPriceView;
        requestInterface = new RequestInterfaceModel();
    }


    public void getGoodsPrice(String goodsSN,String categoryId) {
        requestInterface.getGoodsPrice("user", "login", "b214af95fab423cc843792c70a199f09", goodsSN ,categoryId ,new IBeanCallback() {
            @Override
            public void onSuccesd(Object o) {
                getGoodsPriceView.getPriceSucceed(o);
            }

            @Override
            public void onError(String msg) {
                getGoodsPriceView.getPriceFailure(msg);
            }
        });
    }
}
