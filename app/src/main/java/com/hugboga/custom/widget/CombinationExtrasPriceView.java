package com.hugboga.custom.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hugboga.custom.R;
import com.hugboga.custom.data.bean.CarAdditionalServicePrice;
import com.hugboga.custom.data.bean.CarBean;
import com.hugboga.custom.data.bean.GroupQuotesBean;
import com.hugboga.custom.utils.CharterDataUtils;
import com.hugboga.custom.utils.CommonUtils;
import com.sevenheaven.iosswitch.ShSwitchView;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by qingcha on 17/8/22.
 */
public class CombinationExtrasPriceView extends LinearLayout implements ChooseCountView.OnCountChangeListener {

    @BindView(R.id.extras_price_pickup_switch_view)
    ShSwitchView pickupSwitchView;
    @BindView(R.id.extras_price_pickup_price_tv)
    TextView pickupPriceTV;
    @BindView(R.id.extras_price_pickup_layout)
    RelativeLayout pickupLayout;
    @BindView(R.id.extras_price_pickup_bottom_line)
    View pickupBottomLine;
    @BindView(R.id.extras_price_pickup_et)
    EditText pickupET;
    @BindView(R.id.extras_price_pickup_star_tv)
    TextView pickupStarTV;

    @BindView(R.id.extras_price_checkin_switch_view)
    ShSwitchView checkinSwitchView;
    @BindView(R.id.extras_price_checkin_price_tv)
    TextView checkinPriceTV;
    @BindView(R.id.extras_price_checkin_layout)
    RelativeLayout checkinLayout;
    @BindView(R.id.extras_price_checkin_bottom_line)
    View checkinBottomLine;

    @BindView(R.id.extras_price_child_seat_price_tv)
    TextView childSeatPriceTV;
    @BindView(R.id.extras_price_child_seat_choose_countview)
    ChooseCountView childSeatCountView;
    @BindView(R.id.extras_price_child_seat_layout)
    RelativeLayout childSeatLayout;
    @BindView(R.id.extras_price_child_seat_price_hint_tv)
    TextView childSeatPriceHintTV;
    @BindView(R.id.extras_price_child_seat_hint_layout)
    LinearLayout childSeatHintLayout;

    private CarBean carBean;
    private OnAdditionalPriceChangeListener additionalPriceChangeListener;
    private AdditionalPriceBean additionalPriceBean;

    public int pickupSignPrice = -1;
    public int checkInPrice = -1;
    public int childSeatCount = -1;

    public double realCharterDayNums = 0;

    public CombinationExtrasPriceView(Context context) {
        this(context, null);
    }

    public CombinationExtrasPriceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = inflate(context, R.layout.view_combination_extras_price, this);
        ButterKnife.bind(view);
        additionalPriceBean = new AdditionalPriceBean();
    }

    public void update(CarBean _carBean, CharterDataUtils charterDataUtils) {
        if (_carBean == null) {
            return;
        }
        this.realCharterDayNums = charterDataUtils.getRealCharterDayNums();
        this.carBean = _carBean;

        calculatePrice();
        final boolean isShowChildSeat = charterDataUtils.isSupportChildSeat && charterDataUtils.childSeatCount > 0;
        final boolean isShowPickup = pickupSignPrice >= 0 && charterDataUtils.isPickup();
        final boolean isShowCheckIn = checkInPrice >= 0 && charterDataUtils.isTransfer();

        if (isShowChildSeat) {
            childSeatLayout.setVisibility(View.VISIBLE);
            childSeatPriceHintTV.setVisibility(View.VISIBLE);
            childSeatHintLayout.setVisibility(View.VISIBLE);

            if (childSeatCount == -1) {
                childSeatCount = charterDataUtils.childSeatCount;
                childSeatCountView.setOnCountChangeListener(this);
                childSeatCountView.setMinCount(0).setMaxCount(charterDataUtils.childSeatCount).setCount(charterDataUtils.childSeatCount, false);
            }

            updateChildSeatTextView();

            String childSeatPrice1Str = additionalPriceBean.childSeatPrice1 > 0 ? CommonUtils.getString(R.string.order_extras_price_child_seat_hint1, (int)(additionalPriceBean.childSeatPrice1 / realCharterDayNums)) : CommonUtils.getString(R.string.free_price);
            String childSeatPrice2Str = additionalPriceBean.childSeatPrice2 > 0 ? CommonUtils.getString(R.string.order_extras_price_child_seat_hint2, (int)(additionalPriceBean.childSeatPrice2 / realCharterDayNums)) : CommonUtils.getString(R.string.free_price);
            String priceHintTV = CommonUtils.getString(R.string.order_extras_price_child_seat_hint3, childSeatPrice1Str, childSeatPrice2Str);
            childSeatPriceHintTV.setText(priceHintTV);
        } else {
            childSeatLayout.setVisibility(View.GONE);
            childSeatPriceHintTV.setVisibility(View.GONE);
            childSeatHintLayout.setVisibility(View.GONE);
        }

        if (isShowPickup) {
            pickupLayout.setVisibility(View.VISIBLE);
            pickupPriceTV.setText(String.format("¥%1$s", pickupSignPrice > 0 ? pickupSignPrice : 0));
            pickupSwitchView.setOnSwitchStateChangeListener(new ShSwitchView.OnSwitchStateChangeListener() {
                @Override
                public void onSwitchStateChange(boolean b) {
                    if (pickupSignPrice > 0 && additionalPriceChangeListener != null) {
                        additionalPriceChangeListener.onAdditionalPriceChange(getAdditionalPrice());
                    }
                    pickupET.setVisibility(b ? View.VISIBLE : View.GONE);
                    pickupStarTV.setVisibility(b ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            pickupSwitchView.setOn(false);
            pickupLayout.setVisibility(View.GONE);
        }

        if (isShowCheckIn) {
            checkinLayout.setVisibility(View.VISIBLE);
            checkinPriceTV.setText(String.format("¥%1$s", checkInPrice > 0 ? checkInPrice : 0));
            checkinSwitchView.setOnSwitchStateChangeListener(new ShSwitchView.OnSwitchStateChangeListener() {
                @Override
                public void onSwitchStateChange(boolean b) {
                    if (checkInPrice > 0 && additionalPriceChangeListener != null) {
                        additionalPriceChangeListener.onAdditionalPriceChange(getAdditionalPrice());
                    }
                }
            });
        } else {
            checkinSwitchView.setOn(false);
            checkinLayout.setVisibility(View.GONE);
        }

        if (!isShowChildSeat) {
            if (isShowPickup && !isShowCheckIn) {
                pickupBottomLine.setVisibility(View.GONE);
            } else if(!isShowPickup && isShowCheckIn) {
                checkinBottomLine.setVisibility(View.GONE);
            }
        }

        if (isShowChildSeat || isShowPickup || isShowCheckIn) {
            setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.GONE);
        }
    }

    private void calculatePrice() {
        int allChildSeatPrice1 = -1;
        int allChildSeatPrice2 = -1;
        ArrayList<GroupQuotesBean> quotes = carBean.quotes;
        int size = quotes.size();
        for (int i = 0; i < size; i++) {
            GroupQuotesBean groupQuotesBean = quotes.get(i);
            if (groupQuotesBean.additionalServicePrice != null) {
                CarAdditionalServicePrice additionalServicePrice = groupQuotesBean.additionalServicePrice;
                if (additionalServicePrice == null) {
                    continue;
                }
                if (!TextUtils.isEmpty(additionalServicePrice.pickupSignPrice)) {
                    pickupSignPrice = CommonUtils.getCountInteger(additionalServicePrice.pickupSignPrice);
                }
                if (!TextUtils.isEmpty(additionalServicePrice.checkInPrice)) {
                    checkInPrice = CommonUtils.getCountInteger(additionalServicePrice.checkInPrice);
                }

                if (TextUtils.isEmpty(additionalServicePrice.childSeatPrice1) || TextUtils.isEmpty(additionalServicePrice.childSeatPrice2)) {
                    continue;
                }
                int childSeatPrice1 = CommonUtils.getCountInteger(additionalServicePrice.childSeatPrice1);
                int childSeatPrice2 = CommonUtils.getCountInteger(additionalServicePrice.childSeatPrice2);
                if (childSeatPrice1 == -1 || childSeatPrice2 == -1) {
                    continue;
                }
                if (allChildSeatPrice1 == -1 && childSeatPrice1 >= 0) {
                    allChildSeatPrice1 = 0;
                }
                if (allChildSeatPrice2 == -1 && childSeatPrice2 >= 0) {
                    allChildSeatPrice2 = 0;
                }
                allChildSeatPrice1 += childSeatPrice1;
                allChildSeatPrice2 += childSeatPrice2;
            }
        }
        if (allChildSeatPrice1 != -1 && allChildSeatPrice2 != -1) {
            additionalPriceBean.childSeatPrice1 = allChildSeatPrice1;
            additionalPriceBean.childSeatPrice2 = allChildSeatPrice2;
        }
    }

    private int updateChildSeatTextView() {
        int seatTotalPrice = getSeatTotalPrice();
        String seatTotalPriceStr = "";
        if (childSeatCount == 0) {
            seatTotalPriceStr = "";
        } else if (seatTotalPrice > 0) {
            seatTotalPriceStr = String.format("(¥%1$s)", seatTotalPrice);
        } else {
            seatTotalPriceStr = "(" + CommonUtils.getString(R.string.free_price) + ")";
        }
        childSeatPriceTV.setText(seatTotalPriceStr);
        return seatTotalPrice;
    }

    public int getSeatTotalPrice() {
        int seatTotalPrice = 0;
        if (childSeatCount >= 1 && additionalPriceBean.childSeatPrice1 > 0) {
            seatTotalPrice = additionalPriceBean.childSeatPrice1;
        }
        if (childSeatCount > 1 && additionalPriceBean.childSeatPrice2 > 0) {
            seatTotalPrice += (additionalPriceBean.childSeatPrice2 * (childSeatCount - 1));
        }
        return seatTotalPrice;
    }

    public int getAdditionalPrice() {
        int totalPrice = getSeatTotalPrice();
        if (pickupSwitchView.isOn() && pickupSignPrice > 0) {
            totalPrice += pickupSignPrice;
        }
        if (checkinSwitchView.isOn() && checkInPrice > 0) {
            totalPrice += checkInPrice;
        }
        return totalPrice;
    }

    public interface OnAdditionalPriceChangeListener {
        public void onAdditionalPriceChange(double price);
    }

    public void setOnAdditionalPriceChangeListener(OnAdditionalPriceChangeListener additionalPriceChangeListener) {
        this.additionalPriceChangeListener = additionalPriceChangeListener;
    }

    @Override
    public void onCountChange(View view, int count) {
        int oldSeatTotalPrice = getSeatTotalPrice();
        childSeatCount = count;
        int seatTotalPrice = updateChildSeatTextView();
        if (oldSeatTotalPrice != seatTotalPrice && additionalPriceChangeListener != null) {
            additionalPriceChangeListener.onAdditionalPriceChange(getAdditionalPrice());
        }
    }

    public boolean checkFlightBrandSign() {
        String flightBrandSign = CommonUtils.getText(pickupET, false);
        if (pickupSwitchView.isOn() && TextUtils.isEmpty(flightBrandSign)) {
            CommonUtils.showToast(R.string.order_travelerinfo_check_pickup);
            return false;
        } else {
            return true;
        }
    }

    public AdditionalPriceBean getAdditionalPriceBean() {
        additionalPriceBean.childSeatCount = childSeatCount;
        additionalPriceBean.isCheckin = checkinSwitchView.isOn() ? 1 : 0;
        additionalPriceBean.isFlightSign = pickupSwitchView.isOn() ? 1 : 0;
        additionalPriceBean.flightBrandSign = CommonUtils.getText(pickupET, false);;
        additionalPriceBean.seatTotalPrice = getSeatTotalPrice();
        additionalPriceBean.pickupSignPrice = pickupSignPrice >= 0 ? pickupSignPrice : 0;
        additionalPriceBean.checkInPrice = checkInPrice >= 0 ? checkInPrice : 0;
        return additionalPriceBean;
    }

    public static class AdditionalPriceBean implements Serializable{
        public int childSeatCount;
        public int childSeatPrice1;
        public int childSeatPrice2;
        public int pickupSignPrice;
        public int checkInPrice;
        public int isCheckin;
        public int isFlightSign;
        public String flightBrandSign;
        public int seatTotalPrice;
    }
}
