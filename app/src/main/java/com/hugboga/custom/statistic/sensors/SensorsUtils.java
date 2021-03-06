package com.hugboga.custom.statistic.sensors;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.WebView;

import com.hugboga.custom.BuildConfig;
import com.hugboga.custom.MyApplication;
import com.hugboga.custom.activity.CityActivity;
import com.hugboga.custom.activity.UnicornServiceActivity;
import com.hugboga.custom.data.bean.UserEntity;
import com.hugboga.custom.statistic.bean.EventPayBean;
import com.hugboga.custom.utils.CommonUtils;
import com.hugboga.custom.utils.OrderUtils;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by on 16/11/25.
 * 神策统计
 */
public class SensorsUtils {

    public static void onAppClick(String pageName, String elementContent, String refer) {
        onAppClick(pageName, pageName, elementContent, refer);
    }

    public static void onAppClick(String pageName, String pageTitle, String elementContent, String refer) {
        try {
            JSONObject properties = new JSONObject();
            properties.put("pageName", pageName);
            properties.put("pageTitle", pageTitle);
            properties.put("element_content", elementContent);
            properties.put("refer", refer);
            SensorsDataAPI.sharedInstance(MyApplication.getAppContext()).track("AppClick", properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setPageEvent(String eventSource, String pageTitle, String intentSource) {
        try {
            if (TextUtils.isEmpty(eventSource)) {
                return;
            }
            JSONObject properties = new JSONObject();
            properties.put("pageName", eventSource);
            properties.put("pageTitle", TextUtils.isEmpty(pageTitle) ? eventSource : pageTitle);
            properties.put("refer", intentSource);
            SensorsDataAPI.sharedInstance(MyApplication.getAppContext()).track("AppViewScreen", properties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void track(String eventName) {
        try {
            SensorsDataAPI.sharedInstance(MyApplication.getAppContext()).track(eventName, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onOperated(String refer, String pageName) {
        try {
            if (TextUtils.isEmpty(refer) || TextUtils.isEmpty(pageName)) {
                return;
            }
            JSONObject properties = new JSONObject();
            properties.put("refer", refer);
            properties.put("pageName", pageName);
            SensorsDataAPI.sharedInstance(MyApplication.getAppContext()).track("buy_Operated", properties);
        } catch (JSONException e) {
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
                    skuType = "单次";
                    break;
                case 5:
                    skuType = "固定线路";
                    break;
                case 6:
                    skuType = "推荐线路";
                    break;

            }
            JSONObject properties = new JSONObject();
            properties.put("payMethod", payMethod);//支付方式
            properties.put("orderNo", eventPayBean.orderId);
            properties.put("isSuccess", payResult);
            properties.put("hbc_sku_type", skuType);
//            properties.put("hbc_is_appoint_guide", eventPayBean.isSelectedGuide);//指定司导下单
//            properties.put("hbc_price_total", eventPayBean.shouldPay);//费用总计
//            properties.put("orderChannel", eventPayBean.shouldPay + "");//费用总计
//            properties.put("priceChannel", eventPayBean.shouldPay);//实际支付金额
//            properties.put("hbc_price_coupon", "" + eventPayBean.couponPrice);//使用优惠券
//            properties.put("hbc_price_tra_fund", eventPayBean.travelFundPrice);//使用旅游基金
//            properties.put("hbc_price_actually", eventPayBean.actualPay);//实际支付金额
//            properties.put("priceActual", eventPayBean.actualPay);//实际支付金额
//            properties.put("userId", UserEntity.getUser().getUserId(MyApplication.getAppContext()));
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
                    skuType = "单次";
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
    public static void setSensorsShareEvent(String type, String source, String goodsNo, String guideId) {
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

            String serviceType = UnicornServiceActivity.SourceType.getRequsetTypeString(sourceType);

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
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = sDateFormat.format(new java.util.Date());
        try {
            JSONObject properties = new JSONObject();
            properties.put("hbc_user_id", SensorsDataAPI.sharedInstance(MyApplication.getAppContext()).getAnonymousId());
            properties.put("hbc_id", UserEntity.getUser().getUserId(context));
            properties.put("hbc_gender", UserEntity.getUser().getGender(context));
            properties.put("hbc_age", UserEntity.getUser().getAgeType(context));
            properties.put("hbc_phone", UserEntity.getUser().getPhone(context));
            properties.put("hbc_realname", UserEntity.getUser().getUserName(context));
            properties.put("hbc_plateform_type", "Android");        // 平台类型
            properties.put("hbc_version", BuildConfig.VERSION_NAME);// C端产品版本
            properties.put("hbc_longitude", UserEntity.getUser().getLongitude());
            properties.put("hbc_latitude", UserEntity.getUser().getLatitude());
            properties.put("hbc_time", date);
            SensorsDataAPI.sharedInstance(context).showUpWebView(webView, false, properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //神策统计_Im监控统计
    public static void setSensorsAppointImAnalysis(String event, HashMap<String, String> attributes) {
        try {
            JSONObject properties = new JSONObject();
            if (attributes != null && attributes.size() > 0) {
                Set<Map.Entry<String, String>> entrySet = attributes.entrySet();
                for (Iterator<Map.Entry<String, String>> iterator = entrySet.iterator(); iterator.hasNext(); ) {
                    Map.Entry<String, String> entry = iterator.next();
                    String entryKey = entry.getKey();
                    String entryValue = entry.getValue();
                    if (!TextUtils.isEmpty(entryKey) && !TextUtils.isEmpty(entryValue)) {
                        properties.put(entryKey, entryValue);
                    }
                }
            }
            SensorsDataAPI.sharedInstance(MyApplication.getAppContext()).track(event, properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //神策统计_展示报价
    public static void setSensorsPriceEvent(String orderType, String guideId, boolean isHavePrice) {
        setSensorsPriceEvent(orderType, orderType, guideId, isHavePrice);
    }

    //神策统计_展示报价
    public static void setSensorsPriceEvent(String orderType, String orderGoodsType, String guideId, boolean isHavePrice) {
        try {
            JSONObject properties = new JSONObject();
            properties.put("orderType", CommonUtils.getCountInteger(orderType));
            properties.put("orderGoodsType", orderGoodsType);
            properties.put("hbc_guide_id", guideId);
            properties.put("isHavePrice", isHavePrice);
            SensorsDataAPI.sharedInstance(MyApplication.getAppContext()).track("seePrice", properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 点击标签埋点
     *
     * @param paramsData
     * @param tagLevel
     * @param tagName
     */
    public static void setSensorsClickTag(CityActivity.Params paramsData, String tagLevel, String tagName) {
        try {
            JSONObject properties = new JSONObject();
            switch (paramsData.cityHomeType) {
                case CITY:
                    properties.put("cityId", paramsData.id);
                    properties.put("cityName", paramsData.titleName);
                    break;
                case ROUTE:
                    properties.put("lineGroupId", paramsData.id);
                    properties.put("lineGroupName", paramsData.titleName);
                    break;
                case COUNTRY:
                    properties.put("countryId", paramsData.id);
                    properties.put("countryName", paramsData.titleName);
                    break;
            }
//            properties.put("tagGroupName", null); //标签组名称,主要针对目的地tab，如果没有，为空
            properties.put("tagLevel", tagLevel);
            properties.put("tagName", tagName); //玩法标签名
            SensorsDataAPI.sharedInstance(MyApplication.getAppContext()).track("clickTag", properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 目的地Tab页线路标签点击
     *
     * @param tagGroupName
     * @param tagName
     */
    public static void setSensorsClickTagTab(String tagGroupName, String tagName) {
        try {
            JSONObject properties = new JSONObject();
            properties.put("tagGroupName", tagGroupName); //标签组名称,主要针对目的地tab，如果没有，为空
            properties.put("tagName", tagName); //玩法标签名
            SensorsDataAPI.sharedInstance(MyApplication.getAppContext()).track("clickTag", properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //神策统计_下单-初始页浏览
    public static void setSensorsBuyViewEvent(String type, String refer, String guideId) {
        try {
            JSONObject properties = new JSONObject();
            properties.put("hbc_sku_type", type);
            properties.put("hbc_refer", refer);
            properties.put("hbc_guide_id", guideId);
            SensorsDataAPI.sharedInstance(MyApplication.getAppContext()).track("buy_view", properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 收藏埋点
     * @param favoriteType
     * @param goodsNo
     * @param guideId
     */
    public static void setSensorsFavorite(String favoriteType, String goodsNo, String guideId) {
        try {
            JSONObject properties = new JSONObject();
            properties.put("favoriteType", favoriteType);
            properties.put("goodsNo", goodsNo);
            properties.put("guideId", guideId);
            SensorsDataAPI.sharedInstance(MyApplication.getAppContext()).track("favorite", properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 首页顶部Banner埋点
     * @param bannerUrl
     * @param bannerNo
     */
    public static void setSensorsClickBanner(String bannerUrl, String bannerNo) {
        try {
            JSONObject properties = new JSONObject();
            properties.put("bannerUrl", bannerUrl);
            properties.put("bannerNo", CommonUtils.getCountInteger(bannerNo));
            SensorsDataAPI.sharedInstance(MyApplication.getAppContext()).track("clickBanner", properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
