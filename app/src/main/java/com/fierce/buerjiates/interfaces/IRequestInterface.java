package com.fierce.buerjiates.interfaces;

import android.support.annotation.NonNull;

import com.fierce.buerjiates.bean.Banners_Bean;
import com.fierce.buerjiates.bean.GoodsBean;
import com.fierce.buerjiates.bean.GoodsList_Bean;
import com.fierce.buerjiates.bean.GoodsSort_Bean;


/**
 * Created by win7 on 2017/3/6.
 */

public interface IRequestInterface {

    void activeDevice(@NonNull String device_Id, @NonNull String device_Key, IBeanCallback callback);

    void getBanner(IBeanCallback<Banners_Bean> callback);

    void getGoodsSort(IBeanCallback<GoodsSort_Bean> callback);

    void getGoodsList(@NonNull String categoryId, IBeanCallback<GoodsList_Bean> callback);

    void getGoodsInfo(@NonNull String goodsId, IBeanCallback<GoodsBean> callback);

    void getGoodsPrice(String c, String a, String key, String goodsSn, String categoryId, IBeanCallback callback);

    void getTuanGouPrice(String c, String a, String ukey, String goodsSn, String categoryId, IBeanCallback callback);
}
