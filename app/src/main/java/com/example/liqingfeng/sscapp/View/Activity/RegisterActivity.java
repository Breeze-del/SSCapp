package com.example.liqingfeng.sscapp.View.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.liqingfeng.sscapp.Model.Entity.Param;
import com.example.liqingfeng.sscapp.Model.Entity.ResponseModel;
import com.example.liqingfeng.sscapp.Presenter.CheckStatuss;
import com.example.liqingfeng.sscapp.Model.UrlConfig;
import com.example.liqingfeng.sscapp.Model.UserConstant;
import com.example.liqingfeng.sscapp.Presenter.Util.OkhttpUtil.RequestManager;
import com.example.liqingfeng.sscapp.R;

/**
 * 注册界面
 */
public class RegisterActivity extends Activity {
    private EditText userNickName;
    private EditText userName;
    private EditText userPassword1;
    private EditText userPassword2;
    private Button   backTologin;
    //两次密码
    private String passWord1,passWord2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_register );
        init();
    }

    /**
     * 初始化界面控件
     */
    private void init() {
        userNickName=(EditText)findViewById( R.id.register_nickname );
        userName=(EditText)findViewById( R.id.register_name );
        userPassword1=(EditText)findViewById( R.id.register_password1 );
        userPassword2=(EditText)findViewById( R.id.register_password2 );
    }
    /**
     * 注册按钮点击事件
     * @param view 本界面View对象
     */
    public void registerStart(View view) {
        UserConstant.userNickName=userNickName.getText().toString();
        UserConstant.userName=userName.getText().toString();
        passWord1=userPassword1.getText().toString();
        passWord2=userPassword2.getText().toString();
        if (UserConstant.userNickName.trim().equals("") || UserConstant.userName.trim().equals("")
                || passWord1.trim().equals("")||passWord2.trim().equals("")) {
            Toast.makeText(RegisterActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
        } else if (!passWord1.equals(passWord2)){
            Toast.makeText(RegisterActivity.this,"两次输入密码不一致",Toast.LENGTH_SHORT).show();
        } else {
            //数据加密
            passWord1 += "swust_sport";
            passWord1 = android.util.Base64.encodeToString(passWord1.getBytes(),
                    android.util.Base64.DEFAULT);
            passWord1 = passWord1.replaceAll("[\\s*\t\n\r]", "");
            register( passWord1);
        }
    }

    /**
     * 注册接口
     * 为了安全起见 未存用户密码
     * @param password  用户密码
     */
    private void register(String password) {
        RequestManager requestManager = RequestManager.getInstance( this );
        requestManager.requestAsyn( UrlConfig.registerUrl,RequestManager.TYPE_POST_JSON,
                new Param().append( "usName",UserConstant.userName ).
                        append( "usNickname",UserConstant.userNickName ).append( "usPassword",password ).end(),false,
                new RequestManager.ReqCallBack<ResponseModel>() {
                    @Override
                    public void onReqSuccess(ResponseModel result) {
                        if(CheckStatuss.CheckStatus( result, getApplicationContext() ) == 1) {
                            if ((Double) result.getData() == 0.0) {
                                Toast.makeText( getApplicationContext(), "注册用户已经存在", Toast.LENGTH_SHORT).show();
                            } else if ((Double) result.getData() == 1.0) {
                                Toast.makeText( getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        } else {
                            Toast.makeText( getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onReqFailed(String errorMsg) {
                    }
                } );
    }

    /**
     * 系统回调函数 返回intent携带的数据
     */
    @Override
    public void onBackPressed() {
        Intent intent=new Intent();
        intent.putExtra("result",passWord2);
        this.setResult(1, intent);
        this.finish();
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }

    /**
     * 返回登陆界面
     * @param view
     */
    public void backTologin(View view)
    {
        passWord2="";
        onBackPressed();
    }
    /**
     * 点击空白区域隐藏键盘.
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        if (me.getAction() == MotionEvent.ACTION_DOWN) {  //把操作放在用户点击的时候
            View v = getCurrentFocus();      //得到当前页面的焦点,ps:有输入框的页面焦点一般会被输入框占据
            if (isShouldHideKeyboard(v, me)) { //判断用户点击的是否是输入框以外的区域
                hideKeyboard(v.getWindowToken());   //收起键盘
            }
        }
        return super.dispatchTouchEvent(me);
    }
    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {  //判断得到的焦点控件是否包含EditText
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],    //得到输入框在屏幕中上下左右的位置
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击位置如果是EditText的区域，忽略它，不收起键盘。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略
        return false;
    }
    /**
     * 获取InputMethodManager，隐藏软键盘
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
