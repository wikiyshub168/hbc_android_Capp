package com.hugboga.custom.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hugboga.custom.R;
import com.hugboga.custom.data.bean.GoodsFilterBean;
import com.hugboga.custom.data.event.EventAction;
import com.hugboga.custom.data.event.EventType;
import com.hugboga.custom.utils.FilterTagUtils;
import com.hugboga.custom.widget.FilterTagGroupBase;
import com.hugboga.custom.widget.TagGroup;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class SkuScopeFilterFragment extends BaseFragment implements TagGroup.OnTagItemClickListener {

    @BindView(R.id.sku_filter_day_one_tv)
    TextView dayOneTV;
    @BindView(R.id.sku_filter_day_two_tv)
    TextView dayTwoTV;
    @BindView(R.id.sku_filter_day_multi_tv)
    TextView dayMultiTV;

    @BindView(R.id.sku_filter_theme_title_tv)
    TextView themeTitleTV;
    @BindView(R.id.sku_filter_theme_taggroup)
    FilterTagGroupBase themeTagGroup;
    @BindView(R.id.sku_filter_theme_line_view)
    View lineView;

    private SkuFilterBean skuFilterBean;
    private SkuFilterBean skuFilterBeanCache;
    private ArrayList<GoodsFilterBean.FilterTheme> themeList;
    private String dayTypes;

    @Override
    public int getContentViewId() {
        return R.layout.fragment_sku_scope_filter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    protected void initView() {
        themeTagGroup.setOnTagItemClickListener(this);
        skuFilterBean = new SkuFilterBean();
        skuFilterBeanCache = new SkuFilterBean();
        setThemeList(themeList);
        setDayTypes(dayTypes);
    }

    @OnClick({R.id.sku_filter_day_one_tv, R.id.sku_filter_day_two_tv, R.id.sku_filter_day_multi_tv})
    public void onSelectDay(View view) {
        switch (view.getId()) {
            case R.id.sku_filter_day_one_tv:
                skuFilterBean.dayOne = !dayOneTV.isSelected();
                setDatViewSelected(dayOneTV, skuFilterBean.dayOne);
                break;
            case R.id.sku_filter_day_two_tv:
                skuFilterBean.dayTwo = !dayTwoTV.isSelected();
                setDatViewSelected(dayTwoTV, skuFilterBean.dayTwo);
                break;
            case R.id.sku_filter_day_multi_tv:
                skuFilterBean.dayMulti = !dayMultiTV.isSelected();
                setDatViewSelected(dayMultiTV, skuFilterBean.dayMulti);
                break;
        }
        skuFilterBean.isInitial = false;
        skuFilterBean.isSave = false;
    }

    @Override
    public void onTagClick(View view, int position) {
        themeTagGroup.setViewSelected((TextView) view, !view.isSelected());
        skuFilterBean.isInitial = false;
        skuFilterBean.isSave = false;
    }

    @OnClick({R.id.sku_filter_reset_tv, R.id.sku_filter_confirm_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sku_filter_reset_tv:
                skuFilterBean.reset();
                updateUI(skuFilterBean);
                skuFilterBean.isSave = false;
                break;
            case R.id.sku_filter_confirm_tv:
                skuFilterBean.themeList = themeTagGroup.getList();
                skuFilterBeanCache = (SkuFilterBean) skuFilterBean.clone();
                skuFilterBean.isSave = true;
                EventBus.getDefault().post(new EventAction(EventType.SKU_FILTER_SCOPE, skuFilterBeanCache));
                break;
        }
    }

    public void setDatViewSelected(TextView view, boolean isSelected) {
        view.setSelected(isSelected);
        view.setTextColor(isSelected ? getContext().getResources().getColor(R.color.default_yellow) : 0xFF8A8A8A);
    }

    public void resetAllFilterBean() {
        skuFilterBean.reset();
        skuFilterBeanCache.reset();
        updateUI(skuFilterBean);
    }

    public void resetCacheFilter() {
        if (!skuFilterBean.isSave) {
            skuFilterBean = (SkuFilterBean) skuFilterBeanCache.clone();
            updateUI(skuFilterBean);
        }
    }

    public void updateUI(SkuFilterBean skuFilterBean) {
        setDatViewSelected(dayOneTV, skuFilterBean.dayOne);
        setDatViewSelected(dayTwoTV, skuFilterBean.dayTwo);
        setDatViewSelected(dayMultiTV, skuFilterBean.dayMulti);
        updateThemeViews(skuFilterBean.themeList);
    }

    public void setThemeList(ArrayList<GoodsFilterBean.FilterTheme> themeList) {
        if (themeTitleTV != null) {
            updateThemeViews(themeList);
            if (skuFilterBean != null) {
                skuFilterBean.themeList = themeList;
                skuFilterBeanCache.themeList = themeList;
            }
        } else {
            this.themeList = themeList;
        }
    }

    public void setDayTypes(String dayTypes) {
        if (themeTitleTV != null) {
            updateDayTypeViews(dayTypes);
        } else {
            this.dayTypes = dayTypes;
        }
    }

    public void updateDayTypeViews(String dayTypes) {
        if (TextUtils.isEmpty(dayTypes)) {
            return;
        }
        String[] dayType = dayTypes.split(",");
        for (int i = 0; i < dayType.length; i++) {
            if ("1".equals(dayType[i])) {
                skuFilterBean.dayOne = true;
                skuFilterBeanCache.dayOne = true;
            } else if ("2".equals(dayType[i])) {
                skuFilterBean.dayTwo = true;
                skuFilterBeanCache.dayTwo = true;
            } else if ("-1".equals(dayType[i])) {
                skuFilterBean.dayMulti = true;
                skuFilterBeanCache.dayTwo = true;
            }
        }
        updateUI(skuFilterBean);
    }

    public void updateThemeViews(ArrayList<GoodsFilterBean.FilterTheme> _themeList) {
        if (_themeList != null && _themeList.size() > 0) {
            themeTitleTV.setVisibility(View.VISIBLE);
            lineView.setVisibility(View.VISIBLE);
            themeTagGroup.setVisibility(View.VISIBLE);
        } else {
            themeTitleTV.setVisibility(View.GONE);
            lineView.setVisibility(View.GONE);
            themeTagGroup.setVisibility(View.GONE);
        }
        themeTagGroup.setData(_themeList);
    }

    public static class SkuFilterBean implements Serializable, Cloneable {

        public boolean dayOne;
        public boolean dayTwo;
        public boolean dayMulti;
        public ArrayList<GoodsFilterBean.FilterTheme> themeList;

        public boolean isInitial = true;
        public boolean isSave = false;

        public SkuFilterBean() {
            themeList = new ArrayList<>();
            reset();
        }

        public void reset() {
            isInitial = true;

            dayOne = false;
            dayTwo = false;
            dayMulti = false;

            FilterTagUtils.reset(themeList);
        }

        public int getOperateCount() {
            int operateCount = 0;

            if (dayOne) operateCount++;
            if (dayTwo) operateCount++;
            if (dayMulti) operateCount++;

            operateCount += FilterTagUtils.getOperateCount(themeList);
            return operateCount;
        }

        @Override
        public Object clone() {
            SkuFilterBean skuFilterBean = null;
            try {
                skuFilterBean = (SkuFilterBean) super.clone();
                skuFilterBean.themeList = new ArrayList<>();
                int size = 0;
                if (themeList != null) {
                    size = themeList.size();
                }
                for (int i = 0; i < size; i++) {
                    skuFilterBean.themeList.add((GoodsFilterBean.FilterTheme) themeList.get(i).clone());
                }
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return skuFilterBean;
        }

        public String getDayTypeParams() {
            StringBuilder stringBuilder = new StringBuilder();
            if (dayOne) {
                stringBuilder.append("1");
            }
            if (dayTwo) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(",");
                }
                stringBuilder.append("2");
            }
            if (dayMulti) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(",");
                }
                stringBuilder.append("-1");
            }
            return stringBuilder.toString();
        }

        public String getThemeIds() {
            return FilterTagUtils.getIds(themeList);
        }
    }

}
