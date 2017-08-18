package com.fierce.buerjiates.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fierce.buerjiates.R;
import com.fierce.buerjiates.base.BaseActivity;
import com.fierce.buerjiates.bean.Gift_Bean;
import com.fierce.buerjiates.config.MyApp;
import com.fierce.buerjiates.presents.IGteGifPresent;
import com.fierce.buerjiates.utils.EncodingUtils;
import com.fierce.buerjiates.utils.LotteryUtil;
import com.fierce.buerjiates.utils.mlog;
import com.fierce.buerjiates.views.IGetGiftView;
import com.fierce.buerjiates.widget.CustomDialog;
import com.fierce.buerjiates.widget.LotteryView;
import com.google.gson.Gson;
import com.google.zxing.WriterException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author : Lukang
 * >>Live and learn<<
 * @PackegeName : com.fierce.buerjiates.ui
 * @ProjectName : BuErJiaTES
 * @Date :  2017-07-06
 */

public class Lottery_activity extends BaseActivity implements IGetGiftView {
    @BindView(R.id.img_1)
    ImageView img1;
    @BindView(R.id.img_2)
    ImageView img2;
    @BindView(R.id.img_3)
    ImageView img3;
    @BindView(R.id.img_4)
    ImageView img4;
    @BindView(R.id.img_5)
    ImageView img5;
    @BindView(R.id.img_6)
    ImageView img6;
    @BindView(R.id.img_7)
    ImageView img7;
    @BindView(R.id.img_8)
    ImageView img8;
    @BindView(R.id.tv_backHome)
    TextView tvBackHome;

    @BindView(R.id.lottery_view)
    LotteryView lotteryView;


    private IGteGifPresent present;
    private List<Gift_Bean.ListBean> giftList;
    private List<Double> probability;
    private Gift_Bean giftBean;
    private int stopPosition;

    @Override
    protected int getLayoutRes() {
        return R.layout.lottray_activity;
    }

    @Override
    protected void initView() {
        present = new IGteGifPresent(this);
        present.getGifts();
        probability = new ArrayList<>();

        int width = getWindowManager().getDefaultDisplay().getWidth();
        int hiegt = getWindowManager().getDefaultDisplay().getHeight();
        width = width - 200;
        int maginTop = (hiegt - width) / 2;
        Log.e("TAG", "onCreate: " + width);
        lotteryView = (LotteryView) findViewById(R.id.lottery_view);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
        params.setMargins(100, maginTop, 100, 0);
        lotteryView.setLayoutParams(params);

        AnimationDrawable anim = (AnimationDrawable) lotteryView.getBackground();
        anim.start();
        lotterayRun();
        setDialog();
    }

    private void lotterayRun() {
        lotteryView.setOnStartListener(new LotteryView.OnStartListener() {
            @Override
            public void onStart() {
                tvBackHome.setClickable(false);
                initStopPosition(giftList);
                stopPosition = LotteryUtil.lottery(probability);
                mlog.e(stopPosition);
                lotteryView.stop(stopPosition);
            }

            @Override
            public void onStop() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showDialog();
                        tvBackHome.setClickable(true);
                    }
                });
            }
        });
    }

    private void setGiftImage(List<Gift_Bean.ListBean> giftList) {
        Glide.with(this).load(giftList.get(0).getImageUrl()).into(img1);
        Glide.with(this).load(giftList.get(1).getImageUrl()).into(img2);
        Glide.with(this).load(giftList.get(2).getImageUrl()).into(img3);
        Glide.with(this).load(giftList.get(3).getImageUrl()).into(img4);
        Glide.with(this).load(giftList.get(4).getImageUrl()).into(img5);
        Glide.with(this).load(giftList.get(5).getImageUrl()).into(img6);
        Glide.with(this).load(giftList.get(6).getImageUrl()).into(img7);
        Glide.with(this).load(giftList.get(7).getImageUrl()).into(img8);
    }

    private Dialog lotterayDialog;
    private LotteryHolder holder;

    private void setDialog() {
        View v = View.inflate(this, R.layout.lotteray_dialog_layout, null);
        holder = new LotteryHolder(v);
        lotterayDialog = new CustomDialog.Builder(this)
                .setIsFloating(false)
                .setIsFullscreen(false)
                .setView(v)
                .build();
    }

    private void showDialog() {
        holder.tvBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lotterayDialog.isShowing())
                    lotterayDialog.dismiss();
            }
        });

        try {
            if (giftList.get(stopPosition).getGoodName().equals("谢谢参与")) {
                holder.imgQcode.setImageResource(R.mipmap.timg);
                holder.tvSao1sao.setText("不要灰心~");
                holder.tvDialogTitle.setText("真遗憾呐~");
            } else {
                Bitmap bitmap = EncodingUtils.createQRCode("cehsjsehfjhfj", null, 220);
                holder.imgQcode.setImageBitmap(bitmap);
            }
            holder.tvGiftname.setText(giftList.get(stopPosition).getGoodName());
            lotterayDialog.show();

        } catch (
                WriterException e)

        {
            e.printStackTrace();
        }
    }

    private void initStopPosition(List<Gift_Bean.ListBean> giftList) {
        probability.clear();
        mlog.e(giftList.size());
        for (Gift_Bean.ListBean bean : giftList) {
            double themProb = bean.getProbability();
            if (themProb < 0)
                themProb = 0;
            probability.add(themProb);
        }
    }

    @Override
    public void getGiftSucceed(Gift_Bean giftBean) {
        this.giftBean = giftBean;
        giftList = giftBean.getList();
        setGiftImage(giftList);
    }

    @Override
    public void getGiftFault(String msg) {

        if (MyApp.getInstance().getGiftJson() != null) {
            Gson gson = new Gson();
            String json = MyApp.getInstance().getGiftJson();
            giftBean = gson.fromJson(json, Gift_Bean.class);
            giftList = giftBean.getList();
            setGiftImage(giftList);
        }

    }

    @OnClick(R.id.tv_backHome)
    public void onViewClicked() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    static class LotteryHolder {

        @BindView(R.id.tv_dialog_title)
        TextView tvDialogTitle;
        @BindView(R.id.tv_sao1sao)
        TextView tvSao1sao;
        @BindView(R.id.img_qcode)
        ImageView imgQcode;
        @BindView(R.id.tv_giftname)
        TextView tvGiftname;
        @BindView(R.id.tv_backButton)
        TextView tvBackButton;

        LotteryHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
