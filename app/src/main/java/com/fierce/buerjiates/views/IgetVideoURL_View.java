package com.fierce.buerjiates.views;

/**
 * @Author : Lukang
 * >>Live and learn<<
 * blog : http://blog.csdn.net/fight_0513
 * @PackegeName : com.fierce.buerjiates.views
 * @ProjectName : BuErJiaTES
 * @Date :  2017-10-17
 */

public interface IgetVideoURL_View {
    void getURLSucceed(String videoUrl, int urlid);

    void getURLFailure(String msg);
}
