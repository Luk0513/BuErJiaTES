package com.fierce.buerjiates.views;


import com.fierce.buerjiates.bean.GoodsSort_Bean;

import java.util.List;

/**
 * Created by win7 on 2017/3/15.
 */

public interface IGetGoodsSorView {
     void showToas_Sort(String msg);

     void getSortSucceed();

     void getSortFailure(String msg);

     void setGoodsSortImage(List<GoodsSort_Bean.ListBean> goodsSortListBean);
}
