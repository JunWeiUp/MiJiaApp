package com.mijia.app.bean;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class UploadHistoryBean {

    @Id
    public Long id;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
