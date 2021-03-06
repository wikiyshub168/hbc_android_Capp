package com.hugboga.custom.data.request;

import android.content.Context;

import com.huangbaoche.hbcframe.data.parser.ImplParser;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.bean.UserEntity;
import com.hugboga.custom.data.net.NewParamsBuilder;
import com.hugboga.custom.data.net.UrlLibs;

import org.xutils.http.HttpMethod;
import org.xutils.http.annotation.HttpRequest;

import java.util.HashMap;

/**
 * Created by qingcha on 16/5/24.
 */
@HttpRequest(path = UrlLibs.COLLECT_GUIDES_ID, builder = NewParamsBuilder.class)
public class RequestCollectGuidesId extends BaseRequest {

    private String guideId;

    public RequestCollectGuidesId(Context context, String guideId) {
        this(context, guideId, null);
    }

    public RequestCollectGuidesId(Context context, String guideId, String sharingUserId) {
        super(context);
        this.guideId = guideId;
        map = new HashMap<String, Object>();
        map.put("source", Constants.REQUEST_SOURCE);
        map.put("userId", UserEntity.getUser().getUserId(context));
        map.put("guideId", guideId);//司导ID
        map.put("sharingUserId", sharingUserId);//非必须 分享用户ID
    }

    @Override
    public ImplParser getParser() {
        return null;
    }

    @Override
    public HttpMethod getHttpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public String getUrlErrorCode() {
        return "40025";
    }

    public String getGuideId() {
        return guideId;
    }
}
