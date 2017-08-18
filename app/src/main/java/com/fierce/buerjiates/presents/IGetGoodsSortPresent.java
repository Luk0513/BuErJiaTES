package com.fierce.buerjiates.presents;

import com.fierce.buerjiates.bean.GoodsSort_Bean;
import com.fierce.buerjiates.models.RequestInterfaceModel;
import com.fierce.buerjiates.views.IGetGoodsSorView;

import com.fierce.buerjiates.interfaces.IBeanCallback;
import com.fierce.buerjiates.interfaces.IRequestInterface;

/**
 * Created by win7 on 2017/3/15.
 */

public class IGetGoodsSortPresent {
    private IGetGoodsSorView goodsSorView;
    private IRequestInterface requestInterface;

    public IGetGoodsSortPresent(IGetGoodsSorView goodsSorView) {
        this.goodsSorView = goodsSorView;
        requestInterface = new RequestInterfaceModel();

    }

    public void getGoodsSort() {
        requestInterface.getGoodsSort(new IBeanCallback<GoodsSort_Bean>() {
            @Override
            public void onSuccesd(GoodsSort_Bean goodsSort_bean) {
                goodsSorView.getSortSucceed(goodsSort_bean.getList());
            }

            @Override
            public void onError(String msg) {
                goodsSorView.getSortFailure(msg);
                goodsSorView.showToas_Sort(msg);
            }
        });
    }
}
