package com.hugboga.custom.widget.charter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hugboga.custom.R;
import com.hugboga.custom.activity.BaseActivity;
import com.hugboga.custom.activity.ChooseAirActivity;
import com.hugboga.custom.activity.ChooseAirPortActivity;
import com.hugboga.custom.activity.ChooseCityActivity;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.bean.CityBean;
import com.hugboga.custom.statistic.MobClickUtils;
import com.hugboga.custom.statistic.StatisticConstant;
import com.hugboga.custom.utils.CharterDataUtils;
import com.hugboga.custom.utils.SharedPre;
import com.hugboga.custom.utils.UIUtils;
import com.hugboga.custom.widget.guideview.Component;
import com.hugboga.custom.widget.guideview.Guide;
import com.hugboga.custom.widget.guideview.GuideBuilder;
import com.hugboga.custom.widget.guideview.MutiComponent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by qingcha on 17/2/25.
 */
public class CharterSubtitleView extends LinearLayout{

    public static final String TAG = CharterSubtitleView.class.getSimpleName();

    @BindView(R.id.charter_subtitle_day_tv)
    TextView dayTV;
    @BindView(R.id.charter_subtitle_amend_tv)
    TextView amendTV;

    @BindView(R.id.charter_subtitle_pickup_layout)
    LinearLayout pickupLayout;
    @BindView(R.id.charter_subtitle_pickup_icon_iv)
    ImageView iconIV;
    @BindView(R.id.charter_subtitle_pickup_left_tv)
    TextView leftTV;
    @BindView(R.id.charter_subtitle_pickup_right_tv)
    TextView rightTV;
    @BindView(R.id.charter_subtitle_pickup_arrow_iv)
    ImageView pickupArrowIV;

    private Context context;
    private CharterDataUtils charterDataUtils;
    private OnPickUpOrSendSelectedListener listener;

    public CharterSubtitleView(Context context) {
        this(context, null);
    }

    public CharterSubtitleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        View view = inflate(context, R.layout.view_charter_subtitle, this);
        ButterKnife.bind(view);

        amendTV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        amendTV.getPaint().setAntiAlias(true);//抗锯齿

        charterDataUtils = CharterDataUtils.getInstance();
    }

    public void update() {
        CityBean currentDayCityBean = charterDataUtils.getCurrentDayStartCityBean();
        if (charterDataUtils.chooseDateBean.dayNums == 1) {
            dayTV.setText(currentDayCityBean.name);
        } else {
            dayTV.setText(String.format("第%1$s天: %2$s", charterDataUtils.currentDay, currentDayCityBean.name));
        }

        if (charterDataUtils.isShowEmpty) {
            pickupLayout.setVisibility(View.GONE);
            amendTV.setVisibility(View.GONE);
            return;
        }

        if (charterDataUtils.isFirstDay()) {
            pickupLayout.setVisibility(View.VISIBLE);
            amendTV.setVisibility(View.GONE);
            if (charterDataUtils.flightBean != null) {
                iconIV.setBackgroundResource(R.drawable.selector_charter_pickup);
                iconIV.setSelected(charterDataUtils.isSelectedPickUp);
                leftTV.setText("从机场出发 ");
                rightTV.setText(charterDataUtils.flightBean.flightNo);
                rightTV.setTextColor(getContext().getResources().getColor(R.color.default_black));
                pickupArrowIV.setVisibility(View.VISIBLE);
            } else {
                iconIV.setBackgroundResource(R.mipmap.trip_icon_come);
                leftTV.setText("从机场出发");
                rightTV.setText("请添加航班号");
                rightTV.setTextColor(getContext().getResources().getColor(R.color.default_highlight_blue));
                pickupArrowIV.setVisibility(View.GONE);
                showGuide(true);
            }
        } else if( charterDataUtils.isLastDay()) {
            amendTV.setVisibility(View.VISIBLE);
            pickupLayout.setVisibility(View.VISIBLE);
            if (charterDataUtils.airPortBean != null) {
                iconIV.setBackgroundResource(R.drawable.selector_charter_pickup);
                iconIV.setSelected(charterDataUtils.isSelectedSend);
                leftTV.setText("送机 ");
                rightTV.setText(charterDataUtils.airPortBean.airportName);
                rightTV.setTextColor(getContext().getResources().getColor(R.color.default_black));
                pickupArrowIV.setVisibility(View.VISIBLE);
            } else {
                iconIV.setBackgroundResource(R.mipmap.trip_icon_go);
                leftTV.setText("如需送机");
                rightTV.setText("请选择送机机场");
                rightTV.setTextColor(getContext().getResources().getColor(R.color.default_highlight_blue));
                pickupArrowIV.setVisibility(View.GONE);
                showGuide(false);
            }
        } else {
            amendTV.setVisibility(View.VISIBLE);
            pickupLayout.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.charter_subtitle_pickup_layout})
    public void onClick(View view) {
        if (charterDataUtils.isFirstDay() && (charterDataUtils.isSelectedPickUp || charterDataUtils.flightBean == null)) {//包车第一天，添写接机航班号
            Intent intent = new Intent(context,ChooseAirActivity.class);
            if(charterDataUtils.flightBean != null){
                Bundle bundle = new Bundle();
                bundle.putSerializable("flightBean",charterDataUtils.flightBean);
                intent.putExtra("flightBean",bundle);
            }
            getContext().startActivity(intent);
            MobClickUtils.onEvent(StatisticConstant.R_ADDJ);
        } else if (charterDataUtils.currentDay > 1 && charterDataUtils.isLastDay() && (charterDataUtils.isSelectedSend || charterDataUtils.airPortBean == null)) {//包车最后一天，添写送达机场
            Intent intent = new Intent(context, ChooseAirPortActivity.class);
            intent.putExtra(Constants.PARAMS_SOURCE, getEventSource());
            if (charterDataUtils.getCurrentDayStartCityBean() != null) {
                intent.putExtra(ChooseAirPortActivity.KEY_GROUPID, charterDataUtils.getCurrentDayStartCityBean().groupId);
            }
            context.startActivity(intent);
            MobClickUtils.onEvent(StatisticConstant.R_ADDS);
        }
    }

    @OnClick({R.id.charter_subtitle_pickup_icon_iv})
    public void onSelectedPickUpOrSend() {
        boolean isSelected = true;
        boolean isPickUp = true;
        if (charterDataUtils.isFirstDay()) {
            charterDataUtils.isSelectedPickUp = !charterDataUtils.isSelectedPickUp;
            isSelected = charterDataUtils.isSelectedPickUp;
            isPickUp = true;
        } else if (charterDataUtils.isLastDay()) {
            charterDataUtils.isSelectedSend = !charterDataUtils.isSelectedSend;
            isSelected = charterDataUtils.isSelectedSend;
            isPickUp = false;
        }
        iconIV.setSelected(isSelected);
        if (listener != null) {
            listener.onPickUpOrSendSelected(isPickUp, isSelected);
        }
    }

    private void intentActivity(Class<?> cls) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(Constants.PARAMS_SOURCE, getEventSource());
        context.startActivity(intent);
    }

    private String getEventSource() {
        if (context instanceof BaseActivity) {
            return ((BaseActivity) context).getEventSource();
        } else {
            return "";
        }
    }

    @OnClick({R.id.charter_subtitle_amend_tv})
    public void selectStartCity() {
        if (charterDataUtils.isFirstDay()) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt(ChooseCityActivity.KEY_BUSINESS_TYPE, Constants.BUSINESS_TYPE_DAILY);
        bundle.putString(ChooseCityActivity.KEY_FROM_TAG, CharterSubtitleView.TAG);
        bundle.putInt(ChooseCityActivity.KEY_CITY_ID, charterDataUtils.getCurrentDayStartCityBean().cityId);
        bundle.putString(Constants.PARAMS_SOURCE, getEventSource());
        bundle.putString(ChooseCityActivity.KEY_FROM, ChooseCityActivity.GROUP_START);
        bundle.putInt(ChooseCityActivity.KEY_SHOW_TYPE, ChooseCityActivity.ShowType.GROUP_START);
        Intent intent = new Intent(context, ChooseCityActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
        MobClickUtils.onEvent(StatisticConstant.R_CHANGCITY);
    }

    public interface OnPickUpOrSendSelectedListener {
        /**
         * @param isPickUp 接机true、送机false
         */
        public void onPickUpOrSendSelected(boolean isPickUp, boolean isSelected);
    }

    public void setOnPickUpOrSendSelectedListener(OnPickUpOrSendSelectedListener listener) {
        this.listener = listener;
    }

    public static final String PICKUP_GUIDE_VISITED = "pickup_guide_visited";
    public static final String SEND_GUIDE_VISITED = "send_guide_visited";
    private Guide guide;
    boolean isdd = false;
    boolean isPickup;

    public synchronized void showGuide(boolean _isPickup) {
        this.isPickup = _isPickup;
        final boolean isVisited = SharedPre.getBoolean(_isPickup ? CharterSubtitleView.PICKUP_GUIDE_VISITED: CharterSubtitleView.SEND_GUIDE_VISITED, false);
        if (isVisited) {
            return;
        }
        isdd = false;
        ViewTreeObserver vto = pickupLayout.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                if (!isdd) {
                    if (isPickup) {
                        initGuideView();
                    } else {
                        handler.sendEmptyMessageDelayed(0, 500);
                    }
                    SharedPre.setBoolean(isPickup ? CharterSubtitleView.PICKUP_GUIDE_VISITED : CharterSubtitleView.SEND_GUIDE_VISITED, true);
                    isdd = true;
                }
                return true;
            }
        });
    }

    private synchronized void initGuideView() {
        GuideBuilder builder = new GuideBuilder();
        builder.setTargetView(pickupLayout)
                .setAlpha(150)
                .setHighTargetGraphStyle(Component.ROUNDRECT)
                .setHighTargetPadding(UIUtils.dip2px(1))
                .setHighTargetCorner(UIUtils.dip2px(3))
                .setOverlayTarget(false)
                .setOutsideTouchable(false);
        builder.setOnVisibilityChangedListener(new GuideBuilder.OnVisibilityChangedListener() {
            @Override
            public void onShown() {
            }

            @Override
            public void onDismiss() {
            }
        });
        builder.addComponent(new MutiComponent(isPickup));
        guide = builder.createGuide();
        guide.setShouldCheckLocInWindow(true);
        guide.show((Activity) getContext());
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            initGuideView();
        }
    };
}
