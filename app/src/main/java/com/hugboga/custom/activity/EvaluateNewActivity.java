package com.hugboga.custom.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.huangbaoche.hbcframe.data.bean.UserSession;
import com.huangbaoche.hbcframe.data.request.BaseRequest;
import com.huangbaoche.hbcframe.util.MLog;
import com.huangbaoche.hbcframe.util.WXShareUtils;
import com.huangbaoche.imageselector.ImageLoader;
import com.huangbaoche.imageselector.ImgSelActivity;
import com.huangbaoche.imageselector.ImgSelConfig;
import com.huangbaoche.imageselector.bean.Image;
import com.hugboga.custom.R;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.bean.AppraisementBean;
import com.hugboga.custom.data.bean.AreaCodeBean;
import com.hugboga.custom.data.bean.EvaluateData;
import com.hugboga.custom.data.bean.EvaluateTagBean;
import com.hugboga.custom.data.bean.OrderBean;
import com.hugboga.custom.data.bean.OrderGuideInfo;
import com.hugboga.custom.data.bean.Photo;
import com.hugboga.custom.data.bean.UserEntity;
import com.hugboga.custom.data.event.EventAction;
import com.hugboga.custom.data.event.EventType;
import com.hugboga.custom.data.request.RequestCollectGuidesId;
import com.hugboga.custom.data.request.RequestEvaluateNew;
import com.hugboga.custom.data.request.RequestEvaluateTag;
import com.hugboga.custom.statistic.MobClickUtils;
import com.hugboga.custom.statistic.StatisticConstant;
import com.hugboga.custom.statistic.click.StatisticClickEvent;
import com.hugboga.custom.statistic.event.EventEvaluate;
import com.hugboga.custom.statistic.event.EventEvaluateShare;
import com.hugboga.custom.statistic.event.EventEvaluateShareBack;
import com.hugboga.custom.statistic.event.EventEvaluateShareFloat;
import com.hugboga.custom.statistic.event.EventEvaluateSubmit;
import com.hugboga.custom.utils.AlbumUploadHelper;
import com.hugboga.custom.utils.CommonUtils;
import com.hugboga.custom.utils.ImageUtils;
import com.hugboga.custom.utils.PermissionRes;
import com.hugboga.custom.utils.Tools;
import com.hugboga.custom.utils.UIUtils;
import com.hugboga.custom.widget.DialogUtil;
import com.hugboga.custom.widget.EvaluateShareView;
import com.hugboga.custom.widget.EvaluateTagGroup;
import com.hugboga.custom.widget.RatingView;
import com.hugboga.custom.widget.ShareDialog;
import com.hugboga.custom.widget.SimpleRatingBar;
import com.hugboga.custom.widget.SpaceItemDecoration;
import com.hugboga.tools.HLog;
import com.yalantis.ucrop.UCrop;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import net.grobas.view.PolygonImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhangqiang on 17/6/19.
 */

public class EvaluateNewActivity extends BaseActivity implements RatingView.OnLevelChangedListener {

    private static final int SELECTIMAGE = 0;
    private static final int CAMERA = 1;
    @Bind(R.id.evaluate_scrollview)
    ScrollView scrollview;
    //@Bind(R.id.evaluate_avatar_iv)
    //PolygonImageView avatarIV;
    //@Bind(R.id.evaluate_name_tv)
    //TextView nameTV;
    //@Bind(R.id.evaluate_score_ratingview)
    //SimpleRatingBar scoreRatingview;
    //@Bind(R.id.evaluate_describe_tv)
    //TextView describeTV;
    //@Bind(R.id.evaluate_plate_number_tv)
    //TextView plateNumberTV;
    @Bind(R.id.evaluate_active_tv)
    TextView activeTV;
    @Bind(R.id.evaluate_score_tv)
    TextView scoreTV;
    @Bind(R.id.evaluate_ratingView)
    RatingView ratingview;
    @Bind(R.id.evaluate_taggroup)
    EvaluateTagGroup tagGroup;
    @Bind(R.id.evaluate_comment_et)
    EditText commentET;
    //@Bind(R.id.evaluate_comment_icon_iv)
    //ImageView commentIconIV;
    //@Bind(R.id.evaluate_submit_tv)
    //TextView submitTV;
    @Bind(R.id.evaluate_pic)
    GridView gridView;
    @Bind(R.id.line_comment)
    TextView lineComment;
    //@Bind(R.id.more_btn_layout)
    //LinearLayout moreBtn;
    //修改为右上角提交
    //@Bind(R.id.evaluate_share_view)
    //EvaluateShareView shareView;
    @Bind(R.id.header_left_btn)
    ImageView fgLeftBtn;
    @Bind(R.id.header_right_btn)
    ImageView fgRightBtn;
    @Bind(R.id.header_right_txt)
    TextView fgRightTV;
    @Bind(R.id.header_title)
    TextView fgTitle;
    @Bind(R.id.id_recyclerview_horizontal)
    RecyclerView mRecyclerView;
    @Bind(R.id.banar_below)
    View banarBelow;
    @Bind(R.id.banar_top)
    View banarTop;
    @Bind(R.id.guide_reply)
    View guideReply;
    private OrderBean orderBean;
    private DialogUtil mDialogUtil;
    private boolean isFirstIn = true;
    PicsAdapter picsdapter;
    GalleryAdapter evluatedPicAdapter;
    Activity activity;
    ArrayList<Photo> localPhotos = new ArrayList<>(1);

    @Override
    public int getContentViewId() {
        return R.layout.fg_evaluate_new;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            orderBean = (OrderBean) savedInstanceState.getSerializable(Constants.PARAMS_DATA);
        } else {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                orderBean = (OrderBean) bundle.getSerializable(Constants.PARAMS_DATA);
            }
        }
        mDialogUtil = DialogUtil.getInstance(this);
        EventBus.getDefault().register(this);
        activity = this;
        initView();

        MobClickUtils.onEvent(new EventEvaluate("" + orderBean.orderType));
    }
    /*@OnClick({R.id.more_btn_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.more_btn_layout:

                break;
            default:
                break;
        }}*/

    @Override
    protected void onStop() {
        super.onStop();
        hideInputMethod(commentET);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (orderBean != null) {
            outState.putSerializable(Constants.PARAMS_DATA, orderBean);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*if(!isEvaluated()){
            picsdapter.abortUpload();
        }*/
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEventMainThread(EventAction action) {
        switch (action.getType()) {
            case WECHAT_SHARE_SUCCEED:
                WXShareUtils wxShareUtils = WXShareUtils.getInstance(this);
                if (getEventSource().equals(wxShareUtils.source)) {//分享成功
                    MobClickUtils.onEvent(new EventEvaluateShareBack("" + orderBean.orderType, getEventSource(), "" + wxShareUtils.type));
                }
                break;
            case EVALUTE_PIC_DELETE:

                ArrayList<String> deletList = (ArrayList<String>) action.getData();
                if (deletList != null) {
                    deleteLocalPhotos(deletList);
                    //setDatas(picSelected,false,false);
                }
                break;
            /*case EVALUTE_PIC_DELETE_ONLYONE:
                //重新初始化图片
                if(localPhotos.size() >0){
                    localPhotos.clear();
                }

                Photo photo = new Photo();
                photo.photoType = Photo.ADD_IMAGE_ICON;
                photo.localFilePath= "add";
                localPhotos.add(photo);

                initPicGridParams(cityGridView,localPhotos,true);
                break;*/
        }
    }

    public void deleteLocalPhotos(List<String> deletePhotos) {
        if (deletePhotos.size() > 0) {
            for (Iterator<Photo> iter = localPhotos.iterator(); iter.hasNext(); ) {
                Photo photo = iter.next();
                for (Iterator<String> iterator = deletePhotos.iterator(); iterator.hasNext(); ) {
                    String path = iterator.next();
                    //String guideRemotePhotoPath = photo.cardPhotoSrc;
                    String guideLocalPhotoPath = photo.localFilePath;
                    /*if (!TextUtils.isEmpty(guideRemotePhotoPath) && guideRemotePhotoPath.equals(path)) {
                        iter.remove();
                        iterator.remove();
                        break;
                    }*/
                    if (!TextUtils.isEmpty(guideLocalPhotoPath) && guideLocalPhotoPath.equals(path)) {
                        iter.remove();
                        iterator.remove();
                        break;
                    }
                }
            }
            //删除完还要判断是否有添加按钮
            boolean hasMore = false;
            for (Photo photo : localPhotos) {
                if (photo.localFilePath.equals("add")) {
                    hasMore = true;
                    break;
                }
            }
            if (!hasMore) {
                Photo photo = new Photo();
                photo.localFilePath = "add";
                photo.photoType = Photo.ADD_IMAGE_ICON;
                localPhotos.add(photo);
            }
            picsdapter.setFirstIn(false);
            picsdapter.notifyDataSetChanged();
            picsdapter.deleteUploadFiles(deletePhotos);
            //saveStatusCheck();
        }
    }

    private ImageLoader loader = new ImageLoader() {
        @Override
        public void displayImage(Context context, String path, ImageView imageView) {
            Glide.with(context).load(path).into(imageView);
        }
    };

    public void initDefalut() {
        Photo photo = new Photo();
        photo.photoType = Photo.ADD_IMAGE_ICON;
        photo.localFilePath = "add";
        localPhotos.add(photo);
    }

    private void initView() {
        //initDefaultTitleBar();
        fgTitle.setText(getString(R.string.evaluate_title));
        if (orderBean == null) {
            return;
        }
        final OrderGuideInfo guideInfo = orderBean.orderGuideInfo;
        if (guideInfo == null) {
            return;
        }



        if (isEvaluated()) {//已评价
            fgLeftBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            RelativeLayout.LayoutParams headerRightImageParams = new RelativeLayout.LayoutParams(UIUtils.dip2px(20), UIUtils.dip2px(20));
            headerRightImageParams.rightMargin = UIUtils.dip2px(18);
            headerRightImageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            headerRightImageParams.addRule(RelativeLayout.CENTER_VERTICAL);
            fgRightBtn.setLayoutParams(headerRightImageParams);
            fgRightBtn.setPadding(0, 0, 0, 0);
            fgRightBtn.setVisibility(View.VISIBLE);
            fgRightBtn.setImageResource(R.mipmap.evaluate_share);
            fgRightBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //分享
                    String source = getEventSource();
                    //MobClickUtils.onEvent(new EventEvaluateShare(orderBean.orderType, source, "" + type));
                    final String shareUrl = CommonUtils.getBaseUrl(orderBean.appraisement.wechatShareUrl) + "orderNo=" + orderBean.orderNo + "&userId=" + UserEntity.getUser().getUserId(EvaluateNewActivity.this);
                    CommonUtils.shareDialog(EvaluateNewActivity.this, orderBean.appraisement.wechatShareHeadSrc,
                            orderBean.appraisement.wechatShareTitle, orderBean.appraisement.content,shareUrl , source,
                            new ShareDialog.OnShareListener() {
                                @Override
                                public void onShare(int type) {
                                    StatisticClickEvent.clickShare(StatisticConstant.SHAREG_TYPE, type == 1 ? "微信好友" : "朋友圈");
                                }
                            });
                }
            });
            ratingview.setLevel(orderBean.appraisement.totalScore);
            if (TextUtils.isEmpty("ASDJAGHAGS;GK;DASGA;GA;GDHA;GAJDSGHASKGHSKHGKASDHSAHGKASGKAHGLKA"/*orderBean.appraisement.content*/)) {
                //commentIconIV.setVisibility(View.GONE);
                commentET.setVisibility(View.GONE);
            } else {
                //commentIconIV.setVisibility(View.VISIBLE);
                commentET.setPadding(0, 0, 0, 0);
                commentET.setVisibility(View.VISIBLE);
                commentET.setText("ADJGHALSHGLADHGALHGADGHALDHGLAHGALGHALHGALHDGKDSADHGJSKAHSKAHGKSALHKGAHKHGKAGH"/*orderBean.appraisement.content*/);
            }
            commentET.setEnabled(false);
            commentET.setBackgroundColor(0x00000000);
            //submitTV.setVisibility(View.GONE);
            scoreTV.setVisibility(View.VISIBLE);
            //判断(没有评价标签和内容),是否显示 "你还没有提交评价内容~"
            if (true/*orderBean.appraisement.totalScore >= 5*/) {
                scoreTV.setText(getString(R.string.evaluate_evaluated_satisfied));
            } else {
                scoreTV.setText(getString(R.string.evaluate_evaluated_ordinary));
            }
            ratingview.setOnLevelChangedListener(null);
            ratingview.setTouchable(false);
            if (orderBean.appraisement.guideLabels == null) {
                tagGroup.setVisibility(View.GONE);
            } else {
                tagGroup.setTagEnabled(false);
                tagGroup.setEvaluatedData(orderBean.appraisement.guideLabels);
            }

            //测试
            ArrayList<String> _itemParams = new ArrayList<String>(2);
            _itemParams.add("http://img3.imgtn.bdimg.com/it/u=129953484,1791172593&fm=11&gp=0.jpg");
            _itemParams.add("http://img3.imgtn.bdimg.com/it/u=129953484,1791172593&fm=11&gp=1.jpg");
            _itemParams.add("http://img3.imgtn.bdimg.com/it/u=129953484,1791172593&fm=11&gp=1.jpg");
            _itemParams.add("http://img3.imgtn.bdimg.com/it/u=129953484,1791172593&fm=11&gp=1.jpg");
            _itemParams.add("http://img3.imgtn.bdimg.com/it/u=129953484,1791172593&fm=11&gp=1.jpg");

            //设置布局管理器
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mRecyclerView.setLayoutManager(linearLayoutManager);
            //设置适配器
            evluatedPicAdapter = new GalleryAdapter(this,_itemParams);
            mRecyclerView.setAdapter(evluatedPicAdapter);
            mRecyclerView.setHorizontalScrollBarEnabled(false);
            SpaceItemDecoration itemDecoration = new SpaceItemDecoration();
            final int paddingLeft = getResources().getDimensionPixelOffset(R.dimen.charter_item_margin_left);
            itemDecoration.setItemOffsets(paddingLeft, 0, 0, 0, LinearLayout.HORIZONTAL);
            mRecyclerView.addItemDecoration(itemDecoration);
            gridView.setVisibility(View.GONE);
            banarBelow.setVisibility(View.GONE);
            banarTop.setVisibility(View.VISIBLE);
            //在接口中刷新司导等信息
            //to do --

        } else {
            //准备评价
            fgLeftBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EvaluateNewActivity.this).setTitle("动动手指，留个评价吧~").setNegativeButton("不要", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).setPositiveButton("好吧", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    final AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            fgRightTV.setVisibility(View.VISIBLE);
            fgRightTV.setText("提交");
            fgRightTV.setTextColor(getResources().getColor(R.color.all_bg_yellow));
            fgRightTV.setTextSize(15);
            fgRightTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //提交评价
                    if (commentET.getText() != null && !TextUtils.isEmpty(commentET.getText().toString())) {
                        String content = commentET.getText().toString().trim();
                        final int size = content.length();
                        for (int i = 0; i < size; i++) {
                            if (!Tools.isEmojiCharacter(content.charAt(i))) {
                                CommonUtils.showToast("评价不能包含表情符号");
                                return;
                            }
                        }
                    }
                    mDialogUtil.showLoadingDialog();
                    RequestEvaluateNew.RequestParams params = new RequestEvaluateNew.RequestParams();
                    params.fromUname = UserEntity.getUser().getNickname(getApplicationContext());
                    params.guideId = orderBean.orderGuideInfo.guideID;
                    params.guideName = orderBean.orderGuideInfo.guideName;
                    params.orderNo = orderBean.orderNo;
                    params.orderType = orderBean.orderType;
                    params.totalScore = Math.round(ratingview.getLevel());
                    params.labels = tagGroup.getRequestTagIds();
                    params.content = TextUtils.isEmpty(commentET.getText()) ? "" : commentET.getText().toString();
                    RequestEvaluateNew request = new RequestEvaluateNew(getApplicationContext(), params);
                    requestData(request);
                }
            });

            initDefalut();
            initPicGridParams(gridView, localPhotos, true);

            commentET.setEnabled(true);
            //commentET.setBackgroundResource(R.drawable.border_evaluate_comment);
            requestData(new RequestEvaluateTag(this, orderBean.orderType));
            //commentIconIV.setVisibility(View.GONE);
            //shareView.setVisibility(View.GONE);
        }

        /*if (isActive()) {//活动
            String activeText = null;
            if (isEvaluated() && orderBean.appraisement.totalScore >= 5) {
                activeText = getString(R.string.evaluate_active_evaluated_get, "" + orderBean.priceCommentReward);
            } else if (isEvaluated()) {
                activeText = getString(R.string.evaluate_active_evaluated);
            } else {//未评价
                activeText = getString(R.string.evaluate_active, "" + orderBean.priceCommentReward);
            }
            activeTV.setText(activeText);
            activeTV.setVisibility(View.VISIBLE);
        } else */
        {
            activeTV.setVisibility(View.GONE);
        }
        scrollview.setFocusable(true);
        scrollview.setFocusableInTouchMode(true);
        scrollview.requestFocus();
    }

    private void initPicGridParams(GridView gridView, List<Photo> pics, boolean isFirstIn) {
        picsdapter = new PicsAdapter(localPhotos, activity, false, isFirstIn);
        int gridWidth = UIUtils.dip2px(74);
        gridView.setColumnWidth(gridWidth);
        gridView.setNumColumns(4);
        gridView.setAdapter(picsdapter);

    }

    /**
     * 是否评价过
     */
    private boolean isEvaluated() {
        return orderBean.userCommentStatus == 1 && orderBean.appraisement != null;
    }

    /**
     * 是否有活动
     */
    private boolean isActive() {
        return orderBean.priceCommentReward != 0;
    }

    @Override
    public void onLevelChanged(RatingView starView, float level) {
        if (isFirstIn) {
            isFirstIn = false;
            //submitTV.setVisibility(View.VISIBLE);
            commentET.setVisibility(View.VISIBLE);
            scoreTV.setVisibility(View.VISIBLE);
        }
        scoreTV.setText(getScoreString(Math.round(level)));
        tagGroup.setLevelChanged((int) level);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) lineComment.getLayoutParams();
        lp.setMargins(0, 0, 0, 0);
        lineComment.setLayoutParams(lp);
        /*if (!isEvaluated() && isActive()) {
            if ((int)level == 5) {
                ratingview.setAllItemBg(R.drawable.selector_evaluate_ratingbar_full);
            } else {
                ratingview.setAllItemBg(R.drawable.selector_evaluate_ratingbar);
            }
        }*/
    }


    @Override
    public void onDataRequestSucceed(BaseRequest _request) {
        super.onDataRequestSucceed(_request);
        if (_request instanceof RequestEvaluateNew) {
            orderBean.userCommentStatus = 1;
            if (orderBean.appraisement == null) {
                orderBean.appraisement = new AppraisementBean();
            }
            orderBean.appraisement.totalScore = Math.round(ratingview.getLevel());
            orderBean.appraisement.content = TextUtils.isEmpty(commentET.getText()) ? "" : commentET.getText().toString();
            initView();
            CommonUtils.showToast(R.string.evaluate_succeed);
            EventBus.getDefault().post(new EventAction(EventType.FGTRAVEL_UPDATE));
            EventBus.getDefault().post(new EventAction(EventType.ORDER_DETAIL_UPDATE_EVALUATION, orderBean.orderNo));
            MobClickUtils.onEvent(new EventEvaluateSubmit(("" + orderBean.orderType), "" + Math.round(ratingview.getLevel()), !TextUtils.isEmpty(commentET.getText()), false));

            RequestEvaluateNew request = (RequestEvaluateNew) _request;
            EvaluateData evaluateData = request.getData();
            if (evaluateData != null && orderBean.orderGuideInfo != null) {
                if (orderBean.appraisement.totalScore > 3 && !orderBean.orderGuideInfo.isCollected()) {
                    requestData(new RequestCollectGuidesId(this, orderBean.orderGuideInfo.guideID));
                }
                MobClickUtils.onEvent(new EventEvaluateShareFloat("" + orderBean.orderType, getEventSource()));
                ShareGuidesActivity.Params params = new ShareGuidesActivity.Params();
                params.evaluateData = evaluateData;
                params.orderNo = orderBean.orderNo;
                params.orderType = orderBean.orderType;
                params.totalScore = (int) orderBean.appraisement.totalScore;
                params.guideAgencyType = orderBean.guideAgencyType;
                Intent intent = new Intent(EvaluateNewActivity.this, ShareGuidesActivity.class);
                intent.putExtra(Constants.PARAMS_DATA, params);
                EvaluateNewActivity.this.startActivity(intent);
                finish();
            }
        } else if (_request instanceof RequestEvaluateTag) {
            RequestEvaluateTag request = (RequestEvaluateTag) _request;
            EvaluateTagBean tagBean = request.getData();
            tagGroup.initTagView(tagBean);
            tagGroup.setLineBelow(lineComment);
            if (isFirstIn) {
                ratingview.setLevel(5);
                scoreTV.setText(getScoreString(5));
                tagGroup.setLevelChanged(5);
                //tagGroup.setEvaluatedData(tagBean.fiveStarTags);
            }
            ratingview.setOnLevelChangedListener(this);
        }
    }

    private String getScoreString(int totalScore) {
        int resultStrId = 0;
        switch (totalScore) {
            case 1:
                resultStrId = R.string.evaluate_star_very_unsatisfactory;
                break;
            case 2:
                resultStrId = R.string.evaluate_star_unsatisfactory;
                break;
            case 3:
                resultStrId = R.string.evaluate_star_ordinary;
                break;
            case 4:
                resultStrId = R.string.evaluate_star_satisfied;
                break;
            case 5:
                resultStrId = R.string.evaluate_star_very_satisfied;
                break;

        }
        return getString(resultStrId);
    }

    @Override
    public String getEventSource() {
        return "评价页";
    }

    class PicsAdapter extends BaseAdapter {

        private List<Photo> picsList;
        boolean isCamera = false;
        boolean isFirstIn = false;
        private Activity mContext;

        public PicsAdapter(List<Photo> picsList, Activity context, boolean isCamera, boolean isFirstIn) {
            this.picsList = picsList;
            this.mContext = context;
            this.isCamera = isCamera;
            this.isFirstIn = isFirstIn;
        }

        public void setFirstIn(boolean isFirstIn) {
            this.isFirstIn = isFirstIn;
        }

        @Override
        public int getCount() {
            return picsList == null ? 0 : picsList.size();
        }

        @Override
        public Object getItem(int position) {
            return picsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            PicsHolder picsHolder;
            if (convertView == null) {
                picsHolder = new PicsHolder();
                convertView = LayoutInflater.from(mContext).
                        inflate(R.layout.evaluatepics_item, null);
                ButterKnife.bind(picsHolder, convertView);
                convertView.setTag(picsHolder);
            } else {
                picsHolder = (PicsHolder) convertView.getTag();
            }
            final Photo photo = picsList.get(position);
            //isFirstIn
            if (isFirstIn) {
                picsHolder.image.setImageResource(R.mipmap.evaluate_add_image);//只显示加号图片
                picsHolder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        grantSdcard();
                    }
                });
                return convertView;
            }

            File dir = new File(photo.localFilePath);
            Uri dirUri = Uri.fromFile(dir);

            if (!photo.localFilePath.equals("add")) {
                Glide.with(getApplicationContext()).load(new File(photo.localFilePath)).into(picsHolder.image);
                if (photo.uploadStatus == AlbumUploadHelper.UPLOAD_FAIL) {
                    picsHolder.failUpload.setVisibility(View.VISIBLE);
                    picsHolder.add.setVisibility(View.GONE);
                    picsHolder.failUpload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //重新上传
                            AlbumUploadHelper.with(getApplicationContext()).reStartUpload();
                        }
                    });
                } else {
                    picsHolder.failUpload.setVisibility(View.GONE);
                    picsHolder.add.setVisibility(View.GONE);
                    //picsHolder.image.setImageURI(dirUri);
                    picsHolder.image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //to do放大图片
                            ArrayList<String> largePic = new ArrayList<String>(1);
                            for (Photo path : picsList) {
                                largePic.add(path.localFilePath);
                            }

                            for (int i = 0; i < largePic.size(); i++) {
                                if (largePic.get(i).equals("add")) {
                                    largePic.remove(i);
                                }
                            }
                            CommonUtils.showLargerLocalImage(activity, largePic, true, position,false);
                        }
                    });
                }

            }

            if (photo.localFilePath.equals("add")) {
                picsHolder.add.setVisibility(View.VISIBLE);
                picsHolder.image.setImageResource(R.mipmap.evaluate_add_image);//最后一个显示加号图片
                picsHolder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        grantSdcard();
                    }
                });

            }


            return convertView;
        }

        public int getUploadFailCount() {
            return AlbumUploadHelper.with(mContext).getUploadQueueSize();
        }

        public void abortUpload() {
            AlbumUploadHelper.with(getApplicationContext()).cancle();
        }

        public void upload(List<Photo> photos) {
            //String guideId = UserSession.getInstances(HBCApplication.GlobalContext).getValue("userid", "");
            if (AlbumUploadHelper.with(getApplicationContext()).isUploading()) {
                //判断上传队列是否有正在上传的数据，如果有，则直接在原来队列添加
                AlbumUploadHelper.with(getApplicationContext()).appendUploadQueue(photos);
                return;
            }
            AlbumUploadHelper.with(getApplicationContext()).setData(photos)
                    .setUploadListener(uploadListener)
                    .startUpload();
        }

        public void deleteUploadFiles(List<String> photo) {
            AlbumUploadHelper.with(getApplicationContext()).deleteUploadFiles(photo);
        }

        AlbumUploadHelper.UploadListener uploadListener = new AlbumUploadHelper.UploadListener() {
            @Override
            public void onPostUploadProgress(int fid, String percent) {
                /*for (int i = 0; i < photoList.size(); i++) {
                    Photo photo = photoList.get(i);
                    if (photo.unquineId == fid && fid != 0) {
                        photo.uploadStatus = AlbumUploadHelper.UPLOAD_PROGRESS;
                        photo.uploadPercent = percent;
                        AlbumBaseAdapter.this.notifyItemChanged(i);
                        break;
                    }
                }*/
            }

            @Override
            public void onPostUploadSuccess(int fid, String uploadFileUrl) {
                for (int i = 0; i < picsList.size(); i++) {
                    Photo photo = picsList.get(i);
                    if (photo.unquineId == fid && fid != 0) {
                        photo.uploadStatus = AlbumUploadHelper.UPLOAD_SUCCESS;
                        photo.cardPhotoSrc = uploadFileUrl;
                        notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onPostUploadFail(int fid, String message) {
                int failIndex = 0;
                for (int i = 0; i < picsList.size(); i++) {
                    Photo photo = picsList.get(i);
                    if (photo.unquineId == fid && fid != 0) {
                        failIndex = i;
                        break;
                    }
                }

                for (int i = failIndex; i < picsList.size(); i++) {
                    Photo photo = picsList.get(i);
                    if (photo.photoType == Photo.ADD_IMAGE_ICON) {
                        continue;
                    }
                    photo.uploadStatus = AlbumUploadHelper.UPLOAD_FAIL;

                }

                notifyDataSetChanged();
                /*if (onChildUploadListener != null) {
                    onChildUploadListener.onChildOneUploadFail();
                }*/
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPostAllUploaded() {
                HLog.i("相册图片上传完成");
                /*if (photoUploadListener != null) {
                    photoUploadListener.onUploadOver();
                }*/

            }

            @Override
            public void onPostUploadCancleAll() {
                Toast.makeText(getApplicationContext(), "上传取消", Toast.LENGTH_SHORT).show();
            }
        };
    }

    static class PicsHolder {
        @Bind(R.id.img_pic)
        ImageView image;
        @Bind(R.id.add)
        TextView add;
        @Bind(R.id.fail_upload)
        TextView failUpload;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECTIMAGE && resultCode == RESULT_OK && data != null) {
            final ArrayList<String> pathList = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);

            handlerPicture(pathList);

        }
        if (requestCode == CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                cropPhoto();//裁剪图片
            }
        }
        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                final Uri resultUri = UCrop.getOutput(data);
                String path = CommonUtils.getRealFilePath(this, resultUri);
                List<String> paths = new ArrayList<>();
                paths.add(path);

                handlerPicture(paths);

            } else if (resultCode == UCrop.RESULT_ERROR) {
                final Throwable cropError = UCrop.getError(data);
                cropError.printStackTrace();
            }
        }

    }

    private void handlerPicture(List<String> pathList) {
        if (pathList == null) {
            return;
        }

        if (localPhotos.get(localPhotos.size() - 1).localFilePath.equals("add")) {
            localPhotos.remove(localPhotos.size() - 1);
        }
        List<Photo> photos = new ArrayList<>();
        for (String path : pathList) {
            if (localPhotos.size() < 9) {
                Photo photo = new Photo();
                photo.localFilePath = path;
                photo.uploadStatus = AlbumUploadHelper.UPLOAD_WAITING;
                photo.unquineId = (System.currentTimeMillis() + path).hashCode();
                localPhotos.add(photo);
                photos.add(photo);
            }
            if (localPhotos.size() == 9) {
                break;//不再添加
            }

        }
        //最后一个item,添加图片
        if (localPhotos.size() < 9) {
            Photo photo = new Photo();
            photo.photoType = Photo.ADD_IMAGE_ICON;
            photo.localFilePath = "add";
            localPhotos.add(photo);
        }

        if (picsdapter != null) {
            if (picsdapter.getUploadFailCount() > 0) {
                for (Photo photo : localPhotos) {
                    if (photo.photoType == Photo.ADD_IMAGE_ICON) {
                        continue;
                    }
                    if (photo.uploadStatus == AlbumUploadHelper.UPLOAD_FAIL) {
                        photo.uploadStatus = AlbumUploadHelper.UPLOAD_WAITING;
                    }
                }
                AlbumUploadHelper.with(getApplicationContext()).reStartUpload();
            }
            picsdapter.setFirstIn(false);
            picsdapter.upload(photos);
            picsdapter.notifyDataSetChanged();
        }
    }


    /**
     * 授权获取照相机权限
     */
    private String cropPic = null;
    private String newPic = null;

    private void grantSdcard() {
        MPermissions.requestPermissions(this, PermissionRes.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @PermissionGrant(PermissionRes.WRITE_EXTERNAL_STORAGE)
    public void requestSdcardSuccess() {
        cropPic = ImageUtils.getPhotoFileName();
        newPic = "new" + cropPic;
        //修改头像
        final CharSequence[] items = getResources().getStringArray(R.array.my_info_phone_type);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this)/*.setTitle("上传头像")*/.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    grantCamera();
                } else if (which == 1) {
                    //确认之前选择的张数
                    ArrayList<String> tmpSelected = new ArrayList<String>(1);
                    for (Photo photo : localPhotos) {
                        tmpSelected.add(photo.localFilePath);
                    }

                    for (int i = 0; i < tmpSelected.size(); i++) {
                        if (tmpSelected.get(i).equals("add")) {
                            tmpSelected.remove(i);
                        }
                    }
                    //添加图片
                    ImgSelConfig config = new ImgSelConfig.Builder(getApplicationContext(), loader)
                            .multiSelect(true)
                            // 是否记住上次选中记录
                            .rememberSelected(false)
                            //动态调整最大张数
                            .maxNum(9 - tmpSelected.size())
                            // 使用沉浸式状态栏
                            .build();

                    ImgSelActivity.startActivity(activity, config, SELECTIMAGE);
                }
            }
        });
        AlertDialog dialog = builder1.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    /**
     * 调用系统剪切图片
     */
    public void cropPhoto() {
        try {
            File oldFile = new File(Constants.IMAGE_DIR, cropPic);
            File newFile = new File(Constants.IMAGE_DIR, newPic);
            UCrop.Options options = new UCrop.Options();
            options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
            UCrop.of(Uri.fromFile(oldFile), Uri.fromFile(newFile)).withAspectRatio(1f, 1f).withOptions(options).withMaxResultSize(200, 200).start(EvaluateNewActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PermissionDenied(PermissionRes.WRITE_EXTERNAL_STORAGE)
    public void requestSdcardFailed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.grant_fail_title);
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            builder.setMessage(R.string.grant_fail_phone1);
        } else {
            builder.setMessage(R.string.grant_fail_sdcard);
            builder.setPositiveButton(R.string.grant_fail_btn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    grantSdcard();
                }
            });
        }
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    /**
     * 授权获取照相机权限
     */
    private void grantCamera() {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.grant_fail_title);
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {
            builder.setMessage(R.string.grant_fail_phone1);
        } else {
            builder.setMessage(R.string.grant_fail_camera);
            builder.setPositiveButton(R.string.grant_fail_btn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    grantCamera();
                }
            });
        }
        builder.show();
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    public static class EvalutedPicAdapter extends PagerAdapter {

        private Context mContext;
        private ViewGroup.LayoutParams itemParams;
        private ArrayList<String> itemList;
        public EvalutedPicAdapter(Context mContext, ArrayList<String> _itemParams) {
            this.mContext = mContext;
            this.itemList = _itemParams;
            itemParams = new ViewGroup.LayoutParams(100, 100);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            if (itemList == null) {
                return super.instantiateItem(container, position);
            }
            final String itemData = itemList.get(position);
            ImageView itemView = new ImageView(mContext);
            itemView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            /*if (itemData != null && !TextUtils.isEmpty(itemData)) {
                Tools.showImage(itemView, itemData, R.mipmap.empty_home_banner);
            } else */{
                itemView.setImageResource(R.mipmap.empty_home_banner);
            }
            //itemView.setLayoutParams(itemParams);
            container.addView(itemView, new ViewGroup.LayoutParams(100, 100));

            if(itemData == null ){
                return itemView;
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonUtils.showLargerLocalImage(mContext, itemList, true, position,true);
                }
            });
            return itemView;
        }

        @Override
        public int getCount() {
            return itemList == null ? 0 : itemList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewGroup) container.getParent()).removeView((View)object);
        }
    }


    public class GalleryAdapter extends
            RecyclerView.Adapter<GalleryAdapter.ViewHolder>
    {
        private LayoutInflater mInflater;
        private ArrayList<String> mDatas;
        private Context mDontext;
        public GalleryAdapter(Context context, ArrayList<String> datats)
        {
            mInflater = LayoutInflater.from(context);
            mDatas = datats;
            mDontext = context;
        }
        public class ViewHolder extends RecyclerView.ViewHolder
        {
            public ViewHolder(View arg0)
            {
                super(arg0);
            }
            ImageView mImg;
        }
        @Override
        public int getItemCount()
        {
            return mDatas.size();
        }
        /**
         * 创建ViewHolder
         */
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
        {
            View view = mInflater.inflate(R.layout.evluatedpic_item,
                    viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.mImg = (ImageView) view
                    .findViewById(R.id.id_index_gallery_item_image);
            return viewHolder;
        }
        /**
         * 设置值
         */
        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int i)
        {
            //viewHolder.mImg.setImageResource(R.mipmap.empty_home_banner);
            //viewHolder.mImg.setImageResource(mDatas.get(i));
            Tools.showImage(viewHolder.mImg, mDatas.get(i), R.mipmap.empty_home_banner);
            viewHolder.mImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommonUtils.showLargerLocalImage(mDontext, mDatas, true, i,true);
                }
            });

        }
    }
}
