package com.fierce.buerjiates.models;

import android.support.annotation.NonNull;
import android.util.Log;

import com.fierce.buerjiates.bean.Banners_Bean;
import com.fierce.buerjiates.bean.GoodsBean;
import com.fierce.buerjiates.bean.GoodsList_Bean;
import com.fierce.buerjiates.bean.GoodsSort_Bean;
import com.fierce.buerjiates.config.MyApp;
import com.fierce.buerjiates.https.HttpManage;
import com.fierce.buerjiates.https.HttpServerInterface;
import com.fierce.buerjiates.interfaces.IBeanCallback;
import com.fierce.buerjiates.interfaces.IRequestInterface;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by win7 on 2017/3/6.
 */

public class RequestInterfaceModel implements IRequestInterface {
    private final static String TAG = "RequestInterfaceModel";

    @Override
    public void activeDevice(@NonNull final String device_Id, @NonNull String device_Key,
                             final IBeanCallback callback) {
        Retrofit retrofit = MyApp.getInstance().getStringRetrofit();

        Call<String> call = retrofit.create(HttpManage.class).activatedDve(device_Id, device_Key);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
//                Log.e("TAG", "onResponse:  返回结果+++_______::::::::::::::++++" + response.body());
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
//                    Log.e(TAG, "onResponse: :::::::::::::" + jsonObject.optString("objMessage"));
                    if (jsonObject.optString("resCode").equals("0")) {
                        JSONArray array = jsonObject.getJSONArray("listMassage");
                        JSONObject object2 = array.getJSONObject(0);
                        //分销商ID
                        String mid = object2.optString("mId");
//                    Log.e(TAG, "onResponse: >>>>>>>" + mid);
                        MyApp.getInstance().saveDevice_id(device_Id, mid);
                        callback.onSuccesd("设备激活成功");
                    } else {
                        if (jsonObject.optString("objMessage").equals("2")) {
                            callback.onError("2");
                        } else if (jsonObject.optString("objMessage").equals("0"))
                            callback.onError("0");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onError("服务器连接失败");
            }
        });
    }

    @Override
    public void getBanner(final IBeanCallback callback) {
        Retrofit retrofit = MyApp.getInstance().getGsonRetrofit();
        Call<Banners_Bean> call = retrofit.create(HttpManage.class).getBannerImage();
        call.enqueue(new Callback<Banners_Bean>() {
            @Override
            public void onResponse(Call<Banners_Bean> call, Response<Banners_Bean> response) {
                Gson gson = new Gson();
                try {
                    String json = gson.toJson(response.body());
                    MyApp.getInstance().saveBannerJson(json);
                    callback.onSuccesd(response.body());
                } catch (Exception e) {
                    callback.onError("获取数据失败");
                }
            }

            @Override
            public void onFailure(Call<Banners_Bean> call, Throwable t) {
                Log.e(TAG, "onFailure:getBanner " + t.toString());
                callback.onError("服务器连接失败");
            }
        });
    }

    @Override
    public void getGoodsSort(final IBeanCallback<GoodsSort_Bean> callback) {
        Retrofit retrofit = MyApp.getInstance().getGsonRetrofit();
        Call<GoodsSort_Bean> call = retrofit.create(HttpManage.class).getGoodsSort();
        call.enqueue(new Callback<GoodsSort_Bean>() {
            @Override
            public void onResponse(Call<GoodsSort_Bean> call, Response<GoodsSort_Bean> response) {
//                Log.e(TAG, "onResponse:getGoodsSort  " + response.body().toString());
                Gson gson = new Gson();
                String json = gson.toJson(response.body());
                MyApp.getInstance().saveGoodSortJson(json);
                callback.onSuccesd(response.body());
            }

            @Override
            public void onFailure(Call<GoodsSort_Bean> call, Throwable t) {
                Log.e(TAG, "onFailure:getGoodsSort  " + t.toString());
                callback.onError("服务器连接失败");
            }
        });
    }

    @Override
    public void getGoodsList(@NonNull final String categoryId, final IBeanCallback<GoodsList_Bean> callback) {
        Retrofit retrofit = MyApp.getInstance().getGsonRetrofit();
        Call<GoodsList_Bean> call = retrofit.create(HttpManage.class).getGoodsList(categoryId);
        final long t = System.currentTimeMillis();
        Log.e(TAG, "getGoodsList: ..........." + t);

        call.enqueue(new Callback<GoodsList_Bean>() {
            @Override
            public void onResponse(Call<GoodsList_Bean> call, Response<GoodsList_Bean> response) {
//                 Log.e(TAG, "onResponse:getGoodsList " + response.body().toString());
                Gson gson = new Gson();
                String json = gson.toJson(response.body());
                MyApp.getInstance().saveGoodsListJson(categoryId, json);
                Log.e(TAG, "onResponse: ,.........>>>>>>" + (System.currentTimeMillis() - t));
                callback.onSuccesd(response.body());
            }

            @Override
            public void onFailure(Call<GoodsList_Bean> call, Throwable t) {
                callback.onError("服务器连接失败");
            }
        });
    }

    @Override
    public void getGoodsInfo(@NonNull final String goodsId, final IBeanCallback<GoodsBean> callback) {
        Retrofit retrofit = MyApp.getInstance().getGsonRetrofit();
        Call<GoodsBean> call = retrofit.create(HttpManage.class).getGoodsInfo(goodsId);
        call.enqueue(new Callback<GoodsBean>() {
            @Override
            public void onResponse(Call<GoodsBean> call, Response<GoodsBean> response) {
//                Log.e(TAG, "onResponse: getGoodsInfo   " + response.body().toString());
                Gson gson = new Gson();
                String json = gson.toJson(response.body());
                MyApp.getInstance().saveGoodsInfoJson(goodsId, json);
                callback.onSuccesd(response.body());
            }

            @Override
            public void onFailure(Call<GoodsBean> call, Throwable t) {
                Log.e(TAG, "onFailure:getGoodsInfo  " + t.toString());
                callback.onError("服务器连接失败");
            }
        });
    }

    @Override
    public void getGoodsPrice(String c, String a, String key, final String goodsSn, String categoryId, final IBeanCallback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(800, TimeUnit.MILLISECONDS) //设置请求超时
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(HttpServerInterface.PRICE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();
        Call<String> call = retrofit.create(HttpManage.class).getGoodsPrice(c, a, key, goodsSn);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
//                Log.e(TAG, "onResponse:   ,.,.,.,.,.,.,.,.,,,," + response.body());
                if (response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        callback.onSuccesd(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                    callback.onError("价格数据丢失……");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onError("价格数据丢失……");
                Log.e(TAG, "onFailure: getGoodsPrice:::::::::::::::::::::::::" + t.toString());
            }
        });
    }

    @Override
    public void getTuanGouPrice(final String c, String a, String ukey, String goodsSn, String categoryId, final IBeanCallback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(800, TimeUnit.MILLISECONDS) //设置请求超时
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(HttpServerInterface.PRICE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();
        Call<String> call = retrofit.create(HttpManage.class).getTGprice(c, a, ukey, goodsSn);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    if (response.body() != null) {
                        JSONObject jsonObject = new JSONObject(response.body());
                        callback.onSuccesd(jsonObject);
                    } else
                        callback.onError("获取数据失败");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onError("网络错误");
            }
        });
    }


}
