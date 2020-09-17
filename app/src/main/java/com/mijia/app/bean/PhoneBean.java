package com.mijia.app.bean;

public class PhoneBean {


    /**
     * phoneNumber : 18310230853
     * personName : 王鲸鱼
     */

    private String phoneNumber;
    private String personName;

    public PhoneBean(String string, String string1) {
        this.phoneNumber = string1;
        this.personName = string;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    @Override
    public String toString() {
        return "PhoneBean{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", personName='" + personName + '\'' +
                '}';
    }
}
