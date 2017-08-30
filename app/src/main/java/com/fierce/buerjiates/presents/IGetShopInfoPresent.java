package com.fierce.buerjiates.presents;

import com.fierce.buerjiates.bean.ShopInfo_Bean;
import com.fierce.buerjiates.interfaces.IBeanCallback;
import com.fierce.buerjiates.interfaces.IRequestInterface;
import com.fierce.buerjiates.models.RequestInterfaceModel;
import com.fierce.buerjiates.views.IGetShopInfoView;

/**
 * @Author : Lukang
 * >>Live and learn<<
 * blog : http://blog.csdn.net/fight_0513
 * @PackegeName : com.fierce.buerjiates.presents
 * @ProjectName : BuErJiaTES
 * @Date :  2017-08-29
 */

public class IGetShopInfoPresent {
    IGetShopInfoView view;
    IRequestInterface modle;

    public IGetShopInfoPresent(IGetShopInfoView view) {
        this.view = view;
        modle = new RequestInterfaceModel();
    }

    public IGetShopInfoPresent() {
        modle = new RequestInterfaceModel();
    }

    public void getShopInfo(String d_id) {
        modle.getShopInfo(d_id, new IBeanCallback<ShopInfo_Bean>() {
            @Override
            public void onSuccesd(ShopInfo_Bean shopInfo_bean) {
                view.getTGetShopInfoSucceed(shopInfo_bean);
            }

            @Override
            public void onError(String msg) {
                view.getGetShopInfoFailure(msg);
            }
        });
    }

    public void getShopInfo_nocallbacke(String d_id) {
        modle.getShopInfo(d_id, new IBeanCallback<ShopInfo_Bean>() {
            @Override
            public void onSuccesd(ShopInfo_Bean shopInfo_bean) {
            }

            @Override
            public void onError(String msg) {
            }
        });
    }
}
