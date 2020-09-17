package com.mijia.app.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoVoiceFileBean implements Parcelable {

    public VideoVoiceFileBean() {
    }

    private boolean isSelect;


    protected VideoVoiceFileBean(Parcel in) {
        isSelect = in.readByte() != 0;
        displayName = in.readString();
        path = in.readString();
        size = in.readLong();
        duration = in.readLong();
        courseId = in.readLong();
        type = in.readInt();
        thumb = in.readString();
    }

    public static final Creator<VideoVoiceFileBean> CREATOR = new Creator<VideoVoiceFileBean>() {
        @Override
        public VideoVoiceFileBean createFromParcel(Parcel in) {
            return new VideoVoiceFileBean(in);
        }

        @Override
        public VideoVoiceFileBean[] newArray(int size) {
            return new VideoVoiceFileBean[size];
        }
    };

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    private String displayName ;

    private String path;

    private long size;

    private long duration;

    private long courseId;

    private int type; // 0 视频 1 文档 2 音频


    private String thumb;

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isSelect ? 1 : 0));
        dest.writeString(displayName);
        dest.writeString(path);
        dest.writeLong(size);
        dest.writeLong(duration);
        dest.writeLong(courseId);
        dest.writeInt(type);
        dest.writeString(thumb);
    }
}
