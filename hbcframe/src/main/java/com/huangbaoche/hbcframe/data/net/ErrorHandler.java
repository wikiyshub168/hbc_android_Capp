package com.huangbaoche.hbcframe.data.net;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.Toast;

import com.huangbaoche.hbcframe.data.request.BaseRequest;

/**
 * Created by admin on 2016/2/25.
 */
public  class ErrorHandler implements HttpRequestListener{

    private Activity mActivity;

    public ErrorHandler(Activity activity){
        this.mActivity = activity;
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
            case ExceptionErrorCode.ERROR_CODE_PARSE:
                errState = "数据解析错误";
//                errStrint = R.string.error_parser;
                break;
            case ExceptionErrorCode.ERROR_CODE_NET_UNAVAILABLE:
                // errState = "网络不可用";
//                if (mDialogUtil != null)
//                    mDialogUtil.showSettingDialog();
                break;
            case ExceptionErrorCode.ERROR_CODE_NET_TIMEOUT:
                // errState = "数据加载超时";
                errorInfo.exception = null;
//                if (mDialogUtil != null)
//                    mDialogUtil.showOvertimeDialog(new HttpRequestUtils(mContext, this.getParser(), callback));
                break;
            case ExceptionErrorCode.ERROR_CODE_NET_NOTFOUND:
                errState = "404";
//                errStrint = R.string.error_nofound;
                break;
            case ExceptionErrorCode.ERROR_CODE_URL:
                errState = "URL 地址错误";
//                errStrint = R.string.error_url;
                break;
            case ExceptionErrorCode.ERROR_CODE_NET:
                errState = "联网失败";
//                errStrint = R.string.error_net;
                break;
            case ExceptionErrorCode.ERROR_CODE_SSL:
//                errState = "联网失败SSL";
//                errStrint = R.string.error_ssl;
//                mDialogUtil.showCustomDialog(mContext.getString(errStrint));
                break;
            case ExceptionErrorCode.ERROR_CODE_SERVER:
                errState = "服务器返回错误";
                ServerException serverException = (ServerException) errorInfo.exception;
//                if (serverException.getCode() == 10011) {//accessKey不合法
//                    UserEntity.getUser().setAccessKey(mContext, null);
//                    execute();
//                } else if (mDialogUtil != null) {
//                    mDialogUtil.showCustomDialog(serverException.getMessage(),
//                            serverException.getCode(), serverException.getOpr());
//                }
                Toast.makeText(mActivity, serverException.getMessage(), Toast.LENGTH_LONG).show();
                return;
            case ExceptionErrorCode.ERROR_CODE_OTHER:
                errState = "系统内部错误";
//                errStrint = R.string.error_other;
                break;

        }
        if (!TextUtils.isEmpty(errState))
            Toast.makeText(mActivity, errState, Toast.LENGTH_LONG).show();
        if (errorInfo.exception != null) {
//			ErrorUpload upload = new ErrorUpload(errorInfo.state, errState, errorInfo.exception);
//          Common.uploadErrorInfo(mContext, upload);
        }
    }

    @Override
    public void onDataRequestCancel(BaseRequest request) {

    }
}