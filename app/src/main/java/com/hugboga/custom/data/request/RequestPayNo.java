package com.hugboga.custom.data.request;

import android.content.Context;

import com.huangbaoche.hbcframe.data.parser.ImplParser;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.hugboga.custom.BuildConfig;
import com.hugboga.custom.MyApplication;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.net.NewParamsBuilder;
import com.hugboga.custom.data.net.UrlLibs;
import com.hugboga.custom.data.parser.ParserWxPay;
import com.hugboga.custom.utils.Config;

import org.json.JSONObject;
import org.xutils.http.HttpMethod;
import org.xutils.http.annotation.HttpRequest;

import java.util.HashMap;

/**
 * 支付宝 支付id
 * Created by admin on 2016/3/23.
 */

@HttpRequest(path = "", builder = NewParamsBuilder.class)
public class RequestPayNo extends BaseRequest<Object> {

    public int payType;
    public int apiType;

    /**
     * orderID 订单ID<br/>
     * payPrice 支付金额<br/>
     * payType 1 支付宝 ，2 微信，3.googleplay微信
     * couponID 优惠券<br/>
     */
    public RequestPayNo(Context context, String orderId, double payPrice, int _payType, String couponID){
        this(context, orderId, payPrice, _payType, couponID, 0);
    }

    public RequestPayNo(Context context, String orderId, double payPrice, int _payType, String couponID, int apiType) {
        super(context);
        this.apiType = apiType;
        map =new HashMap<String,Object>();
        map.put("appId", Config.getImei());
        map.put("appEnv","android");
        map.put("orderNo",orderId);
        map.put("actualPrice",payPrice);
        map.put("coupId", couponID);

        if (_payType == Constants.PAY_STATE_WECHAT && Constants.CHANNEL_GOOGLE_PLAY.equals(BuildConfig.FLAVOR)) {
            map.put("payType", 3);
        } else {
            map.put("payType", _payType);
        }
        this.payType = _payType;
    }

    @Override
    public HttpMethod getHttpMethod() {
        return  HttpMethod.POST;
    }

    @Override
    public String getUrlErrorCode() {
        if (apiType == 1) {
            return "40151";
        } else {
            return "40068";
        }
    }

    @Override
    public String getUrl() {
        if (apiType == 1) {
            return UrlLibs.API_COUPON_PAY;
        } else {
            return UrlLibs.SERVER_IP_ORDER_PAY_ID;
        }
    }

    @Override
    public ImplParser getParser() {
        if(payType == Constants.PAY_STATE_WECHAT){
            return new ParserWxPay();
        }else{
            return new ImplParser() {
                @Override
                public Object parseObject(JSONObject obj) throws Throwable {
                    return obj.optString("payurl");
                }
            };
        }
    }
}
