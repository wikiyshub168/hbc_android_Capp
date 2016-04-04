package com.hugboga.custom.utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.huangbaoche.hbcframe.data.net.ErrorHandler;
import com.huangbaoche.hbcframe.data.net.ExceptionInfo;
import com.huangbaoche.hbcframe.data.net.HttpRequestListener;
import com.huangbaoche.hbcframe.data.net.HttpRequestUtils;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.huangbaoche.hbcframe.util.MLog;
import com.hugboga.custom.data.bean.UserEntity;
import com.hugboga.custom.data.net.UrlLibs;
import com.hugboga.custom.data.request.RequestResetIMToken;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.CameraInputProvider;
import io.rong.imkit.widget.provider.ImageInputProvider;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

/**
 * Created by admin on 2016/3/10.
 */
public class IMUtil {

    Context context;
    private int requestIMTokenCount = 0;

    public IMUtil(final Context context) {
        this.context = context;
    }

    public void conn(String imToken) {
        connect(imToken);
    }

    public void connect(String imToken) {
        if (TextUtils.isEmpty(imToken)) {
            MLog.e("IMToken 不能为空");
            return;
        }
        RongIM.connect(imToken, new RongIMClient.ConnectCallback() {

            @Override
            public void onTokenIncorrect() {
                MLog.e("-Token已过期，重新获取Token");
                if (requestIMTokenCount < 3) {
                    requestIMTokenCount++;
                    requestIMTokenUpdate();
                }
            }

            @Override
            public void onSuccess(String userId) {
                MLog.e("-连接融云 ——onSuccess— -" + userId);
                initRongIm(context, userId);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                MLog.e("-连接融云 ——onError— -" + errorCode);
                if (requestIMTokenCount < 3) {
                    //您需要更换 Token
                    requestIMTokenCount++;
                    requestIMTokenUpdate();
                }
            }
        });
    }

    /**
     * update token
     */
    private void requestIMTokenUpdate() {
        RequestResetIMToken requestResetToken = new RequestResetIMToken(context);
        HttpRequestUtils.request(context, requestResetToken, httpRequestListener);
    }

    HttpRequestListener httpRequestListener = new HttpRequestListener() {
        @Override
        public void onDataRequestSucceed(BaseRequest request) {
            connect(request.getData().toString());
        }

        @Override
        public void onDataRequestCancel(BaseRequest request) {

        }

        @Override
        public void onDataRequestError(ExceptionInfo errorInfo, BaseRequest request) {
            ErrorHandler handler = new ErrorHandler((Activity) context, this);
            handler.onDataRequestError(errorInfo, request);
        }
    };

    /**
     * IM扩展功能自定义
     */
    private static void initRongIm(Context context, String userId) {
        InputProvider.ExtendProvider[] provider = {
                new ImageInputProvider(RongContext.getInstance()),//图片
                new CameraInputProvider(RongContext.getInstance()),//相机
//                new VoIPInputProvider(RongContext.getInstance()),// 语音通话
        };
        RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.PRIVATE, provider);
        RongIM.getInstance().setCurrentUserInfo(getUserInfo(context));
    }

    /**
     * 获取头像UserInfo
     *
     * @return
     */
    private static UserInfo getUserInfo(Context context) {
        UserInfo userInfo = null;
        String userid = UserEntity.getUser().getUserId();
        String username = UserEntity.getUser().getNickname(context);
        String guideAvatarUrl = UserEntity.getUser().getAvatar(context);
        if (!TextUtils.isEmpty(guideAvatarUrl) && guideAvatarUrl.startsWith(UrlLibs.SERVER_HTTP_SCHEME_HTTPS)) {
            guideAvatarUrl = guideAvatarUrl.replace(UrlLibs.SERVER_HTTP_SCHEME_HTTPS, UrlLibs.SERVER_HTTP_SCHEME_HTTP);
            userInfo = new UserInfo("Y" + userid, username, Uri.parse(guideAvatarUrl));
        }
        return userInfo;
    }
}
