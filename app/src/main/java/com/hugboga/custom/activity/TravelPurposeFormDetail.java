package com.hugboga.custom.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huangbaoche.hbcframe.data.net.HttpRequestUtils;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.hugboga.custom.R;
import com.hugboga.custom.data.bean.TravelPurposeFormBean;
import com.hugboga.custom.data.request.RequestTravelPurposeFormDetail;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/3/3.
 */

public class TravelPurposeFormDetail extends BaseActivity {

    @Bind(R.id.header_left_btn)
    ImageView headerLeft;
    @Bind(R.id.header_title)
    TextView title;

    @Bind(R.id.city_name)
    TextView cityName;
    @Bind(R.id.start_date)
    TextView startDate;
    @Bind(R.id.remark)
    TextView remark;
    @Bind(R.id.user_name)
    TextView userName;
    @Bind(R.id.mobile_phone)
    TextView mobliePhone;

    public TravelPurposeFormBean.ListData listData;
    private String opUserId = "";     //操作人ID
    private Integer id = 0;         //工单ID


    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_travel_purpose_form_detail);
        ButterKnife.bind(this);
        init();
    }

    public void init(){
        title.setText(R.string.travel_purpose_form_detail);
        title.setVisibility(View.VISIBLE);
        headerLeft.setVisibility(View.VISIBLE);

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            opUserId = bundle.getString("opUserId");
            id = bundle.getInt("id", 0);
        }
        requestData();
    }

    @OnClick({R.id.header_left_btn})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.header_left_btn:
                finish();
            break;
        }
    }

    /**
     * 请求数据
     */
    public void requestData(){
        if (TextUtils.isEmpty(opUserId) || 0 == id){
            return;
        }

        RequestTravelPurposeFormDetail requeset = new RequestTravelPurposeFormDetail(this,opUserId,id);
        requestData(requeset);

    }

    @Override
    public void onDataRequestSucceed(BaseRequest request) {
        super.onDataRequestSucceed(request);
        if (request instanceof RequestTravelPurposeFormDetail){
            listData = ((RequestTravelPurposeFormDetail) request).getData();
            if (null == listData){
                return;
            }
            cityName.setText(listData.toCity);
            startDate.setText(listData.tripTimeStr);
            if (TextUtils.isEmpty(listData.userRemark)){
                remark.setText("无");
                remark.setTextColor(0xFFCCCCCC);
            }else {
                remark.setText(listData.userRemark);
                remark.setTextColor(getResources().getColor(R.color.basic_black));
            }
            userName.setText(listData.userName);
            mobliePhone.setText("+"+listData.userAreaCode+" "+listData.userMobile);
        }
    }
}
