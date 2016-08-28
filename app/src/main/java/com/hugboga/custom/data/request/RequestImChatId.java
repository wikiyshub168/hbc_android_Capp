package com.hugboga.custom.data.request;

import android.content.Context;

import com.google.gson.Gson;
import com.huangbaoche.hbcframe.data.parser.ImplParser;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.hugboga.custom.data.bean.ImChatInfo;
import com.hugboga.custom.data.bean.SkuItemBean;
import com.hugboga.custom.data.net.NewParamsBuilder;
import com.hugboga.custom.data.net.UrlLibs;

import org.json.JSONObject;
import org.xutils.http.HttpMethod;
import org.xutils.http.annotation.HttpRequest;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/8/27.
 */
@HttpRequest(path = UrlLibs.GET_CHAT_ID, builder = NewParamsBuilder.class)
public class RequestImChatId extends BaseRequest {

    public RequestImChatId(Context context, String userId,String userType,String targetId,String targetType) {
        super(context);
        map = new HashMap<String, Object>();
        map.put("userId",userId);
        map.put("userType",userType);
        map.put("targetId",targetId);
        map.put("targetType",targetType);
    }

    @Override
    public ImplParser getParser() {
        return new IMInfoParser();
    }

    @Override
    public HttpMethod getHttpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public String getUrlErrorCode() {
        return "";
    }


    private static class IMInfoParser extends  ImplParser{
        @Override
        public ImChatInfo parseObject(JSONObject obj) throws Throwable {
            ImChatInfo imChatInfo = new ImChatInfo();
            imChatInfo.imCount = obj.optInt("imCount");
            imChatInfo.neTargetId = obj.optString("neTargetId");
            imChatInfo.neUserId = obj.optString("neUserId");
            imChatInfo.targetAvatar = obj.optString("targetAvatar");
            imChatInfo.targetId = obj.optString("targetId");
            imChatInfo.targetType = obj.optString("targetType");
            imChatInfo.targetName = obj.optString("targetName");
            imChatInfo.neUserId = obj.optString("neUserid");
            imChatInfo.userType = obj.optString("userType");
            imChatInfo.inBlack = obj.optInt("inBlack");
            return imChatInfo;
        }
    }


}
