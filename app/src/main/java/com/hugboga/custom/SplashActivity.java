package com.hugboga.custom;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hugboga.custom.action.data.ActionBean;
import com.hugboga.custom.activity.BaseActivity;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.bean.UserEntity;
import com.hugboga.custom.utils.PhoneInfo;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SplashActivity extends BaseActivity {

    //引导图片资源
    private static final int[] pics = {R.drawable.splash_1, R.drawable.splash_2, R.drawable.splash_3};

    @BindView(R.id.splash_viewpage)
    ViewPager viewPager;
    @BindView(R.id.indicator)
    CirclePageIndicator mIndicator;
    //TextView enter;

    private ActionBean actionBean;

    @Override
    public int getContentViewId() {
        return R.layout.activity_splash;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            actionBean = (ActionBean) savedInstanceState.getSerializable(Constants.PARAMS_ACTION);
        } else {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                actionBean = (ActionBean) bundle.getSerializable(Constants.PARAMS_ACTION);
            }
        }
        gotoSetp(); //执行主体任务
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (actionBean != null) {
            outState.putSerializable(Constants.PARAMS_ACTION, actionBean);
        }
    }

    /**
     * 执行引导页显示任务
     */
    private void gotoSetp() {
        /*
         * 构建过第一次引导页之后，改变首次运行值
         * 解决在finish之前修改的问题
         */
        UserEntity.getUser().setVersion(SplashActivity.this, PhoneInfo.getSoftwareVersion(SplashActivity.this));

        //构建滑动页
        List<View> views = new ArrayList<View>();
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        for (int i = 0; i < pics.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(mParams);
            iv.setImageResource(pics[i]);
            views.add(iv);
        }

        //增加最后一页滑动
//        RelativeLayout relativeLayout = new RelativeLayout(this);
//        relativeLayout.setLayoutParams(mParams);
//        relativeLayout.setBackgroundColor(getResources().getColor(R.color.basic_white));
//        relativeLayout.setAnimation(AnimationUtils.loadAnimation(this, R.anim.exit_alpha));
//        views.add(relativeLayout);

        AdPageAdapter aAdapter = new AdPageAdapter(views);
        viewPager.setAdapter(aAdapter);

        //mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        //mIndicator.setViewPager(viewPager);

        //enter = (TextView)findViewById(R.id.enter);
//        enter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finishHandler.sendEmptyMessageDelayed(0,0);
//            }
//        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                if (arg0 == 2) {
                    //enter.setVisibility(View.VISIBLE);
                    //AnimationUtils.showAnimationAlpha(enter,500,null);
                }else{
                    //enter.setVisibility(GONE);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    /**
     * 进入登录界面
     */
    Handler finishHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            //记录首次运行标记
            super.handleMessage(msg);
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            if (actionBean != null) {
                intent.putExtra(Constants.PARAMS_ACTION, actionBean);
            }
            startActivity(intent);
            finish();
        }

    };

    /**
     * 引导页
     * com.hugboga.custom.AdPageAdapter
     *
     * @author ZHZEPHI
     *         Create at 2015年1月30日 下午7:06:36
     */
    public class AdPageAdapter extends PagerAdapter {

        private List<View> views;

        public AdPageAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView(views.get(position));
        }

        /* (non-Javadoc)
         * @see android.support.v4.view.PagerAdapter#getCount()
         */
        @Override
        public int getCount() {
            if (views != null) {
                return views.size();
            }
            return 0;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = views.get(position);
            if (position == 2) {
                view.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        finishHandler.sendEmptyMessage(0);
                    }
                });
            }
            ((ViewPager) container).addView(view, 0);
            return views.get(position);
        }

        /* (non-Javadoc)
         * @see android.support.v4.view.PagerAdapter#isViewFromObject(android.view.View, java.lang.Object)
         */
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return (arg0 == arg1);
        }

    }
}
