package com.hugboga.custom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huangbaoche.hbcframe.activity.BaseFragmentActivity;
import com.huangbaoche.hbcframe.util.MLog;
import com.hugboga.custom.adapter.MenuItemAdapter;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.bean.LvMenuItem;
import com.hugboga.custom.data.bean.PushMessage;
import com.hugboga.custom.data.bean.UserEntity;
import com.hugboga.custom.data.event.EventAction;
import com.hugboga.custom.fragment.BaseFragment;
import com.hugboga.custom.fragment.FgChat;
import com.hugboga.custom.fragment.FgChooseCity;
import com.hugboga.custom.fragment.FgCoupon;
import com.hugboga.custom.fragment.FgHome;
import com.hugboga.custom.fragment.FgIMChat;
import com.hugboga.custom.fragment.FgLogin;
import com.hugboga.custom.fragment.FgOrder;
import com.hugboga.custom.fragment.FgPersonInfo;
import com.hugboga.custom.fragment.FgServicerCenter;
import com.hugboga.custom.fragment.FgSetting;
import com.hugboga.custom.fragment.FgTest;
import com.hugboga.custom.fragment.FgTravel;
import com.hugboga.custom.utils.IMUtil;
import com.hugboga.custom.utils.ImageOptionUtils;
import com.hugboga.custom.utils.PhoneInfo;
import com.hugboga.custom.utils.UpdateResources;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import de.greenrobot.event.EventBus;


@ContentView(R.layout.activity_main)
public class MainActivity extends BaseFragmentActivity
        implements ViewPager.OnPageChangeListener, AdapterView.OnItemClickListener, View.OnClickListener {

    public static final String PUSH_BUNDLE_MSG = "pushMessage";
    public static final String FILTER_PUSH_DO = "com.hugboga.custom.pushdo";

    @ViewInject(R.id.drawer_layout)
    private DrawerLayout drawer;

    @ViewInject(R.id.container)
    private ViewPager mViewPager;

    private TextView tv_modify_info;//header的修改资料
    private ImageView my_icon_head;//header的头像
    private TextView tv_nickname;//header的昵称

//    @ViewInject(R.id.toolbar)
//    private Toolbar toolbar;

    private TextView tabMenu[] = new TextView[3];

//    @ViewInject(R.id.nav_view)
//    private NavigationView navigationView;

    @ViewInject(R.id.lv_slide_menu)
    private ListView mLvLeftMenu;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private FgHome fgHome;
    private FgChat fgChat;
    private FgTravel fgTravel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setSupportActionBar(toolbar);
        contentId = R.id.drawer_layout;
        initAdapterContent();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(this);
//        navigationView.setNavigationItemSelectedListener(this);
        JPushInterface.setAlias(MainActivity.this, PhoneInfo.getIMEI(this), null);
        initBottomView();
//        addErrorProcess();
        UpdateResources.checkLocalDB(this);
        UpdateResources.checkLocalResource(this);
        setUpDrawer();
        connectIM();
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initAdapterContent() {
        fgHome = new FgHome();
        fgChat = new FgChat();
        fgTravel = new FgTravel();
        addFragment(fgHome);
        addFragment(fgChat);
        addFragment(fgTravel);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Push任务接收器
     */
    class PushDoReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            PushMessage pushMessage = (PushMessage) intent.getSerializableExtra(PUSH_BUNDLE_MSG);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if ("rong".equals(intent.getData().getScheme())) {
            Bundle bundle = new Bundle();
            bundle.putString(FgIMChat.KEY_TITLE, intent.getData().toString());
            startFragment(new FgIMChat(), bundle);
        } else {
            PushMessage message = (PushMessage) intent.getSerializableExtra(MainActivity.PUSH_BUNDLE_MSG);
            if (message != null) {
                if ("IM".equals(message.type)) {
                    gotoChatList();
                } else {
                    gotoOrder(message);
                }
            }
        }
    }

    private void gotoChatList() {
        //如果是收到消息推送 关了上层的页面
        if (getFragmentList().size() > 3) {
            for (int i = getFragmentList().size() - 1; i >= 3; i--) {
                getFragmentList().get(i).finish();
            }
        }
        //跳转到聊天列表
        mViewPager.setCurrentItem(1);
    }

    private void gotoOrder(PushMessage message) {
        Bundle bundle = new Bundle();
        bundle.putInt(BaseFragment.KEY_BUSINESS_TYPE, message.orderType);
        bundle.putInt(BaseFragment.KEY_GOODS_TYPE, message.goodsType);
        bundle.putString(FgOrder.KEY_ORDER_ID, message.orderID);
        startFragment(new FgOrder(), bundle);
    }

    public void onEventMainThread(EventAction action) {
        switch (action.getType()) {
            case CLICK_USER_LOGIN:
            case CLICK_USER_LOOUT:
                refreshContent();
                break;
            case SET_MAIN_PAGE_INDEX:
                int index = Integer.valueOf(action.data.toString());
                if (index >= 0 && index < 3)
                    mViewPager.setCurrentItem(index);
                break;
            case CLICK_HEADER_LEFT_BTN_BACK:
                if (getFragmentsSize() == mSectionsPagerAdapter.getCount()) {
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED); //打开
                }
                break;
            case START_NEW_FRAGMENT:
                if (getFragmentsSize() > mSectionsPagerAdapter.getCount()) {
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED); //关闭手势滑动
                }
                break;
            default:
                break;
        }
    }

    private void connectIM() {
        if (UserEntity.getUser().getImToken(this) != null)
            new IMUtil(this).conn(UserEntity.getUser().imToken);
    }

    private void initBottomView() {
        tabMenu[0] = (TextView) findViewById(R.id.tab_text_1);
        tabMenu[1] = (TextView) findViewById(R.id.tab_text_2);
        tabMenu[2] = (TextView) findViewById(R.id.tab_text_3);
        tabMenu[0].setSelected(true);
    }


    private List<LvMenuItem> mItems = new ArrayList<LvMenuItem>(
            Arrays.asList(
                    new LvMenuItem(R.mipmap.personal_center_coupon, "优惠券", "3张可用"),
                    new LvMenuItem(R.mipmap.personal_center_customer_service, "客服中心", ""),
                    new LvMenuItem(R.mipmap.personal_center_internal, "境内客服", "仅限国内使用"),
                    new LvMenuItem(R.mipmap.personal_center_overseas, "境外客服", "仅限国外使用"),
                    new LvMenuItem(R.mipmap.personal_center_setting, "设置", "")
            ));

    private void setUpDrawer() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View header = inflater.inflate(R.layout.nav_header_main, null);
        tv_modify_info = (TextView) header.findViewById(R.id.tv_modify_info);//编辑
        tv_modify_info.setOnClickListener(this);
        my_icon_head = (ImageView) header.findViewById(R.id.my_icon_head);//头像
        my_icon_head.setOnClickListener(this);
        tv_nickname = (TextView) header.findViewById(R.id.tv_nickname);//昵称
        tv_nickname.setOnClickListener(this);

        mLvLeftMenu.addHeaderView(header);
        mLvLeftMenu.setAdapter(new MenuItemAdapter(this, mItems));
        mLvLeftMenu.setOnItemClickListener(this);

        refreshContent();
    }

    /**
     * 刷新左边侧滑栏
     */
    private void refreshContent() {
        if (!UserEntity.getUser().isLogin(this)) {
            my_icon_head.setImageResource(R.mipmap.chat_head);
            tv_nickname.setText(this.getResources().getString(R.string.person_center_nickname));
            tv_modify_info.setVisibility(View.INVISIBLE);
        } else {
            tv_modify_info.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(UserEntity.getUser().getAvatar(this))) {
                x.image().bind(my_icon_head, UserEntity.getUser().getAvatar(this), ImageOptionUtils.userPortraitImageOptions);
            } else {
                my_icon_head.setImageResource(R.mipmap.chat_head);
            }

            if (!TextUtils.isEmpty(UserEntity.getUser().getNickname(this))) {
                tv_nickname.setText(UserEntity.getUser().getNickname(this));
            } else {
                tv_nickname.setText(this.getResources().getString(R.string.person_center_no_nickname));
            }
        }
    }

    /**
     * 打开左侧菜单
     */
    public void openDrawer() {
        if (!drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START);
        }
        MLog.e(" openDrawer ");
    }

    private long exitTime;

    @Override
    public void onBackPressed() {
        MLog.e("getFragmentList().size() =" + getFragmentList().size());
        if (getFragmentList().size() > mSectionsPagerAdapter.getCount()) {
            doFragmentBack();
        } else {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                long times = System.currentTimeMillis();
                if ((times - exitTime) > 2000) {
                    Toast.makeText(this, "再次点击退出", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    finish();
                }
            }
        }
    }


    @Override
    public int getContentId() {
        return contentId;
    }

    public BaseFragment getTestFragment(String name) {
        FgTest fg = new FgTest();
        Bundle bundle = new Bundle();
        bundle.putString(FgTest.KEY_NAME, name);
        fg.setArguments(bundle);
        return fg;
    }

    private BaseFragment getFgChooseCityFragment() {
        FgChooseCity fgChooseCity = new FgChooseCity();
        String KEY_FROM = "key_from";
        Bundle bundle = new Bundle();
        bundle.putString(KEY_FROM, "startAddress");
        fgChooseCity.setArguments(bundle);
        return fgChooseCity;
    }


    @Event({R.id.tab_text_1, R.id.tab_text_2, R.id.tab_text_3})
    private void onClickView(View view) {
        switch (view.getId()) {
            case R.id.tab_text_1:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.tab_text_2:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.tab_text_3:
                mViewPager.setCurrentItem(2);
                break;
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
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case Constants.PERSONAL_CENTER_COUPON:
                //我的优惠券
                if (isLogin()) {
                    startFragment(new FgCoupon());
                    UserEntity.getUser().setHasNewCoupon(false);
//                    couponPoint.setVisibility(View.GONE);
                }
                break;
            case Constants.PERSONAL_CENTER_CUSTOMER_SERVICE:
                //客服
                startFragment(new FgServicerCenter());
                break;
            case Constants.PERSONAL_CENTER_INTERNAL_SERVICE:
                //境内客服
                PhoneInfo.CallDial(MainActivity.this, Constants.CALL_NUMBER_IN);
                break;
            case Constants.PERSONAL_CENTER_OVERSEAS_SERVICE:
                //境外客服
                PhoneInfo.CallDial(MainActivity.this, Constants.CALL_NUMBER_OUT);
                break;
            case Constants.PERSONAL_CENTER_SETTING:
                //我的设置
                if (isLogin()) {
//                    versionPoint.setVisibility(View.GONE);
                    startFragment(new FgSetting());
                }
                break;
            default:
                break;
        }
        drawer.closeDrawer(GravityCompat.START);


    }

    /**
     * 判断是否登录
     *
     * @return
     */
    private boolean isLogin() {
        if (UserEntity.getUser().isLogin(this)) {
            return true;
        } else {
            startFragment(new FgLogin());
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_modify_info:
                if (isLogin()) {
                    startFragment(new FgPersonInfo());
                }
                break;

            case R.id.my_icon_head:
            case R.id.tv_nickname:
                isLogin();
                break;

        }
        drawer.closeDrawer(GravityCompat.START);
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
                    return fgChat;
                }
                case 2: {
                    return fgTravel;
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "发现";
                case 1:
                    return "私聊";
                case 2:
                    return "行程";
            }
            return null;
        }
    }


}
