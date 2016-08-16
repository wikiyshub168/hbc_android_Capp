package com.hugboga.custom.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.huangbaoche.hbcframe.data.bean.UserSession;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.huangbaoche.hbcframe.util.MLog;
import com.huangbaoche.hbcframe.util.WXShareUtils;
import com.hugboga.custom.R;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.bean.AreaCodeBean;
import com.hugboga.custom.data.bean.UserBean;
import com.hugboga.custom.data.bean.UserEntity;
import com.hugboga.custom.data.event.EventAction;
import com.hugboga.custom.data.event.EventType;
import com.hugboga.custom.data.request.RequestLogin;
import com.hugboga.custom.data.request.RequestLoginCheckOpenId;
import com.hugboga.custom.utils.ApiFeedbackUtils;
import com.hugboga.custom.utils.CommonUtils;
import com.hugboga.custom.utils.IMUtil;
import com.hugboga.custom.utils.SharedPre;
import com.hugboga.custom.widget.DrawableCenterButton;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created on 16/8/4.
 */

public class LoginActivity extends BaseActivity implements TextWatcher {

    public static String KEY_PHONE = "key_phone";
    public static String KEY_AREA_CODE = "key_area_code";

    @Bind(R.id.change_mobile_areacode)
    TextView areaCodeTextView;
    @Bind(R.id.login_phone)
    EditText phoneEditText;
    @Bind(R.id.login_password)
    EditText passwordEditText;
    @Bind(R.id.login_submit)
    Button loginButton;
    @Bind(R.id.iv_pwd_visible)
    ImageView passwordVisible;
    @Bind(R.id.login_weixin)
    DrawableCenterButton login_weixin;

    @Bind(R.id.login_register)
    TextView login_register;


    boolean isPwdVisibility = false;
    String phone;
    String areaCode;
    @Bind(R.id.header_left_btn)
    ImageView headerLeftBtn;
    @Bind(R.id.header_right_btn)
    ImageView headerRightBtn;
    @Bind(R.id.header_title)
    TextView headerTitle;
    @Bind(R.id.header_right_txt)
    TextView headerRightTxt;
    @Bind(R.id.change_mobile_diepwd)
    TextView changeMobileDiepwd;
    @Bind(R.id.tv_account_tips)
    TextView tvAccountTips;
    private SharedPre sharedPre;
    private String source = "";
    private IWXAPI wxapi;
    public static boolean isWXLogin = false;
    public static String WX_CODE = "";

    protected void initHeader() {
        headerTitle.setText(getString(R.string.login));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fg_login);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
        initHeader();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEventMainThread(EventAction action) {
        switch (action.getType()) {
            case WECHAT_LOGIN_CODE:
                if (action.getData() != null && action.getData() instanceof SendAuth.Resp) {
                    SendAuth.Resp resp = (SendAuth.Resp) action.getData();
                    if (!TextUtils.isEmpty(resp.code) && !TextUtils.isEmpty(resp.state) && resp.state.equals("hbc")) {
                        loginCheckOpenId(resp.code);
                    }
                }
                break;
            case BIND_MOBILE:
                finish();
                break;
            case CHOOSE_COUNTRY_BACK:
                if (!(action.getData() instanceof AreaCodeBean)) {
                    break;
                }
                AreaCodeBean areaCodeBean = (AreaCodeBean) action.getData();
                if (areaCodeBean == null) {
                    break;
                }
                areaCodeTextView.setText("+" + areaCodeBean.getCode());
                break;
            default:
                break;
        }
    }

    public void loginCheckOpenId(String code) {
        RequestLoginCheckOpenId request = new RequestLoginCheckOpenId(activity, code);
        requestData(request);
    }

    protected void initView() {
        login_register.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        login_register.getPaint().setAntiAlias(true);
        String areaCode = null;
        String phone = null;
        if (getIntent() != null) {
            areaCode = getIntent().getStringExtra(KEY_AREA_CODE);
            phone = getIntent().getStringExtra(KEY_PHONE);
            source = getIntent().getStringExtra("source");
        }
        sharedPre = new SharedPre(activity);
        if (TextUtils.isEmpty(areaCode)) {
            areaCode = sharedPre.getStringValue(SharedPre.LOGIN_CODE);
        }
        if (!TextUtils.isEmpty(areaCode)) {
            this.areaCode = areaCode;
            areaCodeTextView.setText("+" + areaCode);
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
        phoneEditText.addTextChangedListener(this);
        passwordEditText.addTextChangedListener(this);
    }


    @Override
    public void onStop() {
        super.onStop();
        this.activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("source", source);
        MobclickAgent.onEvent(activity, "login_launch", map);
    }

    @Override
    public void onDataRequestSucceed(BaseRequest request) {
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

            if (TextUtils.isEmpty(user.imToken)) {
                ApiFeedbackUtils.requestIMFeedback(1, "49001", "获取到的imToken为空，不能连接IM服务器");
                CommonUtils.showToast("服务器忙翻了，请稍后再试");
            }

            connectIM();
            EventBus.getDefault().post(new EventAction(EventType.CLICK_USER_LOGIN));
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("source", source);
            map.put("loginstyle", "手机号");
            map.put("head", !TextUtils.isEmpty(user.avatar) ? "是" : "否");
            map.put("nickname", !TextUtils.isEmpty(user.nickname) ? "是" : "否");
            map.put("phone", !TextUtils.isEmpty(user.mobile) ? "是" : "否");

            MobclickAgent.onEvent(activity, "login_succeed", map);
//            finishForResult(new Bundle());

            CommonUtils.showToast("登录成功");
            finish();

        } else if (request instanceof RequestLoginCheckOpenId) {
            RequestLoginCheckOpenId request1 = (RequestLoginCheckOpenId) request;
            UserBean userBean = request1.getData();
            Log.i("aa", "RequestLoginCheckOpenId " +userBean.isNotRegister);
            if (userBean.isNotRegister == 1) {//未注册，走注册流程
                Bundle bundle = new Bundle();
                bundle.putString("unionid", userBean.unionid);
                bundle.putString("source", "提示弹层");
                Intent intent = new Intent(LoginActivity.this, BindMobileActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("source", "提示弹层");
                MobclickAgent.onEvent(activity, "bind_trigger", map);
            } else {//注册了，有用户信息
                userBean.setUserEntity(activity);
                UserSession.getUser().setUserToken(activity, userBean.userToken);
                connectIM();
                EventBus.getDefault().post(
                        new EventAction(EventType.CLICK_USER_LOGIN));

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("source", source);
                map.put("loginstyle", "微信");
                map.put("head", !TextUtils.isEmpty(userBean.avatar) ? "是" : "否");
                map.put("nickname", !TextUtils.isEmpty(userBean.nickname) ? "是" : "否");
                map.put("phone", !TextUtils.isEmpty(userBean.mobile) ? "是" : "否");
                MobclickAgent.onEvent(activity, "login_succeed", map);
//                finishForResult(new Bundle());
                CommonUtils.showToast("登录成功");
                finish();
            }
        }
    }

    private void connectIM() {
        IMUtil.getInstance().connect();
    }

    @OnClick({R.id.header_left_btn, R.id.login_weixin, R.id.login_submit, R.id.change_mobile_areacode, R.id.login_register, R.id.change_mobile_diepwd, R.id.iv_pwd_visible})
    public void onClick(View view) {
        Intent intent = null;
        HashMap<String, String> map = new HashMap<String, String>();
        switch (view.getId()) {
            case R.id.header_left_btn:
                map.put("source", source);
                MobclickAgent.onEvent(activity, "login_close", map);
                finish();
                break;
            case R.id.login_weixin:
                if (!WXShareUtils.getInstance(activity).isInstall(false)) {
                    CommonUtils.showToast("手机未安装微信或版本太低");
                    return;
                }
                wxapi = WXAPIFactory.createWXAPI(this.activity, Constants.WX_APP_ID);
                wxapi.registerApp(Constants.WX_APP_ID);
                SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "hbc";
                wxapi.sendReq(req);
                isWXLogin = true;

                map.put("source", source);
                MobclickAgent.onEvent(activity, "login_weixin", map);

                break;
            case R.id.login_submit:
                //登录
                loginGo();
                break;
            case R.id.change_mobile_areacode:
                passwordEditText.clearFocus();
                phoneEditText.clearFocus();
                //选择区号
//                collapseSoftInputMethod(); //隐藏键盘
                intent = new Intent(LoginActivity.this, ChooseCountryActivity.class);
                intent.putExtra(KEY_FROM, "login");
                startActivity(intent);
                break;
            case R.id.login_register:
                //注册
//                collapseSoftInputMethod(); //隐藏键盘
                finish();
                Bundle bundle2 = new Bundle();
                bundle2.putString("areaCode", areaCode);
                bundle2.putString("phone", phone);
                bundle2.putString(KEY_FROM, "login");
                if (!TextUtils.isEmpty(source)) {
                    bundle2.putString("source", source);
                }
                intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtras(bundle2);
                startActivity(intent);

                HashMap<String, String> map1 = new HashMap<String, String>();
                map1.put("source", source);
                MobclickAgent.onEvent(activity, "regist_trigger", map1);

                break;
            case R.id.change_mobile_diepwd:
                //忘记密码
//                collapseSoftInputMethod(); //隐藏键盘
                passwordEditText.setText("");
                Bundle bundle1 = new Bundle();
                bundle1.putString("areaCode", areaCode);
                bundle1.putString("phone", phone);
                bundle1.putString(KEY_FROM, "login");
                intent = new Intent(LoginActivity.this, ForgetPasswdActivity.class);
                intent.putExtras(bundle1);
                startActivity(intent);
                break;
            case R.id.iv_pwd_visible:
                if (passwordEditText != null) {
                    if (isPwdVisibility) {//密码可见
                        isPwdVisibility = false;
                        passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        passwordVisible.setImageResource(R.mipmap.icon_pwd_invisible);
                    } else {//密码不可见
                        isPwdVisibility = true;
                        passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        passwordVisible.setImageResource(R.mipmap.icon_pwd_visible);
                    }
                }
                break;
            default:
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
            CommonUtils.showToast("区号不能为空");
            return;
        }
        areaCode = areaCode.replace("+", "");
        phone = phoneEditText.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            CommonUtils.showToast("手机号不能为空");
            return;
        }
        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            CommonUtils.showToast("密码不能为空");
            return;
        }
        if (!Pattern.matches("[\\w]{4,16}", password)) {
            CommonUtils.showToast("密码必须是4-16位数字或字母");
            return;
        }

        RequestLogin request = new RequestLogin(activity, areaCode, phone, password);
        requestData(request);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("source", source);
        MobclickAgent.onEvent(activity, "login_phone", map);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    public void onBackPressed() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("source", source);
        MobclickAgent.onEvent(activity, "login_close", map);
        super.onBackPressed();
    }


    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        if (!TextUtils.isEmpty(areaCode) && !TextUtils.isEmpty(phone)
                && !TextUtils.isEmpty(password)
                && Pattern.matches("[\\w]{4,16}", password)) {
            loginButton.setEnabled(true);
            loginButton.setBackgroundColor(getResources().getColor(R.color.login_ready));
        } else {
            loginButton.setEnabled(false);
            loginButton.setBackgroundColor(getResources().getColor(R.color.login_unready));
        }

    }

}