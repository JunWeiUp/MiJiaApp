package com.mijia.app.bean;

import com.handong.framework.base.BaseBean;

public class LoginBean extends BaseBean {


    /**
     * headImg : http://thirdwx.qlogo.cn/mmopen/vi_32/8PNtDIIEu5djykN4eUt2NUznRVeGJOAItWBgsw1C34pCw0ElIfk9pZQY5skC3YWJib6tuOTSlibSAO2kuInrGjaw/132
     * nickName : 武克
     * sign : 国内领先的跨终端信息加密工具
     * userId : a620ea2c0d010e43987fb0dd59a0832a
     */

    private String headImg;
    private String nickName;
    private String sign;
    private String userId;

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
