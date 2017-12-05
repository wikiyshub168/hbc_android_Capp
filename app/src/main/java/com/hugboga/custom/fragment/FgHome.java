package com.hugboga.custom.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.huangbaoche.hbcframe.data.net.ExceptionInfo;
import com.huangbaoche.hbcframe.data.net.HttpRequestUtils;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.hugboga.custom.R;
import com.hugboga.custom.activity.SearchDestinationGuideLineActivity;
import com.hugboga.custom.adapter.HomeAdapter;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.bean.HomeBean;
import com.hugboga.custom.data.bean.HomeTopBean;
import com.hugboga.custom.data.bean.UserEntity;
import com.hugboga.custom.data.bean.UserFavoriteLineList;
import com.hugboga.custom.data.event.EventAction;
import com.hugboga.custom.data.request.FavoriteGuideSaved;
import com.hugboga.custom.data.request.FavoriteLinesaved;
import com.hugboga.custom.data.request.RequestHome;
import com.hugboga.custom.data.request.RequestHomeTop;
import com.hugboga.custom.statistic.StatisticConstant;
import com.hugboga.custom.statistic.click.StatisticClickEvent;
import com.hugboga.custom.statistic.sensors.SensorsConstant;
import com.hugboga.custom.utils.UIUtils;
import com.hugboga.custom.utils.WrapContentLinearLayoutManager;
import com.hugboga.custom.widget.home.HomeRefreshHeader;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.yan.pullrefreshlayout.PullRefreshLayout;
import com.yan.pullrefreshlayout.ShowGravity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by qingcha on 17/11/22.
 */

public class FgHome extends BaseFragment {

    @BindView(R.id.home_refresh_layout)
    PullRefreshLayout refreshLayout;
    @BindView(R.id.home_list_view)
    RecyclerView homeListView;
    @BindView(R.id.homed_titlebar_ai_iv)
    ImageView titlebarAiIV;
    @BindView(R.id.home_network_layout)
    LinearLayout networkLayout;

    HomeRefreshHeader homeRefreshHeader;

    private HomeAdapter homeAdapter;
    private HomeBean homeBean;

    @Override
    public int getContentViewId() {
        return R.layout.fg_home;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        setSensorsDefaultEvent(getEventSource(), SensorsConstant.DISCOVERY);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        setSensorsViewScreenBeginEvent();
    }

    @Override
    public void onStop() {
        super.onStop();
        setSensorsViewScreenEndEvent();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initView() {
        homeAdapter = new HomeAdapter();
        WrapContentLinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(this.getActivity());
        homeListView.setLayoutManager(layoutManager);
        homeListView.setHasFixedSize(true);
        homeListView.setAdapter(homeAdapter);
        sendRequest();

        refreshLayout.setHeaderShowGravity(ShowGravity.FOLLOW);
        refreshLayout.setDragDampingRatio(0.7f);
        refreshLayout.setTwinkEnable(false);
        homeRefreshHeader = new HomeRefreshHeader(getContext(), refreshLayout);
        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                HomeRefreshHeader homeRefreshHeader = refreshLayout.getHeaderView();
                if (!homeRefreshHeader.isTwoRefresh()) {
                    refreshLayout.refreshComplete();
                }
            }

            @Override
            public void onLoading() {
            }
        });

        homeListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (homeAdapter.homeBannerModel == null || homeAdapter.homeBannerModel.itemView == null) {
                    return;
                }
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                int scrollY = Math.abs(recyclerView.getChildAt(0).getTop());
                float bannerHeight = homeAdapter.homeBannerModel.itemView.getBannerLayoutHeight();
                float bannerHalfHeight = bannerHeight / 2.0f;
                if (firstVisibleItemPosition == 0 && scrollY <= bannerHeight && scrollY >= bannerHalfHeight) {
                    float progress = ((scrollY - bannerHalfHeight) / bannerHalfHeight);
                    homeAdapter.homeAiModel.homeAIView.setProgress(progress);
                    titlebarAiIV.setVisibility(View.GONE);
                    Log.i("aa", "homeAiModel " + scrollY + "   bannerHeight " + ((scrollY - bannerHalfHeight) / bannerHalfHeight));
                } else if (firstVisibleItemPosition > 1) {
                    titlebarAiIV.setVisibility(View.VISIBLE);
                } else {
                    titlebarAiIV.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onDataRequestSucceed(BaseRequest _request) {
        super.onDataRequestSucceed(_request);
        if (_request instanceof RequestHome) {
            homeBean = ((RequestHome) _request).getData();
            homeAdapter.setData(homeBean);
            requestFavoriteLinesaved();
            RequestHomeTop requestHomeTop = new RequestHomeTop(getActivity());
            requestData(requestHomeTop);
            networkLayout.setVisibility(View.GONE);
        } else if (_request instanceof RequestHomeTop) {
            List<HomeTopBean> homeTopBeanList = ((RequestHomeTop) _request).getData();
            if (refreshLayout.getHeaderView() == null) {
                refreshLayout.setHeaderView(homeRefreshHeader);
            }
            homeRefreshHeader.update(homeTopBeanList);
        } else if (_request instanceof FavoriteLinesaved) {
            onRequestFavoriteLineSucceed(_request);
        }
    }

    @Override
    public void onDataRequestError(ExceptionInfo errorInfo, BaseRequest _request) {
        super.onDataRequestError(errorInfo, _request);
        if (_request instanceof RequestHome) {
            networkLayout.setVisibility(View.VISIBLE);
        }
    }

    private void sendRequest() {
        RequestHome requestHome = new RequestHome(getActivity());
        requestData(requestHome);
    }

    @OnClick({R.id.home_network_layout})
    public void refreshHomeData() {
        Log.i("aa", "refreshHomeData");
        sendRequest();
    }

    @OnClick({R.id.homed_titlebar_search_iv})
    public void intentSearchActivity() {
        Intent intent = new Intent(this.getContext(), SearchDestinationGuideLineActivity.class);
        intent.putExtra("com.hugboga.custom.home.flush", Constants.BUSINESS_TYPE_HOME);
        intent.putExtra("isHomeIn", true);
        intent.putExtra(Constants.PARAMS_SOURCE, getEventSource());
        this.getContext().startActivity(intent);
        StatisticClickEvent.click(StatisticConstant.SEARCH_LAUNCH, getEventSource());
    }

    @Override
    public String getEventSource() {
        return "首页";
    }

    private void setSensorsViewScreenBeginEvent() {
        try {
            SensorsDataAPI.sharedInstance(getContext()).trackTimerBegin("AppViewScreen");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSensorsViewScreenEndEvent() {
        try {
            JSONObject properties = new JSONObject();
            properties.put("pageName", getEventSource());
            properties.put("pageTitle", getEventSource());
            properties.put("refer", "");
            SensorsDataAPI.sharedInstance(getContext()).trackTimerEnd("AppViewScreen", properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onEventMainThread(EventAction action) {
        switch (action.getType()) {
            case CLICK_USER_LOGIN:
                if (homeBean != null && homeBean.hotAlbumList != null && homeBean.hotAlbumList.size() > 0) {
                    FavoriteLinesaved favoriteLinesaved = new FavoriteLinesaved(getContext(), UserEntity.getUser().getUserId(getContext()));
                    HttpRequestUtils.request(getContext(), favoriteLinesaved, this, false);
                }
                break;
            case CLICK_USER_LOOUT:
                if (homeBean != null) {
                    for (int k = 0; k < homeBean.hotAlbumList.size(); k++) {
                        for (int m = 0; m < homeBean.hotAlbumList.get(k).albumRelItemList.size(); m++) {
                            homeBean.hotAlbumList.get(k).albumRelItemList.get(m).isCollected = 0;
                        }
                    }
                    homeAdapter.notifyDataSetChanged();
                }
                break;
            case LINE_UPDATE_COLLECT:
                requestFavoriteLinesaved();
                break;
        }
    }

    // -----**  收藏功能历史遗留，我也很无奈--！，待优化 **-----
    public void onRequestFavoriteLineSucceed(BaseRequest _request) {
        if (homeBean == null) {
            return;
        }
        for (int k = 0; k < homeBean.hotAlbumList.size(); k++) {
            for (int m = 0; m < homeBean.hotAlbumList.get(k).albumRelItemList.size(); m++) {
                homeBean.hotAlbumList.get(k).albumRelItemList.get(m).isCollected = 0;
            }
        }
        //所有线路的收藏状态同步在此
        if (_request.getData() instanceof UserFavoriteLineList) {
            UserFavoriteLineList userFavoriteLineList = (UserFavoriteLineList) _request.getData();
            for (int o = 0; o < userFavoriteLineList.goodsNos.size(); o++) {
                for (int k = 0; k < homeBean.hotAlbumList.size(); k++) {
                    int itemSize = homeBean.hotAlbumList.get(k).albumRelItemList.size();
                    for (int m = 0; m < itemSize; m++) {
                        if (TextUtils.equals(userFavoriteLineList.goodsNos.get(o), homeBean.hotAlbumList.get(k).albumRelItemList.get(m).goodsNo)) {
                            homeBean.hotAlbumList.get(k).albumRelItemList.get(m).isCollected = 1;
                        }
                    }
                }
            }
            homeAdapter.notifyDataSetChanged();
        }
    }

    public void requestFavoriteLinesaved() {
        if (UserEntity.getUser().isLogin(getContext())) {
            FavoriteLinesaved favoriteLinesaved = new FavoriteLinesaved(getContext(), UserEntity.getUser().getUserId(getContext()));
            HttpRequestUtils.request(getContext(), favoriteLinesaved, this, false);
        }
    }

}
