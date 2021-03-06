package com.hugboga.custom.data.request;

import android.content.Context;

import com.huangbaoche.hbcframe.data.parser.ImplParser;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.hugboga.custom.data.bean.MostFitBean;
import com.hugboga.custom.data.bean.UserEntity;
import com.hugboga.custom.data.net.NewParamsBuilder;
import com.hugboga.custom.data.net.UrlLibs;
import com.hugboga.custom.data.parser.ParseMostFit;

import org.xutils.http.annotation.HttpRequest;

import java.util.Map;
import java.util.TreeMap;


@HttpRequest(path = UrlLibs.MOSTFIT, builder = NewParamsBuilder.class)
public class RequestMostFit extends BaseRequest<MostFitBean> {


    String useOrderPrice;
    String priceChannel;// 渠道价格   [必填]
    String serviceTime; // 服务时间   [必填]
    String carTypeId; // 1-经济 2-舒适 3-豪华 4-奢华    [必填]
    String carSeatNum; // 车座数    [必填]
    String serviceCityId; // 服务城市ID    [必填]
    String serviceCountryId; // 服务所在国家ID   [必填]
    String totalDays; // 日租天数，[日租必填]
    String distance; // 预估路程公里数 [必填]
    String expectedCompTime; // 接送机预计完成时间[非日租必填]
    String orderType;
    String carModelId;
    Integer isPickupTransfer;// 组合单是否紧接送机 1:是 2:不是


    public RequestMostFit(Context context, String useOrderPrice,
                          String priceChannel,String serviceTime,
                          String carTypeId,String carSeatNum,
                          String serviceCityId,String serviceCountryId,
                          String totalDays,String distance,
                          String expectedCompTime,String orderType,String carModelId, Integer isPickupTransfer) {
        super(context);
        this.useOrderPrice = useOrderPrice;
        this.priceChannel = priceChannel;
        this.serviceTime = serviceTime;

        this.carTypeId = carTypeId;
        this.carSeatNum = carSeatNum;
        this.serviceCityId = serviceCityId;
        this.serviceCountryId = serviceCountryId;
        this.totalDays = totalDays;
        this.distance = distance;
        this.expectedCompTime = expectedCompTime;
        this.orderType = orderType;
        this.carModelId = carModelId;
        this.isPickupTransfer = isPickupTransfer;
    }

    @Override
    public Map<String, Object> getDataMap() {
        TreeMap map = new TreeMap<String, Object>();
        map.put("useOrderPrice", useOrderPrice);
        map.put("priceChannel", priceChannel);
        map.put("serviceTime", serviceTime);
        map.put("carTypeId", carTypeId);
        map.put("userId", UserEntity.getUser().getUserId(getContext()));

        map.put("carSeatNum", carSeatNum);
        map.put("serviceCityId", serviceCityId);
        map.put("serviceCountryId", serviceCountryId);
        map.put("totalDays", totalDays);
        map.put("distance", distance);
        map.put("expectedCompTime", expectedCompTime);
        map.put("orderType",orderType);
        map.put("carModelId",carModelId);

        if (isPickupTransfer != null) {
            map.put("isPickupTransfer", isPickupTransfer);
        }
        return map;
    }

    @Override
    public ImplParser getParser() {
        return new ParseMostFit();
    }

    @Override
    public String getUrlErrorCode() {
        return "40057";
    }

}