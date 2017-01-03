package com.hugboga.custom.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import com.huangbaoche.hbcframe.data.net.ErrorHandler;
import com.huangbaoche.hbcframe.data.net.ExceptionInfo;
import com.huangbaoche.hbcframe.data.net.HttpRequestListener;
import com.huangbaoche.hbcframe.data.net.HttpRequestUtils;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.hugboga.custom.activity.OrderDetailActivity;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.bean.CanServiceGuideBean;
import com.hugboga.custom.data.bean.ChooseGuideMessageBean;
import com.hugboga.custom.data.event.EventAction;
import com.hugboga.custom.data.event.EventType;
import com.hugboga.custom.data.request.RequestChooseGuide;
import com.hugboga.custom.data.request.RequestGuideCropValid;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by qingcha on 16/12/23.
 */

public class ChooseGuideUtils implements HttpRequestListener {

    private Activity mActivity;
    private String orderNo;
    private String source;
    private CanServiceGuideBean.GuidesBean selectedBean;
    private ErrorHandler errorHandler;

    public ChooseGuideUtils(Activity activity, String orderNo, String source) {
        this.mActivity = activity;
        this.orderNo = orderNo;
        this.source = source;
    }

    public void chooseGuide(CanServiceGuideBean.GuidesBean bean) {
        if (bean == null) {
            return;
        }
        this.selectedBean = bean;
        RequestGuideCropValid requestGuideCropValid = new RequestGuideCropValid(mActivity, bean.getGuideId(), bean.getCityId(), bean.getAllocatGno(), orderNo);
        HttpRequestUtils.request(mActivity, requestGuideCropValid, this);
    }

    @Override
    public void onDataRequestSucceed(BaseRequest request) {
        ApiReportHelper.getInstance().addReport(request);
        if (request instanceof RequestGuideCropValid) {
            if (selectedBean == null) {
                return;
            }
            ChooseGuideMessageBean messageBean = ((RequestGuideCropValid)request).getData();
            if (messageBean.isSucceed()) {
                showDialogSucceed();
            } else if (messageBean.result == 0) {
                showDialogError(messageBean.message);
            } else {
                showDialogIntentDetail(messageBean.message);
            }
        } else if (request instanceof RequestChooseGuide) {
            ChooseGuideMessageBean messageBean = ((RequestChooseGuide)request).getData();
            if (messageBean.isSucceed()) {
                intentOrderDetail();
            } else if (messageBean.result == 0) {
                showDialogError(messageBean.message);
            } else {
                showDialogIntentDetail(messageBean.message);
            }
        }
    }

    @Override
    public void onDataRequestCancel(BaseRequest request) {

    }

    @Override
    public void onDataRequestError(ExceptionInfo errorInfo, BaseRequest request) {
        if (errorHandler == null) {
            errorHandler = new ErrorHandler(mActivity, this);
        }
        errorHandler.onDataRequestError(errorInfo, request);
    }

    public void showDialogSucceed() {
        AlertDialogUtils.showAlertDialog(mActivity, String.format("确定选%1$s为您服务吗", selectedBean.getGuideName()), "就选" + (selectedBean.getGender() == 1 ? "他" : "她"), "我再想想", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                RequestChooseGuide requestChooseGuide = new RequestChooseGuide(mActivity, selectedBean.getAllocatGno(), orderNo, selectedBean.getGuideId());
                HttpRequestUtils.request(mActivity, requestChooseGuide, ChooseGuideUtils.this);
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public void showDialogError(String content) {
        AlertDialogUtils.showAlertDialogOneBtn(mActivity, content, "我知道了");
    }

    public void showDialogIntentDetail(String content) {
        AlertDialogUtils.showAlertDialog(mActivity, content, "回订单详情", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                intentOrderDetail();
            }
        });
    }

    public void intentOrderDetail() {
        EventBus.getDefault().post(new EventAction(EventType.ORDER_DETAIL_UPDATE, orderNo));
        OrderDetailActivity.Params orderParams = new OrderDetailActivity.Params();
        orderParams.orderId = orderNo;
        Intent intent = new Intent(mActivity, OrderDetailActivity.class);
        intent.putExtra(Constants.PARAMS_DATA, orderParams);
        intent.putExtra(Constants.PARAMS_SOURCE, source);
        mActivity.startActivity(intent);
    }
}