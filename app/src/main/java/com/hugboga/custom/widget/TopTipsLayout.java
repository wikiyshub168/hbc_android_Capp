package com.hugboga.custom.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hugboga.custom.R;
import com.hugboga.custom.data.bean.OrderBean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TopTipsLayout extends RelativeLayout implements HbcViewBehavior{

    View view;
    @BindView(R.id.img_close)
    ImageView imgClose;
    @BindView(R.id.top_text)
    TextView topText;
    @BindView(R.id.top_line)
    TextView topLine;

    public TopTipsLayout(Context context) {
        this(context,null);
    }

    public TopTipsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        view = inflate(context, R.layout.order_detail_top_item, this);
        ButterKnife.bind(this, view);
    }

    public void setText(String text){
        topText.setText(text);
    }

    public void setText(int text){
        topText.setText(text);
    }

    public void hide(){
        view.setVisibility(GONE);
    }

    @OnClick(R.id.img_close)
    public void onClick() {
        view.setVisibility(GONE);
    }

    //订单详情会执行
    @Override
    public void update(Object _data) {
        if (_data == null) {
            return;
        }
        OrderBean orderBean = (OrderBean) _data;
        if (null != orderBean) {
            if (orderBean.orderStatus.code == 1) {
                setVisibility(VISIBLE);
                setText(R.string.order_detail_top2_tips);
            } else {
                setVisibility(GONE);
            }
        } else {
            setVisibility(GONE);
        }
    }
}
