package com.hugboga.custom.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huangbaoche.hbcframe.data.net.ExceptionInfo;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.hugboga.custom.R;
import com.hugboga.custom.adapter.HbcRecyclerSingleTypeAdpater;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.bean.CityBean;
import com.hugboga.custom.data.bean.GoodsFilterBean;
import com.hugboga.custom.data.bean.SkuItemBean;
import com.hugboga.custom.data.event.EventAction;
import com.hugboga.custom.data.request.RequestGoodsFilter;
import com.hugboga.custom.fragment.SkuScopeFilterFragment;
import com.hugboga.custom.utils.DatabaseManager;
import com.hugboga.custom.utils.IntentUtils;
import com.hugboga.custom.utils.WrapContentLinearLayoutManager;
import com.hugboga.custom.widget.HbcLoadingMoreFooter;
import com.hugboga.custom.widget.SkuFilterLayout;
import com.hugboga.custom.widget.SkuItemView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;

public class FilterSkuListActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    @BindView(R.id.filter_sku_list_filter_layout)
    SkuFilterLayout filterLayout;
    @BindView(R.id.filter_sku_list_recyclerview)
    XRecyclerView mRecyclerView;

    @BindView(R.id.filter_sku_list_empty_layout)
    LinearLayout emptyLayout;
    @BindView(R.id.filter_sku_list_empty_iv)
    ImageView emptyIV;
    @BindView(R.id.filter_sku_list_empty_hint_tv)
    TextView emptyHintTV;
    @BindView(R.id.filter_sku_list_empty_charter_layout)
    LinearLayout emptyCharterLayout;

    private FilterSkuListActivity.Params paramsData;

    private CityActivity.Params cityParams;
    private SkuScopeFilterFragment.SkuFilterBean skuFilterBean;
    private boolean isThemes;

    private HbcRecyclerSingleTypeAdpater<SkuItemBean> mAdapter;
    public List<SkuItemBean> listData;

    public static class Params implements Serializable {
        public int id;
        public CityActivity.CityHomeType cityHomeType;
        public String titleName;
        public String days;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_filter_sku_list;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            paramsData = (FilterSkuListActivity.Params) savedInstanceState.getSerializable(Constants.PARAMS_DATA);
        } else {
            Bundle bundle = this.getIntent().getExtras();
            if (bundle != null) {
                paramsData = (FilterSkuListActivity.Params) bundle.getSerializable(Constants.PARAMS_DATA);
            }
        }
        EventBus.getDefault().register(this);
        initView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (paramsData != null) {
            outState.putSerializable(Constants.PARAMS_DATA, paramsData);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private boolean hideFilterView() {
        if (filterLayout.isShowFilterView()) {
            filterLayout.hideFilterView();
            return true;
        } else {
            return false;
        }
    }

    private void initView() {
        initTitleBar();

        WrapContentLinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setFootView(new HbcLoadingMoreFooter(this));
        mRecyclerView.setLoadingListener(this);
        mAdapter = new HbcRecyclerSingleTypeAdpater(this, SkuItemView.class);
        //mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        if (paramsData != null) {
            CityActivity.Params params = new CityActivity.Params();
            params.cityHomeType = paramsData.cityHomeType;
            params.id = paramsData.id;
            params.titleName = paramsData.titleName;
            filterLayout.initCityFilter(params);

            filterLayout.setDayTypes(paramsData.days);
        }
        requestGuideList(true);
    }

    public void initTitleBar() {
        initDefaultTitleBar();
        fgTitle.setText(R.string.filter_sku_list_title);
        fgRightTV.setVisibility(View.GONE);
    }

    @Subscribe
    public void onEventMainThread(EventAction action) {
        switch (action.getType()) {
            case GUIDE_FILTER_CITY:
                if (action.getData() instanceof CityActivity.Params) {
                    paramsData = null;
                    skuFilterBean = null;
                    cityParams = (CityActivity.Params) action.getData();
                    filterLayout.setCityParams(cityParams);
                    requestGuideList(true);
                }
                break;
            case SKU_FILTER_SCOPE:
                if (action.getData() instanceof SkuScopeFilterFragment.SkuFilterBean) {
                    skuFilterBean = (SkuScopeFilterFragment.SkuFilterBean) action.getData();
                    filterLayout.setSkuFilterBean(skuFilterBean);
                    requestGuideList(false);
                }
                break;
            case FILTER_CLOSE:
                filterLayout.hideFilterView();
                break;
            case LINE_UPDATE_COLLECT:
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onRefresh() {
        requestGuideList(isThemes, 0, false);
    }

    @Override
    public void onLoadMore() {
        requestGuideList(false, mAdapter.getListCount(), false);
    }

    @Override
    public String getEventSource() {
        return "包车线路";
    }

    public void requestGuideList(boolean isThemes) {
        requestGuideList(isThemes, 0, true);
    }

    public void requestGuideList(boolean isThemes, int offset, boolean isShowLoading) {
        CityActivity.CityHomeType cityHomeType = null;
        int id = 0;
        String themeIds = null;
        String dayTypes = null;

        if (cityParams != null) {
            cityHomeType = cityParams.cityHomeType;
            id = cityParams.id;
        } else if (paramsData != null) {
            cityHomeType = paramsData.cityHomeType;
            id = paramsData.id;
            dayTypes = paramsData.days;
        }

        if (skuFilterBean != null) {
            themeIds = skuFilterBean.getThemeIds();
            dayTypes = skuFilterBean.getDayTypeParams();
        }
        requestGuideList(cityHomeType, id, themeIds, isThemes, dayTypes, offset, isShowLoading);
    }

    public void requestGuideList(CityActivity.CityHomeType cityHomeType, int id, String themeIds, boolean isThemes, String days, int offset, boolean isShowLoading) {
        this.isThemes = isThemes;
        RequestGoodsFilter.Builder builder = new RequestGoodsFilter.Builder();
        int type = -1;//全部
        if (cityHomeType != null && id > 0) {
            switch (cityHomeType) {
                case CITY:
                    type = 3;
                    break;
                case ROUTE:
                    type = 1;
                    break;
                case COUNTRY:
                    type = 2;
                    break;
            }
            builder.id = id;
        }
        builder.type = type;
        builder.limit = Constants.DEFAULT_PAGESIZE;
        builder.offset = offset;
        builder.themeIds = themeIds;
        builder.returnThemes = isThemes;
        builder.days = days;
        requestData(new RequestGoodsFilter(this, builder), isShowLoading);
    }

    @Override
    public void onDataRequestSucceed(BaseRequest _request) {
        super.onDataRequestSucceed(_request);
        if (_request instanceof RequestGoodsFilter) {
            GoodsFilterBean goodsFilterBean = ((RequestGoodsFilter) _request).getData();
            int offset = _request.getOffset();
            if (offset == 0 && (goodsFilterBean == null || goodsFilterBean.listData == null || goodsFilterBean.listCount <= 0)) {
                setEmptyLayout(true, true);
                return;
            } else {
                setEmptyLayout(false, true);
            }
            listData = goodsFilterBean.listData;
            mAdapter.addData(listData, offset > 0);
            if (offset == 0) {
                mRecyclerView.smoothScrollToPosition(0);
            }
            if (hasThemes(_request)) {
                filterLayout.setThemeList(goodsFilterBean.themes);
            }
            mRecyclerView.refreshComplete();
            mRecyclerView.setNoMore(mAdapter.getListCount() >= goodsFilterBean.listCount);
        }
    }

    @Override
    public void onDataRequestError(ExceptionInfo errorInfo, BaseRequest request) {
        super.onDataRequestError(errorInfo, request);
        if (request instanceof RequestGoodsFilter) {
            int offset = request.getOffset();
            if (offset == 0) {
                setEmptyLayout(true, false);
            }
        }
    }

    private void setEmptyLayout(boolean isShow, boolean isDataNull) {
        emptyLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);
        if (!isShow) {
            return;
        }
        hideFilterView();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        if (isDataNull) {
            params.addRule(RelativeLayout.BELOW, R.id.filter_sku_list_filter_layout);
            emptyLayout.setLayoutParams(params);

            emptyIV.setBackgroundResource(R.drawable.empty_trip);
            emptyHintTV.setText("暂无满足当前筛选条件的线路");
            emptyCharterLayout.setVisibility(View.VISIBLE);
            emptyLayout.setOnClickListener(null);
            emptyCharterLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int cityId = -1;
                    if (cityParams != null && cityParams.cityHomeType == CityActivity.CityHomeType.CITY) {
                        cityId = cityParams.id;
                    } else if (paramsData != null && paramsData.cityHomeType == CityActivity.CityHomeType.CITY) {
                        cityId = paramsData.id;
                    }
                    CityBean cityBean = null;
                    if (cityId != -1) {
                        cityBean = DatabaseManager.getCityBean("" + cityId);
                    }
                    IntentUtils.intentCharterActivity(FilterSkuListActivity.this, null, null, cityBean, getEventSource());
                }
            });
        } else {
            params.addRule(RelativeLayout.BELOW, R.id.filter_sku_list_titlebar);
            emptyLayout.setLayoutParams(params);

            emptyIV.setBackgroundResource(R.drawable.empty_wifi);
            emptyHintTV.setText("似乎与网络断开，点击屏幕重试");
            emptyCharterLayout.setVisibility(View.GONE);
            emptyLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestGuideList(isThemes);
                }
            });
        }
    }


    public boolean hasThemes(BaseRequest request) {
        boolean returnThemes = false;
        if (request.map != null && request.map.containsKey("returnThemes") && request.map.get("returnThemes") != null) {
            Object returnThemesObj = request.map.get("returnThemes");
            if (returnThemesObj instanceof Boolean) {
                returnThemes = (Boolean) returnThemesObj;
            }
        }
        return returnThemes;
    }
}
