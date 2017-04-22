package com.hugboga.custom.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hugboga.custom.R;
import com.hugboga.custom.data.bean.FilterGuideBean;
import com.hugboga.custom.utils.Tools;
import com.hugboga.custom.utils.UIUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by qingcha on 17/4/14.
 */
public class ChoicenessGuideView extends LinearLayout implements HbcViewBehavior{

    @Bind(R.id.choiceness_guide_bg_iv)
    ImageView bgIV;
    @Bind(R.id.choiceness_guide_description_tv)
    TextView descTV;
    @Bind(R.id.choiceness_guide_level_tv)
    TextView levelTV;
    @Bind(R.id.choiceness_guide_name_tv)
    TextView nameTV;
    @Bind(R.id.choiceness_guide_taggroup)
    TagGroup tagGroup;
    @Bind(R.id.choiceness_guide_service_type_tv)
    TextView serviceTypeTV;

    public ChoicenessGuideView(Context context) {
        this(context, null);
    }

    public ChoicenessGuideView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = inflate(context, R.layout.view_choiceness_guide, this);
        ButterKnife.bind(view);

        int padding = getContext().getResources().getDimensionPixelOffset(R.dimen.home_view_padding_left);
        setOrientation(LinearLayout.VERTICAL);
        setBackgroundColor(0xFFFFFFFF);
        setPadding(0, padding, 0, 0);

        int imageWidth = UIUtils.getScreenWidth() - padding * 2;
        int imageHeight = (int)((400/690.0f) * imageWidth);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(imageWidth, imageHeight);
        bgIV.setLayoutParams(params);
    }

    @Override
    public void update(Object _data) {
        FilterGuideBean data = (FilterGuideBean) _data;
        Tools.showImage(bgIV, data.guideCover, R.drawable.home_guide_dafault);

        if (TextUtils.isEmpty(data.homeDesc)) {
            descTV.setVisibility(View.GONE);
        } else {
            descTV.setVisibility(View.VISIBLE);
            descTV.setText(data.homeDesc.trim());
        }
        if (data.serviceStar <= 0) {
            levelTV.setText("暂无星级");
            levelTV.setTextSize(14);
            levelTV.setBackgroundColor(0xFFD1D1D1);
        } else {
            levelTV.setText("" + data.serviceStar);
            levelTV.setTextSize(16);
            levelTV.setBackgroundColor(0xFFF9B900);
        }
        nameTV.setText(data.guideName);
        setTag(data.skillLabelNames);

        String serviceType = data.getServiceType();
        if (TextUtils.isEmpty(serviceType)) {
            serviceTypeTV.setVisibility(View.GONE);
        } else {
            serviceTypeTV.setVisibility(View.VISIBLE);
            serviceTypeTV.setText(serviceType);
        }
    }

    private void setTag(String tagsStr) {
        if (TextUtils.isEmpty(tagsStr)) {
            tagGroup.setVisibility(View.GONE);
            return;
        }
        String[] tagList = tagsStr.split(",");
        if (tagList != null && tagList.length > 0) {
            tagGroup.setVisibility(View.VISIBLE);
            final int labelsSize = tagList.length;
            ArrayList<View> viewList = new ArrayList<View>(labelsSize);
            for (int i = 0; i < labelsSize; i++) {
                String tag = tagList[i];
                if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(tag.trim())) {
                    continue;
                }
                tag = tag.trim();
                if (i < tagGroup.getChildCount()) {
                    LinearLayout tagLayout = (LinearLayout)tagGroup.getChildAt(i);
                    tagLayout.setVisibility(View.VISIBLE);
                    TextView tagTV = (TextView)tagLayout.findViewWithTag("tagTV");
                    tagTV.setText(tag);
                } else {
                    viewList.add(getNewTagView(tag));
                }
            }
            for (int j = labelsSize; j < tagGroup.getChildCount(); j++) {
                tagGroup.getChildAt(j).setVisibility(View.GONE);
            }
            tagGroup.setTags(viewList, tagGroup.getChildCount() <= 0);
        } else {
            tagGroup.setVisibility(View.GONE);
        }
    }

    private LinearLayout getNewTagView(String label) {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        ImageView imageView = new ImageView(getContext());
        imageView.setBackgroundResource(R.mipmap.personal_label);
        linearLayout.addView(imageView);
        imageView.getLayoutParams().height = UIUtils.dip2px(12);
        imageView.getLayoutParams().width = UIUtils.dip2px(12);

        TextView tagTV = new TextView(getContext());
        tagTV.setPadding(UIUtils.dip2px(2), 0, 0, UIUtils.dip2px(3));
        tagTV.setTextSize(13);
        tagTV.setTextColor(0xFFF9B900);
        tagTV.setEnabled(false);
        tagTV.setText(label);
        tagTV.setTag("tagTV");
        linearLayout.addView(tagTV);

        return linearLayout;
    }

}
