package com.hugboga.custom.adapter;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hugboga.custom.R;
import com.hugboga.custom.activity.SkuDetailActivity;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.bean.SkuItemBean;
import com.hugboga.custom.statistic.StatisticConstant;
import com.hugboga.custom.statistic.click.StatisticClickEvent;
import com.hugboga.custom.utils.Tools;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;

import java.util.List;

public class CityHotPagerAdapter extends PagerAdapter {

    private List<SkuItemBean> hotExplorations;
    private int type = 1;

    public CityHotPagerAdapter(List<SkuItemBean> hotExplorations) {
        this.hotExplorations = hotExplorations;
    }

    @Override
    public int getCount() {
        return hotExplorations == null ? 1 : hotExplorations.size() + 1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.home_hot_search_city_item, null);
        handlerViewShow(view, position);
        container.addView(view);
        return view;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(((View) object));
    }

    private void handlerViewShow(View view, final int position) {
        ImageView imageView = (ImageView) view.findViewById(R.id.home_hot_search_city_img);
        View filterView = view.findViewById(R.id.home_hot_search_city_fillter_view);
        TextView guideCountView = (TextView) view.findViewById(R.id.home_hot_search_city_bottom_text);
        TextView bottomTitle = (TextView) view.findViewById(R.id.home_hot_search_city_title);
        View bottomLayout = view.findViewById(R.id.home_hot_search_city_item_bottom_layout);
        TextView perPrice = (TextView) view.findViewById(R.id.home_hot_search_city_item_per_price);
        TextView customCount = (TextView) view.findViewById(R.id.home_hot_search_city_item_custom_count);
        View containerView = view.findViewById(R.id.home_hot_search_city_layout);

        int viewWidth = ScreenUtil.screenWidth - ScreenUtil.dip2px(40);
        containerView.getLayoutParams().width = viewWidth;
        imageView.getLayoutParams().height = viewWidth * 200 / 325;
        filterView.getLayoutParams().height = viewWidth * 200 / 325;
        if (position == getCount() - 1) {
            imageView.setImageResource(R.mipmap.home_more);
            filterView.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.GONE);
            bottomTitle.setVisibility(View.GONE);
            guideCountView.setVisibility(View.GONE);
        } else {
            SkuItemBean skuItemBean = hotExplorations.get(position);
            Tools.showImage(imageView, skuItemBean.goodsPicture, R.mipmap.home_default_route_item);
            customCount.setText(skuItemBean.guideAmount + "位中文导游可服务");
            bottomTitle.setText(skuItemBean.getGoodsName());
            guideCountView.setText(skuItemBean.saleAmount + "人已体验");

            String price = "￥" + skuItemBean.perPrice;
            String count = "/人起";
            SpannableString spannableString = new SpannableString(price + count);
            spannableString.setSpan(new AbsoluteSizeSpan(15, true), 0, price.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new AbsoluteSizeSpan(12, true), price.length(), count.length() + price.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            perPrice.setText(spannableString);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position==getCount() - 1){
                }else{
                    SkuItemBean skuItemBean1 = hotExplorations.get(position);
                    Intent intent = new Intent(v.getContext(), SkuDetailActivity.class);
                    intent.putExtra(SkuDetailActivity.WEB_SKU, skuItemBean1);
                    intent.putExtra(Constants.PARAMS_ID, skuItemBean1.goodsNo);
                    intent.putExtra(Constants.PARAMS_SOURCE, "首页线路列表");
                    v.getContext().startActivity(intent);
                    StatisticClickEvent.click(StatisticConstant.CLICK_RG, "首页");
                }
            }
        });

    }
}
