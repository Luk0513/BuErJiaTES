package com.fierce.buerjiates.views;


import com.fierce.buerjiates.bean.GoodsList_Bean;

import java.util.List;

/**
 * Created by win7 on 2017/3/15.
 */

public interface IGetGoodsListView {
    void showToas_getGoodList(String msg);

    void getListSucceed(List<GoodsList_Bean.ListBean> goodsListBean);

    void getListFailure(String msg);
}
