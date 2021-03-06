package com.hugboga.custom.data.parser;

import com.huangbaoche.hbcframe.data.parser.ImplParser;

import org.json.JSONObject;

/**
 * 解析融云IM获取Token
 * Created by ZHZEPHI on 2016/3/27.
 */
public class ParserImToken extends ImplParser {

    @Override
    public Object parseObject(JSONObject obj) throws Throwable {
        return obj.toString();
    }
}
