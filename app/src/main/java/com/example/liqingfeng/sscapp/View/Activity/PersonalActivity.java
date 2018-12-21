package com.example.liqingfeng.sscapp.View.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.liqingfeng.sscapp.Model.Entity.Param;
import com.example.liqingfeng.sscapp.Model.Entity.ResponseModel;
import com.example.liqingfeng.sscapp.Model.Entity.Users;
import com.example.liqingfeng.sscapp.Model.UrlConfig;
import com.example.liqingfeng.sscapp.Model.UserConstant;
import com.example.liqingfeng.sscapp.Presenter.CheckStatuss;
import com.example.liqingfeng.sscapp.Presenter.Util.ConvertUtil.DataConvertUtil;
import com.example.liqingfeng.sscapp.Presenter.Util.ImageUtil.FastBlurUtils;
import com.example.liqingfeng.sscapp.Presenter.Util.ImageUtil.ImageLoaderUtil;
import com.example.liqingfeng.sscapp.Presenter.Util.OkhttpUtil.RequestManager;
import com.example.liqingfeng.sscapp.R;


/**
 * 个人信息页面 （通过点击头像进入）
 */
public class PersonalActivity extends Activity {
    private ImageView mBackP;
    private ImageView mHeadP;
    private Bitmap headPicture;
    private TextView musName;
    private TextView musNickName;
    private TextView musSex;
    private TextView musClass;
    private TextView musSign;
    private Users userinfo;

    Handler handler = new Handler()
    {
        public void handleMessage(android.os.Message msg) {
            if(msg.what==0x123)
            {
                show();
            }
        };
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        initActivity();
        getInfo();
    }

    /**
     *  填入数据
     */
    private void show() {
        mHeadP.setImageBitmap(headPicture);
        headPicture = FastBlurUtils.doBlur(headPicture,20,false);
        mBackP.setImageBitmap(headPicture);

        musNickName.setText(userinfo.getUsNickname());
        musName.setText(userinfo.getUsName());
        musSex.setText(userinfo.getUsSex());
        musClass.setText(userinfo.getUsClass());
        musSign.setText(userinfo.getUsSign());
    }

    /**
     * 初始化界面控件
     */
    private void initActivity() {
        mBackP =(ImageView) findViewById(R.id.h_back);
        mHeadP = (ImageView) findViewById(R.id.h_head);
        musNickName=(TextView) findViewById(R.id.info_nickname);
        musName=(TextView) findViewById(R.id.info_username);
        musSex=(TextView) findViewById(R.id.info_sex);
        musClass=(TextView) findViewById(R.id.info_classid);
        musSign=(TextView) findViewById(R.id.info_sign);
    }

    /**
     * 获取用户信息
     */
    private void getInfo() {
        RequestManager requestManager = RequestManager.getInstance(this);
        requestManager.requestAsyn(UrlConfig.findUserByID, RequestManager.TYPE_GET,
                new Param("userId", UserConstant.uesrID + "").end(),
                true, new RequestManager.ReqCallBack<ResponseModel>() {
                    @Override
                    public void onReqSuccess(ResponseModel result) {
                        if (CheckStatuss.CheckStatus(result, getApplicationContext()) == 1) {
                            String status = result.getFromData("usStatus") + "";
                            if (status.equals("1.0")) {
                                userinfo = new Users();
                                userinfo.setUsName((String) result.getFromData("usName"));
                                userinfo.setUsNickname((String) result.getFromData("usNickname"));
                                userinfo.setUsClass((String) result.getFromData("usClass"));
                                userinfo.setUsSex(DataConvertUtil.doubleToString((double)result.getFromData("usSex")));
                                userinfo.setUsSign((String) result.getFromData("usSign"));
                                UserConstant.user_head_picture = (String) result.getFromData("usImg");
                                // 获取头像
                                getHeadPicture();
                            } else {
                                Toast.makeText(getApplicationContext(), "用户已被锁定,请联系管理员", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            }
                        }
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {

                    }
                });
    }

    /**
     * 获取头像
     */
    private void getHeadPicture() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!UserConstant.user_head_picture.equals("")) {
                    ImageLoaderUtil imageload = ImageLoaderUtil.getInstance(getApplication());
                    headPicture = imageload.loadImageBySyn(UrlConfig.imageBaseUrl+UserConstant.user_head_picture);
                    // 更新UI
                    handler.sendEmptyMessage(0x123);
                }
            }
        }).start();
    }
    /**
     * 返回按钮的点击事件
     * @param view
     */
    public void backMainActivity(View view) {
        onBackPressed();
    }
    /**
     * 系统回调函数 返回intent携带的数据
     */
    @Override
    public void onBackPressed() {
        Intent intent=new Intent();
        intent.putExtra("nickname",userinfo.getUsNickname());
        intent.putExtra("sign",userinfo.getUsSign());
        this.setResult(1, intent);
        this.finish();
    }
}
