package com.example.liqingfeng.sscapp.View.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.liqingfeng.sscapp.Model.Entity.Param;
import com.example.liqingfeng.sscapp.Model.Entity.ResponseModel;
import com.example.liqingfeng.sscapp.Model.UrlConfig;
import com.example.liqingfeng.sscapp.Presenter.CheckStatuss;
import com.example.liqingfeng.sscapp.Presenter.ImageManage.Varify;
import com.example.liqingfeng.sscapp.Presenter.Util.OkhttpUtil.RequestManager;
import com.example.liqingfeng.sscapp.R;
import com.example.liqingfeng.sscapp.View.CustomView.ClipViewLayout;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class ClipImageActivity extends AppCompatActivity implements View.OnClickListener {
    private ClipViewLayout clipViewLayout1;
    private ClipViewLayout clipViewLayout2;
    private ImageView back;
    private TextView tv_ok;
    //类别 1：qq  2：微信
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip_image);
        type = getIntent().getIntExtra("type",1);
        initView();
    }

    private void initView() {
        clipViewLayout1 = (ClipViewLayout)findViewById(R.id.clipViewLayout1);
        clipViewLayout2 = (ClipViewLayout)findViewById(R.id.clipViewLayout2);
        back = (ImageView)findViewById(R.id.photo_back);
        tv_ok = (TextView)findViewById(R.id.photo_ok);
        back.setOnClickListener(this);
        tv_ok.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(type == 1){
            clipViewLayout1.setVisibility(View.VISIBLE);
            clipViewLayout2.setVisibility(View.GONE);
            //设置图片资源
            clipViewLayout1.setImageSrc(getIntent().getData());
        }else {
            clipViewLayout2.setVisibility(View.VISIBLE);
            clipViewLayout1.setVisibility(View.GONE);
            clipViewLayout2.setImageSrc(getIntent().getData());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.photo_back:
                finish();
                break;
            case R.id.photo_ok:
                generateUriAndReturn();
                break;
        }
    }

    /**
     * 生成Uri并且通过setResult返回给打开的Activity
     */
    private void generateUriAndReturn() {
        //调用返回剪切图
        Bitmap zoomedCropBitmap;
        if (type == 1) {
            zoomedCropBitmap = clipViewLayout1.clip();
        } else {
            zoomedCropBitmap = clipViewLayout2.clip();
        }
        if (zoomedCropBitmap == null) {
            Log.e("android", "zoomedCropBitmap == null");
            return;
        }
        Uri mSaveUri = Uri.fromFile(new File(getCacheDir(), "cropped_" + System.currentTimeMillis() + ".jpg"));
        if (mSaveUri != null) {
            OutputStream outputStream = null;
            try {
                outputStream = getContentResolver().openOutputStream(mSaveUri);
                if (outputStream != null) {
                    zoomedCropBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                }
            } catch (IOException ex) {
                Log.e("android", "Cannot open file: " + mSaveUri, ex);
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            // 将bitmap上传到后台
            sendBitmapToS(zoomedCropBitmap);

            Intent intent = new Intent();
            intent.setData(mSaveUri);
            setResult(RESULT_OK, intent);
        }
    }

    /**
     * 将选择的bitmap上传到后台
     * @param headBitmap 新的到的头像bitmap
     */
    private void sendBitmapToS(Bitmap headBitmap) {
        Varify varify = new Varify();
        String hpicture =varify.bitmapToBase64(headBitmap);
        RequestManager requestManager = RequestManager.getInstance(this);
        requestManager.requestPutWithParam(UrlConfig.sendHeadPicture,
                new Param().append("avatar",hpicture).end(), true,
                new RequestManager.ReqCallBack<ResponseModel>() {
                    @Override
                    public void onReqSuccess(ResponseModel result) {
                        if (CheckStatuss.CheckStatus(result, getApplicationContext()) == 1) {
                            //不干什么
                            finish();
                        }
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {
                        Log.e("falue", errorMsg);
                    }
                });
    }
}
