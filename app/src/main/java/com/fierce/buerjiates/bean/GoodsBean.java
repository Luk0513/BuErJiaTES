package com.fierce.buerjiates.bean;

import java.util.List;

/**
 * Created by win7 on 2017/3/22.
 */

public class GoodsBean {

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * hgGoodName : http://fx.bejmall.com/images/201509/goods_img/236_G_1443479444803.jpg
         * productCategoryId : 3
         * goodName : Merries 花王妙而舒 L 44片 拉拉裤/学步裤
         * proJsonCode : {"goods_name":"EARTH'S BEST 1段有机梨泥","shop_price":"27.00","goods_id":"236","goods_sn":"ECS000236","goods_desc":"","goods_brief":"EARTH BEST 果泥 辅食","goods_number":"0","goods_thumb":"http://fx.bejmall.com/images/201509/thumb_img/236_thumb_G_1443479444179.jpg","last_update":"1462525429","country_name":"美国","goods_img":"http://fx.bejmall.com/images/201509/goods_img/236_G_1443479444803.jpg","goods_country_id":"3","country_logo":"1440657372060915652.png"}
         * applyPrice : 155
         */

        private String hgGoodName;
        private String productCategoryId;
        private String goodName;
        private String admcNum;
        private ProJsonCodeBean proJsonCode;
        private String applyPrice;

        public String getHgGoodName() {
            return hgGoodName;
        }

        public void setHgGoodName(String hgGoodName) {
            this.hgGoodName = hgGoodName;
        }

        public String getProductCategoryId() {
            return productCategoryId;
        }

        public void setProductCategoryId(String productCategoryId) {
            this.productCategoryId = productCategoryId;
        }

        public String getAdmcNum() {
            return admcNum;
        }

        public void setAdmcNum(String admcNum) {
            this.admcNum = admcNum;
        }

        public String getGoodName() {
            return goodName;
        }

        public void setGoodName(String goodName) {
            this.goodName = goodName;
        }

        public ProJsonCodeBean getProJsonCode() {
            return proJsonCode;
        }

        public void setProJsonCode(ProJsonCodeBean proJsonCode) {
            this.proJsonCode = proJsonCode;
        }

        public String getApplyPrice() {
            return applyPrice;
        }

        public void setApplyPrice(String applyPrice) {
            this.applyPrice = applyPrice;
        }

        public static class ProJsonCodeBean {
            /**
             * goods_name : EARTH'S BEST 1段有机梨泥
             * shop_price : 27.00
             * goods_id : 236
             * goods_sn : ECS000236
             * goods_desc :
             * goods_brief : EARTH BEST 果泥 辅食
             * goods_number : 0
             * goods_thumb : http://fx.bejmall.com/images/201509/thumb_img/236_thumb_G_1443479444179.jpg
             * last_update : 1462525429
             * country_name : 美国
             * goods_img : http://fx.bejmall.com/images/201509/goods_img/236_G_1443479444803.jpg
             * goods_country_id : 3
             * country_logo : 1440657372060915652.png
             */

            private String goods_name;
            private String shop_price;
            private String goods_id;
            private String goods_sn;
            private String goods_desc;
            private String goods_brief;
            private String goods_number;
            private String goods_thumb;
            private String last_update;
            private String country_name;
            private String goods_img;
            private String goods_country_id;
            private String country_logo;

            public String getGoods_name() {
                return goods_name;
            }

            public void setGoods_name(String goods_name) {
                this.goods_name = goods_name;
            }

            public String getShop_price() {
                return shop_price;
            }

            public void setShop_price(String shop_price) {
                this.shop_price = shop_price;
            }

            public String getGoods_id() {
                return goods_id;
            }

            public void setGoods_id(String goods_id) {
                this.goods_id = goods_id;
            }

            public String getGoods_sn() {
                return goods_sn;
            }

            public void setGoods_sn(String goods_sn) {
                this.goods_sn = goods_sn;
            }

            public String getGoods_desc() {
                return goods_desc;
            }

            public void setGoods_desc(String goods_desc) {
                this.goods_desc = goods_desc;
            }

            public String getGoods_brief() {
                return goods_brief;
            }

            public void setGoods_brief(String goods_brief) {
                this.goods_brief = goods_brief;
            }

            public String getGoods_number() {
                return goods_number;
            }

            public void setGoods_number(String goods_number) {
                this.goods_number = goods_number;
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

            public String getCountry_name() {
                return country_name;
            }

            public void setCountry_name(String country_name) {
                this.country_name = country_name;
            }

            public String getGoods_img() {
                return goods_img;
            }

            public void setGoods_img(String goods_img) {
                this.goods_img = goods_img;
            }

            public String getGoods_country_id() {
                return goods_country_id;
            }

            public void setGoods_country_id(String goods_country_id) {
                this.goods_country_id = goods_country_id;
            }

            public String getCountry_logo() {
                return country_logo;
            }

            public void setCountry_logo(String country_logo) {
                this.country_logo = country_logo;
            }
        }
    }
}
