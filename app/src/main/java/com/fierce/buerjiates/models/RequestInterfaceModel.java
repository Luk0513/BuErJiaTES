package com.fierce.buerjiates.models;

import android.support.annotation.NonNull;

import com.fierce.buerjiates.bean.Banners_Bean;
import com.fierce.buerjiates.bean.GiftsBean;
import com.fierce.buerjiates.bean.GoodsBean;
import com.fierce.buerjiates.bean.GoodsList_Bean;
import com.fierce.buerjiates.bean.GoodsSort_Bean;
import com.fierce.buerjiates.bean.ShopInfo_Bean;
import com.fierce.buerjiates.config.MyApp;
import com.fierce.buerjiates.https.HttpManage;
import com.fierce.buerjiates.https.HttpServerInterface;
import com.fierce.buerjiates.interfaces.IBeanCallback;
import com.fierce.buerjiates.interfaces.IRequestInterface;
import com.fierce.buerjiates.interfaces.VerifyCallback;
import com.fierce.buerjiates.utils.mlog;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by win7 on 2017/3/6.
 */

public class RequestInterfaceModel implements IRequestInterface {

    @Override
    public void activeDevice(@NonNull final String device_Id, @NonNull String device_Key,
                             final IBeanCallback callback) {
        Retrofit retrofit = MyApp.getInstance().getStringRetrofit();

        Call<String> call = retrofit.create(HttpManage.class).activatedDve(device_Id, device_Key);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    if (jsonObject.optString("resCode").equals("0")) {
                        JSONArray array = jsonObject.getJSONArray("listMassage");
                        JSONObject object2 = array.getJSONObject(0);
                        //分销商ID
                        String mid = object2.optString("mId");
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
                mlog.e(t.toString());
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
                mlog.e(t.toString());
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
                Gson gson = new Gson();
                String json = gson.toJson(response.body());
                MyApp.getInstance().saveGoodSortJson(json);
                callback.onSuccesd(response.body());
            }

            @Override
            public void onFailure(Call<GoodsSort_Bean> call, Throwable t) {
                mlog.e(t.toString());
                callback.onError("服务器连接失败");
            }
        });
    }

    @Override
    public void getGoodsList(@NonNull final String categoryId, @NonNull String admcNum, final IBeanCallback<GoodsList_Bean> callback) {
        Retrofit retrofit = MyApp.getInstance().getGsonRetrofit();
        Call<GoodsList_Bean> call = retrofit.create(HttpManage.class).getGoodsList(categoryId, admcNum);
        call.enqueue(new Callback<GoodsList_Bean>() {
            @Override
            public void onResponse(Call<GoodsList_Bean> call, Response<GoodsList_Bean> response) {
                Gson gson = new Gson();
                String json = gson.toJson(response.body());
                MyApp.getInstance().saveGoodsListJson(categoryId, json);
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
                Gson gson = new Gson();
                String json = gson.toJson(response.body());
                MyApp.getInstance().saveGoodsInfoJson(goodsId, json);
                callback.onSuccesd(response.body());
            }

            @Override
            public void onFailure(Call<GoodsBean> call, Throwable t) {
                mlog.e(t.toString());
                callback.onError("服务器连接失败");
            }
        });
    }

    @Override
    public void getGoodsPrice(String c, String a, String key, final String goodsSn, String categoryId, final IBeanCallback callback) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(HttpServerInterface.PRICE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        Call<String> call = retrofit.create(HttpManage.class).getGoodsPrice(c, a, key, goodsSn);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
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
                mlog.e(t.toString());
            }
        });
    }

    @Override
    public void getTuanGouPrice(final String c, String a, String ukey, String goodsSn, String categoryId, final IBeanCallback callback) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(HttpServerInterface.PRICE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
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
                mlog.e(t.toString());
                callback.onError("网络错误");
            }
        });
    }

    @Override
    public void getGifts(@NonNull String admcNum, final IBeanCallback<GiftsBean> callback) {

        Retrofit retrofit = MyApp.getInstance().getGsonRetrofit();
        Call<GiftsBean> call = retrofit.create(HttpManage.class).getGift(admcNum);
        call.enqueue(new Callback<GiftsBean>() {
            @Override
            public void onResponse(Call<GiftsBean> cal, Response<GiftsBean> response) {

                if (response.body() != null) {
                    if (response.body().getList() != null && response.body().getList().size() > 0) {
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body());
//                        mlog.e(json);
                        MyApp.getInstance().saveLotteryGift(json);
                        callback.onSuccesd(response.body());
                    } else {
                        mlog.json("");
                        callback.onError("数据集合为空");
                    }
                } else {
                    mlog.json("");
                    callback.onError("数据集合为空");
                }
            }

            @Override
            public void onFailure(Call<GiftsBean> call, Throwable t) {
                mlog.e(t);
                callback.onError(t.toString());
            }
        });
    }

    @Override
    public void verify(@NonNull String code, final VerifyCallback callback) {

        Retrofit retrofit = MyApp.getInstance().getStringRetrofit();

        Call<String> call = retrofit.create(HttpManage.class).verifycode(code);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    if (response.body() != null) {
                        JSONObject jsonObject = new JSONObject(response.body());
                        mlog.e(jsonObject.toString());
                        if (jsonObject.optInt("state") == 200) {
                            //验证成功
                            callback.onSuccesd("验证成功");
                        }
                        if (jsonObject.optInt("state") == 300) {
                            callback.onError(300, "验证失败");
                        }
                    } else {
                        callback.onError(300, "验证失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                mlog.e(t);
                callback.onError(0, "网络连接错误,请稍后再试");
            }
        });

    }

    @Override
    public void getShopInfo(@NonNull String device_num, final IBeanCallback<ShopInfo_Bean> callback) {
        Retrofit retrofit = MyApp.getInstance().getGsonRetrofit();
        Call<ShopInfo_Bean> call = retrofit.create(HttpManage.class).getShopInfo(device_num);
        call.enqueue(new Callback<ShopInfo_Bean>() {
            @Override
            public void onResponse(Call<ShopInfo_Bean> call, Response<ShopInfo_Bean> response) {

                mlog.e(response.body());
                if (response.body() != null && response.body().getList().size() > 0) {
                    MyApp.getInstance().saveM_id(response.body().getList().get(0).getMId());
                    callback.onSuccesd(response.body());
                } else {
                    callback.onError("获取数据失败");
                }
            }

            @Override
            public void onFailure(Call<ShopInfo_Bean> call, Throwable t) {
                callback.onError("网络异常");
                mlog.e(t);
            }
        });

    }

    @Override
    public void getVideoURL(@NonNull String admcNum, final IBeanCallback<Object> callback) {
        Retrofit retrofit = MyApp.getInstance().getStringRetrofit();
        Call<String> call = retrofit.create(HttpManage.class).getVideoURL(admcNum);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.body().equals("null") && response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
//                        String videoURL = jsonObject.optString("filePath");
                        callback.onSuccesd(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mlog.e("获取视频地址失败");
                    }
                } else {
                    mlog.e("获取视频地址失败");
                    callback.onError("获取视频地址失败");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                mlog.e(t);
                callback.onError("连接服务器出错");
            }
        });
    }


}
