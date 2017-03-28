package com.hugboga.custom.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.hugboga.custom.R;
import com.hugboga.custom.adapter.HbcRecyclerSingleTypeAdpater;
import com.hugboga.custom.adapter.HbcRecyclerTypeBaseAdpater;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.bean.CityBean;
import com.hugboga.custom.data.bean.GuideCropBean;
import com.hugboga.custom.data.event.EventAction;
import com.hugboga.custom.data.event.EventType;
import com.hugboga.custom.data.request.RequestGuideCrop;
import com.hugboga.custom.utils.CharterDataUtils;
import com.hugboga.custom.utils.DatabaseManager;
import com.hugboga.custom.utils.UIUtils;
import com.hugboga.custom.widget.ChooseGuideCityView;
import com.hugboga.custom.widget.title.TitleBar;


import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by qingcha on 17/3/12.
 */

public class ChooseGuideCityActivity extends BaseActivity implements HbcRecyclerTypeBaseAdpater.OnItemClickListener{

    @Bind(R.id.chhoose_guide_city_titlebar)
    TitleBar titleBar;
    @Bind(R.id.chhoose_guide_city_recyclerview)
    RecyclerView mRecyclerView;

    public String guideId;
    private HbcRecyclerSingleTypeAdpater<GuideCropBean> mAdapter;
    private ArrayList<GuideCropBean> guideCropList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            guideId = savedInstanceState.getString(Constants.PARAMS_ID);
        } else {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                guideId = bundle.getString(Constants.PARAMS_ID);
            }
        }
        setContentView(R.layout.activity_choose_guide_city);
        ButterKnife.bind(this);

        titleBar.setTitle("选择开始城市");
        TextView leftTV = titleBar.getLeftTV();
        RelativeLayout.LayoutParams leftViewParams = new RelativeLayout.LayoutParams(UIUtils.dip2px(20), UIUtils.dip2px(20));
        leftViewParams.leftMargin = UIUtils.dip2px(15);
        leftViewParams.addRule(RelativeLayout.CENTER_VERTICAL);
        leftTV.setLayoutParams(leftViewParams);
        titleBar.getLeftTV().setBackgroundResource(R.mipmap.top_close);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new HbcRecyclerSingleTypeAdpater(this, ChooseGuideCityView.class);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        requestData(new RequestGuideCrop(this, guideId));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.PARAMS_ID, guideId);
    }

    @Override
    public void onItemClick(View view, int position, Object itemData) {
        if (itemData instanceof GuideCropBean) {
            GuideServiceCitys guideServiceCitys = new GuideServiceCitys(guideCropList, position);
            EventBus.getDefault().post(new EventAction(EventType.CHOOSE_GUIDE_CITY_BACK, guideServiceCitys));
            finish();
        }
    }

    @Override
    public void onDataRequestSucceed(BaseRequest _request) {
        super.onDataRequestSucceed(_request);
        if (_request instanceof RequestGuideCrop) {
            this.guideCropList = ((RequestGuideCrop) _request).getData();
            mAdapter.addData(guideCropList);
        }
    }

    public static class GuideServiceCitys implements Serializable {
        public ArrayList<GuideCropBean> guideCropList;
        public int selectedIndex;

        public GuideServiceCitys(ArrayList<GuideCropBean> guideCropList, int selectedIndex) {
            this.guideCropList = guideCropList;
            this.selectedIndex = selectedIndex;
        }

        public CityBean getSelectedCityBean() {
            return DatabaseManager.getCityBean(guideCropList.get(selectedIndex).cityId);
        }

        public String getGuideServiceCitysId() {
            if (guideCropList == null || guideCropList.size() <= 0) {
                return "";
            }
            String cityId = "";
            int size = guideCropList.size();
            for (int i = 0; i < size; i++) {
                cityId += guideCropList.get(i).cityId;
                if (i < size - 1) {
                    cityId += ",";
                }
            }
            return cityId;
        }

    }
}