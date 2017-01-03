package com.hugboga.custom.widget;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hugboga.custom.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by qingcha on 16/12/23.
 */
public class SkuOrderEmptyView extends LinearLayout{

    @Bind(R.id.sku_order_empty_iv)
    ImageView emptyIV;
    @Bind(R.id.sku_order_empty_hint_tv)
    TextView hintTV;
    @Bind(R.id.sku_order_empty_refresh_tv)
    TextView refreshTV;

    private OnRefreshDataListener listener;

    public SkuOrderEmptyView(Context context) {
        this(context, null);
    }

    public SkuOrderEmptyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view  = inflate(context, R.layout.view_sku_order_empty, this);
        ButterKnife.bind(view);

        refreshTV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        refreshTV.getPaint().setAntiAlias(true);
    }

    public void setEmptyVisibility(int visibility) {
        if (visibility == View.VISIBLE) {
            setVisibility(View.VISIBLE);
            emptyIV.setBackgroundResource(R.drawable.icon_sku_order_empty_car);
            hintTV.setText("很抱歉，没有找到可客服的司导，换个日期试试");
            refreshTV.setVisibility(View.GONE);
        } else {
            setVisibility(View.GONE);
        }
    }

    public void setErrorVisibility(int visibility) {
        if (visibility == View.VISIBLE) {
            setVisibility(View.VISIBLE);
            emptyIV.setBackgroundResource(R.drawable.icon_sku_order_net_error);
            hintTV.setText("似乎与网络断开，请检查网络环境");
            refreshTV.setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.sku_order_empty_refresh_tv})
    public void refreshData() {
        if (listener != null) {
            this.listener.onRefresh();
        }
    }

    public interface OnRefreshDataListener {
        public void onRefresh();
    }

    public void setOnRefreshDataListener(OnRefreshDataListener listener) {
        this.listener = listener;
    }
}