package com.mijia.app.bean;

import android.text.TextUtils;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class DownRowTaskBean {
    @Id
    private long id;
    private String fullpath = "";
    private String locationPath = "";
    private long fileSize;


    /**
     * 0:未发送请求
     * 1：接收到响应 （传输中）
     * 2：传输完成
     * 3：长时间（5 分钟）未接收到响应（下载失败）
     */
    private int currTaskStatus = 0;


    /**
     * 下载的位置
     */
    private long filePointer;

    // 当前文件的总包数
    private String fileAllNumber = "";
    // 当前 写入的 文件的 第几个包
    private String filePositionNumber = "0";


    private String userName;
    private String userId;
    private String gsName;
    private String gsId;
    private String diskName;
    private String diskId;

    private long finishTime = 0;
    int type = 0;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }

    public String getLocationPath() {
        return locationPath == null ? "" : locationPath;
    }

    public void setLocationPath(String locationPath) {
        this.locationPath = locationPath;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getCurrTaskStatus() {
        return currTaskStatus;
    }

    public void setCurrTaskStatus(int currTaskStatus) {
        this.currTaskStatus = currTaskStatus;
    }

    public long getFilePointer() {
        return filePointer;
    }

    public void setFilePointer(long filePointer) {
        this.filePointer = filePointer;
    }

    public String getFileAllNumber() {
        return fileAllNumber == null ? "" : fileAllNumber;
    }

    public void setFileAllNumber(String fileAllNumber) {
        this.fileAllNumber = fileAllNumber;
    }

    public String getFilePositionNumber() {
        return filePositionNumber == null ? "" : filePositionNumber;
    }

    public int getFilePositionIntNumber() {
        if (TextUtils.isEmpty(filePositionNumber)) {
            this.filePositionNumber = "0";
        }
        return Integer.valueOf(filePositionNumber);
    }


    public void setFilePositionNumber(String filePositionNumber) {
        this.filePositionNumber = filePositionNumber;

        if (TextUtils.isEmpty(filePositionNumber)) {
            this.filePositionNumber = "0";
        }

    }
}
