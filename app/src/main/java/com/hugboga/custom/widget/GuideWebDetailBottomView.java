package com.hugboga.custom.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hugboga.custom.R;
import com.hugboga.custom.activity.NIMChatActivity;
import com.hugboga.custom.data.bean.GuideExtinfoBean;
import com.hugboga.custom.data.bean.UserEntity;
import com.hugboga.custom.utils.IMUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GuideWebDetailBottomView extends LinearLayout implements HbcViewBehavior{

    @Bind(R.id.guide_detail_bottom_top_line_view)
    View topLineView;
    @Bind(R.id.guide_detail_bottom_hint_tv)
    TextView hintTv;

    @Bind(R.id.guide_detail_bottom_time_tv)
    TextView timeTv;

    @Bind(R.id.guide_detail_bottom_contact_iv)
    ImageView contactIv;
    @Bind(R.id.guide_detail_bottom_contact_tv)
    TextView contactTv;
    @Bind(R.id.guide_detail_bottom_contact_layout)
    LinearLayout contactLayout;

    private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    private SimpleDateFormat dateTimeFormat;
    private volatile long delayedMillis;
    private volatile boolean isStop = false;

    private GuideExtinfoBean guideExtinfoBean;

    public GuideWebDetailBottomView(Context context) {
        this(context, null);
    }

    public GuideWebDetailBottomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = inflate(context, R.layout.view_guide_detail_bottom, this);
        ButterKnife.bind(view);
        setOrientation(LinearLayout.VERTICAL);
        dateTimeFormat = new SimpleDateFormat("HH:mm");
    }

    @OnClick(R.id.guide_detail_bottom_contact_layout)
    public void contactGuide() {
        if (guideExtinfoBean == null || TextUtils.isEmpty(guideExtinfoBean.neUserId) || !IMUtil.getInstance().isLogined()) {
            return;
        }
        NIMChatActivity.start(getContext(), guideExtinfoBean.neUserId);
    }

    @Override
    public void update(Object _data) {
        if (!UserEntity.getUser().isLogin(getContext())) {
            setVisibility(View.GONE);
            return;
        }
        setVisibility(View.VISIBLE);
        guideExtinfoBean = (GuideExtinfoBean) _data;

        if (guideExtinfoBean.accessible == 1) {//是否可联系司导
            hintTv.setVisibility(View.GONE);
            topLineView.setVisibility(View.GONE);
            contactIv.setBackgroundResource(R.mipmap.navbar_chat_white);
            contactTv.setTextColor(0xFFFFFFFF);
            contactLayout.setBackgroundResource(R.drawable.shape_rounded_yellow_btn);
            contactLayout.setEnabled(true);
        } else {
            hintTv.setVisibility(View.VISIBLE);
            topLineView.setVisibility(View.VISIBLE);
            contactIv.setBackgroundResource(R.mipmap.navbar_chat);
            contactTv.setTextColor(0xFF666666);
            contactLayout.setBackgroundResource(R.drawable.shape_rounded_disabled);
            contactLayout.setEnabled(false);
        }

        if (TextUtils.isEmpty(guideExtinfoBean.localTime) || guideExtinfoBean.localTimezone == null) {
            timeTv.setText("");
        } else {
            isStop = false;
            String timeZoneString = "GMT";
            if (guideExtinfoBean.localTimezone >= 0) {
                timeZoneString += "+";
            } else {
                timeZoneString += "-";
            }
            timeZoneString += Math.abs(guideExtinfoBean.localTimezone);
            TimeZone timeZone = TimeZone.getTimeZone(timeZoneString);
            dateTimeFormat.setTimeZone(timeZone);

            Calendar mCalendar = Calendar.getInstance(timeZone);
            mCalendar.setTimeInMillis(System.currentTimeMillis());
            delayedMillis = 60 * 1000 - mCalendar.get(Calendar.SECOND) * 1000 - mCalendar.get(Calendar.MILLISECOND);
            singleThreadExecutor.execute(timeRunnable);
        }
    }

    private Runnable timeRunnable = new Runnable() {

        @Override
        public void run() {
            try {
                if (delayedMillis > 0 && mHandler != null) {
                    mHandler.sendEmptyMessage(1);
                    Thread.sleep(delayedMillis);
                    delayedMillis = 0;
                }
                do {
                    mHandler.sendEmptyMessage(1);
                    Thread.sleep(60 * 1000);
                } while (!isStop && mHandler != null);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (isStop) {
                        return;
                    }
                    timeTv.setText(String.format("当地时间: %1$s", dateTimeFormat.format(System.currentTimeMillis())));
                    break;
                default:
                    break;
            }
        }
    };

    public void setStop(boolean isStop) {
        this.isStop = isStop;
    }
}
