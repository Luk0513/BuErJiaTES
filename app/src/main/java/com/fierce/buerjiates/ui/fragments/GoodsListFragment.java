package com.fierce.buerjiates.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fierce.buerjiates.R;
import com.fierce.buerjiates.adapters.GoodsSrcListAdapter;
import com.fierce.buerjiates.base.BaseFragment;
import com.fierce.buerjiates.bean.GoodsBean;
import com.fierce.buerjiates.bean.GoodsSort_Bean;
import com.fierce.buerjiates.config.MyApp;
import com.fierce.buerjiates.presents.IGetGoodsPresent;
import com.fierce.buerjiates.presents.IGetGoodsPricePresent;
import com.fierce.buerjiates.presents.IGetGoodsSortPresent;
import com.fierce.buerjiates.ui.GoodsShelfActivity;
import com.fierce.buerjiates.utils.EncodingUtils;
import com.fierce.buerjiates.utils.ImageCacheUtils;
import com.fierce.buerjiates.views.IGetGoodsInfoView;
import com.fierce.buerjiates.views.IGetGoodsPriceView;
import com.fierce.buerjiates.views.IGetGoodsSorView;
import com.google.gson.Gson;
import com.google.zxing.WriterException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/3/12.
 */

public class GoodsListFragment extends BaseFragment implements IGetGoodsSorView, IGetGoodsInfoView, View.OnTouchListener {
    private static final String TAG = "GoodsListFragment";

    @BindView(R.id.iv_image1)
    ImageView ivImage1;
    @BindView(R.id.iv_image5)
    ImageView ivImage5;
    @BindView(R.id.iv_image2)
    ImageView ivImage2;
    @BindView(R.id.iv_image3)
    ImageView ivImage3;
    @BindView(R.id.iv_image6)
    ImageView ivImage6;
    @BindView(R.id.iv_image4)
    ImageView ivImage4;

    private IGetGoodsSortPresent present;
    private IGetGoodsPresent getGoodsPresent;
    private List<GoodsSort_Bean.ListBean> listBeen;
    private List<GoodsBean.ListBean> goodsList;
    private ImageCacheUtils cacheUtils;
    private PopupWindow popupWindow;
    private GoodsSrcListAdapter srcListAdapter;

    @Override
    protected int getLayoutRes() {
        return R.layout.goodslayout;
    }

    @Override
    protected void initView() {
        present = new IGetGoodsSortPresent(this);
        getGoodsPresent = new IGetGoodsPresent(this);
        present.getGoodsSort();
        cacheUtils = new ImageCacheUtils(getActivity());
        popupWindow = new PopupWindow(getActivity());
        timer = new ICountDownTimer(20 * 1000, 1000);
        registrBodcast();
        initDetailsView();
    }

    @OnClick({R.id.iv_image1, R.id.iv_image5, R.id.iv_image2, R.id.iv_image3, R.id.iv_image6, R.id.iv_image4})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_image1:
                jumpActivity("1");
                break;
            case R.id.iv_image5:
                jumpActivity("5");
                break;
            case R.id.iv_image2:
                jumpActivity("2");
                break;
            case R.id.iv_image3:
                jumpActivity("3");
                break;
            case R.id.iv_image6:
                jumpActivity("6");
                break;
            case R.id.iv_image4:
                jumpActivity("4");
                break;
        }
    }

    private void jumpActivity(String categoryId) {
        String banner2Url;
        if (listBeen != null) {
            for (GoodsSort_Bean.ListBean bean : listBeen) {
                if (bean.getProductCategoryId().equals(categoryId)) {
                    if (bean.getState() == 1) {
                        banner2Url = bean.getPic();
                        Intent intent = new Intent(getActivity(), GoodsShelfActivity.class);
                        intent.putExtra("categoryId", categoryId);
                        intent.putExtra("bannerImage", banner2Url);
                        startActivity(intent);
                        break;
                    } else if (bean.getState() == 2) {
                        String goodsUrl = bean.getPic();
                        String goodsId = goodsUrl.substring(goodsUrl.indexOf("=") + 1);
                        Log.e(TAG, "jumpActivity: ++++++++++++++>>>" + goodsId);
                        if (!popupWindow.isShowing())
                            initPopuwindow(goodsId);
                        break;
                    }
                }
            }
        }
    }

    private void setIvImage(List<GoodsSort_Bean.ListBean> mListBeen) {
        for (GoodsSort_Bean.ListBean bean : mListBeen) {
            switch (bean.getProductCategoryId()) {
                case "1":
                    if (bean.getState() == 1)
                        cacheUtils.loadBitmaps(bean.getPic());
//                    cacheUtils.loadBitmaps(ivImage1, bean.getTaxCode(), null);
                    Glide.with(getActivity()).load(bean.getTaxCode())
                            .diskCacheStrategy(DiskCacheStrategy.RESULT).into(ivImage1);
                    break;
                case "2":
                    if (bean.getState() == 1)
                        cacheUtils.loadBitmaps(bean.getPic());
//                    cacheUtils.loadBitmaps(ivImage2, bean.getTaxCode(), null);
                    Glide.with(getActivity()).load(bean.getTaxCode())
                            .diskCacheStrategy(DiskCacheStrategy.RESULT).into(ivImage2);
                    break;
                case "3":
                    if (bean.getState() == 1)
                        cacheUtils.loadBitmaps(bean.getPic());
//                    cacheUtils.loadBitmaps(ivImage3, bean.getTaxCode(), null);
                    Glide.with(getActivity()).load(bean.getTaxCode())
                            .diskCacheStrategy(DiskCacheStrategy.RESULT).into(ivImage3);
                    break;
                case "4":
                    if (bean.getState() == 1)
                        cacheUtils.loadBitmaps(bean.getPic());
//                    cacheUtils.loadBitmaps(ivImage4, bean.getTaxCode(), null);
                    Glide.with(getActivity()).load(bean.getTaxCode())
                            .diskCacheStrategy(DiskCacheStrategy.RESULT).into(ivImage4);
                    break;
                case "5":
                    if (bean.getState() == 1)
                        cacheUtils.loadBitmaps(bean.getPic());
//                    cacheUtils.loadBitmaps(ivImage5, bean.getTaxCode(), null);
                    Glide.with(getActivity()).load(bean.getTaxCode())
                            .diskCacheStrategy(DiskCacheStrategy.RESULT).into(ivImage5);
                    break;
                case "6":
                    if (bean.getState() == 1)
                        cacheUtils.loadBitmaps(bean.getPic());
//                    cacheUtils.loadBitmaps(ivImage6, bean.getTaxCode(), null);
                    Glide.with(getActivity()).load(bean.getTaxCode())
                            .diskCacheStrategy(DiskCacheStrategy.RESULT).into(ivImage6);
                    break;
            }
        }
    }

    private ImageView ivGoodsPic;
    private ImageView ivCountryPic;
    private TextView tvCountryName;
    private ImageView close2;
    private TextView tvGoodsName;
    private TextView tvGoodsBrief;
    private TextView tvGoodsPrice;
    private ImageView ewm;
    private ListView listView;
    private View v;
    private GoodsBean.ListBean.ProJsonCodeBean proJsonCodeBean;

    private void initDetailsView() {
        v = View.inflate(getActivity(), R.layout.goods_details, null);
        tvCountryName = (TextView) v.findViewById(R.id.tv_countryName);
        tvGoodsName = (TextView) v.findViewById(R.id.tv_goodsName);
        tvGoodsBrief = (TextView) v.findViewById(R.id.tv_goodsBrief);
        tvGoodsPrice = (TextView) v.findViewById(R.id.tv_goodsPrice);
        close2 = (ImageView) v.findViewById(R.id.iv_close2);
        ewm = (ImageView) v.findViewById(R.id.iv_ewm);
        ivGoodsPic = (ImageView) v.findViewById(R.id.iv_goodsPic);
        ivCountryPic = (ImageView) v.findViewById(R.id.iv_countryPic);
        listView = (ListView) v.findViewById(R.id.lv_goodsSrc);
        listView.setOnTouchListener(this);
        popupWindow = new PopupWindow(v, 860, 1600, false);
        popupWindow.setAnimationStyle(R.style.popwin_anim_style);
    }

    private List<String> imgUrlList;

    private void initPopuwindow(String goodsId) {
        close2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                cacheUtils.removeCache();
                sendBrocat(false);
                timer.cancel();
            }
        });
        sendBrocat(true);
        timer.start();
        popupWindow.showAtLocation(getView(), Gravity.CENTER, 0, -120);
        getGoodsPresent.getGoodsInfo(goodsId);
    }

    private void setPopuContent() {
        tvCountryName.setText(proJsonCodeBean.getCountry_name());
        String countryLogo = proJsonCodeBean.getCountry_logo();
        if (!countryLogo.startsWith("http://")) {
            countryLogo = "http://fx.bejmall.com/data/ecycountrypic/" + countryLogo;
        }
        Glide.with(getActivity()).load(countryLogo).into(ivCountryPic);
        ivGoodsPic.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        if (checkNetworkState())
            Glide.with(this).load(proJsonCodeBean.getGoods_img()).into(ivGoodsPic);
        else {
            byte[] imagebyte = cacheUtils.getBitmapByte(proJsonCodeBean.getGoods_img());
            Glide.with(this).load(imagebyte).into(ivGoodsPic);
        }
        IGetGoodsPricePresent getGoodsPricePresent = new IGetGoodsPricePresent(new IGetGoodsPriceView() {
            @Override
            public void getPriceSucceed(String price) {
                tvGoodsPrice.setText("心动价：RMB" + price);
            }

            @Override
            public void getPriceFailure(String msg) {
                tvGoodsPrice.setText("价格：有惊喜！");
            }
        });
        if (proJsonCodeBean.getGoods_sn() != null) {
            getGoodsPricePresent.getGoodsPrice(proJsonCodeBean.getGoods_sn());
        }
        tvGoodsName.setText(proJsonCodeBean.getGoods_name());
        tvGoodsBrief.setText(proJsonCodeBean.getGoods_brief());
        String html = proJsonCodeBean.getGoods_desc();
        /**
         *网页爬虫 抓取页面数据
         */
        Document doc = Jsoup.parse(html);
//        Log.e(TAG, "onItemClick:(matcher.matches())1 " + doc.body().toString());
        Elements media = doc.select("[src]");
        imgUrlList = new ArrayList<>();
        for (Element src : media) {
            if (src.tagName().equals("img")) {
                imgUrlList.add(src.attr("src"));
            }
        }
        srcListAdapter = new GoodsSrcListAdapter(getActivity(), imgUrlList, cacheUtils, listView);
        listView.setAdapter(srcListAdapter);
        String goodsId = proJsonCodeBean.getGoods_id();
        String d_Id = MyApp.getInstance().getDevice_id();
        createQRcode(goodsId, d_Id);//二维码
    }

    /**
     * 二维码生成
     */
    private void createQRcode(String goodsId, String d_Id) {
        String shopUrl = "http://m.bejmall.com/app/index.php?i=4&c=entry" +
                "&m=ewei_shopv2&do=mobile&r=goods.detail&id=" + goodsId + "&d_id=" + d_Id;
        try {
            Bitmap bitmap = EncodingUtils.createQRCode(shopUrl, null, 160);
            ewm.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private ConnectivityManager manager;

    public boolean checkNetworkState() {
        boolean flag = false;
        //得到网络连接信息
        manager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        //去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        return flag;
    }

    @Override
    public void showToas_Sort(String msg) {

    }

    @Override
    public void getSortSucceed() {

    }

    @Override
    public void getSortFailure(String msg) {
        String json = MyApp.getInstance().getGoodSortJson();
        if (json != null) {
            Gson gson = new Gson();
            GoodsSort_Bean goodsSort_bean = gson.fromJson(json, GoodsSort_Bean.class);
            listBeen = goodsSort_bean.getList();
            setIvImage(listBeen);
        }

    }

    @Override
    public void setGoodsSortImage(List<GoodsSort_Bean.ListBean> goodsSortListBean) {
        listBeen = goodsSortListBean;
        setIvImage(goodsSortListBean);
    }

    @Override
    public void getGoodsInfoSucceed(List<GoodsBean.ListBean> goodsList) {
        /**
         * 获取数据成功
         */
        this.goodsList = goodsList;
        if (goodsList.size() != 0) {
            proJsonCodeBean = goodsList.get(0).getProJsonCode();
            setPopuContent();
        }
    }

    @Override
    public void getGoogsInfoFailure(String msg) {
        Log.e(TAG, "getGoogsInfoFailure: " + msg);
        String goodsInfoJson = MyApp.getInstance().getGoodsInfoJson(msg);
        Gson gson = new Gson();
        GoodsBean goodsBean = gson.fromJson(goodsInfoJson, GoodsBean.class);
        goodsList = goodsBean.getList();
        proJsonCodeBean = goodsList.get(0).getProJsonCode();
        setPopuContent();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                timer.cancel();
                break;
            case MotionEvent.ACTION_UP:
                timer.start();
                break;
        }
        return false;
    }

    private class ICountDownTimer extends CountDownTimer {
        private ICountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
                cacheUtils.removeCache();
            }
            sendBrocat(false);
            Intent intent = new Intent("PupoState");
            intent.putExtra("isshowDialog", true);
            getContext().sendBroadcast(intent);
        }
    }

    private ICountDownTimer timer;
    private IntentFilter intentFilter;
    private IBroadcastReceiver broadcastReceiver;

    private class IBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
                cacheUtils.removeCache();
                sendBrocat(false);
                timer.cancel();
            }
        }
    }

    private void registrBodcast() {
        intentFilter = new IntentFilter();
        intentFilter.addAction("dismmisPopu");
        broadcastReceiver = new IBroadcastReceiver();
        getContext().registerReceiver(broadcastReceiver, intentFilter);

    }

    private void sendBrocat(Boolean isShowe) {
        Intent intent = new Intent("PupoState");
        intent.putExtra("isPopuShowe", isShowe);
        getActivity().sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cacheUtils.cancelAllTasks();
        cacheUtils.removeCache();
        getContext().unregisterReceiver(broadcastReceiver);
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        cacheUtils.fluchCache();
    }

}
