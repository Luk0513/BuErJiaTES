package com.fierce.buerjiates.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.fierce.buerjiates.R;
import com.fierce.buerjiates.base.ListItemAdapter;
import com.fierce.buerjiates.bean.GoodsList_Bean;
import com.fierce.buerjiates.presents.IGetGoodsPricePresent;
import com.fierce.buerjiates.presents.IgetTuanGouPricePresent;
import com.fierce.buerjiates.utils.ImageCacheUtils;
import com.fierce.buerjiates.views.IGetGoodsPriceView;
import com.fierce.buerjiates.views.IgetTuangouView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by win7 on 2017/3/13.
 */

public class MyGridviewAdapter extends ListItemAdapter<GoodsList_Bean.ListBean> {

    private ImageCacheUtils cacheUtils;
    private GridView gridView;

    public MyGridviewAdapter(Context context, List<GoodsList_Bean.ListBean> list,
                             GridView gridView, ImageCacheUtils cacheUtils) {
        super(context, list);
        this.cacheUtils = cacheUtils;
        this.gridView = gridView;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.goodslist_layout, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final GoodsList_Bean.ListBean bean = getItem(position);
        final String imgUrl = bean.getProJsonCode().getGoods_img();
        final String categoryId = bean.getProductCategoryId();

        /**
         *  调取微信商城的限时购价格
         */
        IGetGoodsPricePresent getGoodsPricePresent = new IGetGoodsPricePresent(new IGetGoodsPriceView() {

            @Override
            public void getPriceSucceed(Object o) {
                String price;
                JSONObject jsonObject = (JSONObject) o;
                if (categoryId.equals("2")) {
                    price = jsonObject.optString("xsg_price");
                    String starTime = jsonObject.optString("timestart");
                    String endTime = jsonObject.optString("timeend");
                    long times = System.currentTimeMillis();
                    if (Long.parseLong(endTime) * 1000 < times) {
                        holder.ivTimeout.setVisibility(View.VISIBLE);
                    }
                    holder.tvTimelimit.setVisibility(View.VISIBLE);
                    holder.tvTimelimit.setText("Time：" + formatTime(starTime) + "—" + formatTime(endTime));
                    holder.tvMarketPrice.setVisibility(View.VISIBLE);
                    holder.tvMarketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                    holder.tvMarketPrice.setText("原价：¥" + jsonObject.optString("marketprice"));
                    holder.tvGoodsPrice.setText("限时优惠：¥" + price);
                }
            }

            @Override
            public void getPriceFailure(String msg) {
                Log.e("TAG", "getPriceFailure: >>>>>>>>>>>" + msg);
                holder.tvGoodsPrice.setText("价格：有惊喜！");
            }
        });

        /**
         * 团购信息
         */
        IgetTuanGouPricePresent tgPrice = new IgetTuanGouPricePresent(new IgetTuangouView() {
            @Override
            public void getTGPriceSucceed(Object o) {
                JSONObject tgInfoJson = (JSONObject) o;
                holder.tvGoodsName.setText(tgInfoJson.optString("title"));
                String goodsImgUrl = "http://fx.bejmall.com/" + tgInfoJson.optString("thumb");
                holder.ivGoodsIV.setTag(goodsImgUrl);
                cacheUtils.loadBitmaps(holder.ivGoodsIV, goodsImgUrl, gridView);

                holder.tvMarketPrice.setVisibility(View.VISIBLE);
                holder.tvMarketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                holder.tvMarketPrice.setText("原价：¥" + tgInfoJson.optString("price"));
                holder.tvGoodsPrice.setText("拼团价：¥" + tgInfoJson.optString("groupsprice"));
            }

            @Override
            public void getTGPriceFailure(String msg) {

            }
        });

        //设置商品列表角标
        if (bean.getProductCategoryId().equals("5")) {
            //团购
            holder.ivCube.setBackgroundResource(R.mipmap.ping);
            if (bean.getProJsonCode().getGoods_sn() != null)
                tgPrice.getTGPrice(bean.getProJsonCode().getGoods_sn(), bean.getProductCategoryId());

        } else if (bean.getProductCategoryId().equals("2")) {
            //限时购
            holder.ivGoodsIV.setTag(imgUrl);
            cacheUtils.loadBitmaps(holder.ivGoodsIV, imgUrl, gridView);
            holder.tvGoodsName.setText(bean.getProJsonCode().getGoods_name());
            holder.ivCube_xs.setBackgroundResource(R.mipmap.xs_48);
            if (bean.getProJsonCode().getGoods_sn() != null) {
                getGoodsPricePresent.getGoodsPrice(bean.getProJsonCode().getGoods_sn(), bean.getProductCategoryId());
            }
        } else {
            //其他正常销售商品
            holder.tvGoodsPrice.setText("价格：¥" + bean.getProJsonCode().getShop_price());
            holder.tvGoodsName.setText(bean.getProJsonCode().getGoods_name());
            holder.ivCube_xs.setVisibility(View.GONE);
            holder.ivCube.setVisibility(View.GONE);
            holder.ivGoodsIV.setTag(imgUrl);
            cacheUtils.loadBitmaps(holder.ivGoodsIV, imgUrl, gridView);
//            Glide.with(getContext()).load(imgUrl).into(holder.ivGoodsIV);
        }
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.iv_goodsIV)
        ImageView ivGoodsIV;
        @BindView(R.id.tv_goods_name)
        TextView tvGoodsName;
        @BindView(R.id.tv_goods_price)
        TextView tvGoodsPrice;
        @BindView(R.id.iv_cube)
        ImageView ivCube;
        @BindView(R.id.iv_cube_xs)
        ImageView ivCube_xs;
        @BindView(R.id.tv_market_price)
        TextView tvMarketPrice;
        @BindView(R.id.tv_timeLimit)
        TextView tvTimelimit;
        @BindView(R.id.iv_timeout)
        ImageView ivTimeout;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private String formatTime(String time) {
        long t = Long.parseLong(time) * 1000;
        SimpleDateFormat formats = new SimpleDateFormat("yyyy.MM.dd");
        String d = formats.format(new Date(t));
        return d;
    }
}
