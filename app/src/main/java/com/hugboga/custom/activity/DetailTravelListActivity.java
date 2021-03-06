package com.hugboga.custom.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.hugboga.custom.R;
import com.hugboga.custom.adapter.HbcRecyclerSingleTypeAdpater;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.bean.OrderBean;
import com.hugboga.custom.utils.WrapContentLinearLayoutManager;
import com.hugboga.custom.widget.DetailTravelItemView;
import com.hugboga.custom.widget.title.TitleBar;

import java.util.List;

import butterknife.BindView;

/**
 * Created by qingcha on 17/3/16.
 */

public class DetailTravelListActivity extends BaseActivity {

    @BindView(R.id.detail_travel_list_titlebar)
    TitleBar titleBar;
    @BindView(R.id.detail_travel_list_recyclerview)
    RecyclerView recyclerView;

    private OrderBean orderBean;

    @Override
    public int getContentViewId() {
        return R.layout.activity_detail_travel_list;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            orderBean = (OrderBean) savedInstanceState.getSerializable(Constants.PARAMS_DATA);
        } else {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                orderBean = (OrderBean) bundle.getSerializable(Constants.PARAMS_DATA);
            }
        }
        if (orderBean == null || orderBean.journeyList == null) {
            finish();
        }
        initView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Constants.PARAMS_DATA, orderBean);
    }

    public void initView() {
        titleBar.setTitle(R.string.traveler_list_title);

        WrapContentLinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        HbcRecyclerSingleTypeAdpater mAdapter = new HbcRecyclerSingleTypeAdpater(this, DetailTravelItemView.class);
        recyclerView.setAdapter(mAdapter);
        List<OrderBean.JourneyItem> journeyList = orderBean.journeyList;
        int size = journeyList.size();
        for (int i = 0; i < size; i++) {
            journeyList.get(i).day = i + 1;
        }
        mAdapter.addData(orderBean.journeyList);
    }
}
