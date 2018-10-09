package com.example.liqingfeng.sscapp.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 全局本机用户的信息
 */
public class UserConstant {
    /**
     * 本机用户账号
     */
    public static String userName;
    /**
     * 本机用户的Token
     */
    public static String tokenCode="eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJ3YW5nIiwiYXVkIjoid2FuZyIsImV4cCI6MTUzOTE1MDg3NCwidXNlcklkIjoxL" +
            "CJpYXQiOjE1MzkwNjQ0NzQsInJvbGVJZCI6MX0.mPxEWg8X3uDbQJiKHK3CmzpccR6qQ3eNeC1plE6Wuk8";
    /**
     * 本机用户的昵称
     */
    public static String userNickName;
    /**
     * 本机用户ID
     */
    public static int uesrID;
    /**
     * 全局的运动模块
     */
    public static List<Map<String,String>> list_sport=new ArrayList<>();
    /**
     * 全局的运动房间
     */
    public static List<Map<String,Object>> list_room=new ArrayList<>(  );
    /**
     * 运动房间的地址
     */
    public static String room_image_path="";
    /**
     * 运动房间名字
     */
    public static String room_Sport_name="";
}
