package com.mijia.app.bean;

import java.util.List;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public  class DiskInfoBean {



    public DiskInfoBean() {
    }

    public DiskInfoBean(String diskName) {
        this.diskName = diskName;
    }
    @Id
    public long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    /**
     * diskName : test_disk
     * diskSize : 100
     * diskStatu : open
     * diskInuse : 50
     * fileInfo : [{"size":"15","name":"/test.txt","type":"file"},{"size":"0","name":"/","type":"folder"}]
     * diskId : cccccccccccc
     */

    private String diskName;
    private String diskSize;
    private String diskStatu;
    private String diskInuse;
    private String diskId;
    private String diskType;
    private List<FileBean> fileInfo;

    public String getDiskType() {
        return diskType;
    }

    public void setDiskType(String diskType) {
        this.diskType = diskType;
    }

    public String getDiskName() {
        return diskName;
    }

    public void setDiskName(String diskName) {
        this.diskName = diskName;
    }

    public String getDiskSize() {
        return diskSize;
    }

    public void setDiskSize(String diskSize) {
        this.diskSize = diskSize;
    }

    public String getDiskStatu() {
        return diskStatu;
    }

    public void setDiskStatu(String diskStatu) {
        this.diskStatu = diskStatu;
    }

    public String getDiskInuse() {
        return diskInuse;
    }

    public void setDiskInuse(String diskInuse) {
        this.diskInuse = diskInuse;
    }

    public String getDiskId() {
        return diskId;
    }

    public void setDiskId(String diskId) {
        this.diskId = diskId;
    }

    public List<FileBean> getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(List<FileBean> fileInfo) {
        this.fileInfo = fileInfo;
    }

}