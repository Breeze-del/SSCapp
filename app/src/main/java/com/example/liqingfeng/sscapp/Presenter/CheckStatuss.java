package com.example.liqingfeng.sscapp.Presenter;

import android.content.Context;
import android.widget.Toast;

import com.example.liqingfeng.sscapp.Model.Entity.ResponseModel;
import com.google.gson.Gson;

/**
 * 全局数据
 */
public class CheckStatuss {
    /**
     * 全局的Gson对象
     */
    public static Gson gson=new Gson();

    /**
     * 判断 请求返回信息 是否过期
     * @param responseModel 请求返回信息实体
     * @param context       activity上下文
     * @return              返回是否出错
     */
    public static int CheckStatus(ResponseModel responseModel, Context context) {
        if (responseModel == null) {
            Toast.makeText( context,"网络超时，请重试！",Toast.LENGTH_SHORT ).show();
            return 2;
        } else if (responseModel.getCode().equals( "success" )) {
            return 1;
        } else if (responseModel.getCode().equals( "JWT WRONG" )){
            Toast.makeText( context,"JWT WRONG",Toast.LENGTH_SHORT ).show();
            return 3;
        } else if(responseModel.getCode().equals( "JWT EXPIRE" )){
            Toast.makeText( context,"用户登陆过期",Toast.LENGTH_SHORT ).show();
            return 3;
        } else{
            Toast.makeText( context,"后台出错啦",Toast.LENGTH_SHORT ).show();
            return 2;
        }
    }
    /*
     检查返回值的意义：
     1————success 执行后面操作
     2————不执行任何操作 停留在当前的界面中
     3————跳转到登陆界面
     */
}
