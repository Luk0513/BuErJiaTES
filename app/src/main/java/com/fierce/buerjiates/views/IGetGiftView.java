package com.fierce.buerjiates.views;

import com.fierce.buerjiates.bean.Gift_Bean;

/**
 * @Author : Lukang
 * >>Live and learn<<
 * blog : http://blog.csdn.net/fight_0513
 * @PackegeName : com.fierce.buerjiates.views
 * @ProjectName : BuErJiaTES
 * @Date :  2017-08-18
 */

public interface IGetGiftView {
    void getGiftSucceed(Gift_Bean listBean);

    void getGiftFault(String msg);
}
