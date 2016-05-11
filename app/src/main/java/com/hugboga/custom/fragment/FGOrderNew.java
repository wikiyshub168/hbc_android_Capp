package com.hugboga.custom.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huangbaoche.hbcframe.data.net.ExceptionInfo;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.huangbaoche.hbcframe.util.MLog;
import com.hugboga.custom.R;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.bean.CityBean;
import com.hugboga.custom.data.bean.OrderBean;
import com.hugboga.custom.data.bean.OrderContact;
import com.hugboga.custom.data.bean.PoiBean;
import com.hugboga.custom.data.bean.SelectCarBean;
import com.hugboga.custom.data.bean.UserEntity;
import com.hugboga.custom.data.request.RequestSubmitBase;
import com.hugboga.custom.data.request.RequestSubmitDaily;
import com.hugboga.custom.utils.ToastUtils;
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
    @Bind(R.id.city)
    TextView city;
    @Bind(R.id.date)
    TextView date;
    @Bind(R.id.mans)
    TextView mans;
    @Bind(R.id.seat)
    TextView seat;
    @Bind(R.id.baggage)
    TextView baggage;
    @Bind(R.id.cartype)
    TextView cartype;
    @Bind(R.id.order_user_name)
    EditText orderUserName;
    @Bind(R.id.area_code_click)
    TextView areaCodeClick;
    @Bind(R.id.user_phone)
    EditText userPhone;
    @Bind(R.id.phone2_line)
    TextView phone2Line;
    @Bind(R.id.area_code_2_click)
    TextView areaCode2Click;
    @Bind(R.id.user_phone_2)
    EditText userPhone2;
    @Bind(R.id.phone2_layout)
    LinearLayout phone2Layout;
    @Bind(R.id.phone3_line)
    TextView phone3Line;
    @Bind(R.id.area_code_3_click)
    TextView areaCode3Click;
    @Bind(R.id.user_phone_3)
    EditText userPhone3;
    @Bind(R.id.phone3_layout)
    LinearLayout phone3Layout;
    @Bind(R.id.checkbox_other)
    CheckBox checkboxOther;
    @Bind(R.id.add_other_phone_click)
    TextView addOtherPhoneClick;
    @Bind(R.id.order_user_name_other)
    EditText orderUserNameOther;
    @Bind(R.id.area_code_other_click)
    TextView areaCodeOtherClick;
    @Bind(R.id.user_phone_other)
    EditText userPhoneOther;
    @Bind(R.id.for_other_people_layout)
    LinearLayout forOtherPeopleLayout;
    @Bind(R.id.up_time_text)
    TextView upTimeText;
    @Bind(R.id.up_site_text)
    TextView upSiteText;
    @Bind(R.id.hotel_phone_text_code_click)
    TextView hotelPhoneTextCodeClick;
    @Bind(R.id.hotel_phone_text)
    EditText hotelPhoneText;
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
    @Bind(R.id.dayNums)
    TextView dayNumsText;
    @Bind(R.id.mark)
    EditText mark;

    @Override
    protected void initHeader() {
        fgRightBtn.setVisibility(View.VISIBLE);
        fgTitle.setText(R.string.select_city_title);
        source = getArguments().getString("source");
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


        city.setText("城市:"+startCityName);
        if(halfDay.equalsIgnoreCase("0")){
            date.setText("用车时间:"+startDate+"到"+endDate);
            dayNumsText.setText("("+dayNums+"天)");
        }else{
            date.setText("用车时间:"+startDate);
            dayNumsText.setVisibility(View.INVISIBLE);
        }
        mans.setText("人数:"+adultNum+"成人/"+childrenNum+"儿童");
        seat.setText("儿童座椅:"+childseatNum);
        baggage.setText("托运行李:"+luggageNum);
        cartype.setText("车型:"+carTypeName);
        dayNumsText.setText("("+dayNums+"天)");

        allMoneyLeftText.setText(carBean.price + "");

        checkboxOther.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    forOtherPeopleLayout.setVisibility(View.VISIBLE);
                } else {
                    forOtherPeopleLayout.setVisibility(View.GONE);
                }
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
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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
        }else if (FgPoiSearch.class.getSimpleName().equals(fragmentName)) {
            PoiBean poiBean = (PoiBean) bundle.getSerializable(FgPoiSearch.KEY_ARRIVAL);
            upSiteText.setText(poiBean.placeName + "\n" + poiBean.placeDetail);
        }
    }

    /**
     * 时间选择器
     */
    public void showTimeSelect() {
        Calendar cal = Calendar.getInstance();
        MyTimePickerDialogListener myTimePickerDialog = new MyTimePickerDialogListener();
        com.wdullaer.materialdatetimepicker.time.TimePickerDialog datePickerDialog = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(myTimePickerDialog, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
        datePickerDialog.setAccentColor(getActivity().getResources().getColor(R.color.all_bg_yellow));
        datePickerDialog.show(this.getActivity().getFragmentManager(), "TimePickerDialog");                //显示日期设置对话框
    }

    String serverTime = "09:00";
    /*
         * Function  :       自定义MyDatePickerDialog类，用于实现DatePickerDialog.OnDateSetListener接口，
         *                           当点击日期设置对话框中的“设置”按钮时触发该接口方法
         */
    class MyTimePickerDialogListener implements TimePickerDialog.OnTimeSetListener {
        @Override
        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
            String hour = String.format("%02d", hourOfDay);
            String minuteStr = String.format("%02d", minute);
            serverTime = hour + ":" + minuteStr;
            upTimeText.setText(serverTime + "(当地时间)");
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
            bundle.putString("source",source);
            bundle.putBoolean("needShowAlert",true);
            startFragment(new FgOrder(), bundle);
        }

    }

    @Override
    public void onDataRequestError(ExceptionInfo errorInfo, BaseRequest request) {
        super.onDataRequestError(errorInfo, request);
        MLog.e(errorInfo.toString()+"===========error");
    }

    //TODO;时间太紧 文字先写代码里
    List<OrderContact> contact = new ArrayList<OrderContact>();
    private void checkData(){
        contact.clear();
        if(TextUtils.isEmpty(orderUserName.getText())){
            ToastUtils.showLong("联系人姓名不能为空!");
            return;
        }

        if(TextUtils.isEmpty(areaCodeClick.getText())){
            ToastUtils.showLong("联系人区号不能为空!");
            return;
        }

        if(TextUtils.isEmpty(userPhone.getText())){
            ToastUtils.showLong("联系人电话不能为空!");
            return;
        }

        if(!TextUtils.isEmpty(areaCodeClick.getText()) && !TextUtils.isEmpty(userPhone.getText())){
            OrderContact orderContact = new OrderContact();
            orderContact.areaCode = areaCodeClick.getText().toString();
            orderContact.tel = userPhone.getText().toString();
            contact.add(orderContact);
        }

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

        if(checkboxOther.isChecked()){
            if(TextUtils.isEmpty(orderUserNameOther.getText())){
                ToastUtils.showLong("乘车人姓名不能为空!");
                return;
            }

            if(TextUtils.isEmpty(areaCodeOtherClick.getText())){
                ToastUtils.showLong("乘车人电话区号不能为空!");
                return;
            }

            if(TextUtils.isEmpty(userPhoneOther.getText())){
                ToastUtils.showLong("乘车人电话不能为空!");
                return;
            }
        }

        if(phone2Layout.isShown()){
            if(!TextUtils.isEmpty(areaCode2Click.getText()) && !TextUtils.isEmpty(userPhone2.getText())) {
                userPhone2.getText().toString();
                OrderContact orderContact = new OrderContact();
                orderContact.areaCode = areaCode2Click.getText().toString();
                orderContact.tel = userPhone2.getText().toString();
                contact.add(orderContact);
            }
        }
        if(phone3Layout.isShown()) {
            if(!TextUtils.isEmpty(areaCode3Click.getText()) && !TextUtils.isEmpty(userPhone3.getText())) {
                userPhone3.getText().toString();
                OrderContact orderContact = new OrderContact();
                orderContact.areaCode = areaCode3Click.getText().toString();
                orderContact.tel = userPhone3.getText().toString();
                contact.add(orderContact);
            }
        }
        if (UserEntity.getUser().isLogin(getActivity())) {
            RequestSubmitDaily requestSubmitBase = new RequestSubmitDaily(getActivity(), getOrderByInput());
            requestData(requestSubmitBase);
            doUMengStatistic();
        }else{
            Bundle bundle = new Bundle();//用于统计
            bundle.putString("source","包车下单");
            startFragment(new FgLogin(), bundle);
        }
    }

    private void doUMengStatistic(){
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("source", source);
        map.put("begincity", startBean.name);
        map.put("carstyle", carBean.carDesc);
        if(checkboxOther.isChecked()) {
            map.put("forother", "是");
        }else{
            map.put("forother", "否");
        }
//        map.put("guestcount", adultNum + childrenNum + "");
//        map.put("luggagecount", luggageNum + "");
//        map.put("drivedays", dayNums + "");
//        map.put("payableamount", carBean.price + "");
        MobclickAgent.onEventValue(getActivity(), "submitorder_oneday", map, carBean.price);
    }

    OrderBean orderBean;
    ArrayList passCityList;
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
        orderBean.serviceCityId  = Integer.valueOf(startCityId);
        orderBean.serviceEndCityid = Integer.valueOf(endCityId);
        orderBean.serviceCityName = startCityName;
        orderBean.serviceEndCityName = endCityId;
        orderBean.isHalfDaily =  Integer.valueOf(halfDay);
        orderBean.contact = contact;
        orderBean.serviceStartTime = serverTime+":00";
        orderBean.serviceTime = startDate;

        if(halfDay.equalsIgnoreCase("0")) {
            orderBean.oneCityTravel = 2;
            orderBean.totalDays = Integer.valueOf(dayNums);
            orderBean.inTownDays = inNum;
            orderBean.outTownDays = outNum;
            orderBean.serviceEndTime = endDate;
            orderBean.startAddressPoi = startBean.location;
            orderBean.destAddressPoi = endBean.location;
        }else{
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

        orderBean.startAddress = upSiteText.getText().toString();
        orderBean.startAddressDetail = "";//upSiteText.getText().toString();


        orderBean.destAddressDetail = upSiteText.getText().toString();

        orderBean.userName = orderUserName.getText().toString();
        orderBean.stayCityListStr = passCities;
        orderBean.userRemark = mark.getText().toString();

        orderBean.serviceDepartTime = serverTime;

        orderBean.priceChannel = carBean.price+"";
        orderBean.childSeatNum = childseatNum;
        orderBean.luggageNum = luggageNum;
        orderBean.realUserName = orderUserNameOther.getText().toString();
        orderBean.realAreaCode = areaCodeOtherClick.getText().toString();
        orderBean.realMobile = userPhoneOther.getText().toString();
        if(checkboxOther.isChecked()) {
            orderBean.isRealUser = "2";
        }else{
            orderBean.isRealUser = "1";
        }


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


    @OnClick({R.id.header_right_txt,R.id.up_site_text,R.id.all_money_info,R.id.up_time_text,R.id.header_left_btn, R.id.area_code_click, R.id.area_code_2_click, R.id.area_code_3_click, R.id.add_other_phone_click, R.id.area_code_other_click, R.id.hotel_phone_text_code_click, R.id.all_money_submit_click})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.up_site_text:
                startArrivalSearch(Integer.valueOf(startCityId), startBean.location);
            break;
            case R.id.header_right_txt:
                HashMap<String,String> map = new HashMap<String,String>();
                map.put("source", "提交订单页面");
                MobclickAgent.onEvent(getActivity(), "callcenter_oneday", map);
                view.setTag("提交订单页面,calldomestic_oneday,calloverseas_oneday");
                super.onClick(view);
                break;
            case R.id.all_money_info:
                FgOrderInfo fgOrderInfo = new FgOrderInfo();
                Bundle bundleCar = new Bundle();
                bundleCar.putParcelable("carBean",carBean);
                bundleCar.putString("halfDay",halfDay);
                fgOrderInfo.setArguments(bundleCar);
                startFragment(fgOrderInfo);
                break;
            case R.id.up_time_text:
                showTimeSelect();
                break;
            case R.id.header_left_btn:
                finish();
                break;
            case R.id.header_title:
                break;
            case R.id.add_other_phone_click:
                if(!phone2Layout.isShown()) {
                    phone2Layout.setVisibility(View.VISIBLE);
                }else if(!phone3Layout.isShown()) {
                    phone3Layout.setVisibility(View.VISIBLE);
                    addOtherPhoneClick.setTextColor(Color.parseColor("#929394"));
                }

                break;
            case R.id.area_code_click:
            case R.id.area_code_2_click:
            case R.id.area_code_3_click:
            case R.id.area_code_other_click:
            case R.id.hotel_phone_text_code_click:
                FgChooseCountry chooseCountry = new FgChooseCountry();
                Bundle bundleCode = new Bundle();
                bundleCode.putInt("airportCode", view.getId());
                startFragment(chooseCountry, bundleCode);
                break;
            case R.id.all_money_submit_click:
                checkData();
                break;
        }
    }
}