package com.fierce.buerjiates.adapters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fierce.buerjiates.R;
import com.fierce.buerjiates.config.MyApp;
import com.fierce.buerjiates.utils.ImageCacheUtils;
import com.fierce.buerjiates.utils.mlog;

import java.util.List;

/**
 * Created by win7 on 2017/3/18.
 */

public class BannerPagerAdapter extends PagerAdapter {

    private Context context;
    private List<View> mList;
    private List<String> imgUrl;
    //    private ImageCacheUtils cacheUtils;
    private View v;
    private ImageView imageView;

    public BannerPagerAdapter(Context context, List<View> mList, List<String> imgUrl, ImageCacheUtils cacheUtils) {
        this.context = context;
        this.mList = mList;
        this.imgUrl = imgUrl;
//        this.cacheUtils = cacheUtils;
        v = View.inflate(context, R.layout.banner_itemlayout, null);
        imageView = (ImageView) v.findViewById(R.id.iv_imag);
        ivClose = (ImageView) v.findViewById(R.id.iv_close3);
        popupWindow = new PopupWindow(v, 860, 1600, false);
        popupWindow.setAnimationStyle(R.style.popwin_anim_style);
        registrBodcast();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mList.get(position));
    }

    private PopupWindow popupWindow;
    private ImageView ivClose;

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        View v = mList.get(position);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null)
                    popupWindow.dismiss();
                timer.cancel();
                timer = null;
                sendBrocat2MianAct(false);
                sendBrocat2Banner(false);
            }
        });
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!popupWindow.isShowing()) {
                    popupWindow.showAtLocation(v, Gravity.CENTER, 0, -100);
                }
//                cacheUtils.loadBitmaps(imageView, imgUrl.get(position), null);
                Glide.with(MyApp.getInstance().getApplicationContext()).load(imgUrl.get(position))
                        .diskCacheStrategy(DiskCacheStrategy.RESULT).into(imageView);
                timer = new ICountDownTimer(15000, 1000);
                sendBrocat2MianAct(true);
                sendBrocat2Banner(true);
                timer.start();
            }
        });
        container.addView(mList.get(position));
        return mList.get(position);
    }

    //倒计时
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
                timer.cancel();
                timer = null;
            }
            sendBrocat2MianAct(false);
            sendBrocat2Banner(false);
            mlog.e("banner");
        }
    }

    private ICountDownTimer timer;
    private IntentFilter intentFilter;

    public IBroadcastReceiver getBroadcastReceiver() {
        return broadcastReceiver;
    }

    private IBroadcastReceiver broadcastReceiver;

    private class IBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
                sendBrocat2Banner(false);
                sendBrocat2MianAct(false);
                timer.cancel();
            }
        }
    }

    private void registrBodcast() {
        intentFilter = new IntentFilter();
        intentFilter.addAction("dismmisPopu");
        broadcastReceiver = new IBroadcastReceiver();
        context.registerReceiver(broadcastReceiver, intentFilter);

    }

    private void sendBrocat2MianAct(Boolean isShowe) {
        Intent intent = new Intent("PupoState");
        intent.putExtra("isPopuShowe", isShowe);
        context.sendBroadcast(intent);
    }

    private void sendBrocat2Banner(Boolean isShowe) {
        Intent intent = new Intent("BannerPupoState");
        intent.putExtra("isPopuShowe", isShowe);
        context.sendBroadcast(intent);
    }

}

