package com.mijia.app.bean;

public class UploadRequestBean {
    /**
     * userName : test_user
     * userId : aaaaaaaaaa
     * gsName : test_gs
     * gsId : bbbbbbbbbbbbbb
     * diskName : test_disk
     * diskId : cccccccccccc
     * fullpath : /1.txt
     * file : ......
     */

    private String userName;
    private String userId;
    private String gsName;
    private String gsId;
    private String diskName;
    private String diskId;
    private String fullpath;
    private String data;
    private String all;
    private String index;
    private String datalen;


    public String getAll() {
        return all == null ? "" : all;
    }

    public void setAll(String all) {
        this.all = all;
    }

    public String getIndex() {
        return index == null ? "" : index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getDatalen() {
        return datalen == null ? "" : datalen;
    }

    public void setDatalen(String datalen) {
        this.datalen = datalen;
    }

    public String getUserName() {
        return userName == null ? "" : userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId == null ? "" : userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGsName() {
        return gsName == null ? "" : gsName;
    }

    public void setGsName(String gsName) {
        this.gsName = gsName;
    }

    public String getGsId() {
        return gsId == null ? "" : gsId;
    }

    public void setGsId(String gsId) {
        this.gsId = gsId;
    }

    public String getDiskName() {
        return diskName == null ? "" : diskName;
    }

    public void setDiskName(String diskName) {
        this.diskName = diskName;
    }

    public String getDiskId() {
        return diskId == null ? "" : diskId;
    }

    public void setDiskId(String diskId) {
        this.diskId = diskId;
    }

    public String getFullpath() {
        return fullpath == null ? "" : fullpath;
    }

    public void setFullpath(String fullpath) {
        this.fullpath = fullpath;
    }

    public String getData() {
        return data == null ? "" : data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
