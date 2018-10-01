package com.example.liqingfeng.sscapp.Presenter.ImageManage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;


/**
 * 全局函数
 */
public class Varify {
    /**
     * 传入一个bath64加密的string  转换为一个bitmap对象
     * @param string bath64加密的string
     * @return bitmap对象
     */
    public static Bitmap stringToBitmap(String string) {
        //将字符串转换成Bitmap类型
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
