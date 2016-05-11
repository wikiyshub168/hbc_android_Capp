package com.hugboga.custom.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.huangbaoche.hbcframe.util.MLog;
import com.hugboga.custom.R;
import com.hugboga.custom.adapter.AirportAdapter;
import com.hugboga.custom.data.bean.AirPort;
import com.hugboga.custom.data.request.RequestAirPort;
import com.hugboga.custom.utils.DBHelper;
import com.hugboga.custom.utils.SharedPre;
import com.hugboga.custom.widget.SideBar;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.db.Selector;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择机场
 */
@ContentView(R.layout.fg_city)
public class FgChooseAirport extends BaseFragment implements SideBar.OnTouchingLetterChangedListener, AdapterView.OnItemClickListener, View.OnKeyListener, TextWatcher {

    public static final String KEY_BUNDLE = "bundle";
    public static final String KEY_AIRPORT = "key_airport";

    public AirportAdapter adapter;
    private ListView sortListView;
    private long t = 0;
    private List<AirPort> sourceDateList;
    private SideBar sideBar;
    private View emptyView;
    @ViewInject(R.id.empty_layout_text)
    private TextView emptyViewText;
    private TextView editSearch;
    private DbManager mDbManager;
    private SharedPre sharedPer;
    private ArrayList<String> airportHistory;


    @Override
    protected void initHeader() {
        mDbManager = new DBHelper(getActivity()).getDbManager();
        sharedPer = new SharedPre(getActivity());
        setProgressState(0);
//		fgTitle.setText(getString(R.string.title_choose_airport));
    }

    protected void initView() {
        //实例化汉字转拼音类
        View view = getView();
        emptyView = view.findViewById(R.id.arrival_empty_layout);
        emptyViewText.setText(getString(R.string.empty_text));
        sideBar = (SideBar) view.findViewById(R.id.sidrbar);
        TextView dialog = (TextView) view.findViewById(R.id.dialog);
        sideBar.setTextView(dialog);
        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(this);
        sortListView = (ListView) view.findViewById(R.id.country_lvcountry);
        sortListView.setOnItemClickListener(this);
        editSearch = (TextView) view.findViewById(R.id.head_search);
        editSearch.setHint("输入城市或机场");
        editSearch.addTextChangedListener(this);
        editSearch.setOnKeyListener(this);
        editSearch.requestFocus();

        adapter = new AirportAdapter(getActivity(), sourceDateList);
        sortListView.setAdapter(adapter);
        sideBar.setVisibility(View.VISIBLE);
        initSideBar(sideBar);

    }

    @Override
    protected Callback.Cancelable requestData() {
        requestDate(null);
        requestHotDate();
        requestHistoryDate();
        return null;
    }

    private void initSideBar(SideBar sideBar) {
        String[] b = {"历史", "热门", "A", "B", "C", "D", "E", "F", "G", "H", "I",
                "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z", "#"};
        sideBar.setLetter(b);

    }

    private void setFirstWord(List<AirPort> sourceDateList) {
        String key = "";
        if (sourceDateList == null) return;
        for (int i = 0; i < sourceDateList.size(); i++) {
            AirPort model = sourceDateList.get(i);
            if (key.equals(model.cityFirstLetter)) {
                model.isFirst = false;
            } else {
                model.isFirst = true;
                key = model.cityFirstLetter;
            }
        }
    }

    protected void requestDate(String keyword) {
        Selector selector = null;
        try {
            selector = mDbManager.selector(AirPort.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(keyword))
            selector.where("airport_name", "LIKE", "%" + keyword + "%").or("city_name", "LIKE", "%" + keyword + "%");
        try {
            sourceDateList = selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        selector.orderBy("city_initial");
        inflateContent();
    }

    /**
     * 热门机场
     */
    private void requestHotDate() {
        Selector selector = null;
        try {
            selector = mDbManager.selector(AirPort.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        selector.where("is_hot", "=", 1);
        selector.orderBy("hot_weight");
        selector.limit(3);
        try {
            List<AirPort> hotAirportDate = selector.findAll();
            for (AirPort bean : hotAirportDate) {
                bean.cityFirstLetter = "热门机场";
            }
            if (hotAirportDate.size() > 0l && hotAirportDate.get(0) != null) {
                hotAirportDate.get(0).isFirst = true;
            }
            sourceDateList.addAll(0, hotAirportDate);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    /**
     * 搜索历史记录
     */
    private void requestHistoryDate() {
        String airportHistoryStr = sharedPer.getStringValue(mBusinessType + SharedPre.RESOURCES_AIRPORT_HISTORY);
        airportHistory = new ArrayList<String>();
        if (!TextUtils.isEmpty(airportHistoryStr)) {
            for (String airport : airportHistoryStr.split(",")) {
                airportHistory.add(airport);
            }
        }
        Selector selector = null;
        try {
            selector = mDbManager.selector(AirPort.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        WhereBuilder where = WhereBuilder.b();
        where.and("airport_id", "IN", airportHistory);
        selector.where(where);
        try {
            List<AirPort> tmpHistoryAirportDate = selector.findAll();
            ArrayList<AirPort> historyAirportDate = new ArrayList<AirPort>();
            for (String historyId : airportHistory) {
                for (AirPort bean : tmpHistoryAirportDate) {
                    if (historyId.equals(String.valueOf(bean.airportId))) {
                        historyAirportDate.add(bean);
                    }
                    bean.cityFirstLetter = "搜索历史";
                }
            }
            if (historyAirportDate.size() > 0 && historyAirportDate.get(0) != null) {
                historyAirportDate.get(0).isFirst = true;
            }
            sourceDateList.addAll(0, historyAirportDate);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDataRequestSucceed(BaseRequest request) {
        if (request instanceof RequestAirPort) {
            RequestAirPort mParser = (RequestAirPort) request;
            sourceDateList = mParser.getData();
            inflateContent();
        }
    }

    @Override
    protected void inflateContent() {
        // 设置key
        setFirstWord(sourceDateList);
//		emptyViewText.setText(getString(R.string.arrival_empty_text, editSearch.getText().toString().trim()));
        if (sourceDateList == null || sourceDateList.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
            setFirstWord(sourceDateList);
        }
        adapter.updateListView(sourceDateList);
    }

    @Event({R.id.head_search_clean, R.id.head_text_right})
    private void onClickView(View view) {
        switch (view.getId()) {
            case R.id.head_search_clean:
                editSearch.setText("");
                break;
            case R.id.head_text_right:
                String keyword = editSearch.getText().toString().trim();
                if (TextUtils.isEmpty(keyword)) return;
                requestDate(keyword); //进行点击搜索
                break;
        }
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        //该字母首次出现的位置
        int position = adapter.getPositionForSection(s);
        if (position != -1) {
            sortListView.setSelection(position);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
        collapseSoftInputMethod();
        Bundle tBundle = getArguments();
        Bundle bundle;
        if (tBundle != null) {
            bundle = new Bundle(tBundle);
        } else {
            bundle = new Bundle();
        }
        saveHistoryDate(sourceDateList.get(position));
        bundle.putSerializable(KEY_AIRPORT, sourceDateList.get(position));
        finishForResult(bundle);
    }

    /**
     * 存储历史
     *
     * @param airPort 机场
     */
    private void saveHistoryDate(AirPort airPort) {
        if (airportHistory == null) airportHistory = new ArrayList<String>();
        airportHistory.remove(String.valueOf(airPort.airportId));//排重
        airportHistory.add(0, String.valueOf(airPort.airportId));
        for (int i = airportHistory.size() - 1; i > 2; i--) {
            airportHistory.remove(i);
        }
        sharedPer.saveStringValue(mBusinessType + SharedPre.RESOURCES_AIRPORT_HISTORY, TextUtils.join(",", airportHistory));
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        MLog.e("" + event.getAction() + " " + keyCode);
        if (event.getAction() == KeyEvent.ACTION_UP && (keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_ENTER)) {
            requestDate(editSearch.getText().toString().trim());
            return true;
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(s)) {
            requestData();
        } else {
            requestDate(editSearch.getText().toString().trim());
        }
    }
}