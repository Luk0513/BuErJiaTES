package com.fierce.buerjiates.config;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.fierce.buerjiates.bean.GoodsList_Bean;
import com.fierce.buerjiates.https.HttpManage;
import com.fierce.buerjiates.https.HttpServerInterface;
import com.fierce.buerjiates.presents.IGetGoodsListPresent;
import com.fierce.buerjiates.services.DownAPKService;
import com.fierce.buerjiates.utils.SPHelper;
import com.fierce.buerjiates.utils.mlog;
import com.fierce.buerjiates.views.IGetGoodsListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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
        SPHelper spHelper = new SPHelper(getContext(), "ServerState");
        spHelper.clear();
        JPushInterface.setDebugMode(false);          // 设置开启日志,发布时请关闭日志
        JPushInterface.init(getApplicationContext());   // 初始化 JPush
    }

    public Context getContext() {
        return context;
    }

    //打开网络请求
    //返回Gson
    public Retrofit getGsonRetrofit() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(HttpServerInterface.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

    //返回字符串
    public Retrofit getStringRetrofit() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(HttpServerInterface.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
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
        return helper.getString(key);
    }

    public void saveGoodsInfoJson(String key, String json) {
        SPHelper spHelper = new SPHelper(getApplicationContext(), "GoodsInfoJson");
        spHelper.save(new SPHelper.ContentValue(key, json));
    }

    public String getGoodsInfoJson(String key) {
        SPHelper spHelper = new SPHelper(getApplicationContext(), "GoodsInfoJson");
        return spHelper.getString(key);


    }

    public void saveisRunning(boolean isRunning) {
        SPHelper spHelper = new SPHelper(getContext(), "ServerState");
        spHelper.save(new SPHelper.ContentValue("state", isRunning));
    }

    public boolean getisRunning() {
        SPHelper spHelper = new SPHelper(getContext(), "ServerState");
        return spHelper.getBoolean("state");
    }

    public void saveLotteryGift(String giftJson) {
        SPHelper spHelper = new SPHelper(getContext(), "Gifts");
        spHelper.save(new SPHelper.ContentValue("gift", giftJson));
    }

    public String getGiftJson() {
        SPHelper spHelper = new SPHelper(getContext(), "Gifts");
        return spHelper.getString("gift");
    }

    public void saveQR_Num(int QRNum) {
        SPHelper spHelper = new SPHelper(getContext(), "QRNUM");
        spHelper.save(new SPHelper.ContentValue("qr_num", QRNum));
    }

    public int getQR_Num() {
        SPHelper spHelper = new SPHelper(getContext(), "QRNUM");
        return spHelper.getInt("qr_num");
    }

    public void updateAPP() {
        //先检查本地文件夹是否存在
        final File downloadFile = new File(Environment.getExternalStorageDirectory(), "update");
        if (!downloadFile.mkdirs()) {
            try {
                downloadFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //下载apk
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://120.76.159.2:8090/admin/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        Call<String> call = retrofit.create(HttpManage.class).updateApp();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mlog.json(response.body());
                try {
                    JSONObject object = new JSONObject(response.body());
                    JSONArray array = object.getJSONArray("list");
                    JSONObject object2 = array.getJSONObject(0);
                    String versionTexe = object2.optString("versionText");
                    //服务器Apk 版本号
                    double versionCode = Double.parseDouble(versionTexe);
                    //本地应用版本号
                    double appCode = getAppVersion();
                    if (versionCode > appCode) {
                        //如果服务器上的版本号大于本地应用版本号 开始更新
                        if (!MyApp.getInstance().getisRunning()) {
                            //在下载apk文件前 先检查本地apk文件的版本号是否等于服务器上的版本号
                            //相等则直接安装 否则开始下载
                            File[] allFiles = new File(downloadFile.getAbsolutePath()).listFiles();
                            int apkCode = 0;
                            File apkfile = null;
                            if (allFiles.length > 0) {
                                apkfile = allFiles[0];
                                PackageInfo packageInfo = getPackageManager()
                                        .getPackageArchiveInfo(apkfile.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
                                try {
                                    apkCode = packageInfo.versionCode;//apk 版本号；
                                } catch (NullPointerException e) {
                                    //捕捉到空指针
                                }
                            }
                            if (apkCode == versionCode && apkfile != null) {
                                Intent in = new Intent("PupoState");
                                in.putExtra("isDone", true);
                                in.putExtra("apk", apkfile.getAbsolutePath());
                                sendBroadcast(in);
                            } else {
                                //后台下载Apk
                                String apkUrl = object2.optString("filePath");
                                Intent intent = new Intent(getBaseContext(), DownAPKService.class);
                                intent.putExtra("url", apkUrl);
                                startService(intent);
                            }
                        } else {
                            mlog.e("onResponse: KKKKKKKKKKKKKKKKKKKK 已经启动服务");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
//                Log.e("TAG", "onFailure: ::::::::::::::" + t.toString());
            }
        });
    }

    /**
     * 获取单个App版本号
     **/
    public double getAppVersion() {
        PackageManager pManager = this.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = pManager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int appVersion = packageInfo.versionCode;
        double version = Double.valueOf(appVersion).doubleValue();
        return version;
    }

    public void sendJpushBrocads() {
        Intent intent = new Intent("Jpush");
        intent.putExtra("Jp", "jp");
        getApplicationContext().sendBroadcast(intent);
    }


    public void getGoodsListBean(String categoryId, String admcNum) {
        IGetGoodsListPresent goodsListPresent = new IGetGoodsListPresent(new IGetGoodsListView() {
            @Override
            public void showToas_getGoodList(String msg) {

            }

            @Override
            public void getListSucceed(List<GoodsList_Bean.ListBean> goodsListBeans) {

            }

            @Override
            public void getListFailure(String msg) {

            }
        });
        goodsListPresent.getGoodsList(categoryId, admcNum);
    }

}
