package com.hugboga.custom.data.request;

import android.content.Context;
import com.huangbaoche.hbcframe.data.parser.ImplParser;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.bean.CollectGuideNewBean;
import com.hugboga.custom.data.bean.UserEntity;
import com.hugboga.custom.data.net.NewParamsBuilder;
import com.hugboga.custom.data.net.UrlLibs;
import com.hugboga.custom.data.parser.HbcParser;

import org.xutils.http.HttpMethod;
import org.xutils.http.annotation.HttpRequest;

import java.util.HashMap;

/**
 * Created by qingcha on 16/5/23.
 */
@HttpRequest(path = UrlLibs.COLLECT_GUIDES_LIST, builder = NewParamsBuilder.class)
public class RequestCollectGuideList extends BaseRequest<CollectGuideNewBean> {
    public RequestCollectGuideList(Context context, int offset) {
        super(context);
        map = new HashMap<String, Object>();
        map.put("source", Constants.REQUEST_SOURCE);
        map.put("userId", UserEntity.getUser().getUserId(context));
        map.put("offset", offset);
        map.put("limit", Constants.DEFAULT_PAGESIZE);
    }

    @Override
    public ImplParser getParser() {
        return new HbcParser(UrlLibs.COLLECT_GUIDES_LIST, CollectGuideNewBean.class);
    }

    @Override
    public HttpMethod getHttpMethod() {
        return HttpMethod.GET;
    }

    @Override
    public String getUrlErrorCode() {
        return "40023";
    }

}
