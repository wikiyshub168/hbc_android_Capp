package com.hugboga.custom.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.huangbaoche.hbcframe.util.MLog;
import com.hugboga.custom.R;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.bean.UserBean;
import com.hugboga.custom.data.bean.UserEntity;
import com.hugboga.custom.data.event.EventAction;
import com.hugboga.custom.data.event.EventType;
import com.hugboga.custom.data.request.RequestChangeUserInfo;
import com.hugboga.custom.data.request.RequestUserInfo;
import com.hugboga.custom.statistic.MobClickUtils;
import com.hugboga.custom.statistic.StatisticConstant;
import com.hugboga.custom.utils.AlbumUploadHelper;
import com.hugboga.custom.utils.AlertDialogUtils;
import com.hugboga.custom.utils.CommonUtils;
import com.hugboga.custom.utils.FileUtil;
import com.hugboga.custom.utils.ImageUtils;
import com.hugboga.custom.utils.PermissionRes;
import com.hugboga.custom.utils.Tools;
import com.hugboga.custom.utils.UploadPicUtils;
import com.hugboga.custom.widget.UpPicDialog;
import com.qiyukf.unicorn.api.Unicorn;
import com.yalantis.ucrop.UCrop;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import net.grobas.view.PolygonImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.AgePicker;

/**
 * Created by on 16/8/6.
 */
public class PersonInfoActivity extends BaseActivity{

    @BindView(R.id.my_info_menu_head1)
    PolygonImageView headImageView;
    @BindView(R.id.my_info_nickname)
    TextView nickNameTextView;
    @BindView(R.id.my_info_sex)
    TextView sexTextView;
    @BindView(R.id.my_info_age)
    TextView ageTextView;
    @BindView(R.id.my_info_mobile)
    TextView mobileTextView;
    @BindView(R.id.my_info_realname)
    TextView realNameTextView;

    UserBean userBean;
    Bitmap head;//头像Bitmap

    boolean isSetHead = false;

    @Override
    public int getContentViewId() {
        return R.layout.fg_my_info;
    }

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        EventBus.getDefault().register(this);
        initDefaultTitleBar();
        fgTitle.setText(R.string.personinfo_title);
        initView();
        requestData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        hideSoftInput();
    }

    @Override
    public String getEventSource() {
        return "我的资料";
    }

    private void initView() {
        if (userBean == null) {
            return;
        }
        if (!isSetHead) {
            if (!TextUtils.isEmpty(userBean.avatar)) {
                Tools.showImage(headImageView, userBean.avatar, R.mipmap.icon_avatar_user);
            } else {
                headImageView.setImageResource(R.mipmap.icon_avatar_user);
            }
        } else {
            isSetHead = false;
        }
        if (!TextUtils.isEmpty(userBean.nickname)) {
            nickNameTextView.setText(userBean.nickname);
        }
        if (!TextUtils.isEmpty(userBean.gender)) {
            sexTextView.setText(userBean.getGenderStr());
        }
        if (userBean.ageType != -1) {
            ageTextView.setText(userBean.getAgeStr());
        }
        if (!TextUtils.isEmpty(userBean.mobile)) {
            mobileTextView.setText(userBean.mobile);
        }
        if (!TextUtils.isEmpty(userBean.name)) {
            realNameTextView.setText(userBean.name);
        }
        fgLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new EventAction(EventType.SETTING_BACK));
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        EventBus.getDefault().post(new EventAction(EventType.SETTING_BACK));
        finish();
    }
    @Subscribe
    public void onEventMainThread(EventAction action) {
        switch (action.getType()) {
            case CHANGE_MOBILE:
                requestData();
                break;
            case BIND_MOBILE:
                if (action.getData() instanceof UserBean) {
                    userBean = (UserBean) action.getData();
                    initView();
                } else {
                    requestData();
                }
                break;
        }
    }

    @Override
    public void onDataRequestSucceed(BaseRequest request) {
        super.onDataRequestSucceed(request);
        if (request instanceof RequestUserInfo) {
            RequestUserInfo requestUserInfo = (RequestUserInfo) request;
            userBean = requestUserInfo.getData();
            if (userBean == null) {
                return;
            }
            UserEntity.getUser().setNickname(this, userBean.nickname);
            UserEntity.getUser().setAvatar(this, userBean.avatar);
            UserEntity.getUser().setUserName(this, userBean.name);
            UserEntity.getUser().setTravelFund(this, userBean.travelFund);
            UserEntity.getUser().setCoupons(this, userBean.coupons);
            UserEntity.getUser().setAreaCode(this, userBean.areaCode);
            UserEntity.getUser().setPhone(this, userBean.mobile);
            initView();
        } else if (request instanceof RequestChangeUserInfo) {
            RequestChangeUserInfo requestChangeUserInfo = (RequestChangeUserInfo) request;
            userBean = requestChangeUserInfo.getData();
            UserEntity.getUser().setNickname(this, userBean.nickname);
            UserEntity.getUser().setAvatar(this, userBean.avatar);
            UserEntity.getUser().setUserName(this, userBean.name);
            UserEntity.getUser().setAreaCode(activity, userBean.areaCode);
            UserEntity.getUser().setPhone(activity, userBean.mobile);
            initView();
            EventBus.getDefault().post(new EventAction(EventType.CLICK_USER_LOGIN));
        }
    }
    AgePicker agePicker;
    int sexInt = 0;
    @OnClick({R.id.my_info_menu_layout1, R.id.my_info_menu_layout2, R.id.my_info_menu_layout3, R.id.my_info_menu_layout4, R.id.my_info_menu_layout5, R.id.my_info_menu_realname_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_info_menu_layout1:
                //头像
                grantSdcard();
                break;
            case R.id.my_info_menu_layout2:
                //昵称
                RelativeLayout rl = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.fg_person_info_nick, null);
                final EditText inputServer = (EditText) rl.findViewById(R.id.person_info_nick_text);
                inputServer.setText(nickNameTextView.getText().toString());
                inputServer.setSelection(inputServer.getText().length());
                AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(rl)/*.setTitle("填写昵称")*/.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        im.hideSoftInputFromWindow(inputServer.getWindowToken(), 0);
                        dialog.cancel();
                    }
                }).setPositiveButton(R.string.hbc_submit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String nickStr = inputServer.getText().toString().trim();
                        if (TextUtils.isEmpty(nickStr)) {
                            showTip(CommonUtils.getString(R.string.personinfo_check_nickname));
                            return;
                        }
                        nickStr = nickStr.replaceAll(" ", "");
                        nickNameTextView.setText(nickStr);
                        submitChangeUserInfo(2, nickStr);
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                break;
            case R.id.my_info_menu_layout3:
                //性别
                final CharSequence[] items3 = getResources().getStringArray(R.array.my_info_sex);
                final AlertDialog.Builder sexDialog = new AlertDialog.Builder(activity);
                sexDialog.setTitle("选择性别");
                sexDialog.setSingleChoiceItems(items3, getSexInt(items3), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sexInt = which;
                        String sexStr = items3[which].toString();
                        sexTextView.setText(sexStr);
                        submitChangeUserInfo(3,String.valueOf(sexInt + 1));
                        dialog.dismiss();
                    }
                });

                Dialog sexDialog2 = sexDialog.create();
                sexDialog2.setCancelable(true);
                sexDialog2.setCanceledOnTouchOutside(true);
                sexDialog2.show();
                /*agePicker = new AgePicker(PersonInfoActivity.this, items3);
                //agePicker.setTitleText("性别");
                //agePicker.setTitleTextSize(16);
                //agePicker.setTitleTextColor(getResources().getColor(R.color.reserve_calendar_week_color2));
                agePicker.setCancelText(R.string.cancel);
                agePicker.setSubmitText(R.string.hbc_confirm);
                //agePicker.setTopLineHeight(0.5f);
                //agePicker.setTopLineColor(getResources().getColor(R.color.pickerLine));
                agePicker.setTopLineVisible(false);
                agePicker.setCancelTextColor(getResources().getColor(R.color.guildsaved));
                agePicker.setSubmitTextColor(getResources().getColor(R.color.default_yellow));
                agePicker.setTopBackgroundColor(getResources().getColor(R.color.allbg_white));
                agePicker.setLineConfig(null);
                agePicker.setOnItemPickListener(
                        new SinglePicker.OnItemPickListener<String>() {
                            @Override
                            public void onItemPicked(int index, String item) {
                                String agesStr = items3[index].toString();
                                sexTextView.setText(agesStr);
                                submitChangeUserInfo(3,String.valueOf(index + 1));
                            }
                        }
                );
                agePicker.show();*/
                break;
            case R.id.my_info_menu_layout4:
                //年龄
                final String[] ages = getResources().getStringArray(R.array.my_info_age);
                AlertDialog.Builder ageDialogBuild = new AlertDialog.Builder(this);
                ageDialogBuild.setTitle("选择年龄");
                ageDialogBuild.setSingleChoiceItems(ages, getAgeInt(ages), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String agesStr = ages[which].toString();
                        ageTextView.setText(agesStr);
                        dialog.dismiss();
                        submitChangeUserInfo(4, String.valueOf(which));
                    }
                });
                Dialog ageDialog = ageDialogBuild.create();
                ageDialog.setCancelable(true);
                ageDialog.setCanceledOnTouchOutside(true);
                ageDialog.show();
                /*agePicker = new AgePicker(PersonInfoActivity.this, ages);
                //agePicker.setTitleText("年龄");
                //agePicker.setTitleTextSize(16);
                //agePicker.setTitleTextColor(getResources().getColor(R.color.reserve_calendar_week_color2));
                agePicker.setCancelText(R.string.cancel);
                agePicker.setSubmitText(R.string.hbc_confirm);
                //agePicker.setTopLineHeight(0.5f);
                //agePicker.setTopLineColor(getResources().getColor(R.color.pickerLine));
                agePicker.setTopLineVisible(false);
                agePicker.setCancelTextColor(getResources().getColor(R.color.guildsaved));
                agePicker.setSubmitTextColor(getResources().getColor(R.color.default_yellow));
                agePicker.setTopBackgroundColor(getResources().getColor(R.color.allbg_white));
                //agePicker.setLineColor(getResources().getColor(R.color.text_hint_color));
                agePicker.setLineConfig(null);
                agePicker.setOnItemPickListener(
                        new SinglePicker.OnItemPickListener<String>() {
                            @Override
                            public void onItemPicked(int index, String item) {
                                String agesStr = ages[index].toString();
                                ageTextView.setText(agesStr);
                                submitChangeUserInfo(4, String.valueOf(index));
                            }
                        }
                );
                agePicker.show();*/
                break;
            case R.id.my_info_menu_layout5:
                Intent intent = null;
                if(userBean != null && !TextUtils.isEmpty(userBean.mobile)){
                    //修改手机号
                    intent = new Intent(PersonInfoActivity.this, ChangeMobileActivtiy.class);
                    startActivity(intent);
                }else {
                    //绑定手机号
                    if(userBean != null){
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("isAfterProcess",true);
                        bundle.putString("source", getEventSource());
                        bundle.putString("key_phone",userBean.mobile);
                        bundle.putString("key_area_code",userBean.areaCode);
                        intent = new Intent(PersonInfoActivity.this, BindMobileActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        MobClickUtils.onEvent(StatisticConstant.BIND_TRIGGER);
                    }

                }
                break;
            case R.id.my_info_menu_realname_layout://真实姓名
                RelativeLayout layout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.fg_person_info_nick, null);
                final EditText editText = (EditText) layout.findViewById(R.id.person_info_nick_text);
                editText.setText(realNameTextView.getText().toString());
                editText.setSelection(editText.getText().length());
                final TextView title = (TextView) layout.findViewById(R.id.person_info_nick_title);
                title.setText(R.string.personinfo_username);
                AlertDialog.Builder realNameBuilder = new AlertDialog.Builder(this).setView(layout)/*.setTitle("填写真实姓名")*/.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        im.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                        dialog.cancel();
                    }
                }).setPositiveButton(R.string.hbc_submit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String nickStr = editText.getText().toString().trim();
                        if (TextUtils.isEmpty(nickStr)) {
                            CommonUtils.showToast(R.string.personinfo_check_username);
                            return;
                        }else{
                            for(int i = 0;i< nickStr.length();i++) {
                                if (!Tools.isEmojiCharacter(nickStr.charAt(i))) {
                                    CommonUtils.showToast(R.string.personinfo_check_username_isemoji);
                                    return;
                                }
                            }
                        }
                        nickStr = nickStr.replaceAll(" ", "");
                        realNameTextView.setText(nickStr);
                        submitChangeUserInfo(6, nickStr);
                    }
                });
                final AlertDialog realNameDialog = realNameBuilder.create();
                realNameDialog.setCanceledOnTouchOutside(false);
                realNameDialog.show();
                break;
            default:
                break;
        }
    }

    /**
     * 授权获取照相机权限
     */
    private void grantSdcard() {
        MPermissions.requestPermissions(this, PermissionRes.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
    UpPicDialog upPicDialog;
    @PermissionGrant(PermissionRes.WRITE_EXTERNAL_STORAGE)
    public void requestSdcardSuccess() {
        cropPic = ImageUtils.getPhotoFileName();
        newPic = "new"+cropPic;
        //修改头像
        /*final CharSequence[] items = getResources().getStringArray(R.array.my_info_phone_type);
        AlertDialog.Builder builder1= new AlertDialog.Builder(this)*//*.setTitle("上传头像")*//*.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    grantCamera();
                } else if (which == 1) {
                    choosePhoto();
                }
            }
        });
        AlertDialog dialog = builder1.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();*/
        upPicDialog =  CommonUtils.uppicDialog(this);
    }


    @PermissionDenied(PermissionRes.WRITE_EXTERNAL_STORAGE)
    public void requestSdcardFailed() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertDialogUtils.showAlertDialog(this, true, true, getString(R.string.grant_fail_title), getString(R.string.grant_fail_phone1));
        } else {
            AlertDialogUtils.showAlertDialog(this, true, getString(R.string.grant_fail_title), getString(R.string.grant_fail_sdcard)
                    , getString(R.string.grant_fail_btn)
                    , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            grantSdcard();
                        }
                    });
        }
    }

    public void choosePhoto(){
        Intent phoneIntent = new Intent(Intent.ACTION_PICK, null);
        phoneIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(phoneIntent, 2);
    }
    /**
     * 授权获取照相机权限
     */
    public void grantCamera() {
        MPermissions.requestPermissions(this, PermissionRes.CAMERA, android.Manifest.permission.CAMERA);
    }

    @PermissionGrant(PermissionRes.CAMERA)
    public void requestPhoneSuccess() {
        //拍照
        ImageUtils.checkDir(); //检查并创建图片目录
        Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Constants.IMAGE_DIR, cropPic);
        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(takeIntent, 1);
    }

    @PermissionDenied(PermissionRes.CAMERA)
    public void requestPhoneFailed() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {
            AlertDialogUtils.showAlertDialog(this, true, true, getString(R.string.grant_fail_title), getString(R.string.grant_fail_phone1));
        } else {
            AlertDialogUtils.showAlertDialog(this, true, getString(R.string.grant_fail_title), getString(R.string.grant_fail_camera)
                    , getString(R.string.grant_fail_btn)
                    , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            grantCamera();
                        }
                    });
        }
    }

    /**
     * 获取年龄选择项
     *
     * @param ages
     * @return
     */
    private int getAgeInt(CharSequence[] ages) {
        String str = ageTextView.getText().toString();
        if (str == null || str.isEmpty()) {
            return -1;
        }
        for (int i = 0; i < ages.length; i++) {
            if (str.equals(ages[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 选择性别选择项
     *
     * @param items3
     * @return
     */
    private int getSexInt(CharSequence[] items3) {
        String str = sexTextView.getText().toString();
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

    private Integer getAgeLevel(Integer item) {
        switch (item) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            default:
                return -1;
        }
    }

    private String cropPic = null;
    private String newPic = null;
    /**
     * 调用系统剪切图片
     */
    public void cropPhoto() {
        try {
            File oldFile = new File(Constants.IMAGE_DIR, cropPic);
            File newFile = new File(Constants.IMAGE_DIR, newPic);
            UCrop.Options options = new UCrop.Options();
            options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
            UCrop.of(Uri.fromFile(oldFile), Uri.fromFile(newFile)).withAspectRatio(1f, 1f).withOptions(options).withMaxResultSize(200, 200).start(PersonInfoActivity.this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private String setPicToView(Bitmap mBitmap) {
        FileOutputStream b = null;
        ImageUtils.checkDir(); //检查并创建图片目录
        String fileName = Constants.IMAGE_DIR + File.separator + cropPic;//Constants.HEAD_IMAGE;//图片名字
        try {
            b = new FileOutputStream(fileName);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
            b.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭流
                if (b != null) {
                    b.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return fileName;
    }

    /**
     * 提交修改后的个人资料
     *
     * @param type
     * @param str
     */
    private void submitChangeUserInfo(Integer type, String str) {
        RequestChangeUserInfo request = null;
        switch (type) {
            case 1:
                //提交头像
                request = new RequestChangeUserInfo(this, str, null, null, null, null, null);
                break;
            case 2:
                //提交昵称
                request = new RequestChangeUserInfo(this, null, str, null, null, null, null);
                break;
            case 3:
                //提交性别
                request = new RequestChangeUserInfo(this, null, null, str, null, null, null);
                break;
            case 4:
                //提交年龄
                request = new RequestChangeUserInfo(this, null, null, null, str, null, null);
                break;
            case 5:
                //提交签名
                request = new RequestChangeUserInfo(this, null, null, null, null, str, null);
                break;
            case 6:
                //真实姓名
                request = new RequestChangeUserInfo(this, null, null, null, null, null, str);
                break;
            default:
                break;
        }
        if (request != null) {
            Unicorn.setUserInfo(null);
            requestData(request);
        }
    }

    protected int getBusinessType() {
        return Constants.BUSINESS_TYPE_OTHER;
    }

    private void requestData() {
        RequestUserInfo requestUserInfo = new RequestUserInfo(this);
        requestData(requestUserInfo);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    cropPhoto();//裁剪图片
                }
                break;
            case 2:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;
                        Bitmap bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(data.getData()), null, options);
                        FileUtil.saveBitmapToFile(bitmap, Constants.IMAGE_DIR, cropPic);
                        cropPhoto();//裁剪图片
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case UCrop.REQUEST_CROP:
                if (resultCode == Activity.RESULT_OK) {
                    final Uri resultUri = UCrop.getOutput(data);
                    if (resultUri != null) {
                        Bitmap bitmap = getBitmapFromUri(resultUri);
                        if (bitmap != null) {
                            isSetHead = true;
                            headImageView.setImageURI(resultUri);
                            String fileName = setPicToView(bitmap);//保存在SD卡中
                            MLog.e("fileName=" + fileName);
                            uploadPic(fileName);
//                            headImageView.setImageBitmap(bitmap);//用ImageView显示出来
                        }
                    }
                } else if (resultCode == UCrop.RESULT_ERROR) {
                    final Throwable cropError = UCrop.getError(data);
                    cropError.printStackTrace();
                }
                if(upPicDialog!=null && upPicDialog.isShowing()){
                    upPicDialog.dismiss();
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    private void uploadPic(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            uploadPic(file);
        }
    }

    /**
     * 上传图片
     * type资源类型：图片 1  视频 2
     * refId 用户id
     * refType
     * // 实体对象类型： 导游车辆 1;
     * // 实体对象类型： 推送消息  2;
     * // 实体对象类型： 导游头像 3;
     * // 用户头像 4;
     *
     * @param file
     */
    private void uploadPic(File file) {
        new UploadPicUtils(this, file, new AlbumUploadHelper.UploadListener() {
            @Override
            public void onPostUploadProgress(int fid, String percent) {

            }

            @Override
            public void onPostUploadSuccess(int fid, String uploadFileUrl) {
                submitChangeUserInfo(1, uploadFileUrl);
            }

            @Override
            public void onPostUploadFail(int fid, String message) {
                CommonUtils.showToast(message);
            }

            @Override
            public void onPostAllUploaded() {

            }

            @Override
            public void onPostUploadCancleAll() {

            }
        });
    }

    private void showTip(String tips) {
        CommonUtils.showToast(tips);
    }
}
