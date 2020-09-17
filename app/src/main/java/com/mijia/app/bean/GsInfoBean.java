package com.mijia.app.bean;

import java.util.List;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

@Entity
public class GsInfoBean {

    public GsInfoBean() {
    }

    public GsInfoBean(String gsName) {
        this.gsName = gsName;
    }

    /**
     * gsStatu : online
     * gsId : bbbbbbbbbbbbbb
     * gsName : test_gs
     * diskInfo : [{"diskName":"test_disk","diskSize":"100","diskStatu":"open","diskInuse":"50","fileInfo":[{"size":"15","name":"/test.txt","type":"file"},{"size":"0","name":"/","type":"folder"}],"diskId":"cccccccccccc"}]
     */

    @Id
    public long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    private String gsStatu;
    private String gsId;
    private String gsName;

    public ToMany<DiskInfoBean> diskList;

    private List<DiskInfoBean> diskInfo;

    public String getGsStatu() {
        return gsStatu;
    }

    public void setGsStatu(String gsStatu) {
        this.gsStatu = gsStatu;
    }

    public String getGsId() {
        return gsId;
    }

    public void setGsId(String gsId) {
        this.gsId = gsId;
    }

    public String getGsName() {
        return gsName;
    }

    public void setGsName(String gsName) {
        this.gsName = gsName;
    }

    public List<DiskInfoBean> getDiskInfo() {
        return diskInfo;
    }

    public void setDiskInfo(List<DiskInfoBean> diskInfo) {
        this.diskInfo = diskInfo;
    }


}
