package com.fierce.buerjiates.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.fierce.buerjiates.presents.IGetGoodsPricePresent;
import com.fierce.buerjiates.presents.IgetTuanGouPricePresent;
import com.fierce.buerjiates.utils.EncodingUtils;
import com.fierce.buerjiates.utils.ImageCacheUtils;
import com.fierce.buerjiates.views.IGetGoodsPriceView;
import com.fierce.buerjiates.views.IgetTuangouView;
import com.google.gson.Gson;
import com.google.zxing.WriterException;

import org.json.JSONObject;
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

public class GoodsShelfActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnTouchListener {
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
    private PopupWindow popupWindow;
    private List<GoodsList_Bean.ListBean> goodsListBean;
    private String goods_sn;
    private String d_Id;
    private ImageCacheUtils cacheUtils;
    private MyGridviewAdapter adapter;
    private Handler mHandler;

    @Override
    protected int getLayoutRes() {
        return R.layout.goods_shelf;
    }

    @Override
    protected void initView() {
        //自定义字体
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonnts/mnkt.TTF");
        categoryId = getIntent().getStringExtra("categoryId");
        goodsListBean = new ArrayList<>();
//        IGetGoodsListPresent present = new IGetGoodsListPresent(this);
        tvBackHome.setTypeface(typeface);
        mHandler = new MyHandler(this);
        mHandler.sendEmptyMessageDelayed(1, 20 * 1000);
        currentTime = System.currentTimeMillis();
        String banner2Image = getIntent().getStringExtra("bannerImage");
        cacheUtils = new ImageCacheUtils(this);
        Glide.with(this).load(cacheUtils.getBitmapByte(banner2Image))
                .diskCacheStrategy(DiskCacheStrategy.RESULT).into(ivAdpictuer);
        d_Id = MyApp.getInstance().getDevice_id();
//        Log.e(TAG, "initView: >>>" + d_Id);
        gvGoodslist.setOnTouchListener(this);
        getGoodsListBean(categoryId);
    }


    private void getGoodsListBean(String categoryId) {
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
            initDetailsView();
        }
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
    private ImageView ivGoodsPic;
    private ImageView ivCountryLogo;
    private TextView tvCountryName;
    private TextView tvGoodsName;
    private TextView tvTimeLimited;//限时时间
    private LinearLayout llTimelimit;
    private TextView tvGoodsBrief;
    private TextView tvGoodsPrice;
    private TextView tvMarketPrice;
    private ImageView ewm;
    private ListView listView;
    private ImageView close2;
    private long resultTime; //限时购时间差

    private void initDetailsView() {
        View v = View.inflate(this, R.layout.goods_details, null);
        tvCountryName = (TextView) v.findViewById(R.id.tv_countryName);
        tvGoodsName = (TextView) v.findViewById(R.id.tv_goodsName);
        tvGoodsBrief = (TextView) v.findViewById(R.id.tv_goodsBrief);
        llTimelimit = (LinearLayout) v.findViewById(R.id.ll_timeLimited);
        tvGoodsPrice = (TextView) v.findViewById(R.id.tv_goodsPrice);
        tvMarketPrice = (TextView) v.findViewById(R.id.tv_marketPrice);
        tvTimeLimited = (TextView) v.findViewById(R.id.tv_Timelimited);
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

    public boolean checkNetworkState() {
        boolean flag = false;
        //得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        //去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        return flag;
    }

    /**
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
//        Log.e(TAG, "onItemClick: " + position + " // " + id);
        String ctLogo;
        close2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                mHandler.removeCallbacks(runnable);
                cacheUtils.removeCache();
                ivPopuBg.setVisibility(View.GONE);
            }
        });
        if (!popupWindow.isShowing())
            popupWindow.showAtLocation(ivAdpictuer, Gravity.CENTER, 0, -120);
        ivPopuBg.setVisibility(View.VISIBLE);
        GoodsList_Bean.ListBean.ProJsonCodeBean proJsonCodeBean = goodsListBean.get(position).getProJsonCode();
        if (proJsonCodeBean.getCountry_logo() != null) {
            tvCountryName.setText(proJsonCodeBean.getCountry_name());
            if (proJsonCodeBean.getCountry_logo().startsWith("http://"))
                ctLogo = proJsonCodeBean.getCountry_logo();
            else
                ctLogo = "http://fx.bejmall.com/data/ecycountrypic/" + proJsonCodeBean.getCountry_logo();

            Glide.with(this).load(ctLogo).diskCacheStrategy(DiskCacheStrategy.RESULT).into(ivCountryLogo);
        } else {
            ivCountryLogo.setVisibility(View.INVISIBLE);
            tvCountryName.setVisibility(View.INVISIBLE);
        }

        //by lukas  限时购优惠价格 限时倒计时
        IGetGoodsPricePresent getGoodsPricePresent = new IGetGoodsPricePresent(new IGetGoodsPriceView() {

            @Override
            public void getPriceSucceed(Object object) {
                JSONObject jsonObject = (JSONObject) object;
                String price = "";
                String endTime = "";
                String marketPrice = jsonObject.optString("marketprice");
                if (categoryId.equals("2")) {
                    price = jsonObject.optString("xsg_price");
                    endTime = jsonObject.optString("timeend");
                    llTimelimit.setVisibility(View.VISIBLE);
                    if (Long.parseLong(endTime) * 1000 - System.currentTimeMillis() >= 0) {
                        resultTime = Long.parseLong(endTime) - System.currentTimeMillis() / 1000;
                        mHandler.postDelayed(runnable, 1000);
                    } else {
                        mHandler.removeCallbacks(runnable);
                        tvTimeLimited.setText("活动结束,下次早点来哦~");
                    }
                    tvGoodsPrice.setText("限时优惠：¥" + price);
                    tvMarketPrice.setVisibility(View.VISIBLE);
                    tvMarketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG); //中划线
                    tvMarketPrice.setText("原价：¥" + marketPrice);
                }
            }

            @Override
            public void getPriceFailure(String msg) {
                tvGoodsPrice.setText("价格：有惊喜！");
            }
        });


        IgetTuanGouPricePresent getTGPriceP = new IgetTuanGouPricePresent(new IgetTuangouView() {
            @Override
            public void getTGPriceSucceed(Object o) {
                JSONObject tgInfoJson = (JSONObject) o;
                tvGoodsName.setText(tgInfoJson.optString("title"));
                String goodsImgUrl = "http://fx.bejmall.com/" + tgInfoJson.optString("thumb");

                tvGoodsPrice.setText("拼团价：¥" + tgInfoJson.optString("groupsprice"));
                tvMarketPrice.setVisibility(View.VISIBLE);
                tvMarketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG); //中划线
                tvMarketPrice.setText("原价：¥" + tgInfoJson.optString("price"));
                //商品图片
                if (checkNetworkState()) {
                    Glide.with(GoodsShelfActivity.this).load(goodsImgUrl).into(ivGoodsPic);
                } else {
                    byte[] imagebyte = cacheUtils.getBitmapByte(goodsImgUrl);
                    Glide.with(GoodsShelfActivity.this).load(imagebyte).into(ivGoodsPic);
                }
            }

            @Override
            public void getTGPriceFailure(String msg) {

            }
        });
        switch (categoryId) {
            case "2":
                tvGoodsName.setText(proJsonCodeBean.getGoods_name());
                if (proJsonCodeBean.getGoods_sn() != null) {
                    getGoodsPricePresent.getGoodsPrice(proJsonCodeBean.getGoods_sn(), categoryId);
                }
                //商品图片
                if (checkNetworkState())
                    Glide.with(this).load(proJsonCodeBean.getGoods_img()).into(ivGoodsPic);
                else {
                    byte[] imagebyte = cacheUtils.getBitmapByte(proJsonCodeBean.getGoods_img());
                    Glide.with(this).load(imagebyte).into(ivGoodsPic);
                }
                break;
            case "5":
                if (proJsonCodeBean.getGoods_sn() != null) {
                    getTGPriceP.getTGPrice(proJsonCodeBean.getGoods_sn(), categoryId);
                }
                break;
            default:
                tvGoodsName.setText(proJsonCodeBean.getGoods_name());
                //商品图片
                if (checkNetworkState())
                    Glide.with(this).load(proJsonCodeBean.getGoods_img()).into(ivGoodsPic);
                else {
                    byte[] imagebyte = cacheUtils.getBitmapByte(proJsonCodeBean.getGoods_img());
                    Glide.with(this).load(imagebyte).into(ivGoodsPic);
                }
                tvGoodsPrice.setText("价格：¥" + proJsonCodeBean.getShop_price());
                break;
        }

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
                String url = src.attr("src");
                //后台数据有问题需要处理
                if (url.lastIndexOf("http:") != 0 && url.lastIndexOf("http:") != -1) {
                    String replaceText = url.substring(0, url.lastIndexOf("http:"));
                    url = url.replace(replaceText, "");
                }
                imgUrlList.add(url);
            }
        }

        GoodsSrcListAdapter srcListAdapter = new GoodsSrcListAdapter(this, imgUrlList, cacheUtils, listView);
        listView.setAdapter(srcListAdapter);
        String goodsId = proJsonCodeBean.getGoods_id();
        goods_sn = proJsonCodeBean.getGoods_sn();
        createQRcode(goodsId, d_Id);//二维码
    }

    /**
     * 二维码生成
     */
    private void createQRcode(String goodsId, String d_Id) {
        String m_id = MyApp.getInstance().getM_id();
        int mid = Integer.parseInt(m_id);
        String shopUrl;
        if (categoryId.equals("5")) {
            shopUrl = "http://m.bejmall.com/app/index.php?i=4&c=entry" +
                    "&m=ewei_shopv2&do=mobile&r=groups.goods&erw_gsn="
                    + goods_sn + "&d_id=" + d_Id + "&mid=" + mid;
        } else {
            shopUrl = "http://m.bejmall.com/app/index.php?i=4&c=entry" +
                    "&m=ewei_shopv2&do=mobile&r=goods.detail&id=" + goodsId
                    + "&d_id=" + d_Id + "&mid=" + mid;
        }
        try {
            Bitmap bitmap = EncodingUtils.createQRCode(shopUrl, null, 160);
            ewm.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
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

    //倒计时
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            resultTime--;
            String formatLongToTimeStr = formatLongToTimeStr(resultTime);
            tvTimeLimited.setText(formatLongToTimeStr);
            if (resultTime > 0) {
                mHandler.postDelayed(runnable, 1000);
            }
        }
    };

    public String formatLongToTimeStr(Long l) {
        long days = l / 60 / 60 / 24;
        long hour = l / 60 / 60 % 24;
        long minute = l / 60 % 60;
        long second = l % 60;
        return days + " 天 " + hour + " 小时 " + minute + " 分钟 " + second + " 秒 ";
    }

}