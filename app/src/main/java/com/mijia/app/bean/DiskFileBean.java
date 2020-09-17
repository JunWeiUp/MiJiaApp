package com.mijia.app.bean;

import java.util.List;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class DiskFileBean {


    @Id
    public Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String diskId;

    public String GsId;

    public String getGsId() {
        return GsId;
    }

    public void setGsId(String gsId) {
        GsId = gsId;
    }

    public List<FileBean> mFileBeans;

    public String getDiskId() {
        return diskId;
    }

    public void setDiskId(String diskId) {
        this.diskId = diskId;
    }

    public List<FileBean> getFileBeans() {
        return mFileBeans;
    }

    public void setFileBeans(List<FileBean> fileBeans) {
        mFileBeans = fileBeans;
    }
}
