package com.hugboga.custom.data.bean;

import com.huangbaoche.hbcframe.data.bean.IBaseBean;

import java.util.List;

/**
 * Created on 16/9/9.
 */

public class CanServiceGuideBean implements IBaseBean {

    /**
     * totalSize : 7
     * guides : [{"integralFrozen":0,"carBrandName":"","cityId":217,"guideId":"200100000140","guideName":"宋博","cityName":"东京","carName":"Alphard （埃尔法）","gender":1,"genderName":"男","avatarS":"http://fr.dev.hbc.tech/guide/s_20151117164111.jpg","orderCounts":0,"serviceStar":4.5},{"integralFrozen":0,"carBrandName":"","cityId":217,"guideId":"200100013548","guideName":"于龙","cityName":"东京","carName":"Alphard （埃尔法）","gender":1,"genderName":"男","avatarS":"http://fr.dev.hbc.tech/guide/guide.png","orderCounts":0,"serviceStar":4.5},{"integralFrozen":0,"carBrandName":"","cityId":217,"guideId":"200100011322","guideName":"徐国庆","cityName":"东京","carName":"奥德赛","gender":1,"genderName":"男","avatarS":"http://fr.dev.hbc.tech/guide/s_20151026094230.jpg","orderCounts":0,"serviceStar":4.5},{"integralFrozen":0,"carBrandName":"奔驰","cityId":235,"guideId":"291448316911","guideName":"测试","cityName":"普吉岛","carName":"vito","gender":1,"genderName":"男","avatarS":"http://fr.dev.hbc.tech/guide/guide.png","orderCounts":2,"serviceStar":4.5},{"integralFrozen":0,"carBrandName":"","cityId":217,"guideId":"200100004126","guideName":"杨丽丽","cityName":"东京","carName":"嘉华","gender":2,"genderName":"女","avatarS":"http://fr.dev.hbc.tech/guide/guide.png","orderCounts":0,"serviceStar":4.5},{"integralFrozen":0,"carBrandName":"","cityId":217,"guideId":"200100013546","guideName":"郭嘉興","cityName":"东京","carName":"嘉华","gender":1,"genderName":"男","avatarS":"http://fr.dev.hbc.tech/guide/guide.png","orderCounts":0,"serviceStar":4.5},{"integralFrozen":0,"carBrandName":"","cityId":217,"guideId":"200100009100","guideName":"王子豪","cityName":"东京","carName":"嘉华","gender":1,"genderName":"男","avatarS":"http://fr.dev.hbc.tech/guide/s_20150930133133.jpg","orderCounts":0,"serviceStar":4.5}]
     */

    private int totalSize;
    /**
     * integralFrozen : 0
     * carBrandName :
     * cityId : 217
     * guideId : 200100000140
     * guideName : 宋博
     * cityName : 东京
     * carName : Alphard （埃尔法）
     * gender : 1
     * genderName : 男
     * avatarS : http://fr.dev.hbc.tech/guide/s_20151117164111.jpg
     * orderCounts : 0
     * serviceStar : 4.5
     */

    private List<GuidesBean> guides;

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public List<GuidesBean> getGuides() {
        return guides;
    }

    public void setGuides(List<GuidesBean> guides) {
        this.guides = guides;
    }

    public static class GuidesBean {
        private int integralFrozen;
        private String carBrandName;
        private int cityId;
        private String guideId;
        private String guideName;
        private String cityName;
        private String carName;
        private int gender;
        private String genderName;
        private String avatarS;
        private int orderCounts;
        private double serviceStar;

        public int getIntegralFrozen() {
            return integralFrozen;
        }

        public void setIntegralFrozen(int integralFrozen) {
            this.integralFrozen = integralFrozen;
        }

        public String getCarBrandName() {
            return carBrandName;
        }

        public void setCarBrandName(String carBrandName) {
            this.carBrandName = carBrandName;
        }

        public int getCityId() {
            return cityId;
        }

        public void setCityId(int cityId) {
            this.cityId = cityId;
        }

        public String getGuideId() {
            return guideId;
        }

        public void setGuideId(String guideId) {
            this.guideId = guideId;
        }

        public String getGuideName() {
            return guideName;
        }

        public void setGuideName(String guideName) {
            this.guideName = guideName;
        }

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public String getCarName() {
            return carName;
        }

        public void setCarName(String carName) {
            this.carName = carName;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public String getGenderName() {
            return genderName;
        }

        public void setGenderName(String genderName) {
            this.genderName = genderName;
        }

        public String getAvatarS() {
            return avatarS;
        }

        public void setAvatarS(String avatarS) {
            this.avatarS = avatarS;
        }

        public int getOrderCounts() {
            return orderCounts;
        }

        public void setOrderCounts(int orderCounts) {
            this.orderCounts = orderCounts;
        }

        public double getServiceStar() {
            return serviceStar;
        }

        public void setServiceStar(double serviceStar) {
            this.serviceStar = serviceStar;
        }
    }
}