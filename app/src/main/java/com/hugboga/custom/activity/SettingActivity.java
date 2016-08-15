package com.hugboga.custom.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huangbaoche.hbcframe.HbcConfig;
import com.huangbaoche.hbcframe.data.net.ExceptionInfo;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.hugboga.custom.BuildConfig;
import com.hugboga.custom.R;
import com.hugboga.custom.data.bean.UserEntity;
import com.hugboga.custom.data.event.EventAction;
import com.hugboga.custom.data.event.EventType;
import com.hugboga.custom.data.parser.ParserLogout;
import com.hugboga.custom.data.request.RequestLogout;
import com.hugboga.custom.utils.SharedPre;

import org.greenrobot.eventbus.EventBus;
import org.xutils.x;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created on 16/8/6.
 */

public class SettingActivity extends BaseActivity {
    @Bind(R.id.setting_menu_clear_cache_flag)
    TextView cacheSizeTextView;
    @Bind(R.id.setting_menu_version_content_flag)
    TextView newVersionTextView;
    SharedPre sharedPre;
    Long cacheSize;
    @Bind(R.id.header_left_btn)
    ImageView headerLeftBtn;
    @Bind(R.id.header_right_btn)
    ImageView headerRightBtn;
    @Bind(R.id.header_title)
    TextView headerTitle;
    @Bind(R.id.header_right_txt)
    TextView headerRightTxt;
    @Bind(R.id.setting_menu_layout2)
    RelativeLayout settingMenuLayout2;
    @Bind(R.id.setting_clear_cache_arrow)
    ImageView settingClearCacheArrow;
    @Bind(R.id.setting_menu_layout7)
    RelativeLayout settingMenuLayout7;
    @Bind(R.id.setting_menu_layout3)
    RelativeLayout settingMenuLayout3;
    @Bind(R.id.setting_about_arrow)
    ImageView settingAboutArrow;
    @Bind(R.id.setting_menu_layout5)
    RelativeLayout settingMenuLayout5;
    @Bind(R.id.setting_exit)
    TextView settingExit;

    @Bind(R.id.setting_menu_developer_layout)
    RelativeLayout developerLayout;

    @Override
    public void onDataRequestSucceed(BaseRequest request) {
        if (request instanceof RequestLogout) {
//            activity.sendBroadcast(new Intent(FgHome.FILTER_FLUSH));
            UserEntity.getUser().clean(activity);
            EventBus.getDefault().post(new EventAction(EventType.CLICK_USER_LOOUT));
            finish();
        }
    }

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.fg_setting);
        ButterKnife.bind(this);
        initHeader();

//        if (HbcConfig.IS_DEBUG) {
//            developerLayout.setVisibility(View.VISIBLE);
//        } else {
//            developerLayout.setVisibility(View.GONE);
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDataRequestError(ExceptionInfo errorInfo, BaseRequest request) {
        if (request instanceof RequestLogout) {
//            activity.sendBroadcast(new Intent(FgHome.FILTER_FLUSH));
            UserEntity.getUser().clean(activity);
            EventBus.getDefault().post(new EventAction(EventType.CLICK_USER_LOOUT));
            finish();
        } else {
            super.onDataRequestError(errorInfo, request);
        }
    }

    Intent intent;
    @OnClick({R.id.setting_menu_layout2, R.id.setting_menu_layout3, R.id.setting_menu_layout5, R.id.setting_exit, R.id.setting_menu_layout7})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_menu_layout2:
                //修改密码
//                startFragment(new FgChangePsw());
                intent = new Intent(activity,ChangePswActivity.class);
                startActivity(intent);
                break;
            case R.id.setting_menu_layout3:
                //意见反馈
//                startFragment(new FgCallBack());
                intent = new Intent(activity,CallBackActivity.class);
                startActivity(intent);

                break;
            case R.id.setting_menu_layout5:
                //关于我们
//                startFragment(new FgAbout());
                intent = new Intent(activity,AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.setting_exit:
                //退出登录
                new AlertDialog.Builder(activity).setTitle("退出登录").setMessage("退出后不会删除任何历史数据，下次登录依然可以使用本账号").setNegativeButton("取消", null).setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParserLogout parser = new ParserLogout();
                        RequestLogout requestLogout = new RequestLogout(activity);
                        requestData(requestLogout);
                    }
                }).show();
                break;
            case R.id.setting_menu_layout7:
                new AlertDialog.Builder(activity)
                        .setTitle("清除缓存")
                        .setMessage("将删除" + getCacheSize() + "图片和系统预填信息")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                x.image().clearCacheFiles();
                                x.image().clearMemCache();
                                cacheSize = 0L;
                                cacheSizeTextView.setText(getCacheSize());
                                sharedPre.saveLongValue(SharedPre.CACHE_SIZE, 0);
                            }
                        }).show();
                break;
            default:
                break;
        }
    }


    protected void initHeader() {
        newVersionTextView.setText("v" + BuildConfig.VERSION_NAME);
        headerTitle.setText("设置");
        sharedPre = new SharedPre(activity);
        cacheSize = sharedPre.getLongValue(SharedPre.CACHE_SIZE);
        cacheSizeTextView.setText(getCacheSize());
        headerLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private String getCacheSize() {
        String result = "";
        if (cacheSize == null) {
            return result;
        }
        long oneKB = 1024;
        long oneMB = 1024 * 1024;
        long oneGB = 1024 * 1024 * 1024;
        if (cacheSize == 0) {
            return "0K";
        } else if (cacheSize > 0 && cacheSize < oneKB) {
            return "1K";
        } else if (cacheSize > oneKB && cacheSize < oneMB) {
            long num = cacheSize / oneKB;
            return num + "K";
        } else if (cacheSize > oneMB && cacheSize < oneGB) {
            long num = cacheSize / oneMB;
            return num + "M";
        } else if (cacheSize > oneGB) {
            long num = cacheSize / oneGB;
            return num + "G";
        }
        return result;
    }


}

