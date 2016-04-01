package com.hugboga.custom.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

import com.huangbaoche.hbcframe.util.MLog;
import com.huangbaoche.hbcframe.util.WXShareUtils;
import com.hugboga.custom.R;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.bean.SkuItemBean;
import com.hugboga.custom.utils.PhoneInfo;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.Serializable;
import java.util.TreeMap;

/**
 * Created by admin on 2016/3/17.
 */

@ContentView(R.layout.fg_sku_detail)
public class FgSkuDetail extends FgWebInfo {

    public static final String WEB_SKU = "web_sku";
    public static final String WEB_CITY = "web_city";
    private SkuItemBean skuItemBean;//sku详情

    @Override
    protected void initView() {
        super.initView();
        MLog.e("微信 getInstance "+WXShareUtils.getInstance(getActivity()).isInstall(false));
        getView().findViewById(R.id.header_right_btn).setVisibility(WXShareUtils.getInstance(getActivity()).isInstall(false)?View.VISIBLE:View.GONE);
        if(this.getArguments()!=null){
            skuItemBean =  (SkuItemBean)getArguments().getSerializable(WEB_SKU);
        }
    }

    @Event({R.id.header_right_btn,R.id.phone_consultation,R.id.goto_order})
    private void onClickView(View view){
        switch (view.getId()){
            case R.id.header_right_btn:
                if(skuItemBean!=null){
                    String title =skuItemBean.goodsName;
                    String content = getActivity().getString(R.string.wx_share_content);
                    String shareUrl = skuItemBean.shareURL==null?skuItemBean.skuDetailUrl:skuItemBean.shareURL;
                    shareUrl = shareUrl==null?"http://www.huangbaoche.com":shareUrl;
                    skuShare(skuItemBean.goodsPicture,title,content,shareUrl);

                }
                break;
            case R.id.phone_consultation:
                final AlertDialog.Builder callDialog = new AlertDialog.Builder(getActivity());
                callDialog.setTitle("呼叫客服");
                final String [] callItems = new String[]{Constants.CALL_NUMBER_IN,Constants.CALL_NUMBER_OUT};
                callDialog.setItems(callItems,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PhoneInfo.CallDial(getActivity(), callItems[which]);
                    }
                  });
                AlertDialog dialog = callDialog.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                dialog.show();
                break;
            case R.id.goto_order:
                Bundle bundle =new Bundle();
                if(getArguments()!=null){
                    bundle.putAll(getArguments());
                }
                startFragment(new FgSkuSubmit(),bundle);
                break;
        }
    }

    private void skuShare(String goodsPicture, final String title, final String content, final String shareUrl) {
        final AlertDialog.Builder callDialog = new AlertDialog.Builder(getActivity());
        callDialog.setTitle("分享");
        final String [] callItems = new String[]{"分享好友","分享朋友圈"};
        callDialog.setItems(callItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                WXShareUtils.getInstance(getActivity()).share(which+1, skuItemBean.goodsPicture, title, content, shareUrl);
            }
        });
        callDialog.setCancelable(true);
        callDialog.create().setCanceledOnTouchOutside(true);
        callDialog.show();
    }

}
