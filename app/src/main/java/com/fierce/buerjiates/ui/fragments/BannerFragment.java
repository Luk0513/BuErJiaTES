package com.fierce.buerjiates.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fierce.buerjiates.R;
import com.fierce.buerjiates.adapters.BannerPagerAdapter;
import com.fierce.buerjiates.base.BaseFragment;
import com.fierce.buerjiates.bean.Banners_Bean;
import com.fierce.buerjiates.config.MyApp;
import com.fierce.buerjiates.presents.IGetBannerPresent;
import com.fierce.buerjiates.utils.ImageCacheUtils;
import com.fierce.buerjiates.views.IGetBannerView;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by win7 on 2017/3/27.
 */

public class BannerFragment extends BaseFragment implements IGetBannerView, ViewPager.OnPageChangeListener {

    @BindView(R.id.vp_banner)
    ViewPager vpBanner;
    @BindView(R.id.hid_banner)
    ImageView hidImage;
    //啊大家看好看见
    private List<View> mList = new ArrayList<>();
    private List<Banners_Bean.ListBean> listBeen;
    private BannerPagerAdapter adapter;
    private MyHandler handler;
    private ImageCacheUtils cacheUtils;
    private IGetBannerPresent bannerPresent;

    @Override
    protected int getLayoutRes() {
        return R.layout.banner_layout;
    }

    @Override
    protected void initView() {
        cacheUtils = new ImageCacheUtils(getContext());
        bannerPresent = new IGetBannerPresent(this);
        bannerPresent.getBanners();
        vpBanner.addOnPageChangeListener(this);
        handler = new MyHandler(this);
        registrBodcast();

    }

    @Override
    public void getBannerSucceed(List<Banners_Bean.ListBean> banerListBean) {
        listBeen = banerListBean;
        initImagView();
    }

    @Override
    public void getBannerFailure(String msg) {
        String bannerJson = MyApp.getInstance().getBannerJson();
        Gson gson = new Gson();
        Banners_Bean banners_bean = gson.fromJson(bannerJson, Banners_Bean.class);
        if (banners_bean != null) {
            listBeen = banners_bean.getList();
            initImagView();
        } else
            showToast("服务器错误");
    }

    private void initImagView() {
        ImageView imageView1;
        List<String> urlList = new ArrayList<>();
        List<String> imgUrl = new ArrayList<>();
        for (Banners_Bean.ListBean bean : listBeen) {
//            Log.e("TAG", "initImagView: ;：：：：：：：：：：：：：：：" + bean.getAdmcId());
            if (MyApp.getInstance().getDevice_id() != null) {
                if (MyApp.getInstance().getDevice_id().equals(bean.getAdmcNum())) {
                    urlList.add(bean.getAd_img());
                    imgUrl.add(bean.getPic());
                }
            }
        }
        if (urlList.size() > 0) {
            urlList.add(0, urlList.get(urlList.size() - 1));
            imgUrl.add(0, imgUrl.get(imgUrl.size() - 1));
            urlList.add(urlList.get(1));
            imgUrl.add(imgUrl.get(1));
            for (int i = 0; i < urlList.size(); i++) {
                imageView1 = new ImageView(getContext());
                imageView1.setScaleType(ImageView.ScaleType.FIT_XY);
                cacheUtils.loadBitmaps(imgUrl.get(i));
                Glide.with(this).load(urlList.get(i)).diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(imageView1);
                mList.add(imageView1);
            }
            adapter = new BannerPagerAdapter(getContext(), mList, imgUrl, cacheUtils);
            vpBanner.setAdapter(adapter);
            vpBanner.setCurrentItem(1);
            handler.sendEmptyMessageDelayed(1, 4000);
        } else {
            hidImage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position == mList.size() - 1) {
            vpBanner.setCurrentItem(1, false);
        }
        if (position == 0) {
            if (positionOffset == 0) {
                vpBanner.setCurrentItem(mList.size() - 2, false);
            }
        }
    }


    @Override
    public void onPageSelected(final int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE)
            if (!handler.hasMessages(1))
                handler.sendEmptyMessageDelayed(1, 4000);
    }

    private void setVpCurrtPager() {
        vpBanner.setCurrentItem(vpBanner.getCurrentItem() + 1);
        if (!handler.hasMessages(1))
            handler.sendEmptyMessageDelayed(1, 4000);
    }

    private static class MyHandler extends Handler {
        private WeakReference<BannerFragment> mWeakReference;

        private MyHandler(BannerFragment fragment) {
            mWeakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mWeakReference.get() == null)
                return;
            switch (msg.what) {
                case 1:
                    mWeakReference.get().setVpCurrtPager();
                    break;
                case 2:
                    mWeakReference.get().bannerPresent.getBanners();
                    mWeakReference.get().handler.sendEmptyMessageDelayed(2, 1000 * 60);
                    break;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        cacheUtils.fluchCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(broadcastReceiver);
        getContext().unregisterReceiver(adapter.getBroadcastReceiver());
        handler.removeCallbacksAndMessages(null);
    }

    private IntentFilter intentFilter;
    private IBroadcastReceiver broadcastReceiver;

    private class IBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean bannerPopu = intent.getBooleanExtra("isPopuShowe", false);
            if (bannerPopu) {
                if (handler.hasMessages(1))
                    handler.removeMessages(1);
            } else {
                if (!handler.hasMessages(1))
                    handler.sendEmptyMessageDelayed(1, 4000);
            }
        }
    }

    private void registrBodcast() {
        intentFilter = new IntentFilter();
        intentFilter.addAction("BannerPupoState");
        broadcastReceiver = new IBroadcastReceiver();
        getContext().registerReceiver(broadcastReceiver, intentFilter);
    }
}
