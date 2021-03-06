package com.hugboga.custom.data.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ZHZEPHI on 2015/7/20.
 */
public class OrderGuideInfo implements IBaseBean ,Parcelable {

    public String guideAvatar;//头像
    public String guideName;//车导名
    public String guideID;//车导ID
    public String guideTel;//车导电话
    public double guideStarLevel;//车导级别
    public String guideCar;//车描述
    public String carNumber;//车牌
    public int storeStatus;//导游是否被收藏 0没有 1已收藏
    public String guideImId; //导游的im 账号
    public int inBlack; //是否拉黑
    public String guideCarId;
    public String flag;//司导所在国家的国旗
    public int timediff;//时差
    public int timezone;
    public String cityName;
    public int cityId;
    public String countryName;
    public int countryId;
    public String contact;//地接社队长名称（名称-队长）
    public int orderCount;//司导服务单数

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.guideAvatar);
        dest.writeString(this.guideName);
        dest.writeString(this.guideID);
        dest.writeString(this.guideTel);
        dest.writeDouble(this.guideStarLevel);
        dest.writeString(this.guideCar);
        dest.writeString(this.carNumber);
        dest.writeInt(this.storeStatus);
        dest.writeString(this.guideImId);
        dest.writeString(this.guideCarId);
        dest.writeString(this.flag);
        dest.writeInt(this.timediff);
        dest.writeInt(this.timezone);
        dest.writeString(this.cityName);
        dest.writeInt(this.cityId);
        dest.writeString(this.countryName);
        dest.writeInt(this.countryId);
        dest.writeString(this.contact);
    }

    public OrderGuideInfo() {
    }

    protected OrderGuideInfo(Parcel in) {
        this.guideAvatar = in.readString();
        this.guideName = in.readString();
        this.guideID = in.readString();
        this.guideTel = in.readString();
        this.guideStarLevel = in.readDouble();
        this.guideCar = in.readString();
        this.carNumber = in.readString();
        this.storeStatus = in.readInt();
        this.guideImId = in.readString();
        this.guideCarId = in.readString();
        this.flag = in.readString();
        this.timediff = in.readInt();
        this.timezone = in.readInt();
        this.cityName = in.readString();
        this.cityId = in.readInt();
        this.countryName = in.readString();
        this.countryId = in.readInt();
        this.contact = in.readString();
    }

    public boolean isCollected() {
        return storeStatus == 1;
    }

    public static final Creator<OrderGuideInfo> CREATOR = new Creator<OrderGuideInfo>() {
        @Override
        public OrderGuideInfo createFromParcel(Parcel source) {
            return new OrderGuideInfo(source);
        }

        @Override
        public OrderGuideInfo[] newArray(int size) {
            return new OrderGuideInfo[size];
        }
    };
}
