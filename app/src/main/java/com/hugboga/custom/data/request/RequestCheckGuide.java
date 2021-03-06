package com.hugboga.custom.data.request;

import android.content.Context;
import android.text.TextUtils;

import com.huangbaoche.hbcframe.data.parser.ImplParser;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.hugboga.custom.data.net.NewParamsBuilder;
import com.hugboga.custom.data.net.UrlLibs;
import com.hugboga.custom.utils.JsonUtils;

import org.xutils.http.HttpMethod;
import org.xutils.http.annotation.HttpRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

@HttpRequest(path = "", builder = NewParamsBuilder.class)
public class RequestCheckGuide extends BaseRequest {

    private String goodsNo;

    public RequestCheckGuide(Context context, CheckGuideBeanList checkGuideBeanList) {
        super(context);
        map = new HashMap<String, Object>();
        bodyEntity = JsonUtils.toJson(checkGuideBeanList);
        errorType = ERROR_TYPE_IGNORE;
    }

    public RequestCheckGuide(Context context, CheckGuideBean checkGuideBean, String goodsNo) {
        super(context);
        this.goodsNo = goodsNo;
        map = new HashMap<String, Object>();
        CheckGuideBeanList checkGuideBeanList = new CheckGuideBeanList();
        checkGuideBeanList.guideCheckInfos.add(checkGuideBean);
        checkGuideBeanList.goodsNo = goodsNo;
        bodyEntity = JsonUtils.toJson(checkGuideBeanList);
        errorType = ERROR_TYPE_IGNORE;
    }

    public RequestCheckGuide(Context context, CheckGuideBean checkGuideBean) {
        this(context, checkGuideBean, null);
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
        return TextUtils.isEmpty(goodsNo) ? "40143" : "40176";
    }

    @Override
    public String getUrl() {
        return TextUtils.isEmpty(goodsNo) ? UrlLibs.API_GUIDE_AVAILABLE_CHECK : UrlLibs.API_CHECK_SKU;
    }

    public static class CheckGuideBeanList implements Serializable {
        public ArrayList<CheckGuideBean> guideCheckInfos;
        public ArrayList<CheckGuideBean> guideSubOrderInfos;
        public String goodsNo;

        public CheckGuideBeanList() {
            guideCheckInfos = new ArrayList<CheckGuideBean>();
            guideSubOrderInfos = new ArrayList<CheckGuideBean>();
        }

        public void updateFirstDayServiceTime(String time) {
            if (guideSubOrderInfos.size() <= 0 || TextUtils.isEmpty(time)) {
                return;
            }
            CheckGuideBean checkGuideBean = guideSubOrderInfos.get(0);
            checkGuideBean.startTime = time;
        }
    }

    public static class CheckGuideBean implements Serializable {
        public String guideId;
        public int cityId;
        public int orderType;
        public String startTime;
        public String endTime;
    }
}