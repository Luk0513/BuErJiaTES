package com.fierce.buerjiates.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.fierce.buerjiates.R;
import com.fierce.buerjiates.utils.EncodingUtils;
import com.fierce.buerjiates.widget.CustomDialog;
import com.fierce.buerjiates.widget.DashboardView;
import com.fierce.buerjiates.widget.HighlightCR;
import com.google.zxing.WriterException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author : Lukang
 * >>Live and learn<<
 * blog : http://blog.csdn.net/fight_0513
 * @PackegeName : com.fierce.buerjiates.ui
 * @ProjectName : BuErJiaTES
 * @Date :  2017-10-13
 */

public class ElectronicScale_Activity extends AppCompatActivity {
    @BindView(R.id.img_monkey)
    ImageView imgMonkey;
    @BindView(R.id.img_v)
    ImageView imgV;
    @BindView(R.id.tv_close)
    TextView tvClose;
    @BindView(R.id.dashboard_view_4)
    DashboardView dashboardView4;
    @BindView(R.id.tv_reader)
    TextView tvReader;
    @BindView(R.id.tv_readerB)
    TextView tvReaderB;
    @BindView(R.id.tv_readerZ)
    TextView tvReaderZ;
    @BindView(R.id.tv_readerBD)
    TextView tvReaderBD;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideNavigationBar();
        setContentView(R.layout.scales_layout);
        ButterKnife.bind(this);
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        setDashboardview();
        showeAnim();
        initResDialog();


        tvReader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(new Intent("reader"));
            }
        });
        tvReaderZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(new Intent("readerZ"));
            }
        });
    }


    private void showeAnim() {
        ValueAnimator anim = ValueAnimator.ofFloat(getResources().getDisplayMetrics().heightPixels, 0);
        anim.setDuration(1000 * 2);
        anim.setInterpolator(new LinearInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float x = (float) animation.getAnimatedValue();
                imgMonkey.setTranslationY(x);
            }
        });
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                AnimationDrawable anim = (AnimationDrawable) imgV.getBackground();
                anim.start();
                dashboardView4.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();


        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRes();
            }
        });
    }


    Dialog resultrDialog;
    float screenWidth;
    ResHolder resHolder;

    private void initResDialog() {
        View view = View.inflate(this, R.layout.test_result_layout, null);
        resHolder = new ResHolder(view);
        resultrDialog = new CustomDialog.Builder(this)
                .setIsFloating(false)
                .setIsFullscreen(true)
                .setView(view)
                .build();
    }

    private void showRes() {
        resultrDialog.show();
        ValueAnimator animatorT = ValueAnimator.ofFloat(screenWidth, -200);
        animatorT.setDuration(1000 * 240);
        animatorT.setInterpolator(new LinearInterpolator());
        animatorT.setRepeatCount(-1);
        animatorT.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float x = (float) animation.getAnimatedValue();
                resHolder.cloud2.setTranslationX(x);
            }
        });
        animatorT.start();
        cloud1Move();
        cloud3Move();
        sunMove();
        adMove();
    }
    private void cloud1Move() {
        ValueAnimator animatorCloud1 = ValueAnimator.ofFloat(screenWidth, -200);
        animatorCloud1.setDuration(1000 * 120);
        animatorCloud1.setInterpolator(new LinearInterpolator());
        animatorCloud1.setRepeatCount(-1);
        animatorCloud1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float x = (float) animation.getAnimatedValue();
                resHolder.cloud1.setTranslationX(x);
            }
        });
        animatorCloud1.start();
    }

    private void cloud3Move() {
        ValueAnimator animatorCloud3 = ValueAnimator.ofFloat(-200, screenWidth);
        animatorCloud3.setDuration(1000 * 140);
        animatorCloud3.setInterpolator(new LinearInterpolator());
        animatorCloud3.setRepeatCount(-1);
        animatorCloud3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float x = (float) animation.getAnimatedValue();
                resHolder.cloud3.setTranslationX(x);
            }
        });
        animatorCloud3.start();
    }

    private void sunMove() {
        ValueAnimator animatorSun = ValueAnimator.ofFloat(60, 80);
        animatorSun.setDuration(6000);
        animatorSun.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSun.setRepeatCount(-1);
        animatorSun.setRepeatMode(ValueAnimator.REVERSE);
        animatorSun.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float x = (float) animation.getAnimatedValue();
                resHolder.imgSun.setTranslationY(x);
            }
        });
        animatorSun.start();
    }

    String url = "http://ksw.ec-8.cn/app/index.php?i=2&c=entry&name=xm_housekeep&do=adata&m=xm_housekeep&weight=111&zukang=120";

    private void adMove() {
        ValueAnimator animatorAD = ValueAnimator.ofFloat(-screenWidth, 0);
        animatorAD.setDuration(3000);
        animatorAD.setInterpolator(new BounceInterpolator());
        animatorAD.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float x = (float) animation.getAnimatedValue();
                resHolder.imgEwmResult.setTranslationX(x);
            }
        });
        animatorAD.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                monkeyAnim();
                try {
                    Bitmap logo = BitmapFactory.decodeResource(getResources(), R.mipmap.logo);
                    Bitmap qrCode = EncodingUtils.createQRCode(url, logo, 280);
                    resHolder.imgEwm.setImageBitmap(qrCode);
                    ValueAnimator alpha = ValueAnimator.ofFloat(0, 100);
                    alpha.setDuration(4000);
                    alpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float x = (float) animation.getAnimatedValue();
                            resHolder.imgEwm.setAlpha(x);
                        }
                    });
                    alpha.start();
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorAD.start();
    }

    private void monkeyAnim() {
        ValueAnimator animatorMonkey = ValueAnimator.ofFloat(-800, 0);
        animatorMonkey.setDuration(1200);
        animatorMonkey.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorMonkey.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float x = (float) animation.getAnimatedValue();
                resHolder.imgMonkey1.setTranslationX(x);
            }
        });
        resHolder.imgMonkey1.setVisibility(View.VISIBLE);
        animatorMonkey.start();
        AnimationDrawable monkeyDraw = (AnimationDrawable) resHolder.imgMonkey1.getBackground();
        monkeyDraw.start();
    }
    //设置仪表盘
    private void setDashboardview() {
        dashboardView4.setRadius(220);
        dashboardView4.setArcColor(getResources().getColor(android.R.color.holo_green_dark));
//      dashboardView4.setTextColor(Color.parseColor("#212121"));
//      dashboardView4.setBgColor(getResources().getColor(android.R.color.white));
        dashboardView4.setPointerRadius(160);
        dashboardView4.setCircleRadius(8);
        dashboardView4.setMaxValue(150);
        dashboardView4.setBigSliceCount(15);
        dashboardView4.setMeasureTextSize(18);
        dashboardView4.setHeaderRadius(120);
        dashboardView4.setHeaderTitle("KG");
        dashboardView4.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
        dashboardView4.setHeaderTextSize(20);
        dashboardView4.setStripeWidth(26);
        dashboardView4.setStripeMode(DashboardView.StripeMode.OUTER);
        //外弧三段色
        List<HighlightCR> highlight3 = new ArrayList<>();
        highlight3.add(new HighlightCR(180, 80, Color.parseColor("#4CAF50")));
        highlight3.add(new HighlightCR(260, 60, Color.parseColor("#FFEB3B")));
        highlight3.add(new HighlightCR(320, 40, Color.parseColor("#F44336")));
        dashboardView4.setStripeHighlightColorAndRange(highlight3);
//        dashboardView4.setRealTimeValue(65,true,1000);

        final ValueAnimator valueAnimator = ValueAnimator.ofInt(40, 120, 45, 65, 50, 75, 60, 75, 50, 65, 110, 40);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                dashboardView4.setRealTimeValue((int) animation.getAnimatedValue());
            }
        });
        valueAnimator.setDuration(10000);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            valueAnimator.start();
                        }
                    });
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void hideNavigationBar() {
        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar

        if (Build.VERSION.SDK_INT >= 19) {
            //SYSTEM_UI_FLAG_IMMERSIVE_STICKY: hide navigation bars - compatibility:
            // building API level is lower thatn 19, use magic number directly for
            // higher API target level
            uiFlags |= 0x00001000;
        } else {
            uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }

        getWindow().getDecorView().setSystemUiVisibility(uiFlags);
    }

    static class ResHolder {
        @BindView(R.id.img_sun)
        ImageView imgSun;
        @BindView(R.id.cloud_2)
        ImageView cloud2;
        @BindView(R.id.cloud_3)
        ImageView cloud3;
        @BindView(R.id.cloud_1)
        ImageView cloud1;
        @BindView(R.id.img_monkey1)
        ImageView imgMonkey1;
        @BindView(R.id.img_ewm_result)
        ImageView imgEwmResult;
        @BindView(R.id.img_ewm)
        ImageView imgEwm;

        ResHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
