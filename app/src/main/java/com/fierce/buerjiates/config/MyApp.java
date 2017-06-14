package com.fierce.buerjiates.config;

import android.app.Application;
import android.content.Context;

import com.fierce.buerjiates.https.HttpServerInterface;
import com.fierce.buerjiates.utils.SPHelper;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


/**
 * 保存设备的 ID
 * <p>
 * Created by win7 on 2017/3/1.
 */

public class MyApp extends Application {

    private static volatile MyApp instance;
    private static Context context;

    public static MyApp getInstance() {
        if (instance == null) {
            synchronized (MyApp.class) {
                if (instance == null)
                    instance = new MyApp();
            }
        }
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        context = getApplicationContext();
    }

    public Context getContext() {
        return context;
    }

    //打开网络请求
    //返回Gson
    public Retrofit getGsonRetrofit() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(400, TimeUnit.MILLISECONDS) //设置请求超时
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(HttpServerInterface.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return retrofit;
    }

    //返回字符串
    public Retrofit getStringRetrofit() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(400, TimeUnit.MILLISECONDS) //设置请求超时
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(HttpServerInterface.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();
        return retrofit;
    }


    //设备ID
    private String device_id = null;

    private String videoName = null;

    public String getVideoName() {
        return videoName;
    }
    //数据库

    public String getDevice_id() {
        return device_id;
    }

    public String getM_id() {
        SPHelper spHelper = new SPHelper(this, "M_id");
        return spHelper.getString("mid");
    }

    //保存数据
    public void saveDevice_id(String device_id, String mid) {
        this.device_id = device_id;
        SPHelper sp = new SPHelper(this, "Device_id");
        sp.save(new SPHelper.ContentValue("d_id", device_id));
        SPHelper spHelper = new SPHelper(this, "M_id");
        spHelper.save(new SPHelper.ContentValue("mid", mid));
    }

    //清除数据
    public void cleanDevice_id() {
        SPHelper sp = new SPHelper(this, "Device_id");
        sp.clear();
        device_id = null;
    }

    //是否激活设备
    public boolean isActivateDevice() {
        SPHelper spHelper = new SPHelper(this, "Device_id");
        if (spHelper != null) {
            device_id = spHelper.getString("d_id");
            return device_id != null && device_id.length() > 0;
        } else {
            return false;
        }
    }


    public void saveVideoPosition(int position) {
        SPHelper helper = new SPHelper(getApplicationContext(), "VideoPosit");
        helper.save(new SPHelper.ContentValue("position", position));
    }

    public int getVideoPosition() {
        SPHelper helper = new SPHelper(getApplicationContext(), "VideoPosit");
        if (helper != null)
            return helper.getInt("position");
        return 0;
    }


    /////////////////////////////////////////////
    public void saveBannerJson(String json) {
        SPHelper helper = new SPHelper(getApplicationContext(), "BannerJson");
        helper.save(new SPHelper.ContentValue("BannerJson", json));
    }

    public String getBannerJson() {
        SPHelper helper = new SPHelper(getApplicationContext(), "BannerJson");
        if (helper != null) {
            String json = helper.getString("BannerJson");
            return json;
        }
        return null;
    }

    public void saveGoodSortJson(String json) {
        SPHelper helper = new SPHelper(getApplicationContext(), "GoodSortJson");
        helper.save(new SPHelper.ContentValue("GoodSortJson", json));
    }

    public String getGoodSortJson() {
        SPHelper helper = new SPHelper(getApplicationContext(), "GoodSortJson");
        if (helper != null) {
            String json = helper.getString("GoodSortJson");
            return json;
        }
        return null;
    }

    public void saveGoodsListJson(String key, String json) {
        SPHelper helper = new SPHelper(getApplicationContext(), "GoodsListJson");
        helper.save(new SPHelper.ContentValue(key, json));
    }

    public String getGoodsListJson(String key) {
        SPHelper helper = new SPHelper(getApplicationContext(), "GoodsListJson");
        if (helper != null) {
            String json = helper.getString(key);
            return json;
        }
        return null;
    }

    public void saveGoodsInfoJson(String key, String json) {
        SPHelper spHelper = new SPHelper(getApplicationContext(), "GoodsInfoJson");
        spHelper.save(new SPHelper.ContentValue(key, json));
    }

    public String getGoodsInfoJson(String key) {
        SPHelper spHelper = new SPHelper(getApplicationContext(), "GoodsInfoJson");
        if (spHelper != null) {
            String json = spHelper.getString(key);
            return json;
        }
        return null;
    }

    public void saveApKVersionCode(String code) {
        SPHelper spHelper = new SPHelper(getApplicationContext(), "ApkVersion");
        spHelper.save(new SPHelper.ContentValue("apkVersion", code));
    }

    public String getVersionCode() {
        SPHelper spHelper = new SPHelper(getApplicationContext(), "ApkVersion");
        return spHelper.getString("apkVersion");
    }
}
