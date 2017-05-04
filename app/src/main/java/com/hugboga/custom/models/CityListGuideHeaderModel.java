package com.hugboga.custom.models;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.epoxy.EpoxyModel;
import com.hugboga.custom.R;
import com.hugboga.custom.activity.CityListActivity;
import com.hugboga.custom.activity.FilterGuideListActivity;
import com.hugboga.custom.constants.Constants;
/**
 * Created by qingcha on 17/4/19.
 */
public class CityListGuideHeaderModel extends EpoxyModel<LinearLayout> {

    private CityListActivity.Params paramsData;

    @Override
    protected int getDefaultLayout() {
        return R.layout.view_city_list_guide_header;
    }

    public CityListGuideHeaderModel(CityListActivity.Params paramsData) {
        this.paramsData = paramsData;
    }

    @Override
    public void bind(LinearLayout view) {
        super.bind(view);
        final TextView moretv = (TextView) view.findViewById(R.id.city_guide_more_tv);
        moretv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = moretv.getContext();
                Intent intent = new Intent(context, FilterGuideListActivity.class);
                if (paramsData != null) {
                    FilterGuideListActivity.Params params = new FilterGuideListActivity.Params();
                    params.id = paramsData.id;
                    params.cityHomeType = paramsData.cityHomeType;
                    params.titleName = paramsData.titleName;
                    intent.putExtra(Constants.PARAMS_DATA, params);

                    String source = "";
                    switch (paramsData.cityHomeType) {
                        case CITY:
                            source = "城市页";
                            break;
                        case ROUTE:
                            source = "国家页";
                            break;
                        case COUNTRY:
                            source = "线路圈页";
                            break;
                    }
                    intent.putExtra(Constants.PARAMS_SOURCE, source);
                }
                context.startActivity(intent);
            }
        });
    }
}