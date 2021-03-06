package com.hugboga.custom.data.request;

import android.content.Context;

import com.hugboga.custom.data.bean.OrderBean;

/**
 * Created by admin on 2016/3/22.
 */
public class RequestSubmitPick extends RequestSubmitBase {
    public RequestSubmitPick(Context context, OrderBean orderBean) {
        super(context, orderBean);
        map.put("memo", orderBean.userRemark);

        map.put("flightBrandSign", orderBean.brandSign);
        map.put("isArrivalVisa", orderBean.visa);

        map.put("destAddress",orderBean.destAddress);
        map.put("flightBrandSign",orderBean.flightBrandSign);
        map.put("carDesc",orderBean.carDesc);

        map.put("childSeat",orderBean.childSeatStr);
        map.put("priceChannel",orderBean.priceChannel);
        map.put("userRemark", orderBean.userRemark);

        map.put("priceFlightBrandSign",orderBean.priceFlightBrandSign);
        map.put("isFlightSign",orderBean.isFlightSign);

        if (orderBean.isSeckills) {
            map.put("limitedSaleNo",orderBean.timeLimitedSaleNo);
            map.put("limitedSaleScheduleNo",orderBean.timeLimitedSaleScheduleNo);
            map.put("priceTicket",orderBean.priceTicket);
        }

        if (orderBean.flightBean != null) {
            map.put("flightNo", orderBean.flightBean.flightNo);
            map.put("flightAirportCode", orderBean.flightBean.depAirportCode);
            map.put("flightAirportName", orderBean.flightBean.depAirportName);
            map.put("flightDestCode", orderBean.flightBean.arrivalAirportCode);
            map.put("flightDestName", orderBean.flightBean.arrAirportName);
            map.put("flightFlyTimeL", orderBean.flightBean.depDate + " " + orderBean.flightBean.depTime + ":00");
            map.put("flightArriveTimeL", orderBean.flightBean.arrDate + " " + orderBean.flightBean.arrivalTime + ":00");
            map.put("flightAirportBuiding", orderBean.flightBean == null ? null : orderBean.flightBean.arrTerminal);


            map.put("flightDeptCityName",orderBean.flightBean.depCityName);// 起飞机场城市名
            map.put("flightDestCityName",orderBean.flightBean.arrCityName);// 降落机场城市名
        }
    }
}
