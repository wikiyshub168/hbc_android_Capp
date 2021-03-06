package com.hugboga.custom.statistic.event;

import com.hugboga.custom.statistic.StatisticConstant;
import com.hugboga.custom.statistic.bean.EventPayBean;

import java.util.HashMap;

/**
 * Created by qingcha on 16/8/19.
 */
public class EventPayResult extends EventBase{

    private EventPayBean eventPayBean;
    private boolean payResult;

    public EventPayResult(EventPayBean eventPayBean, boolean payResult) {
        this.eventPayBean = eventPayBean;
        this.payResult = payResult;
    }

    @Override
    public String getEventId() {
        //goodsNo 订单类型：1-接机、2-送机、3-包车游、4-次租(单次接送)、5-固定线路、6-推荐线路；
        String result = null;
        if (eventPayBean == null) {
            return result;
        }
        switch (eventPayBean.orderType) {
            case 1:
                if (eventPayBean.isSeckills) {
                    result = payResult ? StatisticConstant.LAUNCH_PAYSUCCEED_J_MS : StatisticConstant.LAUNCH_PAYFAILED_J_MS;
                } else {
                    result = payResult ? StatisticConstant.LAUNCH_PAYSUCCEED_J : StatisticConstant.LAUNCH_PAYFAILED_J;
                }
                break;
            case 2:
                result = payResult ? StatisticConstant.LAUNCH_PAYSUCCEED_S : StatisticConstant.LAUNCH_PAYFAILED_S;
                break;
            case 3:
            case 888:
                result = payResult ? StatisticConstant.LAUNCH_PAYSUCCEED_R : StatisticConstant.LAUNCH_PAYFAILED_R;
                break;
            case 4:
                result = payResult ? StatisticConstant.LAUNCH_PAYSUCCEED_C : StatisticConstant.LAUNCH_PAYFAILED_C;
                break;
            case 5:
            case 6:
                result = payResult ? StatisticConstant.LAUNCH_PAYSUCCEED_SKU : StatisticConstant.LAUNCH_PAYFAILED_SKU;
                break;
        }
        return result;
    }

    @Override
    public HashMap getParamsMap() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        if (eventPayBean == null) {
            return map;
        }
        EventUtil eventUtil = EventUtil.getInstance();
        map.put("source", eventUtil.source);//触发来源 首页、搜索、城市页、收藏司导列表、司导个人页
        map.put("carstyle", eventPayBean.carType);//车型  经济5座，舒适5座，经济7座……
        map.put("guestcount", eventPayBean.guestcount);//乘客人数 1，2，3，4......11
        map.put("forother", eventPayBean.forother);//为他人订车 是、否
        map.put("paystyle", eventPayBean.paystyle);//支付方式 支付宝、微信支付、无
        map.put("paysource", eventUtil.isRePay ? "失败重新支付" : eventPayBean.paysource);//支付来源 下单过程中、失败重新支付、未支付订单详情页
        map.put("orderstatus", eventPayBean.orderStatus != null ? eventPayBean.orderStatus.name : "");//订单状态 未支付、已支付、已接单……

        switch (eventPayBean.orderType) {
            case 1:
                map.put("pickwait", EventUtil.booleanTransform(eventPayBean.isFlightSign));//接机举牌等待 是、否
                break;
            case 2:
                map.put("assist", EventUtil.booleanTransform(eventPayBean.isCheckin));//协助办理登记 是、否
                break;
            case 3:
            case 888:
                map.put("selectG", EventUtil.booleanTransform(eventPayBean.isSelectedGuide));//选择已收藏司导 是、否
                map.put("days", eventPayBean.days);
                break;
        }
        return map;
    }
}
