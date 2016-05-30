package com.hugboga.custom.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huangbaoche.hbcframe.data.net.ExceptionInfo;
import com.huangbaoche.hbcframe.data.net.HttpRequestListener;
import com.huangbaoche.hbcframe.data.net.HttpRequestUtils;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.huangbaoche.hbcframe.util.MLog;
import com.hugboga.custom.R;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.bean.CityBean;
import com.hugboga.custom.data.bean.ContactUsersBean;
import com.hugboga.custom.data.bean.MostFitAvailableBean;
import com.hugboga.custom.data.bean.MostFitBean;
import com.hugboga.custom.data.bean.OrderBean;
import com.hugboga.custom.data.bean.OrderContact;
import com.hugboga.custom.data.bean.PoiBean;
import com.hugboga.custom.data.bean.SelectCarBean;
import com.hugboga.custom.data.bean.UserEntity;
import com.hugboga.custom.data.event.EventAction;
import com.hugboga.custom.data.event.EventType;
import com.hugboga.custom.data.request.RequestMostFit;
import com.hugboga.custom.data.request.RequestSubmitBase;
import com.hugboga.custom.data.request.RequestSubmitDaily;
import com.hugboga.custom.utils.DateUtils;
import com.umeng.analytics.MobclickAgent;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.xutils.common.Callback;
import org.xutils.view.annotation.ContentView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

import static com.hugboga.custom.R.id.car_seat;
import static com.hugboga.custom.R.id.car_seat_tips;
import static com.hugboga.custom.R.id.coupon_right;
import static com.hugboga.custom.R.id.diary_layout;
import static com.hugboga.custom.R.id.end_hospital_title;
import static com.hugboga.custom.R.id.end_hospital_title_tips;
import static com.hugboga.custom.R.id.man_name;
import static com.hugboga.custom.R.id.other_layout;
import static com.hugboga.custom.R.id.other_name;
import static com.hugboga.custom.R.id.start_hospital_title;
import static com.hugboga.custom.R.id.up_address_right;
import static com.hugboga.custom.R.id.up_right;
/**
 * Created  on 16/4/18.
 */

@ContentView(R.layout.fg_order_new)
public class FGOrderNew extends BaseFragment {
    @Bind(R.id.header_left_btn)
    ImageView headerLeftBtn;
    @Bind(R.id.header_title)
    TextView headerTitle;
    @Bind(R.id.header_right_btn)
    ImageView headerRightBtn;
    @Bind(R.id.header_right_txt)
    TextView headerRightTxt;
    @Bind(R.id.citys_line_title)
    TextView citysLineTitle;
    @Bind(diary_layout)
    LinearLayout diaryLayout;
    @Bind(start_hospital_title)
    TextView startHospitalTitle;
    @Bind(R.id.start_hospital_title_tips)
    TextView startHospitalTitleTips;
    @Bind(end_hospital_title)
    TextView endHospitalTitle;
    @Bind(end_hospital_title_tips)
    TextView endHospitalTitleTips;
    @Bind(car_seat)
    TextView carSeat;
    @Bind(car_seat_tips)
    TextView carSeatTips;
    @Bind(R.id.checkin)
    TextView checkin;
    @Bind(R.id.man_phone_name)
    TextView manPhoneName;
    @Bind(R.id.for_other_man)
    TextView forOtherMan;
    @Bind(R.id.man_phone_layout)
    LinearLayout manPhoneLayout;
    @Bind(R.id.up_left)
    TextView upLeft;
    @Bind(R.id.up_address_left)
    TextView upAddressLeft;
    @Bind(up_address_right)
    TextView upAddressRight;

    @Bind(R.id.hotel_phone_text_code_click)
    TextView hotelPhoneTextCodeClick;
    @Bind(R.id.hotel_phone_text)
    EditText hotelPhoneText;
    @Bind(R.id.mark)
    EditText mark;
    @Bind(R.id.coupon_left)
    RadioButton couponLeft;
    @Bind(coupon_right)
    TextView couponRight;
    @Bind(R.id.dream_left)
    RadioButton dreamLeft;
    @Bind(R.id.dream_right)
    TextView dreamRight;
    @Bind(R.id.insure_left)
    TextView insureLeft;
    @Bind(R.id.insure_right)
    TextView insureRight;
    @Bind(R.id.change_title)
    TextView changeTitle;
    @Bind(R.id.change_detail)
    TextView changeDetail;
    @Bind(R.id.all_money_left)
    TextView allMoneyLeft;
    @Bind(R.id.all_money_left_text)
    TextView allMoneyLeftText;
    @Bind(R.id.all_money_submit_click)
    TextView allMoneySubmitClick;
    @Bind(R.id.all_money_info)
    TextView allMoneyInfo;
    @Bind(R.id.bottom)
    RelativeLayout bottom;
    @Bind(man_name)
    TextView manName;
    @Bind(R.id.man_phone)
    TextView manPhone;
    @Bind(R.id.other_phone_name)
    TextView otherPhoneName;
    @Bind(other_name)
    TextView otherName;
    @Bind(R.id.other_phone)
    TextView otherPhone;
    @Bind(R.id.other_phone_layout)
    LinearLayout otherPhoneLayout;
    @Bind(R.id.pick_name_left)
    TextView pickNameLeft;
    @Bind(R.id.pick_name)
    EditText pickName;
    @Bind(R.id.up_right)
    TextView upRight;
    @Bind(other_layout)
    RelativeLayout otherLayout;

    @Bind(R.id.citys_line_title_tips)
    TextView citys_line_title_tips;

    @Bind(R.id.pick_name_layout)
    RelativeLayout pick_name_layout;

    @Bind(R.id.dream_right_tips)
    TextView dream_right_tips;

    @Override
    protected void initHeader() {
        fgRightBtn.setVisibility(View.VISIBLE);
        fgTitle.setText(R.string.select_city_title);
        source = getArguments().getString("source");

        fgLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        contactUsersBean = new ContactUsersBean();
        String userName = UserEntity.getUser().getNickname(this.getActivity());
        String userPhone = UserEntity.getUser().getPhone(this.getActivity());
        contactUsersBean.userName = userName;
        contactUsersBean.userPhone = userPhone;
        manName.setText(userName);
        manPhone.setText(userPhone);
    }

    String startCityId;
    String endCityId;
    String startDate;
    String endDate;
    String halfDay;
    String adultNum;
    String childrenNum;
    String childseatNum;
    String luggageNum;
    String passCities;
    String carTypeName;
    String startCityName;
    String dayNums;
    SelectCarBean carBean;

    CityBean startBean;
    CityBean endBean;

    public int inNum = 0;
    public int outNum = 0;

    int type = 1;
    boolean isHalfTravel = false;
    @Override
    protected void initView() {
        passCityList = (ArrayList<CityBean>) getArguments().getSerializable("passCityList");

        carBean = this.getArguments().getParcelable("carBean");
        startCityId = this.getArguments().getString("startCityId");
        endCityId = this.getArguments().getString("endCityId");
        startDate = this.getArguments().getString("startDate");
        endDate = this.getArguments().getString("endDate");
        halfDay = this.getArguments().getString("halfDay");
        adultNum = this.getArguments().getString("adultNum");
        childrenNum = this.getArguments().getString("childrenNum");
        childseatNum = this.getArguments().getString("childseatNum");
        luggageNum = this.getArguments().getString("luggageNum");
        passCities = this.getArguments().getString("passCities");
        carTypeName = this.getArguments().getString("carTypeName");
        startCityName = this.getArguments().getString("startCityName");
        dayNums = this.getArguments().getString("dayNums");

        startBean = this.getArguments().getParcelable("startBean");
        endBean = this.getArguments().getParcelable("endBean");

        inNum = this.getArguments().getInt("innum");
        outNum = this.getArguments().getInt("outnum");
        isHalfTravel = this.getArguments().getBoolean("isHalfTravel");

        type = this.getArguments().getInt("type");
        genType(type);

        requestMostFit();

//        city.setText("城市:" + startCityName);
//        if (halfDay.equalsIgnoreCase("0")) {
//            date.setText("用车时间:" + startDate + "到" + endDate);
//            dayNumsText.setText("(" + dayNums + "天)");
//        } else {
//            date.setText("用车时间:" + startDate);
//            dayNumsText.setVisibility(View.INVISIBLE);
//        }
//        mans.setText("人数:" + adultNum + "成人/" + childrenNum + "儿童");
//        seat.setText("儿童座椅:" + childseatNum);
//        baggage.setText("托运行李:" + luggageNum);
//        cartype.setText("车型:" + carTypeName);
//        dayNumsText.setText("(" + dayNums + "天)");
//
//        allMoneyLeftText.setText(carBean.price + "");
//
//        checkboxOther.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    forOtherPeopleLayout.setVisibility(View.VISIBLE);
//                } else {
//                    forOtherPeopleLayout.setVisibility(View.GONE);
//                }
//            }
//        });
    }

    //1,包车 2,接机,3,送机,4,单次接送,5,sku
    private void genType(int type){
        switch(type){
            case 1:
                genDairy();
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
        }
    }


    //包车界面
    private void genDairy(){
        if(isHalfTravel){
            citysLineTitle.setText(startBean.name+"-0.5天包车");
        }else{
            citysLineTitle.setText(startBean.name+"-"+dayNums+"天包车");
        }
        String startWeekDay = "";
        startWeekDay = DateUtils.getWeekOfDate(startDate);

        String endWeekDay = "";
        endWeekDay = DateUtils.getWeekOfDate(endDate);
        citys_line_title_tips.setText("当地时间"+startDate+ "("+startWeekDay +") 至"+"  "+endDate+" ("+endWeekDay+")");


        if(isHalfTravel){
            TextView textView = new TextView(getContext());
            textView.setText("半天: " + startCityName + "市内");
            textView.setCompoundDrawablePadding(10);
            textView.setPadding(20,0,0,0);
            textView.setMinHeight(40);
            textView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.top_line,0,0,0);
            diaryLayout.addView(textView);
        }

        if(!isHalfTravel && null != passCityList) {
            TextView textView;
            CityBean cityBean;
            for (int i = 0; i < passCityList.size(); i++) {
//            public int cityType = 1;// 1 市内 2 周边 3,市外
                textView = new TextView(getContext());
                textView.setCompoundDrawablePadding(10);
                textView.setPadding(20,0,0,0);
                textView.setMinHeight(40);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                cityBean = passCityList.get(i);
                if(i == 0){
                    textView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.top_line,0,0,0);
                }else if(i>0 && i < passCityList.size() -1){
                    textView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.middle_line,0,0,0);
                }else{
                    textView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.bottom_line,0,0,0);
                }

                if (cityBean.cityType == 1) {
                    textView.setText("第" + (i + 1) + "天: 住在" + cityBean.name + ",市内游玩");
                } else if (passCityList.get(i).cityType == 2) {
                    textView.setText("第" + (i + 1) + "天: 住在" + cityBean.name + ",周边游玩");
                } else if (passCityList.get(i).cityType == 3) {
                    textView.setText("第" + (i + 1) + "天: 住在其它城市" + cityBean.name);
                }
                diaryLayout.addView(textView);
            }
        }
        adultNum = this.getArguments().getString("adultNum");
        childrenNum = this.getArguments().getString("childrenNum");
        childseatNum = this.getArguments().getString("childseatNum");
        luggageNum = this.getArguments().getString("luggageNum");

        carSeat.setText(carTypeName);
        carSeatTips.setText("("+"乘坐"+(Integer.valueOf(adultNum)+Integer.valueOf(childrenNum))+"人,行李箱"+luggageNum+"件,儿童座椅"+childseatNum+"个)");


        startHospitalTitle.setVisibility(View.GONE);
        startHospitalTitleTips.setVisibility(View.GONE);

        endHospitalTitle.setVisibility(View.GONE);
        endHospitalTitleTips.setVisibility(View.GONE);
        checkin.setVisibility(View.GONE);
        pick_name_layout.setVisibility(View.GONE);

    }

//
//    String useOrderPrice;
//    String priceChannel;// 渠道价格   [必填]
//    String serviceTime; // 服务时间   [必填]
//    String carTypeId; // 1-经济 2-舒适 3-豪华 4-奢华    [必填]
//    String carSeatNum; // 车座数    [必填]
//    String serviceCityId; // 服务城市ID    [必填]
//    String serviceCountryId; // 服务所在国家ID   [必填]
//    String totalDays; // 日租天数，[日租必填]
//    String distance; // 预估路程公里数 [必填]
//    String serviceLocalDays;// 日租市内天数 [日租必填]
//    String serviceNonlocalDays;// 日租市外天数 [日租必填]
//    String expectedCompTime; // 接送机预计完成时间[非日租必填]

    private void requestMostFit(){
        RequestMostFit requestMostFit = new RequestMostFit(getContext(),carBean.price+"",carBean.price+"",
                startDate+" 00:00:00",carBean.carType+"",carBean.seatCategory+"",startCityId+"",
                startBean.areaCode+"",dayNums+"","1234", inNum+"",outNum+"",dayNums+"");
        HttpRequestUtils.request(getContext(), requestMostFit, new HttpRequestListener() {
            @Override
            public void onDataRequestSucceed(BaseRequest request) {
                RequestMostFit requestMostFit1 = (RequestMostFit)request;
                MostFitBean mostFitBean = requestMostFit1.getData();
                couponRight.setText(mostFitBean.priceInfo+"优惠券");
                couponRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FgCoupon fgCoupon = new FgCoupon();
                        Bundle bundle = new Bundle();
                        MostFitAvailableBean mostFitAvailableBean = new MostFitAvailableBean();

                        mostFitAvailableBean.carSeatNum = carBean.seatCategory+"";
                        mostFitAvailableBean.carTypeId = carBean.carType+"";
                        mostFitAvailableBean.distance = "";
                        mostFitAvailableBean.expectedCompTime = dayNums+"";
                        mostFitAvailableBean.limit = 0+"";
                        mostFitAvailableBean.offset = 20+"";
                        mostFitAvailableBean.priceChannel = carBean.price+"";
                        mostFitAvailableBean.serviceCityId = startCityId+"";
                        mostFitAvailableBean.serviceCountryId = startBean.areaCode;
                        mostFitAvailableBean.serviceLocalDays = inNum+"";
                        mostFitAvailableBean.serviceNonlocalDays = outNum+"";
                        mostFitAvailableBean.serviceTime = startDate;
                        mostFitAvailableBean.userId = UserEntity.getUser().getUserId(getContext());
                        mostFitAvailableBean.totalDays = dayNums +"";
                        bundle.putSerializable(Constants.PARAMS_DATA, mostFitAvailableBean);
                        fgCoupon.setArguments(bundle);
                        startFragment(fgCoupon);
                    }
                });
            }

            @Override
            public void onDataRequestCancel(BaseRequest request) {
                System.out.print("a发生大幅");
            }

            @Override
            public void onDataRequestError(ExceptionInfo errorInfo, BaseRequest request) {
                System.out.print("a发生大幅");
            }
        });

    }

    @Override
    protected Callback.Cancelable requestData() {
        return null;
    }

    @Override
    protected void inflateContent() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        EventBus.getDefault().register(this);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onFragmentResult(Bundle bundle) {
        String fragmentName = bundle.getString(KEY_FRAGMENT_NAME);
        if (FgChooseCountry.class.getSimpleName().equals(fragmentName)) {
            int viewId = bundle.getInt("airportCode");
            TextView codeTv = (TextView) getView().findViewById(viewId);
            if (codeTv != null) {
                String areaCode = bundle.getString(FgChooseCountry.KEY_COUNTRY_CODE);
                codeTv.setText("+" + areaCode);
            }
        } else if (FgPoiSearch.class.getSimpleName().equals(fragmentName)) {
            PoiBean poiBean = (PoiBean) bundle.getSerializable(FgPoiSearch.KEY_ARRIVAL);
            upAddressRight.setText(poiBean.placeName + "\n" + poiBean.placeDetail);
        }
    }

    /**
     * 时间选择器
     */
    public void showTimeSelect() {
        Calendar cal = Calendar.getInstance();
        MyTimePickerDialogListener myTimePickerDialog = new MyTimePickerDialogListener();
        TimePickerDialog datePickerDialog = TimePickerDialog.newInstance(myTimePickerDialog, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
        datePickerDialog.setAccentColor(getActivity().getResources().getColor(R.color.all_bg_yellow));
        datePickerDialog.show(this.getActivity().getFragmentManager(), "TimePickerDialog");                //显示日期设置对话框
    }

    String serverTime = "09:00";

    class MyTimePickerDialogListener implements TimePickerDialog.OnTimeSetListener {
        @Override
        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
            String hour = String.format("%02d", hourOfDay);
            String minuteStr = String.format("%02d", minute);
            serverTime = hour + ":" + minuteStr;
            upRight.setText(serverTime + "(当地时间)");
        }
    }


    @Override
    public int getBusinessType() {
        mBusinessType = Constants.BUSINESS_TYPE_DAILY;
        setGoodsType(mBusinessType);
        return mBusinessType;
    }

    @Override
    public void onDataRequestSucceed(BaseRequest request) {
        if (request instanceof RequestSubmitBase) {
            bringToFront(FgTravel.class, new Bundle());
            String orderNo = ((RequestSubmitBase) request).getData();
            Bundle bundle = new Bundle();
            bundle.putString(FgOrder.KEY_ORDER_ID, orderNo);
            bundle.putString("source", source);
            bundle.putBoolean("needShowAlert", true);
            startFragment(new FgOrder(), bundle);
        }

    }

    @Override
    public void onDataRequestError(ExceptionInfo errorInfo, BaseRequest request) {
        super.onDataRequestError(errorInfo, request);
        MLog.e(errorInfo.toString() + "===========error");
    }

    //TODO;时间太紧 文字先写代码里
    List<OrderContact> contact = new ArrayList<OrderContact>();

    private void checkData() {
        contact.clear();
//        if (TextUtils.isEmpty(orderUserName.getText())) {
//            ToastUtils.showLong("联系人姓名不能为空!");
//            return;
//        }
//
//        if (TextUtils.isEmpty(areaCodeClick.getText())) {
//            ToastUtils.showLong("联系人区号不能为空!");
//            return;
//        }
//
//        if (TextUtils.isEmpty(userPhone.getText())) {
//            ToastUtils.showLong("联系人电话不能为空!");
//            return;
//        }
//
//        if (!TextUtils.isEmpty(areaCodeClick.getText()) && !TextUtils.isEmpty(userPhone.getText())) {
//            OrderContact orderContact = new OrderContact();
//            orderContact.areaCode = areaCodeClick.getText().toString();
//            orderContact.tel = userPhone.getText().toString();
//            contact.add(orderContact);
//        }

//        if(TextUtils.isEmpty(upTimeText.getText())){
//            ToastUtils.showLong("上车时间不能为空!");
//            return;
//        }

//        if(TextUtils.isEmpty(upSiteText.getText())){
//            ToastUtils.showLong("上车地点不能为空!");
//            return;
//        }

//        if(TextUtils.isEmpty(hotelPhoneTextCodeClick.getText())){
//            ToastUtils.showLong("酒店电话区号不能为空!");
//            return;
//        }
//
//        if(TextUtils.isEmpty(hotelPhoneText.getText())){
//            ToastUtils.showLong("酒店电话不能为空!");
//            return;
//        }
//
//        if (checkboxOther.isChecked()) {
//            if (TextUtils.isEmpty(orderUserNameOther.getText())) {
//                ToastUtils.showLong("乘车人姓名不能为空!");
//                return;
//            }
//
//            if (TextUtils.isEmpty(areaCodeOtherClick.getText())) {
//                ToastUtils.showLong("乘车人电话区号不能为空!");
//                return;
//            }
//
//            if (TextUtils.isEmpty(userPhoneOther.getText())) {
//                ToastUtils.showLong("乘车人电话不能为空!");
//                return;
//            }
//        }
//
//        if (phone2Layout.isShown()) {
//            if (!TextUtils.isEmpty(areaCode2Click.getText()) && !TextUtils.isEmpty(userPhone2.getText())) {
//                userPhone2.getText().toString();
//                OrderContact orderContact = new OrderContact();
//                orderContact.areaCode = areaCode2Click.getText().toString();
//                orderContact.tel = userPhone2.getText().toString();
//                contact.add(orderContact);
//            }
//        }
//        if (phone3Layout.isShown()) {
//            if (!TextUtils.isEmpty(areaCode3Click.getText()) && !TextUtils.isEmpty(userPhone3.getText())) {
//                userPhone3.getText().toString();
//                OrderContact orderContact = new OrderContact();
//                orderContact.areaCode = areaCode3Click.getText().toString();
//                orderContact.tel = userPhone3.getText().toString();
//                contact.add(orderContact);
//            }
//        }
        if (UserEntity.getUser().isLogin(getActivity())) {
            RequestSubmitDaily requestSubmitBase = new RequestSubmitDaily(getActivity(), getOrderByInput());
            requestData(requestSubmitBase);
            doUMengStatistic();
        } else {
            Bundle bundle = new Bundle();//用于统计
            bundle.putString("source", "包车下单");
            startFragment(new FgLogin(), bundle);
        }
    }

    private void doUMengStatistic() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("source", source);
        map.put("begincity", startBean.name);
        map.put("carstyle", carBean.carDesc);
//        if (checkboxOther.isChecked()) {
//            map.put("forother", "是");
//        } else {
//            map.put("forother", "否");
//        }
//        map.put("guestcount", adultNum + childrenNum + "");
//        map.put("luggagecount", luggageNum + "");
//        map.put("drivedays", dayNums + "");
//        map.put("payableamount", carBean.price + "");
        MobclickAgent.onEventValue(getActivity(), "submitorder_oneday", map, carBean.price);
    }

    ContactUsersBean contactUsersBean = null;

    public void onEventMainThread(EventAction action) {
        if(action.getType() == EventType.CONTACT_BACK) {
            contactUsersBean = (ContactUsersBean) action.getData();
            if (!TextUtils.isEmpty(contactUsersBean.userName)) {
                manName.setText(contactUsersBean.userName);
                manPhone.setText(contactUsersBean.phoneCode + " " + contactUsersBean.userPhone);
            }

            if (contactUsersBean.isForOther) {
                otherLayout.setVisibility(View.VISIBLE);
                otherName.setText(contactUsersBean.otherName);
                otherPhone.setText(contactUsersBean.otherphoneCode + " " + contactUsersBean.otherPhone);
            }
        }

    }

    OrderBean orderBean;
    ArrayList<CityBean> passCityList;

    private OrderBean getOrderByInput() {
        orderBean = new OrderBean();//订单

        passCityList = new ArrayList<CityBean>();
        if (orderBean.passByCity != null)
            for (int i = 1; i < orderBean.passByCity.size() - 1; i++) {
                passCityList.add(orderBean.passByCity.get(i));
            }

        orderBean.adult = Integer.valueOf(adultNum);
        orderBean.carDesc = carTypeName;
        orderBean.seatCategory = carBean.seatCategory;
        orderBean.carType = carBean.carType;
        orderBean.child = Integer.valueOf(childrenNum);

        orderBean.destAddress = endCityId;
        orderBean.orderPrice = carBean.price;
        orderBean.priceMark = carBean.pricemark;
        orderBean.serviceCityId = Integer.valueOf(startCityId);
        orderBean.serviceEndCityid = Integer.valueOf(endCityId);
        orderBean.serviceCityName = startCityName;
        orderBean.serviceEndCityName = endCityId;
        orderBean.isHalfDaily = Integer.valueOf(halfDay);
        orderBean.contact = contact;
        orderBean.serviceStartTime = serverTime + ":00";
        orderBean.serviceTime = startDate;

        if (halfDay.equalsIgnoreCase("0")) {
            orderBean.oneCityTravel = 2;
            orderBean.totalDays = Integer.valueOf(dayNums);
            orderBean.inTownDays = inNum;
            orderBean.outTownDays = outNum;
            orderBean.serviceEndTime = endDate;
            orderBean.startAddressPoi = startBean.location;
            orderBean.destAddressPoi = endBean.location;
        } else {
            orderBean.oneCityTravel = 1;
            orderBean.serviceEndTime = startDate;
            orderBean.startAddressPoi = startBean.location;
            orderBean.destAddressPoi = startBean.location;
            orderBean.totalDays = 1;
            orderBean.inTownDays = 1;
            orderBean.outTownDays = 0;
        }

//TODO;
        orderBean.serviceAddressTel = hotelPhoneText.getText().toString();
        orderBean.serviceAreaCode = hotelPhoneTextCodeClick.getText().toString();


        orderBean.startAddressPoi = startBean.location;
        orderBean.destAddressPoi = endBean.location;

        orderBean.startAddress = upRight.getText().toString();
        orderBean.startAddressDetail = "";//upSiteText.getText().toString();


        orderBean.destAddressDetail = upRight.getText().toString();

//        orderBean.userName = orderUserName.getText().toString();
//        orderBean.stayCityListStr = passCities;
//        orderBean.userRemark = mark.getText().toString();
//
//        orderBean.serviceDepartTime = serverTime;
//
//        orderBean.priceChannel = carBean.price + "";
//        orderBean.childSeatNum = childseatNum;
//        orderBean.luggageNum = luggageNum;
//        orderBean.realUserName = orderUserNameOther.getText().toString();
//        orderBean.realAreaCode = areaCodeOtherClick.getText().toString();
//        orderBean.realMobile = userPhoneOther.getText().toString();
//        if (checkboxOther.isChecked()) {
//            orderBean.isRealUser = "2";
//        } else {
//            orderBean.isRealUser = "1";
//        }


        return orderBean;

/**
 "priceChannel": model.priceChannel!,                 // C端价格
 "priceMark": model.priceMark!,                       // 询价系统返回ID
 "adultNum": model.adultNum!,                         // 成人座位数
 "childNum": model.childNum!,                         // 小孩座位数

 "startCityId": model.startCityId!,                  // 服务地城市ID
 "destCityId": model.destCityId!,                    // 服务终止城市ID
 "startCityName": model.startCityName!,
 "destCityName": model.destCityName!,

 "serviceDate": model.serviceDate!,                  // 服务时间
 "serviceEndDate": model.serviceEndDate!,            // 服务终止时间
 "serviceRecTime": model.serviceRecTime!,            // 服务时间
 "serviceDepartTime": model.serviceDepartTime!,      // 出发时间

 "startAddressPoi": model.startAddressPoi!,           // 出发地poi(纬度,经度 : 36.524461,180.155223)
 "destAddressPoi": model.destAddressPoi!,             // 目的地poi(纬度,经度 : 36.524461,180.155223)
 "distance": model.distance!,                         // 服务距离
 "expectedCompTime": model.expectedCompTime!,         // 预计服务时间
 "carTypeId": model.carTypeId!,                       // 车类型：1-经济，2-舒适，3-豪华，4-奢华
 "carSeatNum": model.carSeatNum!,                     // 车容量，车座数
 "carDesc": model.carDesc!,                           // 车辆类型描述（所包含的车辆款式）
 "userAreaCode1": model.userAreaCode1!,               // 用户手机号（默认登录使用手机号）
 "userMobile1": model.userMobile1!,                   // 用户区号
 "userName": model.userName!,                         // 客人名字
 "urgentFlag": model.urgentFlag!,                     // 是否急单
 "orderChannel": channelId,                           // 渠道编号


 **/


    }

    private void startArrivalSearch(int cityId, String location) {
        if (location != null) {
            FgPoiSearch fg = new FgPoiSearch();
            Bundle bundle = new Bundle();
            bundle.putInt(FgPoiSearch.KEY_CITY_ID, cityId);
            bundle.putString(FgPoiSearch.KEY_LOCATION, location);
            startFragment(fg, bundle);
        }
    }

    @OnClick({R.id.other_phone_layout,R.id.other_phone_name,R.id.for_other_man, man_name, R.id.man_phone, R.id.man_phone_layout, up_right, up_address_right, R.id.hotel_phone_text_code_click, R.id.hotel_phone_text})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.man_phone_layout:
            case R.id.for_other_man:
            case R.id.other_phone_layout:
                FgChooseOther fgChooseOther = new FgChooseOther();
                Bundle bundle = new Bundle();
                bundle.putParcelable("contactUsersBean", contactUsersBean);
                fgChooseOther.setArguments(bundle);
                startFragment(fgChooseOther);
                break;
            case man_name:
                break;
            case R.id.man_phone:
                break;
            case up_right:
                showTimeSelect();
                break;
            case up_address_right:
                startArrivalSearch(Integer.valueOf(startCityId), startBean.location);
                break;
            case R.id.hotel_phone_text_code_click:
                FgChooseCountry chooseCountry = new FgChooseCountry();
                Bundle bundleCode = new Bundle();
                bundleCode.putInt("airportCode", view.getId());
                startFragment(chooseCountry, bundleCode);
                break;
            case R.id.hotel_phone_text:
                break;
        }
    }

//
//    @OnClick({R.id.header_right_txt, R.id.all_money_info, R.id.header_left_btn, R.id.hotel_phone_text_code_click, R.id.all_money_submit_click})
//    public void onClick(View view) {
//        switch (view.getId()) {
////            case R.id.up_site_text:
////                startArrivalSearch(Integer.valueOf(startCityId), startBean.location);
////                break;
//            case R.id.header_right_txt:
//                HashMap<String, String> map = new HashMap<String, String>();
//                map.put("source", "提交订单页面");
//                MobclickAgent.onEvent(getActivity(), "callcenter_oneday", map);
//                view.setTag("提交订单页面,calldomestic_oneday,calloverseas_oneday");
//                super.onClick(view);
//                break;
//            case R.id.all_money_info:
//                FgOrderInfo fgOrderInfo = new FgOrderInfo();
//                Bundle bundleCar = new Bundle();
//                bundleCar.putParcelable("carBean", carBean);
//                bundleCar.putString("halfDay", halfDay);
//                fgOrderInfo.setArguments(bundleCar);
//                startFragment(fgOrderInfo);
//                break;
//            case R.id.up_time:
//                showTimeSelect();
//                break;
//            case R.id.header_left_btn:
//                finish();
//                break;
//            case R.id.header_title:
//                break;
////            case R.id.add_other_phone_click:
////                if (!phone2Layout.isShown()) {
////                    phone2Layout.setVisibility(View.VISIBLE);
////                } else if (!phone3Layout.isShown()) {
////                    phone3Layout.setVisibility(View.VISIBLE);
////                    addOtherPhoneClick.setTextColor(Color.parseColor("#929394"));
////                }
//
////                break;
////            case R.id.area_code_click:
////            case R.id.area_code_2_click:
////            case R.id.area_code_3_click:
////            case R.id.area_code_other_click:
//            case R.id.hotel_phone_text_code_click:
//                FgChooseCountry chooseCountry = new FgChooseCountry();
//                Bundle bundleCode = new Bundle();
//                bundleCode.putInt("airportCode", view.getId());
//                startFragment(chooseCountry, bundleCode);
//                break;
//            case R.id.all_money_submit_click:
//                checkData();
//                break;
//        }
//    }
}
