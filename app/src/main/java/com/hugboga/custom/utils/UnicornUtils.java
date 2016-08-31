package com.hugboga.custom.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hugboga.custom.MyApplication;
import com.hugboga.custom.data.bean.UserEntity;
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.ImageLoaderListener;
import com.qiyukf.unicorn.api.SavePowerConfig;
import com.qiyukf.unicorn.api.StatusBarNotificationConfig;
import com.qiyukf.unicorn.api.UICustomization;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.UnicornImageLoader;
import com.qiyukf.unicorn.api.YSFOptions;
import com.qiyukf.unicorn.api.YSFUserInfo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by on 16/8/26.
 */

public class UnicornUtils {

    private static final String APPKEY = "d1838897aaf0debe1da1f0443c6942ff";
    private static final long GROUP_ID = 128411;
    private static final String CUSTOMER_AVATAR = "http://fr.huangbaoche.com/im/avatar/default/k_head.jpg";

    public static void initUnicorn() {
        Unicorn.init(MyApplication.getAppContext(), APPKEY, getDefaultOptions(), new UnicornImageLoaderRealize());
    }

    public static void openServiceActivity() {
        if (!Unicorn.isServiceAvailable()) {
            return;
        }
        Context context = MyApplication.getAppContext();
        YSFUserInfo userInfo = new YSFUserInfo();
        userInfo.userId = UserEntity.getUser().getUserId(context);
        userInfo.data = getServiceUserInfo();
        Unicorn.setUserInfo(userInfo);

        UICustomization uiCustomization = new UICustomization();
        uiCustomization.leftAvatar = CUSTOMER_AVATAR;
        uiCustomization.rightAvatar = UserEntity.getUser().getAvatar(context);
        uiCustomization.titleBackgroundColor = 0xFF2D2B24;
        uiCustomization.titleBarStyle = 1;
        YSFOptions options = getDefaultOptions();
        options.uiCustomization = uiCustomization;
        Unicorn.updateOptions(options);

        Unicorn.toggleNotification(false);

        // 设置访客来源，标识访客是从哪个页面发起咨询的，用于客服了解用户是从什么页面进入三个参数分别为来源页面的url，来源页面标题，来源页面额外信息（可自由定义）
        // 设置来源后，在客服会话界面的"用户资料"栏的页面项，可以看到这里设置的值。
        ConsultSource source = new ConsultSource("", "", "");
        source.groupId = UnicornUtils.GROUP_ID;
        Unicorn.openServiceActivity(MyApplication.getAppContext(), "皇包车客服", source);
    }

    private static String getServiceUserInfo() {
        Context context = MyApplication.getAppContext();
        UserEntity userEntity = UserEntity.getUser();
        ArrayList<ServiceUserInfo> list = new ArrayList<ServiceUserInfo>();
        list.add(new ServiceUserInfo("real_name", userEntity.getNickname(context)));
        list.add(new ServiceUserInfo("mobile_phone", userEntity.getPhone(context)));
        list.add(new ServiceUserInfo("user_name", "真实姓名", userEntity.getUserName(context)));
        list.add(new ServiceUserInfo("areaCode", "区域号码", userEntity.getAreaCode(context)));
        list.add(new ServiceUserInfo("userId", "用户ID", userEntity.getUserId(context)));
        list.add(new ServiceUserInfo("version", "应用版本", userEntity.getVersion(context)));
        return JsonUtils.toJson(list);
    }

    private static class ServiceUserInfo implements Serializable {

        public String key;
        public String label;
        public String value;

        public ServiceUserInfo(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public ServiceUserInfo(String key, String label, String value) {
            this.key = key;
            this.label = label;
            this.value = value;
        }
    }

    private static YSFOptions getDefaultOptions() {
        YSFOptions options = new YSFOptions();
//        options.statusBarNotificationConfig = new StatusBarNotificationConfig();
        options.savePowerConfig = new SavePowerConfig();
        return options;
    }

    private static class UnicornImageLoaderRealize implements UnicornImageLoader {

        @Nullable
        @Override
        public Bitmap loadImageSync(String uri, int width, int height) {
            return null;
        }

        @Override
        public void loadImage(String uri, int width, int height, ImageLoaderListener listener) {
            if (width > 0 && height > 0) {
                Glide.with(MyApplication.getAppContext())
                        .load(uri)
                        .asBitmap()
                        .centerCrop()
                        .override(width, height)
                        .into(new LoggingListener(listener));
            } else {
                Glide.with(MyApplication.getAppContext())
                        .load(uri)
                        .asBitmap()
                        .centerCrop()
                        .into(new LoggingListener(listener));
            }
        }

        public class LoggingListener extends SimpleTarget<Bitmap> {

            private ImageLoaderListener listener;

            public LoggingListener(ImageLoaderListener listener) {
                this.listener = listener;
            }

            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                if (listener != null) {
                    listener.onLoadComplete(resource);
                }
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                if (listener != null) {
                    listener.onLoadFailed(e.getCause());
                }
            }
        }
    }

}