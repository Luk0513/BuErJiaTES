package com.fierce.buerjiates.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.fierce.buerjiates.R;
import com.fierce.buerjiates.utils.ImageCacheUtils;

import java.util.List;

/**
 * Created by win7 on 2017/3/20.
 */

public class GoodsSrcListAdapter extends BaseAdapter {
    private ImageCacheUtils cacheUtils;
    private List<String> imgUrlList;
    private Context context;
    private ListView imgLisView;

    public GoodsSrcListAdapter(Context context, List<String> imgUrlList, ImageCacheUtils cacheUtils, ListView imgLisView) {
        this.imgUrlList = imgUrlList;
        this.context = context;
        this.cacheUtils = cacheUtils;
        this.imgLisView = imgLisView;
    }

    @Override
    public int getCount() {
        return imgUrlList.size();
    }

    @Override
    public Object getItem(int position) {
        return imgUrlList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.goodssrc_listitem, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.ivGoodsrclIstitem.setTag(imgUrlList.get(position));
        holder.ivGoodsrclIstitem.setScaleType(ImageView.ScaleType.FIT_XY);
        cacheUtils.loadBitmaps(holder.ivGoodsrclIstitem, imgUrlList.get(position), imgLisView);
        return convertView;
    }

    private static class ViewHolder {
        ImageView ivGoodsrclIstitem;

        ViewHolder(View view) {
            ivGoodsrclIstitem = (ImageView) view.findViewById(R.id.iv_goodsrcl_istitem);
        }
    }
}
