package com.example.liqingfeng.sscapp.Presenter.Util.FileUtil;

import android.content.Context;
import android.util.Log;

import com.example.liqingfeng.sscapp.Model.Entity.ResponseModel;
import com.example.liqingfeng.sscapp.Presenter.CheckStatuss;
import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import static android.content.Context.MODE_PRIVATE;

public class FileManager {
    private Context context;
    private String fileName;
    /**
     * 初化始文件操作类
     * @param context Activity上下文
     * @param filename 文件名
     */
    public FileManager(Context context, String filename) {
        this.context=context;
        this.fileName=filename;
    }
    /**
     * 向指定文件写入
     * @param content 写入类容
     */
    public void writeFileData( String content){

        try {
            System.out.print( context.getFilesDir() );
            FileOutputStream fos = context.openFileOutput(fileName, MODE_PRIVATE);//获得FileOutputStream
            //将要写入的字符串转换为byte数组
            byte[]  bytes = content.getBytes();

            fos.write(bytes);//将byte数组写入文件

            fos.close();//关闭文件输出流

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 向指定文件读入
     * @return  返回读取的类容
     */
    public String readFileData(){

        String result="";

        try{
            FileInputStream fis = context.openFileInput(fileName);

            //获取文件长度
            int lenght = fis.available();

            byte[] buffer = new byte[lenght];

            fis.read(buffer);

            //将byte数组转换成指定格式的字符串
            result = new String(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  result;
    }
    /**
     * 将登陆信息存进文件中
     */
    public void saveUserInfomation(final ResponseModel userInformation) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                Gson gson = CheckStatuss.gson;
                String josn=gson.toJson( userInformation );
                writeFileData( josn );
                //Log.e("file","文件写入成功");
            }
        } ).start();
    }
}
