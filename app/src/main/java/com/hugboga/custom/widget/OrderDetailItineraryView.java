package com.hugboga.custom.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hugboga.custom.R;
import com.hugboga.custom.activity.DetailTravelListActivity;
import com.hugboga.custom.activity.LuggageInfoActivity;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.bean.OrderBean;
import com.hugboga.custom.data.event.EventAction;
import com.hugboga.custom.data.event.EventType;
import com.hugboga.custom.statistic.MobClickUtils;
import com.hugboga.custom.statistic.StatisticConstant;
import com.hugboga.custom.statistic.click.StatisticClickEvent;
import com.hugboga.custom.utils.CommonUtils;
import com.hugboga.custom.utils.DateUtils;
import com.hugboga.custom.utils.Tools;
import com.hugboga.custom.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by qingcha on 16/6/2.
 */
public class OrderDetailItineraryView extends LinearLayout implements HbcViewBehavior, View.OnClickListener{

    @Bind(R.id.order_itinerary_item_layout)
    LinearLayout itineraryLayout;
    @Bind(R.id.order_itinerary_order_number_view)
    OrderDetailNoView orderNumberView;

    @Bind(R.id.order_itinerary_route_tv)
    TextView routeTV;
    @Bind(R.id.order_itinerary_route_iv)
    ImageView routeIV;
    @Bind(R.id.order_itinerary_route_tag_iv)
    ImageView routeTagIV;
    @Bind(R.id.order_itinerary_route_layout)
    RelativeLayout routeLayout;

    @Bind(R.id.order_itinerary_charter_layout)
    RelativeLayout charterLayout;
    @Bind(R.id.order_itinerary_charter_city_tv)
    TextView cityTV;

    @Bind(R.id.order_itinerary_item_travel_view)
    OrderDetailTravelView travelView;


    private OrderBean orderBean;

    public OrderDetailItineraryView(Context context) {
        this(context, null);
    }

    public OrderDetailItineraryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = inflate(context, R.layout.view_order_detail_itinerary, this);
        ButterKnife.bind(view);
    }

    @Override
    public void update(Object _data) {
        if (_data == null) {
            return;
        }
        orderBean = (OrderBean) _data;
        itineraryLayout.removeAllViews();

        // 包车开始城市
        if (orderBean.orderType == 3 || orderBean.orderType == 888) {
            charterLayout.setVisibility(View.VISIBLE);
            cityTV.setText(orderBean.serviceCityName + " - " + orderBean.serviceEndCityName);
        } else {
            charterLayout.setVisibility(View.GONE);
        }

        // 固定线路 超省心、推荐路线 超自由
        if ((orderBean.orderType == 5 || orderBean.orderType == 6) && !TextUtils.isEmpty(orderBean.lineSubject)) {
            setRouteLayoutVisible();
        } else {
            routeLayout.setVisibility(View.GONE);
            routeLayout.setOnClickListener(null);
        }

        // 游玩日期
        if (orderBean.orderType == 3 || orderBean.orderType == 888 || orderBean.orderType == 5 || orderBean.orderType == 6) {
            String startDate = DateUtils.getPointStrFromDate2(orderBean.serviceTime);
            String totalDays = "" + orderBean.totalDays;
            if (orderBean.isHalfDaily == 1) {//半日包
                totalDays = getContext().getString(R.string.order_detail_half_day);
            } else if (orderBean.totalDays > 1 && !TextUtils.isEmpty(orderBean.serviceEndTime)) {
                startDate = startDate + " - " + DateUtils.getPointStrFromDate2(orderBean.serviceEndTime);
            }
            startDate += String.format("(%1$s天)", totalDays);
            addItemView(R.mipmap.trip_icon_date, startDate);
        } else {
            //副标题："航班HKJHKJ 东京-北京"
            String flight = "";
            if (!TextUtils.isEmpty(orderBean.flightNo)) {
                flight = getContext().getString(R.string.order_detail_flight, orderBean.flightNo);
            }
            if (!TextUtils.isEmpty(orderBean.flightDeptCityName) && !TextUtils.isEmpty(orderBean.flightDestCityName)) {
                flight += getContext().getString(R.string.separator, orderBean.flightDeptCityName, orderBean.flightDestCityName);
            }
            String startDate = DateUtils.getWeekStrFromDate2(orderBean.serviceTime);
            if (TextUtils.isEmpty(flight)) {
                addItemView(R.mipmap.trip_icon_time, startDate);
            } else {
                addSubItemView(R.mipmap.trip_icon_time, startDate, flight);
            }
        }

        if (orderBean.orderType == 5 || orderBean.orderType == 6) {//线路 开始城市 - 结束城市
            addItemView(R.mipmap.trip_icon_line, orderBean.serviceCityName + " - " + orderBean.serviceEndCityName);
        } else if (orderBean.orderType == 1 || orderBean.orderType == 2 || orderBean.orderType == 4) {//开始地点 - 结束地点 接机、送机、单次
            addLocationItem();
        }

        // 车型描述
        if (!TextUtils.isEmpty(orderBean.carDesc)) {
            addItemView(R.mipmap.trip_icon_car, orderBean.carDesc);
        }

        // 出行人数
        String passengerCount = String.format("成人%1$s位", orderBean.adult);
        if (orderBean.child != null && orderBean.child > 0) {
            passengerCount += String.format("，儿童%1$s位", orderBean.child);
        }
        addItemView(R.mipmap.trip_icon_people, passengerCount);

        String luggageStr = "";
        if (orderBean.childSeats != null && orderBean.childSeats.getChildSeatCount() > 0) {//儿童座椅数
            luggageStr += String.format("儿童座椅%1$s个，", orderBean.childSeats.getChildSeatCount());
        }
//        if (CommonUtils.getCountInteger(orderBean.luggageNum) > 0) {
            luggageStr += String.format("最多携带行李%1$s件", orderBean.luggageNum);
//        }
        addLuggageView(luggageStr);

        if (orderBean.orderGoodsType == 1  && "1".equalsIgnoreCase(orderBean.isFlightSign)) {//接机
            addItemView(R.mipmap.trip_icon_addition, "接机举牌等待");
        } else if (orderBean.orderGoodsType == 2 && "1".equals(orderBean.isCheckin)) {//送机checkin
            addItemView(R.mipmap.trip_icon_addition, "送机协助办理登机");
        }

        //酒店预订
        if (orderBean.hotelStatus == 1) {
            addItemView(R.mipmap.trip_icon_hotel, getContext().getString(R.string.order_detail_hotle_subscribe, "" + orderBean.hotelDays, "" + orderBean.hotelRoom));
        }

        //其他费用
        if (orderBean.orderPriceInfo != null && orderBean.orderPriceInfo.goodsOtherPrice > 0) {
            addItemView(R.mipmap.other_fees_icon, String.format("其他费用%1$s × %2$s人", orderBean.orderPriceInfo.goodsOtherPrice, orderBean.getTravelerCount()));
        }

        // 订单号
        if (orderBean.orderType == 888) {
            orderNumberView.setVisibility(View.GONE);
        } else {
            orderNumberView.setVisibility(View.VISIBLE);
            orderNumberView.update(orderBean.orderNo);
        }

        if (orderBean.orderType == 3) {
            travelView.singleTravel();
            travelView.setVisibility(View.VISIBLE);
            orderBean.orderIndex = 1;
            travelView.update(orderBean);
        } else {
            travelView.setVisibility(View.GONE);
        }
    }


    private LinearLayout addItemView(int iconId, String title) {
        LinearLayout itemView = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.item_order_detail_itinerary, null, false);

        ImageView iconIV = (ImageView) itemView.findViewById(R.id.item_itinerary_iv);
        iconIV.setBackgroundResource(iconId);
        TextView titleTV = (TextView) itemView.findViewById(R.id.item_itinerary_title_tv);
        titleTV.setText(title);

        itineraryLayout.addView(itemView);
        return itemView;
    }

    private LinearLayout addSubItemView(int iconId, String title, String subTitle) {//带副标题
        LinearLayout itemView = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.item_order_detail_itinerary_sub, null, false);

        ImageView iconIV = (ImageView) itemView.findViewById(R.id.item_itinerary_sub_iv);
        iconIV.setBackgroundResource(iconId);
        TextView titleTV = (TextView) itemView.findViewById(R.id.item_itinerary_sub_title_tv);
        titleTV.setText(title);

        TextView describeTV = (TextView) itemView.findViewById(R.id.item_itinerary_sub_describe_tv);
        if (TextUtils.isEmpty(subTitle)) {
            describeTV.setVisibility(View.GONE);
        } else {
            describeTV.setVisibility(View.VISIBLE);
            describeTV.setText(subTitle);
        }
        itineraryLayout.addView(itemView);
        return itemView;
    }

    public void setRouteLayoutVisible() {
        routeLayout.setVisibility(View.VISIBLE);
        Tools.showImage(routeIV, orderBean.picUrl);
        routeTV.setText(orderBean.lineSubject);
        routeTagIV.setVisibility(orderBean.carPool ? View.VISIBLE : View.GONE);//拼车
        if (orderBean.orderSource == 1 && !TextUtils.isEmpty(orderBean.skuDetailUrl)) {
            routeLayout.setOnClickListener(this);
        }
    }

    private void addLuggageView(String title) {
        RelativeLayout itemView = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.item_order_detail_itinerary_luggage, null, false);
        TextView textView = (TextView) itemView.findViewById(R.id.item_itinerary_luggage_title_tv);
        textView.setText(title);
        itemView.findViewById(R.id.item_itinerary_luggage_info_layout).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), LuggageInfoActivity.class));
            }
        });
        itineraryLayout.addView(itemView, LayoutParams.MATCH_PARENT, UIUtils.dip2px(22));
    }

    private LinearLayout addLocationItem() {
        LinearLayout itemView = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_order_detail_location, null, false);
        TextView startTV = (TextView) itemView.findViewById(R.id.order_travel_item_start_tv);
        TextView startDesTV = (TextView) itemView.findViewById(R.id.order_travel_item_start_des_tv);
        View startLineView = itemView.findViewById(R.id.order_travel_item_start_line_view);

        TextView endTV = (TextView) itemView.findViewById(R.id.order_travel_item_end_tv);
        TextView endDesTV = (TextView) itemView.findViewById(R.id.order_travel_item_end_des_tv);
        startTV.setText(orderBean.startAddress);
        if (!TextUtils.isEmpty(orderBean.startAddressDetail)) {
            startDesTV.setVisibility(View.VISIBLE);
            startDesTV.setText(orderBean.startAddressDetail);

            int showWidth = UIUtils.getScreenWidth() - (getContext().getResources().getDimensionPixelOffset(R.dimen.order_padding_left) * 2 + UIUtils.dip2px(24) + UIUtils.dip2px(3));
            int stringWidth = UIUtils.getStringWidth(startDesTV, orderBean.startAddressDetail);
            int lines = (int)Math.ceil(stringWidth / showWidth);
            int startDesViewWidth = UIUtils.dip2px(20) + lines * UIUtils.dip2px(10);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(UIUtils.dip2px(2), startDesViewWidth);
            params.topMargin = UIUtils.dip2px(20);
            params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.travel_item_start_des_tv);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.leftMargin = UIUtils.dip2px(7.5f);
            startLineView.setLayoutParams(params);
        } else {
            startDesTV.setVisibility(View.GONE);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(UIUtils.dip2px(2), UIUtils.dip2px(5));
            params.topMargin = UIUtils.dip2px(20);
            params.leftMargin = UIUtils.dip2px(7.5f);
            startLineView.setLayoutParams(params);
        }
        endTV.setText(orderBean.destAddress);
        if (!TextUtils.isEmpty(orderBean.destAddressDetail)) {
            endDesTV.setVisibility(View.VISIBLE);
            endDesTV.setText(orderBean.destAddressDetail);
        } else {
            endDesTV.setVisibility(View.GONE);
        }
        itineraryLayout.addView(itemView);
        return itemView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.order_itinerary_route_layout://路线详情
                EventBus.getDefault().post(new EventAction(EventType.ORDER_DETAIL_ROUTE, orderBean.orderNo));
                break;
        }
    }

    @OnClick({R.id.order_itinerary_charter_travel_tv, R.id.order_itinerary_charter_arrow_iv})
    public void intentTravelList() {
        Intent intent = new Intent(getContext(), DetailTravelListActivity.class);
        intent.putExtra(Constants.PARAMS_DATA, orderBean);
        getContext().startActivity(intent);
        StatisticClickEvent.click(StatisticConstant.R_XINGCHENG, "订单详情");
    }
}
