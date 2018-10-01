package com.example.liqingfeng.sscapp.Presenter.Util.FileUtil;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import static android.content.Context.MODE_PRIVATE;

public class FileManager {
    private Context context;
    private String filename;

    /**
     * 初化始文件操作类
     * @param context Activity上下文
     * @param filename 文件名
     */
    public FileManager(Context context, String filename) {
        this.context=context;
        this.filename=filename;
    }

    /**
     * 向指定文件写入
     * @param filename 文件名
     * @param content 写入类容
     */
    public void writeFileData(String filename, String content){

        try {

            FileOutputStream fos = context.openFileOutput(filename, MODE_PRIVATE);//获得FileOutputStream

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
     * @param fileName 文件名
     * @return
     */
    public String readFileData(String fileName){

        String result="";

        try{
            FileInputStream fis =
                    context.openFileInput(fileName);

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
}
