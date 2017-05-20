package com.fierce.buerjiates.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.fierce.buerjiates.R;
import com.fierce.buerjiates.base.ListItemAdapter;
import com.fierce.buerjiates.bean.GoodsList_Bean;
import com.fierce.buerjiates.presents.IGetGoodsPricePresent;
import com.fierce.buerjiates.utils.ImageCacheUtils;
import com.fierce.buerjiates.views.IGetGoodsPriceView;

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
//        Log.e("TAG", "getView:     ?????????????????????? ....>>>" + bean.getAdmcNum());

        /**
         *  调取微信商城的价格
         */
        IGetGoodsPricePresent getGoodsPricePresent = new IGetGoodsPricePresent(new IGetGoodsPriceView() {
            @Override
            public void getPriceSucceed(String price) {
                holder.tvGoodsPrice.setText("活动价格：RMB" + price);
            }

            @Override
            public void getPriceFailure(String msg) {
                holder.tvGoodsPrice.setText("价格：有惊喜！");
            }
        });
        if (bean.getProJsonCode().getGoods_sn() != null) {
            getGoodsPricePresent.getGoodsPrice(bean.getProJsonCode().getGoods_sn());
        }
        holder.ivGoodsIV.setTag(imgUrl);
        cacheUtils.loadBitmaps(holder.ivGoodsIV, imgUrl, gridView);
        holder.tvGoodsName.setText(bean.getProJsonCode().getGoods_name());
        if (bean.getCube().startsWith("http")) {
//            Glide.with(getContext()).load(bean.getCube())
//                    .diskCacheStrategy(DiskCacheStrategy.RESULT).into(holder.ivCube);
            holder.ivCube.setBackgroundResource(R.mipmap.ping);
        } else {
            holder.ivCube_xs.setBackgroundResource(R.mipmap.xs_48);
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

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
