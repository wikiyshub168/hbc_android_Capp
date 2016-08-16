package com.hugboga.custom.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.hugboga.custom.BuildConfig;
import com.hugboga.custom.R;
import com.hugboga.custom.data.bean.CheckVersionBean;
import com.hugboga.custom.data.bean.UserEntity;
import com.hugboga.custom.data.net.UrlLibs;
import com.hugboga.custom.data.request.RequestCheckVersion;
import com.hugboga.custom.utils.CommonUtils;
import com.hugboga.custom.utils.PushUtils;
import com.hugboga.custom.utils.SharedPre;
import com.hugboga.custom.utils.UpdateResources;
import com.hugboga.custom.widget.DialogUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;

/**
 * Created on 16/8/6.
 */

public class AboutActivity extends BaseActivity {
    @Bind(R.id.about_version_tv)
    TextView versionTV;
    @Bind(R.id.header_left_btn)
    ImageView headerLeftBtn;
    @Bind(R.id.header_right_btn)
    ImageView headerRightBtn;
    @Bind(R.id.header_title)
    TextView headerTitle;
    @Bind(R.id.header_right_txt)
    TextView headerRightTxt;
    @Bind(R.id.about_logo_layout)
    LinearLayout aboutLogoLayout;
    @Bind(R.id.about_update_tv)
    TextView aboutUpdateTv;
    @Bind(R.id.about_update_arrow_iv)
    ImageView aboutUpdateArrowIv;
    @Bind(R.id.about_update_layout)
    RelativeLayout aboutUpdateLayout;
    @Bind(R.id.about_grade_layout)
    RelativeLayout aboutGradeLayout;
    @Bind(R.id.about_story_layout)
    RelativeLayout aboutStoryLayout;

    protected void initHeader() {
        headerLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        headerTitle.setText(activity.getString(R.string.about_title));
        versionTV.setText(activity.getString(R.string.about_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));
    }

    @Override
    public void onDataRequestSucceed(BaseRequest request) {
        if (request instanceof RequestCheckVersion) {
            RequestCheckVersion requestCV = (RequestCheckVersion) request;
            final CheckVersionBean checkVersionBean = requestCV.getData();
            if (TextUtils.isEmpty(checkVersionBean.url)) {
                DialogUtil.getInstance(activity).showCustomDialog(activity.getString(R.string.about_newest));
            }
            UserEntity.getUser().setIsNewVersion(activity, !TextUtils.isEmpty(checkVersionBean.url));
            DialogUtil.getInstance(activity).showUpdateDialog(checkVersionBean.hasAppUpdate, checkVersionBean.force, checkVersionBean.content, checkVersionBean.url, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PushUtils.startDownloadApk(activity, checkVersionBean.url);
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
            UpdateResources.checkRemoteResources(activity, checkVersionBean, null);
            UpdateResources.checkRemoteDB(activity, checkVersionBean.dbDownloadLink, checkVersionBean.dbVersion, null);
        }
    }

    @OnClick({R.id.about_update_layout, R.id.about_grade_layout, R.id.about_story_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.about_update_layout://软件更新
                int resourcesVersion = new SharedPre(activity).getIntValue(SharedPre.RESOURCES_H5_VERSION);
                final RequestCheckVersion requestCheckVersion = new RequestCheckVersion(activity, resourcesVersion);
                requestData(requestCheckVersion);
                break;
            case R.id.about_grade_layout://给我评价
                try {
                    Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    CommonUtils.showToast(activity.getString(R.string.about_no_appstore));
                }
                break;
            case R.id.about_story_layout://关于我们

//                Bundle bundle = new Bundle();
//                bundle.putString(FgWebInfo.WEB_URL, UrlLibs.H5_ABOUT);
//                startFragment(new FgWebInfo(), bundle);

                Intent intent = new Intent(activity, WebInfoActivity.class);
                intent.putExtra(WebInfoActivity.WEB_URL, UrlLibs.H5_ABOUT);
                startActivity(intent);

                break;
            default:
                break;
        }
    }

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.fg_about);
        ButterKnife.bind(this);
        initHeader();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
