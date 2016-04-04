package com.hugboga.custom.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huangbaoche.hbcframe.data.net.ErrorHandler;
import com.huangbaoche.hbcframe.data.net.ExceptionInfo;
import com.huangbaoche.hbcframe.data.net.HttpRequestListener;
import com.huangbaoche.hbcframe.data.net.HttpRequestUtils;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.huangbaoche.hbcframe.util.MLog;
import com.hugboga.custom.R;
import com.hugboga.custom.data.bean.ChatInfo;
import com.hugboga.custom.data.bean.OrderBean;
import com.hugboga.custom.data.event.EventAction;
import com.hugboga.custom.data.event.EventType;
import com.hugboga.custom.data.parser.ParserChatInfo;
import com.hugboga.custom.data.request.RequestIMClear;
import com.hugboga.custom.data.request.RequestIMOrder;
import com.hugboga.custom.utils.PermissionRes;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.RongIMClientWrapper;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;

@ContentView(R.layout.activity_imchat)
public class FgIMChat extends BaseFragment {

    public static final String KEY_TITLE = "key_title";

    @ViewInject(R.id.header_title)
    TextView title;
    @ViewInject(R.id.header_right_txt)
    TextView topRightBtn;
    @ViewInject(R.id.imchat_viewpage_layout)
    RelativeLayout viewPageLayout;
    @ViewInject(R.id.imchat_viewpage)
    ViewPager viewPage; //订单展示
    @ViewInject(R.id.imchat_point_layout)
    LinearLayout pointLayout; //小点容器

    public final String USER_IM_ADD = "G";
    private boolean isChat = false; //是否开启聊天
    private String userId; //用户ID
    private String imUserId; //用户ID
    private String userAvatar; //用户头像
    private String targetType; //目标类型

    private RelativeLayout view;

    @Override
    protected void initHeader() {
        fgRightBtn.setText(R.string.letter_chat_btn);
    }

    @Override
    protected void initView() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        ConversationFragment conversation = (ConversationFragment) getChildFragmentManager().findFragmentById(R.id.conversation);
        if (conversation == null) {
            return;
        }
        Uri uri = Uri.parse(bundle.getString(KEY_TITLE));
        conversation.setUri(uri);
        view = (RelativeLayout) conversation.getView();
        //刷新订单信息
        getUserInfoToOrder(uri);
        //        grantAudio(); //对音频进行授权
    }

    /**
     * 解析用户ID信息
     */
    private void getUserInfoToOrder(Uri uri) {
        String jsonStr = uri.getQueryParameter("title");
        try {
            ChatInfo imInfo = new ParserChatInfo().parseObject(new JSONObject(jsonStr));
            if (imInfo == null) return;
            isChat = imInfo.isChat;
            userId = imInfo.userId;
            imUserId = USER_IM_ADD + userId;
            userAvatar = imInfo.userAvatar;
            title.setText(imInfo.title); //设置标题
            targetType = imInfo.targetType;
            resetRightBtn();
            initRunningOrder(); //构建和该用户之间的订单
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void resetRightBtn() {
        if (!TextUtils.isEmpty(targetType) && "3".equals(targetType)) {
            topRightBtn.setVisibility(View.GONE); //显示历史订单按钮
        } else {
            topRightBtn.setVisibility(View.VISIBLE); //显示历史订单按钮
        }
    }

    /**
     * 展示司导和用户之间的订单
     */
    private void initRunningOrder() {
        clearImChat(); //进入后清空消息提示
        setUserInfo(); //设置聊天对象头像
        resetChatting(); //设置是否可以聊天
        loadImOrder(); //显示聊天订单信息
        initEmpty(); //构建空提示
    }

    @Override
    protected Callback.Cancelable requestData() {
        grantAudio(); //进行授权
        return null;
    }

    /**
     * 授权获取手机音频权限
     */
    private void grantAudio() {
        MPermissions.requestPermissions(this, PermissionRes.IM_PERMISSION, android.Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
    }

    @PermissionGrant(PermissionRes.IM_PERMISSION)
    public void requestAudioSuccess() {
    }

    @PermissionDenied(PermissionRes.IM_PERMISSION)
    public void requestAudioFailed() {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getActivity());
        dialog.setCancelable(false);
        dialog.setTitle(R.string.grant_fail_title);
        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            dialog.setMessage(R.string.grant_fail_phone1);
        } else {
            dialog.setMessage(R.string.grant_fail_im);
            dialog.setPositiveButton(R.string.grant_fail_btn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    grantAudio();
                }
            });
        }
        dialog.show();
    }

    @Override
    protected void inflateContent() {

    }

    @Override
    public void onPause() {
        notifyChatList();
        clearImChat(); //清空未读消息记录
        super.onPause();
    }

    /**
     * 设置是否可以聊天
     */
    private void resetChatting() {
        if (!isChat) {
            View view1 = view.getChildAt(0);
            view1.setVisibility(View.GONE);
            title.setText(getString(R.string.chat_log));
        }
    }

    /**
     * 设置对方头像
     */
    private void setUserInfo() {
        if (userAvatar != null && !userAvatar.equals("")) {
            UserInfo peerUser = new UserInfo(imUserId, getString(R.string.title_activity_imchat), Uri.parse(userAvatar));
            RongContext.getInstance().getUserInfoCache().put(imUserId, peerUser);
        }
    }

    /**
     * 根据数据刷新界面
     *
     * @param orders
     */
    private void flushOrderView(ArrayList<OrderBean> orders) {
        if (orders != null && orders.size() > 0) {
            //有订单数据
            viewPageLayout.setVisibility(View.VISIBLE);
            viewPage.setAdapter(new IMOrderPagerAdapter(orders));
            viewPage.addOnPageChangeListener(onPageChangeListener);
        } else {
            //无订单数据
            viewPageLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 获取构建viewPage数据
     *
     * @param datas
     * @return
     */
    private List<View> getOrderViews(ArrayList<OrderBean> datas) {
        List<View> views = new ArrayList<>();
        pointLayout.removeAllViews();
        for (OrderBean orderBean : datas) {
            View view = View.inflate(getActivity(), R.layout.im_chat_orders_item, null);
            //设置状态
            TextView textView = (TextView) view.findViewById(R.id.im_chat_orders_item_state);
            textView.setText(orderBean.orderStatus.name);
//            resetStatusColor(textView, letterOrder.status);
            //订单类型和时间
            TextView textViewtype = (TextView) view.findViewById(R.id.im_chat_orders_item_ordertime);
            textViewtype.setText(getTypeStr(orderBean));
            //订单地址1
            TextView textViewAddr1 = (TextView) view.findViewById(R.id.im_chat_orders_item_address1);
            textViewAddr1.setText(getAddr1(orderBean));
            //订单地址1
            TextView textViewAddr2 = (TextView) view.findViewById(R.id.im_chat_orders_item_address2);
            textViewAddr2.setText(getAddr2(orderBean));
            views.add(view);
            //设置红点
            View viewp = View.inflate(getActivity(), R.layout.im_chat_orders_point, null);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(3, 2, 3, 2);
            viewp.setLayoutParams(layoutParams);
            pointLayout.addView(viewp);
        }
        if (pointLayout.getChildCount() > 0) {
            pointLayout.getChildAt(0).setSelected(true);
        }
        return views;
    }

    private String getTypeStr(OrderBean orderBean) {
        StringBuilder sb = new StringBuilder();
        MLog.e("orderGoodsType =" + orderBean.orderGoodsType);
        MLog.e("getOrderTypeStr = " + orderBean.getOrderTypeStr(getActivity()));
        if (orderBean.orderGoodsType == 5) {
            sb.append("[" + orderBean.getOrderTypeStr(getActivity()) + "]");
            sb.append(orderBean.lineSubject);
        } else {
            sb.append("[" + orderBean.getOrderTypeStr(getActivity()) + "]");
        }
        return sb.toString();
    }

    private String getAddr1(OrderBean orderBean) {
        StringBuilder sb = new StringBuilder();
        if (orderBean.orderGoodsType == 1 || orderBean.orderGoodsType == 2 || orderBean.orderGoodsType == 4) {
            sb.append("出发：");
            sb.append(orderBean.startAddress);
        } else if (orderBean.orderGoodsType == 5) {
            //线路
            sb.append(orderBean.serviceTime + "至" + orderBean.serviceEndTime);
        } else {
            sb.append(orderBean.serviceCityName + "-" + orderBean.serviceEndCityName);
        }
        return sb.toString();
    }

    private String getAddr2(OrderBean orderBean) {
        StringBuilder sb = new StringBuilder();
        if (orderBean.orderGoodsType == 1 || orderBean.orderGoodsType == 2 || orderBean.orderGoodsType == 4) {
            sb.append("到达：");
            sb.append(orderBean.destAddress);
        } else if (orderBean.orderGoodsType == 5) {
            //线路
            sb.append("出发：");
            sb.append(orderBean.serviceCityName);
        } else {
            sb.append(orderBean.serviceTime + "至" + orderBean.serviceEndTime);
        }
        return sb.toString();
    }

    /**
     * 根据私信订单状态改变颜色
     *
     * @param tv
     * @param status
     */
    private void resetStatusColor(TextView tv, String status) {
        if (!TextUtils.isEmpty(status)) {
            if (getString(R.string.letter_order_state1).equals(status)) {
                tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.letter_item_order1));
            } else if (getString(R.string.letter_order_state2).equals(status)) {
                tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.letter_item_order2));
            } else if (getString(R.string.letter_order_state3).equals(status)) {
                tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.letter_item_order3));
            }
        }
    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            resetPoint(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    /**
     * 滑动小红点位置
     *
     * @param position
     */
    private void resetPoint(int position) {
        int views = pointLayout.getChildCount();
        for (int i = 0; i < views; i++) {
            pointLayout.getChildAt(i).setSelected(false);
        }
        pointLayout.getChildAt(position).setSelected(true);
    }

    class IMOrderPagerAdapter extends PagerAdapter {

        List<View> views;

        IMOrderPagerAdapter(ArrayList<OrderBean> datas) {
            this.views = getOrderViews(datas);
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position), 0);
            return views.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }
    }

    /**
     * 授权获取手机音频权限
     */
   /* private void grantAudio() {
        MPermissions.requestPermissions(IMChatActivity.this, PermissionRes.RECORD_AUDIO, android.Manifest.permission.RECORD_AUDIO);
    }

    @PermissionGrant(PermissionRes.RECORD_AUDIO)
    public void requestAudioSuccess() {
    }

    @PermissionDenied(PermissionRes.RECORD_AUDIO)
    public void requestAudioFailed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(IMChatActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle(R.string.grant_fail_title);
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.RECORD_AUDIO)) {
            dialog.setMessage(R.string.grant_fail_phone1);
        } else {
            dialog.setMessage(R.string.grant_fail_audio);
            dialog.setPositiveButton(R.string.grant_fail_btn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    grantAudio();
                }
            });
        }
        dialog.setNegativeButton(R.string.grant_fail_btn_exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                HBCApplication.getInstance().exit();
            }
        });
        dialog.show();
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.header_right_btn:
                MLog.e("进入历史订单列表");
                Bundle bundle = new Bundle();
                bundle.putInt(FgNewOrder.SEARCH_TYPE, FgNewOrder.SearchType.SEARCH_TYPE_HISTORY.getType());
                bundle.putString(FgNewOrder.SEARCH_USER, userId);
                startFragment(new FgNewOrder(), bundle);
                break;
            default:
                super.onClick(v);
                break;
        }
    }
    @Override
    public boolean onBackPressed() {
        notifyChatList();
        return false;
    }

    private void notifyChatList() {
        EventBus.getDefault().post(new EventAction(EventType.REFRESH_CHAT_LIST));
    }

    /**
     * 构建聊天为空界面
     */
    private void initEmpty() {
        //消息为空提示
        if (view == null) {
            return;
        }
        final RelativeLayout rl = (RelativeLayout) view.findViewById(io.rong.imkit.R.id.empty);
        try {
            int imNumber = 0;
            RongIM rongIM = RongIM.getInstance();
            if (rongIM != null) {
                RongIMClientWrapper rongIMClientWrapper = rongIM.getRongIMClient();
                if (rongIMClientWrapper != null) {
                    try {
                        List<Message> messageList = rongIMClientWrapper.getHistoryMessages(Conversation.ConversationType.PRIVATE, imUserId, -1, 10);
                        if (messageList != null)
                            imNumber = messageList.size();
                        if (imNumber > 0) {
                            rl.setVisibility(View.GONE);
                        } else {
                            rl.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //设置通知栏免打扰
                    rongIMClientWrapper.setConversationNotificationStatus(Conversation.ConversationType.PRIVATE, imUserId, Conversation.ConversationNotificationStatus.DO_NOT_DISTURB, new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
                        @Override
                        public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {
                            MLog.e("setConversationNotificationStatus-onSuccess");
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                            MLog.e("setConversationNotificationStatus-onError");
                        }
                    });
                }
            }
            //发送消息监控
            if (rongIM != null) {
                rongIM.setSendMessageListener(new RongIM.OnSendMessageListener() {
                    @Override
                    public Message onSend(Message message) {
                        return message;
                    }

                    @Override
                    public boolean onSent(Message message, RongIM.SentMessageErrorCode sentMessageErrorCode) {
                        if (rl.getVisibility() == View.VISIBLE) {
                            rl.setVisibility(View.GONE);
                        }
                        return false;
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清空融云消息数
     */
    private void clearImChat() {
        // 调用接口清空聊天未读信息
        RequestIMClear requestIMClear = new RequestIMClear(getActivity(), userId, targetType);
        HttpRequestUtils.request(getActivity(), requestIMClear, imClearListener);
    }

    /**
     * 刷新IM聊天订单
     */
    private void loadImOrder() {
        RequestIMOrder requestIMOrder = new RequestIMOrder(getActivity(), userId);
        HttpRequestUtils.request(getActivity(), requestIMOrder, orderListener);
    }

    HttpRequestListener orderListener = new HttpRequestListener() {
        @Override
        public void onDataRequestSucceed(BaseRequest request) {
            Object[] objs = ((RequestIMOrder) request).getData();
            if (objs != null && objs[1] != null) {
                ArrayList<OrderBean> datas = (ArrayList) objs[1];
                flushOrderView(datas);
            }
            MLog.e("orderListener-onDataRequestSucceed");
        }

        @Override
        public void onDataRequestCancel(BaseRequest request) {
            MLog.e("orderListener-onDataRequestCancel");
        }

        @Override
        public void onDataRequestError(ExceptionInfo errorInfo, BaseRequest request) {
            MLog.e("orderListener-onDataRequestError");
            ErrorHandler handler = new ErrorHandler(getActivity(), this);
            handler.onDataRequestError(errorInfo, request);
        }
    };

    HttpRequestListener imClearListener = new HttpRequestListener() {
        @Override
        public void onDataRequestSucceed(BaseRequest request) {
            MLog.e("清除IM消息成功");
        }

        @Override
        public void onDataRequestCancel(BaseRequest request) {

        }

        @Override
        public void onDataRequestError(ExceptionInfo errorInfo, BaseRequest request) {
            ErrorHandler handler = new ErrorHandler(getActivity(), this);
            handler.onDataRequestError(errorInfo, request);
        }
    };
}
