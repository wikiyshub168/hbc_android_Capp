package com.hugboga.custom.utils;

import android.text.TextUtils;

import com.huangbaoche.hbcframe.data.net.ExceptionInfo;
import com.huangbaoche.hbcframe.data.net.HttpRequestListener;
import com.huangbaoche.hbcframe.data.net.HttpRequestUtils;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.hugboga.custom.MyApplication;
import com.hugboga.custom.data.request.RequestReport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by on 16/10/17.
 */
public class ApiReportHelper {

    private static final String PARAMS_TRACEID = "traceId";
    private static final String PARAMS_NETWORK = "network";
    private static final String PARAMS_LATENCY = "latency";

    private static final int REPORT_VALVE_COUNT = 10; //10条上报一次

    private static ApiReportHelper instance;

    private JSONArray reportList;

    private ApiReportHelper() {
        reportList = new JSONArray();
    }

    public static ApiReportHelper getInstance() {
        if (instance == null) {
            instance = new ApiReportHelper();
        }
        return instance;
    }

    public void addReport(BaseRequest request) {
        if (request == null || request.responseHeaders == null) {
            return;
        }
        Map<String, List<String>> responseHeaders = request.responseHeaders;
        long spendTime = 0;
        String traceId = "";
        String currentNetwork = "";
        try {
            spendTime = CommonUtils.getCountLong(responseHeaders.get("X-Android-Received-Millis").get(0)) - CommonUtils.getCountLong(responseHeaders.get("X-Android-Sent-Millis").get(0));
            if (responseHeaders.containsKey(PARAMS_TRACEID) && responseHeaders.get(PARAMS_TRACEID) != null) {
                traceId = responseHeaders.get(PARAMS_TRACEID).get(0);
            }
            currentNetwork = NetWorkUtils.getCurrentNetwork();
        } catch (Exception e) {
          return;
        }

        if (TextUtils.isEmpty(traceId) || spendTime <= 0) {
            return;
        }

        JSONObject obj = new JSONObject();
        try {
            obj.put(PARAMS_TRACEID, traceId);        // 请求ID
            obj.put(PARAMS_NETWORK, currentNetwork); // 网络类型
            obj.put(PARAMS_LATENCY, "" + spendTime); // 客户端请求耗时，精确到毫秒
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (reportList.length() >= REPORT_VALVE_COUNT) {
            requestReport(reportList.toString());
            reportList = new JSONArray();
        } else {
            reportList.put(obj);
        }
    }

    public void commitAllReport() {
        if (reportList != null && reportList.length() > 0) {
            requestReport(reportList.toString());
            reportList = new JSONArray();
        }
    }

    public void requestReport(String body) {
        if (TextUtils.isEmpty(body)) {
            return;
        }
        RequestReport requestReport = new RequestReport(MyApplication.getAppContext(), body);
        HttpRequestUtils.request(MyApplication.getAppContext(), requestReport, new HttpRequestListener() {
            @Override
            public void onDataRequestSucceed(BaseRequest request) {

            }

            @Override
            public void onDataRequestCancel(BaseRequest request) {

            }

            @Override
            public void onDataRequestError(ExceptionInfo errorInfo, BaseRequest request) {

            }
        }, false);
    }
}