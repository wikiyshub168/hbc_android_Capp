package com.hugboga.custom.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hugboga.custom.R;
import com.hugboga.custom.data.bean.OrderBean;
import com.hugboga.custom.data.bean.OrderStatus;
import com.hugboga.custom.utils.UIUtils;

/**
 * Created by qingcha on 16/6/2.
 */
public class OrderDetailAmountView extends LinearLayout implements HbcViewBehavior  {

    private LinearLayout billLayout;
    private LinearLayout groupLayout;

    public OrderDetailAmountView(Context context) {
        this(context, null);
    }

    public OrderDetailAmountView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_order_detail_amount, this);
        billLayout = (LinearLayout) findViewById(R.id.order_detail_amount_bill_layout);
        groupLayout = (LinearLayout) findViewById(R.id.order_detail_amount_group_layout);
    }

    @Override
    public void update(Object _data) {
        if (_data == null) {
            return;
        }
        OrderBean orderBean = (OrderBean) _data;

        billLayout.removeAllViews();
        groupLayout.removeAllViews();
        addBillView(R.string.order_detail_cost_chartered, null);//TODO 包车费用 orderPrice
        if (orderBean.orderGoodsType == 1) {//TODO 接机 举牌费用   priceFlightBrandSign
            addBillView(R.string.order_detail_cost_placards, null);
        } else if(orderBean.orderGoodsType == 2) {//送机 checkin费用  orderPriceInfo.checkInPrice
            addBillView(R.string.order_detail_cost_checkin, null);
        }
        addBillView(R.string.order_detail_cost_child_seats, null);//TODO 儿童座椅 childSeat childSeatPrice1+childSeatPrice2

        addGroupView(R.string.order_detail_cost_total, null);//TODO 费用总计 shouldPay
        addGroupView(R.string.order_detail_cost_coupon, null);//TODO 优惠金额 coupons

        double realSum = orderBean.orderStatus == OrderStatus.INITSTATE ? orderBean.orderPriceInfo.shouldPay : orderBean.orderPriceInfo.actualPay;
        addGroupView(R.string.order_detail_cost_realpay, String.valueOf(realSum));//TODO 实付款 1 shouldPay actualPay
    }

    private void addBillView(int titleID, String price) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.include_order_detail_amount_item, null, false);
        TextView titleTV = (TextView) itemView.findViewById(R.id.order_detail_amount_title_tv);
        titleTV.setText(getContext().getString(titleID));
        titleTV.setMinWidth(UIUtils.dip2px(80));
        titleTV.setTextSize(13);
        TextView priceTV = (TextView) itemView.findViewById(R.id.order_detail_amount_price_tv);
        priceTV.setTextSize(14);
        if (TextUtils.isEmpty(price)) {
            price = "0";
        }
        priceTV.setText(getContext().getString(R.string.sign_rmb) + price);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, UIUtils.dip2px(35));
        params.setMargins(UIUtils.dip2px(20), 0,UIUtils.dip2px(20), 0);
        billLayout.addView(itemView, params);
    }

    private void addGroupView(int titleID, String price) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.include_order_detail_amount_item, null, false);
        TextView titleTV = (TextView) itemView.findViewById(R.id.order_detail_amount_title_tv);
        titleTV.setText(getContext().getString(titleID));
        TextView priceTV = (TextView) itemView.findViewById(R.id.order_detail_amount_price_tv);
        if (TextUtils.isEmpty(price)) {
            price = "0";
        }
        String priceText = getContext().getString(R.string.sign_rmb) + price;
        if (titleID == R.string.order_detail_cost_coupon) {
            priceText = "- " + priceText;
        } else if (titleID == R.string.order_detail_cost_realpay) {
            priceTV.setTextColor(0xFFFE6732);
        }
        priceTV.setText(priceText);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, UIUtils.dip2px(35));
        params.setMargins(UIUtils.dip2px(10), 0,UIUtils.dip2px(10), 0);
        groupLayout.addView(itemView, params);
    }
}