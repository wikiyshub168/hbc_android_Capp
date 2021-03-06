package com.hugboga.custom.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.huangbaoche.hbcframe.data.net.DefaultSSLSocketFactory;
import com.huangbaoche.hbcframe.data.net.ExceptionErrorCode;
import com.huangbaoche.hbcframe.data.net.ExceptionInfo;
import com.huangbaoche.hbcframe.data.net.HttpRequestListener;
import com.huangbaoche.hbcframe.data.net.ServerException;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.huangbaoche.hbcframe.util.WXShareUtils;
import com.huangbaoche.imageselector.common.Constant;
import com.hugboga.custom.BuildConfig;
import com.hugboga.custom.MainActivity;
import com.hugboga.custom.R;
import com.hugboga.custom.alipay.PayResult;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.bean.AbroadCreditBean;
import com.hugboga.custom.data.bean.CreditCardInfoBean;
import com.hugboga.custom.data.bean.PayResultExtarParamsBean;
import com.hugboga.custom.data.bean.WXpayBean;
import com.hugboga.custom.data.event.EventAction;
import com.hugboga.custom.data.event.EventBusPayResult;
import com.hugboga.custom.data.event.EventType;
import com.hugboga.custom.data.request.RequestAbroadCreditPayment;
import com.hugboga.custom.data.request.RequestPayNo;
import com.hugboga.custom.statistic.MobClickUtils;
import com.hugboga.custom.statistic.bean.EventPayBean;
import com.hugboga.custom.statistic.event.EventPay;
import com.hugboga.custom.statistic.event.EventPayResult;
import com.hugboga.custom.statistic.event.EventPayShow;
import com.hugboga.custom.statistic.event.EventUtil;
import com.hugboga.custom.statistic.sensors.SensorsUtils;
import com.hugboga.custom.utils.AlertDialogUtils;
import com.hugboga.custom.utils.CommonUtils;
import com.hugboga.custom.utils.JsonUtils;
import com.hugboga.custom.utils.SharedPre;
import com.hugboga.custom.widget.DialogUtil;
import com.hugboga.custom.wxapi.WXPay;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by on 16/8/4.
 */
public class ChoosePaymentActivity extends BaseActivity implements HttpRequestListener {

    public static final String PAY_PARAMS = "pay_params";
    public static final String TAG = "ChoosePaymentActivity";

    @BindView(R.id.choose_payment_price_tv)
    TextView priceTV;
    @BindView(R.id.choose_payment_sign_tv)
    TextView choosePaymentSignTv;
    @BindView(R.id.choose_payment_alipay_iv)
    ImageView choosePaymentAlipayIv;
    @BindView(R.id.choose_payment_alipay_layout)
    RelativeLayout choosePaymentAlipayLayout;
    @BindView(R.id.choose_payment_wechat_iv)
    ImageView choosePaymentWechatIv;
    @BindView(R.id.choose_payment_wechat_layout)
    RelativeLayout choosePaymentWechatLayout;

    private DialogUtil mDialogUtil;
    private int payType;
    public RequestParams requestParams;
    public String creditType;
    CreditCardInfoBean cardInfoBean;
    private boolean isShowingAlipay = false;
    public static final String ORDERTYPE = "order_type";
    public static final String APITYPE = "api_type";

    public static class RequestParams implements Serializable {
        public String orderId;
        public double shouldPay;
        public String couponId;
        public String payDeadTime;
        public String source;
        public boolean needShowAlert;
        public EventPayBean eventPayBean;
        public int orderType;
        public int apiType;//0：正常  1：买券
        public boolean isOrder;//是否是在下单过程中进入
        public boolean isAliPay = true;
        public boolean isWechat = true;
        public PayResultExtarParamsBean extarParamsBean;

        public String getShouldPay() {
            return CommonUtils.doubleTrans(shouldPay);
        }
    }

    @Override
    public int getContentViewId() {
        return R.layout.fg_choose_payment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            requestParams = (RequestParams) savedInstanceState.getSerializable(Constants.PARAMS_DATA);
        } else {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                requestParams = (RequestParams) bundle.getSerializable(Constants.PARAMS_DATA);
            }
        }

        EventBus.getDefault().register(this);
        initDefaultTitleBar();

        initView();
        MobClickUtils.onEvent(new EventPayShow(requestParams.eventPayBean));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (requestParams != null) {
            outState.putSerializable(Constants.PARAMS_DATA, requestParams);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DefaultSSLSocketFactory.resetSSLSocketFactory(this);
        if (mDialogUtil != null) {
            mDialogUtil.dismissDialog();
        }
    }

    private void initView() {
        initDefaultTitleBar();
        fgTitle.setText(getString(R.string.choose_payment_title));
        RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        titleParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        fgTitle.setLayoutParams(titleParams);
        if (requestParams.isOrder) {
            fgLeftBtn.setVisibility(View.GONE);
            TextView rightTV = (TextView) findViewById(R.id.header_right_txt);
            rightTV.setText(R.string.par_result_detail);
            rightTV.setVisibility(View.VISIBLE);
            rightTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    backWarn();
                }
            });
        } else {
            fgLeftBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            fgRightTV.setVisibility(View.GONE);
        }
        priceTV.setText(requestParams.getShouldPay());
        mDialogUtil = DialogUtil.getInstance(this);

        if (!requestParams.isAliPay) {
            choosePaymentAlipayLayout.setVisibility(View.GONE);
        }
        if (!requestParams.isWechat) {
            choosePaymentWechatLayout.setVisibility(View.GONE);
        } else {
            // 将该app注册到微信
            IWXAPI msgApi = WXAPIFactory.createWXAPI(this, BuildConfig.WX_APP_ID);
            msgApi.registerApp(BuildConfig.WX_APP_ID);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mDialogUtil != null) {
            mDialogUtil.dismissDialog();
        }
    }

    @Subscribe
    public void onEventMainThread(EventBusPayResult action) {
        if (action == null || requestParams == null || !TextUtils.equals(action.orderNo, requestParams.orderId)) {
            return;
        }
        if (requestParams.eventPayBean != null) {
            requestParams.eventPayBean.paystyle = CommonUtils.selectPayStyle(this.payType);
            MobClickUtils.onEvent(new EventPayResult(requestParams.eventPayBean, action.payResult));
            setSensorsPayResultEvent(CommonUtils.selectPayStyle(this.payType), action.payResult);
        }
    }

    private void sendRequest(int payType) {
        if (requestParams.eventPayBean != null) {
            requestParams.eventPayBean.paystyle = CommonUtils.selectPayStyle(payType);
            MobClickUtils.onEvent(new EventPay(requestParams.eventPayBean));
            SensorsUtils.setSensorsPayOnClickEvent(requestParams.eventPayBean, requestParams.eventPayBean.paystyle);
        }
        if (payType == Constants.PAY_STATE_BANK) {
            return;
        }
        this.payType = payType;
        RequestPayNo request = new RequestPayNo(this, requestParams.orderId, requestParams.shouldPay, payType, requestParams.couponId, requestParams.apiType);
        requestData(request);
    }

    @OnClick({R.id.choose_payment_alipay_layout, R.id.choose_payment_wechat_layout, R.id.choose_payment_add_credit_card_layout, R.id.choose_payment_abrod_credit_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.choose_payment_alipay_layout://支付宝支付
                SensorsUtils.onAppClick(getEventSource(), "支付宝", getIntentSource());
                DefaultSSLSocketFactory.resetSSLSocketFactory(this);
                sendRequest(Constants.PAY_STATE_ALIPAY);
                break;
            case R.id.choose_payment_wechat_layout://微信支付
                if (isFinishing() || !WXShareUtils.getInstance(this).isInstallOf(this, true)) {
                    return;
                }
                SensorsUtils.onAppClick(getEventSource(), "微信", getIntentSource());
                DefaultSSLSocketFactory.resetSSLSocketFactory(this);
                sendRequest(Constants.PAY_STATE_WECHAT);
                break;
            case R.id.choose_payment_add_credit_card_layout:
                //国内信用卡支付
                SensorsUtils.onAppClick(getEventSource(), "国内信用卡", getIntentSource());
                if (requestParams != null) {
                    Intent intent = new Intent(this, DomesticCreditCardActivity.class);
                    intent.putExtra(PAY_PARAMS, requestParams);
                    startActivity(intent);
                }
                sendRequest(Constants.PAY_STATE_BANK);//仅仅只用于埋点
                break;
            case R.id.choose_payment_abrod_credit_layout:
                SensorsUtils.onAppClick(getEventSource(), "境外信用卡", getIntentSource());
                if (requestParams != null) {
                    RequestAbroadCreditPayment requestAbroadCreditPayment = new RequestAbroadCreditPayment(this, requestParams.shouldPay, requestParams.orderId, 2);
                    requestData(requestAbroadCreditPayment, true);
                    SharedPre.setInteger(ORDERTYPE, requestParams.orderType);
                    SharedPre.setInteger(APITYPE, requestParams.apiType);
                }
                break;
        }
    }

    @Override
    public void onDataRequestSucceed(BaseRequest request) {
        super.onDataRequestSucceed(request);
        if (request instanceof RequestPayNo) {
            RequestPayNo mParser = (RequestPayNo) request;
            if (mParser.payType == Constants.PAY_STATE_ALIPAY) {
                //支付宝使用旅游基金和优惠券0元支付
                if ("travelFundPay".equals(mParser.getData()) || "couppay".equals(mParser.getData())) {
                    mHandler.sendEmptyMessage(1);
                } else {
                    payByAlipay((String) mParser.getData());
                }
            } else if (mParser.payType == Constants.PAY_STATE_WECHAT) {
                WXpayBean bean = (WXpayBean) mParser.getData();
                if (bean != null) {
                    if (bean.travelFundPay) {//全部使用旅游基金支付的时候
                        mDialogUtil.showLoadingDialog();
                        mHandler.sendEmptyMessage(1);
                    } else if (bean.coupPay) {
                        mDialogUtil.showLoadingDialog();
                        mHandler.sendEmptyMessage(1);
                    } else {
                        SharedPre sharedPre = new SharedPre(ChoosePaymentActivity.this);
                        sharedPre.saveStringValue(SharedPre.PAY_WECHAT_ORDER_ID, requestParams.orderId);
                        sharedPre.saveIntValue(SharedPre.PAY_WECHAT_ORDER_TYPE, requestParams.orderType);
                        sharedPre.saveIntValue(SharedPre.PAY_WECHAT_APITYPE, requestParams.apiType);
                        if (requestParams.extarParamsBean != null) {
                            sharedPre.saveStringValue(SharedPre.PAY_EXTAR_PARAMS, JsonUtils.toJson(requestParams.extarParamsBean, PayResultExtarParamsBean.class));
                        } else if (sharedPre.contains(SharedPre.PAY_EXTAR_PARAMS)) {
                            sharedPre.removeKey(SharedPre.PAY_EXTAR_PARAMS);
                        }
                        WXPay.pay(this, bean);
                    }
                }
            }
        } else if (request instanceof RequestAbroadCreditPayment) {
            if (request.getData() != null) {
                AbroadCreditBean abroadCreditBean = (AbroadCreditBean) request.getData();
                if (abroadCreditBean.payurl != null) {
                    Intent intent = new Intent(this, WebInfoActivity.class);
                    intent.putExtra(WebInfoActivity.WEB_URL, abroadCreditBean.payurl);
                    startActivity(intent);
                }
            }

        }
    }

    @Override
    public void onDataRequestError(ExceptionInfo errorInfo, BaseRequest parser) {
        if (errorInfo.state == ExceptionErrorCode.ERROR_CODE_SERVER) {
            ServerException exception = (ServerException) errorInfo.exception;
            if (exception.getCode() == 2) {
                mDialogUtil.showLoadingDialog();
                mHandler.sendEmptyMessage(2);
                return;
            }
        }
        super.onDataRequestError(errorInfo, parser);
    }

    private void payByAlipay(final String payInfo) {
        if (!TextUtils.isEmpty(payInfo)) {
            if ("couppay".equals(payInfo)) {
                //优惠券0元支付
                mDialogUtil.showLoadingDialog();
                mHandler.sendEmptyMessage(1);
            } else {//正常支付
                isShowingAlipay = true;
                Runnable payRunnable = new Runnable() {
                    @Override
                    public void run() {
                        // 构造PayTask 对象
                        PayTask alipay = new PayTask(ChoosePaymentActivity.this);
                        // 调用支付接口，获取支付结果
                        String result = alipay.pay(payInfo, true);
                        Message msg = new Message();
                        msg.what = PayResult.SDK_PAY_FLAG;
                        msg.obj = result;
                        mAlipayHandler.sendMessage(msg);
                    }
                };
                // 必须异步调用
                Thread payThread = new Thread(payRunnable);
                payThread.start();
            }
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mDialogUtil.dismissLoadingDialog();
            PayResultActivity.Params params = new PayResultActivity.Params();
            params.payResult = msg.what == 1;//1.支付成功，2.支付失败
            params.orderId = requestParams.orderId;
            params.orderType = requestParams.orderType;
            params.apiType = requestParams.apiType;
            params.extarParamsBean = requestParams.extarParamsBean;

            //为了兼容信用卡支付
            SharedPre sharedPre = new SharedPre(ChoosePaymentActivity.this);
            if (requestParams.extarParamsBean != null) {
                sharedPre.saveStringValue(SharedPre.PAY_EXTAR_PARAMS, JsonUtils.toJson(requestParams.extarParamsBean, PayResultExtarParamsBean.class));
            } else if (sharedPre.contains(SharedPre.PAY_EXTAR_PARAMS)) {
                sharedPre.removeKey(SharedPre.PAY_EXTAR_PARAMS);
            }
            Intent intent = new Intent(ChoosePaymentActivity.this, PayResultActivity.class);
            intent.putExtra(Constants.PARAMS_DATA, params);
            intent.putExtra(Constants.PARAMS_SOURCE, getEventSource());
            ChoosePaymentActivity.this.startActivity(intent);
        }
    };


    //支付宝回调
    private Handler mAlipayHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PayResult.SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    String resultInfo = payResult.getResult();
                    String resultStatus = payResult.getResultStatus();
                    if (TextUtils.equals(resultStatus, "9000")) {//支付成功
                        mDialogUtil.showLoadingDialog();
                        mHandler.sendEmptyMessageDelayed(1, 3000);
                    } else if (TextUtils.equals(resultStatus, "8000")) {
                        CommonUtils.showToast("支付结果确认中");
                    } else {//支付失败
                        mHandler.sendEmptyMessage(2);
                    }
                    isShowingAlipay = false;
                    break;
                }
                case PayResult.SDK_CHECK_FLAG: {
                    CommonUtils.showToast("检查结果为：" + msg.obj);
                    break;
                }
                default:
                    break;
            }
        }
    };

    private void backWarn() {
        if (isFinishing()) {
            return;
        }
        AlertDialogUtils.showAlertDialog(ChoosePaymentActivity.this
                , "放弃付款？"
                , getString(R.string.order_cancel_pay, requestParams.payDeadTime)
                , "继续支付", "确定离开"
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EventUtil eventUtil = EventUtil.getInstance();
                        eventUtil.isRePay = false;
                        Intent intent = null;

                        intent = new Intent(ChoosePaymentActivity.this, MainActivity.class);
                        startActivity(intent);
                        EventBus.getDefault().post(new EventAction(EventType.SET_MAIN_PAGE_INDEX, 3));
                        EventBus.getDefault().post(new EventAction(EventType.TRAVEL_LIST_TYPE, 1));

                        OrderDetailActivity.Params orderParams = new OrderDetailActivity.Params();
                        orderParams.orderId = requestParams.orderId;
                        intent = new Intent(ChoosePaymentActivity.this, OrderDetailActivity.class);
                        intent.putExtra(Constants.PARAMS_DATA, orderParams);
                        intent.putExtra(Constants.PARAMS_SOURCE, ChoosePaymentActivity.this.getEventSource());
                        ChoosePaymentActivity.this.startActivity(intent);
                    }
                });
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            try {
                if (!isShowingAlipay && requestParams.isOrder) {
                    backWarn();
                } else {
                    return super.onKeyUp(keyCode, event);
                }
            } catch (Exception e) {

            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void setSensorsPayResultEvent(String payMethod, boolean payResult) {
        if (requestParams == null || requestParams.eventPayBean == null) {
            return;
        }
        SensorsUtils.setSensorsPayResultEvent(requestParams.eventPayBean, payMethod, payResult);
    }

    @Override
    public String getEventSource() {
        return "收银台";
    }
}
