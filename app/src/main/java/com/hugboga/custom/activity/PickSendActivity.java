package com.hugboga.custom.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.hugboga.custom.R;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.bean.AirPort;
import com.hugboga.custom.data.bean.FlightBean;
import com.hugboga.custom.data.bean.GuidesDetailData;
import com.hugboga.custom.data.bean.PoiBean;
import com.hugboga.custom.data.event.EventAction;
import com.hugboga.custom.data.event.EventType;
import com.hugboga.custom.fragment.FgPickup;
import com.hugboga.custom.fragment.FgSend;
import com.hugboga.custom.statistic.sensors.SensorsUtils;
import com.hugboga.custom.utils.CommonUtils;
import com.hugboga.custom.utils.OrderUtils;
import com.hugboga.custom.utils.SharedPre;
import com.hugboga.custom.widget.CsDialog;
import com.hugboga.custom.widget.title.TitleBarPickSend;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;

import butterknife.BindView;

/**
 * Created by qingcha on 17/5/17.
 */
public class PickSendActivity extends BaseActivity implements TitleBarPickSend.TitlerBarOnClickLister {

    @BindView(R.id.pick_send_titlebar)
    TitleBarPickSend titlebar;

    private Fragment currentFragment;
    private PickSendActivity.Params params;

    CsDialog csDialog;
    public static class Params implements Serializable {
        public GuidesDetailData guidesDetailData;
        public Integer type;
        public AirPort airPortBean;
        public PoiBean startPoiBean;
        public FlightBean flightBean;

        public String timeLimitedSaleNo;         // 秒杀活动编号
        public String timeLimitedSaleScheduleNo; // 秒杀活动场次编号
        public boolean isSeckills = false;       // 是否进入秒杀通道

        public String cityId;
        public String cityName;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_pick_send;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            params = (PickSendActivity.Params) savedInstanceState.getSerializable(Constants.PARAMS_DATA);
        } else {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                params = (PickSendActivity.Params) bundle.getSerializable(Constants.PARAMS_DATA);
            }
        }
        initView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (params != null) {
            outState.putSerializable(Constants.PARAMS_DATA, params);
        }
    }

    public void initView() {
        titlebar.setTitlerBarOnClickLister(this);
        if (params != null && params.type != null) {
            titlebar.onTabSelected(params.type);
        } else {
            titlebar.onTabSelected(0);
        }
    }

    @Override
    public void onBack() {
        if (!isShowSaveDialog()) {
            finish();
        } else {
            refreshSeckillsWeb();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (isShowSaveDialog()) {
                return true;
            } else {
                refreshSeckillsWeb();
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    public boolean isShowSaveDialog() {
        boolean isShowDialog = false;
        if (currentFragment instanceof FgPickup) {
            isShowDialog = !((FgPickup) currentFragment).isFlightBeanNull();
        } else if (currentFragment instanceof FgSend) {
            isShowDialog = ((FgSend) currentFragment).isShowSaveDialog();
        }
        if (isShowDialog) {
            OrderUtils.showSaveDialog(this, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    refreshSeckillsWeb();
                    dialog.dismiss();
                    PickSendActivity.this.finish();
                }
            } );
        } else {
            refreshSeckillsWeb();
        }
        return isShowDialog;
    }

    public void refreshSeckillsWeb() {
        if (params != null && params.isSeckills) {
            EventBus.getDefault().post(new EventAction(EventType.WEBINFO_REFRESH));
        }
    }

    public PickSendActivity.Params getParams() {
        return params;
    }

    @Override
    public void onCustomerService() {
        String eventSource = currentFragment instanceof FgPickup ? "接机" : "送机";
        SensorsUtils.onAppClick(eventSource, "客服", getIntentSource());
        //DialogUtil.getInstance(PickSendActivity.this).showServiceDialog(PickSendActivity.this, null, UnicornServiceActivity.SourceType.TYPE_CHARTERED, null, null, eventSource);
        csDialog = CommonUtils.csDialog(PickSendActivity.this, null, null, null, UnicornServiceActivity.SourceType.TYPE_CHARTERED, eventSource, new CsDialog.OnCsListener() {
            @Override
            public void onCs() {
                if (csDialog != null && csDialog.isShowing()) {
                    csDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onTabSelected(int index) {
        changeFragment(index == 0 ? FgPickup.TAG : FgSend.TAG);
    }

    public void changeFragment(String tag) {
        hideSoftInput();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (currentFragment != null) {
            ft.hide(currentFragment);
        }
        if (fragment == null) {
            if (FgPickup.TAG.equals(tag)) {
                fragment = new FgPickup();
            } else if (FgSend.TAG.equals(tag)) {
                fragment = new FgSend();
            }

            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.PARAMS_DATA, params);
            bundle.putString(Constants.PARAMS_SOURCE, getIntentSource());
            fragment.setArguments(bundle);

            ft.add(R.id.pick_send_container_layout, fragment, tag);
        } else {
            ft.show(fragment);
        }

        currentFragment = fragment;
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //离开接送机界面,将选航班号的类型初始化按选航班号类型=1
        SharedPre sharedPre = new SharedPre(PickSendActivity.this);
        sharedPre.saveIntValue("chooseAirType",1);
    }

    @Override
    public void finish() {
        super.finish();
        //离开接送机界面,将选航班号的类型初始化按选航班号类型=1
        SharedPre sharedPre = new SharedPre(PickSendActivity.this);
        sharedPre.saveIntValue("chooseAirType",1);
    }
}
