package com.hugboga.custom.data.request;

import android.content.Context;

import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.hugboga.custom.data.bean.TravelListAllBean;

import java.util.HashMap;

/**
 * 订单行程列表 解析器
 * Created by admin on 2016/3/23.
 */
public abstract class RequestTravel extends BaseRequest<TravelListAllBean> {
    public RequestTravel(Context context, int orderShowType, int limit, int offset) {
        super(context);
        map = new HashMap<String, Object>();
        map.put("searchType", orderShowType);
        map.put("limit", limit);
        map.put("offset", offset);

    }

}
