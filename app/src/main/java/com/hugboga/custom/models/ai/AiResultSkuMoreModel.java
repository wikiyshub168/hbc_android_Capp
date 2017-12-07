package com.hugboga.custom.models.ai;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.hugboga.custom.R;
import com.hugboga.custom.activity.CityActivity;
import com.hugboga.custom.activity.FilterSkuListActivity;
import com.hugboga.custom.constants.Constants;
import com.hugboga.custom.data.bean.city.DestinationHomeVo;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by HONGBO on 2017/12/7 12:45.
 */

public class AiResultSkuMoreModel extends EpoxyModelWithHolder<AiResultSkuMoreModel.AiResultSkuMoreHolder> {

    Context mContext;
    DestinationHomeVo vo;

    public AiResultSkuMoreModel(Context mContext, DestinationHomeVo vo) {
        this.mContext = mContext;
        this.vo = vo;
    }

    @Override
    protected AiResultSkuMoreHolder createNewHolder() {
        return new AiResultSkuMoreHolder();
    }

    @Override
    protected int getDefaultLayout() {
        return R.layout.ai_result_sku_more_model_layout;
    }

    class AiResultSkuMoreHolder extends EpoxyHolder {
        @Override
        protected void bindView(View itemView) {
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.ai_result_more_root_layout)
        public void onClick(View view) {
            // 打开全部玩法
            clickMoreSku();
        }
    }

    /**
     * 查看某区域更多玩法
     */
    public void clickMoreSku() {
        FilterSkuListActivity.Params params = new FilterSkuListActivity.Params();
        if (vo != null) {
            params.id = vo.destinationId;
            params.cityHomeType = CityActivity.CityHomeType.getNew(vo.destinationType);
//            params.titleName = paramsData.titleName;
//            params.days = "1,2"; // 游玩天数需要动态获取
        }
        Intent intent = new Intent(mContext, FilterSkuListActivity.class);
        intent.putExtra(Constants.PARAMS_SOURCE, getEventSource());
        intent.putExtra(Constants.PARAMS_DATA, params);
        mContext.startActivity(intent);
    }

    private String getEventSource() {
        return "AI结果页";
    }
}
