package com.fierce.buerjiates.ui;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fierce.buerjiates.R;
import com.fierce.buerjiates.base.BaseActivity;
import com.fierce.buerjiates.bean.GiftsBean;
import com.fierce.buerjiates.bean.ShopInfo_Bean;
import com.fierce.buerjiates.config.MyApp;
import com.fierce.buerjiates.presents.IGetShopInfoPresent;
import com.fierce.buerjiates.presents.IGteGifPresent;
import com.fierce.buerjiates.presents.IVerifyPresent;
import com.fierce.buerjiates.utils.LotteryUtil;
import com.fierce.buerjiates.utils.mlog;
import com.fierce.buerjiates.views.IGetGiftView;
import com.fierce.buerjiates.views.IGetShopInfoView;
import com.fierce.buerjiates.views.IVerifyView;
import com.fierce.buerjiates.widget.CustomDialog;
import com.fierce.buerjiates.widget.LotteryView;
import com.google.gson.Gson;

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

public class Lottery_activity extends BaseActivity implements IGetGiftView, View.OnClickListener,
        IVerifyView, IGetShopInfoView {
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
    @BindView(R.id.image_hover)
    ImageView imgHover;
//    @BindView(R.id.img_button)
//    ImageView imgButton;

    @BindView(R.id.tv_backHome)
    TextView tvBackHome;
    @BindView(R.id.lottery_view)
    LotteryView lotteryView;


    private IGteGifPresent present;
    private List<GiftsBean.ListBean> giftList;
    private List<Double> probability;
    private GiftsBean giftBean;
    private int stopPosition;
    private Dialog lotterayDialog;
    private LotteryHolder holder;

    private String d_Id;//设备id
    private int mid;   //分销商id
    private int y_id;  //优惠券id
    private String shopname; //门店名称
    private IGetShopInfoPresent getShopInfoPresent;
    private ShopInfo_Bean shopInfo_bean;

    @Override
    protected int getLayoutRes() {
        return R.layout.lottray_activity;
    }

    @Override
    protected void initView() {
        present = new IGteGifPresent(this);
        verifyPresent = new IVerifyPresent(this);
        d_Id = MyApp.getInstance().getDevice_id();
        getShopInfoPresent = new IGetShopInfoPresent(this);
        present.getGifts(d_Id);
        probability = new ArrayList<>();
        setLottreyLayout();
        lotterayRun();
        setDialog();
        //前期不需要抽奖验证
//        imgHover.setOnClickListener(this);
        d_Id = MyApp.getInstance().getDevice_id();
        getShopInfoPresent.getShopInfo(d_Id);
    }

    private void setLottreyLayout() {
        int width = getWindowManager().getDefaultDisplay().getWidth();
        int hiegt = getWindowManager().getDefaultDisplay().getHeight();
        width = width - 160;
        int maginTop = (hiegt - width) / 2;
        lotteryView = (LotteryView) findViewById(R.id.lottery_view);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
        params.setMargins(80, maginTop, 80, 0);
        lotteryView.setLayoutParams(params);

        AnimationDrawable anim = (AnimationDrawable) lotteryView.getBackground();
        anim.start();
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
                        try {
                            Thread.sleep(1200);
                            showDialog();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    /**
     * @param giftList 设置奖项图片
     */
    private void setGiftImage(List<GiftsBean.ListBean> giftList) {
        Glide.with(this).load(giftList.get(0).getImageUrl()).into(img1);
        Glide.with(this).load(giftList.get(1).getImageUrl()).into(img2);
        Glide.with(this).load(giftList.get(2).getImageUrl()).into(img3);
        Glide.with(this).load(giftList.get(3).getImageUrl()).into(img4);
        Glide.with(this).load(giftList.get(4).getImageUrl()).into(img5);
        Glide.with(this).load(giftList.get(5).getImageUrl()).into(img6);
        Glide.with(this).load(giftList.get(6).getImageUrl()).into(img7);
        Glide.with(this).load(giftList.get(7).getImageUrl()).into(img8);
    }

    private void setDialog() {
        View v = View.inflate(this, R.layout.lotteray_dialog_layout, null);
        holder = new LotteryHolder(v);
        lotterayDialog = new CustomDialog.Builder(this)
                .setIsFloating(false)
                .setIsFullscreen(false)
                .setView(v)
                .build();
    }

    private int QR_Num = 0;

    /**
     * 抽奖结果弹窗
     */
    private void showDialog() {
        holder.tvBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lotterayDialog.isShowing())
                    lotterayDialog.dismiss();
                tvBackHome.setClickable(true);
                if (!giftList.get(stopPosition).getGoodName().startsWith("再")) {
                    imgHover.setVisibility(View.VISIBLE);
                }
            }
        });
        String giftName = giftList.get(stopPosition).getGoodName();
        try {
            if (giftName.startsWith("谢谢")) {
                holder.imgQcode.setImageResource(R.mipmap.timg);
                holder.tvSao1sao.setText("不要灰心~");
                holder.tvDialogTitle.setText("真遗憾呐~");
            } else if (giftName.startsWith("再")) {
                holder.imgQcode.setImageResource(R.mipmap.again);
                holder.tvSao1sao.setText("换个姿势，说不定几率会增加哦~");
                holder.tvDialogTitle.setText("获得一次抽奖机会！");
            } else {

//                holder.tvSao1sao.setText("打开微信扫一扫，大奖领回家~");
                holder.tvSao1sao.setText("赶紧在活动期间去找店小二兑奖吧~");
                holder.tvDialogTitle.setText("恭喜你中奖啦！");

                Glide.with(this).load(giftList.get(stopPosition).getImageUrl()).into(holder.imgQcode);
//                String m_id = MyApp.getInstance().getM_id();
//                mid = Integer.parseInt(m_id);
//                //优惠券Id
//                String Yid = giftList.get(stopPosition).getCoupon();
//                y_id = Integer.parseInt(Yid);
//                QR_Num = MyApp.getInstance().getQR_Num();
//                QR_Num++;
//                String QRNO = d_Id + QR_Num;
//                mlog.e("TAG", y_id, QR_Num, QRNO);
//                //二维码 链接
//                String shopUrl = "http://m.bejmall.com/app/index.php?i=4&c=entry" +
//                        "&m=ewei_shopv2&do=mobile&r=goods.youhui&d_id="
//                        + d_Id + "&mid=" + mid + "&y_id=" + y_id + "&QR_Num=" + QRNO + "&shopname=" + (shopname + giftName);

//                Bitmap bitmap = EncodingUtils.createQRCode(shopUrl, null, 220);
//                holder.imgQcode.setImageBitmap(bitmap);
//                MyApp.getInstance().saveQR_Num(QR_Num);
            }
            holder.tvGiftname.setText(giftName);
            lotterayDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initStopPosition(List<GiftsBean.ListBean> giftList) {
        probability.clear();
        mlog.e(giftList.size());
        for (GiftsBean.ListBean bean : giftList) {
            double themProb = bean.getProbability();
            if (themProb < 0)
                themProb = 0;
            probability.add(themProb);
        }
    }

    @Override
    public void getGiftSucceed(GiftsBean giftBean) {
        this.giftBean = giftBean;
        giftList = giftBean.getList();
        setGiftImage(giftList);
    }

    @Override
    public void getGiftFault(String msg) {

        if (MyApp.getInstance().getGiftJson() != null) {
            Gson gson = new Gson();
            String json = MyApp.getInstance().getGiftJson();
            mlog.json(json);
            giftBean = gson.fromJson(json, GiftsBean.class);
            giftList = giftBean.getList();
            setGiftImage(giftList);
        } else {
            showUnStartTipDialog();
        }
    }

    private Dialog unStartTipDialog;

    /**
     * 提示 抽奖活动未开放
     */
    private void showUnStartTipDialog() {
        unStartTipDialog = new CustomDialog.Builder(this)
                .setIsFullscreen(false)
                .setIsFloating(false)
                .setLayout(R.layout.lottery_unstart_dialog_layout)
                .setViewOnClike(R.id.tv_back, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        unStartTipDialog.dismiss();
                        startActivity(new Intent(Lottery_activity.this, MainActivity.class));
                        finish();
                    }
                })
                .build();
        unStartTipDialog.show();
    }

    @OnClick(R.id.tv_backHome)
    public void onViewClicked() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        showeYzDialog();
    }

    private Dialog yzDailog;//验证对话框
    private IVerifyPresent verifyPresent;
    private YZ_ViewHolder yzHolder;

    private void showeYzDialog() {
        View view = View.inflate(this, R.layout.lottery_verify_dialog_layout, null);
        yzHolder = new YZ_ViewHolder(view);
        yzDailog = new CustomDialog.Builder(this)
                .setIsFloating(false)
                .setIsFullscreen(false)
                .setView(view)
                .setViewOnClike(R.id.image_close, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        yzDailog.dismiss();
                    }
                })
                .build();

        yzHolder.tvYangzheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //验证
                if (yzHolder.etVerify.getText().toString().length() <= 0) {
                    ObjectAnimator animator = shake(yzHolder.etVerify);
                    animator.start();
                    return;
                }
                String verifyCode = yzHolder.etVerify.getText().toString();
                verifyPresent.verify(verifyCode);
                yzHolder.progressVerify.setVisibility(View.VISIBLE);
                mlog.e(verifyCode);
            }
        });

        yzHolder.etVerify.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                yzHolder.tvErro.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    yzHolder.imgDelete.setVisibility(View.VISIBLE);
                } else {
                    yzHolder.imgDelete.setVisibility(View.INVISIBLE);
                }
            }
        });

        yzHolder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yzHolder.etVerify.setText("");
            }
        });

        yzDailog.show();
    }

    //控件抖动的动画效果
    private static ObjectAnimator shake(View view) {
        int delta = view.getResources().getDimensionPixelOffset(R.dimen.spacing_medium);//8dp 左右抖动的幅度
        PropertyValuesHolder pvhTranslateX = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_X,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(.10f, -delta),
                Keyframe.ofFloat(.26f, delta),
                Keyframe.ofFloat(.42f, -delta),
                Keyframe.ofFloat(.58f, delta),
                Keyframe.ofFloat(.74f, -delta),
                Keyframe.ofFloat(.90f, delta),
                Keyframe.ofFloat(1f, 0f)
        );
        return ObjectAnimator.ofPropertyValuesHolder(view, pvhTranslateX).setDuration(500);
    }

    @Override
    public void verifySucceed(String message) {
        imgHover.setVisibility(View.GONE);
        yzHolder.progressVerify.setVisibility(View.INVISIBLE);
        yzDailog.dismiss();
    }

    @Override
    public void verifyFailure(int errorType, String msg) {
        yzHolder.progressVerify.setVisibility(View.INVISIBLE);
        String errorTips = null;
        switch (errorType) {
            case 300://验证失败（验证码失效）
                errorTips = "验证失败！该抽奖码不存在或者已被使用过\n请向店小二核对抽奖码";
                break;
            case 0://网络连接错误
                errorTips = "连接网络出错！\n请检查你的网络设置后再试";
                break;
        }
        yzHolder.tvErro.setVisibility(View.VISIBLE);
        yzHolder.tvErro.setText(errorTips);
        ObjectAnimator animator = shake(yzHolder.tvErro);
        animator.start();
    }

    @Override
    public void getTGetShopInfoSucceed(Object o) {
        shopInfo_bean = (ShopInfo_Bean) o;
        ShopInfo_Bean.ListBean listBean = shopInfo_bean.getList().get(0);
        shopname = listBean.getUsername();
    }

    @Override
    public void getGetShopInfoFailure(String msg) {

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

    @Override
    protected void onDestroy() {
        if (lotterayDialog.isShowing())
            lotterayDialog.dismiss();
        super.onDestroy();
    }

    static class YZ_ViewHolder {
        @BindView(R.id.image_close)
        ImageView imageClose;
        @BindView(R.id.tv_tip)
        TextView tvTip;
        @BindView(R.id.tv_tipMessage)
        TextView tvTipMessage;
        @BindView(R.id.tv_yanzhengma)
        TextView tvYanzhengma;
        @BindView(R.id.tv_yangzheng)
        TextView tvYangzheng;
        @BindView(R.id.et_verify)
        EditText etVerify;
        @BindView(R.id.tv_erro)
        TextView tvErro;
        @BindView(R.id.img_deletenum)
        ImageView imgDelete;
        @BindView(R.id.progress_verify)
        LinearLayout progressVerify;

        YZ_ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
