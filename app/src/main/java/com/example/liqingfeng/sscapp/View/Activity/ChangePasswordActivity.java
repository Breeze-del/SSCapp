package com.example.liqingfeng.sscapp.View.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.liqingfeng.sscapp.Model.Entity.Param;
import com.example.liqingfeng.sscapp.Model.Entity.ResponseModel;
import com.example.liqingfeng.sscapp.Model.UrlConfig;
import com.example.liqingfeng.sscapp.Presenter.CheckStatuss;
import com.example.liqingfeng.sscapp.Presenter.Util.FileUtil.FileManager;
import com.example.liqingfeng.sscapp.Presenter.Util.OkhttpUtil.RequestManager;
import com.example.liqingfeng.sscapp.R;


public class ChangePasswordActivity extends Activity {
    private EditText mOldPa;
    private EditText mNewPa1;
    private EditText mNewPa2;
    private Button ok;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepasw);
        initView();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getInfo();
            }
        });
    }

    private void getInfo() {
        String oldPa = mOldPa.getText().toString();
        String new1 = mNewPa1.getText().toString();
        String new2 = mNewPa2.getText().toString();
        if (oldPa.trim().equals("") || new1.trim().equals("")
                || new2.trim().equals("")) {
            Toast.makeText(this, "输入不能为空", Toast.LENGTH_SHORT).show();
        } else if (!new1.equals(new2)){
            Toast.makeText(this,"两次输入密码不一致",Toast.LENGTH_SHORT).show();
        } else {
            //数据加密
            oldPa += "swust_sport";
            new1 += "swust_sport";
            new1 = android.util.Base64.encodeToString(new1.getBytes(),
                    android.util.Base64.DEFAULT);
            oldPa = android.util.Base64.encodeToString(oldPa.getBytes(),
                    android.util.Base64.DEFAULT);
            new1 = new1.replaceAll("[\\s*\t\n\r]", "");
            oldPa = oldPa.replaceAll("[\\s*\t\n\r]", "");
            sendPassword( new1, oldPa);
        }
    }

    private void sendPassword(String newPassword, String oldPassword) {
        RequestManager requestManager = RequestManager.getInstance(this);
        requestManager.requestPutWithParam(UrlConfig.changePasswordUrl,
                new Param().append("oldPwd",oldPassword).append("newPwd",newPassword).end(), true,
                new RequestManager.ReqCallBack<ResponseModel>() {
                    @Override
                    public void onReqSuccess(ResponseModel result) {
                        if (CheckStatuss.CheckStatus(result, getApplicationContext()) == 1) {
                            //不干什么
                            if(result.getData().equals("true")) {
                                Toast.makeText(getApplicationContext(),"修改密码成功,请重新登陆",Toast.LENGTH_SHORT).show();
                                FileManager fileManager = new FileManager(getApplicationContext(), UrlConfig.userInformation);
                                fileManager.writeFileData("");
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),"修改密码失败",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {
                        Log.e("falue", errorMsg);
                    }
                });
    }

    private void initView() {
        mOldPa = (EditText) findViewById(R.id.password_old);
        mNewPa1 = (EditText) findViewById(R.id.password_new1);
        mNewPa2 = (EditText) findViewById(R.id.password_new2);
        ok = (Button) findViewById(R.id.password_ok);
    }

    public void backForNoChange(View view) {
        Intent intent = new Intent(this, PersonalActivity.class);
        setResult(0x124,intent);
        overridePendingTransition(R.anim.anim_out,R.anim.anim_in);
    }
}
