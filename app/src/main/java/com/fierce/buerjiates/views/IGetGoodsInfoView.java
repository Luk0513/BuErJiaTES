package com.fierce.buerjiates.views;

import com.fierce.buerjiates.bean.GoodsBean;

import java.util.List;

/**
 * Created by win7 on 2017/3/22.
 */

public interface IGetGoodsInfoView {
    void getGoodsInfoSucceed(List<GoodsBean.ListBean> goodsList);

    void getGoogsInfoFailure(String msg);
}
