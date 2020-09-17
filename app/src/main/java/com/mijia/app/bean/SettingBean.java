package com.mijia.app.bean;

import com.handong.framework.base.BaseBean;

public class SettingBean extends BaseBean {


    /**
     * aboutUs : aboutus
     * logoImg : logoimg
     * phone : phone
     * store : store
     */

    private String aboutUs;
    private String logoImg;
    private String phone;
    private String store;

    public String getAboutUs() {
        return aboutUs;
    }

    public void setAboutUs(String aboutUs) {
        this.aboutUs = aboutUs;
    }

    public String getLogoImg() {
        return logoImg;
    }

    public void setLogoImg(String logoImg) {
        this.logoImg = logoImg;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }
}
