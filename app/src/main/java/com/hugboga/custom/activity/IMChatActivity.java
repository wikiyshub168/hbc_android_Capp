package com.hugboga.custom.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huangbaoche.hbcframe.activity.BaseFragmentActivity;
import com.huangbaoche.hbcframe.data.net.ErrorHandler;
import com.huangbaoche.hbcframe.data.net.ExceptionInfo;
import com.huangbaoche.hbcframe.data.net.HttpRequestListener;
import com.huangbaoche.hbcframe.data.net.HttpRequestUtils;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.huangbaoche.hbcframe.util.MLog;
import com.hugboga.custom.R;
import com.hugboga.custom.data.bean.ChatInfo;
import com.hugboga.custom.data.bean.ChatOrderBean;
import com.hugboga.custom.data.parser.ParserChatInfo;
import com.hugboga.custom.data.request.RequestIMClear;
import com.hugboga.custom.data.request.RequestIMOrder;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.Inflater;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.RongIMClientWrapper;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;

@ContentView(R.layout.activity_imchat)
public class IMChatActivity extends BaseFragmentActivity {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title.setText(getTitle()); //刷新标题
        topRightBtn.setVisibility(View.VISIBLE); //显示历史订单按钮
//        grantAudio(); //对音频进行授权
        ConversationFragment conversation = (ConversationFragment) getSupportFragmentManager().findFragmentById(R.id.conversation);
        if (conversation == null) {
            return;
        }
        view = (RelativeLayout) conversation.getView();
        //刷新订单信息
        getUserInfoToOrder(conversation.getUri());
    }

    /**
     * 解析用户ID信息
     *
     * @param uri
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
            initRunningOrder(); //构建和该用户之间的订单
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
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
    private void flushOrderView(ArrayList<ChatOrderBean> orders) {
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
    private List<View> getOrderViews(ArrayList<ChatOrderBean> datas) {
        List<View> views = new ArrayList<>();
        pointLayout.removeAllViews();
        for (ChatOrderBean letterOrder : datas) {
            View view = View.inflate(IMChatActivity.this, R.layout.im_chat_orders_item, null);
            //设置状态
            TextView textView = (TextView) view.findViewById(R.id.im_chat_orders_item_state);
            textView.setText(letterOrder.status);
            resetStatusColor(textView, letterOrder.status);
            //订单类型和时间
            TextView textViewtype = (TextView) view.findViewById(R.id.im_chat_orders_item_ordertime);
            textViewtype.setText(letterOrder.serviceTime + "  " + letterOrder.orderTypeStr);
            //订单地址
            TextView textViewAddr = (TextView) view.findViewById(R.id.im_chat_orders_item_address);
            textViewAddr.setText(letterOrder.startAddress + " - " + letterOrder.destAddress);
            views.add(view);
            //设置红点
            View viewp = View.inflate(IMChatActivity.this, R.layout.im_chat_orders_point, null);
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

    /**
     * 根据私信订单状态改变颜色
     *
     * @param tv
     * @param status
     */
    private void resetStatusColor(TextView tv, String status) {
        if (!TextUtils.isEmpty(status)) {
            if (getString(R.string.letter_order_state1).equals(status)) {
                tv.setTextColor(ContextCompat.getColor(IMChatActivity.this, R.color.letter_item_order1));
            } else if (getString(R.string.letter_order_state2).equals(status)) {
                tv.setTextColor(ContextCompat.getColor(IMChatActivity.this, R.color.letter_item_order2));
            } else if (getString(R.string.letter_order_state3).equals(status)) {
                tv.setTextColor(ContextCompat.getColor(IMChatActivity.this, R.color.letter_item_order3));
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

        IMOrderPagerAdapter(ArrayList<ChatOrderBean> datas) {
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

    @Event({R.id.header_left_btn, R.id.header_right_txt})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.header_left_btn:
                finish();
                clearImChat();
                break;
            case R.id.header_right_txt:
                MLog.e("进入历史订单列表");
//                Intent intent = new Intent(IMChatActivity.this, NewOrderActivity.class);
//                intent.putExtra(NewOrderActivity.SEARCH_TYPE, NewOrderActivity.SearchType.SEARCH_TYPE_HISTORY.getType());
//                intent.putExtra(NewOrderActivity.SEARCH_USER, userId);
//                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        clearImChat();
        super.onBackPressed();
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
        RequestIMClear requestIMClear = new RequestIMClear(IMChatActivity.this, userId, targetType);
        HttpRequestUtils.request(IMChatActivity.this, requestIMClear, imClearListener);
    }

    /**
     * 刷新IM聊天订单
     */
    private void loadImOrder() {
        RequestIMOrder requestIMOrder = new RequestIMOrder(IMChatActivity.this, userId);
        HttpRequestUtils.request(IMChatActivity.this, requestIMOrder, orderListener);
    }

    HttpRequestListener orderListener = new HttpRequestListener() {
        @Override
        public void onDataRequestSucceed(BaseRequest request) {
            Object[] objs = ((RequestIMOrder) request).getData();
            if (objs != null && objs[1] != null) {
                ArrayList<ChatOrderBean> datas = (ArrayList) objs[1];
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
            ErrorHandler handler = new ErrorHandler(IMChatActivity.this, this);
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
            ErrorHandler handler = new ErrorHandler(IMChatActivity.this, this);
            handler.onDataRequestError(errorInfo, request);
        }
    };
}
