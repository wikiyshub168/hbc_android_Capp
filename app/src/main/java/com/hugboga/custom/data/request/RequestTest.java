package com.hugboga.custom.data.request;

import android.content.Context;

import com.huangbaoche.hbcframe.data.parser.ImplParser;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.hugboga.custom.data.bean.TestBean;
import com.hugboga.custom.data.net.NewParamsBuilder;
import com.hugboga.custom.data.net.UrlLibs;

import org.xutils.http.HttpMethod;
import org.xutils.http.annotation.HttpRequest;

import java.util.Map;

/**
 * Created by admin on 2016/2/25.
 */

@HttpRequest(
        path = UrlLibs.SERVER_IP_CHECK_APP_VERSION, builder = NewParamsBuilder.class)
public class RequestTest extends BaseRequest<TestBean> {

    public RequestTest(Context context) {
        super(context);
    }

    @Override
    public Map<String, Object> getDataMap() {
        return null;
    }

    @Override
    public ImplParser getParser() {
        return null;
    }

    @Override
    public HttpMethod getHttpMethod() {
        return HttpMethod.POST;
    }


}
