package com.example.liqingfeng.sscapp.View.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liqingfeng.sscapp.Model.Entity.Param;
import com.example.liqingfeng.sscapp.Model.Entity.ResponseModel;
import com.example.liqingfeng.sscapp.Model.Entity.Users;
import com.example.liqingfeng.sscapp.Model.UrlConfig;
import com.example.liqingfeng.sscapp.Model.UserConstant;
import com.example.liqingfeng.sscapp.Presenter.CheckStatuss;
import com.example.liqingfeng.sscapp.Presenter.Util.OkhttpUtil.RequestManager;
import com.example.liqingfeng.sscapp.R;

public class ChangeInfoActivity extends Activity {

    private TextView mNickname;
    private TextView mUserAge;
    private TextView mUserSex;
    private TextView mClass;
    private TextView mInstitution;
    private TextView mUserSign;
    private Users userInfo;
    private String userJson;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeinfo);
        initView();
        show();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        // 获取到个人信息页面传递来的数据
        Intent intent = getIntent();
        userJson = intent.getExtras().getString("userInfo");
        userInfo = CheckStatuss.gson.fromJson(userJson, Users.class);

        mNickname = (TextView) findViewById(R.id.change_userNickname);
        mUserAge = (TextView) findViewById(R.id.change_userAge);
        mUserSex = (TextView) findViewById(R.id.change_sex);
        mClass = (TextView) findViewById(R.id.change_classid);
        mInstitution = (TextView) findViewById(R.id.change_Institution);
        mUserSign = (TextView) findViewById(R.id.change_sign);
    }

    /**
     * 展示信息
     */
    private void show() {
        mNickname.setText(userInfo.getUsNickname());
        mUserAge.setText(userInfo.getUsAge());
        mUserSex.setText(userInfo.getUsSex());
        mClass.setText(userInfo.getUsClass());
        mInstitution.setText(userInfo.getUsInstitution());
        mUserSign.setText(userInfo.getUsSign());
    }
    /**
     * 获取控件的输入
     */
    private void getInfo() {
        userInfo.setUsNickname(mNickname.getText().toString());
        userInfo.setUsAge(mUserAge.getText().toString());
        userInfo.setUsSex(mUserSex.getText().toString());
        userInfo.setUsClass(mClass.getText().toString());
        userInfo.setUsInstitution(mInstitution.getText().toString());
        userInfo.setUsSign(mUserSign.getText().toString());

        UserConstant.userNickName = userInfo.getUsNickname();
        UserConstant.user_Sign = userInfo.getUsSign();
    }

    /**
     *  返回到个人信息界面
     * @param view
     */
    public void backToPersonal(View view) {
        Intent intent = new Intent(this, PersonalActivity.class);
        intent.putExtra("user",userJson);
        setResult(123, intent);
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
        finish();
    }

    /**
     * 确认修改个人信息
     * @param view
     */
    public void okChange(View view) {
        // 获取修改信息
        getInfo();
        // 信息发给后台
        senToWZY();
        // 信息传给上一个界面
        Intent intent=new Intent(this, PersonalActivity.class);
        String json = CheckStatuss.gson.toJson(userInfo);
        intent.putExtra("user",json);
        setResult(123, intent);
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }

    /**
     * 将修改后的数据传递到后台
     */
    private void senToWZY() {
        RequestManager requestManager = RequestManager.getInstance(this);
        requestManager.requestPutWithParam(UrlConfig.changeUserInfo,
                new Param().append("usSex",userInfo.getUsSex()).append("usNickname",userInfo.getUsNickname())
                        .append("usAge",userInfo.getUsAge()).append("usSign",userInfo.getUsSign())
                        .append("usClass",userInfo.getUsClass()).append("usInstitution",userInfo.getUsInstitution())
                        .end(), true,
                new RequestManager.ReqCallBack<ResponseModel>() {
                    @Override
                    public void onReqSuccess(ResponseModel result) {
                        if (CheckStatuss.CheckStatus(result, getApplicationContext()) == 1) {
                            //不干什么
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),"后台错误",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {
                        Log.e("falue", errorMsg);
                    }
                });
    }

}
