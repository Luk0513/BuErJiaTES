package com.fierce.buerjiates.views;


import com.fierce.buerjiates.bean.Banners_Bean;

import java.util.List;

/**
 * Created by win7 on 2017/3/15.
 */

public interface IGetBannerView {

     void getBannerSucceed(List<Banners_Bean.ListBean> banerListBean);

     void getBannerFailure(String msg);

}
