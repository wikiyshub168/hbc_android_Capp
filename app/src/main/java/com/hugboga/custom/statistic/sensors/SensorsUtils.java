package com.hugboga.custom.statistic.sensors;

import android.content.Context;
import android.webkit.WebView;

import com.hugboga.custom.MyApplication;
import com.hugboga.custom.activity.UnicornServiceActivity;
import com.hugboga.custom.data.bean.UserEntity;
import com.hugboga.custom.statistic.bean.EventPayBean;
import com.hugboga.custom.utils.OrderUtils;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;

import org.json.JSONObject;

/**
 * Created by on 16/11/25.
 * 神策统计
 */
public class SensorsUtils {

    public static void track(String eventName) {
        try {
            SensorsDataAPI.sharedInstance(MyApplication.getAppContext()).track(eventName, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //支付结果
    public static void setSensorsPayResultEvent(EventPayBean eventPayBean, String payMethod, boolean payResult) {
        try {
            String skuType = "";
            switch (eventPayBean.orderType) {
                case 1:
                    skuType = "接机";
                    break;
                case 2:
                    skuType = "送机";
                    break;
                case 3:
                case 888:
                    skuType = "按天包车游";
                    break;
                case 4:
                    skuType = "单次接送";
                    break;
                case 5:
                    skuType = "固定线路";
                    break;
                case 6:
                    skuType = "推荐线路";
                    break;

            }
            JSONObject properties = new JSONObject();
            properties.put("hbc_sku_type", skuType);
            properties.put("hbc_order_id", eventPayBean.orderId);
            properties.put("hbc_is_appoint_guide", eventPayBean.isSelectedGuide);//指定司导下单
            properties.put("hbc_price_total", eventPayBean.shouldPay);//费用总计
            properties.put("hbc_price_coupon", "" + eventPayBean.couponPrice);//使用优惠券
            properties.put("hbc_price_tra_fund", eventPayBean.travelFundPrice);//使用旅游基金
            properties.put("hbc_price_actually", eventPayBean.actualPay);//实际支付金额
            properties.put("hbc_pay_method", payMethod);//支付方式
            properties.put("hbc_pay_result", payResult);
            SensorsDataAPI.sharedInstance(MyApplication.getAppContext()).track("pay_result", properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //点击支付
    public static void setSensorsPayOnClickEvent(EventPayBean eventPayBean, String payMethod) {
        try {
            String skuType = "";
            switch (eventPayBean.orderType) {
                case 1:
                    skuType = "接机";
                    break;
                case 2:
                    skuType = "送机";
                    break;
                case 3:
                    skuType = "按天包车游";
                    break;
                case 4:
                    skuType = "单次接送";
                    break;
                case 5:
                    skuType = "固定线路";
                    break;
                case 6:
                    skuType = "推荐线路";
                    break;

            }
            JSONObject properties = new JSONObject();
            properties.put("hbc_sku_type", skuType);
            properties.put("hbc_order_id", eventPayBean.orderId);
            properties.put("hbc_is_appoint_guide", eventPayBean.isSelectedGuide);//指定司导下单
            properties.put("hbc_price_total", eventPayBean.shouldPay);//费用总计
            properties.put("hbc_price_coupon", "" + eventPayBean.couponPrice);//使用优惠券
            properties.put("hbc_price_tra_fund", eventPayBean.travelFundPrice);//使用旅游基金
            properties.put("hbc_price_actually", eventPayBean.actualPay);//实际支付金额
            properties.put("hbc_pay_method", payMethod);//支付方式
            SensorsDataAPI.sharedInstance(MyApplication.getAppContext()).track("buy_pay", properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //分享
    public static void setSensorsShareEvent(String type, String source,String goodsNo,String guideId) {
        try {
            JSONObject properties = new JSONObject();
            properties.put("shareType", type);
            properties.put("shareContent", source);
            properties.put("goodsNo", goodsNo);
            properties.put("guideId", guideId);
            SensorsDataAPI.sharedInstance(MyApplication.getAppContext()).track("share", properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //联系客服
    public static void setSensorsServiceEvent(int sourceType, String source, int _type) {
        try {
            String typeStr = "";
            switch (_type) {
                case 0:
                    typeStr = "在线客服";
                    break;
                case 1:
                    typeStr = "境内电话";
                    break;
                case 2:
                    typeStr = "境外电话";
                    break;
            }

            String serviceType = "";
            switch (sourceType){
                case 1:
                    serviceType = "售后";
                    break;
                case 2:
                    serviceType = "售前";
                    break;
                case 3:
                    serviceType = "通用";
                    break;
            }
            JSONObject properties = new JSONObject();
            properties.put("pageName", source);
            properties.put("serviceMethod", typeStr);
            properties.put("serviceType", serviceType);
            SensorsDataAPI.sharedInstance(MyApplication.getAppContext()).track("contactService", properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setSensorsShowUpWebView(WebView webView) {
        Context context = MyApplication.getAppContext();
        try {
            JSONObject properties = new JSONObject();
            properties.put("hbc_user_id", SensorsDataAPI.sharedInstance(MyApplication.getAppContext()).getAnonymousId());
            properties.put("hbc_id", UserEntity.getUser().getUserId(context));
            properties.put("hbc_gender", UserEntity.getUser().getGender(context));
            properties.put("hbc_age", UserEntity.getUser().getAgeType(context));
            properties.put("hbc_phone", UserEntity.getUser().getPhone(context));
            properties.put("hbc_realname", UserEntity.getUser().getUserName(context));
            SensorsDataAPI.sharedInstance(context).showUpWebView(webView, false, properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //神策统计_指定司导下单
    public static void setSensorsAppointGuide(String source, int orderType, String guideCity, String serviceCity) {
        try {
            JSONObject properties = new JSONObject();
            properties.put("hbc_appoint_entrance", source);
            properties.put("hbc_appoint_type", OrderUtils.getOrderTypeStr(orderType));
            properties.put("guide_city", guideCity);
            properties.put("service_city", serviceCity);
            SensorsDataAPI.sharedInstance(MyApplication.getAppContext()).track("appoint_guide", properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
