package com.hugboga.custom.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hugboga.custom.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by qingcha on 16/12/16.
 */
public class ChooseCountView extends LinearLayout{

    @Bind(R.id.choose_count_subtract_tv)
    TextView subtractTV;
    @Bind(R.id.choose_count_tv)
    TextView countTV;
    @Bind(R.id.choose_count_plus_tv)
    TextView plusTV;

    private int maxCount = Integer.MAX_VALUE;
    private int minCount = 0;
    private int count;
    private boolean isCanAdd = true;
    private OnCountChangeListener listener;

    public ChooseCountView(Context context) {
        this(context, null);
    }

    public ChooseCountView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = inflate(context, R.layout.view_choose_count, this);
        ButterKnife.bind(view);
        resetUI();
    }

    @OnClick({R.id.choose_count_subtract_tv, R.id.choose_count_plus_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.choose_count_subtract_tv:
                if (count == minCount) {
                    return;
                }
                --count;
                break;
            case R.id.choose_count_plus_tv:
                if (count == maxCount || !isCanAdd) {
                    return;
                }
                ++count;
                break;
        }
        setCount(count);
    }


    public ChooseCountView setIsCanAdd(boolean isCanAdd) {
        this.isCanAdd = isCanAdd;
        resetUI();
        return this;
    }


    public ChooseCountView setMaxCount(int maxCount) {
        this.maxCount = maxCount;
        resetUI();
        return this;
    }

    public ChooseCountView setMinCount(int minCount) {
        this.minCount = minCount;
        resetUI();
        return this;
    }

    public ChooseCountView setCount(int count, boolean isCallListener) {
        if (count > maxCount) {
            this.count = maxCount;
        } else if (count < minCount) {
            this.count = minCount;
        }
        this.count = count;
        resetUI();
        countTV.setText("" + count);
        if (listener != null && isCallListener) {
            listener.onCountChange(this, count);
        }
        return this;
    }

    public ChooseCountView setCount(int count) {
        return setCount(count, true);
    }

    public int getCount() {
        return count;
    }

    private void resetUI() {
        if (count == minCount) {
            subtractTV.setTextColor(0xFFCCCCCC);
        } else {
            subtractTV.setTextColor(getContext().getResources().getColor(R.color.default_black));
        }
        if (count == maxCount || !isCanAdd) {
            plusTV.setTextColor(0xFFCCCCCC);
        } else {
            plusTV.setTextColor(getContext().getResources().getColor(R.color.default_black));
        }
    }

    public interface OnCountChangeListener {
        public void onCountChange(View view, int count);
    }

    public void setOnCountChangeListener(OnCountChangeListener listener) {
        this.listener = listener;
    }
}