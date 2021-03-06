package com.hugboga.custom.data.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * SKU item
 * Created by admin on 2016/3/3.
 */
public class SkuItemBean implements Serializable {

    public String goodsNo;//商品ID
    public String goodsName;//商品名
    public String goodsPicture;//商品图片
    public String goodsDesc;//商品描述
    public String perPrice;//商品人均价格
    public int guideAmount;//车导数量
    public String headLable;//商品标签（超省心或超自由)
    public String bookLable;//预定日期标签(今日可订)
    public int goodsClass = -1;//商品类别（1，固定线路；2，推荐线路）-1为按天报价goodsClass不反回
    public ArrayList<CharacteristicLables> characteristicLables;
    public int goodsVersion;//商品版本号
    public String aliasName;//线路别名

    public String cityId;//非接口字段


    public String goodsMinPrice;//最低价格
    public String salePoints;//标签
    public int daysCount;//天数
    public String places;
    public String skuDetailUrl;//详情地址
    public String shareURL;//分享地址

    public int arrCityId;
    public String arrCityName;
    public int depCityId;
    public String depCityName;
    public int goodsType;
    public List<CityBean> passCityList;
    public String passPoiListStr;

    public int hotelCostAmount;//天数
    public int hotelStatus;//是否有酒店  (0，没有；1，有)

    public String goodsLable;// 商品下标签（例如：城市x日玩法推荐、¥ 243 起/人 · 1日 · 含酒店）
    public ArrayList<String> goodsPics;
    public String keyWords;
    public int saleAmount;
    public int transactionVolumes;

    public GoodsBookDateBean bookDateInfo;

    public static class CharacteristicLables implements Serializable {
        public String lableName;
        public int lableType;
    }

    public String getGoodsName() {
        if (!TextUtils.isEmpty(aliasName)) {
            return aliasName;
        } else if (!TextUtils.isEmpty(goodsName)) {
            return goodsName;
        }
        return "";
    }
}
