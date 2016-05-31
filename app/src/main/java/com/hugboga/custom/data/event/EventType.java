package com.hugboga.custom.data.event;

/**
 * event 类型定义
 */
public enum EventType {
    CLICK_USER_LOGIN,//登录
    CLICK_USER_LOOUT,//退出
    REFRESH_ORDER_DETAIL,//刷新订单详情
    REFRESH_CHAT_LIST,//刷新聊天列表
    SET_MAIN_PAGE_INDEX,//设置main 页面滚动到第几个tab
    PAY_CANCEL,//取消支付
    CLICK_HEADER_LEFT_BTN_BACK, //点击后退按钮 或者back健
    START_NEW_FRAGMENT, //startfragment
    WECHAT_LOGIN_CODE,
    EDIT_INSURE, //编辑投保人
    ADD_INSURE, //添加投保人
    EDIT_BACK_INSURE, //编辑返回
    CHECK_INSURE, //选择投保人
    ADD_INSURE_SUCCESS, //添加投保人成功
    AIR_NO,//航班号返回
    MAN_CHILD_LUUAGE, //乘客行李
    CHANGE_CAR, //选择汽车
    CHOOSE_GUIDE, //收藏选择司导
    CONTACT, //通讯录选取联系人
    CONTACT_BACK, //通讯录选取联系人返回显示

    SINGLE_TYPE, //单次接送
    DAIRY_TYPE, //包车
    PICK_SEND_TYPE, //接送机
    SINGLE_BACK, //单次接送返回
    pick_BACK, //接机返回
    SEND_BACK, //送机返回
    SELECT_COUPON_BACK, //选择优惠券返回

}
