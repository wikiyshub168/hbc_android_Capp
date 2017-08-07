package com.hugboga.custom.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hugboga.custom.R;
import com.hugboga.custom.data.bean.GuidesDetailData;
import com.hugboga.custom.data.bean.SkuItemBean;
import com.hugboga.custom.utils.DateUtils;
import com.hugboga.custom.utils.Tools;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by qingcha on 16/12/16.
 */
public class SkuOrderDescriptionView extends LinearLayout{

    @Bind(R.id.sku_order_itinerary_iv)
    ImageView itineraryIV;
    @Bind(R.id.sku_order_itinerary_title_tv)
    TextView titleTV;

    @Bind(R.id.sku_order_itinerary_start_date_tv)
    TextView startDateTV;
    @Bind(R.id.sku_order_itinerary_end_date_tv)
    TextView endDateTV;
    @Bind(R.id.sku_order_itinerary_guide_name_tv)
    TextView guideNameTV;

    public SkuOrderDescriptionView(Context context) {
        this(context, null);
    }

    public SkuOrderDescriptionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = inflate(context, R.layout.view_sku_order_description, this);
        ButterKnife.bind(view);
    }

    public void update(SkuItemBean skuItemBean, String serverDate, GuidesDetailData guidesDetailData) {

        Tools.showImage(itineraryIV, skuItemBean.goodsPicture);

        titleTV.setText(skuItemBean.goodsName);

        startDateTV.setText(String.format("出行日期：%1$s（当地时间）", serverDate));
        endDateTV.setText(String.format("起止日期：%1$s至%2$s", serverDate, DateUtils.getDay(serverDate, skuItemBean.daysCount - 1)));

        if (guidesDetailData != null) {
            guideNameTV.setVisibility(View.VISIBLE);
            guideNameTV.setText("已选司导：" + guidesDetailData.guideName);
        }
    }
}
