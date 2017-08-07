package com.fierce.buerjiates.presents;

import com.fierce.buerjiates.bean.GoodsList_Bean;
import com.fierce.buerjiates.interfaces.IBeanCallback;
import com.fierce.buerjiates.interfaces.IRequestInterface;
import com.fierce.buerjiates.models.RequestInterfaceModel;
import com.fierce.buerjiates.views.IGetGoodsListView;

/**
 * Created by win7 on 2017/3/15.
 */

public class IGetGoodsListPresent {
    private IGetGoodsListView goodsListView;
    private IRequestInterface iRequestInterface;

    public IGetGoodsListPresent(IGetGoodsListView goodsListView) {
        this.goodsListView = goodsListView;
        iRequestInterface = new RequestInterfaceModel();
    }

    public void getGoodsList(String categoryId, String admcNum) {
        iRequestInterface.getGoodsList(categoryId, admcNum, new IBeanCallback<GoodsList_Bean>() {
            @Override
            public void onSuccesd(GoodsList_Bean listBean) {
                goodsListView.getListSucceed(listBean.getList());
            }

            @Override
            public void onError(String msg) {
                goodsListView.showToas_getGoodList(msg);
                goodsListView.getListFailure(msg);
            }


        });
    }
}
