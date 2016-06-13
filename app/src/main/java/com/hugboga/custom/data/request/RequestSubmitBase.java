package com.hugboga.custom.data.request;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.huangbaoche.hbcframe.data.parser.ImplParser;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.hugboga.custom.data.bean.OrderBean;
import com.hugboga.custom.data.bean.OrderInfoBean;
import com.hugboga.custom.utils.Config;

import org.json.JSONObject;
import org.xutils.http.HttpMethod;

import java.util.TreeMap;

/**
 * 下单请求公用基类
 * Created by admin on 2016/3/22.
 */
public class RequestSubmitBase extends BaseRequest<OrderInfoBean> {

    public RequestSubmitBase(Context context, OrderBean orderBean) {
        super(context);
        map = new TreeMap();
        map.put("orderNo", orderBean.orderNo);
        map.put("orderChannel", Config.channelId);
        map.put("orderType", orderBean.orderType);
        map.put("urgentFlag", orderBean.urgentFlag);
        if (orderBean.checkInPrice != null)
            map.put("checkInPrice", orderBean.checkInPrice);
        map.put("priceChannel", orderBean.orderPrice);
        map.put("priceMark", orderBean.priceMark);
        map.put("adultNum", orderBean.adult);
        map.put("childNum", orderBean.child);

        map.put("childNum", orderBean.child);
        map.put("luggageNumber",orderBean.luggageNum);
//        if (orderBean.childSeat != null)
//            map.put("childSeat", TextUtils.join(",", orderBean.childSeat));


        map.put("childSeat",orderBean.childSeatStr);

        map.put("userName", orderBean.contactName);
        map.put("serviceCityId", orderBean.serviceCityId);
        map.put("serviceEndCityid", orderBean.serviceEndCityid);

        map.put("serviceTimeL", orderBean.serviceTime);
        map.put("serviceEndTimeL", orderBean.serviceEndTime);
        if(orderBean.serviceStartTime!=null)
            map.put("serviceRecTime", orderBean.serviceStartTime);
        map.put("totalDays", orderBean.totalDays);
        map.put("expectedCompTime", orderBean.expectedCompTime);

        map.put("startAddress", orderBean.startAddress);
        map.put("startAddressDetail", orderBean.startAddressDetail);
        map.put("startAddressPoi", orderBean.startLocation);
        map.put("destAddress", orderBean.destAddress);
        map.put("destAddressDetail", orderBean.destAddressDetail);
        map.put("destAddressPoi", orderBean.terminalLocation);
        map.put("distance", orderBean.distance);
        map.put("carTypeId", orderBean.carType);
        map.put("carSeatNum", orderBean.seatCategory);
        map.put("carDesc", orderBean.carDesc);
        map.put("userRemark", orderBean.memo);
        map.put("userEx", orderBean.userEx);
        map.put("priceActual",orderBean.priceActual);

        map.put("serviceAreaCode", orderBean.serviceAreaCode);
        map.put("serviceAddressTel", orderBean.serviceAddressTel);

        map.put("realUserEx",orderBean.realUserEx);
        map.put("userEx",orderBean.userEx);
        map.put("realSendSms",orderBean.realSendSms);
        map.put("travelFund",orderBean.travelFund);
        map.put("guideCollectId",orderBean.guideCollectId);

        map.put("coupId", orderBean.coupId);
        map.put("coupPriceInfo", orderBean.coupPriceInfo);

//        if (orderBean.contact != null && orderBean.contact.size() > 0) {
//            map.put("userAreaCode1", orderBean.contact.get(0).areaCode);
//            map.put("userMobile1", orderBean.contact.get(0).tel);
//        }
//        if (orderBean.contact.size()>1&&orderBean.contact.get(1) != null) {
//            map.put("userAreaCode2", orderBean.contact.get(1).areaCode);
//            map.put("userMobile2", orderBean.contact.get(1).tel);
//        }
//        if (orderBean.contact.size()>2&&orderBean.contact.get(2) != null) {
//            map.put("userAreaCode3", orderBean.contact.get(2).areaCode);
//            map.put("userMobile3", orderBean.contact.get(2).tel);
//        }

        if (orderBean.orderType != null && orderBean.orderType.equals(5)) {//如果是SKU  传
            map.put("lineSubject", orderBean.lineSubject);
            map.put("lineDescription", orderBean.lineDescription);
            map.put("orderGoodsType", orderBean.orderGoodsType);
            map.put("goodNo", orderBean.goodsNo);
        }

    }


    @Override
    public HttpMethod getHttpMethod() {
        return HttpMethod.POST;
    }


    @Override
    public ImplParser getParser() {
        return new ImplParser() {
//            @Override
//            public String parseObject(JSONObject obj) throws Throwable {
//                return obj.optString("orderno");
//            }

            @Override
            public OrderInfoBean parseObject(JSONObject obj) throws Throwable {
                Gson gson = new Gson();
                OrderInfoBean  orderInfoBean = gson.fromJson(obj.toString(),OrderInfoBean.class);
                return orderInfoBean;
            }
        };
    }
}
