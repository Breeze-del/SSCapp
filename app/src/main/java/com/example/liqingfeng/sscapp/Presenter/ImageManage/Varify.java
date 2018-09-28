package com.example.liqingfeng.sscapp.Presenter.ImageManage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.example.liqingfeng.sscapp.Model.ResponseModel;
import com.example.liqingfeng.sscapp.Presenter.DataConstant;
import com.example.liqingfeng.sscapp.Presenter.UrlConfig;
import com.example.liqingfeng.sscapp.Util.OkhttpUtil.RequestManager;

/**
 * 全局函数
 */
public class Varify {
    /**
     * 全局获取验证码
     * @param context activity上下文
     */
    public static void getVarify(Context context) {
        RequestManager requestManager = RequestManager.getInstance( context );
        requestManager.requestGetWithoutParam( UrlConfig.verifyUrl, new RequestManager.ReqCallBack<ResponseModel>() {
            @Override
            public void onReqSuccess(ResponseModel result) {
                String img = (String) result.getData();
                DataConstant.varifyCode=img;
            }

            @Override
            public void onReqFailed(String errorMsg) {
                Log.e( "verify failed", errorMsg.toString());
            }
        } );
    }

    /**
     * 传入一个bath64加密的string  转换为一个bitmap对象
     * @param string bath64加密的string
     * @return bitmap对象
     */
    public static Bitmap stringToBitmap(String string) {
        //将字符串转换成Bitmap类型
        Bitmap bitmap=null;
        try {
            byte[]bitmapArray;
            bitmapArray= Base64.decode(string, Base64.DEFAULT);
            bitmap= BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
