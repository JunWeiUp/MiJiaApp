package com.mijia.app.bean;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;


@Entity
public class FileBean {

    public FileBean(){

    }

    public FileBean(String diskId, String gsId, String size, String name, String type, long time) {
        this.diskId = diskId;
        GsId = gsId;
        this.size = size;
        this.name = name;
        this.type = type;
        this.time = time;
    }

    /**
     * size : 15
     * name : /test.txt
     * type : file
     */




    @Id
    public Long fileId;


    public String diskId;

    public String GsId;

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getDiskId() {
        return diskId;
    }

    public void setDiskId(String diskId) {
        this.diskId = diskId;
    }

    public String getGsId() {
        return GsId;
    }

    public void setGsId(String gsId) {
        GsId = gsId;
    }

    private String size;
    private String name;
    private String type;
    private boolean isSelected = false;
    private long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
