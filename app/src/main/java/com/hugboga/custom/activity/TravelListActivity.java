package com.hugboga.custom.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.epoxy.EpoxyAdapter;
import com.hugboga.custom.R;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.bean.ChooseDateBean;
import com.hugboga.custom.data.bean.CityRouteBean;
import com.hugboga.custom.data.event.EventAction;
import com.hugboga.custom.data.event.EventType;
import com.hugboga.custom.models.TravelAddItemModel;
import com.hugboga.custom.models.TravelItemModel;
import com.hugboga.custom.statistic.StatisticConstant;
import com.hugboga.custom.statistic.click.StatisticClickEvent;
import com.hugboga.custom.utils.AlertDialogUtils;
import com.hugboga.custom.utils.CharterDataUtils;
import com.hugboga.custom.utils.CommonUtils;
import com.hugboga.custom.utils.DateUtils;
import com.hugboga.custom.utils.UIUtils;
import com.hugboga.custom.utils.WrapContentLinearLayoutManager;
import com.hugboga.custom.widget.TravelAddItemView;
import com.hugboga.custom.widget.charter.CharterSecondBottomView;
import com.hugboga.custom.widget.charter.TravelItemView;
import com.hugboga.custom.widget.title.TitleBar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by qingcha on 17/2/28.
 */

public class TravelListActivity extends BaseActivity {

    @BindView(R.id.travel_list_titlebar)
    TitleBar titleBar;
    @BindView(R.id.travel_list_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.travel_list_bottom_view)
    CharterSecondBottomView bottomView;

    @Override
    public int getContentViewId() {
        return R.layout.activity_travel_list;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    public void initView() {
        titleBar.setTitle(R.string.daily_travel_list);
        TextView leftTV = titleBar.getLeftTV();
        RelativeLayout.LayoutParams leftViewParams = new RelativeLayout.LayoutParams(UIUtils.dip2px(20), UIUtils.dip2px(20));
        leftViewParams.leftMargin = UIUtils.dip2px(15);
        leftViewParams.addRule(RelativeLayout.CENTER_VERTICAL);
        leftTV.setLayoutParams(leftViewParams);
        titleBar.getLeftTV().setBackgroundResource(R.mipmap.top_close);

        TravelListAdapter adapter = new TravelListAdapter(this);
        WrapContentLinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        bottomView.queryPriceState();
        bottomView.setOnBottomClickListener(new CharterSecondBottomView.OnBottomClickListener() {
            @Override
            public void confirm() {
                if (CommonUtils.isLogin(TravelListActivity.this,getEventSource())) {
                    Intent intent = new Intent(TravelListActivity.this, CombinationOrderActivity.class);
                    intent.putExtra(Constants.PARAMS_SOURCE, getEventSource());
                    startActivity(intent);

                    CharterDataUtils charterDataUtils = CharterDataUtils.getInstance();
                    StatisticClickEvent.dailyClick(StatisticConstant.CONFIRM2_R, getIntentSource(), charterDataUtils.chooseDateBean.dayNums,
                            charterDataUtils.guidesDetailData != null, (charterDataUtils.adultCount + charterDataUtils.childCount) + "");
                    charterDataUtils.setSensorsConfirmEvent(TravelListActivity.this);
                }
            }

            @Override
            public void previous() {

            }

            @Override
            public void intentTravelList() {

            }
        });
    }

    @Override
    public String getEventSource() {
        return "行程单";
    }

    public static class TravelListAdapter extends EpoxyAdapter implements TravelItemView.OnEditClickListener, TravelAddItemView.OnAddTravelListener{

        private TravelListActivity activity;
        private CharterDataUtils charterDataUtils;
        private TravelAddItemModel travelAddItemModel;
        private ArrayList<TravelItemModel> travelItemModels;

        public TravelListAdapter(TravelListActivity activity) {
            this.activity = activity;
            charterDataUtils = CharterDataUtils.getInstance();
            travelItemModels = new ArrayList<TravelItemModel>();

            if (travelAddItemModel == null) {
                travelAddItemModel = new TravelAddItemModel();
                travelAddItemModel.setOnAddTravelListener(this);
                addModel(travelAddItemModel);
            }
            setData();
        }

        public void setData() {
            if (charterDataUtils == null || charterDataUtils.chooseDateBean == null) {
                return;
            }
            final int dayNums = charterDataUtils.chooseDateBean.dayNums;
            final int itemModelSize = travelItemModels.size();
            for (int i = itemModelSize; i < dayNums; i++) {
                TravelItemModel travelItemModel = new TravelItemModel();
                travelItemModel.setPosition(i);
                travelItemModel.setOnEditClickListener(this);
                travelItemModels.add(travelItemModel);
                insertModelBefore(travelItemModel, travelAddItemModel);
            }
            for (int i = 0; i < itemModelSize; i++) {
                TravelItemModel travelItemModel = travelItemModels.get(i);
                travelItemModel.show(i < dayNums);
                notifyModelChanged(travelItemModel);
            }
            ArrayList<CityRouteBean.CityRouteScope> travelList = charterDataUtils.travelList;
            if (charterDataUtils.isSeckills()) {
                updateAddModel(false);
            } else if (dayNums == travelList.size() && charterDataUtils.isSelectedSend && charterDataUtils.airPortBean != null) {//最后一天选了送机，不显示添加
                updateAddModel(false);
            } else {
                updateAddModel(true);
            }

            //编辑到最后1天，行程单页面底部出现“查看报价”
            activity.bottomView.setVisibility(checkInfo() && dayNums == travelList.size() ? View.VISIBLE : View.GONE);
        }

        public void updateAddModel(boolean isShow) {
            showModel(travelAddItemModel, isShow);
            notifyModelChanged(travelAddItemModel);
        }

        @Override
        public void onEditClick(int position) {
            EventBus.getDefault().post(new EventAction(EventType.CHARTER_LIST_REFRESH, new CharterSecondStepActivity.RefreshBean(position + 1)));
            activity.finish();
        }

        @Override
        public void onDelClick(int position) {
            AlertDialogUtils.showAlertDialog(activity, CommonUtils.getString(R.string.daily_travel_list_del_dialog)
                    , CommonUtils.getString(R.string.daily_travel_list_yes), CommonUtils.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ChooseDateBean chooseDateBean = charterDataUtils.chooseDateBean;
                    charterDataUtils.resetSendInfo();
                    if (chooseDateBean.dayNums - 1 < charterDataUtils.travelList.size()) {
                        charterDataUtils.cleanDayDate(chooseDateBean.dayNums);
                    }
                    chooseDateBean.dayNums--;
                    chooseDateBean.end_date = DateUtils.getDay(chooseDateBean.end_date, -1);
                    chooseDateBean.showEndDateStr = DateUtils.orderChooseDateTransform(chooseDateBean.end_date);
                    chooseDateBean.endDate = DateUtils.getDateByStr(chooseDateBean.end_date);

                    boolean isAtwill = false;
                    if (chooseDateBean.dayNums - 1 < charterDataUtils.travelList.size()) {//当前天是随便转转，删除数据，改为默认市内一日游
                        if (charterDataUtils.travelList.get(chooseDateBean.dayNums - 1).routeType == CityRouteBean.RouteType.AT_WILL) {
                            if (chooseDateBean.dayNums > 1 && chooseDateBean.dayNums - 1 < charterDataUtils.travelList.size()) {
                                charterDataUtils.travelList.remove(chooseDateBean.dayNums - 1);
                                isAtwill = true;
                            }
                        }
                    }
                    setData();
                    if (charterDataUtils.currentDay == chooseDateBean.dayNums + 1) {
                        charterDataUtils.currentDay--;
                        EventBus.getDefault().post(new EventAction(EventType.CHARTER_LIST_REFRESH, new CharterSecondStepActivity.RefreshBean(charterDataUtils.currentDay)));
                    } else if (isAtwill) {
                        EventBus.getDefault().post(new EventAction(EventType.CHARTER_LIST_REFRESH, new CharterSecondStepActivity.RefreshBean(charterDataUtils.currentDay, true)));
                    }
                    dialog.dismiss();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        @Override
        public void onAddTravel() {
            ChooseDateBean chooseDateBean = charterDataUtils.chooseDateBean;
            chooseDateBean.dayNums++;
            chooseDateBean.end_date = DateUtils.getDay(chooseDateBean.end_date, 1);
            chooseDateBean.showEndDateStr = DateUtils.orderChooseDateTransform(chooseDateBean.end_date);
            chooseDateBean.endDate = DateUtils.getDateByStr(chooseDateBean.end_date);
            setData();
        }

        public boolean checkInfo() {
            ArrayList<CityRouteBean.CityRouteScope> travelList = charterDataUtils.travelList;
            final int travelListSize = travelList.size();
            for (int i = 0; i < travelListSize; i++) {
                CityRouteBean.CityRouteScope cityRouteScope = travelList.get(i);
                if (!charterDataUtils.checkInfo(cityRouteScope.routeType, i + 1, false)) {
                    return false;
                }
            }
            return true;
        }
    }
}
