package com.hugboga.custom.data.parser;

import com.huangbaoche.hbcframe.data.parser.ImplParser;
import com.hugboga.custom.data.bean.UserBean;

import org.json.JSONObject;

/**
 * Created by admin on 2016/3/8.
 */
public class ParserLogin extends ImplParser {
    @Override
    public UserBean parseObject(JSONObject jsonObj) throws Throwable {
        UserBean bean = new UserBean();
        bean.avatar = jsonObj.optString("avatar");
        bean.nickname = jsonObj.optString("nickName");
        bean.gender = jsonObj.optString("gender");
        bean.ageType = jsonObj.optInt("ageType", -1);
        bean.signature = jsonObj.optString("signature");
        bean.userID = jsonObj.optString("userId");
        bean.areaCode = jsonObj.optString("areaCode");
        bean.mobile = jsonObj.optString("mobile");
        bean.userToken = jsonObj.optString("userToken");
        bean.weakPassword = jsonObj.optBoolean("weakPassword");
        bean.weakPasswordMsg = jsonObj.optString("weakPasswordMsg");
        bean.imToken = jsonObj.optString("IMtoken");
        bean.openid = jsonObj.optString("openid");
        bean.isNotRegister = jsonObj.optInt("isNotRegister", -1);
        return bean;
    }
}
