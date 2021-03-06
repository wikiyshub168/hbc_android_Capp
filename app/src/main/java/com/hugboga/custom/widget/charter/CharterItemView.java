package com.hugboga.custom.widget.charter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hugboga.custom.R;
import com.hugboga.custom.activity.BaseActivity;
import com.hugboga.custom.activity.ChooseCityActivity;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.bean.CityBean;
import com.hugboga.custom.data.bean.CityRouteBean;
import com.hugboga.custom.utils.CharterDataUtils;
import com.hugboga.custom.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by qingcha on 17/2/24.
 */
public class CharterItemView extends LinearLayout{

    public static final String TAG = CharterItemView.class.getSimpleName();

    @BindView(R.id.charter_item_root_layout)
    RelativeLayout rootLayout;
    @BindView(R.id.charter_item_selected_iv)
    ImageView selectedIV;
    @BindView(R.id.charter_item_bottom_space_view)
    View bottomSpaceView;

    @BindView(R.id.charter_item_title_tv)
    TextView titleTV;
    @BindView(R.id.charter_item_scope_tv)
    TextView scopeTV;
    @BindView(R.id.charter_item_places_tv)
    TextView placesTV;
    @BindView(R.id.charter_item_time_tv)
    TextView timeTV;
    @BindView(R.id.charter_item_distance_tv)
    TextView distanceTV;
    @BindView(R.id.charter_item_tag_layout)
    LinearLayout tagLayout;

    @BindView(R.id.charter_item_edit_arrived_city_layout)
    RelativeLayout editArrivedCityLayout;
    @BindView(R.id.charter_item_edit_arrived_city_tv)
    TextView editArrivedCityTV;
    @BindView(R.id.charter_item_add_arrived_city_layout)
    LinearLayout addArrivedCityLayout;

    private Context context;
    private CharterDataUtils charterDataUtils;
    private CityRouteBean.CityRouteScope cityRouteScope;

    public CharterItemView(Context context) {
        this(context, null);
    }

    public CharterItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        View view = inflate(context, R.layout.view_charter_item, this);
        ButterKnife.bind(view);
        setPadding(UIUtils.dip2px(8), UIUtils.dip2px(5), UIUtils.dip2px(8), UIUtils.dip2px(5));
        charterDataUtils = CharterDataUtils.getInstance();
    }

    public void setCityRouteScope(CityRouteBean.CityRouteScope _cityRouteScope) {
        this.cityRouteScope = _cityRouteScope;
        if (_cityRouteScope.routeType == CityRouteBean.RouteType.AT_WILL) {
            titleTV.setText(cityRouteScope.routeTitle);
        } else {
            String titleStr = cityRouteScope.routeTitle;
            if (charterDataUtils.isFirstDay() && charterDataUtils.isSelectedPickUp && charterDataUtils.flightBean != null) {
                titleStr = "接机+" + titleStr;
            } else if (charterDataUtils.isLastDay() && charterDataUtils.isSelectedSend && charterDataUtils.airPortBean != null) {
                titleStr = titleStr + "+送机";
            }
            titleTV.setText(titleStr);
            timeTV.setText(String.format("%1$s小时", "" + cityRouteScope.routeLength));
            distanceTV.setText(String.format("%1$s公里", "" + cityRouteScope.routeKms));
            if (cityRouteScope.isOpeanFence()) {
                if (cityRouteScope.routeType == CityRouteBean.RouteType.OUTTOWN) {//跨城市
                    scopeTV.setText("热门城市：" + cityRouteScope.routePlaces);
                    CityBean cityBean = charterDataUtils.getEndCityBean();
                    if (cityBean != null) {
                        editArrivedCityTV.setText("送达城市：" + cityBean.name);
                    }
                } else {
                    scopeTV.setText("范围：" + cityRouteScope.routeScope);
                    placesTV.setText("推荐景点：" + cityRouteScope.routePlaces);
                }
            } else {
                if (cityRouteScope.routeType == CityRouteBean.RouteType.OUTTOWN) {//跨城市
                    CityBean cityBean = charterDataUtils.getEndCityBean();
                    if (cityBean != null) {
                        editArrivedCityTV.setText("送达城市：" + cityBean.name);
                    }
                    scopeTV.setText("热门城市：" + cityRouteScope.routePlaces);
                } else {
                    scopeTV.setText(cityRouteScope.routeScope);
                }
            }
        }
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (cityRouteScope.isOpeanFence()) {
            tagLayout.setPadding(0, UIUtils.dip2px(8), 0, 0);
            if (cityRouteScope.routeType == CityRouteBean.RouteType.OUTTOWN) {//跨城市
                bottomSpaceView.setVisibility(View.VISIBLE);
                tagLayout.setVisibility(View.VISIBLE);
                placesTV.setVisibility(View.GONE);

                if (!TextUtils.isEmpty(cityRouteScope.routePlaces)) {
                    scopeTV.setVisibility(View.VISIBLE);
                    tagLayout.setPadding(0, UIUtils.dip2px(8), 0, 0);
                } else {
                    scopeTV.setVisibility(View.GONE);
                    tagLayout.setPadding(0, 0, 0, 0);
                }

                if (selected) {
                    CityBean cityBean = charterDataUtils.getEndCityBean();
                    if (cityBean == null) {
                        addArrivedCityLayout.setVisibility(View.VISIBLE);
                        editArrivedCityLayout.setVisibility(View.GONE);
                    } else {
                        addArrivedCityLayout.setVisibility(View.GONE);
                        editArrivedCityLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    addArrivedCityLayout.setVisibility(View.GONE);
                    editArrivedCityLayout.setVisibility(View.GONE);
                }
            } else if (cityRouteScope.routeType == CityRouteBean.RouteType.AT_WILL) {//随便转转，不包车
                scopeTV.setVisibility(View.GONE);
                placesTV.setVisibility(View.GONE);
                tagLayout.setVisibility(View.GONE);
                addArrivedCityLayout.setVisibility(View.GONE);
                editArrivedCityLayout.setVisibility(View.GONE);
                bottomSpaceView.setVisibility(View.GONE);
            } else {
                bottomSpaceView.setVisibility(View.VISIBLE);
                tagLayout.setVisibility(View.VISIBLE);
                addArrivedCityLayout.setVisibility(View.GONE);
                editArrivedCityLayout.setVisibility(View.GONE);

                if (!TextUtils.isEmpty(cityRouteScope.routeScope)) {
                    scopeTV.setVisibility(View.VISIBLE);
                    tagLayout.setPadding(0, UIUtils.dip2px(8), 0, 0);
                } else {
                    scopeTV.setVisibility(View.GONE);
                    tagLayout.setPadding(0, 0, 0, 0);
                }

                if (selected && !TextUtils.isEmpty(cityRouteScope.routePlaces)) {
                    placesTV.setVisibility(View.VISIBLE);
                } else {
                    placesTV.setVisibility(View.GONE);
                }
            }
        } else {
            if (cityRouteScope.routeType == CityRouteBean.RouteType.OUTTOWN) {//跨城市
                bottomSpaceView.setVisibility(View.VISIBLE);
                tagLayout.setVisibility(View.VISIBLE);
                placesTV.setVisibility(View.GONE);
                if (selected) {
                    CityBean cityBean = charterDataUtils.getEndCityBean();
                    if (cityBean == null) {
                        addArrivedCityLayout.setVisibility(View.VISIBLE);
                        editArrivedCityLayout.setVisibility(View.GONE);
                    } else {
                        addArrivedCityLayout.setVisibility(View.GONE);
                        editArrivedCityLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    addArrivedCityLayout.setVisibility(View.GONE);
                    editArrivedCityLayout.setVisibility(View.GONE);
                }
                if (!TextUtils.isEmpty(cityRouteScope.routePlaces)) {
                    scopeTV.setVisibility(View.VISIBLE);
                    tagLayout.setPadding(0, UIUtils.dip2px(8), 0, 0);
                } else {
                    scopeTV.setVisibility(View.GONE);
                    tagLayout.setPadding(0, 0, 0, 0);
                }
            } else if (cityRouteScope.routeType == CityRouteBean.RouteType.AT_WILL) {//随便转转，不包车
                scopeTV.setVisibility(View.GONE);
                placesTV.setVisibility(View.GONE);
                tagLayout.setVisibility(View.GONE);
                addArrivedCityLayout.setVisibility(View.GONE);
                editArrivedCityLayout.setVisibility(View.GONE);
                bottomSpaceView.setVisibility(View.GONE);
            } else {
                bottomSpaceView.setVisibility(View.VISIBLE);
                tagLayout.setVisibility(View.VISIBLE);
                addArrivedCityLayout.setVisibility(View.GONE);
                editArrivedCityLayout.setVisibility(View.GONE);
                placesTV.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(cityRouteScope.routeScope)) {
                    scopeTV.setVisibility(View.VISIBLE);
                    tagLayout.setPadding(0, UIUtils.dip2px(8), 0, 0);
                } else {
                    scopeTV.setVisibility(View.GONE);
                    tagLayout.setPadding(0, 0, 0, 0);
                }
            }
        }
        setBackgroundChange();
    }

    @OnClick({R.id.charter_item_edit_arrived_city_layout,
            R.id.charter_item_add_arrived_city_layout})
    public void intentPoiSearch() {
        if (charterDataUtils == null) {
            return;
        }
        CityBean cityBean = charterDataUtils.getCurrentDayStartCityBean();
        Intent intent = new Intent(getContext(), ChooseCityActivity.class);
        if (context instanceof BaseActivity) {
            intent.putExtra(Constants.PARAMS_SOURCE, ((BaseActivity) context).getEventSource());
        }
        intent.putExtra(ChooseCityActivity.KEY_BUSINESS_TYPE, Constants.BUSINESS_TYPE_DAILY);
        intent.putExtra(ChooseCityActivity.KEY_CITY_ID, cityBean.cityId);
        intent.putExtra(ChooseCityActivity.KEY_FROM_TAG, CharterItemView.TAG);
        intent.putExtra(ChooseCityActivity.KEY_FROM, ChooseCityActivity.GROUP_OUTTOWN);
        intent.putExtra(ChooseCityActivity.KEY_SHOW_TYPE, ChooseCityActivity.ShowType.GROUP_OUTTOWN);
        getContext().startActivity(intent);
    }

    public void setBackgroundChange() {
        if (isSelected()) {
            rootLayout.setBackgroundResource(R.drawable.shape_rounded_charter_selected);
            selectedIV.setVisibility(View.VISIBLE);
        } else {
            rootLayout.setBackgroundResource(R.drawable.shape_rounded_charter_unselected);
            selectedIV.setVisibility(View.GONE);
        }
    }

}
