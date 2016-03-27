package com.hugboga.custom.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.huangbaoche.hbcframe.adapter.ZBaseAdapter;
import com.huangbaoche.hbcframe.util.MLog;
import com.huangbaoche.hbcframe.viewholder.ZBaseViewHolder;
import com.hugboga.custom.R;
import com.hugboga.custom.adapter.viewholder.NewOrderVH;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.bean.OrderBean;
import com.hugboga.custom.fragment.FgTravel;
import com.hugboga.custom.utils.DateUtils;
import com.hugboga.custom.widget.DialogUtil;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.text.ParseException;

/**
 * 聊天历史订单
 * Created by ZHZEPHI on 2015/11/7 11:47.
 */
public class HistoryOrderAdapter extends ZBaseAdapter<OrderBean, NewOrderVH> {

    private final ImageOptions options;
    DialogUtil dialog;

    public HistoryOrderAdapter(Context context) {
        super(context);
        dialog = DialogUtil.getInstance((Activity) context);
        options = new ImageOptions.Builder()
                .setFailureDrawableId(R.mipmap.chat_head)
                .setLoadingDrawableId(R.mipmap.chat_head)
                .setCircular(true)
                .build();
    }

    @Override
    protected int initResource() {
        return R.layout.order_history_item;
    }

    @Override
    protected ZBaseViewHolder getVH(View view) {
        return new NewOrderVH(view);
    }

    @Override
    protected void getView(int position, NewOrderVH vh) {
        OrderBean orderBean = datas.get(position);
        //车辆类型
        vh.mCarType.setText(orderBean.carDesc);
        //当地城市时间
        vh.mServiceTimeLocl.setVisibility(View.VISIBLE);
        vh.mServiceTimeLocl.setText("(" + orderBean.serviceCityName + "时间)");
        //订单状态
        vh.mStatus.setText(orderBean.orderStatus.name);
        vh.mCommendStartAddress.setVisibility(View.GONE);
        switch (orderBean.orderType) {
            case Constants.BUSINESS_TYPE_PICK:
                //接机
                vh.mLineView.setBackgroundResource(R.drawable.jour_blue_ttt);
                vh.mTypeStr.setText(R.string.title_pick);
                vh.mDays.setVisibility(View.GONE);
                vh.mFrom.setVisibility(View.VISIBLE);
                vh.mFrom.setText(orderBean.startAddress);
                vh.mTo.setVisibility(View.VISIBLE);
                vh.mTo.setText(orderBean.destAddress);
                vh.mCitys.setVisibility(View.GONE);
                try {
                    vh.mServiceTime.setVisibility(View.VISIBLE);
                    vh.mServiceTime.setText(DateUtils.getWeekStrFromDate(orderBean.serviceTime));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.BUSINESS_TYPE_SEND:
                //送机
                vh.mLineView.setBackgroundResource(R.drawable.jour_green_ttt);
                vh.mTypeStr.setText(R.string.title_send);
                vh.mDays.setVisibility(View.GONE);
                vh.mFrom.setVisibility(View.VISIBLE);
                vh.mFrom.setText(orderBean.startAddress);
                vh.mTo.setVisibility(View.VISIBLE);
                vh.mTo.setText(orderBean.destAddress);
                vh.mCitys.setVisibility(View.GONE);
                try {
                    vh.mServiceTime.setVisibility(View.VISIBLE);
                    vh.mServiceTime.setText(DateUtils.getWeekStrFromDate(orderBean.serviceTime));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.BUSINESS_TYPE_DAILY:
                //日租
                vh.mLineView.setBackgroundResource(R.drawable.jour_yellow_ttt);
                vh.mTypeStr.setText(R.string.title_daily);

                vh.mServiceTime.setVisibility(View.VISIBLE);
                if (orderBean.isHalfDaily == 1) {//半日包
                    vh.mDays.setVisibility(View.GONE);
                    vh.mServiceTime.setText(orderBean.serviceTime + "(半日包)");
                } else {
                    vh.mDays.setVisibility(View.VISIBLE);
                    vh.mDays.setText((orderBean.totalDays) + "天");
                    vh.mServiceTime.setText(orderBean.serviceTime + " 至 " + orderBean.serviceEndTime);
                }
                if (orderBean.orderGoodsType == Constants.BUSINESS_TYPE_DAILY) {
                    vh.mTypeStr.setText("市内包车");
                } else {
                    vh.mTypeStr.setText("跨城市包车");
                }
                vh.mFrom.setVisibility(View.GONE);
                vh.mTo.setVisibility(View.GONE);
                vh.mCitys.setVisibility(View.VISIBLE);
                String dailyPlace = orderBean.serviceCityName;
                if (!TextUtils.isEmpty(orderBean.serviceEndCityName)) {
                    dailyPlace += " - " + orderBean.serviceEndCityName;
                }
                vh.mCitys.setText(dailyPlace);
                break;
            case Constants.BUSINESS_TYPE_RENT:
                //次租
                vh.mLineView.setBackgroundResource(R.drawable.jour_red_ttt);
                vh.mTypeStr.setText(R.string.title_rent);
                vh.mDays.setVisibility(View.GONE);
                vh.mFrom.setVisibility(View.VISIBLE);
                vh.mFrom.setText(orderBean.startAddress);
                vh.mTo.setVisibility(View.VISIBLE);
                vh.mTo.setText(orderBean.destAddress);
                vh.mCitys.setVisibility(View.GONE);
                try {
                    vh.mServiceTime.setVisibility(View.VISIBLE);
                    vh.mServiceTime.setText(DateUtils.getWeekStrFromDate(orderBean.serviceTime));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.BUSINESS_TYPE_COMMEND:
                //线路包车
                vh.mLineView.setBackgroundResource(R.drawable.jour_purple_ttt);
                vh.mTypeStr.setText(R.string.title_commend);
                vh.mDays.setVisibility(View.GONE);
                vh.mServiceTime.setVisibility(View.VISIBLE);
                vh.mServiceTime.setText(splitDateStr(orderBean.serviceTime) + " 至 " + splitDateStr(splitDateStr(orderBean.serviceEndTime)));
                vh.mCommendStartAddress.setText("(出发地：" + orderBean.serviceCityName + ")");
                vh.mCommendStartAddress.setVisibility(View.VISIBLE);
                vh.mFrom.setVisibility(View.GONE);
                vh.mTo.setVisibility(View.GONE);
                vh.mServiceTimeLocl.setVisibility(View.GONE);
                vh.mCitys.setVisibility(View.VISIBLE);
                vh.mCitys.setText(orderBean.lineSubject);
                break;
        }
        vh.mBtnChat.setVisibility(View.GONE);
        setStatusView(vh, orderBean);
    }

    /**
     * 根据状态重置操作栏
     *
     * @param vh
     * @param orderBean
     */
    private void setStatusView(NewOrderVH vh, OrderBean orderBean) {
        vh.mAssessment.setOnClickListener(null);
        switch (orderBean.orderStatus) {
            case INITSTATE:
                //等待支付 初始状态
                vh.mStatusLayout.setVisibility(View.VISIBLE);
                vh.mPrice.setVisibility(View.VISIBLE);
                vh.mPrice.setText("应付金额：" + orderBean.orderPriceInfo.shouldPay + "元");
                vh.mHeadLayout.setVisibility(View.GONE);
                vh.mBtnPay.setVisibility(View.VISIBLE);
                vh.mBtnPay.setTag(orderBean);
                vh.mBtnPay.setOnClickListener(new TravelOnClickListener(orderBean));
                vh.mBtnChat.setVisibility(View.GONE);
//                vh.mBtnCall.setVisibility(View.GONE);
                vh.mAssessment.setVisibility(View.GONE);
                vh.mStatus.setTextColor(Color.parseColor("#F3AD5B"));
                break;
            case PAYSUCCESS:
                //预订成功
                vh.mStatusLayout.setVisibility(View.GONE);
                vh.mStatus.setTextColor(Color.parseColor("#F3AD5B"));
                break;
            case AGREE:
                //导游已接单
                vh.mStatusLayout.setVisibility(View.VISIBLE);
                vh.mPrice.setVisibility(View.GONE);
                vh.mHeadLayout.setVisibility(View.VISIBLE);
                if (orderBean.orderGuideInfo != null) {
                    vh.mHeadTitle.setText(orderBean.orderGuideInfo.guideName);
                    x.image().bind(vh.mHeadImg, orderBean.orderGuideInfo.guideAvatar, options);
                }
                vh.mBtnChat.setVisibility(View.VISIBLE);
                vh.mBtnPay.setVisibility(View.GONE);
                vh.mAssessment.setVisibility(View.GONE);
                vh.mStatus.setTextColor(Color.parseColor("#F3AD5B"));
                break;
            case ARRIVED:
                //导游已到达
                vh.mStatusLayout.setVisibility(View.VISIBLE);
                vh.mPrice.setVisibility(View.GONE);
                vh.mHeadLayout.setVisibility(View.VISIBLE);
                vh.mHeadTitle.setText(orderBean.orderGuideInfo.guideName);
                x.image().bind(vh.mHeadImg, orderBean.orderGuideInfo.guideAvatar, options);
                vh.mBtnPay.setVisibility(View.GONE);
                vh.mBtnChat.setVisibility(View.VISIBLE);
                vh.mAssessment.setVisibility(View.GONE);
                vh.mStatus.setTextColor(Color.parseColor("#F3AD5B"));
                break;
            case SERVICING:
                //您已上车
                vh.mStatusLayout.setVisibility(View.VISIBLE);
                vh.mPrice.setVisibility(View.GONE);
                vh.mHeadLayout.setVisibility(View.VISIBLE);
                if (orderBean.orderGuideInfo != null) {
                    vh.mHeadTitle.setText(orderBean.orderGuideInfo.guideName);
                    x.image().bind(vh.mHeadImg, orderBean.orderGuideInfo.guideAvatar, options);
                }
                vh.mBtnChat.setVisibility(View.VISIBLE);
                vh.mBtnPay.setVisibility(View.GONE);
                vh.mAssessment.setVisibility(View.GONE);
                vh.mStatus.setTextColor(Color.parseColor("#F3AD5B"));
                break;
            case NOT_EVALUATED:
                //是否可以评价
                vh.mAssessment.setVisibility(View.VISIBLE);
                MLog.e("onclick " + orderBean.orderNo + " orderType = " + orderBean.orderType);
                MLog.e("onclick=" + vh.mAssessment.hasOnClickListeners());
                vh.mAssessment.setOnClickListener(new TravelOnClickListener(orderBean));
                vh.mStatusLayout.setVisibility(View.VISIBLE);
                vh.mPrice.setVisibility(View.GONE);
                vh.mHeadLayout.setVisibility(View.VISIBLE);
                vh.mHeadTitle.setText(orderBean.orderGuideInfo.guideName);
                x.image().bind(vh.mHeadImg, orderBean.orderGuideInfo.guideAvatar, options);
                vh.mBtnPay.setVisibility(View.GONE);
                vh.mBtnChat.setVisibility(View.VISIBLE);
                vh.mStatus.setTextColor(Color.parseColor("#F3AD5B"));
                break;
            case COMPLAINT:
                //客诉处理中
                vh.mStatusLayout.setVisibility(View.GONE);
                vh.mStatus.setTextColor(Color.parseColor("#B3B3B3"));
                break;
            case COMPLETE:
                //已完成
                vh.mAssessment.setVisibility(View.GONE);
                vh.mStatusLayout.setVisibility(View.VISIBLE);
                vh.mHeadLayout.setVisibility(View.VISIBLE);
                vh.mBtnChat.setVisibility(View.VISIBLE);
                vh.mStatus.setTextColor(Color.parseColor("#BDBDBD"));
                vh.mHeadTitle.setText(orderBean.orderGuideInfo.guideName);
                x.image().bind(vh.mHeadImg, orderBean.orderGuideInfo.guideAvatar, options);
                break;
            case CANCELLED:
            case REFUNDED:
                //已取消
                vh.mStatusLayout.setVisibility(View.GONE);
                vh.mStatus.setTextColor(Color.parseColor("#B3B3B3"));
                break;
            default:
                break;
        }
        //显示未读小红点个数
        showMessageNum(vh.mBtnChatNum, orderBean.imcount);
    }

    /**
     * 设置聊一聊未读个数小红点
     * @param chatNumTextView
     */
    private void showMessageNum(final TextView chatNumTextView, Integer imcount){
        if(imcount>0){
            chatNumTextView.setVisibility(View.VISIBLE);
            chatNumTextView.setText(String.valueOf(imcount));
        }else{
            chatNumTextView.setVisibility(View.GONE);
        }
    }

    private String splitDateStr(String dateStr) {
        if (dateStr == null) return null;
        String[] dateArray = dateStr.split(" ");
        if (dateArray != null && dateArray.length > 1) return dateArray[0];
        return dateStr;
    }

    class TravelOnClickListener implements View.OnClickListener {

        private final OrderBean mOrderBean;

        public TravelOnClickListener(OrderBean orderBean) {
            this.mOrderBean = orderBean;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.travel_item_btn_assessment:
                    MLog.e("评价车导2 " + mOrderBean.orderNo + " orderType = " + mOrderBean.orderType);
                    Bundle bundle = new Bundle();
                   /* bundle.putString(FgAssessment.GUIDE_ID, mOrderBean.orderGuideInfo.guideID);
                    bundle.putString(FgAssessment.ORDER_ID, mOrderBean.orderNo);
                    bundle.putInt(FgAssessment.ORDER_TYPE, mOrderBean.orderType);
                    bundle.putInt(BaseFragment.KEY_BUSINESS_TYPE, mOrderBean.orderType);
                    bundle.putString(FgAssessment.GUIDE_NAME, mOrderBean.orderGuideInfo.guideName);
                    fragment.startFragment(new FgAssessment(), bundle);*/
                    break;
                case R.id.travel_item_btn_pay:
                    OrderBean mOrderBean = (OrderBean) v.getTag();
                    MLog.e("立即支付 " + mOrderBean.orderNo);
                    //立即支付，进入订单详情
                    bundle = new Bundle();
                    bundle.putInt(FgTravel.KEY_BUSINESS_TYPE, mOrderBean.orderType);
                   /* bundle.putString(FgOrder.KEY_ORDER_ID, mOrderBean.orderNo);
                    fragment.startFragment(new FgOrder(), bundle);*/
                    break;
            }
        }
    }
}
