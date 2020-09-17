package com.mijia.app.utils.file;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by Administrator on 2019/6/12.
 */
@Entity
public class MediaBean {

    public MediaBean(long mediaId, long cusorId, long date, int width, int height, String path, int size, String displayName, boolean isSelect) {
        this.mediaId = mediaId;
        this.cusorId = cusorId;
        this.date = date;
        this.width = width;
        this.height = height;
        this.path = path;
        this.size = size;
        this.displayName = displayName;
        this.isSelect = isSelect;
    }

    public MediaBean() {
    }

    public long getMediaId() {
        return mediaId;
    }

    public void setMediaId(long mediaId) {
        this.mediaId = mediaId;
    }

    @Id
    private long mediaId;

    public MediaBean(String path, int size, String displayName, long cusorId, int height, int width, long date) {
        this.path = path;
        this.size = size;
        this.displayName = displayName;
        this.width = width;
        this.height = height;
        this.date = date;
        this.cusorId = cusorId;
//       this.upLoadingStatus  ;
    }

    private long cusorId;

    public long getCusorId() {
        return cusorId;
    }

    public void setCusorId(long cusorId) {
        this.cusorId = cusorId;
    }

    private long date;

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    private int width;

    private int height;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    private String path;

    private int size;

    private String displayName;

    private boolean isSelect;

    private int upLoadingStatus = -1;


    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getUpLoadingStatus() {
        return upLoadingStatus;
    }

    public void setUpLoadingStatus(int upLoadingStatus) {
        this.upLoadingStatus = upLoadingStatus;
    }
}
