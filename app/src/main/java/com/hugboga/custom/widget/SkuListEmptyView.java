package com.hugboga.custom.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hugboga.custom.R;
import com.hugboga.custom.fragment.FgOrderSelectCity;
import com.hugboga.custom.fragment.FgSkuList;
import com.hugboga.custom.utils.UIUtils;

/**
 * Created by qingcha on 16/6/28.
 */
public class SkuListEmptyView extends LinearLayout implements View.OnClickListener{

    private FgSkuList fragment;

    private ImageView customIV;
    private TextView customTV, emptyTV;

    public SkuListEmptyView(Context context) {
        this(context, null);
    }

    public SkuListEmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_sku_list_empty, this);
        customIV = (ImageView) findViewById(R.id.sku_list_empty_custom_iv);
        customTV = (TextView) findViewById(R.id.sku_list_empty_custom_tv);
        emptyTV = (TextView) findViewById(R.id.sku_list_empty_tv);

        customTV.setOnClickListener(this);

        float customIVWidth = (UIUtils.getScreenWidth() / 5.0f) * 4;
        float customIVHeight = (408 / 640.0f) * customIVWidth;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)customIVWidth, (int)customIVHeight);
        params.topMargin = UIUtils.dip2px(70);
        params.bottomMargin = UIUtils.dip2px(20);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        customIV.setLayoutParams(params);
    }

    public void setFragment(FgSkuList _fragment) {
        this.fragment = _fragment;
    }

    public void showCustomView() {
        this.setVisibility(View.VISIBLE);
        emptyTV.setVisibility(View.GONE);
        customIV.setVisibility(View.VISIBLE);
        customTV.setVisibility(View.VISIBLE);
    }

    public void requestFailure() {
        customIV.setVisibility(View.GONE);
        customTV.setVisibility(View.GONE);

        this.setVisibility(View.VISIBLE);
        emptyTV.setVisibility(View.VISIBLE);
        emptyTV.setText(R.string.data_load_error_retry);
        emptyTV.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (fragment == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.sku_list_empty_custom_tv:
                fragment.startFragment(new FgOrderSelectCity());
                break;
            case R.id.sku_list_empty_tv:
                emptyTV.setOnClickListener(null);
                emptyTV.setText("");
                fragment.sendRequest(0, true);
                break;
        }
    }
}
