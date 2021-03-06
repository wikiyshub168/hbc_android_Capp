package com.hugboga.custom.data.parser;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.huangbaoche.hbcframe.data.parser.ImplParser;
import com.hugboga.custom.data.bean.CollectGuideBean;

import org.json.JSONObject;

import java.util.ArrayList;

public class ParserCollectGuideList extends ImplParser {

    @Override
    public Object parseObject(JSONObject obj) throws Throwable {
        Gson gson = new Gson();
        CollectGuideList collectGuideList = gson.fromJson(obj.toString(), CollectGuideList.class);
        if (collectGuideList != null) {
            return collectGuideList;
        } else {
            return null;
        }
    }

    public static class CollectGuideList {
        @SerializedName("guides")
        public ArrayList<CollectGuideBean> listDate;

        public int count;
    }
}


