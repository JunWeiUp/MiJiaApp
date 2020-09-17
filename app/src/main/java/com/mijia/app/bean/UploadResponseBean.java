package com.mijia.app.bean;

public class UploadResponseBean {
    /**
     * userName : test_user
     * userId : aaaaaaaaaa
     * gsName : test_gs
     * gsId : bbbbbbbbbbbbbb
     * diskName : test_disk
     * diskId : cccccccccccc
     * fullpath : /1.txt
     * index : 3
     */

    private String userName;
    private String userId;
    private String gsName;
    private String gsId;
    private String diskName;
    private String diskId;
    private String fullpath;
    private String index;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGsName() {
        return gsName;
    }

    public void setGsName(String gsName) {
        this.gsName = gsName;
    }

    public String getGsId() {
        return gsId;
    }

    public void setGsId(String gsId) {
        this.gsId = gsId;
    }

    public String getDiskName() {
        return diskName;
    }

    public void setDiskName(String diskName) {
        this.diskName = diskName;
    }

    public String getDiskId() {
        return diskId;
    }

    public void setDiskId(String diskId) {
        this.diskId = diskId;
    }

    public String getFullpath() {
        return fullpath;
    }

    public void setFullpath(String fullpath) {
        this.fullpath = fullpath;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }


    @Override
    public String toString() {
        return "UploadResponseBean{" +
                "userName='" + userName + '\'' +
                ", userId='" + userId + '\'' +
                ", gsName='" + gsName + '\'' +
                ", gsId='" + gsId + '\'' +
                ", diskName='" + diskName + '\'' +
                ", diskId='" + diskId + '\'' +
                ", fullpath='" + fullpath + '\'' +
                ", index='" + index + '\'' +
                '}';
    }
}
