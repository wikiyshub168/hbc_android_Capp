package com.hugboga.custom.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hugboga.custom.R;
import com.hugboga.custom.data.net.UrlLibs;
import com.hugboga.custom.utils.CommonUtils;
import com.hugboga.custom.utils.UIUtils;
import com.hugboga.custom.widget.CsDialog;

import butterknife.BindView;
import butterknife.OnClick;

import static com.hugboga.custom.data.net.UrlLibs.H5_CANCEL;

/**
 * Created on 16/8/6.
 */

public class ServicerCenterActivity extends BaseActivity {
    @BindView(R.id.header_left_btn)
    ImageView headerLeftBtn;
    @BindView(R.id.header_right_btn)
    ImageView headerRightBtn;
    @BindView(R.id.header_title)
    TextView headerTitle;
    @BindView(R.id.header_right_txt)
    TextView headerRightTxt;

    CsDialog csDialog;
    @OnClick({R.id.servicer_center_item1, R.id.servicer_center_item2, R.id.servicer_center_item3, R.id.servicer_center_item4, R.id.servicer_center_item5, R.id.servicer_center_item6})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.servicer_center_item1:
                //预订须知
                toWebInfo(UrlLibs.H5_NOTICE_V2_2);
                break;
            case R.id.servicer_center_item2:
                //服务承诺
                toWebInfo(UrlLibs.H5_SERVICE);
                break;
            case R.id.servicer_center_item3:
                //订单取消规则
                toWebInfo(H5_CANCEL);
                break;
            case R.id.servicer_center_item4:
                //费用说明
                toWebInfo(UrlLibs.H5_PRICE_V2_2);
                break;
            case R.id.servicer_center_item5:
                //常见问题
                toWebInfo(UrlLibs.H5_PROBLEM);
                break;
            case R.id.servicer_center_item6:
                //保险说明
                toWebInfo(UrlLibs.H5_INSURANCE);
                break;
            default:
                break;
        }
    }

    /**
     * 打开网页
     *
     * @param url
     */
    private void toWebInfo(String url) {
        Intent intent = new Intent(activity, WebInfoActivity.class);
        intent.putExtra(WebInfoActivity.WEB_URL, url);
        intent.putExtra(WebInfoActivity.CONTACT_SERVICE, true);
        startActivity(intent);
    }


    protected void initHeader() {
        headerTitle.setText(R.string.servicer_title);
        headerLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RelativeLayout.LayoutParams headerRightImageParams = new RelativeLayout.LayoutParams(UIUtils.dip2px(38), UIUtils.dip2px(38));
        headerRightImageParams.rightMargin = UIUtils.dip2px(18);
        headerRightImageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        headerRightImageParams.addRule(RelativeLayout.CENTER_VERTICAL);
        headerRightBtn.setLayoutParams(headerRightImageParams);
        headerRightBtn.setPadding(0,0,0,0);
        headerRightBtn.setImageResource(R.mipmap.topbar_cs);
        headerRightBtn.setVisibility(View.VISIBLE);
        headerRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DialogUtil.getInstance(activity).showDefaultServiceDialog(activity,getEventSource());
                //CommonUtils.csDialog(activity,null,null,null,UnicornServiceActivity.SourceType.TYPE_DEFAULT,getEventSource());
                csDialog = CommonUtils.csDialog(activity, null, null, null, UnicornServiceActivity.SourceType.TYPE_DEFAULT, getEventSource(), new CsDialog.OnCsListener() {
                    @Override
                    public void onCs() {
                        if(csDialog != null && csDialog.isShowing()){
                            csDialog.dismiss();
                        }
                    }
                });
            }
        });


    }

    @Override
    public int getContentViewId() {
        return R.layout.fg_servicer_center;
    }

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initHeader();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public String getEventSource() {
        return "服务规则";
    }
}
