package com.huangbaoche.hbcframe.data.net;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.huangbaoche.hbcframe.HbcConfig;
import com.huangbaoche.hbcframe.R;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.huangbaoche.hbcframe.util.MLog;
import com.huangbaoche.hbcframe.widget.DialogUtilInterface;

/**
 * Created by admin on 2016/2/25.
 */
public  class ErrorHandler implements HttpRequestListener{

    private Activity mActivity;
    private HttpRequestListener mListener;
    private DialogUtilInterface mDialogUtil;

    public ErrorHandler(Activity activity,HttpRequestListener listener){
        this.mActivity = activity;
        this.mListener = listener;
        mDialogUtil = HttpRequestUtils.getDialogUtil(activity);
    }

    @Override
    public void onDataRequestSucceed(BaseRequest request) {

    }

    @Override
    public void onDataRequestError(ExceptionInfo errorInfo, BaseRequest request) {
        if(errorInfo==null)return;
        if (mActivity instanceof Activity) {
            if (((Activity) mActivity).isFinishing()){
                return;
            }
        }

        // 提交服务器用
        String errState = "";
        // String 的 用于用户提示
//        int errStrint = R.string.error_other;
        switch (errorInfo.state) {
            case ExceptionErrorCode.ERROR_CODE_NET_UNAVAILABLE:
                // errState = "网络不可用";
                if (mDialogUtil != null)
                    mDialogUtil.showSettingDialog();
                return;
            case ExceptionErrorCode.ERROR_CODE_NET_TIMEOUT:
                 errState = "数据加载超时";
                errorInfo.exception = null;
                if (mDialogUtil != null)
                    mDialogUtil.showOvertimeDialog(request, mListener);
                return;
            case ExceptionErrorCode.ERROR_CODE_SERVER:
                errState = "服务器返回错误";
                ServerException serverException = (ServerException) errorInfo.exception;
                ServerCodeHandlerInterface serverCodeHandler = getServerCodeHandler(mActivity);
                if(!serverCodeHandler.handleServerCode(mActivity,serverException.getMessage(),serverException.getCode(),request,mListener)) {
                    if (request.errorType == BaseRequest.ERROR_TYPE_DEFAULT) {
                        Toast.makeText(mActivity, serverException.getMessage(), Toast.LENGTH_LONG).show();
                    } else if (request.errorType == BaseRequest.ERROR_TYPE_SHOW_DIALOG) {
                        showAlertDialog(mActivity, null, serverException.getMessage(), "知道了", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                    }
                }
                return;
            case ExceptionErrorCode.ERROR_CODE_NET_NOTFOUND:
                errState = "404";
                break;
            case ExceptionErrorCode.ERROR_CODE_URL:
                errState = "URL 地址错误";
                break;
            case ExceptionErrorCode.ERROR_CODE_NET:
                errState = "联网失败";
                break;
            case ExceptionErrorCode.ERROR_CODE_SSL:
                errState = "联网失败SSL";
                DefaultSSLSocketFactory.resetSSLSocketFactory(mActivity);
                return;
            case ExceptionErrorCode.ERROR_CODE_OTHER:
                errState = "系统内部错误";
                break;
        }
        MLog.e("mActivity = "+mActivity);
        if(mActivity!=null){
            if(errorInfo.state == ExceptionErrorCode.ERROR_CODE_NET){
                Toast.makeText(mActivity, "请检查您的网络连接是否正常", Toast.LENGTH_LONG).show();
            }else {
                if (!HbcConfig.IS_DEBUG && request.getData() != null && request.getData() instanceof String) {
                    if (((String) request.getData()).contains("\"status\":200")) {
                        return;
                    }
                }
                String errorStr = request.getUrlErrorCode();
                if (!HbcConfig.IS_DEBUG && "40001".equals(errorStr)) {
                    return;
                }
                if (!TextUtils.isEmpty(errorInfo.errorCode)) {
                    errorStr += " - " + errorInfo.errorCode;
                }
                if (request.errorType == BaseRequest.ERROR_TYPE_DEFAULT) {
                    Toast.makeText(mActivity, mActivity.getString(R.string.request_error, errorStr), Toast.LENGTH_LONG).show();
                } else if (request.errorType == BaseRequest.ERROR_TYPE_SHOW_DIALOG) {
                    showAlertDialog(mActivity, null, mActivity.getString(R.string.request_error, errorStr), "知道了", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onDataRequestCancel(BaseRequest request) {

    }

    public static String getErrorMessage(ExceptionInfo errorInfo, BaseRequest request) {
        if (errorInfo == null || request == null || request.errorType != BaseRequest.ERROR_TYPE_IGNORE) {
            return null;
        }
        switch (errorInfo.state) {
            case ExceptionErrorCode.ERROR_CODE_SERVER:
                ServerException serverException = (ServerException) errorInfo.exception;
                return serverException.getMessage();
        }
        return String.format("服务器忙翻了，请稍后再试(%1$s)", getErrorCode(errorInfo, request));
    }

    public static String getErrorCode(ExceptionInfo errorInfo, BaseRequest request) {
        if (errorInfo == null || request == null || request.errorType != BaseRequest.ERROR_TYPE_IGNORE) {
            return null;
        }
        String errorStr = request.getUrlErrorCode();
        if (!TextUtils.isEmpty(errorInfo.errorCode)) {
            errorStr += " - " + errorInfo.errorCode;
        }
        return errorStr;
    }

    public void showAlertDialog(Context context,String title,String content,String okText,DialogInterface.OnClickListener onClick){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        if (!TextUtils.isEmpty(title)) {
            dialog.setTitle(title);
        }
        dialog.setMessage(content);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, okText, onClick);
        dialog.show();
    }


    public static ServerCodeHandlerInterface getServerCodeHandler(Context mContext){
        ServerCodeHandlerInterface handler = null;
        if(mContext instanceof Activity) {
            try {
                if(HbcConfig.serverCodeHandler!=null) {
                    handler = (ServerCodeHandlerInterface) HbcConfig.serverCodeHandler.newInstance();
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return handler;
    }
}
