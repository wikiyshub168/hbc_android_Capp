package com.hugboga.custom.widget.search;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hugboga.custom.R;
import com.hugboga.custom.utils.IntentUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 搜索区域的快速下单快捷键
 * Created by HONGBO on 2017/12/15 12:30.
 */

public class SearchShortcut extends FrameLayout {

    @BindView(R.id.search_shortcut_daily1)
    TextView search_shortcut_daily1;
    @BindView(R.id.search_shortcut_send1)
    TextView search_shortcut_send1;
    @BindView(R.id.search_shortcut_rent1)
    TextView search_shortcut_rent1;
    @BindView(R.id.search_shortcut_daily2)
    TextView search_shortcut_daily2;
    @BindView(R.id.search_shortcut_send2)
    TextView search_shortcut_send2;
    @BindView(R.id.search_shortcut_rent2)
    TextView search_shortcut_rent2;

    public SearchShortcut(@NonNull Context context) {
        this(context, null);
    }

    public SearchShortcut(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = inflate(context, R.layout.search_shortcut_layout, this);
        ButterKnife.bind(this, view);
    }

    public void init(boolean isFromTravelPurposeForm) {
        if (isFromTravelPurposeForm) {
            setVisibility(GONE);
        }
    }

    @OnClick({R.id.search_shortcut_daily1, R.id.search_shortcut_daily2, R.id.search_shortcut_send1,
            R.id.search_shortcut_send2, R.id.search_shortcut_rent1, R.id.search_shortcut_rent2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_shortcut_daily1: //按天包车游
            case R.id.search_shortcut_daily2: //按天包车游
                IntentUtils.intentCharterActivity(getContext(), getEventSource());
                break;
            case R.id.search_shortcut_send1: //接送机
            case R.id.search_shortcut_send2: //接送机
                IntentUtils.intentPickupActivity(getContext(), getEventSource());
                break;
            case R.id.search_shortcut_rent1: //单次接送
            case R.id.search_shortcut_rent2: //单次接送
                IntentUtils.intentSingleActivity(getContext(), getEventSource());
                break;
        }
    }

    public String getEventSource() {
        return getContext().getResources().getString(R.string.destiation_search);
    }
}