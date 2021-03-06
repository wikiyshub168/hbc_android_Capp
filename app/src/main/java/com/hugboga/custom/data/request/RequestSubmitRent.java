package com.hugboga.custom.data.request;

import android.content.Context;

import com.hugboga.custom.data.bean.OrderBean;
import com.hugboga.custom.data.net.NewParamsBuilder;
import com.hugboga.custom.data.net.UrlLibs;

import org.xutils.http.annotation.HttpRequest;

/**
 * Created by admin on 2016/3/22.
 */
@HttpRequest(path = UrlLibs.SERVER_IP_SUBMIT_SINGLE, builder = NewParamsBuilder.class)
public class RequestSubmitRent extends RequestSubmitBase {
    public RequestSubmitRent(Context context, OrderBean orderBean) {
        super(context, orderBean);
        map.put("startCityId", orderBean.serviceCityId);
        map.put("startCityName", orderBean.serviceCityName);
        map.put("destCityId", orderBean.serviceEndCityid);
        map.put("destCityName", orderBean.serviceEndCityName);
        map.put("serviceDate", orderBean.serviceTime);
        map.put("serviceEndDate", orderBean.serviceEndTime);
//        map.put("serviceTimeL", null);
//        map.put("serviceEndTimeL", null);
        map.put("oneCityTravel", orderBean.oneCityTravel);
        map.put("isHalfDaily", orderBean.isHalfDaily);
        map.put("serviceLocalDays", orderBean.inTownDays);
        map.put("serviceNonlocalDays", orderBean.outTownDays);
        map.put("journeyComment", orderBean.journeyComment);
        map.put("serviceDate", orderBean.serviceTime);
        map.put("serviceEndDate", orderBean.serviceEndTime);
        map.put("serviceDepartTime", orderBean.serviceTime);// + " " + orderBean.serviceStartTime);
        map.put("servicePassCitys", orderBean.stayCityListStr);
        map.put("passbyPois", orderBean.skuPoi);

        map.put("userRemark", orderBean.userRemark);
        map.put("priceChannel", orderBean.priceChannel);
        map.put("childSeatNum", orderBean.childSeatNum);
        map.put("luggageNum", orderBean.luggageNum);
        map.put("realUserName", orderBean.realUserName);
        map.put("realAreaCode", orderBean.realAreaCode);
        map.put("realMobile", orderBean.realMobile);
        map.put("isRealUser", orderBean.isRealUser);
//        map.put("startAddressPoi", orderBean.startAddressPoi);
//        map.put("destAddressPoi", orderBean.destAddressPoi);
        map.put("userName", orderBean.userName);
        map.put("memo", orderBean.userRemark);


        map.put("destAddressDetail", orderBean.destAddressDetail);

    }

    @Override
    public String getUrlErrorCode() {
        return "40084";
    }
}
