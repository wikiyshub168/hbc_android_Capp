package com.hugboga.custom.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.hugboga.custom.MainActivity;
import com.hugboga.custom.R;
import com.hugboga.custom.data.bean.AreaCodeBean;
import com.hugboga.custom.data.bean.UserEntity;
import com.hugboga.custom.data.event.EventAction;
import com.hugboga.custom.data.event.EventType;
import com.hugboga.custom.data.request.RequestTravelPurposeForm;
import com.hugboga.custom.statistic.MobClickUtils;
import com.hugboga.custom.statistic.StatisticConstant;
import com.hugboga.custom.statistic.sensors.SensorsUtils;
import com.hugboga.custom.utils.AlertDialogUtils;
import com.hugboga.custom.utils.CommonUtils;
import com.hugboga.custom.utils.DateUtils;
import com.hugboga.custom.utils.OrderUtils;
import com.hugboga.custom.utils.SharedPre;
import com.hugboga.custom.widget.CsDialog;
import com.hugboga.custom.widget.PurposeFormImgView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.DateTimePicker;


/**
 * Created by Administrator on 2017/2/27.
 */

public class TravelPurposeFormActivity extends BaseActivity implements View.OnClickListener{

    public static final String TAG = "only_year_month_day";   //在加载时间控件的时候用

    @BindView(R.id.header_left_btn)
    ImageView headerLeft;
    @BindView(R.id.header_right_btn)
    ImageView headerRight;
    @BindView(R.id.header_title)
    TextView title;

    @BindView(R.id.city_name)
    TextView cityName;//目的地
    @BindView(R.id.start_date)
    TextView startDate;//出发日期
    //@BindView(R.id.uncertain_check)
    //CheckBox unCertainCheck;//是否确定日期
    @BindView(R.id.remark)
    EditText remark;//备注信息
    @BindView(R.id.user_name)
    EditText userName;//姓名
    @BindView(R.id.areacode)
    TextView areaCode;//区号
    @BindView(R.id.phone)
    EditText phone;//电话
    @BindView(R.id.phone_id)
    TextView phoneId;
    @BindView(R.id.submit_btn)
    Button submitBtn;//提交按钮
    @BindView(R.id.travel_purpose_connect)
    TextView purposeConnect;
    @BindView(R.id.purpose_img)
    PurposeFormImgView purposeImg;
    //CityBean cityBean;
    AreaCodeBean areaCodeBean;
    DateTimePicker picker;
    String tripTimeStr ;
    String areaCodeStr = "86";

    String getCityName = "";
    int getCityId = 0;
    String getStartDate = "";
    int getDays = 0;
    int getAdultNum = 0;
    int getChildNum = 0;
    boolean isFromOrder = false;
    CsDialog csDialog;


    //EditText变化监听
    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            setButtonStatus(submitBtn,checkContent());//设置提交按钮状态
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(isFromOrder){
            EventBus.getDefault().post(new EventAction(EventType.FROM_PURPOSER));
        }
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_travel_purpose_form_new;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        initIntent();
        OrderUtils.genUserTravelPurposeForm(this,purposeConnect,new OrderUtils.MyCLickSpan.OnSpanClickListener(){
            @Override
            public void onSpanClick(View view) {
                SensorsUtils.onAppClick(getEventSource(),"联系定制师",getIntentSource());
                onCustomerService();
            }
        });
        init();
    }
    private void initIntent(){
        getCityName = getIntent().getStringExtra("cityName");
        getCityId = getIntent().getIntExtra("cityId",0);
        getStartDate = getIntent().getStringExtra("startDate");
        getDays = getIntent().getIntExtra("days",0);
        getAdultNum = getIntent().getIntExtra("adultNum",0);
        getChildNum = getIntent().getIntExtra("childNum",0);
        isFromOrder = getIntent().getBooleanExtra("isFromOrder",false);
    }
    public void onCustomerService() {
        //DialogUtil.showServiceDialog(TravelPurposeFormActivity.this, null, UnicornServiceActivity.SourceType.TYPE_CHARTERED, null, null, getEventSource());
        csDialog = CommonUtils.csDialog(TravelPurposeFormActivity.this, null, null, null, UnicornServiceActivity.SourceType.TYPE_CHARTERED, getEventSource(), new CsDialog.OnCsListener() {
            @Override
            public void onCs() {
                if (csDialog != null && csDialog.isShowing()) {
                    csDialog.dismiss();
                }
            }
        });
    }
    public void init(){
        title.setText(getString(R.string.travel_purpose_title));
        headerLeft.setVisibility(View.VISIBLE);
        headerLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFromOrder){
                    EventBus.getDefault().post(new EventAction(EventType.FROM_PURPOSER));
                }
                finish();
            }
        });
        headerRight.setVisibility(View.GONE);
        //添加监听
        userName.addTextChangedListener(watcher);
        phone.addTextChangedListener(watcher);
        SpannableString spannableString = new SpannableString("*手机号");
        spannableString.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        phoneId.setText(spannableString);
        remark.addTextChangedListener(watcher);
        //手机号初始化
        if (!TextUtils.isEmpty(SharedPre.getString(SharedPre.CODE,null)) && !TextUtils.isEmpty(SharedPre.getString(SharedPre.PHONE,null))){
            areaCode.setText("+"+SharedPre.getString(SharedPre.CODE,null).trim());
            areaCodeStr = SharedPre.getString(SharedPre.CODE,null).trim();
            phone.setText(SharedPre.getString(SharedPre.PHONE,null).trim());
        }
        //时间不确定
        /*unCertainCheck.setChecked(false);
        unCertainCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setButtonStatus(submitBtn,checkContent());
                if (isChecked){
                    startDate.setTextColor(0xFFCCCCCC);
                } else {
                    startDate.setTextColor(getResources().getColor(R.color.basic_black));
                }
            }
        });*/
        //初始button
        setButtonStatus(submitBtn,checkContent());
        if(isFromOrder){
            cityName.setText(getCityName);
            startDate.setText(getStartDate);
        }
        purposeImg.update(null);
    }


    @OnClick({R.id.purpose_place_layout,R.id.start_date,R.id.remark,R.id.areacode,R.id.submit_btn})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.purpose_place_layout:
                /*Intent intent = new Intent(this,ChooseCityActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(KEY_FROM, "purpose");
                bundle.putInt(KEY_BUSINESS_TYPE, Constants.BUSINESS_TYPE_DAILY);
                bundle.putString(ChooseCityActivity.KEY_FROM_TAG, CharterFirstStepActivity.TAG);
                intent.putExtras(bundle);
                intent.putExtra("fromInterCity",true);
                startActivity(intent);*/
                goChooseCity();

                break;
            case R.id.start_date:
                gotoSelectDate();
                break;
            case R.id.areacode:
                collapseSoftInputMethod(remark);//收起可能还在展示的软键盘
                collapseSoftInputMethod(userName);
                collapseSoftInputMethod(phone);
                Intent intent1 = new Intent(this,ChooseCountryActivity.class);
                startActivity(intent1);
                break;
            case R.id.submit_btn:
                gotoNext();//提交
                break;
        }
    }
    private void goChooseCity() {
        Intent intent = new Intent(this, QueryCityActivity.class);
        intent.putExtra("isFromTravelPurposeForm",true);
        startActivity(intent);
    }
    @Subscribe
    public void onEventMainThread(EventAction action){
        switch (action.getType()){
            case PURPOSER_CITY:           //选择城市
                /*if (action.getData() instanceof CityBean) {
                    cityBean = (CityBean) action.getData();
                    cityName.setText(cityBean.name);
                }else */if(action.getData() instanceof String){
                    cityName.setText((String)action.getData());
                }
                setButtonStatus(submitBtn,checkContent());
                break;
            case CHOOSE_COUNTRY_BACK:           //选择areaCode
                if (action.getData() instanceof AreaCodeBean){
                    areaCodeBean = (AreaCodeBean)action.getData();
                    areaCode.setText("+"+areaCodeBean.getCode());
                    areaCodeStr = areaCodeBean.getCode();
                }
                setButtonStatus(submitBtn,checkContent());
                break;
        }
    }

    /**
     * 判断每个字段是否都已填
     * @return
     */
    public Boolean checkContent(){
        /*if (TextUtils.isEmpty(cityName.getText().toString().trim())){
            return false;
        }else if (TextUtils.isEmpty(userName.getText().toString().trim())){
            return false;
        }else */if (TextUtils.isEmpty(areaCode.getText().toString().trim())){
            return false;
        }else if (TextUtils.isEmpty(phone.getText().toString().trim())) {
            return false;
        }/*else if (TextUtils.isEmpty(startDate.getText().toString().trim())){
            if (unCertainCheck.isChecked()){
                return true;
            }else {
                return false;
            }
        }*/ else {
            return true;
        }
    }

    /**
     * 设置提交按钮的状态
     * @param button
     */
    public void setButtonStatus(Button button,Boolean b){
        if (b){
            button.setEnabled(true);
            button.setFocusable(true);
            button.setBackground(getResources().getDrawable(R.drawable.shape_rounded_yellow_btn));
        }else {
            button.setEnabled(false);
            button.setFocusable(false);
            button.setBackground(getResources().getDrawable(R.drawable.shape_rounded_gray_btn));
        }
    }

    /**
     * 选择时间
     */
    public void gotoSelectDate(){
        final Calendar calendar = Calendar.getInstance();
        picker = new DateTimePicker(this,DateTimePicker.ONLY_YEAR_MONTH_DAY,true);
        //设置控件头部属性
        picker.setTitleText("请选择旅行日期");
        picker.setTitleTextSize(18);
        picker.setTitleTextColor(getResources().getColor(R.color.basic_black));
        picker.setCancelTextColor(getResources().getColor(R.color.default_yellow));
        picker.setSubmitTextColor(getResources().getColor(R.color.default_yellow));
        picker.setPressedTextColor(getResources().getColor(R.color.default_yellow));
        picker.setTopBackgroundColor(getResources().getColor(R.color.date_title_bg));
        picker.setLineColor(getResources().getColor(R.color.text_hint_color));
        picker.useMaxRatioLine();
        picker.setCancelText("取消");
        picker.setSubmitText("确定");

        picker.setRange(calendar.get(Calendar.YEAR),calendar.get(Calendar.YEAR)+1);
        picker.setSelectedItem(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        picker.setOnDateTimePickListener(new DateTimePicker.OnYearMonthDayTimePickListener() {
            @Override
            public void onDateTimePicked(String year, String month, String day, String hour, String minute) {
                String tempDate="";
                if(month.equals("待定")){
                    tempDate = year + "年" + "待定";
                }else {
                    tempDate = year + "年" + month + "月";
                }

                String currentDate = calendar.get(Calendar.YEAR)+"年"+(calendar.get(Calendar.MONTH)+1)+"月";
                if(DateUtils.getDateByStr2(tempDate).before(DateUtils.getDateByStr2(currentDate))){
                    CommonUtils.showToast("不能选择今天之前的时间");
                    return;
                }
                startDate.setText(tempDate);
                picker.dismiss();
                setButtonStatus(submitBtn, checkContent());
            }
        });
        picker.setLineColor(0xffaaaaaa);
        picker.show();
    }

    /**
     * 点击提交
     */
    public void gotoNext(){
        if ("+86".equals(areaCode.getText().toString().trim())){
            if (!phone.getText().toString().startsWith("1") || !(11 == phone.getText().toString().length())){
                CommonUtils.showToast(R.string.phone_format_incorrect);
                phone.setFocusable(true);
                return;
            }
        }

        //判断不确定按钮是否已经选中
        /*if (unCertainCheck.isChecked()){
            tripTimeStr = "待定";
        } else */{
            tripTimeStr = startDate.getText().toString();
        }
        RequestTravelPurposeForm requestTravelPurposeForm;
        if(!isFromOrder){
            requestTravelPurposeForm = new RequestTravelPurposeForm(this, UserEntity.getUser().getUserId(this),
                    UserEntity.getUser().getUserName(this),UserEntity.getUser().getAreaCode(this),UserEntity.getUser().getPhone(this),
                    null,cityName.getText().toString().trim(),tripTimeStr,
                    remark.getText().toString(),areaCodeStr,phone.getText().toString(),
                    userName.getText().toString().toString(), null,null,null);
        }else{
            requestTravelPurposeForm = new RequestTravelPurposeForm(this, UserEntity.getUser().getUserId(this),
                UserEntity.getUser().getUserName(this),UserEntity.getUser().getAreaCode(this),UserEntity.getUser().getPhone(this),
                null,cityName.getText().toString().trim(),tripTimeStr,
                remark.getText().toString(),areaCodeStr,phone.getText().toString(),
                userName.getText().toString().toString(), getDays,getAdultNum,getChildNum);

        }

        requestData(requestTravelPurposeForm);
        MobClickUtils.onEvent(StatisticConstant.YI_XIANG_SUCCEED);
        SensorsUtils.onAppClick(getEventSource(),"提交",getIntentSource());
    }

    @Override
    public void onDataRequestSucceed(BaseRequest request) {
        super.onDataRequestSucceed(request);
        AlertDialogUtils.showAlertDialog(this, false, getResources().getString(R.string.submit_success), getResources().getString(R.string.alert_submit_success), "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(isFromOrder){
                    Intent intent = new Intent(TravelPurposeFormActivity.this, MainActivity.class);
                    TravelPurposeFormActivity.this.startActivity(intent);
                    finish();
                }else {
                    finish();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public String getEventSource() {
        return "私人定制行程";
    }
}
