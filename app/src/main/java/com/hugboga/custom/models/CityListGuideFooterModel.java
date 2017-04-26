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
import com.hugboga.custom.utils.UIUtils;

/**
 * Created by qingcha on 17/4/19.
 */
public class CityListGuideFooterModel extends EpoxyModel<LinearLayout> {

    private CityListActivity.Params paramsData;

    @Override
    protected int getDefaultLayout() {
        return R.layout.home_page_footer;
    }

    public CityListGuideFooterModel(CityListActivity.Params paramsData) {
        this.paramsData = paramsData;
    }

    @Override
    public void bind(LinearLayout view) {
        super.bind(view);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, UIUtils.dip2px(50));
        view.setLayoutParams(params);
        view.setPadding(0, 0, 0, 0);
        TextView textView = (TextView) view.findViewById(R.id.home_bottom_end_tv);
        textView.setText("查看更多司导");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, FilterGuideListActivity.class);
                if (paramsData != null) {
                    FilterGuideListActivity.Params params = new FilterGuideListActivity.Params();
                    params.id = paramsData.id;
                    params.cityHomeType = paramsData.cityHomeType;
                    params.titleName = paramsData.titleName;
                    intent.putExtra(Constants.PARAMS_DATA, params);
                }
                context.startActivity(intent);
            }
        });
    }
}