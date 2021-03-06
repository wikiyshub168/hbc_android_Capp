package com.hugboga.custom;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huangbaoche.hbcframe.data.net.DefaultSSLSocketFactory;
import com.huangbaoche.hbcframe.data.net.ExceptionInfo;
import com.huangbaoche.hbcframe.data.net.HttpRequestListener;
import com.huangbaoche.hbcframe.data.net.HttpRequestUtils;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.huangbaoche.hbcframe.util.MLog;
import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiAvailability;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.push.HuaweiPush;
import com.huawei.hms.support.api.push.TokenResult;
import com.hugboga.custom.action.ActionController;
import com.hugboga.custom.action.data.ActionBean;
import com.hugboga.custom.activity.BaseActivity;
import com.hugboga.custom.activity.OrderDetailActivity;
import com.hugboga.custom.activity.WebInfoActivity;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.bean.CheckVersionBean;
import com.hugboga.custom.data.bean.PushMessage;
import com.hugboga.custom.data.bean.UserEntity;
import com.hugboga.custom.data.event.EventAction;
import com.hugboga.custom.data.event.EventType;
import com.hugboga.custom.data.request.RequestCheckVersion;
import com.hugboga.custom.data.request.RequestPushClick;
import com.hugboga.custom.data.request.RequestPushToken;
import com.hugboga.custom.data.request.RequestUpdateAntiCheatInfo;
import com.hugboga.custom.data.request.RequestUploadLocation;
import com.hugboga.custom.fragment.FgDestination;
import com.hugboga.custom.fragment.FgHome;
import com.hugboga.custom.fragment.FgMySpace;
import com.hugboga.custom.fragment.FgNimChat;
import com.hugboga.custom.fragment.FgTravel;
import com.hugboga.custom.receiver.HuaweiPushReceiver;
import com.hugboga.custom.service.HbcJobService;
import com.hugboga.custom.statistic.MobClickUtils;
import com.hugboga.custom.statistic.StatisticConstant;
import com.hugboga.custom.statistic.sensors.SensorsUtils;
import com.hugboga.custom.utils.AlertDialogUtils;
import com.hugboga.custom.utils.ApiReportHelper;
import com.hugboga.custom.utils.CommonUtils;
import com.hugboga.custom.utils.FileUtil;
import com.hugboga.custom.utils.IMUtil;
import com.hugboga.custom.utils.JsonUtils;
import com.hugboga.custom.utils.LocationUtils;
import com.hugboga.custom.utils.PermissionRes;
import com.hugboga.custom.utils.PhoneInfo;
import com.hugboga.custom.utils.PushUtils;
import com.hugboga.custom.utils.SharedPre;
import com.hugboga.custom.utils.UpdateResources;
import com.hugboga.custom.utils.collection.CollectionHelper;
import com.hugboga.custom.utils.rom.Rom;
import com.hugboga.custom.widget.DialogUtil;
import com.hugboga.custom.widget.GiftController;
import com.hugboga.custom.widget.NoScrollViewPager;
import com.networkbench.agent.impl.NBSAppAgent;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;


public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener, HttpRequestListener {

    public static final String PUSH_BUNDLE_MSG = "pushMessage";
    public static final String FILTER_PUSH_DO = "com.hugboga.custom.pushdo";
    public static final String PARAMS_PAGE_INDEX = "page_index";

    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int PERMISSION_ACCESS_FINE_LOCATION = 12;

    public static final int REQUEST_EXTERNAL_STORAGE_UPDATE = 1;
    public static final int REQUEST_EXTERNAL_STORAGE_DB = 2;
    public static final int REQUEST_EXTERNAL_STORAGE = 3;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @BindView(R.id.container)
    NoScrollViewPager mViewPager;

    @BindView(R.id.bottom_point_2)
    TextView bottomPoint2;
    @BindView(R.id.bottom_point_3)
    TextView qyServiceUnreadMsgCount;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TextView tabMenu[] = new TextView[5];
    private int currentPosition = 0;
    private CheckVersionBean cvBean;
    private DialogUtil dialogUtil;
    public int lastPagerPosition = 0;

    private FgHome fgHome;
    private FgDestination fgDestination;
    private FgNimChat fgChat;
    private FgTravel fgTravel;
    private FgMySpace fgMySpace;
    private SharedPre sharedPre;

    @Override
    public int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int pagePosition = -1;
        ActionBean actionBean = null;
        if (savedInstanceState != null) {
            pagePosition = savedInstanceState.getInt(MainActivity.PARAMS_PAGE_INDEX, -1);
        } else {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                actionBean = (ActionBean) bundle.getSerializable(Constants.PARAMS_ACTION);
                currentPosition = bundle.getInt(MainActivity.PARAMS_PAGE_INDEX, -1);
            }
        }
        MobClickUtils.onEvent(StatisticConstant.LAUNCH_DISCOVERY);
        checkVersion();
        //收藏信息初始化
        CollectionHelper.getIns(this).initCollection();
        sharedPre = new SharedPre(this);
        verifyStoragePermissions(this, REQUEST_EXTERNAL_STORAGE);
        initBottomView();
        initAdapterContent();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setScrollble(false);

        //为服务器授权
        grantPhone();
        try {
            initLocation();
            grantLocation();
        } catch (Exception e) {

        }
        connectIM();
        receivePushMessage(getIntent());
        new Thread(new CalaCacheThread()).start();//计算缓存图片大小
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MLog.e("umengLog" + getDeviceInfo(this));
        showAdWebView(getIntent().getStringExtra("url"));

        if (actionBean != null) {
            ActionController actionFactory = ActionController.getInstance();
            actionFactory.doAction(this, actionBean);
        }

        if (pagePosition != -1) {
            currentPosition = pagePosition;
            mViewPager.setCurrentItem(currentPosition);
        }

        initNetworkbench();
        requesetBattery();

        PushUtils.initJPush();
        if (Rom.isEmui()) {
            Log.i("jixing", "huawei init");
            initHuaWei();
        } else if (CommonUtils.isSupportGoogleService() && !Rom.isMiui()) {
            Log.i("jixing", "qitajixing init");
            PushUtils.initGeTui();
        } else {
            Log.i("jixing", "xiaomi init");
            PushUtils.initXMpush();
        }
        //启动保活服务,针对部分机型有效
        startJobService();
    }

    protected boolean isDefaultEvent() {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        DefaultSSLSocketFactory.resetSSLSocketFactory(this);
        if (currentPosition == 0) {
//            final String versionName = SharedPre.getString(HomeCustomLayout.PARAMS_LAST_GUIDE_VERSION_NAME, "");
//            if (BuildConfig.VERSION_NAME.equals(versionName)) {
            GiftController.getInstance(this).showGiftDialog();
//            }
            EventBus.getDefault().post(new EventAction(EventType.REQUEST_HOME_DATA));
        }
        //支付结果 单次接送催促下单后不再显示催促下单入口。
        SharedPre.setString(SharedPre.PAY_ORDER, "");
    }

    @Override
    protected void onPause() {
        super.onPause();
        GiftController.getInstance(this).abortion();
    }

    private void checkVersion() {
        int resourcesVersion = new SharedPre(this).getIntValue(SharedPre.RESOURCES_H5_VERSION);
        RequestCheckVersion requestCheckVersion = new RequestCheckVersion(this, resourcesVersion);
        HttpRequestUtils.request(this, requestCheckVersion, this, false);
    }

    abstract class CheckVersionCallBack implements Callback.ProgressCallback<File> {
        @Override
        public void onWaiting() {

        }

        @Override
        public void onStarted() {

        }

        @Override
        public void onLoading(long total, long current, boolean isDownloading) {

        }

        @Override
        public void onSuccess(File result) {

        }

        @Override
        public void onError(Throwable ex, boolean isOnCallback) {

        }

        @Override
        public void onCancelled(CancelledException cex) {

        }

    }

    private void testPush() {
//        String teset  = "{\"action\":\"{\\\"t\\\":\\\"2\\\",\\\"v\\\":\\\"16\\\"}\",\"orderNo\":\"Z191195516914\",\"type\":\"G1\",\"orderType\":\"888\",\"sound\":\"newOrder.mp3\"}";
        String teset = "{\"orderNo\":\"Z191195516914\",\"subOrderNo\":\"R1Z191195516914\",\"type\":\"G1\",\"orderType\":\"888\",\"sound\":\"newOrder.mp3\"}";
        PushMessage pushMessage = (PushMessage) JsonUtils.fromJson(teset, PushMessage.class);
        pushMessage.title = "";
        pushMessage.message = "您有1个新订单，能收到声音吗,请赶快登录皇包车-司导端APP去接单吧";
        PushUtils.showNotification(pushMessage);
    }

    private void showAdWebView(String url) {
        if (null != url) {
            if (UserEntity.getUser().isLogin(activity)) {
                url = CommonUtils.getBaseUrl(url) + "userId=" + UserEntity.getUser().getUserId(activity) + "&t=" + new Random().nextInt(100000);
            }
            Intent intent = new Intent(activity, WebInfoActivity.class);
            intent.putExtra(WebInfoActivity.WEB_URL, url);
            startActivity(intent);

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MainActivity.PARAMS_PAGE_INDEX, currentPosition);
    }


    Timer timer;
    TimerTask timerTask;

    public void uploadLocation() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {

                String lat = new SharedPre(MainActivity.this).getStringValue("lat");
                String lng = new SharedPre(MainActivity.this).getStringValue("lng");
                Log.e("========", "============lat=" + lat + "====lng=" + lng);

                if (!TextUtils.isEmpty(lat)) {
                    RequestUploadLocation requestUploadLocation = new RequestUploadLocation(MainActivity.this);
                    HttpRequestUtils.request(MainActivity.this, requestUploadLocation, MainActivity.this, false);

                }
            }
        };
        timer.schedule(timerTask, 0, 30000);
    }

    /**
     * 授权获取手机信息权限
     */
    private void grantPhone() {
        MPermissions.requestPermissions(MainActivity.this, PermissionRes.READ_PHONE_STATE, android.Manifest.permission.READ_PHONE_STATE);
    }

    @PermissionGrant(PermissionRes.READ_PHONE_STATE)
    public void requestPhoneSuccess() {
        try {
            JPushInterface.setAlias(MainActivity.this, PhoneInfo.getIMEI(this), new TagAliasCallback() {
                @Override
                public void gotResult(int i, String s, Set<String> set) {
                    Log.i(PushUtils.TAG, "JPushInterface setAlias gotResult() pushId = " + JPushInterface.getRegistrationID(MainActivity.this));
                    PushUtils.pushRegister(4, JPushInterface.getRegistrationID(MainActivity.this));
                }
            });
            if (!Rom.isEmui() && !CommonUtils.isSupportGoogleService()) {
                MiPushClient.setAlias(getApplicationContext(), PhoneInfo.getIMEI(this), "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PermissionDenied(PermissionRes.READ_PHONE_STATE)
    public void requestPhoneFailed() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_PHONE_STATE)) {
            AlertDialogUtils.showAlertDialog(MainActivity.this, false, getString(R.string.grant_fail_title), getString(R.string.grant_fail_phone1)
                    , getString(R.string.grant_fail_btn_exit)
                    , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    });
        } else {
            AlertDialogUtils.showAlertDialog(MainActivity.this, false, getString(R.string.grant_fail_title), getString(R.string.grant_fail_phone)
                    , getString(R.string.grant_fail_btn), getString(R.string.grant_fail_btn_exit)
                    , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            grantPhone();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    });
        }
    }

    private void uploadPushClick(String pushId) {
        RequestPushClick request = new RequestPushClick(this, pushId);
        HttpRequestUtils.request(this, request, this);
    }

    private void initAdapterContent() {
        fgHome = new FgHome();
        fgDestination = new FgDestination();
        fgTravel = new FgTravel();
        fgChat = new FgNimChat();
        fgMySpace = new FgMySpace();
        addFragment(fgHome);
        addFragment(fgDestination);
        addFragment(fgChat);
        addFragment(fgTravel);
        addFragment(fgMySpace);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            if (timerTask != null) {
                timerTask.cancel();
                timerTask = null;
            }
            EventBus.getDefault().unregister(this);
            ApiReportHelper.getInstance().commitAllReport();
            ApiReportHelper.getInstance().abort();
            if (Rom.isEmui() && client != null) {
                //建议在onDestroy的时候停止连接华为移动服务
                //业务可以根据自己业务的形态来确定client的连接和断开的时机，但是确保connect和disconnect必须成对出现
                client.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDataRequestSucceed(BaseRequest request) {
        super.onDataRequestSucceed(request);
        if (request instanceof RequestPushToken) {
            MLog.e(request.getData().toString());
        } else if (request instanceof RequestUploadLocation) {
            LocationUtils.cleanLocationInfo(MainActivity.this);
            String cityId = ((RequestUploadLocation) request).getData().cityId;
            String cityName = ((RequestUploadLocation) request).getData().cityName;
            String countryId = ((RequestUploadLocation) request).getData().countryId;
            String countryName = ((RequestUploadLocation) request).getData().countryName;
            LocationUtils.saveLocationCity(MainActivity.this, cityId, cityName, countryId, countryName);
//            MLog.e("Location: cityId:"+cityId + ",  cityName:"+cityName);
        } else if (request instanceof RequestCheckVersion) {
            RequestCheckVersion requestCheckVersion = (RequestCheckVersion) request;
            cvBean = requestCheckVersion.getData();
            if (!cvBean.hasAntiId) {
                requestData(new RequestUpdateAntiCheatInfo(MainActivity.this), false);
            }
            UserEntity.getUser().setIsNewVersion(this, cvBean.hasAppUpdate);//是否有新版本
            dialogUtil = DialogUtil.getInstance(this);
            if (Constants.CHANNEL_GOOGLE_PLAY.equals(BuildConfig.FLAVOR)) {//google play
                dialogUtil.showUpdateDialog(cvBean.hasAppUpdate, cvBean.force, cvBean.content, cvBean.url);
            } else {
                dialogUtil.showUpdateDialog(cvBean.hasAppUpdate, cvBean.force, cvBean.content, cvBean.url, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            boolean isVerify = verifyStoragePermissions(activity, REQUEST_EXTERNAL_STORAGE_UPDATE);
                            if (!isVerify) {
                                downloadApk();
                            }
                        } else {
                            downloadApk();
                        }
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            boolean isVerify = verifyStoragePermissions(activity, REQUEST_EXTERNAL_STORAGE_DB);
                            if (!isVerify) {
                                updateDb();
                            }
                        } else {
                            updateDb();
                        }
                    }
                });
            }

        }
    }

    public void downloadApk() {
        if (cvBean == null || dialogUtil == null) {
            return;
        }
        if (cvBean.force && dialogUtil.getVersionDialog() != null) {
            try {
                Field field = dialogUtil.getVersionDialog().getClass().getSuperclass().getDeclaredField("mShowing");
                field.setAccessible(true);
                field.set(dialogUtil.getVersionDialog(), false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        PushUtils.startDownloadApk(MainActivity.this, cvBean.url);
    }

    public void updateDb() {
        if (cvBean == null) {
            return;
        }
        //在版本检测后 检测DB
        UpdateResources.checkRemoteDB(MainActivity.this, cvBean.dbDownloadLink, cvBean.dbVersion, new CheckVersionCallBack() {
            @Override
            public void onFinished() {
            }
        });
    }

    public static boolean verifyStoragePermissions(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= 23) {
            int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, requestCode);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDataRequestCancel(BaseRequest request) {
    }

    @Override
    public void onDataRequestError(ExceptionInfo errorInfo, BaseRequest request) {
//        super.onDataRequestError(errorInfo, request);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                ActionBean actionBean = (ActionBean) bundle.getSerializable(Constants.PARAMS_ACTION);
                if (actionBean != null) {
                    ActionController actionFactory = ActionController.getInstance();
                    actionFactory.doAction(this, actionBean);
                }
                int pagePosition = bundle.getInt(MainActivity.PARAMS_PAGE_INDEX, -1);
                if (pagePosition != -1) {
                    currentPosition = pagePosition;
                    mViewPager.setCurrentItem(currentPosition);
                }
            }
        }
        receivePushMessage(intent);
    }

    //Push点击
    public static void setSensorsShareEvent(String pushID, String pushTitle) {
        if (TextUtils.isEmpty(pushID)) {
            return;
        }
        try {
            JSONObject properties = new JSONObject();
            properties.put("pushId", pushID);
            properties.put("pushTitle", pushTitle);
            SensorsDataAPI.sharedInstance(MyApplication.getAppContext()).track("clickPush", properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void receivePushMessage(Intent intent) {
        if (intent == null) {
            return;
        }
        PushMessage message = (PushMessage) intent.getSerializableExtra(MainActivity.PUSH_BUNDLE_MSG);
        if (message != null) {
            uploadPushClick(message.messageID);
            //埋点点击事件
            setSensorsShareEvent(message.messageID, message.message);
            ActionBean actionBean = message.getActionBean();
            if (actionBean != null && actionBean.type != null) {//走新协议
                actionBean.source = "push调起";
                ActionController actionFactory = ActionController.getInstance();
                actionFactory.doAction(this, actionBean);
            } else {
                if ("IM".equals(message.type)) {
                    gotoChatList();
                } else if (message.orderNo != null) {//其中之一 type = C13 提醒用户选司导
                    gotoOrder(message);
                } else {
                    //老协议action{"i","push133455"},切换到首页第一项
                    if (actionBean != null && actionBean.type == null) {
                        mViewPager.setCurrentItem(0);
                        SensorsUtils.setPageEvent(getEventSource(), getPageTitle(), actionBean.pushId);
                    }
                }
            }
        }
    }

    private void gotoChatList() {
        //如果是收到消息推送 关了上层的页面
        if (getFragmentList().size() > 4) {
            for (int i = getFragmentList().size() - 1; i >= 4; i--) {
                getFragmentList().get(i).finish();
            }
        }
        //跳转到聊天列表
        mViewPager.setCurrentItem(2);
    }

    private void gotoOrder(PushMessage message) {
        OrderDetailActivity.Params params = new OrderDetailActivity.Params();
        params.orderType = CommonUtils.getCountInteger(message.orderType);
        params.orderId = message.orderNo;
        if ("888".equals(message.orderType) && !TextUtils.isEmpty(message.subOrderNo)) {
            params.subOrderId = message.subOrderNo;
        }
        Intent intent = new Intent(this, OrderDetailActivity.class);
        intent.putExtra(Constants.PARAMS_DATA, params);
        intent.putExtra(Constants.PARAMS_SOURCE, params.source);
        startActivity(intent);
    }

    @Subscribe
    public void onEventMainThread(EventAction action) {
        switch (action.getType()) {
            case SET_MAIN_PAGE_INDEX:
                lastPagerPosition = currentPosition;
                int index = Integer.valueOf(action.data.toString());
                if (index >= 0 && index < 4)
                    mViewPager.setCurrentItem(index);
                break;
            case CLICK_USER_LOOUT:
                SharedPre.setInteger(UserEntity.getUser().getUserId(MyApplication.getAppContext()), SharedPre.IM_CHAT_COUNT, 0);
                setIMCount(0, 0);
                break;
            default:
                break;
        }
    }

    private void connectIM() {
        if (UserEntity.getUser().isLogin(this))
            IMUtil.getInstance().connect();
    }

    private void initBottomView() {
        tabMenu[0] = (TextView) findViewById(R.id.tab_text_1);
        tabMenu[1] = (TextView) findViewById(R.id.tab_text_2);
        tabMenu[2] = (TextView) findViewById(R.id.tab_text_3);
        tabMenu[3] = (TextView) findViewById(R.id.tab_text_4);
        tabMenu[4] = (TextView) findViewById(R.id.tab_text_5);
        tabMenu[0].setSelected(true);
    }

    private long exitTime;

    @Override
    public void onBackPressed() {
//        if (fgHome != null && fgHome.closeGuideView()) {//关掉引导遮罩
//            return;
//        }
//        if (fgHomePage != null && fgHomePage.closeGuideView()) {//关掉引导遮罩
//            return;
//        }
        if (getFragmentList().size() > mSectionsPagerAdapter.getCount()) {
            doFragmentBack();
        } else if (mViewPager.getCurrentItem() != 0) {
            mViewPager.setCurrentItem(0);
        } else {
            long times = System.currentTimeMillis();
            if ((times - exitTime) > 2000) {
                CommonUtils.showToast("再次点击退出");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
        }
    }


    @Override
    public int getContentId() {
        contentId = R.id.main_layout;
        return contentId;
    }


    @OnClick({R.id.tab_text_1, R.id.tab_text_2, R.id.tab_text_3, R.id.tab_text_4, R.id.tab_text_5})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.tab_text_1:
                mViewPager.setCurrentItem(0);
                MobClickUtils.onEvent(StatisticConstant.LAUNCH_DISCOVERY);
                EventBus.getDefault().post(new EventAction(EventType.REQUEST_HOME_DATA));
                SensorsUtils.setPageEvent("首页", "首页", "");
                break;
            case R.id.tab_text_2:
                mViewPager.setCurrentItem(1);
                SensorsUtils.setPageEvent("目的地", "目的地", "");
                setSensorsViewScreenEndEvent(); //首页不见了
                break;
            case R.id.tab_text_3:
                mViewPager.setCurrentItem(2);
                if (fgChat != null) {
                    //首次发起聊天，需要手动刷新建立聊天对话
                    fgChat.flushList();
                }
                SensorsUtils.setPageEvent("私聊", "私聊", "");
                setSensorsViewScreenEndEvent(); //首页不见了
                break;
            case R.id.tab_text_4:
                mViewPager.setCurrentItem(3);
                SensorsUtils.setPageEvent("行程", "行程", "");
                setSensorsViewScreenEndEvent(); //首页不见了
                break;
            case R.id.tab_text_5:
                mViewPager.setCurrentItem(4);
                SensorsUtils.setPageEvent("我的", "我的", "");
                setSensorsViewScreenEndEvent(); //首页不见了
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        MLog.e("onPageSelected = " + position);
        for (int i = 0; i < tabMenu.length; i++) {
            tabMenu[i].setSelected(position == i);
        }
        currentPosition = position;
        if (position == 0) {
            GiftController.getInstance(this).showGiftDialog();
            MobClickUtils.onEvent(StatisticConstant.LAUNCH_DISCOVERY);
        } else {
            GiftController.getInstance(this).abortion();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    //通讯录
    private final int PICK_CONTACTS = 101;

    // 接收通讯录的选择号码事件
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        onHuaWeiPushActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data == null) {
                return;
            }
            if (PICK_CONTACTS == requestCode) {
                Uri result = data.getData();
                String[] contact = PhoneInfo.getPhoneContacts(this, result);
                EventBus.getDefault().post(new EventAction(EventType.CONTACT, contact));
            }
        }
    }

    @Override
    public String getEventId() {
        return super.getEventId();
    }

    @Override
    public String getEventSource() {
        return "首页";
    }

    @Override
    public Map getEventMap() {
        return super.getEventMap();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: {
                    return fgHome;
                }
                case 1: {
                    return fgDestination;
                }
                case 2: {
                    return fgChat;
                }
                case 3: {
                    return fgTravel;
                }
                case 4: {
                    return fgMySpace;
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "发现";
                case 1:
                    return "目的地";
                case 2:
                    return "私聊";
                case 3:
                    return "行程";
                case 4:
                    return "我的";
            }
            return null;
        }
    }

    public void setIMCount(int count, int serviceMsgCount) {
        if (UserEntity.getUser().isLogin(this)) {
            if (count > 0) {
                if (count > 99) {
                    bottomPoint2.setText("99+");
                } else {
                    bottomPoint2.setText("" + count);
                }
                bottomPoint2.setVisibility(View.VISIBLE);
                qyServiceUnreadMsgCount.setVisibility(View.GONE);
            } else if (serviceMsgCount > 0) {
                bottomPoint2.setVisibility(View.GONE);
                qyServiceUnreadMsgCount.setVisibility(View.VISIBLE);
            } else {
                bottomPoint2.setVisibility(View.GONE);
                bottomPoint2.setText("");
                qyServiceUnreadMsgCount.setVisibility(View.GONE);
            }
        } else {
            bottomPoint2.setVisibility(View.GONE);
            bottomPoint2.setText("");
            qyServiceUnreadMsgCount.setVisibility(View.GONE);
        }
    }

    public void restartApp() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent restartIntent = PendingIntent.getActivity(
                this.getApplicationContext(), 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        //退出程序 重启应用
        AlarmManager mgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500, restartIntent); //  重启应用
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
            case PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    MLog.e("==========PERMISSION_GRANTED=========");
                    requestLocation();
                } else {
                    // permission denied
                    MLog.e("==========denied=========");
                }
                break;
            case REQUEST_EXTERNAL_STORAGE_UPDATE:
                downloadApk();
                break;
            case REQUEST_EXTERNAL_STORAGE_DB:
                updateDb();
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void exitApp() {
        restartApp();
        super.exitApp();
    }

    class CalaCacheThread implements Runnable {
        public void run() {
            long cacheSize = calculateCacheFileSize();
            sharedPre.saveLongValue(SharedPre.CACHE_SIZE, cacheSize);
        }
    }

    private long calculateCacheFileSize() {
        long length = 0L;
        try {
            String cachePath = Glide.getPhotoCacheDir(MyApplication.getAppContext()).getPath();
            File cacheDir1 = new File(cachePath);
            if (cacheDir1 != null) {
                length += FileUtil.getFileOrDirSize(cacheDir1);
            }
        } catch (Exception e) {

        }
        return length;
    }

    public void grantLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_ACCESS_COARSE_LOCATION);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 100, locationListener);
        } else {
            requestLocation();
        }
    }


    public void requestLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 100, locationListener);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 100, locationListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    LocationManager locationManager;
    LocationListener locationListener;

    public void initLocation() {
        if (!LocationUtils.gpsIsOpen(this)) {
            AlertDialog dialog = AlertDialogUtils.showAlertDialog(this, "没有开启GPS定位,请到设置里开启", "设置", "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LocationUtils.openGPSSeting(MainActivity.this);
                    dialog.dismiss();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String locStr = String.format("%s\n lat=%f \n lng=%f \n(%f meters)", location.getProvider(),
                        location.getLatitude(), location.getLongitude(), location.getAccuracy());

                LocationUtils.saveLocationInfo(MainActivity.this, location.getLatitude() + "", location.getLongitude() + "");

                UserEntity.getUser().setLongitude(location.getLongitude());
                UserEntity.getUser().setLatitude(location.getLatitude());

                // 神策 公共属性
                try {
                    JSONObject properties = new JSONObject();
                    properties.put("longitude", location.getLongitude());   // 经度
                    properties.put("latitude", location.getLatitude());     // 纬度
                    SensorsDataAPI.sharedInstance(MainActivity.this).registerSuperProperties(properties);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (timer == null) {
                    uploadLocation();
                }
//                MLog.e(locStr);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                LocationUtils.cleanLocationInfo(MainActivity.this);
            }
        };

    }


    @SuppressLint("NewApi")
    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }

    public static String getDeviceInfo(Context context) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String device_id = null;
            if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                device_id = tm.getDeviceId();
            }
            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            String mac = wifi.getConnectionInfo().getMacAddress();
            json.put("mac", mac);
            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }
            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
            }
            json.put("device_id", device_id);
            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 检测是否有手机白名单设置，如果没有则弹框要求增加
     */
    private void requesetBattery() {
        try {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!pm.isIgnoringBatteryOptimizations(getPackageName())) {
                    //如果没有系统白名单设置，则弹框要求加入白名单
                    Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).setData(Uri.parse("package:" + getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            }
        } catch (Exception e) {
        }
    }

    private void initNetworkbench() {
        NBSAppAgent.setLicenseKey("34ac28c049574c4095b57fc0a591cd4b").withLocationServiceEnabled(true).start(this.getApplicationContext());
    }

    private void startJobService() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                JobInfo jobInfo = new JobInfo.Builder(1, new ComponentName(getPackageName(), HbcJobService.class.getName()))
                        .setPeriodic(10000)//设置间隔时间
                        .setPersisted(true)//设备重启之后你的任务是否还要继续执行
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)//设置需要的网络条件，默认NETWORK_TYPE_NONE
                        .build();
                jobScheduler.schedule(jobInfo);
            }
        } catch (Exception e) {

        }
    }

    // ---  华为push start ---
    private HuaweiApiClient client;
    //调用HuaweiApiAvailability.getInstance().resolveError传入的第三个参数
    //作用同startactivityforresult方法中的requestcode
    private static final int REQUEST_HMS_RESOLVE_ERROR = 1000;
    //华为的链接错误处理
    public static final String EXTRA_RESULT = "intent.extra.RESULT";

    private void onHuaWeiPushActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_HMS_RESOLVE_ERROR) {
            if (resultCode == Activity.RESULT_OK) {

                int result = data.getIntExtra(EXTRA_RESULT, 0);

                if (result == ConnectionResult.SUCCESS) {
                    Log.i(HuaweiPushReceiver.TAG, "错误成功解决");
                    if (!client.isConnecting() && !client.isConnected()) {
                        client.connect();
                    }
                } else if (result == ConnectionResult.CANCELED) {
                    Log.i(HuaweiPushReceiver.TAG, "解决错误过程被用户取消");
                } else if (result == ConnectionResult.INTERNAL_ERROR) {
                    Log.i(HuaweiPushReceiver.TAG, "发生内部错误，重试可以解决");
                    //开发者可以在此处重试连接华为移动服务等操作，导致失败的原因可能是网络原因等
                } else {
                    Log.i(HuaweiPushReceiver.TAG, "未知返回码");
                }
            } else {
                Log.i(HuaweiPushReceiver.TAG, "调用解决方案发生错误");
            }
        }
    }

    private void initHuaWei() {
        // 华为链接
        client = new HuaweiApiClient.Builder(this)
                .addApi(HuaweiPush.PUSH_API)
                .addConnectionCallbacks(new HuaweiApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected() {
                        Log.i(HuaweiPushReceiver.TAG, "HuaweiApiClient 连接成功");
                        if (!client.isConnected()) {
                            Log.i(HuaweiPushReceiver.TAG, "获取token失败，原因：HuaweiApiClient未连接");
                            client.connect();
                            return;
                        }

                        Log.i(HuaweiPushReceiver.TAG, "异步接口获取push token");
                        PendingResult<TokenResult> tokenResult = HuaweiPush.HuaweiPushApi.getToken(client);
                        tokenResult.setResultCallback(new ResultCallback<TokenResult>() {
                            @Override
                            public void onResult(TokenResult result) {
                                Log.i(HuaweiPushReceiver.TAG, "异步接口获取push token" + result.getTokenRes().getToken());
                            }
                        });
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        if (!MainActivity.this.isFinishing()) {
                            client.connect();
                        }
                        Log.i(HuaweiPushReceiver.TAG, "HuaweiApiClient 连接断开");
                    }
                })
                .addOnConnectionFailedListener(new HuaweiApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.i(HuaweiPushReceiver.TAG, "HuaweiApiClient连接失败，错误码：" + connectionResult.getErrorCode());
                        if (HuaweiApiAvailability.getInstance().isUserResolvableError(connectionResult.getErrorCode())) {
                            final int errorCode = connectionResult.getErrorCode();
                            new Handler(getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    // 此方法必须在主线程调用, xxxxxx.this 为当前界面的activity
                                    HuaweiApiAvailability.getInstance().resolveError(MainActivity.this, errorCode, REQUEST_HMS_RESOLVE_ERROR);
                                }
                            });
                        } else {
                            //其他错误码请参见开发指南或者API文档
                        }
                    }
                })
                .build();
        client.connect();
    }
    // ---  华为push end ---

    /**
     * 首页结束埋点
     */
    private void setSensorsViewScreenEndEvent() {
        try {
            JSONObject properties = new JSONObject();
            properties.put("pageName", getEventSource());
            properties.put("pageTitle", getEventSource());
            properties.put("refer", "");
            SensorsDataAPI.sharedInstance(this).trackTimerEnd("AppViewScreen", properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
