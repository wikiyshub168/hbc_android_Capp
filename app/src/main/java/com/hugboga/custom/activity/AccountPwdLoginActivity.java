package com.hugboga.custom.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.huangbaoche.hbcframe.data.bean.UserSession;
import com.huangbaoche.hbcframe.data.net.ExceptionInfo;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.huangbaoche.hbcframe.util.MLog;
import com.hugboga.custom.R;
import com.hugboga.custom.action.data.ActionBean;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.bean.AreaCodeBean;
import com.hugboga.custom.data.bean.UserBean;
import com.hugboga.custom.data.bean.UserEntity;
import com.hugboga.custom.data.event.EventAction;
import com.hugboga.custom.data.event.EventType;
import com.hugboga.custom.data.request.RequestLogin;
import com.hugboga.custom.statistic.MobClickUtils;
import com.hugboga.custom.statistic.StatisticConstant;
import com.hugboga.custom.statistic.click.StatisticClickEvent;
import com.hugboga.custom.statistic.sensors.SensorsUtils;
import com.hugboga.custom.utils.CommonUtils;
import com.hugboga.custom.utils.IMUtil;
import com.hugboga.custom.utils.SharedPre;
import com.qiyukf.unicorn.api.Unicorn;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by zhangqiang on 17/5/17.
 */

public class AccountPwdLoginActivity extends BaseActivity implements TextWatcher {

    @BindView(R.id.change_mobile_areacode)
    TextView areaCodeTextView;
    @BindView(R.id.login_phone)
    EditText phoneEditText;
    @BindView(R.id.login_password)
    EditText passwordEditText;
    @BindView(R.id.iv_pwd_visible)
    ImageView passwordVisible;
    @BindView(R.id.header_left_btn)
    ImageView headerLeftBtn;
    @BindView(R.id.header_title)
    TextView fgTitle;
    @BindView(R.id.login_submit)
    Button loginButton;
    @BindView(R.id.delete)
    ImageView delete;
    @BindView(R.id.delete1)
    ImageView delete1;
    boolean isPwdVisibility = false;
    String phone;
    String areaCode;
    private SharedPre sharedPre;
    public static String KEY_PHONE = "key_phone";
    public static String KEY_AREA_CODE = "key_area_code";
    public ActionBean actionBean;
    boolean isFirstEnter = true;
    @Override
    public int getContentViewId() {
        return R.layout.account_pwd_login;
    }

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initView(getIntent());
        EventBus.getDefault().register(this);
    }
    private void initView(Intent intent) {
        fgTitle.setText(R.string.login_account_pwd);
        String areaCode = null;
        String phone = null;
        if (getIntent() != null) {
            areaCode = intent.getStringExtra(KEY_AREA_CODE);
            phone = intent.getStringExtra(KEY_PHONE);
            source = intent.getStringExtra("source");
            actionBean = (ActionBean) intent.getSerializableExtra(Constants.PARAMS_ACTION);
        }
        sharedPre = new SharedPre(activity);
        if (TextUtils.isEmpty(areaCode)) {
            areaCode = sharedPre.getStringValue(SharedPre.LOGIN_CODE);
        }
        if (!TextUtils.isEmpty(areaCode)) {
            this.areaCode = areaCode;
            areaCodeTextView.setText(CommonUtils.addPhoneCodeSign(areaCode));
        } else {
            this.areaCode = "86";
        }
        if (TextUtils.isEmpty(phone)) {
            phone = sharedPre.getStringValue(SharedPre.LOGIN_PHONE);
        }
        if (!TextUtils.isEmpty(phone)) {
            this.phone = phone;
            phoneEditText.setText(phone);
        }
        if(phone!= null){
            if(phone.length() >0){
                delete.setVisibility(View.VISIBLE);
            }else{
                delete.setVisibility(View.GONE);
            }
        }
        if(passwordEditText.getText().toString()!= null){
            if(passwordEditText.getText().toString().length() >0){
                delete1.setVisibility(View.VISIBLE);
            }else{
                delete1.setVisibility(View.GONE);
            }
        }

        phoneEditText.addTextChangedListener(this);
        passwordEditText.addTextChangedListener(this);

        headerLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput();
                finish();
            }
        });
        passwordEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                passwordVisible.setVisibility(View.VISIBLE);
                return false;
            }
        });
        phoneEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                passwordVisible.setVisibility(View.GONE);
                return false;
            }
        });
        if(phoneEditText.getText().toString().length() == 0){
            phoneEditText.setFocusable(true);
            phoneEditText.setFocusableInTouchMode(true);
            phoneEditText.requestFocus();
        }else if(passwordEditText.getText().toString().length() == 0){
            passwordEditText.setFocusable(true);
            passwordEditText.setFocusableInTouchMode(true);
            passwordEditText.requestFocus();
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            }

        }, 200);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initView(intent);
    }
    @OnClick({R.id.header_left_btn,R.id.change_mobile_areacode,R.id.iv_pwd_visible,R.id.change_mobile_diepwd,R.id.login_submit,R.id.login_phone,R.id.delete,R.id.delete1})
    public void onClick(View view) {
        Intent intent = null;
        HashMap<String, String> map = new HashMap<String, String>();
        switch (view.getId()) {
            case R.id.header_left_btn:
                StatisticClickEvent.click(StatisticConstant.LOGIN_CLOSE,getIntentSource());
                finish();
                break;
            case R.id.change_mobile_areacode:
                passwordEditText.clearFocus();
                phoneEditText.clearFocus();
                //选择区号
//                collapseSoftInputMethod(); //隐藏键盘
                intent = new Intent(AccountPwdLoginActivity.this, ChooseCountryActivity.class);
                intent.putExtra(KEY_FROM, "login");
                startActivity(intent);
                break;
            case R.id.change_mobile_diepwd:
                //忘记密码
//                collapseSoftInputMethod(); //隐藏键盘
                passwordEditText.setText("");
                Bundle bundle1 = new Bundle();
                bundle1.putString("areaCode", areaCode);
                bundle1.putString("phone", phone);
                bundle1.putString(KEY_FROM, "login");
                intent = new Intent(AccountPwdLoginActivity.this, ForgetPasswdActivity.class);
                intent.putExtras(bundle1);
                startActivity(intent);
                MobClickUtils.onEvent(StatisticConstant.FIND_PASSWORD);
                break;
            case R.id.iv_pwd_visible:
                if (passwordEditText != null) {
                    if (isPwdVisibility) {//密码可见
                        isPwdVisibility = false;
                        passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        passwordVisible.setImageResource(R.mipmap.login_invisible);
                    } else {//密码不可见
                        isPwdVisibility = true;
                        passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        passwordVisible.setImageResource(R.mipmap.login_visible);
                    }
                }
                break;
            case R.id.login_submit:
                loginGo();
                break;
            case R.id.delete:
                phoneEditText.setText("");
                phoneEditText.setFocusable(true);
                phoneEditText.setFocusableInTouchMode(true);
                phoneEditText.requestFocus();
                break;
            case R.id.delete1:
                passwordEditText.setText("");
                passwordEditText.setFocusable(true);
                passwordEditText.setFocusableInTouchMode(true);
                passwordEditText.requestFocus();
            default:
                break;
        }
    }
    @Subscribe
    public void onEventMainThread(EventAction action) {
        switch (action.getType()) {
            case CHOOSE_COUNTRY_BACK:
                if (!(action.getData() instanceof AreaCodeBean)) {
                    break;
                }
                AreaCodeBean areaCodeBean = (AreaCodeBean) action.getData();
                if (areaCodeBean == null) {
                    break;
                }
                areaCodeTextView.setText("+" + areaCodeBean.getCode());
                areaCode = "+" + areaCodeBean.getCode();
                break;
        }
    }
    /**
     * 进行登录
     */
    private void loginGo() {
//        collapseSoftInputMethod(); //隐藏键盘
        MLog.e("areaCode4=" + areaCode);
        if (TextUtils.isEmpty(areaCode)) {
            CommonUtils.showToast(R.string.login_check_areacode);
            return;
        }
        areaCode = areaCode.replace("+", "");
        phone = phoneEditText.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            CommonUtils.showToast(R.string.login_check_phone_empty);
            return;
        }
        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            CommonUtils.showToast(R.string.login_check_pwd);
            return;
        }
        /*if (!Pattern.matches("[\\w]{6,16}", password)) {
            CommonUtils.showToast(R.string.login_check_pwd_length);
            return;
        }*/
        if(areaCode.equals("86")){
            if(!phone.startsWith("1") || phone.length() != 11){
                CommonUtils.showToast(R.string.login_check_phone_length);
                return;
            }
        }
        RequestLogin request = new RequestLogin(activity, areaCode, phone, password);
        requestData(request);

        StatisticClickEvent.click(StatisticConstant.LOGIN_PHONE,getIntentSource());
        SensorsUtils.onAppClick("登录", "账号密码登录", getIntentSource());
    }

    @Override
    public String getEventId() {
        return StatisticConstant.LOGIN_PHONE;
    }

    @Override
    public String getEventSource() {
        return "账号密码登录";
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDataRequestSucceed(BaseRequest request) {
        super.onDataRequestSucceed(request);
        if (request instanceof RequestLogin) {
            RequestLogin mRequest = (RequestLogin) request;
            UserBean user = mRequest.getData();
            user.setUserEntity(activity);
            UserEntity.getUser().setAreaCode(activity, mRequest.areaCode);
            UserEntity.getUser().setPhone(activity, mRequest.mobile);
            UserEntity.getUser().setLoginAreaCode(activity, mRequest.areaCode);
            UserEntity.getUser().setLoginPhone(activity, mRequest.mobile);
            UserSession.getUser().setUserToken(activity, user.userToken);
            UserEntity.getUser().setUserName(activity, user.name);
            UserEntity.getUser().setNimUserId(activity,user.nimUserId);
            UserEntity.getUser().setNimUserToken(activity,user.nimToken);
            UserEntity.getUser().setUnionid(AccountPwdLoginActivity.this, "");
            try {
                SensorsDataAPI.sharedInstance(this).login(user.userID);
                setSensorsUserEvent();
            } catch (Exception e) {
                e.printStackTrace();
            }

            connectIM();
            Unicorn.setUserInfo(null);

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("source", getIntentSource());
            map.put("loginstyle", "手机号");
            map.put("head", !TextUtils.isEmpty(user.avatar) ? "是" : "否");
            map.put("nickname", !TextUtils.isEmpty(user.nickname) ? "是" : "否");
            map.put("phone", !TextUtils.isEmpty(user.mobile) ? "是" : "否");
            MobClickUtils.onEvent(StatisticConstant.LOGIN_SUCCEED,map);
            CommonUtils.showToast("登录成功");
            CommonUtils.getAgainstDeviceId();
            if (user.mustRestPwd && passwordEditText.getText() != null) {
                final String password = passwordEditText.getText().toString();
                Intent intent = new Intent(this, InitPasswordActivity.class);
                intent.putExtra(Constants.PARAMS_ACTION, actionBean);
                intent.putExtra(Constants.PARAMS_DATA, password);
                startActivity(intent);
            } else {
                EventBus.getDefault().post(new EventAction(EventType.CLICK_USER_LOGIN));
                CommonUtils.loginDoAction(this, actionBean);
            }
            finish();
        }
    }
    private void connectIM() {
        IMUtil.getInstance().connect();
    }
    @Override
    public void onDataRequestError(ExceptionInfo errorInfo, BaseRequest request) {
        super.onDataRequestError(errorInfo, request);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();

        if(phone.length() >0){
            delete.setVisibility(View.VISIBLE);
        }else{
            delete.setVisibility(View.GONE);
        }

        if(passwordEditText.getText().toString()!= null){
            if(passwordEditText.getText().toString().length() >0){
                delete1.setVisibility(View.VISIBLE);
            }else{
                delete1.setVisibility(View.GONE);
            }
        }

        if (!TextUtils.isEmpty(areaCode) && !TextUtils.isEmpty(phone)
                && !TextUtils.isEmpty(password)&& password.length() >=6
                /*&& Pattern.matches("[\\w]{6,16}", password)*/) {
            loginButton.setEnabled(true);
            //loginButton.setBackgroundColor(getResources().getColor(R.color.login_ready));
        } else {
            loginButton.setEnabled(false);
            //loginButton.setBackgroundColor(getResources().getColor(R.color.login_unready));
        }

        if (!isPwdVisibility) {
            passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            passwordVisible.setImageResource(R.mipmap.login_invisible);
        } else {
            passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            passwordVisible.setImageResource(R.mipmap.login_visible);
        }

        if(passwordEditText.hasFocus() && isFirstEnter){
            passwordEditText.setSelection(passwordEditText.getText().toString().length());
            isFirstEnter = false;
        }
    }

    @Override
    public void finish() {
        // TODO Auto-generated method stub
        super.finish();
        //关闭窗体动画显示
        //this.overridePendingTransition(R.anim.out_to_right,0);
    }

}
