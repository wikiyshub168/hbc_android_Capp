package com.hugboga.custom.data.bean.combination;

import com.hugboga.custom.data.bean.ContactUserBean;
import com.hugboga.custom.data.bean.OrderBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by qingcha on 17/3/6.
 */
public class GroupParentParam implements Serializable {
    public String carId;                        // 车ID 指定司导下单必填
    public Integer carTypeId;                   // 车型 如果不是特殊车型必填
    public String carName;                      // 车型名称
    public Integer carSeatNum;                  // 车座数 不是特殊车型必填
    public String carDesc;                      // 车型描述
    public String carModelId;                   // 车型idv2 必填
    public Integer isSpecialCar;                // 必填 是否特殊车型 0 不是 1 是
    public Integer capOfLuggage;                // 行李数最大限制
    public String orderChannel;                 // 订单渠道  必填
    public String orderChannelName;             // 渠道名称 必填
    public Integer urgentFlag;                  // 是否急单 必填
    public String guideCollectId;               // 客人收藏司导ID 指定司导下单时 必填
    public String guideLabel;                   // 司导标签
    public int adultNum;                        // 成人数 必填 大于0
    public int childNum;                        // 儿童数 大于0
    public int capOfPerson;                     // 可乘坐人数 必填 大于0
    public int luggageNumber;                   // 行李数
    public OrderBean.ChildSeats childSeatInfo;  // 儿童座椅
    public String userId;                       // 客户ID; 必填
    public Integer realSendSms;                 // 是否给乘车人发送短信 1 发送
    public Integer isRealUser;                  // 1:自己下单 2:为他人下单 为其他人下单必填
    public ArrayList<ContactUserBean> userExInfo;       // 下单人信息 必填
    public ArrayList<ContactUserBean> realUserExInfo;   // 乘车人信息 为他人下单必填
    public String userRemark;                   // 用户备注
    public String userEmail;                    // 下单人email
    public Double priceChannel;                 // 订单价格
    public Double travelFund;                   // 旅游基金
    public String coupId;                       // 券ID
    public String coupPriceInfo;                // 券价格
    public Double priceActual;                  // 暂时存券支付的时候的价格 使用券 必填
    public Integer totalDays;                   // 订单天数 必填
    public String userWechat;                   // 用户微信号

    public Integer serviceCityId;               // 服务城市Id 必填
    public String serviceCityName;              // 服务城市名 必填
    public String serviceTime;                  // 服务开始时间 yyyy-MM-dd HH:mm:ss 必填  如果第一个订单包含接机 填写接机时间
    public String serviceEndTime;               // 服务结束时间 yyyy-MM-dd HH:mm:ss 必填
    public String priceMark;                    // 报价的batchNo
    public String serviceAddressTel;            // 目的地酒店或者区域电话号码
    public String serviceAreaCode;              // 目的地区号
    public int commission;
    public Integer serviceEndCityid;            // 结束城市Id 必填
    public String serviceEndCityname;           // 结束城市
    public String startAddress;                 // 出发地地址 //GDS-M C-APP-O
    public String startAddressPoi;              // 出发地点POI //M
    public String startAddressDetail;           // 出发地详情(上车地点) //O-for-CAPP | M-for-GDS

    public Double priceTicket;
    public String limitedSaleNo;                // 秒杀活动编号
    public String limitedSaleScheduleNo;        // 秒杀活动场次编号

    public Integer isFlightSign;                // 是否举牌接机
    public String flightBrandSign;              // 举牌接机姓名
    public Double priceFlightBrandSign;         // 举牌接机费用
    public Double checkInPrice;                 // checkin 费用
    public Integer isCheckin;                   // 是否checkin 服务

}
