package com.hugboga.custom.gp.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.huangbaoche.hbcframe.util.MLog;
import com.huangbaoche.hbcframe.util.WXShareUtils;
import com.hugboga.custom.BuildConfig;
import com.hugboga.custom.activity.LoginActivity;
import com.hugboga.custom.data.event.EventAction;
import com.hugboga.custom.data.event.EventType;
import com.hugboga.custom.utils.CommonUtils;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

/**
 * Created by ZONGFI on 2015/5/14.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = WXEntryActivity.class.getSimpleName();
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        api = WXAPIFactory.createWXAPI(this, BuildConfig.WX_APP_ID, false);
        api.handleIntent(getIntent(), this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onReq(BaseReq req) {
        MLog.e("onResp " + req.openId + " " + req.transaction);
    }

    @Override
    public void onResp(BaseResp resp) {
        MLog.e("onResp " + resp.errCode + " " + resp.errStr);
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if(LoginActivity.isWXLogin){
                    SendAuth.Resp sendResp = (SendAuth.Resp) resp;
                    LoginActivity.isWXLogin = false;
                    EventBus.getDefault().post(new EventAction(EventType.WECHAT_LOGIN_CODE,sendResp));
                    CommonUtils.getAgainstDeviceId();
                } else {
                    EventBus.getDefault().post(new EventAction(EventType.WECHAT_SHARE_SUCCEED));
                    setSensorsShareBack();
                }
                //分享成功
                Log.i(TAG, "分享成功");
                break;
            case BaseResp.ErrCode.ERR_COMM:
                //一般错误
                Log.i(TAG, "一般错误");
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //用户取消
                Log.i(TAG, "用户取消");
                break;
            case BaseResp.ErrCode.ERR_SENT_FAILED:
                //发送失败
                Log.i(TAG, "发送失败");
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                //认证被否决
                Log.i(TAG, "认证被否决" + resp.errStr);
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                //不支持错误
                Log.i(TAG, "不支持错误");
                break;
        }
        finish();
    }

    //神策统计_分享后返回
    public void setSensorsShareBack() {
        JSONObject properties = new JSONObject();
        try {
            WXShareUtils wxShareUtils = WXShareUtils.getInstance(getBaseContext());
//            properties.put("share_type",resp.getType()== SendMessageToWX.Req.WXSceneSession?"微信好友":"朋友圈");
            properties.put("share_type", wxShareUtils.type == 1 ? "微信好友" : "朋友圈");
            SensorsDataAPI.sharedInstance(getBaseContext()).track("share_back", properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
