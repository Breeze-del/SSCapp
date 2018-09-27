package com.example.liqingfeng.sscapp.Model;

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
