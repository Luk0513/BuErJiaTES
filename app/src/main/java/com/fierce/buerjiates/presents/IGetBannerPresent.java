package com.fierce.buerjiates.presents;


import com.fierce.buerjiates.bean.Banners_Bean;
import com.fierce.buerjiates.models.RequestInterfaceModel;
import com.fierce.buerjiates.views.IGetBannerView;

import com.fierce.buerjiates.interfaces.IBeanCallback;
import com.fierce.buerjiates.interfaces.IRequestInterface;

/**
 * Created by win7 on 2017/3/15.
 */

public class IGetBannerPresent {

    private IGetBannerView getBannerView;
    private IRequestInterface iRequestInterface;

    public IGetBannerPresent(IGetBannerView getBannerView) {
        this.getBannerView = getBannerView;
        iRequestInterface = new RequestInterfaceModel();
    }

    public void getBanners() {
        iRequestInterface.getBanner(new IBeanCallback<Banners_Bean>() {
            @Override
            public void onSuccesd(Banners_Bean banners_bean) {
                getBannerView.getBannerSucceed(banners_bean.getList());
            }

            @Override
            public void onError(String msg) {
                getBannerView.getBannerFailure(msg);
            }
        });
    }
}
