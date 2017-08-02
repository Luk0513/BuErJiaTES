package com.fierce.buerjiates.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/3/13.
 */

public class GoodsList_Bean implements Serializable {


    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean implements Serializable{
        /**
         * applyPrice : 198
         * goodName : 德国 喜宝 益生元系列 2段 600g
         * cube: "wu"
         * hgGoodName : http://fx.bejmall.com/images/201509/goods_img/193_G_1442626549753.jpg
         * proJsonCode : {"country_logo":"http://fx.bejmall.com/data/ecycountrypic/1440657420869862914.png","country_name":"德国","goods_brief":"喜宝 2段","goods_country_id":"5","goods_desc":" ","goods_id":"193","goods_img":"http://fx.bejmall.com/images/201509/goods_img/193_G_1442626549753.jpg","goods_name":"德国 喜宝 益生元系列 2段 600g 2罐装","goods_number":"100","goods_sn":"ECS000190","goods_thumb":"http://fx.bejmall.com/images/201509/thumb_img/193_thumb_G_1442626549581.jpg","last_update":"1481758323","shop_price":"316.00"}
         * productCategoryId : 1
         */

        private String applyPrice;
        private String cube;
        private String goodName;
        private String hgGoodName;
        private ProJsonCodeBean proJsonCode;
        private String productCategoryId;
        private String admcNum;

        public String getAdmcNum() {
            return admcNum;
        }

        public void setAdmcNum(String admcNum) {
            this.admcNum = admcNum;
        }

        public String getApplyPrice() {
            return applyPrice;
        }

        public void setApplyPrice(String applyPrice) {
            this.applyPrice = applyPrice;
        }

        public String getCube() {
            return cube;
        }

        public void setCube(String cube) {
            this.cube = cube;
        }

        public String getGoodName() {
            return goodName;
        }

        public void setGoodName(String goodName) {
            this.goodName = goodName;
        }

        public String getHgGoodName() {
            return hgGoodName;
        }

        public void setHgGoodName(String hgGoodName) {
            this.hgGoodName = hgGoodName;
        }

        public ProJsonCodeBean getProJsonCode() {
            return proJsonCode;
        }

        public void setProJsonCode(ProJsonCodeBean proJsonCode) {
            this.proJsonCode = proJsonCode;
        }

        public String getProductCategoryId() {
            return productCategoryId;
        }

        public void setProductCategoryId(String productCategoryId) {
            this.productCategoryId = productCategoryId;
        }

        public static class ProJsonCodeBean {
            /**
             * country_logo : http://fx.bejmall.com/data/ecycountrypic/1440657420869862914.png
             * country_name : 德国
             * goods_brief : 喜宝 2段
             * goods_country_id : 5
             * goods_desc :
             * goods_id : 193
             * goods_img : http://fx.bejmall.com/images/201509/goods_img/193_G_1442626549753.jpg
             * goods_name : 德国 喜宝 益生元系列 2段 600g 2罐装
             * goods_number : 100
             * goods_sn : ECS000190
             * goods_thumb : http://fx.bejmall.com/images/201509/thumb_img/193_thumb_G_1442626549581.jpg
             * last_update : 1481758323
             * shop_price : 316.00
             */

            private String country_logo;
            private String country_name;
            private String goods_brief;
            private String goods_country_id;
            private String goods_desc;
            private String goods_id;
            private String goods_img;
            private String goods_name;
            private String goods_number;
            private String goods_sn;
            private String goods_thumb;
            private String last_update;
            private String shop_price;

            public String getCountry_logo() {
                return country_logo;
            }

            public void setCountry_logo(String country_logo) {
                this.country_logo = country_logo;
            }

            public String getCountry_name() {
                return country_name;
            }

            public void setCountry_name(String country_name) {
                this.country_name = country_name;
            }

            public String getGoods_brief() {
                return goods_brief;
            }

            public void setGoods_brief(String goods_brief) {
                this.goods_brief = goods_brief;
            }

            public String getGoods_country_id() {
                return goods_country_id;
            }

            public void setGoods_country_id(String goods_country_id) {
                this.goods_country_id = goods_country_id;
            }

            public String getGoods_desc() {
                return goods_desc;
            }

            public void setGoods_desc(String goods_desc) {
                this.goods_desc = goods_desc;
            }

            public String getGoods_id() {
                return goods_id;
            }

            public void setGoods_id(String goods_id) {
                this.goods_id = goods_id;
            }

            public String getGoods_img() {
                return goods_img;
            }

            public void setGoods_img(String goods_img) {
                this.goods_img = goods_img;
            }

            public String getGoods_name() {
                return goods_name;
            }

            public void setGoods_name(String goods_name) {
                this.goods_name = goods_name;
            }

            public String getGoods_number() {
                return goods_number;
            }

            public void setGoods_number(String goods_number) {
                this.goods_number = goods_number;
            }

            public String getGoods_sn() {
                return goods_sn;
            }

            public void setGoods_sn(String goods_sn) {
                this.goods_sn = goods_sn;
            }

            public String getGoods_thumb() {
                return goods_thumb;
            }

            public void setGoods_thumb(String goods_thumb) {
                this.goods_thumb = goods_thumb;
            }

            public String getLast_update() {
                return last_update;
            }

            public void setLast_update(String last_update) {
                this.last_update = last_update;
            }

            public String getShop_price() {
                return shop_price;
            }

            public void setShop_price(String shop_price) {
                this.shop_price = shop_price;
            }
        }
    }
}
