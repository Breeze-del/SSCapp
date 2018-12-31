package com.example.liqingfeng.sscapp.Model.Entity;

public class PersonsChat {
    private String id;
    private String imgUrl;
    private String name;
    private String chatMessage;
    private String time;
    private boolean isMeSend;

    public String getTime() {
        return time;
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