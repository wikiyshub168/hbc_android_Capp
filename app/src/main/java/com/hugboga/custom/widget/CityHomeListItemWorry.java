package com.hugboga.custom.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hugboga.custom.R;
import com.hugboga.custom.data.bean.GoodsSec;
import com.hugboga.custom.utils.Tools;
import com.hugboga.custom.utils.UIUtils;

import java.util.ArrayList;

/**
 * Created by wcj on 2016/10/18.
 */

public class CityHomeListItemWorry extends RelativeLayout implements HbcViewBehavior{

    private TextView headlable,guideAmount,places,goodsPrice,priceHint;
    private ImageView goodImagefirst;
    private RelativeLayout cityHomeLay;


    public CityHomeListItemWorry(Context context) {
        this(context,null);
    }

    public CityHomeListItemWorry(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflate(context, R.layout.city_home_list_item_save_worry,this);
        headlable=(TextView)findViewById(R.id.city_home_item_head_lable);
        guideAmount=(TextView)findViewById(R.id.city_home_item_guides_count);
        places=(TextView)findViewById(R.id.city_home_list_item_route_name_TV);
        goodsPrice=(TextView)findViewById(R.id.city_home_list_item_price);
        priceHint=(TextView)findViewById(R.id.city_home_list_item_unit);
        goodImagefirst=(ImageView)findViewById(R.id.city_home_list_item_image_first);
        cityHomeLay=(RelativeLayout)findViewById(R.id.city_home_list_item_worry_lay);

    }


    @Override
    public void update(Object _data) {
        if (_data == null || !(_data instanceof GoodsSec)) {
            return;
        }
        GoodsSec goodsSec = (GoodsSec)_data;

        if (!TextUtils.isEmpty(goodsSec.goodsPicture)) {
            Tools.showImage(goodImagefirst,goodsSec.goodsPicture);
        }

        guideAmount.setText("" + goodsSec.guideAmount+"位当地中文司导");
        guideAmount.setVisibility(View.VISIBLE);


        goodsPrice.setText(goodsSec.goodsLable);

        priceHint.setVisibility(View.VISIBLE);
        headlable.setText(goodsSec.headLable);

        if (!TextUtils.isEmpty(goodsSec.goodsName)){
            places.setText(goodsSec.places);
        }

    }
}