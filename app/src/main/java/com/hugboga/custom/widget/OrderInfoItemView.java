package com.hugboga.custom.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hugboga.custom.R;
import com.hugboga.custom.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by qingcha on 17/5/17.
 */

public class OrderInfoItemView extends RelativeLayout {

    @BindView(R.id.order_info_title_tv)
    TextView titleTv;
    @BindView(R.id.order_info_desc_tv1)
    TextView descTv1;
    @BindView(R.id.order_info_desc_tv2)
    TextView descTv2;
    @BindView(R.id.order_info_desc_tv3)
    TextView descTv3;
    @BindView(R.id.order_info_bottom_line_view)
    View bottomLineView;

    private String titleText;
    private String hintText;

    public OrderInfoItemView(Context context) {
        this(context, null);
    }

    public OrderInfoItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = inflate(context, R.layout.view_order_info_item, this);
        ButterKnife.bind(view);
        setBackgroundColor(0xFFFFFFFF);
        setClipChildren(false);
        setClipToPadding(false);
        int paddingLeft = context.getResources().getDimensionPixelOffset(R.dimen.order_padding_left);
        setPadding(paddingLeft, 0, paddingLeft, 0);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.OrderInfoItemView);
        titleText = typedArray.getString(R.styleable.OrderInfoItemView_titleText);
        titleTv.setText(titleText);
        hintText = typedArray.getString(R.styleable.OrderInfoItemView_hintText);
        descTv1.setHint(hintText);
        boolean isShowBottomLine = typedArray.getBoolean(R.styleable.OrderInfoItemView_isShowBottomLine,false);
        bottomLineView.setVisibility(isShowBottomLine ? View.VISIBLE : View.GONE);
        typedArray.recycle();
    }

    public void setTitle(String _titleText) {
        titleText = _titleText;
        titleTv.setText(titleText);
    }

    public void setHintText(String _hintText) {
        hintText = _hintText;
        descTv1.setHint(hintText);
    }

    public void setDesc(String desc) {
        descTv1.setText(desc);
        descTv2.setVisibility(View.GONE);
        descTv3.setVisibility(View.GONE);
    }

    public void setDesc(String desc1, String desc2) {
        descTv1.setText(desc1);
        descTv1.setPadding(0, UIUtils.dip2px(8), 0, 0);
        descTv2.setVisibility(View.GONE);
        descTv3.setText(desc2);
        descTv3.setVisibility(View.VISIBLE);
    }

    public void setDesc(String desc1, String desc2, String desc3) {
        descTv1.setText(desc1);
        descTv1.setPadding(0, UIUtils.dip2px(8), 0, 0);
        descTv2.setText(desc2);
        descTv2.setVisibility(View.VISIBLE);
        descTv3.setText(desc3);
        descTv3.setVisibility(View.VISIBLE);
    }

    public void resetUI() {
        descTv1.setText("");
        descTv1.setHint(hintText);
        descTv1.setPadding(0, 0, 0, 0);
        descTv2.setText("");
        descTv2.setVisibility(View.GONE);
        descTv3.setText("");
        descTv3.setVisibility(View.GONE);
    }
}
