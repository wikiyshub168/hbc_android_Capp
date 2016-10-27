package com.hugboga.custom.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.huangbaoche.hbcframe.util.WXShareUtils;
import com.hugboga.custom.R;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.bean.TravelFundData;
import com.hugboga.custom.data.bean.UserEntity;
import com.hugboga.custom.data.event.EventAction;
import com.hugboga.custom.data.net.ShareUrls;
import com.hugboga.custom.data.net.UrlLibs;
import com.hugboga.custom.data.request.RequestGetInvitationCode;
import com.hugboga.custom.data.request.RequestTravelFundLogs;
import com.hugboga.custom.statistic.MobClickUtils;
import com.hugboga.custom.statistic.StatisticConstant;
import com.hugboga.custom.statistic.event.EventUtil;
import com.hugboga.custom.utils.CommonUtils;
import com.hugboga.custom.widget.ShareDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created on 16/8/4.TravelFund
 */
public class TravelFundActivity extends BaseActivity {

    @Bind(R.id.travel_fund_residue_price_tv)
    TextView residuePriceTV;
    @Bind(R.id.travel_fund_validity_tv)
    TextView validityTV;
    @Bind(R.id.travel_fund_coupon_price_tv)
    TextView couponPriceTV;
    @Bind(R.id.travel_fund_description_tv)
    TextView descriptionTV;

    @Bind(R.id.tracel_fund_header)
    RelativeLayout titlerBar;

    private String invitationCode;
    private TravelFundData travelFundData;

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_travel_fund);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEventMainThread(EventAction action) {
        switch (action.getType()) {
            case WECHAT_SHARE_SUCCEED:
                WXShareUtils wxShareUtils = WXShareUtils.getInstance(this);
                if (getClass().getSimpleName().equals(wxShareUtils.source)) {//分享成功
                    EventUtil.onShareDefaultEvent(StatisticConstant.SHAREFOUND_BACK, "" + wxShareUtils.type);
                }
                break;
        }
    }

    private void initView() {
        initTitleBar();
        sendRequest();
        setCouponPrice("--");
        setActivityPrice("--", "--");
    }

    private void initTitleBar() {
        initDefaultTitleBar();
        fgTitle.setText(getString(R.string.travel_fund_title));
        fgRightBtn.setVisibility(View.VISIBLE);
        fgRightBtn.setText(getString(R.string.travel_fund_rule));
        fgRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobClickUtils.onEvent(StatisticConstant.LAUNCH_FOUNDREGUL);
                Intent intent = new Intent(TravelFundActivity.this, WebInfoActivity.class);
                intent.putExtra(WebInfoActivity.WEB_URL, UrlLibs.H5_RAVEL_FUND_RULE);
                startActivity(intent);
            }
        });
    }

    private void sendRequest() {
        RequestTravelFundLogs request = new RequestTravelFundLogs(activity, 0);
        requestData(request);
        RequestGetInvitationCode codeRequest = new RequestGetInvitationCode(activity);
        requestData(codeRequest);
    }

    @Override
    public void onDataRequestSucceed(final BaseRequest _request) {
        super.onDataRequestSucceed(_request);
        if (_request instanceof RequestTravelFundLogs) {
            RequestTravelFundLogs request = (RequestTravelFundLogs) _request;
            travelFundData = request.getData();
            if (travelFundData == null) {
                return;
            }
            // 基金余额
            residuePriceTV.setText(getString(R.string.travel_fund_residue_price, travelFundData.getFundAmount()));

            // 有效日期
            if (travelFundData.getFundAmountInt() <= 0) {
                validityTV.setVisibility(View.GONE);
            } else {
                validityTV.setVisibility(View.VISIBLE);
                validityTV.setText(getString(R.string.travel_fund_validity, travelFundData.getEffectiveDate()));
            }


            TravelFundData.RewardFields rewardFields = travelFundData.getRewardFields();
            if (rewardFields != null) {
                setCouponPrice(rewardFields.getCouponAmount());
                setActivityPrice(rewardFields.getRewardAmountPerOrder(), rewardFields.getRewardRatePerOrder());
            }
        } else if (_request instanceof RequestGetInvitationCode) {
            RequestGetInvitationCode codeRequest = (RequestGetInvitationCode) _request;
            invitationCode = codeRequest.getData();
        }
    }

    @OnClick({R.id.travel_fund_invite_record_tv, R.id.travel_fund_used_record_layout, R.id.travel_fund_invite_tv})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.travel_fund_invite_record_tv://邀请记录
                intent = new Intent(this, TravelFundRecordActivity.class);
                intent.putExtra(Constants.PARAMS_TYPE, TravelFundRecordActivity.TYPE_INVITE_FRIENDS);
                startActivity(intent);
                break;
            case R.id.travel_fund_used_record_layout://使用明细
                intent = new Intent(this, TravelFundRecordActivity.class);
                intent.putExtra(Constants.PARAMS_TYPE, TravelFundRecordActivity.TYPE_USE_Bill);
                startActivity(intent);
                break;
            case R.id.travel_fund_invite_tv://立即邀请
                if (TextUtils.isEmpty(invitationCode) || travelFundData == null || travelFundData.getRewardFields() == null) {
                    break;
                }
                MobClickUtils.onEvent(StatisticConstant.CLICK_INVITE);
                String shareUrl = ShareUrls.getShareThirtyCouponUrl(UserEntity.getUser().getAvatar(this),
                        UserEntity.getUser().getUserName(this),
                        invitationCode);
                CommonUtils.shareDialog(activity, R.mipmap.share_coupon
                        , getString(R.string.invite_friends_share_title, travelFundData.getRewardFields().getCouponAmount())
                        , getString(R.string.invite_friends_share_content)
                        , shareUrl
                        , getClass().getSimpleName()
                        , new ShareDialog.OnShareListener() {
                            @Override
                            public void onShare(int type) {
                                EventUtil.onShareDefaultEvent(StatisticConstant.CLICK_SHAREFOUND, "" + type);
                            }
                        });
                break;
            default:
                break;
        }
    }

    private void setCouponPrice(String couponPrice) {
        String couponPriceString = getString(R.string.travel_fund_description, couponPrice);
        int start = 7;
        int end = couponPrice.length() + 1 + start;
        SpannableString sp = new SpannableString(couponPriceString);
        sp.setSpan(new ForegroundColorSpan(0xFFFF6633), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        couponPriceTV.setText(sp);
    }

    private void setActivityPrice(String rewardAmountPerOrder, String rewardRatePerOrder) {
        String activityPriceString = getString(R.string.travel_fund_description_2, rewardAmountPerOrder, rewardRatePerOrder);
        int start = 5;
        int end = rewardAmountPerOrder.length() + 1 + start;
        SpannableString sp = new SpannableString(activityPriceString);
        sp.setSpan(new ForegroundColorSpan(0xFFFF6633), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        descriptionTV.setText(sp);
    }

    @Override
    public String getEventId() {
        return StatisticConstant.LAUNCH_TRAVELFOUND;
    }
}
