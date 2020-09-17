package com.mijia.app.bean;

import com.handong.framework.base.BaseBean;

public class AdvertisementBean extends BaseBean {


    /**
     * bannerImg : bannerimg
     * bannerUrl : bannerurl  271383586
     * images : images
     * indexImg : indeximg
     */

    private String bannerImg;
    private String bannerUrl;
    private String images;
    private String indexImg;
    /**
     * androidIsChecking : 1
     * androidVersion : 1.0.0
     * isChecking : 1
     * version : 1.0.0
     */

    private String androidIsChecking;
    private String androidVersion;
    private String isChecking;
    private String version;


    public String getBannerImg() {
        return bannerImg;
    }

    public void setBannerImg(String bannerImg) {
        this.bannerImg = bannerImg;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getIndexImg() {
        return indexImg;
    }

    public void setIndexImg(String indexImg) {
        this.indexImg = indexImg;
    }

    public String getAndroidIsChecking() {
        return androidIsChecking;
    }

    public void setAndroidIsChecking(String androidIsChecking) {
        this.androidIsChecking = androidIsChecking;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }

    public String getIsChecking() {
        return isChecking;
    }

    public void setIsChecking(String isChecking) {
        this.isChecking = isChecking;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
