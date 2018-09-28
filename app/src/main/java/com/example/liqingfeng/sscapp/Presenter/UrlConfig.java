package com.example.liqingfeng.sscapp.Presenter;

/**
 * 记录所有的接口的地址
 */
public class UrlConfig {

    /**
     * 获取图片的基地址
     */
    public static String imageBaseUrl="http://192.168.1.14/avatars";
    /**
     * 获取业务接口的基地址
     */
    public static String bnsBaseUrl="http://192.168.1.14/api";
    /**
     * 登陆接口
     */
    public static String loginUrl="/user/login.do";
    /**
     * 注册接口
     */
    public static String registerUrl="/user/register.do";
    /**
     *获取验证码接口
     */
    public static String verifyUrl="/verify/getVerify.do";
}
