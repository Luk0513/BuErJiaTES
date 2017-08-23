package com.fierce.buerjiates.interfaces;

/**
 * Created by Administrator on 2016-12-05.
 */

public interface VerifyCallback<T> {
    void onSuccesd(T t);

    void onError(int errorType, String msg);

}
