package com.fierce.buerjiates.presents;

import com.fierce.buerjiates.interfaces.IBeanCallback;
import com.fierce.buerjiates.interfaces.IRequestInterface;
import com.fierce.buerjiates.models.RequestInterfaceModel;
import com.fierce.buerjiates.views.IgetVideoURL_View;

import org.json.JSONObject;

/**
 * @Author : Lukang
 * >>Live and learn<<
 * blog : http://blog.csdn.net/fight_0513
 * @PackegeName : com.fierce.buerjiates.presents
 * @ProjectName : BuErJiaTES
 * @Date :  2017-08-18
 */

public class IGteViedoURL_Present {

    IgetVideoURL_View view;

    IRequestInterface requestInterface;

    public IGteViedoURL_Present(IgetVideoURL_View view) {
        this.view = view;
        requestInterface = new RequestInterfaceModel();
    }


    public void getVideoURL(String admcNum) {
        requestInterface.getVideoURL(admcNum, new IBeanCallback<Object>() {
            @Override
            public void onSuccesd(Object o) {
                JSONObject jsonObject = (JSONObject) o;
                String videoURL = jsonObject.optString("filePath");
                int videoURLId = jsonObject.optInt("id");
                view.getURLSucceed(videoURL, videoURLId);
            }

            @Override
            public void onError(String msg) {
                view.getTGPriceFailure(msg);
            }
        });
    }


}
