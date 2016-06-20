package com.hugboga.custom.data.request;

import android.content.Context;

import com.google.gson.Gson;
import com.huangbaoche.hbcframe.data.parser.ImplParser;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.hugboga.custom.data.bean.HomeData;
import com.hugboga.custom.data.net.NewParamsBuilder;
import com.hugboga.custom.data.net.UrlLibs;

import org.json.JSONObject;
import org.xutils.http.HttpMethod;
import org.xutils.http.annotation.HttpRequest;

import java.util.HashMap;

/**
 * 首页数据请求
 * Created by admin on 2016/3/2.
 */
@HttpRequest(path = UrlLibs.API_HOME, builder = NewParamsBuilder.class)
public class RequestHome extends BaseRequest<HomeData> {

    public RequestHome(Context context) {
        super(context);
        map = new HashMap<String, Object>();
    }

    @Override
    public HttpMethod getHttpMethod() {
        return HttpMethod.GET;
    }

    @Override
    public ImplParser getParser() {
        return new DataParser();
    }

    @Override
    public String getUrlErrorCode() {
        return "40041";
    }

    private static class DataParser extends ImplParser {
        @Override
        public Object parseObject(JSONObject obj) {
            Gson gson = new Gson();
            HomeData homeBean = gson.fromJson(obj.toString(), HomeData.class);
            return homeBean;
        }
    }
}
