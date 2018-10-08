package com.example.liqingfeng.sscapp.Model.Entity;

import java.util.HashMap;

/**
 * 存放信息的MAP类
 */
public class Param {
    private HashMap<String,String> data = new HashMap<>();
    public Param(String key, String value){
        data.put(key,value);
    }
    public Param(){
    }
    public Param append(String key, String value) {
        data.put(key, value);
        return this;
    }
    public HashMap<String, String> end() {
        return data;
    }

}
