package com.fierce.buerjiates.https;

/**
 * Created by win7 on 2017/3/1.
 */

public class HttpServerInterface {
    /**
     * 基地址
     */
    public static final String BASE_URL = "http://120.76.159.2:8090/admin/";
    public static final String PRICE_URL = "http://m.bejmall.com/";
//    http://m.bejmall.com/web/index.php?c=user&a=login&key=fierce&good_sn=ISHYHR0000169";
//    http://m.bejmall.com/web/index.php?c=user&a=login&ukey=fd&good_sn=ISHYHR0000013
//    public static final String BASE_URL = "http://192.168.1.111:8080/admin/";
//    public static final String BASE_URL = "http://192.168.1.105:8080/admin/";

// 分类 http://120.76.159.2:8090/admin/productCategory/queryProCategoryList.shtml
// 广告图 http://120.76.159.2:8080/admin/ advertising/queryList.shtml
// 商品 http://120.76.159.2:8090/admin/ product/queryProductList.shtml?categoryId=1
//   "country_logo": "http://fx.bejmall.com/data/ecycountrypic/1440657392962471232.png",
    /**
     * 激活设备接口
     */
    public static final String ACT_DEVICE = "androidApp/queryAdmachineByNumber.shtml";
    /**
     * 商品分类接口
     */
    public static final String GOODS_SORT = "productCategory/queryProCategoryList.shtml";
    /**
     * 广告图接口
     */
    public static final String GET_BANNER = "advertising/queryList.shtml";
    /**
     * 商品列表接口
     */
    public static final String GOODS_List = "product/queryProductList.shtml";
    /**
     * 商品单品
     */
    public static final String GoodsInfo = "product/queryProductById.shtml";
    /**
     * 商品价格
     */
    public static final String GoodsPrice = "web/index.php";
    /**
     * 商品价格
     */
    public static final String UPDATEAPP = "product/queryVersionAll.shtml";
    /**
     * 获取抽奖活动奖项资料
     */
    static final String GetGift = "activity/queryActivityProductList.shtml";

    /**
     * 抽奖资格验证
     */
    static final String LOTTERY_VERIFY = "activity/checkCode.shtml";
}