package com.huangbaoche.hbcframe.data.net;


import android.content.Context;
import android.view.View;

import com.huangbaoche.hbcframe.HbcConfig;
import com.huangbaoche.hbcframe.data.bean.UserSession;
import com.huangbaoche.hbcframe.data.parser.ImplParser;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.huangbaoche.hbcframe.util.MLog;
import com.huangbaoche.hbcframe.util.NetWork;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.x;

import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLHandshakeException;


/**
 * 请求类
 */
public class HttpRequestUtils {



    public static Callback.Cancelable request(Context mContext,final BaseRequest request, final HttpRequestListener listener,View btn){

        if (!NetWork.isNetworkAvailable(mContext)) {
            ExceptionInfo result = new ExceptionInfo(ExceptionErrorCode.ERROR_CODE_NET_UNAVAILABLE, null);
            if (listener != null)
                listener.onDataRequestError(result, request);
            if (btn != null)btn.setEnabled(true);
            return null;
        }
        if (!checkAccessKey(mContext)){
            requestAccessKey(mContext,request,listener,btn);
            return null;
        }
        Callback.Cancelable cancelable = x.http().request(request.getHttpMethod(), request, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                MLog.e("onSuccess result=" + result);
                try {
                    ImplParser parser = request.getParser();
                    if(parser==null) parser= new DefaultParser();
                    Object data = parser.parse(String.class, String.class, result);
                    request.setData(data);
                } catch (Throwable throwable) {
                    listener.onDataRequestError(handleException(throwable), request);
                    return;
                }
                listener.onDataRequestSucceed(request);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                MLog.e("onError",ex);
                listener.onDataRequestError(handleException(ex),request);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                MLog.e("onCancelled="+cex.toString());
                listener.onDataRequestCancel(request);
            }

            @Override
            public void onFinished() {
            }
        });
        return cancelable;
    }

    /**
     * 处理错误
     * @param error
     * @return 错误信息
     */

    public static ExceptionInfo handleException(Throwable error) {
        ExceptionInfo result = null;
        if (error instanceof SocketTimeoutException) {
            result = new ExceptionInfo(ExceptionErrorCode.ERROR_CODE_NET_TIMEOUT, null);
        } else if (error instanceof ConnectTimeoutException) {
            result = new ExceptionInfo(ExceptionErrorCode.ERROR_CODE_NET_TIMEOUT, null);
        } else if (error instanceof MalformedURLException) {
            result = new ExceptionInfo(ExceptionErrorCode.ERROR_CODE_URL, null);
        } else if (error instanceof SocketException) {
            result = new ExceptionInfo(ExceptionErrorCode.ERROR_CODE_NET, null);
        } else if (error instanceof SSLHandshakeException) {
            result = new ExceptionInfo(ExceptionErrorCode.ERROR_CODE_SSL, null);
        }else if(error instanceof UnknownHostException){
            result = new ExceptionInfo(ExceptionErrorCode.ERROR_CODE_NET_NOTFOUND, null);
        } else if(error instanceof  ServerException){
            ServerException serverException = (ServerException)error;
            result = new ExceptionInfo(ExceptionErrorCode.ERROR_CODE_SERVER, serverException);
        }else{
             result = new ExceptionInfo(ExceptionErrorCode.ERROR_CODE_OTHER, null);
        }
        return result;
    }

    /**
     * 检测Accesskey
     * @param mContext
     * @return
     */
    private static boolean checkAccessKey(Context mContext) {
        String accessKey = UserSession.getUser().getAccessKey(mContext);
        if (accessKey == null) {
            UserSession.getUser().setAccessKey(mContext, "");
            return false;
        }
        return true;
    }

    /**
     * 请求AccessKey
     * @param mContext
     * @param baseRequest 上一个请求
     * @param listener 上一个回调
     * @param btn 上一个按钮
     */
    private static void requestAccessKey(final Context mContext,final BaseRequest baseRequest,final HttpRequestListener listener, final View btn){
        try {
            Constructor constructor = HbcConfig.accessKeyRequest.getDeclaredConstructor(Context.class);
            constructor.setAccessible(true);
            final BaseRequest accessKeyRequest = (BaseRequest) constructor.newInstance(mContext);
            request(mContext, accessKeyRequest, new HttpRequestListener() {
                @Override
                public void onDataRequestSucceed(BaseRequest request) {
                    UserSession.getUser().setAccessKey(mContext, (String) request.getData());
                    String accessKey = UserSession.getUser().getAccessKey(mContext);
                    MLog.e("accessKey =" + accessKey);
                    request(mContext, baseRequest, listener, btn);
                }

                @Override
                public void onDataRequestError(ExceptionInfo errorInfo, BaseRequest request) {
                    UserSession.getUser().setAccessKey(mContext, null);
                    listener.onDataRequestError(errorInfo, request);
                }

                @Override
                public void onDataRequestCancel(BaseRequest request) {
                            listener.onDataRequestCancel(request);
                        }
            },btn);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("AccessKeyRequest is not allow null");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 空的解析
     */
    public static class DefaultParser extends ImplParser{
        @Override
        public Object parseObject(JSONObject obj) throws Throwable {
            return obj;
        }
    }
}
