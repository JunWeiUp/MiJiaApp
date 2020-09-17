package com.mijia.app.bean;

/**
 * Created by Administrator on 2019/6/6.
 */

public class SimpleBean {

    public SimpleBean() {

    }

    public SimpleBean(int type) {
        this.type = type;
    }

    public SimpleBean(String name) {
        this.name = name;
    }

    private boolean isSelected = false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    private int type;

    private String name;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
