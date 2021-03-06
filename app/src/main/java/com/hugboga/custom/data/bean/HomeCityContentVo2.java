package com.hugboga.custom.data.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by zhangqiang on 17/8/3.
 */

public class HomeCityContentVo2 implements Serializable {
    public int cityId	= 0;//	城市ID
    public String cityName = "";//城市名称
    public int contentType;//1, 推荐司导；2, 推荐线路
    // 城市或者国家（1，国家；2，城市）
    public int placeType;
    // 国家ID
    public int countryId;
    // 国家名称
    public String countryName;
    public ArrayList<HomeCityItemVo> cityItemList = null;
}
