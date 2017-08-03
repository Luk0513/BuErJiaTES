package com.fierce.buerjiates.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fierce.buerjiates.bean.GoodsBean;
import com.fierce.buerjiates.bean.GoodsList_Bean;
import com.fierce.buerjiates.bean.GoodsSort_Bean;
import com.fierce.buerjiates.config.MyApp;
import com.fierce.buerjiates.https.HttpManage;
import com.fierce.buerjiates.utils.ImageCacheUtils;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by win7 on 2017/3/21.
 */

public class LoadDataSevice extends IntentService {


    private ImageCacheUtils cacheUtils;

    public LoadDataSevice() {
        super("LoadDataSevice");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e("TAG", "onHandleIntent: ::::::::::::::::::::::后台服务");
        getBanner();
        getGoodsort();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        cacheUtils = new ImageCacheUtils(getApplicationContext());
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    private void getBanner() {
        Retrofit retrofit = MyApp.getInstance().getStringRetrofit();
        Call<String> call = retrofit.create(HttpManage.class).getBanner();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String bannerJson = response.body();
                MyApp.getInstance().saveBannerJson(bannerJson);
                // Log.e(TAG, "onResponse: service  getBanner" + bannerJson);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    private void getGoodsort() {
        Retrofit retrofit = MyApp.getInstance().getStringRetrofit();
        Call<String> call = retrofit.create(HttpManage.class).getGoodsSorts();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String goodSort = response.body();
                MyApp.getInstance().saveGoodSortJson(goodSort);
                Gson gson = new Gson();
                GoodsSort_Bean goodsSort_bean = gson.fromJson(goodSort, GoodsSort_Bean.class);
                int i = 1;
                for (GoodsSort_Bean.ListBean listBean : goodsSort_bean.getList()) {

                    if (listBean.getState() == 1) {
                        getGoodsList("" + i, MyApp.getInstance().getDevice_id());
                        i++;
                    } else {
                        String goodsUrl = listBean.getPic();
                        String goodsId = goodsUrl.substring(goodsUrl.indexOf("=") + 1);
                        getGoodsInfo(goodsId);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    List<String> imgUrlList;

    /**
     * @param id 分类 id
     *           有不同的分类 所有要与不同的key    这里的id 即为key
     */
    private void getGoodsList(final String id, String admcNum) {
        Retrofit retrofit = MyApp.getInstance().getStringRetrofit();
        Call<String> call = retrofit.create(HttpManage.class).getGoodsLists(id, admcNum);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String goodsLisJson = response.body();
                MyApp.getInstance().saveGoodsListJson(id, goodsLisJson);
                //json 数据转对象；
                Gson son = new Gson();
                GoodsList_Bean bean = son.fromJson(goodsLisJson, GoodsList_Bean.class);
                for (GoodsList_Bean.ListBean listBean : bean.getList()) {
                    String goods_img = listBean.getProJsonCode().getGoods_img();
                    String goods_desc = listBean.getProJsonCode().getGoods_desc();
                    cacheUtils.loadBitmaps(goods_img);
                    /**
                     *网页爬虫 抓取页面数据
                     */
                    Document doc = Jsoup.parse(goods_desc);
//                    Log.e(TAG, "getGoodsInfo  " + doc.body().toString());
                    Elements media = doc.select("[src]");
                    imgUrlList = new ArrayList<>();
                    for (Element src : media) {
                        if (src.tagName().equals("img")) {
                            imgUrlList.add(src.attr("src"));
                        }
                    }
                    for (String imgUrl : imgUrlList) {
                        cacheUtils.loadBitmaps(imgUrl);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    List<String> imgUrlList1;

    private void getGoodsInfo(final String goodId) {

        Retrofit retrofit = MyApp.getInstance().getStringRetrofit();
        Call<String> call = retrofit.create(HttpManage.class).getGoodsInfos(goodId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String goodsInfo = response.body().toString();
                MyApp.getInstance().saveGoodsInfoJson(goodId, goodsInfo);
                Gson gson = new Gson();
                GoodsBean bean = gson.fromJson(goodsInfo, GoodsBean.class);
                for (GoodsBean.ListBean listBean : bean.getList()) {
                    String html = listBean.getProJsonCode().getGoods_desc();
                    /**
                     *网页爬虫 抓取页面数据
                     */
                    Document doc = Jsoup.parse(html);
//                    Log.e(TAG, "getGoodsInfo  " + doc.body().toString());
                    Elements media = doc.select("[src]");
                    imgUrlList1 = new ArrayList<>();
                    for (Element src : media) {
                        if (src.tagName().equals("img")) {
                            imgUrlList1.add(src.attr("src"));
                        }
                    }
                    for (String imgUrl : imgUrlList1) {
                        cacheUtils.loadBitmaps(imgUrl);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
}