package com.hugboga.custom.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.huangbaoche.hbcframe.data.net.HttpRequestUtils;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.hugboga.custom.R;
import com.hugboga.custom.adapter.CityAdapter;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.bean.UserEntity;
import com.hugboga.custom.data.bean.UserFavoriteLineList;
import com.hugboga.custom.data.bean.city.DestinationGoodsVo;
import com.hugboga.custom.data.bean.city.DestinationHomeVo;
import com.hugboga.custom.data.bean.city.PageQueryDestinationGoodsVo;
import com.hugboga.custom.data.event.EventAction;
import com.hugboga.custom.data.request.FavoriteLinesaved;
import com.hugboga.custom.data.request.RequestCity;
import com.hugboga.custom.data.request.RequestQuerySkuList;
import com.hugboga.custom.utils.CityDataTools;
import com.hugboga.custom.widget.city.CityFilterContentView;
import com.hugboga.custom.widget.city.CityFilterView;
import com.hugboga.custom.widget.city.CityHeaderView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import tk.hongbo.label.FilterView;
import tk.hongbo.label.data.LabelBean;
import tk.hongbo.label.data.LabelItemData;

import static com.hugboga.custom.activity.CityActivity.CityHomeType.COUNTRY;

public class CityActivity extends BaseActivity {

    Toolbar toolbar;

    @BindView(R.id.city_toolbar_title)
    TextView city_toolbar_title; //Toolbar标题
    @BindView(R.id.city_filter_con_view)
    CityFilterContentView filterView; //筛选弹出框

    @BindView(R.id.city_list_listview)
    RecyclerView recyclerView; //筛选线路列表

    @BindView(R.id.city_header_filter_img_root)
    CityHeaderView city_header_filter_img_root; //头部城市信息
    @BindView(R.id.content_city_filte_view1)
    FilterView content_city_filte_view1; //筛选条件内容，游玩线路
    @BindView(R.id.content_city_filte_view2)
    FilterView content_city_filte_view2; //筛选条件内容，出发城市
    @BindView(R.id.content_city_filte_view3)
    FilterView content_city_filte_view3; //筛选条件内容，游玩天数
    @BindView(R.id.city_filter_view)
    CityFilterView cityFilterView; //选择筛选项

    boolean isFromHome;
    boolean isFromDestination;

    DestinationHomeVo data; //目的地初始化数据
    CityDataTools cityDataTools;

    LabelBean labelBeanTag; //筛选项游玩线路标签
    LabelBean labelBeanCity; //筛选项出发城市
    LabelBean labelBeanDay; //筛选项游玩天数

    List<LabelItemData> labels; //标签初始化数据
    CityAdapter adapter;
    private int page = 1; //sku页数

    @Override
    public int getContentViewId() {
        return R.layout.activity_city;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.topbar_back);
        if (savedInstanceState != null) {
            paramsData = (CityActivity.Params) savedInstanceState.getSerializable(Constants.PARAMS_DATA);
        } else {
            Bundle bundle = this.getIntent().getExtras();
            if (bundle != null) {
                paramsData = (CityActivity.Params) bundle.getSerializable(Constants.PARAMS_DATA);
            }
        }
        cityDataTools = new CityDataTools();
        EventBus.getDefault().register(this);
        isFromHome = getIntent().getBooleanExtra("isFromHome", false);
        isFromDestination = getIntent().getBooleanExtra("isFromDestination", false);

        //监听筛选项变化
        cityFilterView.setFilterSeeListener(filterSeeListener);

        //初始化首页内容
        RequestCity requestCity = new RequestCity(this, paramsData.id, paramsData.cityHomeType.getType());
        HttpRequestUtils.request(this, requestCity, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this)); //展示线路数据
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                onScrollFloat(dy);
                if (!recyclerView.canScrollVertically(1) && dy > 0) {
                    // 滚动到底部加载更多
                    page++;
                    flushSkuList();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition() ==
                            recyclerView.getLayoutManager().getItemCount() - 1) {
                        resetBannerUI(0 - toolbar.getHeight());
                        resetFilterUI(0);
                    }
                }
            }
        });
    }

    private void resetBannerUI(int top) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(toolbar.getLayoutParams());
        layoutParams.setMargins(0, top, layoutParams.width, top + adapter.cityFilterModel.cityFilterView.getHeight());
        toolbar.setLayoutParams(layoutParams);
    }

    private void resetFilterUI(int top) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(filterView.getLayoutParams());
        layoutParams.setMargins(0, top, layoutParams.width, top + filterView.getHeight());
        filterView.setLayoutParams(layoutParams);
    }

    private void onScrollFloat(int dy) {
        if (dy < 0) {
            if (toolbar.getTop() < 0) {
                int newTop = toolbar.getTop() + Math.abs(dy);
                if (newTop > 0) {
                    newTop = 0;
                }
                resetBannerUI(newTop);
                int newFilterTop = newTop + toolbar.getHeight();
                if (filterView.getTop() < newFilterTop) {
                    resetFilterUI(newFilterTop);
                }
            } else {
                if (adapter.cityFilterModel.cityFilterView != null) {
                    int bannerTop = adapter.cityFilterModel.cityFilterView.getTop();
                    if (bannerTop > filterView.getTop()) {
                        resetFilterUI(0 - Math.abs(filterView.getTop()));
                    }
                }
            }
        } else if (dy > 0) {
            //向上滑动
            if (toolbar.getBottom() > 0) {
                if (adapter.cityFilterModel.cityFilterView != null) {
                    int bannerTop = adapter.cityFilterModel.cityFilterView.getTop();
                    if (bannerTop < toolbar.getBottom()) {
                        //滑动退出toolbar
                        resetBannerUI(bannerTop - toolbar.getHeight());
                        resetFilterUI(toolbar.getBottom());
                    } else {
//                                if (toolbar.getBottom() < 0) {
//                                    resetFilterUI(0);
//                                }
                    }
                }
            } else {
                resetFilterUI(0);
            }
        }
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

    /**
     * 设置筛选项数据
     */
    private void flushFilterData(DestinationHomeVo data) {
        //游玩线路数据
        labels = cityDataTools.getTagData(data.destinationTagGroupList);
        content_city_filte_view1.setData(labels, onSelectListener1);
        //出发城市数据
        content_city_filte_view2.setData(cityDataTools.getCityData(data.depCityList), onSelectListener2);
        //游玩天数数据
        content_city_filte_view3.setData(cityDataTools.getDayData(data.dayCountList), onSelectListener3);
    }

    /**
     * 游玩线路标签选中处理
     */
    FilterView.OnSelectListener onSelectListener1 = new FilterView.OnSelectResultListener() {
        @Override
        public void onParentSelect(FilterView filterView, LabelBean labelBean) {
            super.onParentSelect(filterView, labelBean);
            //关联重设，两个Tag布局内容
            if (filterView == content_city_filte_view1) {
                adapter.setSelectIds(filterView.getSelectIds());
            } else {
                content_city_filte_view1.setSelectIds(filterView.getSelectIds());
            }
            // 关联城市联动
            linkCity(labelBean);
        }

        @Override
        public void onSelect(FilterView filterView, LabelBean labelBean) {
            content_city_filte_view1.hide();
            cityFilterView.clear();
            labelBeanTag = labelBean;
            //关联重设，两个Tag布局内容
            if (filterView == content_city_filte_view1) {
                adapter.setSelectIds(filterView.getSelectIds());
            } else {
                content_city_filte_view1.setSelectIds(filterView.getSelectIds());
            }
            page = 1; //筛选条件后重置页数为首页
            flushSkuList();
            cityFilterView.setTextTag(labelBean.name);
            // 关联城市联动
            linkCity(labelBean);
        }
    };

    /**
     * 关联城市联动
     *
     * @param labelBean
     */
    private void linkCity(LabelBean labelBean) {
        content_city_filte_view2.setEnableClickIds(cityDataTools.getDepCityIds(labelBean.depIdSet));
    }

    /**
     * 关联标签联动
     *
     * @param labelBean
     */
    private void linkTag(LabelBean labelBean) {
        content_city_filte_view1.setEnableClickIds(cityDataTools.getDepTagIds(data.destinationTagGroupList, labelBean.id));
    }

    /**
     * 出发城市选中处理
     */
    FilterView.OnSelectListener onSelectListener2 = new FilterView.OnSelectResultListener() {
        @Override
        public void onSelect(FilterView filterView, LabelBean labelBean) {
            content_city_filte_view2.hide();
            cityFilterView.clear();
            labelBeanCity = labelBean;
            page = 1; //筛选条件后重置页数为首页
            flushSkuList();
            cityFilterView.setTextCity(labelBean.name);
            linkTag(labelBean);
        }
    };

    /**
     * 游玩天数选中处理
     */
    FilterView.OnSelectListener onSelectListener3 = new FilterView.OnSelectResultListener() {
        @Override
        public void onSelect(FilterView filterView, LabelBean labelBean) {
            cityFilterView.clear();
            content_city_filte_view3.hide();
            labelBeanDay = labelBean;
            page = 1; //筛选条件后重置页数为首页
            flushSkuList();
            cityFilterView.setTextDay(labelBean.name);
        }
    };

    /**
     * 根据条件筛选玩法
     */
    private void flushSkuList() {
        RequestQuerySkuList requestQuerySkuList = new RequestQuerySkuList(this, paramsData.id,
                paramsData.cityHomeType.getType(), labelBeanDay != null ? labelBeanDay.id : "0",
                labelBeanTag != null ? labelBeanTag.id : "0", labelBeanCity != null ? labelBeanCity.id : "0", page);
        HttpRequestUtils.request(this, requestQuerySkuList, this, page == 1);
    }

    @OnClick({R.id.city_toolbar_title})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.city_toolbar_title:
                //点击标题
                Intent intent = new Intent(this, ChooseCityNewActivity.class);
                intent.putExtra("com.hugboga.custom.home.flush", Constants.BUSINESS_TYPE_RECOMMEND);
                intent.putExtra("isHomeIn", false);
                intent.putExtra("source", getEventSource());
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_city, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Snackbar.make(toolbar, "点击了联系方式按钮！！！", Snackbar.LENGTH_SHORT).show();
        } else if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    CityFilterView.FilterSeeListener filterSeeListener = new CityFilterView.FilterSeeListener() {
        @Override
        public void onShowFilter(int position, boolean isSelect) {
            clearContentCityFilteViews();
            switch (position) {
                case 0:
                    if (content_city_filte_view1 != null) {
                        content_city_filte_view1.setVisibility(isSelect ? View.VISIBLE : View.GONE);
                    }
                    break;
                case 1:
                    if (content_city_filte_view2 != null) {
                        content_city_filte_view2.setVisibility(isSelect ? View.VISIBLE : View.GONE);
                    }
                    break;
                case 2:
                    if (content_city_filte_view3 != null) {
                        content_city_filte_view3.setVisibility(isSelect ? View.VISIBLE : View.GONE);
                    }
                    break;
            }
        }
    };

    private void clearContentCityFilteViews() {
        if (content_city_filte_view1 != null) {
            content_city_filte_view1.setVisibility(View.GONE);
        }
        if (content_city_filte_view2 != null) {
            content_city_filte_view2.setVisibility(View.GONE);
        }
        if (content_city_filte_view3 != null) {
            content_city_filte_view3.setVisibility(View.GONE);
        }
    }


    /**************** Old codes *****************************/
    public static final int GUIDE_LIST_COUNT = 8;//精选司导显示的条数

    public enum CityHomeType {
        CITY(202), ROUTE(101), COUNTRY(201), ALL(0);

        int type;

        CityHomeType(int i) {
            this.type = i;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public static CityHomeType getNew(int type) {
            switch (type) {
                case 101:
                    return ROUTE;
                case 201:
                    return COUNTRY;
                case 202:
                    return CITY;
                default:
                    return ALL;
            }
        }
    }

    public static class Params implements Serializable {
        public int id;
        public CityActivity.CityHomeType cityHomeType;
        public String titleName;
    }

    public CityActivity.Params paramsData;

    public boolean isShowCity() {
        if (paramsData.cityHomeType == CityHomeType.ROUTE || paramsData.cityHomeType == COUNTRY) {
            return true;
        } else {
            return false;
        }
    }

    @Subscribe
    public void onEventMainThread(EventAction action) {
        switch (action.getType()) {
            case LINE_UPDATE_COLLECT:
                //查询已收藏线路
                queryFavoriteLineList();
                break;
            case CLICK_USER_LOGIN:
                queryFavoriteLineList();
                break;
        }
    }

    /**
     * 查询已收藏线路数据
     */
    private void queryFavoriteLineList() {
        if (UserEntity.getUser().isLogin(this)) {
            FavoriteLinesaved favoriteLinesaved = new FavoriteLinesaved(this, UserEntity.getUser().getUserId(this));
            HttpRequestUtils.request(this, favoriteLinesaved, this, false);
        }
    }

    @Override
    public void onDataRequestSucceed(BaseRequest request) {
        super.onDataRequestSucceed(request);
        if (request instanceof RequestCity) {
            //首页初始化数据
            data = ((RequestCity) request).getData();
            if (data != null) {
                //修改标题
                city_toolbar_title.setText(data.destinationName);
                if (city_header_filter_img_root != null) {
                    city_header_filter_img_root.init(this, data);
                }
                //设置标签部分
                if (data.destinationGoodsCount > 0) {
                    cityFilterView.setVisibility(View.VISIBLE);
                    flushFilterData(data);
                } else {
                    cityFilterView.setVisibility(View.GONE);
                }
                // 设置玩法列表初始化数据
                if (data.destinationGoodsList != null && data.destinationGoodsList.size() > 0) {
                    flushSkuList(data.destinationGoodsList);
                } else {
                    page = 1; //无条件查询玩法
                    flushSkuList();
                }
            }
            //初始化已收藏线路数据
            queryFavoriteLineList();
        } else if (request instanceof RequestQuerySkuList) {
            //条件筛选玩法
            PageQueryDestinationGoodsVo vo = (PageQueryDestinationGoodsVo) request.getData();
            if (vo != null) {
                flushSkuList(vo.destinationGoodsList);
            }
        } else if (request instanceof FavoriteLinesaved) {
            //查询出已收藏线路信息
            UserFavoriteLineList favoriteLine = (UserFavoriteLineList) request.getData();
            adapter.resetFavious(favoriteLine.goodsNos);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置玩法列表数据
     *
     * @param destinationGoodsList
     */
    private void flushSkuList(List<DestinationGoodsVo> destinationGoodsList) {
        if (adapter == null) {
            adapter = new CityAdapter(this, destinationGoodsList, data.serviceConfigList,
                    labels, onSelectListener1);
            recyclerView.setAdapter(adapter);
        }
        if (page == 1) {
            adapter.load(destinationGoodsList);
        } else {
            adapter.addMoreGoods(destinationGoodsList);
        }
    }

    @Override
    public String getEventSource() {
        String result = "";
        if (paramsData != null) {
            switch (paramsData.cityHomeType) {
                case CITY:
                    result = "城市";
                    break;
                case ROUTE:
                    result = "线路圈";
                    break;
                case COUNTRY:
                    result = "国家";
                    break;
            }
        }
        return result;
    }

    /**
     * 打开更多司导界面
     */
    public void clickMoreGuide() {
        Intent intent = new Intent(this, FilterGuideListActivity.class);
        if (paramsData != null) {
            FilterGuideListActivity.Params params = new FilterGuideListActivity.Params();
            params.id = paramsData.id;
            params.cityHomeType = paramsData.cityHomeType;
            params.titleName = paramsData.titleName;
            intent.putExtra(Constants.PARAMS_DATA, params);
            intent.putExtra(Constants.PARAMS_SOURCE, getEventSource());
        }
        startActivity(intent);
    }
}
