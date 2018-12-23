package com.example.liqingfeng.sscapp.Model.Entity;

public class Image {
    private int id;//数据的ID
    private String path;//图片的路径
    private long date;//图片的创建日期
    private boolean isSelect;//是否选中

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
