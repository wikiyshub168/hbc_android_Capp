package com.hugboga.custom.data.request;

import android.content.Context;

import com.huangbaoche.hbcframe.data.parser.ImplParser;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.hugboga.custom.data.bean.CarBean;
import com.hugboga.custom.data.net.NewParamsBuilder;
import com.hugboga.custom.data.net.UrlLibs;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.http.HttpMethod;
import org.xutils.http.annotation.HttpRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


//"channelId": channelId,
//        "serviceCityId": serviceCityId,
//        "goodsType": goodsType,
//        "carSeatNum": carSeatNum,       // 几座车（5，7，9，12）
//        "carTypeId": carTypeId,         // 车型（经济-1，舒适-2，豪华-3，奢华-4）
//        "servceTime": servceTime,       // 2015-02-10 12:00:00
//        "halfDaily": halfDaily,         // 半日包1，其它0
//        "orderType": orderType

@HttpRequest(path = UrlLibs.CANCLE_TIPS, builder = NewParamsBuilder.class)
public class RequestCancleTips extends BaseRequest<List<String>> {
    public RequestCancleTips(Context context, CarBean carBean, String serviceCityId, String goodsType, String carTypeId,
                             String carSeatNum, String servceTime, String halfDaily, String goodsVersion, String goodsNo, String orderType) {
        super(context);
        map = new HashMap<String, Object>();
        try {
            map.put("channelId","18");
            map.put("serviceCityId",serviceCityId);
//            if("6".equalsIgnoreCase(goodsType)){
//                map.put("goodsType", "5");
//            }else {
                map.put("goodsType", goodsType);
//            }
            map.put("orderType",orderType);
            map.put("carSeatNum",carSeatNum);
            map.put("carTypeId",carTypeId);
            map.put("servceTime",servceTime);
            map.put("halfDaily",halfDaily);
            map.put("goodsVersion", goodsVersion);
            map.put("goodsNo", goodsNo);
            if(null != carBean) {
                map.put("carId", carBean.carId);
                map.put("isSpecialCar", carBean.special);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ImplParser getParser() {
        return new DataParser();
    }

    @Override
    public HttpMethod getHttpMethod() {
        return HttpMethod.GET;
    }

    @Override
    public String getUrlErrorCode() {
        return "40012";
    }

    private static class DataParser extends ImplParser {
        @Override
        public List<String>  parseArray(JSONArray array) throws Throwable {
            List<String> result = new ArrayList<>();
            if (array != null) {
                final int length = array.length();
                for (int i = 0; i < length; i++) {
                    result.add(array.getJSONObject(i).optString("description"));
                }
            }
            return result;
        }

        @Override
        public  String parseObject(JSONObject obj) throws Throwable{
            return "";
        };
    }

}