package com.mijia.app.bean;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.mijia.app.utils.file.MediaBean;


import java.util.List;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Relation;
import io.objectbox.relation.ToMany;

/**
 *
 * @author Administrator
 * @date 2019/6/13
 */

@SuppressLint("ParcelCreator")
@Entity
public class PhotoArrayListBean implements Parcelable{



    @Id
    public Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private String date;

    public ToMany<MediaBean> mMediaBeans;

    public ToMany<MediaBean> getMediaBeans() {
        return mMediaBeans;
    }

    public void setMediaBeans(ToMany<MediaBean> mediaBeans) {
        mMediaBeans = mediaBeans;
    }

    private  List<MediaBean> mediaBean;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<MediaBean> getMediaBean() {
        return mediaBean;
    }

    public void setMediaBean(List<MediaBean> mediaBean) {
        this.mediaBean = mediaBean;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
