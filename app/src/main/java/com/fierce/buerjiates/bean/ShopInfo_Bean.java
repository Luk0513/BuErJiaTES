package com.fierce.buerjiates.bean;

import java.util.List;

/**
 * @Author : Lukang
 * >>Live and learn<<
 * blog : http://blog.csdn.net/fight_0513
 * @PackegeName : com.fierce.buerjiates.bean
 * @ProjectName : BuErJiaTES
 * @Date :  2017-08-29
 */

public class ShopInfo_Bean {

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * cityName : 深圳市
         * areaName : 宝安区
         * street : 测试
         * API_Status : 1
         * mId : 0
         * ad_machineNumber : kk03
         * provinceName : 广东省
         * status : 1
         * username : 公司测试
         */

        private String cityName;
        private String areaName;
        private String street;
        private int API_Status;
        private String mId;
        private String ad_machineNumber;
        private String provinceName;
        private int status;
        private String username;

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public String getAreaName() {
            return areaName;
        }

        public void setAreaName(String areaName) {
            this.areaName = areaName;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public int getAPI_Status() {
            return API_Status;
        }

        public void setAPI_Status(int API_Status) {
            this.API_Status = API_Status;
        }

        public String getMId() {
            return mId;
        }

        public void setMId(String mId) {
            this.mId = mId;
        }

        public String getAd_machineNumber() {
            return ad_machineNumber;
        }

        public void setAd_machineNumber(String ad_machineNumber) {
            this.ad_machineNumber = ad_machineNumber;
        }

        public String getProvinceName() {
            return provinceName;
        }

        public void setProvinceName(String provinceName) {
            this.provinceName = provinceName;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        @Override
        public String toString() {
            return "ListBean{" +
                    "cityName='" + cityName + '\'' +
                    ", areaName='" + areaName + '\'' +
                    ", street='" + street + '\'' +
                    ", API_Status=" + API_Status +
                    ", mId='" + mId + '\'' +
                    ", ad_machineNumber='" + ad_machineNumber + '\'' +
                    ", provinceName='" + provinceName + '\'' +
                    ", status=" + status +
                    ", username='" + username + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ShopInfo_Bean{" +
                "list=" + list +
                '}';
    }
}
