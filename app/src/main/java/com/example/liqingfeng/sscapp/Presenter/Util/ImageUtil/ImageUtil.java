package com.example.liqingfeng.sscapp.Presenter.Util.ImageUtil;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.liqingfeng.sscapp.Model.UrlConfig;
import com.example.liqingfeng.sscapp.Model.UserConstant;

import org.xml.sax.Parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ImageUtil {

    //手机的照片储存的位置
    public static String imageBasePath="/mnt/sdcard/swust_sports/";
    //手机储存照片的名字
    public static String imageName;

    public static String spImg_imagename="";
    public static String spRoing_imagename="";

    //网络请求基础地址
    public static String Base_Url=UrlConfig.imageBaseUrl;

    /**
     * 获取照片的名字
     * @param imgpth
     * @return
     */
    public static String setImageName(String imgpth)
    {
        String temp;
        temp=imgpth.toString().substring(15);
        return temp;
    }


    /**
     * 将bitmao对象存入SD卡中
     * @param bitmap
     * @param imgPath
     * @throws IOException
     */
    public static void SavaImage(Bitmap bitmap,String imgPath) throws IOException {
        if (imgPath.equals("")) {
            return ;
        }
        imageName=setImageName(imgPath);
        File f = new File(imageBasePath+imageName);
        FileOutputStream fileOutputStream = null;
        //文件夹不存在，则创建它
        if(isFolderExists("d")==true) {
            //判断文件存不存在
            if (!f.exists()) {
                f.createNewFile();
                fileOutputStream = new FileOutputStream(f);
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, fileOutputStream);
                fileOutputStream.close();
            }
        }
    }

    /**
     * 读取SD卡中的图片数据，并将它转成bitmao对象返回
     * @param imgPath
     * @return
     * @throws FileNotFoundException
     */
    public static Bitmap getImage(String imgPath) throws FileNotFoundException {
        if (imgPath.equals("")) {
            return null;
        }
        imageName=setImageName(imgPath);
        FileInputStream fs = new FileInputStream(imageBasePath+imageName);
        Bitmap bitmap  = BitmapFactory.decodeStream(fs);
        if (bitmap == null){
            Log.d("bad", "getImage: no picture finding");
            return null;
        }
        return bitmap;
    }


    /**
     * 判断文件夹存不存，不存在就创建文件夹
     * @param strFolder
     * @return
     */
    private static  boolean isFolderExists(String strFolder)
    {
        strFolder=imageBasePath;
        File file = new File(strFolder);

        if (!file.exists())
        {
            if (file.mkdir())
            {
                return true;
            }
            else
                return false;
        }
        return true;
    }



    /**
     * 将List数据传入 ， 然后读取里面的下载零接口，多线程下载
     * @param list
     */
    public static void Image_down(final List<Map<String,String>> list)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<list.size();i++)
                {
                    spRoing_imagename= list.get(i).get("spRoimg");
                    spImg_imagename=list.get(i).get("spImg");
                    down_image(Base_Url+spImg_imagename,spImg_imagename);
                    down_image(Base_Url+spRoing_imagename,spRoing_imagename);
                }
            }
        }).start();
    }

    /**
     * 内部方法 下载图片
     * @param url
     * @param imgname
     */
    public static void down_image(String url, final String imgname)
    {
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder()
                .get()
                .url(url)
                .addHeader( "Authorization", UserConstant.tokenCode )
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream inputStream=response.body().byteStream();
                Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                if(bitmap == null) {
                    System.out.println("bitmao is null");
                } else {
                    ImageUtil.SavaImage(bitmap,imgname);
                }
            }
        });
    }

    public static void saveImageToGallery(Activity context, Bitmap bmp) {
        String local_path = imageBasePath ;
        // 创建文件夹
//        File appDir = new File(Environment.getExternalStorageDirectory(), "imageok");
        File appDir = new File(local_path, "imageok");
        //判断不存在就创建
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        //以时间命名
        String fileName = System.currentTimeMillis() + ".png";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        String path = Environment.getExternalStorageDirectory().getPath();
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
    }
}
