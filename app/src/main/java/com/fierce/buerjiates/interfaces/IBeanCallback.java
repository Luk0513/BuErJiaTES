package com.fierce.buerjiates.interfaces;

/**
 * Created by Administrator on 2016-12-05.
 */

public interface IBeanCallback<T> {
    void onSuccesd(T t);

    void onError(String msg);

}
