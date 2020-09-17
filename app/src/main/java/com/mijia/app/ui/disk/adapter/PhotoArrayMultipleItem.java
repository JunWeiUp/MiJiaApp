package com.mijia.app.ui.disk.adapter;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.mijia.app.bean.FileBean;

import java.util.List;

public class PhotoArrayMultipleItem implements MultiItemEntity {


    public static final int TITLE = 0;
    public static final int PHOTO = 1;


    private int itemType;

    private List<FileBean> mFileBean;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PhotoArrayMultipleItem(int itemType, List<FileBean> fileBean) {
        this.itemType = itemType;
        mFileBean = fileBean;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public List<FileBean> getFileBean() {
        return mFileBean;
    }

    public void setFileBean(List<FileBean> fileBean) {
        mFileBean = fileBean;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
