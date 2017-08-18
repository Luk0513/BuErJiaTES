package com.fierce.buerjiates.bean;

import java.util.List;

/**
 * @Author : Lukang
 * >>Live and learn<<
 * blog : http://blog.csdn.net/fight_0513
 * @PackegeName : com.fierce.buerjiates.bean
 * @ProjectName : BuErJiaTES
 * @Date :  2017-08-18
 */

public class Gift_Bean {

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * admcNum : buerjia001
         * imageId : 1
         * goodName : 商品1
         * probability : 0.1
         * imageUrl : http://........
         * id : 1
         * admcId : 69
         * remark :
         */

        private String admcNum;
        private String imageId;
        private String goodName;
        private double probability;
        private String imageUrl;
        private int id;
        private int admcId;
        private String remark;

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

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
}
