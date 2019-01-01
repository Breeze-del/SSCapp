package com.example.liqingfeng.sscapp.Model.Entity;

import com.example.liqingfeng.sscapp.Presenter.Util.ConvertUtil.DataConvertUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PersonsChat {
    // 用户ID
    private String id;
    // 图片url
    private String imgUrl;
    // 用户名字
    private String name;
    // 消息
    private String chatMessage;
    // 时间
    private String time;
    // 是不是自己发送得
    private boolean isMeSend;

    public String getTime(){
        Date dtime = new Date(Long.parseLong(time));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        return sdf.format(dtime);
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
    }


    public void setMeSend(boolean meSend) {
        isMeSend = meSend;
    }
    public boolean getMeSend() {
        return isMeSend;
    }

    public PersonsChat(String id, String name, String chatMessage, boolean isMeSend) {
        super();
        this.id = id;
        this.name = name;
        this.chatMessage = chatMessage;
        this.isMeSend = isMeSend;
    }
    public PersonsChat() {
        super();
    }
}