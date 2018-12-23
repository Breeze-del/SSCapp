package com.example.liqingfeng.sscapp.View.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.liqingfeng.sscapp.BuildConfig;
import com.example.liqingfeng.sscapp.Model.Entity.Image;
import com.example.liqingfeng.sscapp.Presenter.Adapter.MyPhotoAdapter;
import com.example.liqingfeng.sscapp.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyPhotoActivity extends AppCompatActivity {
    private static final int REQUEST_CAPTURE = 100;
    //private static final int REQUEST_PICK = 101;
    private static final int REQUEST_CROP_PHOTO = 102;

    public static  final int FINSH_RESULT = 104;//截图后的返回

    private static final int LOADER_ID = 0x0100;
    private LoadCallBack mLoad = new LoadCallBack();

    private MyPhotoAdapter mAdapter;
    private List<Image> images = new ArrayList<>();
    //调用照相机返回图片文件
    private File tempFile;
    private static final int MIN_IMAGE_FILE_SIZE = 10 * 1024; // 最小的图片大小

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myphoto);

        GridView gv_photo = (GridView)findViewById(R.id.gv_photo);
        ImageView img_back = (ImageView)findViewById(R.id.iv_back);
        images.add(new Image());
        mAdapter = new MyPhotoAdapter(this,images);
        gv_photo.setAdapter(mAdapter);
        gv_photo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0){
                    //第一个就去照相
                    if(hasPermission(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE})){
                        gotoCamera();
                    }else {
                        requestPermission(0x02,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE});
                    }
                }else {
                    //这里点击获取到图片地址然后裁剪
                    gotoClipActivity(Uri.parse(images.get(i).getPath()));
                }
            }
        });
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(hasPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE})){
            getLoaderManager().initLoader(LOADER_ID,null,mLoad);
        }else {
            requestPermission(0x01,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
        }
    }

    private class LoadCallBack implements LoaderManager.LoaderCallbacks<Cursor>{
        private final String[] IMAGE_PROJECTION = new String[]{
                MediaStore.Images.Media._ID,//Id
                MediaStore.Images.Media.DATA,//图片路径
                MediaStore.Images.Media.DATE_ADDED//图片的创建时间
        };

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            //创建一个Loader
            if(id == LOADER_ID){
                //如果是我们的ID则进行初始化
                return new CursorLoader(getBaseContext(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_PROJECTION,
                        null,
                        null,
                        IMAGE_PROJECTION[2] + " DESC");
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            //当Loader加载完成时
            List<Image> images = new ArrayList<>();
            //判断是否有数据
            if(data != null){
                int count = data.getCount();
                if(count > 0){
                    data.moveToFirst();
                    // 得到对应的列的Index坐标
                    int indexId = data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]);
                    int indexPath = data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]);
                    int indexDate = data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]);
                    do {
                        // 循环读取，直到没有下一条数据
                        int id = data.getInt(indexId);
                        String path = data.getString(indexPath);
                        long dateTime = data.getLong(indexDate);

                        File file = new File(path);
                        if (!file.exists() || file.length() < MIN_IMAGE_FILE_SIZE) {
                            // 如果没有图片，或者图片大小太小，则跳过
                            continue;
                        }
                        // 添加一条新的数据
                        Image image = new Image();
                        image.setId(id);
                        image.setPath(path);
                        image.setDate(dateTime);
                        images.add(image);

                    } while (data.moveToNext());
                }
            }
            updateSource(images);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            updateSource(null);
        }
    }

    /**
     * 通知Adapter数据更改的方法
     * @param images 新的数据
     */
    private void updateSource(List<Image> images){
        this.images.clear();
        this.images.add(new Image());
        if(images == null || images.size() == 0) return;
        this.images.addAll(images);
        mAdapter.notifyDataSetChanged();
    }

    /**
     *权限的返回
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 0x02:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    gotoCamera();
                }
                break;
            case 0x01:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getLoaderManager().initLoader(LOADER_ID,null,mLoad);
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CAPTURE://系统相机返回
                if(resultCode == RESULT_OK){
                    Log.d("jiejie","--------相机---------" + Uri.fromFile(tempFile).toString());
                    Log.d("jiejie","--------path----------" + getRealFilePathFromUri(MyPhotoActivity.this,Uri.fromFile(tempFile)));
                    gotoClipActivity(Uri.fromFile(tempFile));
                }

                break;
            case REQUEST_CROP_PHOTO:
                if(resultCode == RESULT_OK){
                    if(data != null){
                        Uri uri = data.getData();
                        Log.d("jiejie","-------------" + data.getData().getPath());
                        String cropImagePath = getRealFilePathFromUri(MyPhotoActivity.this,uri);
                        Log.d("jiejie","------crop--------" + cropImagePath);
                        Intent intent = new Intent();
                        intent.putExtra("image",cropImagePath);
                        setResult(FINSH_RESULT,intent);
                        MyPhotoActivity.this.finish();
                    }

                }
                break;
        }
    }

    /**
     * 跳转到系统照相机
     */
    private void gotoCamera(){
        String SDState = Environment.getExternalStorageState();
        //判断SD卡是否存在
        if(SDState.equals(Environment.MEDIA_MOUNTED)){
            tempFile = new File(checkDirPath(Environment.getExternalStorageDirectory().getPath()+ "/image/"), System.currentTimeMillis() + ".jpg");
            //隐式的打开调用系统相册
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                //如果是7.0及以上的系统使用FileProvider的方式创建一个Uri
                Uri contentUri = FileProvider.getUriForFile(MyPhotoActivity.this, BuildConfig.APPLICATION_ID + ".fileProvider", tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
            }else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
            }
            startActivityForResult(intent,REQUEST_CAPTURE);
        }
    }

    /**
     * 打开截图的界面
     * @param uri
     */
    private void gotoClipActivity(Uri uri){
        if(uri == null){
            return;
        }
        Intent intent = new Intent(this,ClipImageActivity.class);
        intent.putExtra("type",1);
        intent.setData(uri);
        startActivityForResult(intent,REQUEST_CROP_PHOTO);
    }

    /**
     * 检查文件是否存在
     */
    private static String checkDirPath(String dirPath) {
        if (TextUtils.isEmpty(dirPath)) {
            return "";
        }
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath;
    }
    /**
     * 判断是否有指定的权限
     */
    public boolean hasPermission(String... permissions) {

        for (String permisson : permissions) {
            if (ContextCompat.checkSelfPermission(this, permisson)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    /**
     * 申请指定的权限.
     */
    public void requestPermission(int code, String... permissions) {

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(permissions, code);
        }
    }

    /**
     * 根据Uri返回文件绝对路径
     * 兼容了file:///开头的 和 content://开头的情况
     */
    public static String getRealFilePathFromUri(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
}
