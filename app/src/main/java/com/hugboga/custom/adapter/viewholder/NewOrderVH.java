package com.hugboga.custom.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huangbaoche.hbcframe.viewholder.ZBaseViewHolder;
import com.hugboga.custom.R;

import net.grobas.view.PolygonImageView;

import org.xutils.view.annotation.ViewInject;

/**
 * 订单列表VH
 * Created by ZHZEPHI on 2015/11/7 11:44.
 */
public class NewOrderVH extends ZBaseViewHolder {

    @ViewInject(R.id.travel_item_typestr)
    public TextView mTypeStr; //订单类型
    @ViewInject(R.id.travel_item_cartype)
    public TextView mCarType; //车辆类型
    @ViewInject(R.id.order_item_time_tv)
    public TextView timeTV;
    @ViewInject(R.id.travel_item_citys)
    public TextView citysTV;
    @ViewInject(R.id.order_item_time_local_tv)
    public TextView timeLocalTV;
    @ViewInject(R.id.order_item_start_address_tv)
    public TextView startAddressTV;
    @ViewInject(R.id.order_item_end_address_tv)
    public TextView endAddressTV;
    @ViewInject(R.id.order_item_start_address_iv)
    public ImageView startAddressIV;
    @ViewInject(R.id.order_item_end_address_iv)
    public ImageView endAddressIV;
    @ViewInject(R.id.order_item_start_address_layout)
    public LinearLayout startAddressLayout;
    @ViewInject(R.id.order_item_end_address_layout)
    public LinearLayout endAddressLayout;
    @ViewInject(R.id.order_list_line)
    public View lineView;
    @ViewInject(R.id.order_list_vertical_line)
    public View verticalLine;
    @ViewInject(R.id.travel_item_status)
    public TextView mStatus; //订单状态
    @ViewInject(R.id.travel_item_status_layout)
    public RelativeLayout mStatusLayout; //状态行为操作栏
    @ViewInject(R.id.travel_item_price)
    public TextView mPrice; //支付费用
    @ViewInject(R.id.travel_item_head_layout)
    public LinearLayout mHeadLayout; //导游信息
    @ViewInject(R.id.travel_item_head_img)
    public PolygonImageView mHeadImg;//导游头像
    @ViewInject(R.id.travel_item_head_title)
    public TextView mHeadTitle; //导游名称
    @ViewInject(R.id.travel_item_btn_pay)
    public TextView mBtnPay; //立即支付
    @ViewInject(R.id.travel_item_btn_chat)
    public ImageView mBtnChat; //聊一聊按钮
    @ViewInject(R.id.travel_item_btn_chat_num)
    public TextView mBtnChatNum; //未读消息个数
    @ViewInject(R.id.travel_item_btn_assessment)
    public TextView mAssessment; //评价车导
    @ViewInject(R.id.br_layout)
    public LinearLayout br_layout;
    @ViewInject(R.id.travel_item_btn_br)
    public TextView travel_item_btn_br;;
    @ViewInject(R.id.travel_item_btn_br_tips)
    public ImageView travel_item_btn_br_tips;

    public NewOrderVH(View itemView) {
        super(itemView);
    }
}
