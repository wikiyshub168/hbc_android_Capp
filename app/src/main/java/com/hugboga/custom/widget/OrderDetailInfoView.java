package com.hugboga.custom.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hugboga.custom.R;
import com.hugboga.custom.data.bean.OrderBean;
import com.hugboga.custom.data.bean.OrderStatus;
import com.hugboga.custom.data.event.EventAction;
import com.hugboga.custom.data.event.EventType;

import de.greenrobot.event.EventBus;

/**
 * Created by qingcha on 16/6/2.
 */
public class OrderDetailInfoView extends LinearLayout implements HbcViewBehavior, View.OnClickListener{

    private RelativeLayout insuranceInfoLayout;
    private RelativeLayout insuranceGetLayout;
    private TextView nameTV;
    private TextView insurerTV;

    public OrderDetailInfoView(Context context) {
        this(context, null);
    }

    public OrderDetailInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_order_detail_info, this);

        findViewById(R.id.order_detail_info_layout).setOnClickListener(this);
        nameTV = (TextView) findViewById(R.id.order_detail_info_name_tv);

        insuranceInfoLayout = (RelativeLayout) findViewById(R.id.order_detail_insurance_info_layout);
        insurerTV = (TextView) findViewById(R.id.order_detail_insurer_tv);

        insuranceGetLayout = (RelativeLayout) findViewById(R.id.order_detail_insurance_get_layout);
    }

    @Override
    public void update(Object _data) {
        if (_data == null) {
            return;
        }
        final OrderBean orderBean = (OrderBean) _data;
        nameTV.setText(orderBean.userName);
        if (orderBean.insuranceEnable && orderBean.orderStatus == OrderStatus.INITSTATE) { //是否添加保险
            insuranceInfoLayout.setVisibility(View.GONE);
            insuranceGetLayout.setVisibility(View.VISIBLE);
            insuranceGetLayout.setOnClickListener(this);
        } else if (orderBean.insuranceList != null && orderBean.insuranceList.size() > 0) {
            insuranceInfoLayout.setVisibility(View.VISIBLE);
            insuranceInfoLayout.setOnClickListener(this);
            insuranceGetLayout.setVisibility(View.GONE);
            insurerTV.setText(getContext().getString(R.string.order_detail_info, orderBean.insuranceList.size()));
        } else {
            insuranceInfoLayout.setVisibility(View.GONE);
            insuranceGetLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.order_detail_info_layout://出行人信息
                break;
            case R.id.order_detail_insurance_info_layout://投保人信息
                break;
            case R.id.order_detail_insurance_get_layout://添加投保人
                EventBus.getDefault().post(new EventAction(EventType.ORDER_DETAIL_ADD_INSURER));
                break;
        }
    }
}