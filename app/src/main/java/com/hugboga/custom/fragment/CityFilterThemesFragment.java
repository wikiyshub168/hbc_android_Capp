package com.hugboga.custom.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hugboga.custom.R;
import com.hugboga.custom.data.bean.CityHomeBean;

import org.xutils.view.annotation.ContentView;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/10/22.
 */
@ContentView(R.layout.city_filiter_list_fragment)
public class CityFilterThemesFragment extends BaseFragment {

    List<CityHomeBean.GoodsThemes> goodsThemesList;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

}