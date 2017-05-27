package com.fierce.buerjiates.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fierce.buerjiates.R;
import com.fierce.buerjiates.adapters.GoodsSrcListAdapter;
import com.fierce.buerjiates.adapters.MyGridviewAdapter;
import com.fierce.buerjiates.base.BaseActivity;
import com.fierce.buerjiates.bean.GoodsList_Bean;
import com.fierce.buerjiates.config.MyApp;
import com.fierce.buerjiates.presents.IGetGoodsListPresent;
import com.fierce.buerjiates.presents.IGetGoodsPricePresent;
import com.fierce.buerjiates.utils.EncodingUtils;
import com.fierce.buerjiates.utils.ImageCacheUtils;
import com.fierce.buerjiates.views.IGetGoodsListView;
import com.fierce.buerjiates.views.IGetGoodsPriceView;
import com.google.gson.Gson;
import com.google.zxing.WriterException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by win7 on 2017/3/13.
 */

public class GoodsShelfActivity extends BaseActivity implements IGetGoodsListView, AdapterView.OnItemClickListener, View.OnTouchListener {
    private final String TAG = "GoodsShelfActivity";
    @BindView(R.id.tv_backHome)
    TextView tvBackHome;
    @BindView(R.id.gv_goodslist)
    GridView gvGoodslist;
    @BindView(R.id.iv_adpictuer)
    ImageView ivAdpictuer;
    @BindView(R.id.iv_popuBg)
    ImageView ivPopuBg;

    private String categoryId;
    private String banner2Image;
    private PopupWindow popupWindow;
    private IGetGoodsListPresent present;
    private List<GoodsList_Bean.ListBean> goodsListBean;
    private String goodsId;
    private String d_Id;
    private String shopUrl;
    private ImageCacheUtils cacheUtils;
    private MyGridviewAdapter adapter;
    private GoodsSrcListAdapter srcListAdapter;
    private Handler mHandler;
    private Typeface typeface;

    @Override
    protected int getLayoutRes() {
        return R.layout.goods_shelf;
    }

    @Override
    protected void initView() {
        //自定义字体
        goodsListBean = new ArrayList<>();
        typeface = Typeface.createFromAsset(getAssets(), "fonnts/mnkt.TTF");
        tvBackHome.setTypeface(typeface);
        mHandler = new MyHandler(this);
        mHandler.sendEmptyMessageDelayed(1, 20 * 1000);
        currentTime = System.currentTimeMillis();
        categoryId = getIntent().getStringExtra("categoryId");
        banner2Image = getIntent().getStringExtra("bannerImage");

        cacheUtils = new ImageCacheUtils(this);
        present = new IGetGoodsListPresent(this);
        Glide.with(this).load(cacheUtils.getBitmapByte(banner2Image))
                .diskCacheStrategy(DiskCacheStrategy.RESULT).into(ivAdpictuer);
        present.getGoodsList();
        d_Id = MyApp.getInstance().getDevice_id();
        initDetailsView();
        gvGoodslist.setOnTouchListener(this);
    }

    @Override
    public void showToas_getGoodList(String msg) {
    }

    @Override
    public void getListSucceed() {
    }

    /**
     * @param msg 没有网络时 获取本地JSon数据
     */
    @Override
    public void getListFailure(String msg) {
        String goodsListJson = MyApp.getInstance().getGoodsListJson(categoryId);
        Gson gson = new Gson();
        if (goodsListJson != null) {
            GoodsList_Bean goodsList_bean = gson.fromJson(goodsListJson, GoodsList_Bean.class);
            for (GoodsList_Bean.ListBean bean : goodsList_bean.getList()) {
                if (bean.getAdmcNum().equals(MyApp.getInstance().getDevice_id())) {
                    goodsListBean.add(bean);
                }
            }
            adapter = new MyGridviewAdapter(getApplicationContext(), goodsListBean, gvGoodslist, cacheUtils);
            gvGoodslist.setAdapter(adapter);
            gvGoodslist.setOnItemClickListener(this);
        }
    }

    @Override
    public String getGoodsCategoryId() {
        return categoryId;
    }

    @Override
    public void setGoodsListView(List<GoodsList_Bean.ListBean> goodsListBean) {
        for (GoodsList_Bean.ListBean bean : goodsListBean) {
            if (bean.getAdmcNum().equals(MyApp.getInstance().getDevice_id())) {
                this.goodsListBean.add(bean);
            }
        }
        adapter = new MyGridviewAdapter(getApplicationContext(), this.goodsListBean, gvGoodslist, cacheUtils);
        gvGoodslist.setAdapter(adapter);
        gvGoodslist.setOnItemClickListener(this);
    }

    private long currentTime;

    @Override
    public void onBackPressed() {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //结束计时
                currentTime = System.currentTimeMillis() - currentTime;
                if (currentTime < 1000 * 20) {
                    if (mHandler.hasMessages(1))
                        mHandler.removeMessages(1);
                }
                break;
            case MotionEvent.ACTION_UP:
                //开始计时
                currentTime = System.currentTimeMillis();
                mHandler.sendEmptyMessageDelayed(1, 20 * 1000);
                break;
        }
        return false;
    }

    @OnClick({R.id.tv_backHome, R.id.iv_popuBg})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_backHome:
                if (popupWindow == null) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    if (!popupWindow.isShowing()) {
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    }
                }
                break;
            case R.id.iv_popuBg:
                if (!popupWindow.isShowing()) {
                    ivPopuBg.setVisibility(View.GONE);
                    cacheUtils.removeCache();
                }
                break;
        }
    }

    private void backHoem() {
        startActivity(new Intent(GoodsShelfActivity.this, MainActivity.class));
        finish();
    }

    //todo Handler////////////////////////////////////////////////////////////////////////////////
    private static class MyHandler extends Handler {
        private WeakReference<GoodsShelfActivity> mWeakReference;

        private MyHandler(GoodsShelfActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mWeakReference.get() == null)
                return;
            switch (msg.what) {
                case 1:
                    mWeakReference.get().backHoem();
                    break;
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    ImageView ivGoodsPic;
    ImageView ivCountryLogo;
    TextView tvCountryName;
    TextView tvGoodsName;
    TextView tvGoodsBrief;
    TextView tvGoodsPrice;
    ImageView ewm;
    ListView listView;
    View v;
    ImageView close2;
    GoodsList_Bean.ListBean.ProJsonCodeBean proJsonCodeBean;

    private void initDetailsView() {
        v = View.inflate(this, R.layout.goods_details, null);
        tvCountryName = (TextView) v.findViewById(R.id.tv_countryName);
        tvGoodsName = (TextView) v.findViewById(R.id.tv_goodsName);
        tvGoodsBrief = (TextView) v.findViewById(R.id.tv_goodsBrief);
        tvGoodsPrice = (TextView) v.findViewById(R.id.tv_goodsPrice);
        close2 = (ImageView) v.findViewById(R.id.iv_close2);
        ewm = (ImageView) v.findViewById(R.id.iv_ewm);
        ivGoodsPic = (ImageView) v.findViewById(R.id.iv_goodsPic);
        ivCountryLogo = (ImageView) v.findViewById(R.id.iv_countryPic);
        listView = (ListView) v.findViewById(R.id.lv_goodsSrc);
        listView.setOnTouchListener(this);
        popupWindow = new PopupWindow(v, 840, 1600, false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setAnimationStyle(R.style.popwin_anim_style);
    }

    private ConnectivityManager manager;

    public boolean checkNetworkState() {
        boolean flag = false;
        //得到网络连接信息
        manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        //去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        return flag;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e(TAG, "onItemClick: " + position + " // " + id);
        String ctLogo;
        close2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                cacheUtils.removeCache();
                ivPopuBg.setVisibility(View.GONE);
            }
        });
        if (!popupWindow.isShowing())
            popupWindow.showAtLocation(ivAdpictuer, Gravity.CENTER, 0, -120);
        ivPopuBg.setVisibility(View.VISIBLE);
        proJsonCodeBean = goodsListBean.get(position).getProJsonCode();
        tvCountryName.setText(proJsonCodeBean.getCountry_name());
        if (proJsonCodeBean.getCountry_logo().startsWith("http://"))
            ctLogo = proJsonCodeBean.getCountry_logo();
        else
            ctLogo = "http://fx.bejmall.com/data/ecycountrypic/" + proJsonCodeBean.getCountry_logo();
        Glide.with(this).load(ctLogo).diskCacheStrategy(DiskCacheStrategy.RESULT).into(ivCountryLogo);
        if (checkNetworkState())
            Glide.with(this).load(proJsonCodeBean.getGoods_img()).into(ivGoodsPic);
        else {
            byte[] imagebyte = cacheUtils.getBitmapByte(proJsonCodeBean.getGoods_img());
            Glide.with(this).load(imagebyte).into(ivGoodsPic);
        }

        IGetGoodsPricePresent getGoodsPricePresent = new IGetGoodsPricePresent(new IGetGoodsPriceView() {
            @Override
            public void getPriceSucceed(String price) {
                Log.e(TAG, "getPriceSucceed: " + price);
                tvGoodsPrice.setText("心动价：RMB" + price);
            }

            @Override
            public void getPriceFailure(String msg) {
                tvGoodsPrice.setText("价格：有惊喜！");
            }
        });
        if (proJsonCodeBean.getGoods_sn() != null) {
            Log.e(TAG, "onItemClick: +" + categoryId);
            getGoodsPricePresent.getGoodsPrice(proJsonCodeBean.getGoods_sn(), categoryId);
        }
        tvGoodsName.setText(proJsonCodeBean.getGoods_name());
        tvGoodsBrief.setText(proJsonCodeBean.getGoods_brief());
        String html = proJsonCodeBean.getGoods_desc();
        /**
         *网页爬虫 抓取页面数据
         */
        Document doc = Jsoup.parse(html);
        Elements media = doc.select("[src]");
        List<String> imgUrlList = new ArrayList<>();
        for (Element src : media) {
            if (src.tagName().equals("img")) {
                imgUrlList.add(src.attr("src"));
            }
        }
        srcListAdapter = new GoodsSrcListAdapter(this, imgUrlList, cacheUtils, listView);
        listView.setAdapter(srcListAdapter);
        goodsId = proJsonCodeBean.getGoods_id();
        createQRcode(goodsId, d_Id);//二维码
    }

    /**
     * 二维码生成
     */
    private void createQRcode(String goodsId, String d_Id) {
        shopUrl = "http://m.bejmall.com/app/index.php?i=4&c=entry" +
                "&m=ewei_shopv2&do=mobile&r=goods.detail&id=" + goodsId + "&d_id=" + d_Id;
        try {
            Bitmap bitmap = EncodingUtils.createQRCode(shopUrl, null, 160);
            ewm.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cacheUtils.fluchCache();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            ivPopuBg.setVisibility(View.GONE);
        }
        cacheUtils.cancelAllTasks();
        cacheUtils.removeCache();
        mHandler.removeCallbacksAndMessages(null);
        finish();
    }

}