package com.hugboga.custom.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.huangbaoche.hbcframe.data.net.ExceptionInfo;
import com.huangbaoche.hbcframe.data.net.HttpRequestListener;
import com.huangbaoche.hbcframe.data.net.HttpRequestUtils;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.hugboga.custom.MainActivity;
import com.hugboga.custom.MyApplication;
import com.hugboga.custom.R;
import com.hugboga.custom.action.data.ActionBean;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.bean.PushMessage;
import com.hugboga.custom.data.event.EventAction;
import com.hugboga.custom.data.event.EventType;
import com.hugboga.custom.data.request.RequestPushDeviceInit;
import com.hugboga.custom.data.request.RequestPushReceive;
import com.hugboga.custom.receiver.GetuiPushService;
import com.hugboga.custom.widget.ZVersionDialog;
import com.igexin.sdk.PushManager;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.greenrobot.eventbus.EventBus;
import org.xutils.common.Callback;
import org.xutils.common.task.PriorityExecutor;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.Executor;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by ZHZEPHI on 2015/7/30.
 */
public class PushUtils {

    public final static String TAG = "hbc_push";

    private final static int MAX_DOWNLOAD_THREAD = 2; // 有效的值范围[1, 3], 设置为3时, 可能阻塞图片加载.
    private final static Executor executor = new PriorityExecutor(MAX_DOWNLOAD_THREAD, true);

    /**
     * 是否对当前版本有效
     * @param context
     * @param versions
     * @return
     */
    public boolean isVersion(Context context, String[] versions){
        if(versions==null || versions.length==0){
            return true;
        }
        String thisVersion = PhoneInfo.getSoftwareVersion(context);
        for(String ver:versions){
            if(ver.equals(thisVersion)){
                return true;
            }
        }
        return false;
    }

    /**
     * 是否对当前时间有效
     * @param vaild
     * @return
     */
    public boolean isVaild(String vaild){
        if(TextUtils.isEmpty(vaild)){
            return true;
        }
        try {
            Date pushDate = DateUtils.getDateTimeFromStr(vaild);
            return pushDate.after(new Date());
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 获取通知栏标题
     * @param context
     * @param title
     * @return
     */
    public static String pushTitle(Context context, String title){
        if(TextUtils.isEmpty(title)){
            return context.getString(R.string.app_name);
        }
        return title;
    }

    /**
     * 获取通知栏内容
     * @param content
     * @return
     */
    public static String pushContent(String content){
        if(TextUtils.isEmpty(content)){
            return "";
        }
        return content;
    }

    /**
     * 进行通知栏显示
     */
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void showNotification(PushMessage pushMessage) {
        Context context = MyApplication.getAppContext();
        //设置通知消息属性
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder mBuilder = new Notification.Builder(context);
        mBuilder.setContentTitle(PushUtils.pushTitle(context, pushMessage.title)); //设置标题
        String content = PushUtils.pushContent(pushMessage.message);
        mBuilder.setContentText(content); //设置内容
        mBuilder.setTicker(content); //设置通知栏上升显示
        mBuilder.setWhen(System.currentTimeMillis()); //设置通知事件
        mBuilder.setPriority(Notification.PRIORITY_MAX); //设置优先级,MAX,HIGH,DEFAULT,LOW,MIN
        mBuilder.setOngoing(false); //是否是正在进行的通知
        mBuilder.setDefaults(Notification.DEFAULT_ALL); //提醒模式
        mBuilder.setVibrate(new long[]{300, 100, 300, 100});
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            mBuilder.setSmallIcon(R.mipmap.small_icon);
        } else {
            mBuilder.setSmallIcon(R.mipmap.ic_launcher48);
        }
        mBuilder.setAutoCancel(true); //自动清理

        //设置点击事件
        PendingIntent pIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, getIntent(context, pushMessage), PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(pIntent); //设置事件

        Notification notification = new Notification.BigTextStyle(mBuilder).bigText(content).build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify((int) System.currentTimeMillis(), notification);
    }

    /**
     * 根据Push类型意图，判断进入的界面
     *
     * @param context
     * @param pushMessage
     * @return
     */
    private static Intent getIntent(Context context, PushMessage pushMessage) {
        /*
        在通知栏打开的消息，全部跳进Main进行处理
        1. 订单详情，进入Main判断参数处理
        2. Push聊天，先进入Main，在打开对应聊天信息
        3. 退出提醒，先进入Main，在进行退出提醒
        4. 版本更新，先进入Main，再进行版本更新提示
         */
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MainActivity.PUSH_BUNDLE_MSG, pushMessage);
        intent.putExtras(bundle);
        return intent;
    }

    /**
     * 接收通知后，启动对应操作
     * @param pushMessage
     */
    public static void todoNewVersion(final Activity activity, final AlertDialog versionDialog, final PushMessage pushMessage){
        versionDialog.setTitle("发现新版本");
        if(pushMessage.force.equals("true")){
            versionDialog.setCancelable(false);
        }else{
            versionDialog.setCancelable(true);
            versionDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "稍后更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    versionDialog.dismiss();
                }
            });
        }
        versionDialog.setButton(DialogInterface.BUTTON_POSITIVE, "前去更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (versionDialog.isShowing()) {
                    versionDialog.dismiss();
                }
                // 强制更新，并且点击开始下载更新
                startDownloadApk(activity,pushMessage.url);
            }
        });
        if(!versionDialog.isShowing()){
            versionDialog.show();
        }
    }

    /**
     * 进行新版本下载
     * Created by ZHZEPHI at 2015年4月15日 下午5:32:29
     */
    public static void startDownloadApk(final Activity activity, final String url){
        LogUtil.e("下载apk startDownloadApk");
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File fileDir = new File(Constants.IMAGE_DIR);
            if(!fileDir.exists()){
                fileDir.mkdir();
            }
            final ZVersionDialog dialog = new ZVersionDialog(activity, R.style.VersionDialog);

            RequestParams params = new RequestParams(url);
            params.setAutoResume(true);
            params.setAutoRename(false);
            params.setSaveFilePath(Constants.IMAGE_DIR + File.separator + Constants.VERSION_FILE);
            params.setExecutor(executor);
            params.setCancelFast(true);

            x.http().get(params, new Callback.ProgressCallback<File>() {
                @Override
                public void onWaiting() {

                }

                @Override
                public void onStarted() {
                    dialog.show();
                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                    dialog.updateProgress(total, current);
                }

                @Override
                public void onSuccess(File result) {
                    installApk(activity);
                    dialog.dismiss();
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    dialog.dismiss();
                    CommonUtils.showToast("下载新版本出错");
                }

                @Override
                public void onCancelled(CancelledException cex) {
                }

                @Override
                public void onFinished() {
                }
            });
        }else{
            //存储设备不存在，是否考虑用浏览器打开
            AlertDialogUtils.showAlertDialog(activity, true, "存储设备不存在", "点击用浏览器打开", "前去更新", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    } catch (Exception e) {
                        CommonUtils.showToast("版本更新地址不存在");
                    }
                }
            });
        }
    }

    /**
     * 开始安装新文件
     * Created by ZHZEPHI at 2015年4月15日 下午5:47:56
     */
    private static void installApk(Activity activity){
        File apkfile = new File(Constants.IMAGE_DIR+ File.separator+ Constants.VERSION_FILE);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        activity.startActivity(i);
    }

    public static void pushRegister(int pushGateway, String uniqueId) {
        Log.i(TAG,"pushGateway = " + pushGateway + "   uniqueId = " + uniqueId);
        Context context = MyApplication.getAppContext();
        if (UnicornUtils.isGranted(Manifest.permission.READ_PHONE_STATE, context)) {
            RequestPushDeviceInit request = new RequestPushDeviceInit(context, pushGateway, uniqueId);
            HttpRequestUtils.request(context, request, new HttpRequestListener() {
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

    public static void onPushReceive(PushMessage pushMessage) {
        showNotification(pushMessage);
        if ("IM".equals(pushMessage.type)) {
            EventBus.getDefault().post(new EventAction(EventType.REFRESH_CHAT_LIST));
        }
        ActionBean actionBean = pushMessage.getActionBean();
        if (actionBean != null) {
            uploadPushReceive(actionBean.pushId);
        }
    }

    private static void uploadPushReceive(String pushId){
        if (TextUtils.isEmpty(pushId)) {
            return;
        }
        RequestPushReceive request = new RequestPushReceive(MyApplication.getAppContext(), pushId);
        HttpRequestUtils.request(MyApplication.getAppContext(), request, new HttpRequestListener() {
            @Override
            public void onDataRequestSucceed(BaseRequest request) {

            }

            @Override
            public void onDataRequestCancel(BaseRequest request) {

            }

            @Override
            public void onDataRequestError(ExceptionInfo errorInfo, BaseRequest request) {

            }
        },false);
    }


    public static void initXMpush() {
        MiPushClient.registerPush(MyApplication.getAppContext(), "2882303761517373432", "5601737383432");
        Logger.disablePushFileLog(MyApplication.getAppContext());
    }

    public static void initJPush() {
        JPushInterface.setDebugMode(false);// 设置开启日志,发布时请关闭日志
        JPushInterface.init(MyApplication.getAppContext());// 初始化 JPush
    }

    public static void initGeTui() {
        PushManager.getInstance().initialize(MyApplication.getAppContext(),null);
        // GeTuiService 为第三方自定义的推送服务事件接收类
        PushManager.getInstance().registerPushIntentService(MyApplication.getAppContext(), GetuiPushService.class);
    }
}