package com.hugboga.custom.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huangbaoche.hbcframe.data.net.ErrorHandler;
import com.huangbaoche.hbcframe.data.net.ExceptionInfo;
import com.huangbaoche.hbcframe.data.net.HttpRequestListener;
import com.huangbaoche.hbcframe.data.net.HttpRequestUtils;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.hugboga.custom.MainActivity;
import com.hugboga.custom.R;
import com.hugboga.custom.activity.InsureActivity;
import com.hugboga.custom.activity.OrderDetailActivity;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.bean.PaySucceedBean;
import com.hugboga.custom.data.bean.PickupCouponOpenBean;
import com.hugboga.custom.data.event.EventAction;
import com.hugboga.custom.data.event.EventType;
import com.hugboga.custom.data.request.RequestPaySucceed;
import com.hugboga.custom.data.request.RequestPickupCouponOpen;
import com.hugboga.custom.statistic.event.EventUtil;
import com.hugboga.custom.utils.ApiReportHelper;
import com.hugboga.custom.utils.OrderUtils;
import com.hugboga.custom.utils.PhoneInfo;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by qingcha on 16/9/5.
 */
public class PayResultView extends RelativeLayout implements HttpRequestListener {

    @Bind(R.id.view_pay_result_state_iv)
    ImageView stateIV;
    @Bind(R.id.view_pay_result_state_tv)
    TextView stateTV;
    @Bind(R.id.view_pay_result_order_tv)
    TextView orderTV;
    @Bind(R.id.view_pay_result_service_layout)
    LinearLayout serviceLayout;
    @Bind(R.id.view_pay_result_desc_layout)
    LinearLayout descLayout;
    @Bind(R.id.view_pay_result_recommend_layout)
    PayResultRecommendLayout recommendView;
    @Bind(R.id.view_pay_result_bargain_layout)
    PayResultBargainLayout bargainLayout;
    @Bind(R.id.view_pay_result_bargain_bottom_space)
    View bargainBottomSpace;

    private boolean isPaySucceed; //支付结果
    private String orderId;
    private int orderType;
    private PaySucceedBean paySucceedBean;
    private ErrorHandler errorHandler;

    public PayResultView(Context context) {
        this(context, null);
    }

    public PayResultView(Context context, AttributeSet attrs) {
        super(context, attrs);
        final View view = inflate(context, R.layout.view_pay_result, this);
        ButterKnife.bind(view);
    }

    @OnClick({R.id.view_pay_result_order_tv
            , R.id.view_pay_result_domestic_service_layout
            , R.id.view_pay_result_overseas_service_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.view_pay_result_order_tv:
                if (isPaySucceed) {
                    intentOrderDetail();
                } else {
                    setStatisticIsRePay(true);
                    ((Activity) getContext()).finish();
                }
                break;
            case R.id.view_pay_result_domestic_service_layout:
                PhoneInfo.CallDial(getContext(), Constants.CALL_NUMBER_IN);
                break;
            case R.id.view_pay_result_overseas_service_layout:
                PhoneInfo.CallDial(getContext(), Constants.CALL_NUMBER_OUT);
                break;
        }
    }

    public void initView(boolean _isPaySucceed, String _orderId, int orderType) {
        this.isPaySucceed = _isPaySucceed;
        this.orderId = _orderId;
        this.orderType = orderType;

        EventBus.getDefault().post(new EventAction(EventType.PAY_RESULT, isPaySucceed));

        if (isPaySucceed) {
            stateIV.setBackgroundResource(R.mipmap.pay_succeed_icon);
            stateTV.setVisibility(GONE);
            orderTV.setText("查看订单");
            RequestPaySucceed request = new RequestPaySucceed(getContext(), orderId);
            HttpRequestUtils.request(getContext(), request, this);
            if (orderType == 1) {
                HttpRequestUtils.request(getContext(), new RequestPickupCouponOpen(getContext()), this, false);
            }
            bargainLayout.setParams(orderId, orderType);
        } else {
            stateIV.setBackgroundResource(R.mipmap.pay_failure_icon);
            stateTV.setText("支付遇到了问题......");
            orderTV.setText("重新支付");
            serviceLayout.setVisibility(View.VISIBLE);
        }
    }

    public void intentOrderDetail() {
        intentHome();

        OrderDetailActivity.Params orderParams = new OrderDetailActivity.Params();
        orderParams.orderId = orderId;
        Intent intent = new Intent(getContext(), OrderDetailActivity.class);
        intent.putExtra(Constants.PARAMS_DATA, orderParams);
        intent.putExtra(Constants.PARAMS_SOURCE, getContext().getString(R.string.par_result_title));
        getContext().startActivity(intent);
    }

    public void intentHome() {
        getContext().startActivity(new Intent(getContext(), MainActivity.class));
        EventBus.getDefault().post(new EventAction(EventType.SET_MAIN_PAGE_INDEX, 0));
        if (isPaySucceed) {
            EventBus.getDefault().post(new EventAction(EventType.FGTRAVEL_UPDATE));
        }
        setStatisticIsRePay(false);
    }

    @Override
    public void onDataRequestSucceed(BaseRequest _request) {
        ApiReportHelper.getInstance().addReport(_request);
        if (_request instanceof RequestPaySucceed) {
            RequestPaySucceed request = (RequestPaySucceed) _request;
            paySucceedBean = request.getData();
            if (paySucceedBean == null) {
                return;
            }

            String stateStr = paySucceedBean.getStateStr();
            if (!TextUtils.isEmpty(stateStr)) {
                stateTV.setVisibility(View.VISIBLE);
                stateTV.setText(stateStr);
            }

            descLayout.setVisibility(View.VISIBLE);

            String descStr = paySucceedBean.getDescStr();
            if (!TextUtils.isEmpty(descStr)) {
                TextView descTV = (TextView) descLayout.findViewById(R.id.view_pay_result_desc_tv);
                descTV.setVisibility(View.VISIBLE);
                descTV.setText(descStr);
            }

            TextView insuranceTV = (TextView) descLayout.findViewById(R.id.view_pay_result_desc_insurance_tv);
            String insuranceStr = "添加投保信息获得免费保险";
            String insuranceFocusStr = "添加投保信息";
            if (getContext() instanceof Activity) {
                insuranceTV.setVisibility(View.VISIBLE);
                OrderUtils.genCLickSpan((Activity) getContext(), insuranceTV, insuranceStr, insuranceFocusStr, null
                        , getContext().getResources().getColor(R.color.default_highlight_blue)
                        , new OrderUtils.MyCLickSpan.OnSpanClickListener() {
                            @Override
                            public void onSpanClick(View view) {
                                Bundle insureBundle = new Bundle();
                                insureBundle.putString(Constants.PARAMS_ID, orderId);
                                Intent intent = new Intent(getContext(), InsureActivity.class);
                                intent.putExtras(insureBundle);
                                getContext().startActivity(intent);
                            }
                        });
            } else {
                insuranceTV.setVisibility(View.GONE);
            }

            bargainLayout.setVisibility(paySucceedBean.getBargainStatus() ? View.VISIBLE : View.GONE);
            bargainBottomSpace.setVisibility(paySucceedBean.getBargainStatus() ? View.VISIBLE : View.GONE);

            recommendView.setRequestParams(paySucceedBean.getCityName(), "" + paySucceedBean.getCityId(), paySucceedBean.getGoodsNo(), orderType);
        } else if (_request instanceof RequestPickupCouponOpen) {
            PickupCouponOpenBean pickupCouponOpenBean = ((RequestPickupCouponOpen)_request).getData();
            if (pickupCouponOpenBean != null && pickupCouponOpenBean.isOpen) {
                PickupCouponDialog dialog = new PickupCouponDialog(getContext());
                dialog.show();
            }
        }
    }

    @Override
    public void onDataRequestCancel(BaseRequest request) {

    }

    @Override
    public void onDataRequestError(ExceptionInfo errorInfo, BaseRequest request) {
        if (errorHandler == null) {
            errorHandler = new ErrorHandler((Activity)getContext(), this);
        }
        errorHandler.onDataRequestError(errorInfo, request);
    }

    /**
     * 统计是否是重新支付
     * */
    public void setStatisticIsRePay(boolean isRePay) {
        EventUtil eventUtil = EventUtil.getInstance();
        eventUtil.isRePay = isRePay;
    }
}
