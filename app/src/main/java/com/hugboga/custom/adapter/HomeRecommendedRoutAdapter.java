package com.hugboga.custom.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hugboga.custom.data.bean.HomeCityContentVo2;
import com.hugboga.custom.data.bean.HomeCityGoodsVo;
import com.hugboga.custom.widget.HomeRecommentCityItemView;

/**
 * Created by zhangqiang on 17/8/3.
 */

public class HomeRecommendedRoutAdapter extends PagerAdapter{

    HomeCityContentVo2 homeCityContentVo2;
    Context context;
    public HomeRecommendedRoutAdapter(Context context, HomeCityContentVo2 homeCityContentVo2) {
        this.homeCityContentVo2 = homeCityContentVo2;
        this.context = context;
    }

    @Override
    public int getCount() {
        return homeCityContentVo2.cityGoodsList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        HomeRecommentCityItemView itemView = new HomeRecommentCityItemView(context);
        HomeCityGoodsVo homeCityGoodsVo = homeCityContentVo2.cityGoodsList.get(position);
        itemView.update(homeCityGoodsVo);
        container.addView(itemView, 0);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        LinearLayout view = (LinearLayout) object;
        container.removeView(view);
    }

    public void setData(HomeCityContentVo2 homeCityContentVo2) {
        this.homeCityContentVo2 = homeCityContentVo2;
        notifyDataSetChanged();
    }

}
