package com.hugboga.custom.widget.search;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hugboga.custom.R;
import com.hugboga.custom.activity.ChooseCityNewActivity;
import com.hugboga.custom.adapter.SearchAdapter;
import com.hugboga.custom.adapter.SearchAfterAdapter;
import com.hugboga.custom.data.bean.SearchGroupBean;
import com.hugboga.custom.utils.CityUtils;
import com.hugboga.custom.utils.SearchUtils;
import com.hugboga.custom.utils.WrapContentLinearLayoutManager;
import com.hugboga.custom.widget.MultipleTextViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 搜索历史内容
 * Created by HONGBO on 2017/12/16 10:19.
 */

public class SearchHistoryView extends LinearLayout {

    @BindView(R.id.searchHistoryTagLayout)
    RelativeLayout searchHistoryTagLayout; //历史快速选择标签区域
    @BindView(R.id.history_layout)
    LinearLayout historyLayout; //历史记录容器
    @BindView(R.id.searchHistoryOld)
    MultipleTextViewGroup searchHistoryOld; //历史搜索记录标签部分
    @BindView(R.id.searchHistoryHotitem)
    MultipleTextViewGroup searchHistoryHotitem; //热门搜索标签部分

    @BindView(R.id.searchHistoryFirstList)
    RecyclerView searchHistoryFirstList;
    @BindView(R.id.searchHistoryAfterList)
    RecyclerView searchHistoryAfterList;

    List<SearchGroupBean> listAll;
    List<SearchGroupBean> listfirst;
    List<SearchGroupBean> listAfter;

    SearchAdapter searchAdapter;
    SearchAfterAdapter searchAfterAdapter;

    ChooseCityNewActivity mActivity;

    public SearchHistoryView(Context context) {
        this(context, null);
    }

    public SearchHistoryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = inflate(context, R.layout.search_history_layout, this);
        ButterKnife.bind(this, view);
    }

    public void clearAdapter() {
        if (searchAdapter != null) {
            searchAdapter.removeModels();
        }
        if (searchAfterAdapter != null) {
            searchAfterAdapter.removeModels();
        }
    }

    public void init(ChooseCityNewActivity activity) {
        this.mActivity = activity;
        initSearchAdapter();
        initSearchAfterAdapter();
        initResetUI(); //默认展示历史标签部分，隐藏结果

        if (searchAdapter != null) {
            searchAdapter.removeModels();
        }
        if (searchAfterAdapter != null) {
            searchAfterAdapter.removeModels();
        }
    }

    public void searchText(String searchStr) {
        clearAdapter();
        if (!TextUtils.isEmpty(searchStr)) {
            showUI();
            List<SearchGroupBean> list = CityUtils.search(mActivity, searchStr);
            showLocalResult(list, searchStr); //展示关联词结果
            if (list != null && list.size() <= 0) {
                mActivity.addPoint(); //添加埋点
            }
        } else {
            initResetUI();
        }
    }

    public void showResultQuery(final String searchStr) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(searchStr)) {
                    showAfterUI(searchStr);
                    SearchUtils.addCityHistorySearch(searchStr);
                    SearchUtils.isHistory = false;
                    SearchUtils.isRecommend = false;
                }
            }
        }, 300);
    }

    /**
     * 重置为初始化搜索状态
     */
    private void initResetUI() {
        searchHistoryTagLayout.setVisibility(VISIBLE);
        searchHistoryFirstList.setVisibility(GONE);
        searchHistoryAfterList.setVisibility(GONE);
        changHistory(); //重新展示历史标签
    }

    /**
     * 展示本地关联词结果
     */
    private void showUI() {
        searchHistoryTagLayout.setVisibility(GONE);
        searchHistoryFirstList.setVisibility(VISIBLE);
        searchHistoryAfterList.setVisibility(GONE);
    }

    private void showAfterUI(String msg) {
        searchAdapter.removeModels();
        searchAfterAdapter.removeModels();
        searchHistoryAfterList.setVisibility(VISIBLE);
        searchHistoryFirstList.setVisibility(GONE);
        addAfterSearchDestinationModel(listAll, msg);
    }

    private void showLocalResult(List<SearchGroupBean> listAll, String searchStr) {
        this.listAll = listAll;
        if (listAll.size() >= 5) {
            listfirst = listAll.subList(0, 5);
        } else {
            listfirst = listAll;
        }
        if (listAll.size() >= 3) {
            listAfter = listAll.subList(0, 3);
        } else {
            listAfter = listAll;
        }
        addSearchDestinationModel(listAll, searchStr);
    }

    /**
     * 重新添加搜索内容搜索结果
     *
     * @param list
     * @param searchStr
     */
    public void addSearchDestinationModel(List<SearchGroupBean> list, String searchStr) {
        if (searchHistoryFirstList.getChildCount() > 0) {
            searchHistoryFirstList.removeAllViews();
        }
        searchAdapter.addSearchDestinationModel(getContext(), list, searchStr);
    }

    /**
     * 重新添加搜索词搜索结果
     *
     * @param list
     * @param keyword
     */
    public void addAfterSearchDestinationModel(List<SearchGroupBean> list, String keyword) {
        if (searchHistoryAfterList.getChildCount() > 0) {
            searchHistoryAfterList.removeAllViews();
        }
        searchAfterAdapter.addAfterSearchDestinationModel(getContext(), list, keyword);
    }

    public void initSearchAdapter() {
        searchAdapter = new SearchAdapter();
        WrapContentLinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(getContext());
        layoutManager.setOrientation(WrapContentLinearLayoutManager.VERTICAL);
        layoutManager.setAutoMeasureEnabled(true);
        searchHistoryFirstList.setLayoutManager(layoutManager);
        searchHistoryFirstList.setHasFixedSize(true);
        searchHistoryFirstList.setAdapter(searchAdapter);
    }

    public void initSearchAfterAdapter() {
        searchAfterAdapter = new SearchAfterAdapter();
        WrapContentLinearLayoutManager layoutManager1 = new WrapContentLinearLayoutManager(getContext());
        layoutManager1.setOrientation(WrapContentLinearLayoutManager.VERTICAL);
        layoutManager1.setAutoMeasureEnabled(true);
        searchHistoryAfterList.setLayoutManager(layoutManager1);
        searchHistoryAfterList.setHasFixedSize(true);
        searchHistoryAfterList.setAdapter(searchAfterAdapter);
    }

    /**
     * 展示热门搜索标签部分结果展示
     *
     * @param dataList
     */
    public void showHistorySearchResult(final ArrayList<String> dataList) {
        if (dataList != null && dataList.size() > 0) {
            //通过接口
            searchHistoryTagLayout.setVisibility(VISIBLE);
            searchHistoryHotitem.setVisibility(VISIBLE);
            searchHistoryHotitem.setTextViews(dataList);
            searchHistoryHotitem.setOnMultipleTVItemClickListener(new MultipleTextViewGroup.OnMultipleTVItemClickListener() {
                @Override
                public void onMultipleTVItemClick(View view, int position) {
                    List<SearchGroupBean> list = mActivity.getResulthideSoft(dataList.get(position));
                    addAfterSearchDestinationModel(list, dataList.get(position));
                    SearchUtils.addCityHistorySearch(dataList.get(position));
                    searchAdapter.removeModels();
                    searchAfterAdapter.removeModels();
                    searchHistoryTagLayout.setVisibility(GONE);
                    searchHistoryAfterList.setVisibility(VISIBLE);
                    searchHistoryFirstList.setVisibility(GONE);
                    SearchUtils.isRecommend = true;
                    SearchUtils.isHistory = false;
                }
            });
        } else {
            searchHistoryHotitem.setVisibility(GONE);
        }
    }

    /**
     * 本地查询并展示历史搜索标签
     */
    private void changHistory() {
        //倒序展示
        final List<String> dataList = showHistory(SearchUtils.getSaveHistorySearch());
        if (dataList != null && dataList.size() > 0) {
            historyLayout.setVisibility(VISIBLE);
            searchHistoryOld.setTextViews(dataList);
            searchHistoryOld.setOnMultipleTVItemClickListener(new MultipleTextViewGroup.OnMultipleTVItemClickListener() {
                @Override
                public void onMultipleTVItemClick(View view, int position) {
                    //点击事件
                    mActivity.hideSoft(dataList.get(position));
                    searchAdapter.removeModels();
                    searchAfterAdapter.removeModels();
                    searchHistoryTagLayout.setVisibility(GONE);
                    searchHistoryAfterList.setVisibility(VISIBLE);
                    searchHistoryFirstList.setVisibility(GONE);
                    List<SearchGroupBean> list = CityUtils.search((Activity) getContext(), dataList.get(position));
                    addAfterSearchDestinationModel(list, dataList.get(position));
                    SearchUtils.isHistory = true;
                    SearchUtils.isRecommend = false;
                }
            });
        } else {
            historyLayout.setVisibility(GONE);
        }
    }

    /**
     * 对历史搜索变迁进行倒序排序
     */
    private List<String> showHistory(List<String> history) {
        List<String> showHistory = new ArrayList<>();
        if (history != null && history.size() > 0) {
            for (int i = history.size() - 1; i >= 0; i--) {
                showHistory.add(history.get(i));
            }
        }
        return showHistory;
    }

    @OnClick({R.id.searchHistoryRemove})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.searchHistoryRemove:
                SearchUtils.clearHistorySearch();
                changHistory(); //删除历史标签后重新查询展示历史标签
                break;
        }
    }
}
