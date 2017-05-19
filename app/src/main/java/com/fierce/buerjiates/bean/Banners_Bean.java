package com.fierce.buerjiates.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by win7 on 2017/3/13.
 */

public class Banners_Bean implements Serializable {


    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * ad_img : http://192.168.1.111:8080/image/lunbo_20170401043438752.png
         * clickRate : 100
         * ad_name : 1
         * startTime : 2017-03-27 13:57:05.0
         * id : 7
         * endTime : 2017-05-31 13:57:08.0
         * pic : http://192.168.1.111:8080/image/lunbo_detail_20170401043438852.png
         * admcId : 1
         * status : 0
         */

        private String ad_img;
        private String clickRate;
        private String ad_name;
        private String startTime;
        private int id;
        private String endTime;
        private String pic; //广告详情图
        private int admcId;
        private String admcNum;
        private int status;

        public String getAdmcNum() {
            return admcNum;
        }

        public void setAdmcNum(String admcNum) {
            this.admcNum = admcNum;
        }

        public String getAd_img() {
            return ad_img;
        }

        public void setAd_img(String ad_img) {
            this.ad_img = ad_img;
        }

        public String getClickRate() {
            return clickRate;
        }

        public void setClickRate(String clickRate) {
            this.clickRate = clickRate;
        }

        public String getAd_name() {
            return ad_name;
        }

        public void setAd_name(String ad_name) {
            this.ad_name = ad_name;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public int getAdmcId() {
            return admcId;
        }

        public void setAdmcId(int admcId) {
            this.admcId = admcId;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
