package com.fierce.buerjiates.presents;

import com.fierce.buerjiates.bean.GoodsBean;
import com.fierce.buerjiates.interfaces.IBeanCallback;
import com.fierce.buerjiates.interfaces.IRequestInterface;
import com.fierce.buerjiates.models.RequestInterfaceModel;
import com.fierce.buerjiates.views.IGetGoodsInfoView;

/**
 * Created by win7 on 2017/3/22.
 */

public class IGetGoodsPresent {
    private IGetGoodsInfoView getGoodsInfoView;
    private IRequestInterface requestInterface;

    public IGetGoodsPresent(IGetGoodsInfoView getGoodsInfoView) {
        this.getGoodsInfoView = getGoodsInfoView;
        requestInterface = new RequestInterfaceModel();
    }

    public void getGoodsInfo(final String goodsId) {
        requestInterface.getGoodsInfo(goodsId, new IBeanCallback<GoodsBean>() {
                    @Override
                    public void onSuccesd(GoodsBean goodsBean) {
                        getGoodsInfoView.getGoodsInfoSucceed(goodsBean.getList());
                    }

                    @Override
                    public void onError(String msg) {
                        getGoodsInfoView.getGoogsInfoFailure(goodsId);
                    }
                }

        );

    }

}
