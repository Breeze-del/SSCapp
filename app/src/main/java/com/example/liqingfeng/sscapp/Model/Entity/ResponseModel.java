package com.example.liqingfeng.sscapp.Model.Entity;

import java.util.Map;

/**
 * 实体类 返回状态解析
 */
public class ResponseModel {
    /**
     * 连接成功
     */
    public static final String SUCCESS = "success";
    /**
     * 连接失败
     */
    public static final String FAILED = "failed";
    /**
     * 返回数据包
     */
    private Object data;
    /**
     * 返回状态码
     */
    private String code;
    /**
     * 返回token值
     */
    private String token;
    /**
     * 返回数据组数
     */
    private int total;

    public Object getFromData(String key) {
        Map<String,Object> nowData =(Map<String, Object>) data;
        if (data == null) {
            return "data undefined";
        }
        int start = 0, end = 0;
        while (true) {
            end = key.indexOf(".", start);
            if (end != -1) {//end of the key
                String name = key.substring(start,end);
                nowData = (Map<String,Object>)nowData.get(name);
                if (nowData == null) {
                    return "data undefined";
                }
                start = end + 1;
            } else {
                String name = key.substring(start);
                Object result = nowData.get(name);
                if (result == null) {
                    return "data undefined";
                } else {
                    return result;
                }
            }
        }

    }
    public static String getSUCCESS() {
        return SUCCESS;
    }

    public static String getFAILED() {
        return FAILED;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }


    public ResponseModel()
    {
        super();
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
