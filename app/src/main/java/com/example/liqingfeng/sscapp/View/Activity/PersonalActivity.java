package com.example.liqingfeng.sscapp.View.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.os.Handler;
import android.os.TestLooperManager;
import android.support.annotation.Nullable;
import android.util.Log;
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
    private TextView musAge;
    private TextView musInstitution;
    private Users userinfo;

    Handler handler = new Handler()
    {
        public void handleMessage(android.os.Message msg) {
            if(msg.what==0x123)
            {
                // 接受到消息说明已经完成了信息和头像得获取
                show();
            } else if(msg.what == 0x124) {
                onBackPressed();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        initActivity();
        loadActivity();
    }

    /**
     *  填入数据
     */
    private void show() {
        dealImage();
        musNickName.setText(userinfo.getUsName());
        musName.setText(userinfo.getUsNickname());
        musSex.setText(userinfo.getUsSex());
        musClass.setText(userinfo.getUsClass());
        musSign.setText(userinfo.getUsSign());
        musAge.setText(userinfo.getUsAge());
        musInstitution.setText(userinfo.getUsInstitution());
    }

    /**
     * 处理传入的bitmap，更新UI
     */
    private void dealImage() {
        mHeadP.setImageBitmap(headPicture);
        Bitmap or = headPicture;
        or = FastBlurUtils.doBlur(or,20,false);
        mBackP.setImageBitmap(or);
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
        musAge=(TextView) findViewById(R.id.info_userAge);
        musInstitution=(TextView) findViewById(R.id.info_Institution);
        userinfo = new Users();
        // 获取到传递来的头像
        headPicture = getIntent().getParcelableExtra("headPicture");

    }

    /**
     * 加载用户页面信息
     */
    private void loadActivity() {
        getInfo();
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
                                // 读取用户信息
                                readUser(result);
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
     * 读取用户的个人信息
     * @param result
     */
    private void readUser(ResponseModel result) {
        userinfo.setUsName((String) result.getFromData("usName"));
        userinfo.setUsNickname((String) result.getFromData("usNickname"));
        userinfo.setUsClass((String) result.getFromData("usClass"));
        int sex = DataConvertUtil.toInt( result.getFromData("usSex"));
        if (sex == 1) {
            userinfo.setUsSex("男");
        } else if (sex == 2) {
            userinfo.setUsSex("女");
        } else {
            userinfo.setUsSex("null");
        }
        userinfo.setUsSign((String) result.getFromData("usSign"));
        userinfo.setUsAge(DataConvertUtil.doubleToString( (double)result.getFromData("usAge")));
        userinfo.setUsInstitution((String) result.getFromData("usInstitution"));
        UserConstant.user_head_picture = (String) result.getFromData("usImg");
        handler.sendEmptyMessage(0x123);
    }

    /**
     * 返回按钮的点击事件
     * @param view
     */
    public void backMainActivity(View view) {
        // 请求最新的用户信息并存起来
        requestNewInfo();
    }
    /**
     * 系统回调函数 返回intent携带的数据
     */
    @Override
    public void onBackPressed() {
        Intent intent=new Intent();
        intent.putExtra("nickname",userinfo.getUsNickname());
        intent.putExtra("sign",userinfo.getUsSign());
        intent.putExtra("image",headPicture);
        this.setResult(0x1, intent);
        this.finish();
    }

    /**
     * 头像的点击事件，打开修改头像的页面,等待返回
     * @param view
     */
    public void changePicture(View view) {
        startActivityForResult(new Intent(this,MyPhotoActivity.class),0x04);
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }

    /**
     * 得到返回的图片的URL，然后选择出图片
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0x04:
                if(resultCode == MyPhotoActivity.FINSH_RESULT && data != null){
                    String path = data.getStringExtra("image");
                    //Log.d("jiejie"," -----MainActivity------" + path);
                    // 得到选择图片Bitmap
                    headPicture = BitmapFactory.decodeFile(path);
                    // 将得到的图片Bitmap得到，然后显示出来
                    dealImage();
                }
                break;
            case 0x123:
                if(resultCode == 123 && data != null) {
                    String json = data.getExtras().getString("user");
                    userinfo = CheckStatuss.gson.fromJson(json, Users.class);
                    show();
                }
                break;
            case 0x1234:
                break;
        }
    }

    /**
     *
     */
    private void requestNewInfo() {
        RequestManager requestManager = RequestManager.getInstance(this);
        requestManager.requestAsyn(UrlConfig.findUserByID,RequestManager.TYPE_GET,
                new Param( "userId", UserConstant.uesrID + "" ).end(),true,
                new RequestManager.ReqCallBack<ResponseModel>() {
                    @Override
                    public void onReqSuccess(ResponseModel result) {
                        if (CheckStatuss.CheckStatus(result, getApplicationContext()) == 1) {
                            UserConstant.user_head_picture = (String) result.getFromData("usImg");
                            handler.sendEmptyMessage(0x124);
                        }
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {

                    }
                });
    }

    /**
     * 编辑用户资料,等待修改好的用户信息
     * @param view
     */
    public void changeInfo(View view) {
        Intent intent = new Intent(this, ChangeInfoActivity.class);
        String userjson = CheckStatuss.gson.toJson(userinfo);
        intent.putExtra("userInfo",userjson);
        startActivityForResult(intent, 0x123);
    }

    /**
     * 修改密码
     * @param view
     */
    public void sportTrip(View view) {
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        startActivityForResult(intent, 1);
        overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
    }
}
