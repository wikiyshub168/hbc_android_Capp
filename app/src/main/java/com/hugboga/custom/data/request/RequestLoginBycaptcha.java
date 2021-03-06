package com.hugboga.custom.data.request;

import android.content.Context;

import com.huangbaoche.hbcframe.data.parser.ImplParser;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.hugboga.custom.data.bean.UserBean;
import com.hugboga.custom.data.net.NewParamsBuilder;
import com.hugboga.custom.data.net.UrlLibs;
import com.hugboga.custom.data.parser.ParserUserInfo;
import com.hugboga.custom.utils.CommonUtils;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;

import org.xutils.http.HttpMethod;
import org.xutils.http.annotation.HttpRequest;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by zhangqiang on 17/5/16.
 */
@HttpRequest(path = UrlLibs.SERVER_IP_LOGIN_BYCAPTCHA, builder = NewParamsBuilder.class)
public class RequestLoginBycaptcha extends BaseRequest<UserBean> {
    public String areaCode;
    public String mobile;
    public String captcha;
    public int sourceType;//1.订单 2.活动 3.自然用户
    public int source;//
    public int loginMethod;
    public RequestLoginBycaptcha(Context context,String areaCode,String mobile,String captcha,Integer sourceType,Integer source, Integer loginMethod) {
        super(context);
        this.areaCode = CommonUtils.removePhoneCodeSign(areaCode);
        this.mobile = mobile;
        this.captcha = captcha;
        this.sourceType =sourceType;
        this.source = source;
        this.loginMethod = loginMethod;
    }

    @Override
    public Map<String, Object> getDataMap() {
        TreeMap map = new TreeMap<String, Object>();
        map.put("areaCode", CommonUtils.removePhoneCodeSign(areaCode));
        map.put("mobile", mobile);
        map.put("captcha", captcha);
        map.put("sourceType",sourceType);
        map.put("source", source);
        map.put("loginMethod", loginMethod);//验证码登录传3，语音验证码登录传4
        map.put("distinctid", SensorsDataAPI.sharedInstance(getContext()).getAnonymousId());
        map.put("loginChannel",1);//1:capp 2:m 3:pc
        return map;
    }
    @Override
    public ImplParser getParser() {
        return new ParserUserInfo();
    }
    @Override
    public String getUrlErrorCode() {
        return "40145";
    }

    @Override
    public HttpMethod getHttpMethod() {
        return HttpMethod.POST;
    }
}
