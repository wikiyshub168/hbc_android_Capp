package com.hugboga.custom.action;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.hugboga.custom.action.constants.ActionPageType;
import com.hugboga.custom.action.constants.ActionType;
import com.hugboga.custom.action.data.ActionBean;
import com.hugboga.custom.action.data.ActionSkuDetailBean;
import com.hugboga.custom.utils.CommonUtils;

/**
 * Created by qingcha on 16/7/27.
 */
public class ActionController implements ActionControllerBehavior {

    private volatile static ActionController actionController;
    private ArrayMap<Integer, Class> pageMap;

    private ActionController() {
    }

    public static ActionController getInstance() {
        if (actionController == null) {
            synchronized(ActionController.class) {
                if (actionController == null) {
                    actionController = new ActionController();
                }
            }
        }
        return actionController;
    }

    @Override
    public void doAction(Context context, final ActionBean _actionBean) {
        if (_actionBean == null) {
            return;
        }
//        if (TextUtils.isEmpty(_actionBean.source)) {
//            _actionBean.source = "外部调起";
//        }
        switch (CommonUtils.getCountInteger(_actionBean.type)) {
            case ActionType.WEB_ACTIVITY:
                if (!TextUtils.isEmpty(_actionBean.url) && _actionBean.url.contains("app/detail.html?")) {//产片要求，临时兼容商品详情
                    String goodsNo = CommonUtils.getUrlValue(_actionBean.url, "goodsNo");
                    String cityId = CommonUtils.getUrlValue(_actionBean.url, "cityId");
                    if (!TextUtils.isEmpty(goodsNo) && !TextUtils.isEmpty(cityId)) {
                        ActionSkuDetailBean actionSkuDetailBean = new ActionSkuDetailBean();
                        actionSkuDetailBean.goodsNo = goodsNo;
                        actionSkuDetailBean.cityId = cityId;
                        actionSkuDetailBean.url = _actionBean.url;
                        _actionBean.type = "" + ActionType.NATIVE_PAGE;
                        _actionBean.vcid = "" + ActionPageType.SKU_DETAIL;
                        _actionBean.data = actionSkuDetailBean;
                        doAction(context, _actionBean);
                        break;
                    }
                }
                _actionBean.type = "" + ActionType.NATIVE_PAGE;
                _actionBean.vcid = "" + ActionPageType.WEBVIEW;
                _actionBean.data = "{\"u\":\"" + _actionBean.url + "\"}";
                doAction(context, _actionBean);
                break;
            case ActionType.NATIVE_PAGE:
                if (TextUtils.isEmpty(_actionBean.vcid)) {
                    nonsupportToast();
                    break;
                }
                if (pageMap == null) {
                    pageMap = ActionMapping.getPageMap();
                }
                Class pageActionClass = pageMap.get(CommonUtils.getCountInteger(_actionBean.vcid));
                if (pageActionClass == null) {
                    nonsupportToast();
                    break;
                }
                ActionPageBase actionPageBase = null;
                try {
                    Class<ActionPageBase> cls = (Class<ActionPageBase>) Class.forName(pageActionClass.getName());
                    actionPageBase = cls.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (actionPageBase == null) {
                    nonsupportToast();
                    break;
                } else {
                    actionPageBase.intentPage(context, _actionBean);
                }
                break;
            case ActionType.FUNCTION:

                break;
            default:
                nonsupportToast();
                break;
        }
    }

    @Override
    public void handleAction(Context context, final ActionBean _actionBean) {

    }

    private void nonsupportToast() {
        CommonUtils.showToast("版本较低，请升级到最新版本，体验新功能！");
    }
}
