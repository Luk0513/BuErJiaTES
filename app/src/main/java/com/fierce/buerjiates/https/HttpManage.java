package com.fierce.buerjiates.https;


import com.fierce.buerjiates.bean.Banners_Bean;
import com.fierce.buerjiates.bean.Gift_Bean;
import com.fierce.buerjiates.bean.GoodsBean;
import com.fierce.buerjiates.bean.GoodsList_Bean;
import com.fierce.buerjiates.bean.GoodsSort_Bean;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by win7 on 2017/3/1.
 */

public interface HttpManage {

    //激活设备
    @POST(HttpServerInterface.ACT_DEVICE)
    @FormUrlEncoded
    Call<String> activatedDve(@Field("ad_machineNumber") String deviceId,
                              @Field("sign") String deviceKey);

    @GET(HttpServerInterface.GET_BANNER)
    Call<Banners_Bean> getBannerImage();

    //
    @GET(HttpServerInterface.GET_BANNER)
    Call<String> getBanner();
//

    @GET(HttpServerInterface.GOODS_List)
    Call<GoodsList_Bean> getGoodsList(@Query("categoryId") String id,
                                      @Query("admcNum") String admcNum);

    //
    @GET(HttpServerInterface.GOODS_List)
    Call<String> getGoodsLists(@Query("categoryId") String id,
                               @Query("admcNum") String admcNum);

    //
    @GET(HttpServerInterface.GOODS_SORT)
    Call<GoodsSort_Bean> getGoodsSort();

    @GET(HttpServerInterface.GOODS_SORT)
    Call<String> getGoodsSorts();

    @GET(HttpServerInterface.GoodsInfo)
    Call<GoodsBean> getGoodsInfo(@Query("goodsId") String goodsId);

    @GET(HttpServerInterface.GoodsInfo)
    Call<String> getGoodsInfos(@Query("goodsId") String goodsId);

    @POST(HttpServerInterface.GoodsPrice)
    @FormUrlEncoded
    Call<String> getGoodsPrice(@Field("c") String user,
                               @Field("a") String login,
                               @Field("key") String key,
                               @Field("good_sn") String good_sn);

    @POST(HttpServerInterface.GoodsPrice)
    @FormUrlEncoded
    Call<String> getTGprice(@Field("c") String user,
                            @Field("a") String login,
                            @Field("ukey") String key,
                            @Field("good_sn") String good_sn);

    @GET(HttpServerInterface.UPDATEAPP)
    Call<String> updateApp();

    @GET(HttpServerInterface.GetGift)
    Call<Gift_Bean> getGift();

}
