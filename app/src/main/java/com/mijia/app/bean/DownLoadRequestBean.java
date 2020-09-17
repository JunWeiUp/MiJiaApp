package com.mijia.app.bean;

public class DownLoadRequestBean {
    /**
     * userName : test_user
     * userId : aaaaaaaaaa
     * gsName : test_gs
     * gsId : bbbbbbbbbbbbbb
     * diskName : test_disk
     * diskId : cccccccccccc
     * fullpath : /1.txt
     * index : 3
     * "type":"0",				//0明文   1密文    2时间瓶
     * "time":"7888888",
     */

    private String userName;
    private String userId;
    private String gsName;
    private String gsId;
    private String diskName;
    private String diskId;
    private String fullpath;
    private String index = "1";
    private int type = 0;
    private long time = 0;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

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

    public String getNextIndex() {
        return (Integer.valueOf(this.index) + 1) + "";
    }

    public int getNextIntIndex() {
        return Integer.valueOf(this.index) + 1;
    }


    public String key() {
        return diskId + diskName + fullpath;
    }


}
