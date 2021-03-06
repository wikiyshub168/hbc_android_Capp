package com.hugboga.custom.data.request;

import android.content.Context;

import com.huangbaoche.hbcframe.data.parser.ImplParser;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.hugboga.custom.data.net.NewParamsBuilder;
import com.hugboga.custom.data.net.UrlLibs;

import org.xutils.http.HttpMethod;
import org.xutils.http.annotation.HttpRequest;

import java.util.HashMap;

/**
 * Created by admin on 2016/3/23.
 */
@HttpRequest(path = UrlLibs.SERVER_IP_ORDER_CANCEL, builder = NewParamsBuilder.class)
public class RequestOrderCancel extends BaseRequest {

    public RequestOrderCancel(Context context, String orderID, double cancelPrice, String reason) {
        super(context);
        map = new HashMap<String, Object>();
        map.put("orderNo", orderID);
        map.put("cancelPrice", cancelPrice);
        map.put("reason", reason);
    }

    @Override
    public HttpMethod getHttpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public ImplParser getParser() {
        return null;
    }

    @Override
    public String getUrlErrorCode() {
        return "40065";
    }
}
