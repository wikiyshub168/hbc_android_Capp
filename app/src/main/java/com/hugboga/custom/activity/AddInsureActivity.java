package com.hugboga.custom.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.huangbaoche.hbcframe.data.net.ExceptionInfo;
import com.huangbaoche.hbcframe.data.net.HttpRequestListener;
import com.huangbaoche.hbcframe.data.net.HttpRequestUtils;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.hugboga.custom.R;
import com.hugboga.custom.data.bean.ChooseDateBean;
import com.hugboga.custom.data.bean.InsureResultBean;
import com.hugboga.custom.data.bean.UserEntity;
import com.hugboga.custom.data.event.EventAction;
import com.hugboga.custom.data.event.EventType;
import com.hugboga.custom.data.request.RequestAddInsure;
import com.hugboga.custom.data.request.RequestEditInsure;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.DatePicker;

/**
 * Created on 16/8/6.
 */

public class AddInsureActivity extends BaseActivity implements HttpRequestListener {
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.cardid)
    EditText cardid;
    @BindView(R.id.sex)
    TextView sex;
    @BindView(R.id.birthday)
    TextView birthday;
    @BindView(R.id.header_left_btn)
    ImageView headerLeftBtn;
    @BindView(R.id.header_title)
    TextView headerTitle;
    @BindView(R.id.header_right_txt)
    TextView headerRightTxt;

    private boolean isSubmit = false;

    protected void initHeader() {
        headerLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput();
                finish();
            }
        });
    }

    protected void initView() {
        getArgument();
        headerRightTxt.setVisibility(View.VISIBLE);
        headerRightTxt.setText(R.string.add_insure_save);
        headerRightTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSubmit) {
                    return;
                }
                isSubmit = true;
                if (isEdit) {
                    edit();
                } else {
                    add();
                }
            }
        });
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    disableNextBtn();
                } else {
                    check();
                }
            }
        });
        cardid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    disableNextBtn();
                } else {
                    check();
                }
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        hideSoftInput();
    }

    @Override
    public int getContentViewId() {
        return R.layout.add_new_insure;
    }

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        EventBus.getDefault().register(this);
        getArgument();
        initView();
        initHeader();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    boolean isEdit = false;

    private void getArgument() {
        insureResultBean = (InsureResultBean) this.getIntent().getSerializableExtra("insureResultBean");
        if (null != insureResultBean) {
            isEdit = true;
            name.setText(insureResultBean.name);
            cardid.setText(insureResultBean.passportNo);
            sexInt = insureResultBean.sex;
            sex.setText(sexInt == 0 ? R.string.add_insure_man : R.string.add_insure_woman);
            birthday.setText(insureResultBean.birthday);
            headerTitle.setText(R.string.edit_insure_title);
        } else {
            insureResultBean = new InsureResultBean();
            isEdit = false;
            headerTitle.setText(R.string.add_insure_title);
        }
        check();
    }

    InsureResultBean insureResultBean;

    private void edit() {
        RequestEditInsure requestEditInsure = new RequestEditInsure(activity,
                UserEntity.getUser().getUserId(activity), insureResultBean.insuranceUserId,
                name.getText().toString(), cardid.getText().toString(),
                sexInt + "", birthday.getText().toString());
        HttpRequestUtils.request(activity, requestEditInsure, this);
    }

    private void add() {
        RequestAddInsure requestAddInsure = new RequestAddInsure(activity, UserEntity.getUser().getUserId(activity),
                name.getText().toString(), cardid.getText().toString(),
                sexInt + "", birthday.getText().toString());
        HttpRequestUtils.request(activity, requestAddInsure, this);
    }

    @Override
    public void onDataRequestError(ExceptionInfo errorInfo, BaseRequest request) {
        super.onDataRequestError(errorInfo, request);
        isSubmit = false;
    }

    @Override
    public void onDataRequestSucceed(BaseRequest request) {
        super.onDataRequestSucceed(request);
        insureResultBean = (InsureResultBean) (request.getData());
        if (request instanceof RequestEditInsure) {
            EventBus.getDefault().post(new EventAction(EventType.EDIT_BACK_INSURE, insureResultBean));
        } else if (request instanceof RequestAddInsure) {
            insureResultBean = (InsureResultBean) (request.getData());
            EventBus.getDefault().post(new EventAction(EventType.ADD_INSURE, insureResultBean));
        }
        isSubmit = false;
        finish();
    }

    private void disableNextBtn() {
        headerRightTxt.setEnabled(false);
        headerRightTxt.setTextColor(0xFF929292);
    }

    private void enableNextBtn() {
        headerRightTxt.setEnabled(true);
        headerRightTxt.setTextColor(getResources().getColor(R.color.default_black));
    }

    ChooseDateBean chooseDateBean;
    @Subscribe
    public void onEventMainThread(EventAction action) {
        switch (action.getType()) {
            case CHOOSE_DATE:
                chooseDateBean = (ChooseDateBean) action.getData();
                birthday.setText(chooseDateBean.halfDateStr);
                check();
                break;
        }
    }

    private void check() {
        if (TextUtils.isEmpty(name.getText())) {
//            ToastUtils.showLong("姓名不能为空");
            disableNextBtn();
            return;
        }
        if (TextUtils.isEmpty(cardid.getText())) {
//            ToastUtils.showLong("护照不能为空");
            disableNextBtn();
            return;
        }
        if (TextUtils.isEmpty(sex.getText())) {
//            ToastUtils.showLong("性别不能为空");
            disableNextBtn();
            return;
        }
        if (TextUtils.isEmpty(birthday.getText())) {
//            ToastUtils.showLong("出生日期不能为空");
            disableNextBtn();
            return;
        }
        enableNextBtn();
    }

    DatePicker picker;
    SimpleDateFormat dateDateFormat;
    public void showDaySelect() {
//        Intent intent = new Intent(activity,DatePickerActivity.class);
//        intent.putExtra("startDate","1990-01-01");
//        intent.putExtra("title","请选择出生日期");
//        intent.putExtra("type",3);
//        startActivity(intent);
        Calendar calendar = Calendar.getInstance();
        picker = new DatePicker(activity, DatePicker.YEAR_MONTH_DAY);
        picker.setRangeStart(1900, 01, 01);
        Calendar currentCalendar = Calendar.getInstance();
        picker.setRangeEnd(currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH) + 1, currentCalendar.get(Calendar.DATE));
        picker.setTitleText("请选择出生日期");
        picker.setTopLineVisible(false);
        picker.setPressedTextColor(getResources().getColor(R.color.default_yellow));
        picker.setCancelTextColor(getResources().getColor(R.color.guildsaved));
        picker.setSubmitTextColor(getResources().getColor(R.color.default_yellow));
        picker.setTopBackgroundColor(getResources().getColor(R.color.allbg_white));
        picker.setLineConfig(null);
        try {
            if (!TextUtils.isEmpty(birthday.getText())) {
                if (dateDateFormat == null) {
                    dateDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                }
                calendar.setTime(dateDateFormat.parse(birthday.getText().toString()));
                picker.setSelectedItem(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));
            } else {
                picker.setSelectedItem(1990, 1, 1);
            }
        } catch (ParseException e) {
            picker.setSelectedItem(1990, 1, 1);
        }
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                String serverDate = year + "-" + month + "-" + day;
                birthday.setText(serverDate);
                check();
                picker.dismiss();
            }
        });
        picker.show();

    }



    private int getSexInt(CharSequence[] items3) {
        String str = sex.getText().toString();
        if (str == null || str.isEmpty()) {
            return -1;
        }
        for (int i = 0; i < items3.length; i++) {
            if (str.equals(items3[i])) {
                return i;
            }
        }
        return -1;
    }

    int sexInt = 0;

    @OnClick({R.id.sex, R.id.birthday})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sex:
                final CharSequence[] items3 = getResources().getStringArray(R.array.my_info_sex);
                final AlertDialog.Builder sexDialog = new AlertDialog.Builder(activity);
                sexDialog.setTitle(R.string.add_insure_choose_gender);
                sexDialog.setSingleChoiceItems(items3, getSexInt(items3), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sexInt = which;
                        String sexStr = items3[which].toString();
                        sex.setText(sexStr);
                        dialog.dismiss();
                        check();
                    }
                });

                Dialog dialog = sexDialog.create();
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                break;
            case R.id.birthday:
                showDaySelect();
                break;
        }
    }
}

