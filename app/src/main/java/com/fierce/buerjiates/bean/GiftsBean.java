package com.fierce.buerjiates.bean;

import java.util.List;

/**
 * @Author : Lukang
 * >>Live and learn<<
 * blog : http://blog.csdn.net/fight_0513
 * @PackegeName : com.fierce.buerjiates.bean
 * @ProjectName : BuErJiaTES
 * @Date :  2017-08-22
 */

public class GiftsBean {

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * admcNum : gddg13728133158
         * imageId : 1
         * goodName : 再来一次
         * coupon : 8
         * probability : 0.1
         * imageUrl : http://192.168.1.111:8080/image/hdsp_20170821030538951.png
         * remark : 8
         * id : 1
         * admcId : 92
         */

        private String admcNum;
        private String imageId;
        private String goodName;
        private String coupon;
        private double probability;
        private String imageUrl;
        private String remark;
        private int id;
        private int admcId;

        public String getAdmcNum() {
            return admcNum;
        }

        public void setAdmcNum(String admcNum) {
            this.admcNum = admcNum;
        }

        public String getImageId() {
            return imageId;
        }

        public void setImageId(String imageId) {
            this.imageId = imageId;
        }

        public String getGoodName() {
            return goodName;
        }

        public void setGoodName(String goodName) {
            this.goodName = goodName;
        }

        public String getCoupon() {
            return coupon;
        }

        public void setCoupon(String coupon) {
            this.coupon = coupon;
        }

        public double getProbability() {
            return probability;
        }

        public void setProbability(double probability) {
            this.probability = probability;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getAdmcId() {
            return admcId;
        }

        public void setAdmcId(int admcId) {
            this.admcId = admcId;
        }
    }
}
