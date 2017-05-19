package com.fierce.buerjiates.bean;

import java.util.List;

/**
 * Created by win7 on 2017/3/13.
 */

public class GoodsSort_Bean {


    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * editime : 2017-03-22 09:32:20.097
         * productCategoryId : 1
         * valueAddTaxRate : 10
         * consumerTaxRate : 10
         * ctime : 2017-03-10 17:52:53.703
         * state : 1
         * pic : http://120.76.159.2:8080/image/feilei_2017032209329820.png
         * taxCode : http://120.76.159.2:8080/image/feilei_2017032209329820.png
         * categoryName : 限时抢购
         */

        private String editime;
        private String productCategoryId;
        private int valueAddTaxRate;
        private int consumerTaxRate;
        private String ctime;
        private int state;
        private String pic;
        private String taxCode;
        private String categoryName;

        public String getEditime() {
            return editime;
        }

        public void setEditime(String editime) {
            this.editime = editime;
        }

        public String getProductCategoryId() {
            return productCategoryId;
        }

        public void setProductCategoryId(String productCategoryId) {
            this.productCategoryId = productCategoryId;
        }

        public int getValueAddTaxRate() {
            return valueAddTaxRate;
        }

        public void setValueAddTaxRate(int valueAddTaxRate) {
            this.valueAddTaxRate = valueAddTaxRate;
        }

        public int getConsumerTaxRate() {
            return consumerTaxRate;
        }

        public void setConsumerTaxRate(int consumerTaxRate) {
            this.consumerTaxRate = consumerTaxRate;
        }

        public String getCtime() {
            return ctime;
        }

        public void setCtime(String ctime) {
            this.ctime = ctime;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getTaxCode() {
            return taxCode;
        }

        public void setTaxCode(String taxCode) {
            this.taxCode = taxCode;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }
    }
}
