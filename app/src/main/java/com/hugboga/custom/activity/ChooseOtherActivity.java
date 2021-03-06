package com.hugboga.custom.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hugboga.custom.R;
import com.hugboga.custom.data.bean.AreaCodeBean;
import com.hugboga.custom.data.bean.ContactUsersBean;
import com.hugboga.custom.data.event.EventAction;
import com.hugboga.custom.data.event.EventType;
import com.hugboga.custom.utils.CommonUtils;
import com.hugboga.custom.utils.PhoneInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.OnClick;

import static com.hugboga.custom.R.id.add_other_phone_click;
import static com.hugboga.custom.R.id.passenger_phone_text;
import static com.hugboga.custom.R.id.user_phone_text;
import static com.hugboga.custom.data.event.EventType.CONTACT;

/**
 * Created on 16/8/4.
 */

public class ChooseOtherActivity extends BaseActivity {

    @BindView(R.id.header_left_btn)
    ImageView headerLeftBtn;
    @BindView(R.id.header_title)
    TextView headerTitle;
    @BindView(R.id.header_right_btn)
    ImageView headerRightBtn;
    @BindView(R.id.header_right_txt)
    TextView headerRightTxt;
    @BindView(R.id.name_left)
    TextView nameLeft;
    @BindView(R.id.name_right)
    TextView nameRight;
    @BindView(R.id.name_text)
    EditText nameText;
    @BindView(R.id.user_phone_text_code_click)
    TextView userPhoneTextCodeClick;
    @BindView(user_phone_text)
    EditText userPhoneText;
    @BindView(R.id.name1_left)
    TextView name1Left;
    @BindView(R.id.name1_del)
    TextView name1Del;
    @BindView(R.id.name1_right)
    TextView name1Right;
    @BindView(R.id.name1_text)
    EditText name1Text;
    @BindView(R.id.user1_phone_text_code_click)
    TextView user1PhoneTextCodeClick;
    @BindView(R.id.user1_phone_text)
    EditText user1PhoneText;
    @BindView(R.id.user1_layout)
    LinearLayout user1Layout;
    @BindView(R.id.name2_left)
    TextView name2Left;
    @BindView(R.id.name2_del)
    TextView name2Del;
    @BindView(R.id.name2_right)
    TextView name2Right;
    @BindView(R.id.name2_text)
    EditText name2Text;
    @BindView(R.id.user2_phone_text_code_click)
    TextView user2PhoneTextCodeClick;
    @BindView(R.id.user2_phone_text)
    EditText user2PhoneText;
    @BindView(R.id.user2_layout)
    LinearLayout user2Layout;
    @BindView(add_other_phone_click)
    TextView addOtherPhoneClick;
    @BindView(R.id.other_left)
    TextView otherLeft;
    @BindView(R.id.other_check)
    CheckBox otherCheck;
    @BindView(R.id.passenger_left)
    TextView passengerLeft;
    @BindView(R.id.passenger_right)
    TextView passengerRight;
    @BindView(R.id.passenger_text)
    EditText passengerText;
    @BindView(R.id.passenger_phone_text_code_click)
    TextView passengerPhoneTextCodeClick;
    @BindView(passenger_phone_text)
    EditText passengerPhoneText;
    @BindView(R.id.message_left)
    TextView messageLeft;
    @BindView(R.id.message_check)
    CheckBox messageCheck;
    @BindView(R.id.message_right)
    TextView messageRight;
    @BindView(R.id.other_layout)
    LinearLayout otherLayout;


    ContactUsersBean contactUsersBean;
    protected void initHeader() {

        headerLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideInputMethod(name1Text);
                finish();
            }
        });
        headerTitle.setText(R.string.choose_other_title);
        headerRightTxt.setText(R.string.hbc_confirm);
        headerRightTxt.setVisibility(View.VISIBLE);
        headerRightTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(nameText.getText())
                        || TextUtils.isEmpty(nameText.getText().toString().trim())
                        || TextUtils.isEmpty(userPhoneText.getText())
                        || TextUtils.isEmpty(userPhoneText.getText().toString().trim())
                        || TextUtils.isEmpty(userPhoneTextCodeClick.getText())) {
                    CommonUtils.showToast("联系人名称和电话不能为空");
                    return;
                }

                if(user1Layout.isShown() && (TextUtils.isEmpty(name1Text.getText())
                        || TextUtils.isEmpty(name1Text.getText().toString().trim())
                        || TextUtils.isEmpty(user1PhoneText.getText())
                        || TextUtils.isEmpty(user1PhoneText.getText().toString().trim())
                        || TextUtils.isEmpty(user1PhoneTextCodeClick.getText()))) {
                    CommonUtils.showToast("备用联系人名称和电话不能为空");
                    return;
                }

                if(user2Layout.isShown() && (TextUtils.isEmpty(name2Text.getText())
                        || TextUtils.isEmpty(name2Text.getText().toString().trim())
                        || TextUtils.isEmpty(user2PhoneText.getText())
                        || TextUtils.isEmpty(user2PhoneText.getText().toString().trim())
                        || TextUtils.isEmpty(user2PhoneTextCodeClick.getText()))) {
                    CommonUtils.showToast("备用联系人名称和电话不能为空");
                    return;
                }

                if(otherCheck.isChecked() && (TextUtils.isEmpty(passengerText.getText())
                        || TextUtils.isEmpty(passengerText.getText().toString().trim())
                        || TextUtils.isEmpty(passengerPhoneTextCodeClick.getText())
                        || TextUtils.isEmpty(passengerPhoneText.getText())
                        || TextUtils.isEmpty(passengerPhoneText.getText().toString().trim()))) {
                    CommonUtils.showToast("乘车人名称和电话不能为空");
                    return;
                }
                hideSoftInput();

                contactUsersBean = new ContactUsersBean();
                contactUsersBean.userName = nameText.getText().toString();
                contactUsersBean.phoneCode = CommonUtils.removePhoneCodeSign(userPhoneTextCodeClick.getText().toString());
                contactUsersBean.userPhone = userPhoneText.getText().toString();

                contactUsersBean.user1Name = name1Text.getText().toString();
                contactUsersBean.phone1Code = CommonUtils.removePhoneCodeSign(user1PhoneTextCodeClick.getText().toString());
                contactUsersBean.user1Phone = user1PhoneText.getText().toString();

                contactUsersBean.user2Name = name2Text.getText().toString();
                contactUsersBean.phone2Code = CommonUtils.removePhoneCodeSign(user2PhoneTextCodeClick.getText().toString());
                contactUsersBean.user2Phone = user2PhoneText.getText().toString();

                contactUsersBean.otherName = passengerText.getText().toString();
                contactUsersBean.otherphoneCode = CommonUtils.removePhoneCodeSign(passengerPhoneTextCodeClick.getText().toString());
                contactUsersBean.otherPhone = passengerPhoneText.getText().toString();
                contactUsersBean.isForOther = otherCheck.isChecked();
                contactUsersBean.isSendMessage = messageCheck.isChecked();

                EventBus.getDefault().post(new EventAction(EventType.CONTACT_BACK, contactUsersBean));
                finish();

            }
        });
    }

    // 接收通讯录的选择号码事件
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data == null) {
                return;
            }
            if (PICK_CONTACTS == requestCode) {
                Uri result = data.getData();
                String[] contact = PhoneInfo.getPhoneContacts(this, result);
//                org.greenrobot.eventbus.EventBus.getDefault().post(new EventAction(CONTACT, contact));
                if (TextUtils.isEmpty(contact[1]) && TextUtils.isEmpty(contact[0])){
                    return;
                }
                        switch (clickViewId) {
                            case R.id.name_right:
                                nameText.setText(contact[0]);
                                String userPhone = contact[1];
                                userPhoneText.setText(""+userPhone.replace("+86",""));
                                break;
                            case R.id.name1_right:
                                String user1Phone = contact[1];
                                name1Text.setText(contact[0]);
                                user1PhoneText.setText(""+user1Phone.replace("+86",""));
                                break;
                            case R.id.name2_right:
                                String user2Phone = contact[1];
                                name2Text.setText(contact[0]);
                                user2PhoneText.setText(""+user2Phone.replace("+86",""));
                                break;
                            case R.id.passenger_right:
                                String passPhone = contact[1];
                                passengerText.setText(contact[0]);
                                passengerPhoneText.setText(""+passPhone.replace("+86",""));
                                break;
                        }

            }
        }
    }

    protected void initView() {
        getPermission();
        otherCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    otherLayout.setVisibility(View.VISIBLE);
                }else{
                    otherLayout.setVisibility(View.GONE);
                }
            }
        });

        contactUsersBean = (ContactUsersBean)this.getIntent().getSerializableExtra("contactUsersBean");
        if(null != contactUsersBean){
            if(!TextUtils.isEmpty(contactUsersBean.userName)) {
                nameText.setText(contactUsersBean.userName);
            }
            if(!TextUtils.isEmpty(contactUsersBean.phoneCode)) {
                userPhoneTextCodeClick.setText(CommonUtils.addPhoneCodeSign(contactUsersBean.phoneCode));
            }
            if(!TextUtils.isEmpty(contactUsersBean.userPhone)) {
                userPhoneText.setText(contactUsersBean.userPhone);
            }

            if(!TextUtils.isEmpty(contactUsersBean.user1Name)){
                user1Layout.setVisibility(View.VISIBLE);
            }
            if(!TextUtils.isEmpty(contactUsersBean.user1Name)) {
                name1Text.setText(contactUsersBean.user1Name);
            }
            if(!TextUtils.isEmpty(contactUsersBean.phone1Code)) {
                user1PhoneTextCodeClick.setText(CommonUtils.addPhoneCodeSign(contactUsersBean.phone1Code));
            }
            if(!TextUtils.isEmpty(contactUsersBean.user1Phone)) {
                user1PhoneText.setText(contactUsersBean.user1Phone);
            }

            if(!TextUtils.isEmpty(contactUsersBean.user2Name)){
                user2Layout.setVisibility(View.VISIBLE);
                addOtherPhoneClick.setVisibility(View.GONE);
            }

            if(!TextUtils.isEmpty(contactUsersBean.user2Name)) {
                name2Text.setText(contactUsersBean.user2Name);
            }
            if(!TextUtils.isEmpty(contactUsersBean.phone2Code)) {
                user2PhoneTextCodeClick.setText(CommonUtils.addPhoneCodeSign(contactUsersBean.phone2Code));
            }
            if(!TextUtils.isEmpty(contactUsersBean.user2Phone)) {
                user2PhoneText.setText(contactUsersBean.user2Phone);
            }

            if(!TextUtils.isEmpty(contactUsersBean.otherName)) {
                passengerText.setText(contactUsersBean.otherName);
            }
            if(!TextUtils.isEmpty(contactUsersBean.otherphoneCode)) {
                passengerPhoneTextCodeClick.setText(CommonUtils.addPhoneCodeSign(contactUsersBean.otherphoneCode));
            }
            if(!TextUtils.isEmpty(contactUsersBean.otherPhone)) {
                passengerPhoneText.setText(contactUsersBean.otherPhone);
            }

            if(contactUsersBean.isForOther) {
                otherCheck.setChecked(true);
                otherLayout.setVisibility(View.VISIBLE);
            }
            if(contactUsersBean.isSendMessage){
                messageCheck.setChecked(true);
            }
        }

    }

    @Override
    public int getContentViewId() {
        return R.layout.add_user_layout;
    }

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        EventBus.getDefault().register(this);
        initView();
        initHeader();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private  int clickViewId = -1;//点击的通讯录view id
    @Subscribe
    public void onEventMainThread(EventAction action) {
        if(action.getType() == CONTACT){
            String[] contact = (String[])action.getData();
            switch (action.getType()) {
                case CONTACT:
                    if (TextUtils.isEmpty(contact[1]) && TextUtils.isEmpty(contact[0])){
                        return;
                    }
                    switch (clickViewId) {
                        case R.id.name_right:
                            nameText.setText(contact[0]);
                            String userPhone = contact[1];
                            userPhoneText.setText(""+userPhone.replace("+86",""));
                            break;
                        case R.id.name1_right:
                            String user1Phone = contact[1];
                            name1Text.setText(contact[0]);
                            user1PhoneText.setText(""+user1Phone.replace("+86",""));
                            break;
                        case R.id.name2_right:
                            String user2Phone = contact[1];
                            name2Text.setText(contact[0]);
                            user2PhoneText.setText(""+user2Phone.replace("+86",""));
                            break;
                        case R.id.passenger_right:
                            String passPhone = contact[1];
                            passengerText.setText(contact[0]);
                            passengerPhoneText.setText(""+passPhone.replace("+86",""));
                            break;
                    }
                    break;
                default:
                    break;
            }
        }else if(action.getType() == EventType.CHOOSE_COUNTRY_BACK){
            AreaCodeBean areaCodeBean = (AreaCodeBean)action.getData();
            ((TextView)findViewById(areaCodeBean.viewId)).setText("+" + areaCodeBean.getCode());
        }
    }

    private  final int PICK_CONTACTS = 101;

    @OnClick({R.id.name1_del,R.id.name2_del,R.id.name_right,R.id.name1_right,R.id.name2_right,R.id.passenger_right,R.id.add_other_phone_click,R.id.user_phone_text_code_click, R.id.user1_phone_text_code_click, R.id.user2_phone_text_code_click, R.id.passenger_phone_text_code_click})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.name1_del:
                user1Layout.setVisibility(View.GONE);
                addOtherPhoneClick.setVisibility(View.VISIBLE);
                name1Text.setText("");
                user1PhoneText.setText("");
                break;
            case R.id.name2_del:
                user2Layout.setVisibility(View.GONE);
                addOtherPhoneClick.setVisibility(View.VISIBLE);
                name2Text.setText("");
                user2PhoneText.setText("");
                break;
            case R.id.name_right:
            case R.id.name1_right:
            case R.id.name2_right:
            case R.id.passenger_right:
                clickViewId = view.getId();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setData(ContactsContract.Contacts.CONTENT_URI);
                activity.startActivityForResult(intent, PICK_CONTACTS);
                break;
            case R.id.add_other_phone_click:
                if(user1Layout.isShown()) {
                    user2Layout.setVisibility(View.VISIBLE);
                    addOtherPhoneClick.setVisibility(View.GONE);
                }else{
                    user1Layout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.user_phone_text_code_click:
            case R.id.user1_phone_text_code_click:
            case R.id.user2_phone_text_code_click:
            case R.id.passenger_phone_text_code_click:
                Bundle bundleCode = new Bundle();
                bundleCode.putInt("viewId", view.getId());
                intent = new Intent(activity,ChooseCountryActivity.class);
                intent.putExtras(bundleCode);
                startActivity(intent);
                break;
        }
    }


    private void getPermission() {
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.READ_CONTACTS},
                    1);
        }
    }
}
